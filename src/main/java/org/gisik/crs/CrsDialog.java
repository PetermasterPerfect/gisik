package org.gisik.crs;

import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.map.MapViewport;
import org.gisik.DialogBase;
import org.gisik.WidgetAndPanel;

import javax.swing.*;
import java.awt.*;

public class CrsDialog extends DialogBase {
    private final JButton buttonSet;
    private final JComboBox<String> comboCrs;
    private CoordinateReferenceSystem selectedCrs;

    public CrsDialog(JFrame parent, CoordinateReferenceSystem currentCrs) {
        super(parent, "Set CRS");
        setSize(200, 200);
        setLocationRelativeTo(null);
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

    private void setCrsAction() {
        try {
            selectedCrs = CrsLookup.find(
                    comboCrs.getSelectedItem().toString()
            );
        } catch (Exception e) {
            System.err.println(e.getMessage());
            selectedCrs = null;
        }
        dispose();
    }

    public CoordinateReferenceSystem getSelectedCrs() {
        return selectedCrs;
    }
}
