package org.gisik;
import org.gisik.layerstree.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

class LayersPanel extends JPanel {

    final DefaultListModel model = new DefaultListModel();
    public LayersPanel() {
        JList list = new JList(model);
        list.setCellRenderer(new LayerNodeRenderer());
        JLabel lab = new JLabel("Layers Panel");
        setLayout(new BorderLayout());
        add(lab, BorderLayout.PAGE_START);
        add(new JScrollPane(list), BorderLayout.CENTER);
    }

    public void add(final String text,
            final boolean checked)
    {
        final LayerNodeData data = new LayerNodeData(text, checked);
        model.add(0, data);
    }

    public void add(final String text, Icon icon,
            final boolean checked)
    {
        final LayerNodeData data = new LayerNodeData(text, icon, checked);
        model.add(0, data);
    }
}