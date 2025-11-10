package org.gisik;
import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
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

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class App  extends JFrame {

    private final MapContent mapContent;

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
                    SimpleFeatureSource featureSource = store.getFeatureSource();
                    Style style = SLD.createSimpleStyle(featureSource.getSchema());
                    Layer layer = new FeatureLayer(featureSource, style);
                    mapContent.addLayer(layer);
                }
                catch (IOException ex) {
                    System.err.println(ex.toString());
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

    public App() {
        createMenu();
        setTitle("gisik");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        mapContent = new MapContent();
        MapViewport viewport = new MapViewport();
        viewport.setFixedBoundsOnResize(true);
        mapContent.setViewport(viewport);
        JMapPane mapPane = new JMapPane();

        setupPanAndZoom(mapPane);
        mapPane.setMapContent(mapContent);
        this.setLayout(new BorderLayout());
        this.add(mapPane, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        panel.add(JMapStatusBar.createDefaultStatusBar(mapPane), "grow");
        this.getContentPane().add(panel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        new App();
    }
}