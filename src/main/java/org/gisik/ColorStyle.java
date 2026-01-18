package org.gisik;

import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.filter.FilterFactory;
import org.geotools.api.style.*;
import org.geotools.api.style.Stroke;
import org.geotools.factory.CommonFactoryFinder;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPolygon;

import java.awt.*;
import java.util.Random;

/**
 * Utility class for creating {@link Style} objects for GeoTools layers based on geometry type.
 * <p>
 * Supports point, line, and polygon geometries, with configurable colors. Also provides a method
 * to generate random colors for styling layers.
 */
public class ColorStyle {
    static StyleFactory styleFactory = CommonFactoryFinder.getStyleFactory();
    static FilterFactory filterFactory = CommonFactoryFinder.getFilterFactory();

    /**
     * Creates a {@link Style} for the given feature type and color.
     * Automatically selects the appropriate style based on geometry type (point, line, polygon).
     *
     * @param schema the feature type containing geometry information
     * @param color  the color to use for styling
     * @return a {@link Style} suitable for the feature type
     */
    static public Style createStyle2(SimpleFeatureType schema, Color color) {
        Class geomType = schema.getGeometryDescriptor().getType().getBinding();

        if (Polygon.class.isAssignableFrom(geomType) || MultiPolygon.class.isAssignableFrom(geomType)) {
            return createPolygonStyle(color);

        } else if (LineString.class.isAssignableFrom(geomType) || MultiLineString.class.isAssignableFrom(geomType)) {
            return createLineStyle(color);

        } else {
            return createPointStyle(color);
        }
    }

    /**
     * Creates a polygon style with the given color.
     *
     * @param color the fill color for the polygon
     * @return a {@link Style} representing the polygon style
     */
    static private Style createPolygonStyle(Color color) {
        org.geotools.api.style.Stroke stroke = styleFactory.createStroke(
                filterFactory.literal(Color.BLACK), filterFactory.literal(1), filterFactory.literal(0.5));

        Fill fill = styleFactory.createFill(filterFactory.literal(color), filterFactory.literal(0.5));
        PolygonSymbolizer sym = styleFactory.createPolygonSymbolizer(stroke, fill, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    /**
     * Creates a line style with the given color.
     *
     * @param color the color for the line
     * @return a {@link Style} representing the line style
     */
    static private Style createLineStyle(Color color) {
        Stroke stroke = styleFactory.createStroke(filterFactory.literal(color), filterFactory.literal(1));
        LineSymbolizer sym = styleFactory.createLineSymbolizer(stroke, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    /**
     * Creates a point style with the given color.
     *
     * @param color the color for the point
     * @return a {@link Style} representing the point style
     */
    static private Style createPointStyle(Color color) {
        Graphic gr = styleFactory.createDefaultGraphic();
        Mark mark = styleFactory.getCircleMark();
        mark.setStroke(styleFactory.createStroke(filterFactory.literal(Color.BLACK), filterFactory.literal(1)));

        mark.setFill(styleFactory.createFill(filterFactory.literal(color)));

        gr.graphicalSymbols().clear();
        gr.graphicalSymbols().add(mark);
        gr.setSize(filterFactory.literal(10));

        PointSymbolizer sym = styleFactory.createPointSymbolizer(gr, null);

        Rule rule = styleFactory.createRule();
        rule.symbolizers().add(sym);
        FeatureTypeStyle fts = styleFactory.createFeatureTypeStyle(rule);
        Style style = styleFactory.createStyle();
        style.featureTypeStyles().add(fts);

        return style;
    }

    /**
     * Generates a random {@link Color} with RGB values in the range 0.0 to 1.0.
     *
     * @return a randomly generated {@link Color}
     */
    static public Color randomColor() {
        Random rand = new Random();
        return new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
    }
}
