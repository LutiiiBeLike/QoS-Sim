package de.schule.mqttqos;

import java.util.List;

/** Immutable output of one simulated MQTT message exchange. */
public record SimulationResult(List<PacketEvent> events, boolean delivered, String finalMessage) {
    public SimulationResult {
        events = List.copyOf(events);
    }
}
