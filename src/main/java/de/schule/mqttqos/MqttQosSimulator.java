package de.schule.mqttqos;

import javax.swing.SwingUtilities;

/** Entry point for the MQTT QoS classroom simulator. */
public final class MqttQosSimulator {
    private MqttQosSimulator() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SimulatorFrame().setVisible(true);
        });
    }
}
