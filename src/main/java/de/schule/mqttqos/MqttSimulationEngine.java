package de.schule.mqttqos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

/**
 * Produces a simplified, protocol-faithful sequence for both MQTT hops.
 * It deliberately separates simulation logic from Swing so it can be tested without a UI.
 */
public final class MqttSimulationEngine {
    private final IntSupplier randomPercent;

    /** @param randomPercent supplies values from 0 to 99 for deterministic tests or random use. */
    public MqttSimulationEngine(IntSupplier randomPercent) {
        this.randomPercent = randomPercent;
    }

    public SimulationResult simulate(QosLevel qos, int lossPercent) {
        if (lossPercent < 0 || lossPercent > 100) {
            throw new IllegalArgumentException("Paketverlust muss zwischen 0 und 100 liegen.");
        }
        List<PacketEvent> events = new ArrayList<>();
        if (lossPercent == 100) {
            events.add(new PacketEvent("PUBLISH", Endpoint.SENDER, Endpoint.BROKER, 1, true));
            return new SimulationResult(events, false,
                    "Abbruch: Bei 100 % Paketverlust kann keine Nachricht zugestellt werden.");
        }

        boolean firstHopDelivered = switch (qos) {
            case QOS_0 -> publishOnce(events, Endpoint.SENDER, Endpoint.BROKER, lossPercent);
            case QOS_1 -> exchangeQos1(events, Endpoint.SENDER, Endpoint.BROKER, lossPercent);
            case QOS_2 -> exchangeQos2(events, Endpoint.SENDER, Endpoint.BROKER, lossPercent);
        };
        if (!firstHopDelivered) {
            return new SimulationResult(events, false, "Die Nachricht ging auf dem Weg zum Broker verloren.");
        }

        boolean secondHopDelivered = switch (qos) {
            case QOS_0 -> publishOnce(events, Endpoint.BROKER, Endpoint.RECEIVER, lossPercent);
            case QOS_1 -> exchangeQos1(events, Endpoint.BROKER, Endpoint.RECEIVER, lossPercent);
            case QOS_2 -> exchangeQos2(events, Endpoint.BROKER, Endpoint.RECEIVER, lossPercent);
        };
        return secondHopDelivered
                ? new SimulationResult(events, true, "Erfolg: Die Nachricht wurde beim Empfänger zugestellt.")
                : new SimulationResult(events, false, "Die Nachricht ging auf dem Weg zum Empfänger verloren.");
    }

    private boolean publishOnce(List<PacketEvent> events, Endpoint source, Endpoint destination, int lossPercent) {
        return !send(events, "PUBLISH", source, destination, 1, lossPercent);
    }

    private boolean exchangeQos1(List<PacketEvent> events, Endpoint source, Endpoint destination, int lossPercent) {
        int publishAttempt = 0;
        while (true) {
            publishAttempt++;
            if (send(events, "PUBLISH", source, destination, publishAttempt, lossPercent)) {
                continue;
            }
            // A lost PUBACK makes the sender retransmit PUBLISH. The receiver may see a duplicate.
            if (!send(events, "PUBACK", destination, source, 1, lossPercent)) {
                return true;
            }
        }
    }

    private boolean exchangeQos2(List<PacketEvent> events, Endpoint source, Endpoint destination, int lossPercent) {
        int publishAttempt = 0;
        while (true) {
            publishAttempt++;
            if (send(events, "PUBLISH", source, destination, publishAttempt, lossPercent)) {
                continue;
            }
            if (!send(events, "PUBREC", destination, source, 1, lossPercent)) {
                break;
            }
        }

        int pubrelAttempt = 0;
        while (true) {
            pubrelAttempt++;
            if (send(events, "PUBREL", source, destination, pubrelAttempt, lossPercent)) {
                continue;
            }
            if (!send(events, "PUBCOMP", destination, source, 1, lossPercent)) {
                return true;
            }
        }
    }

    /** @return true when this particular packet is lost. */
    private boolean send(List<PacketEvent> events, String packet, Endpoint source, Endpoint destination,
                         int attempt, int lossPercent) {
        boolean lost = randomPercent.getAsInt() < lossPercent;
        events.add(new PacketEvent(packet, source, destination, attempt, lost));
        return lost;
    }
}
