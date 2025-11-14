package org.gisik.layerstree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class LayerNodeEditor extends AbstractCellEditor implements
        TreeCellEditor
{

    private final LayerNodeRenderer renderer = new LayerNodeRenderer();

    private final JTree theTree;

    public LayerNodeEditor(final JTree tree) {
        theTree = tree;
    }

    @Override
    public Object getCellEditorValue() {
        final LayerNodePanel panel = renderer.getPanel();
        return  new LayerNodeData(panel.label.getText(), panel.label.getIcon(), panel.checkBox.isSelected());
    }

    @Override
    public boolean isCellEditable(final EventObject event) {
        if (!(event instanceof MouseEvent mouseEvent)) return false;

        final TreePath path =
                theTree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
        if (path == null) return false;

        final Object node = path.getLastPathComponent();
        if (!(node instanceof DefaultMutableTreeNode treeNode)) return false;

        final Object userObject = treeNode.getUserObject();
        return userObject instanceof LayerNodeData;
    }

    @Override
    public Component getTreeCellEditorComponent(final JTree tree,
                                                final Object value, final boolean selected, final boolean expanded,
                                                final boolean leaf, final int row)
    {

        final Component editor =
                renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf,
                        row, true);

        final ItemListener itemListener = new ItemListener() {

            @Override
            public void itemStateChanged(final ItemEvent itemEvent) {
                if (stopCellEditing()) {
                    fireEditingStopped();
                }
            }
        };
        if (editor instanceof LayerNodePanel panel) {
            panel.checkBox.addItemListener(itemListener);
        }

        return editor;
    }
}