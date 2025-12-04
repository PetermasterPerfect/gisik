package org.gisik.layerstree;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class LayerNodeRenderer extends LayerNodePanel implements TableCellRenderer {
    public LayerNodeRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

            if (isSelected) {
                setBackground(Color.BLUE);
            } else {
                setBackground(Color.WHITE);
            }

        LayerNodeData nodeData = (LayerNodeData) value;
        this.setOpaque(true);
        this.label.setText(nodeData.getLabel());
        this.label.setIcon(nodeData.getIcon());

        return this;
    }
}