package org.gisik;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.Style;
import org.geotools.map.*;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.JMapPane;
import org.geotools.swing.control.JMapStatusBar;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.styling.JSimpleStyleDialog;
import org.geotools.swing.tool.*;
import org.geotools.tile.TileService;
import org.geotools.tile.impl.osm.OSMService;
import org.geotools.tile.util.AsyncTileLayer;
import org.geotools.tile.util.TileLayer;
import org.gisik.layerstree.LayerNodeData;
import org.gisik.layerstree.LineIcon;
import org.gisik.layerstree.PointIcon;
import org.gisik.layerstree.SquareIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class App  extends JFrame {

    private final MapContent mapContent;
    private LayersPanel layersPanel = null;
    private JCheckBoxMenuItem showLayersPanelItem;
    final private JSplitPane splitPane;

    private int divSize = 0;
    private int divLoc = 0;

    private JMenu createFileMenu() {
        JMenu menu;
        JMenuItem openItem, saveItem;
        menu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save");
        menu.add(openItem);
        menu.add(saveItem);
        return menu;
    }

    private JMenu createEditMenu() {
        JMenu menu;
        menu = new JMenu("Edit");
        JMenuItem deleteLayers = new JMenuItem(new AbstractAction("Delete Layers") {
            public void actionPerformed(ActionEvent e) {
                openLayerDeletion();
            }
        });

        menu.add(deleteLayers);
        return menu;
    }

    private JMenu createViewMenu() {
        JMenu menu;
        menu = new JMenu("View");
        showLayersPanelItem = new JCheckBoxMenuItem(new AbstractAction("Show Layers Panel") {
            public void actionPerformed(ActionEvent e) {
                displayLayerPanel();
            }
        });
        JMenuItem test = new JMenuItem(new AbstractAction("test") {
            public void actionPerformed(ActionEvent e) {
                System.out.println("test");
            }
        });
        showLayersPanelItem.setSelected(true);
        menu.add(showLayersPanelItem);
        menu.add(test);
        return menu;
    }

    private JMenu createLayerMenu() {
        JMenu menu;
        JMenuItem addVectorItem = new JMenuItem(new AbstractAction("Add Vector") {
            public void actionPerformed(ActionEvent e) {
                openShape();
            }
        });
        JMenuItem addCsvItem = new JMenuItem(new AbstractAction("Add csv") {
            public void actionPerformed(ActionEvent e) {
                openCsv();
            }
        });
        menu = new JMenu("Layer");
        menu.add(addVectorItem);
        menu.add(addCsvItem);
        return menu;
    }

    private JMenu createBaseMapMenu() {
        JMenu menu;
        JMenuItem addOSMMap = new JMenuItem(new AbstractAction("Add OSM Map") {
            public void actionPerformed(ActionEvent e) {
                addOSM();
            }
        });

        menu = new JMenu("Base Maps");
        menu.add(addOSMMap);
        return menu;
    }

    private void addOSM(){
        List<String> createdLayers =  mapContent.layers().stream().map(Layer::getTitle).toList() ;

        if(!createdLayers.contains("OSM Map") &&
                (CRS.isEquivalent(DefaultGeographicCRS.WGS84, mapContent.getCoordinateReferenceSystem()) ||
                        mapContent.getCoordinateReferenceSystem() == null
                )){

            String baseURL = "https://tile.openstreetmap.org/";
            TileService service = new OSMService("OSM", baseURL);
            TileLayer tileLayer = new AsyncTileLayer(service);
            tileLayer.setTitle("OSM Map");

            mapContent.addLayer(tileLayer);
            layersPanel.add("OSM Map", null, true);
            layersPanel.revalidate();
            layersPanel.repaint();
        }
    }


    private void openShape() {
        try {
            File file = JFileDataStoreChooser.showOpenFile("shp", null);
            if (file == null) {
                return;
            }
            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            if(store == null)
                throw new IOException("Invalid vector file.");
            System.out.println(file.getName());
            SimpleFeatureSource featureSource = store.getFeatureSource();
            Style style = JSimpleStyleDialog.showDialog(null, featureSource.getSchema());//SLD.createSimpleStyle(featureSource.getSchema());
            Layer layer = new FeatureLayer(featureSource, style);
            SimpleFeatureType schema = featureSource.getSchema();
            layer.setTitle(schema.getName().toString());
            mapContent.addLayer(layer);
            GeometryDescriptor geomDesc = schema.getGeometryDescriptor();
            Class<?> geomBinding = geomDesc.getType().getBinding();

            String geomType = geomBinding.getSimpleName();
            if (geomType.contains("Point")) {
                layersPanel.add(schema.getName().toString(), new PointIcon(Color.RED), true);
            } else if (geomType.contains("Line")) {
                layersPanel.add(schema.getName().toString(), new LineIcon(Color.RED), true);
            } else if (geomType.contains("Polygon")) {
                layersPanel.add(schema.getName().toString(), new SquareIcon(Color.RED), true);
            } else {
                System.out.println("Unknown geom type: " + geomType);
            }
            layersPanel.revalidate();
            layersPanel.repaint();
            store.dispose();
        }
        catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Invalid vector file.", "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(ex);
        }
    }

    private void openCsv() {

        File file = JFileDataStoreChooser.showOpenFile("csv", null);
        if (file == null) {
            return;
        }
        if(!file.canRead()){
            JOptionPane.showMessageDialog(null, "Given file path has no read persmission", "Error", JOptionPane.ERROR_MESSAGE);
        }

        try {
            CsvLoaderDialog csvLoader = new CsvLoaderDialog(file, mapContent, layersPanel, this);
            csvLoader.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot read a file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createMenu() {
        JMenuBar menuBar;

        menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createLayerMenu());
        menuBar.add(createBaseMapMenu());

        this.setJMenuBar(menuBar);
    }

    private void setupPanAndZoom(JMapPane mapPane) {
        PanTool panTool = new PanTool();
        panTool.setMapPane(mapPane);
        mapPane.setCursorTool(panTool);
        mapPane.addMouseListener(new ScrollWheelTool(mapPane));

    }

    private void displayLayerPanel() {
        if(showLayersPanelItem.getState()) {
            Component left = splitPane.getLeftComponent();
            left.setVisible(true);
            splitPane.setDividerSize(divSize);
            splitPane.setDividerLocation(divLoc);
        } else {
            Component left = splitPane.getLeftComponent();
            left.setVisible(false);
            divSize = splitPane.getDividerSize();
            divLoc = splitPane.getDividerLocation();
            splitPane.setDividerSize(0);
            splitPane.setDividerLocation(0.0);
        }
    }

    private void openLayerDeletion() {
        List<Layer> mapLayers = mapContent.layers();
        String[] names = mapLayers.stream().map(Layer::getTitle).toArray(String[]::new);

        JList<String> list = new JList<>(names);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        int result = JOptionPane.showConfirmDialog(
                null,
                scrollPane,
                "Select Layers to Delete",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int[] selected = list.getSelectedIndices();
            if (selected.length == 0) return;

            deleteSelectedLayers(selected, mapLayers);
        }
    }

    private void deleteSelectedLayers(int[] selectedIndices, List<Layer> allLayers) {
        Arrays.sort(selectedIndices);

        List<Layer> toRemove = new ArrayList<>();
        for (int idx : selectedIndices) {
            if (idx >= 0 && idx < allLayers.size()) {
                toRemove.add(allLayers.get(idx));
            }
        }

        for (Layer layer : toRemove) {
            mapContent.removeLayer(layer);
        }

        for (int i = selectedIndices.length - 1; i >= 0; i--) {
            int uiIndex = selectedIndices[i];
            layersPanel.remove(uiIndex);
        }

        layersPanel.revalidate();
        layersPanel.repaint();
    }

    public App() {
        createMenu();
        setTitle("gisik");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        mapContent = new MapContent();
        MapViewport viewport = new MapViewport();
        viewport.setFixedBoundsOnResize(true);
        mapContent.setViewport(viewport);

        mapContent.getViewport().addMapBoundsListener(event -> {
            if ((event.getType() & MapBoundsEvent.COORDINATE_SYSTEM_MASK) != 0) {

                CoordinateReferenceSystem newCrs = mapContent.getCoordinateReferenceSystem();
                boolean isWgs84 = CRS.isEquivalent(newCrs, DefaultGeographicCRS.WGS84);
                layersPanel.setOSMCBLocked(!isWgs84);

                for (int i = 0; i < layersPanel.getModel().getRowCount(); i++) {
                    Object value = layersPanel.getModel().getValueAt(i, 1);
                    String label = null;

                    if (value instanceof LayerNodeData data) {
                        label = data.getLabel();
                    }

                    if ("OSM Map".equals(label)) {
                        mapContent.layers().get(i).setVisible(isWgs84);
                        layersPanel.getModel().setValueAt(isWgs84, i, 0);

                        if (layersPanel.getTable().isEditing()) {
                            layersPanel.getTable().getCellEditor().stopCellEditing();
                        }
                        layersPanel.getModel().fireTableRowsUpdated(i, i);
                    }
                }
            }
        });

        JMapPane mapPane = new JMapPane();
        setupPanAndZoom(mapPane);
        mapPane.setMapContent(mapContent);
        JPanel panel = new JPanel();
        panel.add(JMapStatusBar.createDefaultStatusBar(mapPane), "grow");
        this.getContentPane().add(panel, BorderLayout.SOUTH);

        layersPanel = new LayersPanel(mapContent);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(layersPanel), mapPane);
        splitPane.setOneTouchExpandable(true);
        this.add(splitPane, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}