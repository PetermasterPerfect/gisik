package org.gisik.layerstree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import java.awt.*;

public class LayerNodeRenderer implements TreeCellRenderer {
    private final LayerNodePanel panel = new LayerNodePanel();
    private final DefaultTreeCellRenderer defaultRenderer =
            new DefaultTreeCellRenderer();

    private final Color selectionForeground, selectionBackground;
    private final Color textForeground, textBackground;

    protected LayerNodePanel getPanel() {
        return panel;
    }

    public LayerNodeRenderer() {
        final Font fontValue = UIManager.getFont("Tree.font");
        if (fontValue != null) panel.label.setFont(fontValue);

        selectionForeground = UIManager.getColor("Tree.selectionForeground");
        selectionBackground = UIManager.getColor("Tree.selectionBackground");
        textForeground = UIManager.getColor("Tree.textForeground");
        textBackground = UIManager.getColor("Tree.textBackground");
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                           boolean selected, boolean expanded,
                                           boolean leaf, int row, boolean hasFocus)
    {
        LayerNodeData nodeData = null;
        if(value instanceof DefaultMutableTreeNode node) {
            Object obj = node.getUserObject();
            if(obj instanceof LayerNodeData) {
                nodeData = (LayerNodeData) obj;
            }
        }

        String stringValue =
                tree.convertValueToText(value, selected, expanded, leaf, row, false);
        panel.label.setText(stringValue);
        panel.checkBox.setSelected(false);

        if(selected) {
            panel.setForeground(selectionForeground);
            panel.setBackground(selectionBackground);
            panel.label.setForeground(selectionForeground);
            panel.label.setBackground(selectionBackground);
        }
        else {
            panel.setForeground(textForeground);
            panel.setBackground(textBackground);
            panel.label.setForeground(textForeground);
            panel.label.setBackground(textBackground);
        }

        if (nodeData == null) {
            return defaultRenderer.getTreeCellRendererComponent(tree, value,
                    selected, expanded, leaf, row, hasFocus);
        }

        panel.label.setText(nodeData.getLabel());
        panel.label.setIcon(nodeData.getIcon());
        panel.checkBox.setSelected(nodeData.isChecked());

        return panel;
    }
}
