package org.gisik.layerstree;

import javax.swing.*;
import java.awt.*;

public class LayerNodeEditor extends DefaultCellEditor  {

    public JTextField textField;
    private LayerNodeData currentValue;
    public LayerNodeEditor() {
        super(new JTextField());
        System.out.println("Editor");
        this.textField = (JTextField) getComponent();
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int col) {

        currentValue = (LayerNodeData) value;
        textField.setText(currentValue.getLabel());
        return textField;
    }

    @Override
    public Object getCellEditorValue() {
        currentValue.setLabel(textField.getText());
        return currentValue;
    }
}
