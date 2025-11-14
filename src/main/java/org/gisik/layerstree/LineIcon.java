package org.gisik.layerstree;

import javax.swing.*;
import java.awt.*;

public class LineIcon implements Icon {
    private final Color color;

    public LineIcon(Color color) {
        this.color = color;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.drawLine(x + 2, y + getIconHeight() / 2, x + getIconWidth() - 2, y + getIconHeight() / 2);
    }

    @Override
    public int getIconWidth() { return 20; }

    @Override
    public int getIconHeight() { return 10; }
}