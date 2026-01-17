package org.gisik.csv;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.feature.simple.SimpleFeatureType;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.style.Style;
import org.geotools.data.collection.ListFeatureCollection;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.geotools.map.MapContent;
import org.gisik.*;
import org.gisik.crs.CrsLookup;
import org.gisik.layersextra.FeatureLayerFromCsvProject;
import org.gisik.layersextra.FeatureLayerProject;
import org.gisik.layersview.PointIcon;
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

import static org.gisik.csv.CsvParser.comboTextToChar;

public class CsvLoaderDialog extends DialogBase {

    private final String name;
    private final String absPath;
    private final CsvParser parser;
    private final CoordinateReferenceSystem projectCrs;
    JComboBox<String> comboSeparator;
    JComboBox<String> comboLongitude;
    JComboBox<String> comboLatitude;
    JCheckBox checkBoxFirstRow;
    JButton buttonAdd;
    JComboBox<String> comboCrs;
    String[] separtors = {",", ".", ";", "SPACE", "TAB"};
    MapContent mapContent;
    LayersPanel layersPanel;
    ProjectManager projectManager;

    public CsvLoaderDialog(File csvFile, CoordinateReferenceSystem projectCrs, ProjectManager projectManager,
                           MapContent mapContent, LayersPanel layersPanel, JFrame parent) throws IOException {

        super(parent, "Load csv file");
        this.projectCrs = projectCrs;
        this.projectManager = projectManager;
        setSize(600, 400);
        setLocationRelativeTo(null);
        this.mapContent = mapContent;
        this.layersPanel = layersPanel;
        name = csvFile.getName();
        absPath =  csvFile.getAbsolutePath();
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
                    return;
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
                Color color = ColorStyle.randomColor();
                Style style = ColorStyle.createStyle2(featureType, color);
                try {
                    CsvShadowLoader loader =
                            new CsvShadowLoader(
                                    new File(absPath),
                                    comboSeparator.getSelectedItem().toString(),
                                    comboCrs.getSelectedItem().toString(),
                                    comboLongitude.getSelectedItem().toString(),
                                    comboLatitude.getSelectedItem().toString(),
                                    Boolean.toString(checkBoxFirstRow.isSelected())
                            );

                    FeatureLayerProject layer =
                            loader.loadProjectLayer(
                                    projectCrs
                            );

                    if (projectManager != null) {
                        projectManager.addLayer(layer);
                        projectManager.rebuildRenderLayers();
                    } else {
                        mapContent.addLayer(layer);
                    }

                    layersPanel.add(name, new PointIcon(ColorStyle.randomColor()), true);
                    dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(
                            null,
                            ex.getMessage(),
                            "CSV Load Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

            }
        };
        comboSeparator.addActionListener(actionListener);
        buttonAdd.addActionListener(actionListener);
    }
}