package org.gisik.layersview;

import javax.swing.*;
import java.awt.*;

/**
 * Icon representing a colored square.
 * <p>
 * Extends {@link FigureIcon} and draws a filled square with an outline.
 * Typically used for representing polygon or area layers in the layers panel.
 */
public class SquareIcon extends FigureIcon {
    /**
     * Constructs a new SquareIcon with the given color.
     *
     * @param color the fill color of the square
     */
    public SquareIcon(Color color) {
        super(color);
    }

    /**
     * Paints the square icon.
     *
     * @param c the component to which the icon is added
     * @param g the graphics context
     * @param x the X coordinate of the icon's top-left corner
     * @param y the Y coordinate of the icon's top-left corner
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g.setColor(color);
        g.fillRect(x + 3, y + 3, getIconWidth() - 6, getIconHeight() - 6);
        g.setColor(Color.DARK_GRAY);
        g.drawRect(x + 3, y + 3, getIconWidth() - 6, getIconHeight() - 6);
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