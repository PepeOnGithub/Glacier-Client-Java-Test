package net.glacierclient.core.event.events;

import net.glacierclient.core.event.CancellableEvent;
import net.minecraft.network.packet.Packet;

public class PacketReceiveEvent extends CancellableEvent {
    private final Packet<?> packet;

    public PacketReceiveEvent(Packet<?> packet) { this.packet = packet; }
    public Packet<?> getPacket() { return packet; }
}
