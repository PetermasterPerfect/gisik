package org.gisik.layersextra;

import org.geotools.tile.TileService;
import org.geotools.tile.util.AsyncTileLayer;

public class TileLayerEx extends AsyncTileLayer {

    public TileLayerEx(TileService service) {
        super(service);
        this.setTitle("OSM Map");
    }

    public String getProjectEntry() {
        return String.format("OSM\n");
    }

}
