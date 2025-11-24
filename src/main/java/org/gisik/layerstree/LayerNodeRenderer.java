package org.gisik.layerstree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class LayerNodeRenderer extends JLabel implements ListCellRenderer<Object> {
    private final LayerNodePanel panel = new LayerNodePanel();
    public Component getListCellRendererComponent(
            JList<?> list,           // the list
            Object value,            // value to display
            int index,               // cell index
            boolean isSelected,      // is the cell selected
            boolean cellHasFocus)
    {
        LayerNodeData nodeData = (LayerNodeData) value;

        panel.label.setText(nodeData.getLabel());
        panel.label.setIcon(nodeData.getIcon());
        panel.checkBox.setSelected(nodeData.isChecked());

        return panel;
    }
}