package org.gisik.layersview;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * A simple panel representing a node in the layers panel.
 * <p>
 * It contains a JLabel to display the layer's name or label.
 */
public class LayerNodePanel extends JPanel {
    /** The label used to display the text of the layer node. */
    public JLabel label = new JLabel();

    /**
     * Returns the text of this layer node.
     *
     * @return the text displayed in the label
     */
    @Override
    public String toString() {
        return label.getText();
    }

    /**
     * Constructs a new LayerNodePanel with a BorderLayout
     * and places the label at the center.
     */
    public LayerNodePanel() {
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
    }
}
