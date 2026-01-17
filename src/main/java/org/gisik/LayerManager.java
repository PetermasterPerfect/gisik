package org.gisik;

import org.geotools.api.data.FileDataStore;
import org.geotools.api.data.FileDataStoreFinder;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.Style;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.tile.TileService;
import org.geotools.tile.impl.osm.OSMService;
import org.gisik.crs.CrsUtils;
import org.gisik.layersextra.FeatureLayerFromShape;
import org.gisik.layersextra.FeatureLayerProject;
import org.gisik.layersextra.TileLayerEx;
import org.gisik.layersview.LineIcon;
import org.gisik.layersview.PointIcon;
import org.gisik.layersview.SquareIcon;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class LayerManager {

    static public FeatureLayerProject loadShape(
            File file,
            CoordinateReferenceSystem projectCrs
    ) throws Exception {

        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        if (store == null)
            throw new IOException("Invalid vector file.");

        SimpleFeatureSource featureSource = store.getFeatureSource();
        SimpleFeatureType schema = featureSource.getSchema();

        CoordinateReferenceSystem nativeCrs =
                schema.getCoordinateReferenceSystem();

        SimpleFeatureCollection nativeFeatures =
                featureSource.getFeatures();

        SimpleFeatureCollection projectFeatures =
                CrsUtils.reproject(
                        nativeFeatures,
                        nativeCrs,
                        projectCrs
                );

        Color color = ColorStyle.randomColor();
        Style style = ColorStyle.createStyle2(schema, color);

        FeatureLayerFromShape layer =
                new FeatureLayerFromShape(
                        projectFeatures,
                        style,
                        file.getAbsolutePath(),
                        nativeCrs,
                        projectCrs
                );

        layer.setTitle(schema.getName().toString());

        store.dispose();
        return layer;
    }


    static public void addOSM(MapContent mapContent, LayersPanel layersPanel) {
        List<String> createdLayers = mapContent.layers()
                .stream()
                .map(Layer::getTitle)
                .toList();

        if (!createdLayers.contains("OSM Map")) {

            String baseURL = "https://tile.openstreetmap.org/";
            TileService service = new OSMService("OSM", baseURL);
            TileLayerEx tileLayer = new TileLayerEx(service);

            tileLayer.setTitle("OSM Map");

            mapContent.addLayer(tileLayer);
            layersPanel.add("OSM Map", null, true);
            layersPanel.revalidate();
            layersPanel.repaint();
        }
    }

}
