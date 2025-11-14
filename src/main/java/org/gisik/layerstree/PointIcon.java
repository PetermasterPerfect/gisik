package org.gisik.layerstree;

import javax.swing.*;
import java.awt.*;

public class PointIcon implements Icon {
    private final Color color;
    public PointIcon(Color color) { this.color = color; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillOval(x + 6, y + 6, 8, 8);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x + 6, y + 6, 8, 8);
    }

    @Override public int getIconWidth() { return 20; }
    @Override public int getIconHeight() { return 20; }
}