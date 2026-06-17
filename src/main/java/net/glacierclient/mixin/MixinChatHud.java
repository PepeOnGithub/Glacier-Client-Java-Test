package net.glacierclient.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.glacierclient.GlacierClient;
import net.glacierclient.core.event.events.ChatReceiveEvent;
import net.glacierclient.modules.advanced.social.ChatAnimationsMod;
import net.glacierclient.modules.advanced.social.PlayerChatHeadMod;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ChatHud.class)
public class MixinChatHud {

    // Guards against the re-added message re-triggering the event (addMessage(Text) re-enters this).
    private static boolean glacier$reentrant = false;

    // Timestamp of the most recent incoming chat line — drives the ChatAnimations on-arrival ease.
    private static long glacier$lastAdd = 0L;
    private static boolean glacier$animPushed = false;
    private static boolean glacier$animFaded = false;

    @Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V",
            at = @At("HEAD"), cancellable = true)
    private void onAddMessage(Text message, net.minecraft.network.message.MessageSignatureData signature,
                               net.minecraft.client.gui.hud.MessageIndicator indicator, CallbackInfo ci) {
        if (GlacierClient.getInstance() == null || glacier$reentrant) return;

        glacier$lastAdd = System.currentTimeMillis(); // ChatAnimations: mark arrival time

        String original = message.getString();
        ChatReceiveEvent event = new ChatReceiveEvent(original);
        GlacierClient.getInstance().getEventBus().post(event);

        // A module asked to drop this message entirely (AntiSpam, IgnoreList, ChatFilter block).
        if (event.isCancelled()) { ci.cancel(); return; }

        boolean textChanged = !original.equals(event.getMessage());
        boolean highlighted = event.getHighlightColor() != -1;
        if (!textChanged && !highlighted) return;

        // Rebuild the line so message edits (ChatTimestamp prefix, ChatFilter replace) and mention
        // highlighting actually appear. Re-added through the public 1-arg path under a re-entrancy guard.
        ci.cancel();
        Text rebuilt = Text.literal(event.getMessage());
        if (highlighted) {
            rebuilt = Text.literal(event.getMessage())
                .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(event.getHighlightColor() & 0xFFFFFF)));
        }
        glacier$reentrant = true;
        try {
            ((ChatHud) (Object) this).addMessage(rebuilt);
        } finally {
            glacier$reentrant = false;
        }
    }

    // ---- ChatAnimations: ease the chat in when a new line arrives (Slide / Fade / Pop) ----
    private static ChatAnimationsMod glacier$anim() {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return null;
        ChatAnimationsMod m = gc.getModuleManager().getModule(ChatAnimationsMod.class);
        return (m != null && m.isEnabled()) ? m : null;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void glacier$animBegin(DrawContext ctx, int tickCounter, int mouseX, int mouseY, CallbackInfo ci) {
        glacier$animPushed = false;
        glacier$animFaded = false;
        ChatAnimationsMod m = glacier$anim();
        if (m == null) return;
        double dur = Math.max(1.0, m.getDurationMs());
        double t = (System.currentTimeMillis() - glacier$lastAdd) / dur;
        if (t < 0 || t >= 1) return;
        float eased = (float) (1.0 - Math.pow(1.0 - t, 3)); // ease-out cubic 0..1

        int sh = MinecraftClient.getInstance().getWindow().getScaledHeight();
        var ms = ctx.getMatrices();
        ms.push();
        glacier$animPushed = true;
        switch (m.getStyle()) {
            case "Fade" -> {
                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1f, 1f, 1f, eased);
                glacier$animFaded = true;
            }
            case "Pop" -> {
                // scale up from 85% about the chat's bottom-left anchor
                float s = 0.85f + 0.15f * eased;
                ms.translate(2, sh, 0);
                ms.scale(s, s, 1f);
                ms.translate(-2, -sh, 0);
            }
            default -> // Slide: rise into place from a few px below
                ms.translate(0, (1f - eased) * 8f, 0);
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void glacier$animEnd(DrawContext ctx, int tickCounter, int mouseX, int mouseY, CallbackInfo ci) {
        if (glacier$animFaded) {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            glacier$animFaded = false;
        }
        if (glacier$animPushed) {
            ctx.getMatrices().pop();
            glacier$animPushed = false;
        }
    }

    // ---- PlayerChatHead: draw the author's skin head before each chat line (vanilla <Name> format) ----
    private static final Pattern GLACIER_AUTHOR =
            Pattern.compile("^\\s*[<\\[]?([A-Za-z0-9_]{3,16})[>\\]]?\\s*[:>›»]");

    @Redirect(method = "render",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I"),
            require = 0)
    private int glacier$chatHead(DrawContext ctx, TextRenderer tr, OrderedText text, int x, int y, int color) {
        PlayerChatHeadMod m = glacier$head();
        if (m == null) return ctx.drawTextWithShadow(tr, text, x, y, color);
        int size = Math.max(4, m.getSize());
        int shift = size + 2;
        try {
            String author = glacier$author(glacier$plain(text));
            if (author != null) {
                MinecraftClient mc = MinecraftClient.getInstance();
                var nh = mc.getNetworkHandler();
                PlayerListEntry entry = nh != null ? nh.getPlayerListEntry(author) : null;
                if (entry != null) {
                    int bob = m.isAnimate() ? (int) Math.round(Math.sin(System.currentTimeMillis() / 220.0)) : 0;
                    PlayerSkinDrawer.draw(ctx, entry.getSkinTextures(), x, y - 1 + bob, size);
                }
            }
        } catch (Throwable ignored) {}
        // Indent every line uniformly so wrapped lines stay aligned under the head.
        return ctx.drawTextWithShadow(tr, text, x + shift, y, color);
    }

    private static PlayerChatHeadMod glacier$head() {
        GlacierClient gc = GlacierClient.getInstance();
        if (gc == null) return null;
        PlayerChatHeadMod m = gc.getModuleManager().getModule(PlayerChatHeadMod.class);
        return (m != null && m.isEnabled()) ? m : null;
    }

    private static String glacier$plain(OrderedText text) {
        StringBuilder sb = new StringBuilder();
        text.accept((index, style, codePoint) -> { sb.appendCodePoint(codePoint); return true; });
        return sb.toString();
    }

    private static String glacier$author(String s) {
        Matcher mm = GLACIER_AUTHOR.matcher(s);
        return mm.find() ? mm.group(1) : null;
    }
}
