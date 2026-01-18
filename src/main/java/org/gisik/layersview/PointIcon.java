package org.gisik.layersview;

import javax.swing.*;
import java.awt.*;

/**
 * Icon representing a colored point (circle).
 * <p>
 * Extends {@link FigureIcon} and draws a filled circle with an outline.
 * Typically used for representing point layers in the layers panel.
 */
public class PointIcon extends FigureIcon {
    /**
     * Constructs a new PointIcon with the given color.
     *
     * @param color the fill color of the point
     */
    public PointIcon(Color color) {
        super(color);
    }

    /**
     * Paints the circular point icon.
     *
     * @param c the component to which the icon is added
     * @param g the graphics context
     * @param x the X coordinate of the icon's top-left corner
     * @param y the Y coordinate of the icon's top-left corner
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillOval(x + 6, y + 6, 8, 8);
        g.setColor(Color.DARK_GRAY);
        g.drawOval(x + 6, y + 6, 8, 8);
    }

    /**
     * Returns the color of the point.
     *
     * @return the point color
     */
    public Color getColor() {
        return color;
    }


    /**
     * Returns the icon width.
     *
     * @return the width of the icon in pixels
     */
    @Override public int getIconWidth() { return 20; }

    /**
     * Returns the icon height.
     *
     * @return the height of the icon in pixels
     */
    @Override public int getIconHeight() { return 20; }
}