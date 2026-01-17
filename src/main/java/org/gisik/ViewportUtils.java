package org.gisik;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;
import org.geotools.referencing.crs.DefaultGeographicCRS;

public class ViewportUtils {

    private static final double MAX = 20037508.34;

    public static void clampToWebMercator(MapViewport viewport) {
        ReferencedEnvelope env = viewport.getBounds();
        if (env == null) return;

        double minX = Math.max(env.getMinX(), -MAX);
        double maxX = Math.min(env.getMaxX(),  MAX);
        double minY = Math.max(env.getMinY(), -MAX);
        double maxY = Math.min(env.getMaxY(),  MAX);

        ReferencedEnvelope clamped =
                new ReferencedEnvelope(
                        minX, maxX,
                        minY, maxY,
                        env.getCoordinateReferenceSystem()
                );

        viewport.setBounds(clamped);
    }
}
