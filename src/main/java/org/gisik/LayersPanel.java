package org.gisik;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.expression.Expression;
import org.geotools.api.style.*;
import org.geotools.api.style.Stroke;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.StyleLayer;
import org.geotools.styling.LineSymbolizerImpl;
import org.geotools.swing.JMapPane;
import org.geotools.swing.MapPane;
import org.geotools.swing.styling.JSimpleStyleDialog;
import org.gisik.layersextra.TileLayerEx;
import org.gisik.layersview.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LayersPanel extends JPanel {

    private final JButton deleteBtn = new JButton();
    private final JButton moveupBtn = new JButton();
    private final JButton movedownBtn =  new JButton();
    private final JButton editBtn =  new JButton();
    private final JTable table;
    private final DefaultTableModel model = new DefaultTableModel();
    private final MapContent mapContent;
    private boolean osmCBLocked = false;
    private final JMapPane mapPane;

    public LayersPanel(MapContent mapContent, JMapPane mapPane) {
        this.mapContent = mapContent;
        this.mapPane = mapPane;
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
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column != 0) return false;
                if (osmCBLocked) {
                    Object val = model.getValueAt(row, 1);
                    if (val instanceof LayerNodeData data) {
                        return !"OSM Map".equals(data.getLabel());
                    }
                }
                return true;
            }
        };
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = table.columnAtPoint(e.getPoint());
                int row = table.rowAtPoint(e.getPoint());

                if (row >= 0) {
                    table.setRowSelectionInterval(row, row);
                }

                if (SwingUtilities.isLeftMouseButton(e) && col == 0 && row >= 0) {
                    Boolean checkBox = (Boolean) model.getValueAt(row, 0);
                    mapContent.layers().get(row).setVisible(checkBox);
                }
            }
        });

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

        JPopupMenu popup = new JPopupMenu();
        JMenuItem zoomItem = new JMenuItem("Zoom to Layer");

        zoomItem.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) return;

            Layer layer = mapContent.layers().get(row);
            LayerZoomUtils.zoomToLayer(layer, mapContent.getViewport());
            mapPane.setDisplayArea(mapContent.getViewport().getBounds());

        });
        popup.add(zoomItem);
        table.setComponentPopupMenu(popup);
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
        if(!table.isEditing()) {
            int idx = table.getSelectedRows()[0];
                if(mapContent.layers().get(idx) instanceof StyleLayer layer) {
                    Style style = JSimpleStyleDialog.showDialog(null, (SimpleFeatureType)layer.getFeatureSource().getSchema());
                    if(style != null) {
                        changeColorInPanel(layer, style, idx);
                        layer.setStyle(style);
                    }
                }
        }
    }

    public void add(final String text, FigureIcon icon,
            final boolean checked) {
        final LayerNodeData data = new LayerNodeData(text, icon, checked);
        model.insertRow(model.getRowCount(), new Object[]{checked, data});

    }

    public void replace(final String text, FigureIcon icon,
                    final boolean checked, int index) {
        model.removeRow(index);
        final LayerNodeData data = new LayerNodeData(text, icon, checked);
        model.insertRow(index, new Object[]{checked, data});

    }

    public void remove(int index){
        if(index >= 0 && index < model.getRowCount()) {
            model.removeRow(index);
        }
    }

    private void changeColorInPanel(Layer layer, Style style, int idx) {
        for (FeatureTypeStyle fts : style.featureTypeStyles()) {
            for (Rule rule : fts.rules()) {
                for (Symbolizer sym : rule.symbolizers()) {
                    if (sym instanceof PointSymbolizer ps) {
                        Graphic g = ps.getGraphic();
                        if (g != null) {
                            for (GraphicalSymbol gs : g.graphicalSymbols()) {
                                if (gs instanceof Mark mark) {
                                    Fill fill = mark.getFill();
                                    if (fill != null) {
                                        Color color = (Color) fill.getColor().evaluate(null);

                                        replace(layer.getTitle(), new PointIcon(color), layer.isVisible(), idx);

                                        //System.out.println("Kolor punktu: " + color);
                                    }
                                }
                            }
                        }
                    }

                    if (sym instanceof PolygonSymbolizer ps) {
                        Fill fill = ps.getFill();
                        if (fill != null) {
                            Color color = (Color) fill.getColor().evaluate(null);

                            replace(layer.getTitle(), new SquareIcon(color), layer.isVisible(), idx);
                        }
                    }

                    if (sym instanceof LineSymbolizer ls) {
                        Stroke stroke = ls.getStroke();
                        if (stroke != null) {
                            Color color = (Color) stroke.getColor().evaluate(null);
                            replace(layer.getTitle(), new LineIcon(color), layer.isVisible(), idx);
                        }
                    }
                }
            }
        }
    }

    public Color getLayersColor(int idx) {
        Object val = model.getValueAt(idx, 1);
        if (val instanceof LayerNodeData data) {
            return ((FigureIcon)data.getIcon()).getColor();
        }
        return null;
    }

    public void resetLayersColors() {
        int i=0;
        for(Layer layer : mapContent.layers()) {
            if(layer instanceof TileLayerEx) {
                i++;
                continue;
            }
            Color color = getLayersColor(i);
            Style style = ColorStyle.createStyle2(((SimpleFeatureSource) layer.getFeatureSource()).getSchema(), color);
            ((FeatureLayer) layer).setStyle(style);
            i++;
        }
        repaint();
    }

    public void reset() {
        model.setRowCount(0);
    }

    public void setOSMCBLocked(boolean locked) {
        this.osmCBLocked = locked;
    }

    public DefaultTableModel getModel(){
        return model;
    }

    public JTable getTable(){
        return table;
    }

}