package org.gisik.layersview;

import javax.swing.*;
import java.awt.*;

public class FigureIcon implements Icon {
    protected final Color color;

    public FigureIcon(Color color) { this.color = color; }

    public Color getColor() { return color; }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {

    }

    @Override
    public int getIconWidth() {
        return 0;
    }

    @Override
    public int getIconHeight() {
        return 0;
    }
}
