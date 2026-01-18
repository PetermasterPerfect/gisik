package org.gisik;

import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.type.GeometryDescriptor;
import org.geotools.api.filter.Filter;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.filter.identity.FeatureId;
import org.geotools.api.style.*;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.geotools.map.MapContent;
import org.geotools.swing.MapPane;
import org.geotools.swing.event.MapMouseEvent;
import org.geotools.swing.tool.CursorTool;
import org.gisik.layersextra.TileLayerEx;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;

import java.awt.*;
import org.geotools.api.style.Stroke;

import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Cursor tool responsible for interactive feature selection and distance measurement
 * on the map view.
 * This tool allows the user to click on vector layers to select up to two spatial
 * features at a time. Selected features are highlighted visually and the distance
 * between two selected features is automatically calculated and displayed.
 * The tool supports point, line, and polygon geometries and dynamically adapts
 * the selection style based on the geometry type of the active layer.
 * Tile-based layers (e.g. OSM base map) are ignored during selection.
 * Responsibilities:
 * - Detect mouse clicks on the map
 * - Identify spatial features intersecting the click area
 * - Maintain a list of selected features (maximum two)
 * - Highlight selected features using custom styles
 * - Calculate and display distance between two selected features
 */

class PointSelection extends CursorTool {
    /**
     * Enumeration describing supported geometry types for feature selection
     * and rendering.
     */
    private enum GeomType {
        POINT,
        LINE,
        POLYGON
    };

    private StyleFactory sf = CommonFactoryFinder.getStyleFactory();
    private FilterFactory ff = CommonFactoryFinder.getFilterFactory();
    private MapPane mapPane;
    private MapContent mapContent;
    private LayersPanel layersPanel;
    private String geometryAttributeName;
    private static final Color LINE_COLOUR = Color.BLACK;
    private static final Color SELECTED_COLOUR = Color.YELLOW;
    private static final float OPACITY = 1.0f;
    private static final float LINE_WIDTH = 2.0f;
    private static final float POINT_SIZE = 10.0f;
    private ArrayList<FeatureId> ids = new ArrayList<>();
    private ArrayList<SimpleFeature> features = new ArrayList<>();

    private GeomType geometryType;

    /**
     * Creates a new PointSelection tool bound to a map pane and layers panel.
     *
     * @param mapPane the map pane used to retrieve spatial transformations
     *                and map content
     * @param layersPanel the layers panel used to retrieve layer colors
     *                     and trigger UI repainting
     */
    PointSelection(MapPane mapPane, LayersPanel layersPanel) {
        this.layersPanel = layersPanel;
        this.mapPane = mapPane;
        this.mapContent = mapPane.getMapContent();
    }

    /**
     * Handles mouse click events on the map.
     *
     * Iterates through all map layers and performs feature selection
     * for each vector layer. Tile layers are ignored.
     *
     * @param ev the mouse click event containing screen and world coordinates
     */
    @Override
    public void onMouseClicked(MapMouseEvent ev) {
        for(Layer layer : mapContent.layers()) {
            if(layer instanceof TileLayerEx)
                continue;
            setGeometry(layer);
            selectFeatures(ev, layer );
        }
    }

    /**
     * Selects spatial features from a given layer based on a click location.
     *
     * Converts the screen click position into a small world-space bounding box
     * and queries the layer for features intersecting that area.
     *
     * Maintains a selection list of up to two features. If two features are
     * selected, the distance between them is calculated and displayed.
     *
     * @param ev the mouse event containing the click position
     * @param layer the map layer from which features should be selected
     */
    void selectFeatures(MapMouseEvent ev, Layer layer) {
        SimpleFeatureSource featureSource =  (SimpleFeatureSource) layer.getFeatureSource();

        Point screenPos = ev.getPoint();
        Rectangle screenRect = new Rectangle(screenPos.x, screenPos.y, 5, 5);

        AffineTransform screenToWorld = mapPane.getScreenToWorldTransform();
        Rectangle2D worldRect = screenToWorld.createTransformedShape(screenRect).getBounds2D();
        ReferencedEnvelope bbox =
                new ReferencedEnvelope(worldRect, mapContent.getCoordinateReferenceSystem());


        Filter filter = ff.intersects(ff.property(geometryAttributeName), ff.literal(bbox));
        try {
            SimpleFeatureCollection selectedFeatures = featureSource.getFeatures(filter);

            try (SimpleFeatureIterator iter = selectedFeatures.features()) {
                if (iter.hasNext()) {
                    SimpleFeature feature = iter.next();
                    if(ids.contains(feature.getIdentifier())) {
                        ids.remove(feature.getIdentifier());
                        features.remove(feature);
                    }
                    else if(ids.size() == 2) {
                        ids.remove(0);
                        features.remove(0);
                    } else {
                        ids.add(feature.getIdentifier());
                        features.add(feature);
                    }
                    System.out.println("   " + feature.getIdentifier());
                }
            }

            displaySelectedFeatures(ids, layer);

            if(features.size() == 2) {
                double dist = GeometryUtils.distanceBetween(features.get(0), features.get(1));
                JOptionPane.showMessageDialog(null, "Distance: "+dist, "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            System.err.println("Error selecting features: " + ex.getMessage());
        }
    }

    /**
     * Updates the visual style of a layer to reflect selected features.
     *
     * Selected features are highlighted using a special style, while
     * non-selected features retain their default appearance.
     *
     * @param IDs list of selected feature identifiers
     * @param layer the layer whose style should be updated
     */
    public void displaySelectedFeatures(ArrayList<FeatureId> IDs , Layer layer) {
        Style style;
        Color color = getBasicLayerColor(layer);
        if(IDs.isEmpty()) {
            style = createDefaultStyle(color);
        } else {
            style = createSelectedStyle(color, IDs);
        }

        ((FeatureLayer) layer).setStyle(style);
        layersPanel.repaint();
    }

    /**
     * Retrieves the base color associated with a given layer from the layers panel.
     *
     * @param layer the layer whose display color should be retrieved
     * @return the base color of the layer, or null if not found
     */
    private Color getBasicLayerColor(Layer layer) {
        Color color = null;
        int i=0;
        for(Layer layerIt : mapContent.layers()) {
            if(layer == layerIt) {
                color = layersPanel.getLayersColor(i);
            }
            i++;
        }
        return color;
    }

    /**
     * Creates a default rendering style for a layer using its base color.
     *
     * This style is applied when no features are currently selected.
     *
     * @param basicColor the base color of the layer
     * @return a default style for rendering the layer, or null if no color is provided
     */
    private Style createDefaultStyle(Color basicColor) {
        if(basicColor != null) {
            Rule rule = createRule(LINE_COLOUR, basicColor);

            FeatureTypeStyle fts = sf.createFeatureTypeStyle();
            fts.rules().add(rule);

            Style style = sf.createStyle();
            style.featureTypeStyles().add(fts);
            return style;
        }
        return null;
    }

    /**
     * Creates a style that highlights selected features while preserving
     * the default style for non-selected features.
     *
     * @param basicColor the base color of the layer
     * @param IDs the list of selected feature identifiers
     * @return a style with selection and default rendering rules
     */
    private Style createSelectedStyle(Color basicColor, ArrayList<FeatureId> IDs) {
        Rule selectedRule = createRule(SELECTED_COLOUR, SELECTED_COLOUR);
        selectedRule.setFilter(ff.id(new HashSet<>(IDs)));

        Rule otherRule = createRule(LINE_COLOUR, basicColor);
        otherRule.setElseFilter(true);

        FeatureTypeStyle fts = sf.createFeatureTypeStyle();
        fts.rules().add(selectedRule);
        fts.rules().add(otherRule);

        Style style = sf.createStyle();
        style.featureTypeStyles().add(fts);
        return style;
    }

    /**
     * Creates a rendering rule for a specific geometry type.
     *
     * The rule defines how features should be drawn based on outline color,
     * fill color, and geometry type (point, line, or polygon).
     *
     * @param outlineColor the color of feature outlines
     * @param fillColor the fill color used for point and polygon geometries
     * @return a rule defining how features should be rendered
     */
    private Rule createRule(Color outlineColor, Color fillColor) {
        Symbolizer symbolizer = null;
        Fill fill = null;
        Stroke stroke = sf.createStroke(ff.literal(outlineColor), ff.literal(LINE_WIDTH));

        switch (geometryType) {
            case POLYGON:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));
                symbolizer = sf.createPolygonSymbolizer(stroke, fill, geometryAttributeName);
                break;

            case LINE:
                symbolizer = sf.createLineSymbolizer(stroke, geometryAttributeName);
                break;

            case POINT:
                fill = sf.createFill(ff.literal(fillColor), ff.literal(OPACITY));

                Mark mark = sf.getCircleMark();
                mark.setFill(fill);
                mark.setStroke(stroke);

                Graphic graphic = sf.createDefaultGraphic();
                graphic.graphicalSymbols().clear();
                graphic.graphicalSymbols().add(mark);
                graphic.setSize(ff.literal(POINT_SIZE));

                symbolizer = sf.createPointSymbolizer(graphic, geometryAttributeName);
        }
        Rule rule = sf.createRule();
        rule.symbolizers().add(symbolizer);
        return rule;
    }

    /**
     * Detects and sets the geometry type and geometry attribute name
     * for the given layer.
     *
     * This information is used to correctly create symbolizers and
     * rendering rules for selection highlighting.
     *
     * @param layer the layer whose geometry type should be analyzed
     */
    private void setGeometry(Layer layer) {
        GeometryDescriptor geomDesc = ((SimpleFeatureSource) layer.getFeatureSource()).getSchema().getGeometryDescriptor();
        geometryAttributeName = geomDesc.getLocalName();

        Class<?> clazz = geomDesc.getType().getBinding();

        if (Polygon.class.isAssignableFrom(clazz) || MultiPolygon.class.isAssignableFrom(clazz)) {
            geometryType = GeomType.POLYGON;

        } else if (LineString.class.isAssignableFrom(clazz) || MultiLineString.class.isAssignableFrom(clazz)) {

            geometryType = GeomType.LINE;

        } else {
            geometryType = GeomType.POINT;
        }
    }
}