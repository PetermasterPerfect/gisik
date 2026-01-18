package org.gisik;

import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.style.Style;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.TileService;
import org.geotools.tile.impl.osm.OSMService;
import org.gisik.layersextra.FeatureLayerFromShape;
import org.gisik.layersextra.TileLayerEx;
import org.gisik.layersview.LineIcon;
import org.gisik.layersview.PointIcon;
import org.gisik.layersview.SquareIcon;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for managing layers in a {@link MapContent}, including adding
 * vector layers (shapefiles) and base map layers (OSM).
 */
public class LayerManager {

    /**
     * Adds a shapefile (.shp) as a layer to the given {@link MapContent} and
     * updates the {@link LayersPanel} accordingly.
     * <p>
     * The method automatically assigns a random color to the layer and chooses
     * an appropriate icon for the geometry type (Point, Line, Polygon).
     *
     * @param file        the shapefile to load
     * @param mapContent  the MapContent to which the layer will be added
     * @param layersPanel the UI panel displaying layers
     */
    static public void addShape(File file, MapContent mapContent, LayersPanel layersPanel) {
        try {
            FileDataStore store = FileDataStoreFinder.getDataStore(file);
            if(store == null)
                throw new IOException("Invalid vector file.");
            System.out.println(file.getName());
            SimpleFeatureSource featureSource = store.getFeatureSource();
            Color color = ColorStyle.randomColor();
            Style style = ColorStyle.createStyle2(featureSource.getSchema(), color);
            Layer layer = new FeatureLayerFromShape(featureSource, style, file.getAbsolutePath());
            SimpleFeatureType schema = featureSource.getSchema();
            layer.setTitle(schema.getName().toString());
            mapContent.addLayer(layer);
            GeometryDescriptor geomDesc = schema.getGeometryDescriptor();
            Class<?> geomBinding = geomDesc.getType().getBinding();

            String geomType = geomBinding.getSimpleName();
            if (geomType.contains("Point")) {
                layersPanel.add(schema.getName().toString(), new PointIcon(color), true);
            } else if (geomType.contains("Line")) {
                layersPanel.add(schema.getName().toString(), new LineIcon(color), true);
            } else if (geomType.contains("Polygon")) {
                layersPanel.add(schema.getName().toString(), new SquareIcon(color), true);
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

    /**
     * Adds an OpenStreetMap (OSM) base layer to the given {@link MapContent} and
     * updates the {@link LayersPanel}.
     * <p>
     * The OSM layer is only added if it does not already exist in the MapContent
     * and if the map is in the WGS84 coordinate reference system or has no CRS yet.
     *
     * @param mapContent  the MapContent to which the OSM layer will be added
     * @param layersPanel the UI panel displaying layers
     */
    static public void addOSM(MapContent mapContent, LayersPanel layersPanel) {
        List<String> createdLayers =  mapContent.layers().stream().map(Layer::getTitle).toList() ;

        if(!createdLayers.contains("OSM Map") &&
                (CRS.isEquivalent(DefaultGeographicCRS.WGS84, mapContent.getCoordinateReferenceSystem()) ||
                        mapContent.getCoordinateReferenceSystem() == null
                )){

            String baseURL = "https://tile.openstreetmap.org/";
            TileService service = new OSMService("OSM", baseURL);
            TileLayerEx tileLayer = new TileLayerEx(service);

            mapContent.addLayer(tileLayer);
            layersPanel.add("OSM Map", null, true);
            layersPanel.revalidate();
            layersPanel.repaint();
        }
    }


}
