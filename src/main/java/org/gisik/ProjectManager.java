package org.gisik;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.map.MapViewport;
import org.gisik.crs.CrsLookup;
import org.gisik.csv.CsvShadowLoader;
import org.gisik.layersextra.FeatureLayerProject;
import org.gisik.layersextra.TileLayerEx;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

/**
 * Manages project persistence and restoration for the GIS application.
 *
 * This class is responsible for:
 * - Saving the current map state (CRS and layers) into a project file
 * - Loading a project file and reconstructing all layers and settings
 *
 * The project file format is line-based and supports the following entries:
 * <ul>
 *   <li>CRS — defines the coordinate reference system for the project</li>
 *   <li>OSM — adds an OpenStreetMap tile layer</li>
 *   <li>SHAPE — loads a vector layer from a shapefile</li>
 *   <li>CSV — loads a point layer generated from a CSV file</li>
 * </ul>
 *
 * This class acts as a bridge between the user interface, the map model
 * ({@link org.geotools.map.MapContent}), and the project file format.
 */
public class ProjectManager {
    final private MapContent mapContent;
    final private MapViewport mapViewport;
    final LayersPanel  layersPanel;
    private String filePath;

    /**
     * Creates a new project manager bound to the given map and layers panel.
     *
     * @param mapContent  the map model that stores all active layers
     * @param layersPanel the UI panel used to visualize and control layers
     * @param filePath   the file path where the project will be saved or loaded from
     */
    public  ProjectManager(MapContent mapContent, LayersPanel layersPanel, String filePath) {
        this.mapContent = mapContent;
        mapViewport = this.mapContent.getViewport();
        this.filePath = filePath;
        this.layersPanel = layersPanel;
    }

    /**
     * Updates the file path used for saving and loading the project.
     *
     * This method is typically used when performing a "Save As" operation.
     *
     * @param filePath the new absolute path to the project file
     */
    void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Saves the current project state to disk.
     *
     * The method writes:
     * <ul>
     *   <li>The current CRS of the map viewport</li>
     *   <li>All active layers in the order they appear in the map</li>
     * </ul>
     *
     * Each layer is serialized into a single text line using
     * its {@code getProjectEntry()} representation.
     *
     * Supported layer types:
     * <ul>
     *   <li>OSM tile layers</li>
     *   <li>Shapefile-based feature layers</li>
     *   <li>CSV-generated feature layers</li>
     * </ul>
     *
     * Displays an error dialog if the file cannot be written.
     */
    void saveProject() {
        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(this.filePath), "utf-8"));
            writer.write(String.format("CRS \"%s\"\n", mapViewport.getCoordinateReferenceSystem().getName().toString()));
            for(Layer layer : this.mapContent.layers()) {
                if (layer instanceof TileLayerEx tileLayer) {
                    writer.write(tileLayer.getProjectEntry());

                } else if (layer instanceof FeatureLayerProject projectLayer) {
                    writer.write(projectLayer.getProjectEntry());
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Cannot save project", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            try {writer.close();} catch (Exception ex) {/*ignore*/}
        }
    }

    /**
     * Loads a project from disk and reconstructs the map state.
     *
     * The method reads the project file line by line and:
     * <ul>
     *   <li>Restores the map CRS</li>
     *   <li>Recreates all layers in the original order</li>
     * </ul>
     *
     * Each line is parsed using {@link ProjectLineParser} and dispatched
     * to the appropriate loader based on its entry type.
     *
     * Supported entries:
     * <ul>
     *   <li>CRS — sets the viewport coordinate reference system</li>
     *   <li>OSM — adds an OpenStreetMap tile layer</li>
     *   <li>SHAPE — loads a vector layer from a shapefile</li>
     *   <li>CSV — loads a CSV-based feature layer using {@link CsvShadowLoader}</li>
     * </ul>
     *
     * Displays an error dialog if the project file is malformed
     * or cannot be read.
     */
    void openProject() {
        try {
            final List<String> lines = Files.readAllLines(Paths.get(this.filePath));
            for(String line : lines) {
                List<String> entry = ProjectLineParser.parseLine(line);
                if(entry.size() == 0) {
                    throw new IllegalArgumentException("Empty line");
                }

                if(Objects.equals(entry.get(0), "CRS")) {
                    if(entry.size() == 2) {
                        CoordinateReferenceSystem crs = CrsLookup.find(entry.get(1));
                        mapViewport.setCoordinateReferenceSystem(crs);
                    } else {
                        throw new IllegalArgumentException("Bad paramater for an CRS entry");
                    }
                } else if(Objects.equals(entry.get(0), "OSM")) {
                    LayerManager.addOSM(mapContent, layersPanel);
                } else if(Objects.equals(entry.get(0), "SHAPE")) {
                    if(entry.size() == 2) {
                        LayerManager.addShape(new File(entry.get(1)), mapContent, layersPanel);
                    } else {
                        throw new IllegalArgumentException("Bad paramater for an SHAPE entry");
                    }
                } else if(Objects.equals(entry.get(0), "CSV")) {
                    if (entry.size() == 7) {
                        CsvShadowLoader csvLoader = new CsvShadowLoader(new File(entry.get(1)), entry.get(2), entry.get(3),
                                entry.get(4), entry.get(5), entry.get(6));
                        csvLoader.load(layersPanel, mapContent);
                    } else {
                        throw new IllegalArgumentException("Bad paramater for an CSV entry");
                    }
                }

            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Cannot open the project", "Error", JOptionPane.ERROR_MESSAGE);
        } catch(IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(null, "Cannot open the project: " + ex.toString(), "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

}
