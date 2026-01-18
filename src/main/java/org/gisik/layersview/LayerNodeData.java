package org.gisik.layersview;

/**
 * Represents a single entry (node) in the layers panel.
 * <p>
 * Each node has a textual label, an optional icon (e.g., color or shape),
 * and an optional checked/visible state (not stored in this version, but may be handled by the UI table).
 */
public class LayerNodeData {

    /** The label of the layer node. */
    private String label;

    /** The optional icon representing the layer. */
    private FigureIcon icon;

    /**
     * Constructs a LayerNodeData with a label and icon.
     *
     * @param label the textual label of the node
     * @param icon  the icon associated with the layer
     * @param checked ignored in this implementation (legacy parameter)
     */
    public LayerNodeData(String label, FigureIcon icon, boolean checked) {
        this.label = label;
        this.icon = icon;
    }

    /**
     * Constructs a LayerNodeData with only a label.
     *
     * @param label the textual label of the node
     * @param checked ignored in this implementation (legacy parameter)
     */
    public LayerNodeData(String label, boolean checked) {
        this.label = label;
        this.icon = null;
    }

    /**
     * Returns the label of this layer node.
     *
     * @return the node label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of this layer node.
     *
     * @param label the new label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the icon associated with this layer node.
     *
     * @return the node's icon, or null if none
     */
    public FigureIcon getIcon() {
        return icon;
    }

    /**
     * Sets the icon associated with this layer node.
     *
     * @param icon the icon to associate with this node
     */
    public void setIcon(FigureIcon icon) {
        this.icon = icon;
    }

}
