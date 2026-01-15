package org.gisik.crs;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class CrsLookup {
    static public String[] crs = {"WGS84(DD)", "EPSG2178", "EPSG2180"};
    static public CoordinateReferenceSystem find(String crsName) {
        if(Objects.equals(crsName, "WGS84(DD)")) {
            return DefaultGeographicCRS.WGS84;
        } else {
            return nameToCrs(crsName);
        }
    }

    static private CoordinateReferenceSystem nameToCrs(String crsName) {
        try {
            if(crsName != null) {
                String wkt = new String(Files.readAllBytes(Paths.get(CrsLookup.class.getResource(crsName).toURI())), StandardCharsets.UTF_8);
                return CRS.parseWKT(wkt);
            } else {
                throw new IllegalArgumentException("Bad paramater for an CSV entry");
            }
        }catch(IOException e) {
            System.err.println("Cannot read wtk file");
            return null;
        }catch(FactoryException e) {
            System.err.println("Cannot parse wtk file");
            return null;
        } catch (URISyntaxException e) {
            System.err.println("Cannot parse wtk file, uri syntax expression");
            return null;
        }
    }
}
