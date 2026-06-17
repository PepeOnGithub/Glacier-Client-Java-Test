package net.glacierclient.web;

import com.labymedia.ultralight.UltralightView;
import com.labymedia.ultralight.bitmap.UltralightBitmap;
import com.labymedia.ultralight.bitmap.UltralightBitmapSurface;
import com.labymedia.ultralight.input.UltralightMouseEvent;
import com.labymedia.ultralight.input.UltralightMouseEventButton;
import com.labymedia.ultralight.input.UltralightMouseEventType;
import com.labymedia.ultralight.input.UltralightScrollEvent;
import com.labymedia.ultralight.input.UltralightScrollEventType;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.text.Text;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;

/**
 * A Minecraft {@link Screen} that hosts an Ultralight HTML view (one of our web/*.html pages),
 * sharing Minecraft's GL context. Renders the view's texture over the screen, forwards mouse/keyboard
 * input, and drains the JS action queue (window.__glq) into {@link GlacierBridge} each frame.
 *
 * <p>Opt-in: if Ultralight isn't available (SDK natives missing), {@link #open(String, Screen)} returns
 * false and the caller keeps using the native screens.</p>
 */
public class GlacierWebScreen extends Screen {

    private static final String DRAIN =
            "(function(){try{var q=window.__glq||[];window.__glq=[];return q.join('\\n');}catch(e){return '';}})()";

    private final String page;
    private final Screen fallback;
    private UltralightView view;
    private int viewW, viewH;
    private boolean fellBack = false;

    // GL texture the Ultralight CPU bitmap is uploaded into each frame.
    private int glTexId = -1;
    private int texW, texH;
    // Tightly-packed staging buffer (off-heap) we copy the bitmap into before uploading.
    private ByteBuffer pixelBuf;

    private GlacierWebScreen(String page, Screen fallback) {
        super(Text.literal("Glacier"));
        this.page = page;
        this.fallback = fallback;
    }

    /**
     * Returns the screen to show for a page: the web screen when the platform is supported (it shows a
     * brief loading state while natives unpack, then the HTML), otherwise the native {@code fallback}.
     * This way the very first title/pause screen is already the web UI — no second launch.
     */
    public static Screen forPage(String page, Screen fallback) {
        return (UltralightManager.isEnabled() && UltralightManager.isSupported())
                ? new GlacierWebScreen(page, fallback) : fallback;
    }

    // Fixed logical width the HTML is authored against (~720p). The view is always laid out at this
    // width and stretch-blitted to fill the screen, so the layout is identical at any GUI scale or
    // window size — instead of being cramped into Minecraft's tiny GUI-scaled coordinate space.
    private static final int LOGICAL_W = 1280;

    private void computeLogicalSize() {
        var win = MinecraftClient.getInstance().getWindow();
        int fbw = Math.max(1, win.getFramebufferWidth());
        int fbh = Math.max(1, win.getFramebufferHeight());
        viewW = LOGICAL_W;
        viewH = Math.max(1, Math.round((float) LOGICAL_W * fbh / fbw)); // preserve aspect (no distortion)
    }

    private void ensureView() {
        if (view == null && UltralightManager.get().isReady()) {
            computeLogicalSize();
            view = UltralightManager.get().createView(viewW, viewH);
            view.loadURL("file:///" + page);
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        if (view != null) { computeLogicalSize(); view.resize(viewW, viewH); }
    }

    /** Map a Minecraft scaled-screen coordinate onto the logical view coordinate space. */
    private double toViewX(double x) { return this.width  <= 0 ? x : x * viewW / this.width;  }
    private double toViewY(double y) { return this.height <= 0 ? y : y * viewH / this.height; }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        UltralightManager mgr = UltralightManager.get();

        // Drive init from the render thread; fall back to the native screen only on real failure.
        if (!mgr.isReady()) {
            mgr.ensureInit();
            if (!mgr.isReady()) {
                if (!mgr.isPending() && !fellBack) { // permanently failed -> native UI
                    fellBack = true;
                    MinecraftClient.getInstance().setScreen(fallback);
                    return;
                }
                renderLoading(context);
                return;
            }
        }
        ensureView();
        if (view == null) { renderLoading(context); return; }

        try {
            mgr.update();
            drainActions();
            uploadBitmap();   // copy the CPU bitmap surface into our GL texture
            // On the title screen there is no world behind us, so paint an opaque backdrop (otherwise
            // transparent UI areas reveal a stale framebuffer — that was the flicker / garbage band).
            // In a world we deliberately skip it so the (now frozen) game shows through the UI.
            if (MinecraftClient.getInstance().world == null) {
                context.fill(0, 0, width, height, 0xFF0B0B0E);
            }
            blit(context);
        } catch (Throwable t) {
            context.drawCenteredTextWithShadow(textRenderer, "Web UI error: " + t, width / 2, height / 2, 0xFFFF6B6B);
        }
    }

    /**
     * Upload Ultralight's CPU bitmap surface into {@link #glTexId}.
     *
     * <p>We never hand the raw native Ultralight buffer to {@code glTexImage2D}: that made the GPU
     * driver walk the buffer using its row stride and crash hard (EXCEPTION_ACCESS_VIOLATION in the
     * Intel ICD) on some machines. Instead we copy row-by-row into a tightly-packed direct buffer —
     * bounded by the source buffer's real capacity, so a size mismatch becomes a harmless short copy
     * rather than an out-of-bounds read — and upload that with no row-length tricks.</p>
     */
    private void uploadBitmap() {
        UltralightBitmapSurface surface = (UltralightBitmapSurface) view.surface();
        UltralightBitmap bmp = surface.bitmap();
        int w = (int) bmp.width(), h = (int) bmp.height();
        if (w <= 0 || h <= 0) return;
        long rowBytes = bmp.rowBytes();
        int dstStride = w * 4;
        int needed = dstStride * h;

        ByteBuffer src = bmp.lockPixels();
        try {
            if (src == null) return;
            // (Re)allocate the staging buffer when the size changes.
            if (pixelBuf == null || pixelBuf.capacity() < needed) {
                if (pixelBuf != null) org.lwjgl.system.MemoryUtil.memFree(pixelBuf);
                pixelBuf = org.lwjgl.system.MemoryUtil.memAlloc(needed);
            }
            long srcAddr = org.lwjgl.system.MemoryUtil.memAddress(src);
            long dstAddr = org.lwjgl.system.MemoryUtil.memAddress(pixelBuf);
            // Clamp to whatever the source buffer actually holds — never read past it.
            int srcRows = rowBytes > 0 ? (int) (src.capacity() / rowBytes) : 0;
            int rows = Math.min(h, srcRows);
            for (int y = 0; y < rows; y++) {
                org.lwjgl.system.MemoryUtil.memCopy(srcAddr + (long) y * rowBytes, dstAddr + (long) y * dstStride, dstStride);
            }
            pixelBuf.position(0).limit(needed);

            if (glTexId == -1) glTexId = GlStateManager._genTexture();
            GlStateManager._bindTexture(glTexId);
            GL11.glPixelStorei(GL11.GL_UNPACK_ROW_LENGTH, 0);
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 4);
            if (w != texW || h != texH) {
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, pixelBuf);
                texW = w; texH = h;
            } else {
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, w, h, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, pixelBuf);
            }
        } finally {
            bmp.unlockPixels();
        }
    }

    /** Brief loading state shown while the engine spins up on first launch. */
    private void renderLoading(DrawContext context) {
        context.fill(0, 0, width, height, 0xFF0B1220);
        long t = System.currentTimeMillis() / 300;
        String dots = ".".repeat((int) (t % 4));
        context.drawCenteredTextWithShadow(textRenderer, "Loading Glacier UI" + dots, width / 2, height / 2 - 4, 0xFF8EA9FF);
    }

    /** Draw our uploaded texture over the whole screen (bitmap is top-down → v=0 at top). */
    private void blit(DrawContext context) {
        if (glTexId == -1) return;
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();
        RenderSystem.enableBlend();
        // Ultralight's bitmap surface is PREMULTIPLIED BGRA, so blend with (ONE, ONE_MINUS_SRC_ALPHA)
        // rather than the straight-alpha default — otherwise translucent UI areas render too dark.
        RenderSystem.blendFunc(GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, glTexId);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        int x0 = 0, y0 = 0, x1 = width, y1 = height;
        Tessellator tess = Tessellator.getInstance();
        BufferBuilder bb = tess.getBuffer();
        bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bb.vertex(mat, x0, y1, 0).texture(0f, 1f).next();
        bb.vertex(mat, x1, y1, 0).texture(1f, 1f).next();
        bb.vertex(mat, x1, y0, 0).texture(1f, 0f).next();
        bb.vertex(mat, x0, y0, 0).texture(0f, 0f).next();
        tess.draw();
        RenderSystem.disableBlend();
    }

    private void drainActions() {
        try {
            String result = view.evaluateScript(DRAIN);
            if (result == null || result.isEmpty()) return;
            for (String a : result.split("\n")) {
                a = a.trim();
                if (!a.isEmpty()) GlacierBridge.onAction(a);
            }
        } catch (Throwable ignored) {}
    }

    // ---- input forwarding ----
    private static UltralightMouseEventButton btn(int b) {
        return switch (b) { case 1 -> UltralightMouseEventButton.RIGHT; case 2 -> UltralightMouseEventButton.MIDDLE; default -> UltralightMouseEventButton.LEFT; };
    }

    @Override
    public void mouseMoved(double x, double y) {
        if (view != null) view.fireMouseEvent(new UltralightMouseEvent().x((int) toViewX(x)).y((int) toViewY(y)).type(UltralightMouseEventType.MOVED));
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        if (view != null) view.fireMouseEvent(new UltralightMouseEvent().x((int) toViewX(x)).y((int) toViewY(y)).type(UltralightMouseEventType.DOWN).button(btn(button)));
        return true;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        if (view != null) view.fireMouseEvent(new UltralightMouseEvent().x((int) toViewX(x)).y((int) toViewY(y)).type(UltralightMouseEventType.UP).button(btn(button)));
        return true;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontal, double vertical) {
        if (view != null) view.fireScrollEvent(new UltralightScrollEvent().type(UltralightScrollEventType.BY_PIXEL).deltaX((int) (horizontal * 40)).deltaY((int) (vertical * 40)));
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (view != null) {
            view.fireKeyEvent(new com.labymedia.ultralight.input.UltralightKeyEvent()
                    .type(com.labymedia.ultralight.input.UltralightKeyEventType.CHAR)
                    .text(String.valueOf(chr))
                    .unmodifiedText(String.valueOf(chr)));
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) { close(); return true; }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        if (glTexId != -1) { GlStateManager._deleteTexture(glTexId); glTexId = -1; texW = texH = 0; }
        if (pixelBuf != null) { org.lwjgl.system.MemoryUtil.memFree(pixelBuf); pixelBuf = null; }
    }

    @Override
    public void close() {
        MinecraftClient mc = MinecraftClient.getInstance();
        mc.setScreen(mc.world != null ? null : fallback); // in a world: resume; on title: back to fallback
    }

    @Override
    public boolean shouldPause() {
        // Freeze the integrated server while the UI is open so the world behind doesn't bob/shake
        // (and stays a clean static backdrop). On the title screen there is no world, so this is moot.
        return MinecraftClient.getInstance().world != null;
    }
}
