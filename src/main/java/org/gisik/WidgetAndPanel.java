package org.gisik;

import javax.swing.*;

/**
 * A simple utility container that binds a Swing widget with its associated panel.
 *
 * This class is used to return both a UI component and the panel that visually
 * wraps it, allowing dialogs and forms to easily insert labeled or grouped
 * components into layouts.
 *
 * @param <T> the type of the wrapped Swing widget
 */
public class WidgetAndPanel<T> {
    /** A widget */
    public T widget;

    /** An instance of JPanel */
    public JPanel panel;

    /**
     * Creates a new widget-panel pair.
     *
     * @param widget the UI component (e.g. {@link javax.swing.JCheckBox}, {@link javax.swing.JComboBox})
     * @param panel  the panel that visually contains and arranges the widget
     */
    WidgetAndPanel(T widget, JPanel panel) {
        this.widget = widget;
        this.panel  = panel;
    }
}
