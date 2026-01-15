package org.gisik.layersview;

import javax.swing.*;

public class LayerNodeData {

    private String label;
    private Icon icon;

    public LayerNodeData(String label, Icon icon, boolean checked) {
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

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

}
