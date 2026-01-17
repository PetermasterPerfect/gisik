package org.gisik.layersextra;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.Style;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.map.FeatureLayer;
import org.geotools.map.Layer;
import org.gisik.crs.CrsUtils;

public class FeatureLayerFromCsvProject extends FeatureLayerProject {

    private final SimpleFeatureCollection features;
    private final CoordinateReferenceSystem nativeCrs;
    private final CoordinateReferenceSystem projectCrs;

    public FeatureLayerFromCsvProject(
            SimpleFeatureCollection features,
            Style style,
            String path,
            CoordinateReferenceSystem nativeCrs,
            CoordinateReferenceSystem projectCrs
    ) {
        super(features, style, path);
        this.features = features;
        this.nativeCrs = nativeCrs;
        this.projectCrs = projectCrs;
    }

    @Override
    public Layer createRenderLayer(
            CoordinateReferenceSystem displayCrs
    ) throws Exception {

        SimpleFeatureCollection displayFeatures =
                CrsUtils.reproject(
                        features,
                        projectCrs,
                        displayCrs
                );

        FeatureLayer layer =
                new FeatureLayer(displayFeatures, getStyle());
        layer.setTitle(getTitle());
        return layer;
    }

    @Override
    public String getProjectEntry() {
        return String.format(
                "CSV \"%s\" \"%s\"\n",
                pathToData,
                nativeCrs.getName().toString()
        );
    }

}
