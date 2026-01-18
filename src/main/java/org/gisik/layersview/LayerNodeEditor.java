package org.gisik.layersview;

import javax.swing.*;
import java.awt.*;

/**
 * A custom table cell editor for editing {@link LayerNodeData} objects in a JTable.
 * <p>
 * This editor displays a JTextField for modifying the label of a LayerNodeData instance.
 */
public class LayerNodeEditor extends DefaultCellEditor  {

    /** The text field used for editing the label of the current LayerNodeData. */
    public JTextField textField;

    /** The currently edited LayerNodeData instance. */
    private LayerNodeData currentValue;

    /**
     * Constructs a LayerNodeEditor using a JTextField as the editing component.
     */
    public LayerNodeEditor() {
        super(new JTextField());
        System.out.println("Editor");
        this.textField = (JTextField) getComponent();
    }

    /**
     * Returns the component used for editing the cell value.
     *
     * @param table the JTable that is asking the editor to edit
     * @param value the value of the cell to be edited (expected to be a LayerNodeData)
     * @param isSelected whether the cell is selected
     * @param row the row of the cell being edited
     * @param col the column of the cell being edited
     * @return the component used for editing (the text field)
     */
    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int col) {

        currentValue = (LayerNodeData) value;
        textField.setText(currentValue.getLabel());
        return textField;
    }

    /**
     * Returns the value contained in the editor.
     * Updates the label of the current LayerNodeData with the text field's content.
     *
     * @return the updated LayerNodeData instance
     */
    @Override
    public Object getCellEditorValue() {
        currentValue.setLabel(textField.getText());
        return currentValue;
    }
}
