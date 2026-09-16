package de.schule.mqttqos;

import java.util.List;
import java.util.function.IntSupplier;

/** Minimal regression tests runnable with the standard JDK and without JUnit. */
public final class MqttSimulationEngineTest {
    private MqttSimulationEngineTest() {
    }

    public static void main(String[] args) {
        qos0DeliversWithoutLoss();
        qos0FailsAtTotalLoss();
        qos1RetransmitsAfterLostAcknowledgement();
        qos2UsesCompleteHandshakeOnBothHops();
        System.out.println("Alle MqttSimulationEngine-Tests erfolgreich.");
    }

    private static void qos0DeliversWithoutLoss() {
        SimulationResult result = engine(99).simulate(QosLevel.QOS_0, 0);
        check(result.delivered(), "QoS 0 soll ohne Verlust zustellen.");
        check(result.events().size() == 2, "QoS 0 benötigt zwei PUBLISH-Pakete.");
        check(allPacketsAre(result.events(), "PUBLISH"), "QoS 0 verwendet nur PUBLISH.");
    }

    private static void qos0FailsAtTotalLoss() {
        SimulationResult result = engine(99).simulate(QosLevel.QOS_0, 100);
        check(!result.delivered(), "100 % Verlust darf nicht zustellen.");
        check(result.events().size() == 1 && result.events().get(0).lost(),
                "Bei 100 % Verlust soll der erste Versuch sichtbar verloren gehen.");
    }

    private static void qos1RetransmitsAfterLostAcknowledgement() {
        SimulationResult result = engine(99, 0, 99, 99, 99, 99).simulate(QosLevel.QOS_1, 50);
        check(result.delivered(), "QoS 1 soll nach erneutem Senden zustellen.");
        check(result.events().size() == 6, "QoS 1 muss das verlorene PUBACK durch Wiederholung behandeln.");
        PacketEvent duplicatePublish = result.events().get(2);
        check(duplicatePublish.packetName().equals("PUBLISH") && duplicatePublish.attempt() == 2,
                "Die zweite PUBLISH-Übertragung muss als Wiederholung markiert sein.");
    }

    private static void qos2UsesCompleteHandshakeOnBothHops() {
        SimulationResult result = engine(99).simulate(QosLevel.QOS_2, 0);
        check(result.delivered(), "QoS 2 soll ohne Verlust zustellen.");
        List<String> packets = result.events().stream().map(PacketEvent::packetName).toList();
        check(packets.equals(List.of("PUBLISH", "PUBREC", "PUBREL", "PUBCOMP",
                        "PUBLISH", "PUBREC", "PUBREL", "PUBCOMP")),
                "QoS 2 muss auf beiden Strecken den vollständigen Vier-Schritt-Ablauf verwenden.");
    }

    private static MqttSimulationEngine engine(int... values) {
        IntSupplier valuesThenSuccess = new IntSupplier() {
            private int index;

            @Override
            public int getAsInt() {
                return index < values.length ? values[index++] : 99;
            }
        };
        return new MqttSimulationEngine(valuesThenSuccess);
    }

    private static boolean allPacketsAre(List<PacketEvent> events, String expected) {
        return events.stream().allMatch(event -> event.packetName().equals(expected));
    }

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
