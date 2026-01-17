package org.gisik;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.MapViewport;

public class ViewportUtils {

    private static final double MAX = 20037508.34;

    public static void clampToWebMercator(MapViewport viewport) {
        ReferencedEnvelope env = viewport.getBounds();
        if (env == null) return;

        double minX = env.getMinX();
        double maxX = env.getMaxX();
        double minY = env.getMinY();
        double maxY = env.getMaxY();

        boolean needsClamp = false;

        if (minX < -MAX) {
            minX = -MAX;
            needsClamp = true;
        }
        if (maxX > MAX) {
            maxX = MAX;
            needsClamp = true;
        }
        if (minY < -MAX) {
            minY = -MAX;
            needsClamp = true;
        }
        if (maxY > MAX) {
            maxY = MAX;
            needsClamp = true;
        }

        if (!needsClamp) return;

        ReferencedEnvelope clamped =
                new ReferencedEnvelope(
                        minX, maxX,
                        minY, maxY,
                        env.getCoordinateReferenceSystem()
                );

        viewport.setBounds(clamped);
    }
}
