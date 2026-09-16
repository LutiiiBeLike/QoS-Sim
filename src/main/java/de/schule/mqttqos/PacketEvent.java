package de.schule.mqttqos;

/** One directed MQTT packet transmission, ready for animation and logging. */
public record PacketEvent(String packetName, Endpoint source, Endpoint destination,
                          int attempt, boolean lost) {
    public String displayName() {
        return attempt > 1 ? packetName + " (DUP)" : packetName;
    }
}
