package org.gisik;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.style.Style;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.geotools.styling.SLD;
import org.geotools.swing.JMapPane;
import org.geotools.swing.control.JMapStatusBar;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.tool.*;
import org.gisik.layerstree.LineIcon;
import org.gisik.layerstree.PointIcon;
import org.gisik.layerstree.SquareIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;


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
                try {
                    File file = JFileDataStoreChooser.showOpenFile("shp", null);
                    if (file == null) {
                        return;
                    }
                    FileDataStore store = FileDataStoreFinder.getDataStore(file);
                    System.out.println(file.getName());
                    SimpleFeatureSource featureSource = store.getFeatureSource();
                    Style style = SLD.createSimpleStyle(featureSource.getSchema());
                    Layer layer = new FeatureLayer(featureSource, style);
                    mapContent.addLayer(layer);
                    SimpleFeatureType schema = featureSource.getSchema();

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
                    System.err.println(ex);
                }
            }
        });
        JMenuItem addCsvItem = new JMenuItem("Add csv");
        menu = new JMenu("Layer");
        menu.add(addVectorItem);
        menu.add(addCsvItem);
        return menu;
    }

    private void createMenu() {
        JMenuBar menuBar;

        menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createLayerMenu());

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
        JMapPane mapPane = new JMapPane();

        setupPanAndZoom(mapPane);
        mapPane.setMapContent(mapContent);
        JPanel panel = new JPanel();
        panel.add(JMapStatusBar.createDefaultStatusBar(mapPane), "grow");
        this.getContentPane().add(panel, BorderLayout.SOUTH);

        layersPanel = new LayersPanel();
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(layersPanel), mapPane);
        splitPane.setOneTouchExpandable(true);
        this.add(splitPane, BorderLayout.CENTER);

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}