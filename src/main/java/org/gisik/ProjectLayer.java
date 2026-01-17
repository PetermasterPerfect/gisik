package org.gisik;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.Style;
import org.geotools.data.simple.SimpleFeatureCollection;

public class ProjectLayer {
    private String name;
    private SimpleFeatureCollection features; // always in PROJECT CRS
    private CoordinateReferenceSystem projectCrs;
    private Style style;
    private String sourcePath; // optional but very useful

    // getters / setters
}
