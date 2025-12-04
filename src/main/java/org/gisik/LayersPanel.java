package org.gisik;
import org.geotools.map.MapContent;
import org.gisik.layerstree.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

class LayersPanel extends JPanel {

    private final JButton deleteBtn = new JButton();
    private final JButton moveupBtn = new JButton();
    private final JButton movedownBtn =  new JButton();
    private final JButton editBtn =  new JButton();
    private final JTable table;
    private final DefaultTableModel model = new DefaultTableModel();
    private final MapContent mapContent;
    public LayersPanel(MapContent mapContent) {
        this.mapContent = mapContent;
        model.addColumn("");
        model.addColumn("Layers panel");
        table = new JTable(model) {
            @Override
            public Class getColumnClass(int column) {
                switch (column) {
                    case 0:
                        return Boolean.class;
                    default:
                        return LayerNodePanel.class;
                }
            }
        };
        table.setTableHeader(null);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(1).setCellRenderer(new LayerNodeRenderer());
        table.getColumnModel().getColumn(1).setCellEditor(new LayerNodeEditor());
        setLayout(new BorderLayout());
        add(new JLabel("Layers Panel"), BorderLayout.PAGE_START);
        add(setupButtons(),  BorderLayout.PAGE_END);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setPreferredSize(new Dimension(200, -1));
    }

    private JPanel setupButtons()
    {
        JPanel btnsPanel = new JPanel(new FlowLayout());
        btnsPanel.setPreferredSize(new Dimension(-1, 50));
        deleteBtn.setIcon(new ImageIcon(getClass().getResource("remove.png")));
        moveupBtn.setIcon(new ImageIcon(getClass().getResource("arrowup.png")));
        movedownBtn.setIcon(new ImageIcon(getClass().getResource("arrowdown.png")));
        editBtn.setIcon(new ImageIcon(getClass().getResource("edit.png")));

        deleteBtn.setPreferredSize(new Dimension(30, 30));
        moveupBtn.setPreferredSize(new Dimension(30, 30));
        movedownBtn.setPreferredSize(new Dimension(30, 30));
        editBtn.setPreferredSize(new Dimension(30, 30));
        btnsPanel.add(deleteBtn);
        btnsPanel.add(moveupBtn);
        btnsPanel.add(movedownBtn);
        btnsPanel.add(editBtn);

        deleteBtn.addActionListener(e -> deleteLayer());
        moveupBtn.addActionListener(e -> moveupLayer());
        movedownBtn.addActionListener(e -> movedownLayer());
        editBtn.addActionListener(e -> editLayer());

        return btnsPanel;
    }

    private void deleteLayer() {
        if(!table.isEditing()) {
            int idx = table.getSelectedRows()[0];
            model.removeRow(idx);
            mapContent.removeLayer(mapContent.layers().get(idx));
        }

    }

    private void moveupLayer() {
        if(!table.isEditing()) {
            int idx = table.getSelectedRows()[0];
            if (idx > 0) {
                model.moveRow(idx, idx, idx - 1);
                mapContent.moveLayer(idx, idx - 1);
            }
        }
    }

    private void movedownLayer() {
        if(!table.isEditing()) {
            int idx = table.getSelectedRows()[0];
            if (idx < table.getRowCount() - 1) {
                model.moveRow(idx, idx, idx + 1);
                mapContent.moveLayer(idx, idx + 1);
            }
        }
    }

    private void editLayer() {
    }

    public void add(final String text,
            final boolean checked) {
        final LayerNodeData data = new LayerNodeData(text, checked);
        model.insertRow(0, new Object[]{checked, data});
    }

    public void add(final String text, Icon icon,
            final boolean checked) {
        final LayerNodeData data = new LayerNodeData(text, icon, checked);
        model.insertRow(0, new Object[]{checked, data});
    }

    public void remove(int index){
        if(index >= 0 && index < model.getRowCount()) {
            model.removeRow(index);
        }
    }
}