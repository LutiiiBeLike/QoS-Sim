package de.schule.mqttqos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import javax.swing.Timer;

/** Draws the three stations of the simplified communication path. */
public final class CommunicationPanel extends JPanel {
    private PacketEvent activePacket;
    private double packetProgress;
    private Timer activeTimer;
    public CommunicationPanel() {
        setPreferredSize(new Dimension(700, 230));
        setBackground(Color.WHITE);
    }

    /** Animates one packet and invokes {@code onFinished} on the Swing event thread. */
    public void animate(PacketEvent packet, Runnable onFinished) {
        cancelAnimation();
        activePacket = packet;
        packetProgress = 0;
        long startedAt = System.nanoTime();
        Timer timer = new Timer(15, null);
        activeTimer = timer;
        timer.addActionListener(event -> {
            packetProgress = Math.min(1.0, (System.nanoTime() - startedAt) / 650_000_000.0);
            repaint();
            if (packetProgress >= 1.0) {
                timer.stop();
                activeTimer = null;
                activePacket = null;
                repaint();
                onFinished.run();
            }
        });
        timer.setInitialDelay(0);
        timer.start();
    }

    /** Stops the current packet animation without invoking its completion callback. */
    public void cancelAnimation() {
        if (activeTimer != null) {
            activeTimer.stop();
            activeTimer = null;
        }
        activePacket = null;
        packetProgress = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int y = getHeight() / 2;
        int left = 110;
        int middle = getWidth() / 2;
        int right = getWidth() - 110;

        g2.setColor(new Color(84, 110, 122));
        g2.setStroke(new BasicStroke(3f));
        drawArrow(g2, left + 48, y, middle - 55, y);
        drawArrow(g2, middle + 55, y, right - 48, y);
        drawStation(g2, left, y, "Sender", new Color(66, 133, 244));
        drawStation(g2, middle, y, "MQTT-Broker", new Color(251, 140, 0));
        drawStation(g2, right, y, "Empfänger", new Color(67, 160, 71));
        drawActivePacket(g2, left, middle, right, y);
        g2.dispose();
    }

    private void drawStation(Graphics2D g2, int x, int y, String label, Color color) {
        g2.setColor(color);
        g2.fillRoundRect(x - 48, y - 32, 96, 64, 16, 16);
        g2.setColor(Color.WHITE);
        g2.setFont(getFont().deriveFont(Font.BOLD, 13f));
        int width = g2.getFontMetrics().stringWidth(label);
        g2.drawString(label, x - width / 2, y + 5);
    }

    private static void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.drawLine(x1, y1, x2, y2);
        g2.fillPolygon(new int[] {x2, x2 - 10, x2 - 10}, new int[] {y2, y2 - 6, y2 + 6}, 3);
    }

    private void drawActivePacket(Graphics2D g2, int left, int middle, int right, int y) {
        if (activePacket == null) {
            return;
        }
        int start;
        int end;
        if (activePacket.source() == Endpoint.SENDER && activePacket.destination() == Endpoint.BROKER) {
            start = left + 50;
            end = middle - 58;
        } else if (activePacket.source() == Endpoint.BROKER && activePacket.destination() == Endpoint.SENDER) {
            start = middle - 58;
            end = left + 50;
        } else if (activePacket.source() == Endpoint.BROKER) {
            start = middle + 58;
            end = right - 50;
        } else {
            start = right - 50;
            end = middle + 58;
        }
        int x = (int) Math.round(start + (end - start) * packetProgress);
        String label = activePacket.displayName();
        g2.setFont(getFont().deriveFont(Font.BOLD, 12f));
        int width = Math.max(58, g2.getFontMetrics().stringWidth(label) + 18);
        g2.setColor(activePacket.lost() ? new Color(198, 40, 40) : new Color(84, 110, 122));
        g2.fillRoundRect(x - width / 2, y - 57, width, 25, 12, 12);
        g2.setColor(Color.WHITE);
        g2.drawString(label, x - g2.getFontMetrics().stringWidth(label) / 2, y - 40);
    }
}
