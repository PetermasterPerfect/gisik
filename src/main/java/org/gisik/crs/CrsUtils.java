package org.gisik.crs;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.store.ReprojectingFeatureCollection;

public class CrsUtils {

    public static SimpleFeatureCollection reproject(
            SimpleFeatureCollection collection,
            CoordinateReferenceSystem sourceCRS,
            CoordinateReferenceSystem targetCRS
    ) {
        if (targetCRS == null) {
            return collection;
        }

        return new ReprojectingFeatureCollection(collection, targetCRS);
    }
}