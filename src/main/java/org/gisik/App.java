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
import org.gisik.crs.CrsLookup;
import org.gisik.csv.CsvLoaderDialog;
import org.gisik.layersextra.FeatureLayerProject;
import org.gisik.layersview.LayerNodeData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class App  extends JFrame {

    private final MapViewport viewport;
    private final MapContent mapContent;
    private LayersPanel layersPanel = null;
    private JCheckBoxMenuItem showLayersPanelItem;
    final private JSplitPane splitPane;
    private ProjectManager projectManager = null;
    private CoordinateReferenceSystem displayCrs;
    private CoordinateReferenceSystem projectCrs;

    private int divSize = 0;
    private int divLoc = 0;

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

    private void saveProject() {
        if(projectManager == null) {
            JFileChooser fileChooser = new JFileChooser();
            int option = fileChooser.showSaveDialog(this);
            if(option == JFileChooser.APPROVE_OPTION){
                projectManager = new ProjectManager(mapContent, layersPanel, fileChooser.getSelectedFile().getAbsolutePath());
                projectManager.setProjectCrs(projectCrs);
                projectManager.setDisplayCrs(displayCrs);
            }
        }
        projectManager.saveProject();
    }

    private void saveAsProject() {
        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(this);
        if(option == JFileChooser.APPROVE_OPTION){
            projectManager = new ProjectManager(mapContent, layersPanel, fileChooser.getSelectedFile().getAbsolutePath());
            projectManager.setDisplayCrs(displayCrs);
        }
        projectManager.saveProject();
    }

    private void openProject() {
        File file = JFileDataStoreChooser.showOpenFile("", this);
        if (file == null) {
            return;
        }
        if(!file.canRead()){
            JOptionPane.showMessageDialog(null, "Given file path has no read persmission", "Error", JOptionPane.ERROR_MESSAGE);
        }
        projectManager = new ProjectManager(mapContent, layersPanel, file.getAbsolutePath());
        projectManager.setDisplayCrs(displayCrs);
        projectManager.setProjectCrs(projectCrs);
        projectManager.openProject();
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
                viewport.setCoordinateReferenceSystem(CrsLookup.find("EPSG2178"));
                System.out.println("test");
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
        menu.add(test);
        return menu;
    }

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

    private void openShapeWithDialog() {
            File file = JFileDataStoreChooser.showOpenFile("shp", null);
            if (file == null) {
                return;
            }
        try {
            FeatureLayerProject pl =
                    LayerManager.loadShape(file, projectCrs);

            projectManager.addLayer(pl);
            projectManager.rebuildRenderLayers(mapContent);

            layersPanel.add(pl.getTitle(), null, true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
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
            CsvLoaderDialog csvLoader = new CsvLoaderDialog(file, projectCrs, projectManager, mapContent, layersPanel, this);
            csvLoader.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Cannot read a file", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openCrsDialog() {
        CrsDialog crsDialog = new CrsDialog(this, projectCrs);
        crsDialog.setVisible(true);

        CoordinateReferenceSystem selected = crsDialog.getSelectedCrs();
        if (selected != null) {
            projectCrs = selected;

            if (projectManager != null) {
                projectManager.setProjectCrs(projectCrs);
            }

            System.out.println("Project CRS set to: " + projectCrs.getName());
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

//    String getProjectEntry() {
//        return String.format("CRS %s", viewport.getCoordinateReferenceSystem().getName().toString());
//    }

    private void setCrsPanel() {

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
        viewport = new MapViewport();
        viewport.setFixedBoundsOnResize(true);

        // DISPLAY CRS IS FIXED
        try {
            displayCrs = CRS.decode("EPSG:3857", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        projectCrs = DefaultGeographicCRS.WGS84;

        viewport.setCoordinateReferenceSystem(displayCrs);
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
            ViewportUtils.clampToWebMercator(mapContent.getViewport());
        });

        JMapPane mapPane = new JMapPane();
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().setVisible(true));
    }
}