package org.gisik;

import javax.swing.*;
import java.awt.*;

public class DialogBase extends JDialog {
    protected DialogBase(Frame parent, String title) {
        super(parent, title, true);
    }

    protected WidgetAndPanel<JCheckBox> createCheckBox(String labelText) {
        JPanel checkboxPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(false);
        checkboxPanel.add(label, BorderLayout.WEST);
        checkboxPanel.add(checkBox, BorderLayout.CENTER);
        return new WidgetAndPanel<>(checkBox, checkboxPanel);
    }

    protected WidgetAndPanel<JComboBox<String>> createLabelCombo(String labelText, String[] comboList) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JComboBox<String> combo = new JComboBox<>(comboList);
        panel.add(label, BorderLayout.WEST);
        panel.add(combo, BorderLayout.CENTER);
        return new WidgetAndPanel<>(combo, panel);
    }
}
