package org.gisik.layersview;

import javax.swing.*;

public class LayerNodeData {

    private String label;
    private FigureIcon icon;

    public LayerNodeData(String label, FigureIcon icon, boolean checked) {
        this.label = label;
        this.icon = icon;
    }

    public LayerNodeData(String label, boolean checked) {
        this.label = label;
        this.icon = null;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public FigureIcon getIcon() {
        return icon;
    }

    public void setIcon(FigureIcon icon) {
        this.icon = icon;
    }

}
