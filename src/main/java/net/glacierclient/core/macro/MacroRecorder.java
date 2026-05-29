package net.glacierclient.core.macro;

import net.glacierclient.core.event.*;import net.glacierclient.core.event.events.*;
import java.util.*;

public final class MacroRecorder {
    private static final MacroRecorder INSTANCE = new MacroRecorder();
    public static MacroRecorder get() { return INSTANCE; }

    private boolean recording = false;
    private final List<MacroAction> recorded = new ArrayList<>();
    private long recordStart = 0;

    private final Map<String, List<MacroAction>> saved = new LinkedHashMap<>();

    public void startRecording() {
        recorded.clear();
        recordStart = System.currentTimeMillis();
        recording = true;
    }

    public void stopRecording(String name) {
        recording = false;
        saved.put(name, new ArrayList<>(recorded));
        recorded.clear();
    }

    public void cancelRecording() {
        recording = false;
        recorded.clear();
    }

    public boolean isRecording() { return recording; }

    @EventListen
    public void onKey(KeyInputEvent event) {
        if (!recording) return;
        long timestamp = System.currentTimeMillis() - recordStart;
        recorded.add(new MacroAction(MacroAction.Type.KEY_PRESS, event.getKey(), timestamp));
    }

    @EventListen
    public void onChatSend(ChatSendEvent event) {
        if (!recording) return;
        long timestamp = System.currentTimeMillis() - recordStart;
        recorded.add(new MacroAction(MacroAction.Type.CHAT, event.getMessage(), timestamp));
    }

    public Set<String> getMacroNames() { return Collections.unmodifiableSet(saved.keySet()); }

    public List<MacroAction> getMacro(String name) { return saved.getOrDefault(name, List.of()); }

    public void deleteMacro(String name) { saved.remove(name); }

    public static final class MacroAction {
        public enum Type { KEY_PRESS, MOUSE_CLICK, CHAT }
        public final Type type;
        public final Object data;
        public final long timestamp;
        MacroAction(Type type, Object data, long timestamp) {
            this.type = type; this.data = data; this.timestamp = timestamp;
        }
    }
}
