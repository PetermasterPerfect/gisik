package org.gisik;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.map.*;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.swing.JMapPane;
import org.geotools.swing.control.JMapStatusBar;
import org.geotools.swing.data.JFileDataStoreChooser;
import org.geotools.swing.tool.*;
import org.gisik.crs.CrsDialog;
import org.gisik.csv.CsvLoaderDialog;
import org.gisik.layersview.LayerNodeData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Main application class for the GISik project.
 * This class initializes the main GUI window, manages menus,
 * map content, layers, and interactions with the user.
 * It supports adding vector layers (SHP, CSV), base maps (OSM),
 * CRS transformations, and project save/load functionality.
 */

public class App  extends JFrame {

    /** The main map pane for rendering layers */
    private final JMapPane mapPane;

    /** The viewport that defines the CRS and map bounds */
    private final MapViewport viewport;

    /** The MapContent holds all layers for the current session */
    private final MapContent mapContent;

    /** Panel showing all layers and their visibility */
    private LayersPanel layersPanel = null;

    /** Checkbox menu item for showing/hiding the layers panel */
    private JCheckBoxMenuItem showLayersPanelItem;

    /** Checkbox menu item for enabling/disabling distance measurement tool */
    private JCheckBoxMenuItem measureDistItem;

    /** Split pane separating the layers panel and the map pane */
    private final JSplitPane splitPane;

    /** The manager handling project files, layers, and CRS info */
    private ProjectManager projectManager = null;

    /** Variables to preserve split pane divider location and size */
    private int divSize = 0;
    private int divLoc = 0;

    /**
     * Creates the "File" menu with options to open, save, and save as a project.
     *
     * @return JMenu the constructed File menu
     */
    private JMenu createFileMenu() {
        JMenu menu;
        JMenuItem openItem, saveItem, saveAsItem;
        menu = new JMenu("File");

        openItem = new JMenuItem(new AbstractAction("Open") {
            @Override
            public void actionPerformed(ActionEvent e) {
                openProject();
            }
        });

        saveItem = new JMenuItem(new AbstractAction("Save") {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveProject();
            }
        });

        saveAsItem = new JMenuItem(new AbstractAction("Save as") {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsProject();
            }
        });

        menu.add(openItem);
        menu.add(saveItem);
        menu.add(saveAsItem);
        return menu;
    }

    /**
     * Saves the current project. If no project exists, prompts user to create one.
     */
    private void saveProject() {
        if(projectManager == null) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if(option == JFileChooser.APPROVE_OPTION){
                projectManager = new ProjectManager(mapContent, layersPanel, fileChooser.getSelectedFile().getAbsolutePath());
            }
        }
        projectManager.saveProject();
    }

    /**
     * Saves the project to a new file location chosen by the user.
     */
    private void saveAsProject() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if(option == JFileChooser.APPROVE_OPTION){
            projectManager = new ProjectManager(mapContent, layersPanel, fileChooser.getSelectedFile().getAbsolutePath());
        }
        projectManager.saveProject();
    }


    /**
     * Opens an existing project file, clearing current layers and loading saved layers.
     */
    private void openProject() {
        File file = JFileDataStoreChooser.showOpenFile("", this);
        if (file == null) {
            return;
        }
        if(!file.canRead()){
            JOptionPane.showMessageDialog(null, "Given file path has no read persmission", "Error", JOptionPane.ERROR_MESSAGE);
        }
        layersPanel.reset();
        for(Layer layer : mapContent.layers())
            mapContent.removeLayer(layer);

        projectManager = new ProjectManager(mapContent, layersPanel, file.getAbsolutePath());
        projectManager.openProject();
    }

    /**
     * Creates the "Edit" menu, currently with option to delete layers.
     *
     * @return JMenu the constructed Edit menu
     */
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

    /**
     * Creates the "View" menu, allowing the user to show/hide layers panel,
     * set CRS, and enable the measure distance tool.
     *
     * @return JMenu the constructed View menu
     */
    private JMenu createViewMenu() {
        JMenu menu;
        menu = new JMenu("View");
        showLayersPanelItem = new JCheckBoxMenuItem(new AbstractAction("Show Layers Panel") {
            public void actionPerformed(ActionEvent e) {
                displayLayerPanel();
            }
        });

        measureDistItem = new JCheckBoxMenuItem(new AbstractAction("Measure Distance") {
            public void actionPerformed(ActionEvent e) {
                if(measureDistItem.getState()) {
                    mapPane.setCursorTool(new PointSelection(mapPane, layersPanel));
                } else {
                    setupPanAndZoom(mapPane);
                    layersPanel.resetLayersColors();
                }
            }
        });

        JMenuItem setCrsItem = new JMenuItem(new AbstractAction("Set CRS") {
           public void actionPerformed(ActionEvent e) {
               openCrsDialog();
           }
        });

        showLayersPanelItem.setSelected(true);
        menu.add(showLayersPanelItem);
        menu.add(setCrsItem);
        menu.add(measureDistItem);
        return menu;
    }

    /**
     * Creates the "Layer" menu with options to add vector (SHP) or CSV layers.
     *
     * @return JMenu the constructed Layer menu
     */
    private JMenu createLayerMenu() {
        JMenu menu;
        JMenuItem addVectorItem = new JMenuItem(new AbstractAction("Add Vector") {
            public void actionPerformed(ActionEvent e) {
                openShapeWithDialog();
            }
        });
        JMenuItem addCsvItem = new JMenuItem(new AbstractAction("Add Csv") {
            public void actionPerformed(ActionEvent e) {
                openCsv();
            }
        });
        menu = new JMenu("Layer");
        menu.add(addVectorItem);
        menu.add(addCsvItem);
        return menu;
    }

    /**
     * Creates the "Base Maps" menu with option to add OSM map.
     *
     * @return JMenu the constructed Base Maps menu
     */
    private JMenu createBaseMapMenu() {
        JMenu menu;
        JMenuItem addOSMMap = new JMenuItem(new AbstractAction("Add OSM Map") {
            public void actionPerformed(ActionEvent e) {
                LayerManager.addOSM(mapContent, layersPanel);
            }
        });

        menu = new JMenu("Base Maps");
        menu.add(addOSMMap);
        return menu;
    }

    /**
     * Opens a file chooser to select a SHP file and adds it to the map.
     */
    private void openShapeWithDialog() {
            File file = JFileDataStoreChooser.showOpenFile("shp", null);
            if (file == null) {
                return;
            }
            LayerManager.addShape(file, mapContent, layersPanel);
    }

    /**
     * Opens a file chooser to select a CSV file and opens a loader dialog for it.
     */
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

    /**
     * Opens a dialog for selecting a new CRS for the project.
     */
    private void openCrsDialog() {
        CrsDialog crsDialog = new CrsDialog( viewport, this);
        crsDialog.setVisible(true);
    }

    /**
     * Creates and sets the main menu bar of the application.
     */
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

    /**
     * Configures pan and zoom tools for the given map pane.
     *
     * @param mapPane the map pane to configure
     */
    private void setupPanAndZoom(JMapPane mapPane) {
        PanTool panTool = new PanTool();
        panTool.setMapPane(mapPane);
        mapPane.setCursorTool(panTool);
        mapPane.addMouseListener(new ScrollWheelTool(mapPane));

    }

    /**
     * Shows or hides the layers panel depending on the menu item state.
     */
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

    /**
     * Opens a dialog allowing the user to select layers to delete.
     */
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

    /**
     * Deletes selected layers from the map and layers panel.
     *
     * @param selectedIndices indices of layers to remove
     * @param allLayers       all current layers in the map
     */
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

    /**
     * Constructor for the main application window.
     * <p>
     * Initializes menus, map content, viewport, layers panel,
     * and sets up the GUI layout.
     */
    public App() {
        createMenu();
        setTitle("gisik");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        mapContent = new MapContent();
        viewport = new MapViewport();
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

        mapPane = new JMapPane();
        setupPanAndZoom(mapPane);
        mapPane.setMapContent(mapContent);
        JPanel panel = new JPanel();
        panel.add(JMapStatusBar.createDefaultStatusBar(mapPane), "grow");
        this.getContentPane().add(panel, BorderLayout.SOUTH);

        layersPanel = new LayersPanel(mapContent, mapPane);
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(layersPanel), mapPane);
        splitPane.setOneTouchExpandable(true);
        this.add(splitPane, BorderLayout.CENTER);

    }

    /**
     * Main entry point of the application.
     *
     * @param args command line arguments (unused)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}