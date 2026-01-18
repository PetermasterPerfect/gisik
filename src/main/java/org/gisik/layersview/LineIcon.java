package org.gisik.layersview;

import javax.swing.*;
import java.awt.*;

/**
 * Icon representing a horizontal colored line.
 * <p>
 * Extends {@link FigureIcon} and draws a horizontal line with a specified color.
 * Typically used for representing line layers in the layers panel.
 */
public class LineIcon extends FigureIcon {
    /**
     * Constructs a new LineIcon with the given color.
     *
     * @param color the color of the line
     */
    public LineIcon(Color color) {
        super(color);
    }

    /**
     * Paints the horizontal line icon.
     *
     * @param c the component to which the icon is added
     * @param g the graphics context
     * @param x the X coordinate of the icon's top-left corner
     * @param y the Y coordinate of the icon's top-left corner
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.drawLine(x + 2, y + getIconHeight() / 2, x + getIconWidth() - 2, y + getIconHeight() / 2);
    }

    /**
     * Returns the color of the line.
     *
     * @return the line color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns the icon width.
     *
     * @return the width of the icon in pixels
     */
    @Override
    public int getIconWidth() { return 20; }

    /**
     * Returns the icon height.
     *
     * @return the height of the icon in pixels
     */
    @Override
    public int getIconHeight() { return 10; }
}