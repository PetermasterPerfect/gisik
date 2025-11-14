package org.gisik.layerstree;

import javax.swing.*;
import java.awt.Insets;
import java.awt.BorderLayout;

public class LayerNodePanel extends JPanel {
    public JLabel label = new JLabel();
    public JCheckBox checkBox = new JCheckBox();
    public LayerNodePanel() {
        checkBox.setMargin(new Insets(0, 0, 0, 0));
        setLayout(new BorderLayout());
        add(checkBox, BorderLayout.WEST);
        add(label, BorderLayout.CENTER);
    }
}
