package org.gisik.layersextra;

import org.geotools.tile.TileService;
import org.geotools.tile.util.AsyncTileLayer;
/**
 * Extended tile layer for use in projects.
 * <p>
 * Wraps a {@link AsyncTileLayer} and provides a project entry for saving
 * the base map (currently configured for OSM).
*/
public class TileLayerEx extends AsyncTileLayer {

    /**
     * Constructs a tile layer from the given {@link TileService}.
     * <p>
     * Sets the layer title to "OSM Map" by default.
     *
     * @param service the tile service used to fetch tiles
     */
    public TileLayerEx(TileService service) {
        super(service);
        this.setTitle("OSM Map");
    }

    /**
     * Returns a string representation of this tile layer suitable for saving
     * in a project file.
     *
     * @return the project entry string for this tile layer
     */
    public String getProjectEntry() {
        return String.format("OSM\n");
    }

}
