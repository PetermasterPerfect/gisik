package org.gisik;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.Layer;
import org.geotools.map.MapViewport;
import org.geotools.referencing.CRS;

public class LayerZoomUtils {

    public static void zoomToLayer(
            Layer layer,
            MapViewport viewport
    ) {
        try {
            ReferencedEnvelope bounds = layer.getBounds();
            if (bounds == null || bounds.isEmpty()) {
                return;
            }

            if (!CRS.equalsIgnoreMetadata(
                    bounds.getCoordinateReferenceSystem(),
                    viewport.getCoordinateReferenceSystem()
            )) {
                bounds = bounds.transform(
                        viewport.getCoordinateReferenceSystem(),
                        true
                );
            }

            double padX = bounds.getWidth() * 0.1;
            double padY = bounds.getHeight() * 0.1;
            bounds.expandBy(padX, padY);

            viewport.setBounds(bounds);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
