package de.schule.mqttqos;

/** MQTT delivery guarantees supported by the simulator. */
public enum QosLevel {
    QOS_0(0),
    QOS_1(1),
    QOS_2(2);

    private final int number;

    QosLevel(int number) {
        this.number = number;
    }

    public int number() {
        return number;
    }
}
