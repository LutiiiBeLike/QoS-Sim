package de.schule.mqttqos;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/** The main Swing window. Simulation behaviour is connected in later steps. */
public final class SimulatorFrame extends JFrame {
    private final JRadioButton qos0Button = new JRadioButton("QoS 0");
    private final JRadioButton qos1Button = new JRadioButton("QoS 1", true);
    private final JRadioButton qos2Button = new JRadioButton("QoS 2");
    private final JSlider lossSlider = new JSlider(0, 100, 10);
    private final JLabel lossValueLabel = new JLabel("10 %", SwingConstants.RIGHT);
    private final JButton sendButton = new JButton("Nachricht senden");
    private final JLabel explanationLabel = new JLabel("Wähle eine QoS-Stufe und starte die Simulation.");
    private final JTextArea logArea = new JTextArea();

    public SimulatorFrame() {
        super("MQTT-QoS-Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 650));
        setSize(1_000, 720);
        setLocationByPlatform(true);

        JPanel content = new JPanel(new BorderLayout(12, 12));
        content.setBorder(new EmptyBorder(16, 16, 16, 16));
        content.add(createHeader(), BorderLayout.NORTH);
        content.add(createCenter(), BorderLayout.CENTER);
        content.add(createFooter(), BorderLayout.SOUTH);
        setContentPane(content);

        lossSlider.addChangeListener(event -> lossValueLabel.setText(lossSlider.getValue() + " %"));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(8, 8));
        JLabel title = new JLabel("MQTT Quality of Service (QoS) interaktiv verstehen");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        header.add(title, BorderLayout.NORTH);
        header.add(explanationLabel, BorderLayout.SOUTH);
        return header;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel(new BorderLayout(12, 12));
        center.add(createControls(), BorderLayout.NORTH);
        center.add(new CommunicationPanel(), BorderLayout.CENTER);
        center.add(createLogPanel(), BorderLayout.SOUTH);
        return center;
    }

    private JPanel createControls() {
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 4));
        controls.setBorder(BorderFactory.createTitledBorder("Einstellungen"));
        ButtonGroup qosGroup = new ButtonGroup();
        qosGroup.add(qos0Button);
        qosGroup.add(qos1Button);
        qosGroup.add(qos2Button);
        controls.add(new JLabel("QoS-Stufe:"));
        controls.add(qos0Button);
        controls.add(qos1Button);
        controls.add(qos2Button);
        controls.add(new JLabel("Paketverlust:"));
        lossSlider.setPreferredSize(new Dimension(190, 35));
        lossSlider.setMajorTickSpacing(25);
        lossSlider.setPaintTicks(true);
        controls.add(lossSlider);
        controls.add(lossValueLabel);
        controls.add(sendButton);
        return controls;
    }

    private JScrollPane createLogPanel() {
        logArea.setEditable(false);
        logArea.setRows(8);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        logArea.setText("Bereit. Die Protokollereignisse erscheinen hier.\n");
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Ereignisprotokoll"));
        return scrollPane;
    }

    private JLabel createFooter() {
        JLabel footer = new JLabel(
                "Didaktische Simulation: Echte MQTT-Kommunikation erfolgt getrennt zwischen Client und Broker sowie Broker und Subscriber.");
        footer.setBorder(new EmptyBorder(4, 0, 0, 0));
        return footer;
    }
}
