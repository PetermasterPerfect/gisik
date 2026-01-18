package org.gisik;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;

/**
 * Utility class for operations on geometries, such as distance calculations between features.
 */
public class GeometryUtils {

    /**
     * Computes the distance between the geometries of two {@link SimpleFeature} objects.
     * <p>
     * The method will:
     * <ul>
     *     <li>Check that both features have valid geometries.</li>
     *     <li>Reproject geometries to a common CRS if their CRS differs.</li>
     *     <li>Transform the geometries to a metric CRS (EPSG:2180) for distance calculation.</li>
     * </ul>
     *
     * @param f1 the first feature
     * @param f2 the second feature
     * @return the distance between the two feature geometries in the metric CRS units (meters)
     * @throws Exception if geometries cannot be transformed or features have no geometries
     * @throws IllegalArgumentException if one of the features has no geometry
     */
    public static double distanceBetween(
            SimpleFeature f1,
            SimpleFeature f2
    ) throws Exception {

        Geometry g1 = (Geometry) f1.getDefaultGeometry();
        Geometry g2 = (Geometry) f2.getDefaultGeometry();

        if (g1 == null || g2 == null) {
            throw new IllegalArgumentException("Feature nie ma geometrii");
        }

        CoordinateReferenceSystem crs1 =
                f1.getFeatureType().getCoordinateReferenceSystem();
        CoordinateReferenceSystem crs2 =
                f2.getFeatureType().getCoordinateReferenceSystem();
        
        if (crs1 != null && crs2 != null &&
                !CRS.equalsIgnoreMetadata(crs1, crs2)) {

            MathTransform toCrs1 =
                    CRS.findMathTransform(crs2, crs1, true);
            g2 = JTS.transform(g2, toCrs1);
        }

        CoordinateReferenceSystem metricCRS =
                CRS.decode("EPSG:2180", true);

        MathTransform toMetric =
                CRS.findMathTransform(crs1, metricCRS, true);

        g1 = JTS.transform(g1, toMetric);
        g2 = JTS.transform(g2, toMetric);

        System.out.println("Distance: "+g1.distance(g2) +" meters");
        return g1.distance(g2);
    }
}