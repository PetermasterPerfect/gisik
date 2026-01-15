package org.gisik.layersview;

import javax.swing.*;
import java.awt.BorderLayout;

public class LayerNodePanel extends JPanel {
    public JLabel label = new JLabel();

    @Override
    public String toString() {
        return label.getText();
    }

    public LayerNodePanel() {
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
    }
}
