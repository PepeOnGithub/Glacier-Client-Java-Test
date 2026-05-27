package net.glacierclient.core.event;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {

    private final Map<Class<?>, List<ListenerEntry>> listeners = new ConcurrentHashMap<>();

    public void subscribe(EventTarget target) {
        for (Method method : target.getClass().getMethods()) {
            if (!method.isAnnotationPresent(EventListen.class)) continue;
            if (method.getParameterCount() != 1) continue;
            Class<?> eventType = method.getParameterTypes()[0];
            EventListen annotation = method.getAnnotation(EventListen.class);
            listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                     .add(new ListenerEntry(target, method, annotation.priority()));
            listeners.get(eventType).sort(Comparator.comparingInt(e -> -e.priority));
        }
    }

    public void unsubscribe(EventTarget target) {
        for (List<ListenerEntry> entries : listeners.values()) {
            entries.removeIf(e -> e.target == target);
        }
    }

    public <T extends GlacierEvent> T post(T event) {
        List<ListenerEntry> entries = listeners.get(event.getClass());
        if (entries == null) return event;
        for (ListenerEntry entry : entries) {
            if (event instanceof CancellableEvent cancellable && cancellable.isCancelled()) break;
            try {
                entry.method.invoke(entry.target, event);
            } catch (Exception ignored) {}
        }
        return event;
    }

    private static class ListenerEntry {
        final EventTarget target;
        final Method method;
        final int priority;

        ListenerEntry(EventTarget target, Method method, int priority) {
            this.target = target;
            this.method = method;
            this.priority = priority;
        }
    }
}
