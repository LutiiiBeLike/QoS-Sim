package de.schule.mqttqos;

/** Endpoints shown in the teaching diagram. */
public enum Endpoint {
    SENDER("Sender"),
    BROKER("MQTT-Broker"),
    RECEIVER("Empfänger");

    private final String label;

    Endpoint(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
