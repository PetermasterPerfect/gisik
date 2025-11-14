package org.gisik.layerstree;

import javax.swing.*;
import java.awt.*;

public class SquareIcon implements Icon {
    private final Color color;
    public SquareIcon(Color color) { this.color = color; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x + 3, y + 3, getIconWidth() - 6, getIconHeight() - 6);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x + 3, y + 3, getIconWidth() - 6, getIconHeight() - 6);
    }

    @Override public int getIconWidth() { return 20; }
    @Override public int getIconHeight() { return 20; }
}