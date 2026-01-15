package org.gisik.layersextra;

import org.geotools.api.style.*;
import org.geotools.feature.FeatureCollection;

public class FeatureLayerFromCsv extends FeatureLayerProject {
    private final String crsName;
    private final String lonColumn;
    private final String latColumn;
    private final String sep;
    private final boolean firstRow;
    public FeatureLayerFromCsv(FeatureCollection featureSource, Style style, String path, String sep, String crsName, String lonColumn, String latColumn, boolean firstRow) {
        super(featureSource, style, path);
        this.crsName = crsName;
        this.sep = sep;
        this.lonColumn = lonColumn;
        this.latColumn =  latColumn;
        this.firstRow =  firstRow;
    }

    public String getProjectEntry() {
        return String.format("CSV \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"\n", pathToData, sep, crsName, lonColumn, latColumn, firstRow);
    }
}
// String.format("CRS \"%s\"\n", crs);
// String.format("CSV \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"\n", pathToData,  crsName, lonColumn, latColumn, firstRow);
// String.format("SHAPE \"%s\"\n", pathToData);
// String.format("OSM\n");