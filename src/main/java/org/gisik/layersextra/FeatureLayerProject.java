package org.gisik.layersextra;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.Style;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;

public abstract class FeatureLayerProject extends FeatureLayer {

    protected final String pathToData;
    public CoordinateReferenceSystem projectCrs;
    protected CoordinateReferenceSystem nativeCrs;

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

    public CoordinateReferenceSystem getProjectCrs() {
        return projectCrs;
    }

    public CoordinateReferenceSystem getNativeCrs() {
        return nativeCrs;
    }

    public abstract Layer createRenderLayer(
            CoordinateReferenceSystem displayCrs
    ) throws Exception;

}
