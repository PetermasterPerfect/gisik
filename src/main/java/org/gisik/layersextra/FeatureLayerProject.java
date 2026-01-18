package org.gisik.layersextra;
import org.geotools.api.data.FeatureSource;
import org.geotools.api.style.Style;
import org.geotools.feature.FeatureCollection;
import org.geotools.map.FeatureLayer;

/**
 * Abstract base class for project-aware feature layers.
 * <p>
 * Extends {@link FeatureLayer} to include the path to the original data source
 * and a method to serialize the layer for saving in a project file.
 * Subclasses must implement {@link #getProjectEntry()} to define how the layer
 * is represented in the project file.
 */
public abstract class FeatureLayerProject extends FeatureLayer {
    /** Path to the original data file (shapefile, CSV, etc.) used to create this layer. */
    protected final String pathToData;

    /**
     * Constructs a feature layer from a {@link FeatureSource}.
     *
     * @param featureSource the feature source containing geometries for the layer
     * @param style         the style to apply to the features
     * @param pathToData    the path to the original data file
     */
    protected FeatureLayerProject(
            FeatureSource featureSource,
            Style style,
            String pathToData
    ) {
        super(featureSource, style);
        this.pathToData = pathToData;
    }

    /**
     * Constructs a feature layer from a {@link FeatureCollection}.
     *
     * @param featureSource the feature collection containing geometries for the layer
     * @param style         the style to apply to the features
     * @param pathToData    the path to the original data file
     */
    protected FeatureLayerProject(
            FeatureCollection featureSource,
            Style style,
            String pathToData
    ) {
        super(featureSource, style);
        this.pathToData = pathToData;
    }

    /**
     * Returns a string representation of this layer suitable for saving in a project file.
     * <p>
     * Subclasses must implement this method to define how their layer is serialized
     * (e.g., CSV or SHAPE format).
     *
     * @return the project entry string for this layer
     */
    public abstract String getProjectEntry();
}
