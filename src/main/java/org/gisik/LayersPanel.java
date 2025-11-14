package org.gisik;
import org.gisik.layerstree.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

class LayersPanel extends JPanel {

    private final DefaultMutableTreeNode root;
    private final JTree tree;
    public LayersPanel() {
        root = new DefaultMutableTreeNode(".");

        final DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree = new JTree(treeModel);
        tree.setDragEnabled(true);
        tree.setDropMode(DropMode.ON_OR_INSERT);

        final LayerNodeRenderer renderer = new LayerNodeRenderer();
        tree.setCellRenderer(renderer);

        final LayerNodeEditor editor = new LayerNodeEditor(tree);
        tree.setCellEditor(editor);
        tree.setEditable(true);
        JLabel lab = new JLabel("Layers Panel");
        setLayout(new BorderLayout());
        add(lab, BorderLayout.PAGE_START);
        add(new JScrollPane(tree), BorderLayout.CENTER);
    }

    public void add(final String text,
            final boolean checked)
    {
        LayerNodeData data = new LayerNodeData(text, checked);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.insertNodeInto(node, root, root.getChildCount());
    }

    public void add(final String text, Icon icon,
            final boolean checked)
    {
        final LayerNodeData data = new LayerNodeData(text, icon, checked);
        final DefaultMutableTreeNode node = new DefaultMutableTreeNode(data);
        DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
        model.insertNodeInto(node, root, root.getChildCount());
    }
}