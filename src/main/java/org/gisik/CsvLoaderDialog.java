package org.gisik;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.Style;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.FeatureLayer;
import org.geotools.map.MapContent;
import org.geotools.swing.styling.JSimpleStyleDialog;
import org.gisik.layerstree.PointIcon;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CsvLoaderDialog extends JDialog {

    static class WidgetAndPanel<T> {
        public T widget;
        public JPanel panel;
        WidgetAndPanel(T widget, JPanel panel) {
            this.widget = widget;
            this.panel  = panel;
        }
    }

    private final String name;
    private final CsvParser parser;
    JComboBox<String> comboSeparator;
    JComboBox<String> comboLongitude;
    JComboBox<String> comboLatitude;
    JCheckBox checkBoxFirstRow;
    JButton buttonAdd;
    JComboBox<String> comboCrs;
    String[] separtors = {",", ".", ";", "SPACE", "TAB"};
    MapContent mapContent;
    LayersPanel layersPanel;

    CsvLoaderDialog(File csvFile, MapContent mapContent, LayersPanel layersPanel, JFrame parent) throws IOException {
        super(parent, "Load csv file", true);
        setSize(600, 400);
        setLocationRelativeTo(null);
        this.mapContent = mapContent;
        this.layersPanel = layersPanel;
        name = csvFile.getName();
        parser = new CsvParser(csvFile, ',');

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridy = 0;
        WidgetAndPanel<JComboBox<String>> buf =  createLabelCombo("Separator:", separtors);
        comboSeparator = buf.widget;
        leftPanel.add(buf.panel, gbc);
        gbc.gridy = 1;
        buf = createLabelCombo("Longitude:", parser.getColumnNames());
        comboLongitude = buf.widget;
        leftPanel.add(buf.panel, gbc);
        gbc.gridy = 2;
        buf = createLabelCombo("Latitude: ", parser.getColumnNames());
        comboLatitude = buf.widget;
        leftPanel.add(buf.panel, gbc);
        gbc.gridy = 3;
        buf = createLabelCombo("CRS: ", CrsLookup.crs);
        comboCrs = buf.widget;
        leftPanel.add(buf.panel, gbc);
        WidgetAndPanel<JCheckBox> checkBuf = createCheckBox("First row as data: ");
        checkBoxFirstRow = checkBuf.widget;
        gbc.gridy = 4;
        leftPanel.add(checkBuf.panel, gbc);
        gbc.gridy = 5;
        buttonAdd = new JButton("Add");
        leftPanel.add(buttonAdd, gbc);

        JTextArea textArea = new JTextArea();
        JScrollPane textScroll = new JScrollPane(textArea);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, textScroll);

        textArea.setEditable(false);
        textArea.append(parser.getFileContent());
        getContentPane().add(split);
        setHandler();
    }

    private void setHandler() {
        ActionListener actionListener = e -> {
            Object src = e.getSource();
            if(src == comboSeparator) {
                char sep = comboTextToChar(comboSeparator.getSelectedItem().toString());
                parser.setSeparator(sep);
                DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>( parser.getColumnNames() );
                comboLongitude.setModel(model);
                comboLatitude.setModel(model);
            }else if(src == buttonAdd) {
                CoordinateReferenceSystem crs = CrsLookup.find(comboCrs.getSelectedItem().toString());
                if(crs == null) {
                    JOptionPane.showMessageDialog(null, "Error when reading crs", "Error", JOptionPane.ERROR_MESSAGE);
                    dispose();
                }
                SimpleFeatureTypeBuilder builder = new SimpleFeatureTypeBuilder();
                builder.setName(name);
                builder.setCRS(crs);
                builder.add("the_geom", Point.class);
                List<Double> longs = parser.parseColumnByName(comboLongitude.getSelectedItem().toString(), checkBoxFirstRow.isSelected());
                List<Double> lats = parser.parseColumnByName(comboLatitude.getSelectedItem().toString(), checkBoxFirstRow.isSelected());


                GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory();
                SimpleFeatureType featureType = builder.buildFeatureType();
                SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(featureType);
                List<SimpleFeature> features = new ArrayList<>();
                for(int i=0; i<longs.size(); i++) {
                    if(longs.get(i) == null || lats.get(i) == null) {
                        continue;
                    }
                    Point point = geometryFactory.createPoint(new Coordinate(longs.get(i), lats.get(i)));
                    featureBuilder.add(point);
                    SimpleFeature feature = featureBuilder.buildFeature(null);
                    features.add(feature);
                }

                //JSimpleStyleDialog.showDialog(null, featureType);
                SimpleFeatureCollection collection = new ListFeatureCollection(featureType, features);
                Style style = JSimpleStyleDialog.showDialog(null, featureType);//SLD.createSimpleStyle(featureType, Color.RED);
                FeatureLayer layer = new  FeatureLayer(collection, style);
                layer.setTitle(name);
                layersPanel.add(name, new PointIcon(Color.RED), true);
                mapContent.addLayer(layer);
                dispose();
            }
        };
        comboSeparator.addActionListener(actionListener);
        buttonAdd.addActionListener(actionListener);
    }

    private char comboTextToChar(String txt) {
        if (Objects.equals(txt, "TAB")) {
            return '\t';
        } else if (Objects.equals(txt, "SPACE")) {
            return ' ';
        }
        return txt.charAt(0);
    }

    private WidgetAndPanel<JCheckBox> createCheckBox(String labelText) {
        JPanel checkboxPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JCheckBox checkBox = new JCheckBox();
        checkBox.setSelected(false);
        checkboxPanel.add(label, BorderLayout.WEST);
        checkboxPanel.add(checkBox, BorderLayout.CENTER);
        return new WidgetAndPanel<>(checkBox, checkboxPanel);
    }

    private WidgetAndPanel<JComboBox<String>> createLabelCombo(String labelText, String[] comboList) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel label = new JLabel(labelText);
        JComboBox<String> combo = new JComboBox<>(comboList);
        panel.add(label, BorderLayout.WEST);
        panel.add(combo, BorderLayout.CENTER);
        return new WidgetAndPanel<>(combo, panel);
    }
}