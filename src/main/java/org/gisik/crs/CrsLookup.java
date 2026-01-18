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

/**
 * Utility class for looking up Coordinate Reference Systems (CRS) by name.
 * Provides a predefined list of CRS options and methods to retrieve
 * a CoordinateReferenceSystem object from a string name or WKT file.
 */
public class CrsLookup {
    /** List of CRS names available in the application */
    static public String[] crs = {"WGS84(DD)", "GCS_WGS_1984", "EPSG2178", "EPSG2180"};

    /**
     * Finds a CoordinateReferenceSystem corresponding to the given name.
     * Supports default CRS names like "WGS84(DD)" or "GCS_WGS_1984" and
     * also custom CRS loaded from WKT files for other names.
     *
     * @param crsName the name of the CRS
     * @return the corresponding CoordinateReferenceSystem, or null if loading fails
     */
    static public CoordinateReferenceSystem find(String crsName) {
        if(Objects.equals(crsName, "WGS84(DD)") || Objects.equals(crsName, "GCS_WGS_1984")) {
            return DefaultGeographicCRS.WGS84;
        } else {
            return nameToCrs(crsName);
        }
    }

    /**
     * Loads a CoordinateReferenceSystem from a WKT file based on the given name.
     * The method reads the WKT file from the classpath and parses it into a CRS object.
     *
     * @param crsName the name of the CRS file to load
     * @return the CoordinateReferenceSystem, or null if an error occurs
     */
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
