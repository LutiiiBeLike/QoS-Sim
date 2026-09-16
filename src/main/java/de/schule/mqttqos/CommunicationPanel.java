package de.schule.mqttqos;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/** Draws the three stations of the simplified communication path. */
public final class CommunicationPanel extends JPanel {
    public CommunicationPanel() {
        setPreferredSize(new Dimension(700, 230));
        setBackground(Color.WHITE);
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
}
