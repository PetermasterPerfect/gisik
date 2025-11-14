package org.gisik.layerstree;

import javax.swing.*;

public class LayerNodeData {

    private String label;
    private Icon icon;
    private boolean checked;

    public LayerNodeData(String label, Icon icon, boolean checked) {
        this.label = label;
        this.icon = icon;
        this.checked = checked;
    }

    public LayerNodeData(String label, boolean checked) {
        this.label = label;
        this.icon = null;
        this.checked = checked;
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

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
