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

public class ProjectManager {
    final private MapContent mapContent;
    final private MapViewport mapViewport;
    final LayersPanel  layersPanel;
    private String filePath;

    public  ProjectManager(MapContent mapContent, LayersPanel layersPanel, String filePath) {
        this.mapContent = mapContent;
        mapViewport = this.mapContent.getViewport();
        this.filePath = filePath;
        this.layersPanel = layersPanel;
    }

    void setFilePath(String filePath) {
        this.filePath = filePath;
    }

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
