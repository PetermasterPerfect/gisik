package org.gisik.csv;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.Style;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.MapContent;
import org.gisik.ColorStyle;
import org.gisik.LayersPanel;
import org.gisik.crs.CrsLookup;
import org.gisik.layersextra.FeatureLayerFromCsv;
import org.gisik.layersview.PointIcon;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for loading CSV data into a MapContent and LayersPanel without displaying a dialog.
 * <p>
 * Parses the CSV file, creates point features for the specified longitude and latitude columns,
 * styles them with a random color, and adds them as a FeatureLayerFromCsv to the map and UI.
 */
public class CsvShadowLoader {
    private final CsvParser parser;
    private final String sep;
    private final String layerName;
    private final String crsName;
    private final String lonColumn;
    private final String latColumn;
    private final String absPath;
    private final boolean firstRow;

    /**
     * Constructs a CsvShadowLoader for the specified CSV file and settings.
     *
     * @param csvFile   the CSV file to load
     * @param sep       the column separator (comma, semicolon, tab, etc.)
     * @param crsName   the name of the coordinate reference system (CRS) to use
     * @param lonColumn the column name containing longitude values
     * @param latColumn the column name containing latitude values
     * @param firstRow  whether the first row contains data (true) or headers (false)
     * @throws IOException if the CSV file cannot be read
     */
    public CsvShadowLoader(File csvFile, String sep, String crsName, String lonColumn, String latColumn, String firstRow) throws IOException {
        parser = new CsvParser(csvFile, CsvParser.comboTextToChar(sep));
        this.crsName = crsName;
        this.absPath = csvFile.getAbsolutePath();
        this.layerName = csvFile.getName();
        this.lonColumn = lonColumn;
        this.latColumn =  latColumn;
        this.firstRow =  Boolean.parseBoolean(firstRow);
        this.sep = sep;
    }

    /**
     * Loads the CSV data into the specified LayersPanel and MapContent.
     * <p>
     * Creates point features for each row, builds a SimpleFeatureCollection,
     * applies a random color style, creates a FeatureLayerFromCsv, and adds it
     * to both the map content and the UI layer panel.
     *
     * @param layersPanel the UI panel to add the layer to
     * @param mapContent  the MapContent to display the features
     * @throws IOException if there is an error reading the CSV data
     */
    public void load(LayersPanel layersPanel, MapContent mapContent) throws IOException {
        CoordinateReferenceSystem crs = CrsLookup.find(this.crsName);
        if(crs == null) {
            JOptionPane.showMessageDialog(null, "Error when reading crs", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
        builder.setName(layerName);
        builder.setCRS(crs);
        builder.add("the_geom", Point.class);
        List<Double> longs = parser.parseColumnByName(this.lonColumn, this.firstRow);
        List<Double> lats = parser.parseColumnByName(this.latColumn, this.firstRow);

        GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
        SimpleFeatureType featureType = builder.buildFeatureType();
        SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
        List<SimpleFeature> features = new ArrayList<>();
        for(int i=0; i<longs.size(); i++) {
            if(longs.get(i) == null || lats.get(i) == null) {
                continue;
            }
            Point point = geometryFactory.createPoint(new Coordinate(longs.get(i), lats.get(i)));
            featureBuilder.add(point);
            SimpleFeature feature = featureBuilder.buildFeature(null);
            features.add(feature);
        }

        //JSimpleStyleDialog.showDialog(null, featureType);
        SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
        Color color = ColorStyle.randomColor();
        Style style = ColorStyle.createStyle2(featureType, color);
        FeatureLayerFromCsv layer = new  FeatureLayerFromCsv(collection, style, this.absPath, this.sep, this.crsName,
            this.lonColumn, this.latColumn, this.firstRow);
        layer.setTitle(layerName);
        layersPanel.add(layerName, new PointIcon(color), true);
        mapContent.addLayer(layer);
    }
}
