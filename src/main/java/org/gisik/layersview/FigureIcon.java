package org.gisik.layersview;

import javax.swing.*;
import java.awt.*;

/**
 * Base class for colored icons used in the layers panel.
 * <p>
 * Represents a graphical icon with a specific color. The actual shape
 * rendering is intended to be implemented in subclasses.
 */
public class FigureIcon implements Icon {
    /** The color of the icon. */
    protected final Color color;

    /**
     * Constructs a FigureIcon with the specified color.
     *
     * @param color the color of the icon
     */
    public FigureIcon(Color color) { this.color = color; }

    /**
     * Returns the color of this icon.
     *
     * @return the icon's color
     */
    public Color getColor() { return color; }

    /**
     * Paints the icon at the specified location.
     * <p>
     * This base implementation does nothing. Subclasses should override
     * this method to render the desired shape.
     *
     * @param c the component to which the icon is painted
     * @param g the graphics context
     * @param x the X coordinate of the icon's top-left corner
     * @param y the Y coordinate of the icon's top-left corner
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {

    }

    /**
     * Returns the width of the icon.
     * <p>
     * Base implementation returns 0. Subclasses should override with proper size.
     *
     * @return the width of the icon in pixels
     */
    @Override
    public int getIconWidth() {
        return 0;
    }

    /**
     * Returns the height of the icon.
     * <p>
     * Base implementation returns 0. Subclasses should override with proper size.
     *
     * @return the height of the icon in pixels
     */
    @Override
    public int getIconHeight() {
        return 0;
    }
}
