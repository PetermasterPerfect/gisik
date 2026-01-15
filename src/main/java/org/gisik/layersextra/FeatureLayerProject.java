package org.gisik.layersextra;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.style.Style;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;

public abstract class FeatureLayerProject extends FeatureLayer {

    protected final String pathToData;

    protected FeatureLayerProject(
            FeatureSource featureSource,
            Style style,
            String pathToData
    ) {
        super(featureSource, style);
        this.pathToData = pathToData;
    }

    protected FeatureLayerProject(
            FeatureCollection featureSource,
            Style style,
            String pathToData
    ) {
        super(featureSource, style);
        this.pathToData = pathToData;
    }


    public abstract String getProjectEntry();
}
