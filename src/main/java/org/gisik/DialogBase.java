package org.gisik;

import javax.swing.*;
import java.awt.*;

/**
 * Base class for modal dialogs in the application.
 * Provides utility methods for creating labeled checkboxes and combo boxes.
 */
public class DialogBase extends JDialog {
    /**
     * Constructs a modal dialog with the given parent frame and title.
     *
     * @param parent the parent frame of this dialog
     * @param title  the title to display on the dialog
     */
    protected DialogBase(Frame parent, String title) {
        super(parent, title, true);
    }

    /**
     * Creates a labeled {@link JCheckBox} wrapped in a {@link JPanel}.
     *
     * @param labelText the text label to display next to the checkbox
     * @return a {@link WidgetAndPanel} containing the checkbox and its panel
     */
    protected WidgetAndPanel<JCheckBox> createCheckBox(String labelText) {
        JPanel checkboxPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(false);
        checkboxPanel.add(label, BorderLayout.WEST);
        checkboxPanel.add(checkBox, BorderLayout.CENTER);
        return new WidgetAndPanel<>(checkBox, checkboxPanel);
    }

    /**
     * Creates a labeled {@link JComboBox} wrapped in a {@link JPanel}.
     *
     * @param labelText the text label to display next to the combo box
     * @param comboList an array of strings to populate the combo box
     * @return a {@link WidgetAndPanel} containing the combo box and its panel
     */
    protected WidgetAndPanel<JComboBox<String>> createLabelCombo(String labelText, String[] comboList) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JComboBox<String> combo = new JComboBox<>(comboList);
        panel.add(label, BorderLayout.WEST);
        panel.add(combo, BorderLayout.CENTER);
        return new WidgetAndPanel<>(combo, panel);
    }
}
