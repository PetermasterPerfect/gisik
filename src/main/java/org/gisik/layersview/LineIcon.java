package org.gisik.layersview;

import javax.swing.*;
import java.awt.*;

public class LineIcon extends FigureIcon {
    public LineIcon(Color color) {
        super(color);
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.drawLine(x + 2, y + getIconHeight() / 2, x + getIconWidth() - 2, y + getIconHeight() / 2);
    }

    public Color getColor() {
        return color;
    }

    @Override
    public int getIconWidth() { return 20; }

    @Override
    public int getIconHeight() { return 10; }
}