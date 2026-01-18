package org.gisik.crs;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.map.MapViewport;
import org.gisik.DialogBase;
import org.gisik.WidgetAndPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for selecting and setting the Coordinate Reference System (CRS) of a map viewport.
 * This dialog presents a dropdown list of available CRS codes and allows
 * the user to set the selected CRS for the given MapViewport.
 */
public class CrsDialog extends DialogBase {
    /** Button to apply the selected CRS */
    private final JButton buttonSet;

    /** Combo box containing available CRS options */
    private final JComboBox<String> comboCrs;

    /** The map viewport whose CRS will be updated */
    private final MapViewport mapViewport;

    /**
     * Constructs a CRS selection dialog.
     *
     * @param mapViewport the MapViewport to update with the chosen CRS
     * @param parent      the parent JFrame for this dialog
     */
    public CrsDialog(MapViewport mapViewport, JFrame parent) {
        super(parent, "Set CRS");
        setSize(200, 200);
        setLocationRelativeTo(null);
        this.mapViewport = mapViewport;
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridy = 0;
        WidgetAndPanel<JComboBox<String>> buf;
        buf = createLabelCombo("Choose CRS: ", CrsLookup.crs);
        comboCrs = buf.widget;
        panel.add(buf.panel, gbc);
        gbc.gridy = 1;
        buttonSet = new JButton("Set");
        panel.add(buttonSet, gbc);
        buttonSet.addActionListener(e -> setCrsAction());
        getContentPane().add(panel);
    }


    /**
     * Handles the action when the "Set" button is pressed.
     * Reads the selected CRS from the combo box and applies it to the
     * associated MapViewport. If the selected CRS is invalid or
     * incompatible, an error is printed to the console.
     */
    private void setCrsAction() {
        try {
            CoordinateReferenceSystem crs = CrsLookup.find(comboCrs.getSelectedItem().toString());
            mapViewport.setCoordinateReferenceSystem(crs);
        }
        catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "Chosen crs isnt compatible with layers crs", "Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(e.getMessage());
        }
        dispose();
    }
}
