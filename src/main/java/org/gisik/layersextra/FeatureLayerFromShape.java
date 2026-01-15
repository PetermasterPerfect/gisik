package org.gisik.layersextra;

import org.geotools.api.data.FeatureSource;
import org.geotools.api.style.*;

public class FeatureLayerFromShape extends FeatureLayerProject {
    public FeatureLayerFromShape(FeatureSource featureSource, Style style, String path) {
        super(featureSource, style, path);
    }

    public String getProjectEntry() {
        return String.format("SHAPE \"%s\"\n", pathToData);
    }
}
