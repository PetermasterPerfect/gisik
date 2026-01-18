package org.gisik.layersextra;

import org.geotools.api.style.*;
import org.geotools.feature.FeatureCollection;

/**
 * Represents a CSV-based feature layer for use in a project.
 * <p>
 * Extends {@link FeatureLayerProject} by storing additional CSV-specific metadata,
 * such as the coordinate reference system, separator, longitude/latitude columns, and
 * whether the first row contains actual data.
 */
public class FeatureLayerFromCsv extends FeatureLayerProject {
    private final String crsName;
    private final String lonColumn;
    private final String latColumn;
    private final String sep;
    private final boolean firstRow;

    /**
     * Constructs a CSV-based feature layer.
     *
     * @param featureSource the FeatureCollection containing the geometries
     * @param style         the style to apply to the features
     * @param path          the path to the original CSV file
     * @param sep           the column separator used in the CSV file
     * @param crsName       the coordinate reference system of the CSV data
     * @param lonColumn     the name of the longitude column
     * @param latColumn     the name of the latitude column
     * @param firstRow      whether the first row contains actual data (true) or headers (false)
     */
    public FeatureLayerFromCsv(FeatureCollection featureSource, Style style, String path, String sep, String crsName, String lonColumn, String latColumn, boolean firstRow) {
        super(featureSource, style, path);
        this.crsName = crsName;
        this.sep = sep;
        this.lonColumn = lonColumn;
        this.latColumn =  latColumn;
        this.firstRow =  firstRow;
    }

    /**
     * Returns a string representation of this CSV layer suitable for storing in a project file.
     * <p>
     * The format is:
     * <pre>
     * CSV "path" "separator" "crsName" "lonColumn" "latColumn" "firstRow"
     * </pre>
     *
     * @return the project entry string for this CSV layer
     */
    public String getProjectEntry() {
        return String.format("CSV \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" \"%s\"\n", pathToData, sep, crsName, lonColumn, latColumn, firstRow);
    }
}
