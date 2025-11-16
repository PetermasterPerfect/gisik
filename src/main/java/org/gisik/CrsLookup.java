package org.gisik;

import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CrsLookup {
    static public String[] crs = {"WGS84", "EPSG2178"};
    static public CoordinateReferenceSystem find(String crsName) {
        if(crsName == "WGS84") {
            return DefaultGeographicCRS.WGS84;
        } else {
            return nameToCrs(crsName);
        }
    }

    static private CoordinateReferenceSystem nameToCrs(String crsName) {
        try {
            String wkt = new String(Files.readAllBytes(Paths.get(crsName)), StandardCharsets.UTF_8);
            CoordinateReferenceSystem example = CRS.parseWKT(wkt);
            String code = CRS.lookupIdentifier(example, true);
            return CRS.decode(code);
        }catch(IOException e) {
            System.err.println("Cannot read wtk file");
            return null;
        }catch(FactoryException e) {
            System.err.println("Cannot parse wtk file");
            return null;
        }
    }
}
