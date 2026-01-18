package org.gisik.layersview;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * Table cell renderer for displaying layer nodes in a JTable.
 * <p>
 * Extends {@link LayerNodePanel} to show the label and icon of a {@link LayerNodeData}.
 * Highlights the cell background when selected.
 */
public class LayerNodeRenderer extends LayerNodePanel implements TableCellRenderer {
    /**
     * Constructs a new LayerNodeRenderer.
     */
    public LayerNodeRenderer() {
        super();
    }

    /**
     * Returns the component used for drawing the cell in a JTable.
     * Sets the label text and icon based on the {@link LayerNodeData} value.
     * Changes background color if the cell is selected.
     *
     * @param table      the JTable that uses this renderer
     * @param value      the value to assign to the cell (should be a {@link LayerNodeData})
     * @param isSelected true if the cell is selected
     * @param hasFocus   true if the cell has focus
     * @param row        the row of the cell being drawn
     * @param col        the column of the cell being drawn
     * @return the component used to render the cell
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int col) {

            if (isSelected) {
                setBackground(Color.BLUE);
            } else {
                setBackground(Color.WHITE);
            }

        LayerNodeData nodeData = (LayerNodeData) value;
        this.setOpaque(true);
        this.label.setText(nodeData.getLabel());
        this.label.setIcon(nodeData.getIcon());

        return this;
    }
}