package org.gisik.layersextra;

import org.geotools.api.data.FeatureSource;
import org.geotools.api.style.*;

/**
 * Represents a shapefile-based feature layer for use in a project.
 * <p>
 * Extends {@link FeatureLayerProject} to handle shapefile-specific data.
 * Stores the path to the shapefile and allows exporting a project entry string.
 */
public class FeatureLayerFromShape extends FeatureLayerProject {

    /**
     * Constructs a shapefile-based feature layer.
     *
     * @param featureSource the feature source containing geometries from the shapefile
     * @param style         the style to apply to the features
     * @param path          the path to the original shapefile
     */
    public FeatureLayerFromShape(FeatureSource featureSource, Style style, String path) {
        super(featureSource, style, path);
    }

    /**
     * Returns a string representation of this shapefile layer suitable for storing in a project file.
     * <p>
     * The format is:
     * <pre>
     * SHAPE "path"
     * </pre>
     *
     * @return the project entry string for this shapefile layer
     */
    public String getProjectEntry() {
        return String.format("SHAPE \"%s\"\n", pathToData);
    }
}
