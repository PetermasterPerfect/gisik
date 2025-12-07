package org.gisik;

import javax.swing.*;

class WidgetAndPanel<T> {
    public T widget;
    public JPanel panel;
    WidgetAndPanel(T widget, JPanel panel) {
        this.widget = widget;
        this.panel  = panel;
    }
}
