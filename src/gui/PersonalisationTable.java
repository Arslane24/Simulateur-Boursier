package gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class PersonalisationTable extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (column == 3 && value instanceof Double) {
            double variation = (Double)value;
            if (variation < 0) {
                c.setForeground(Color.RED);
            } else if (variation > 0) {
                c.setForeground(new Color(0, 150, 0));
            } else {
                c.setForeground(Color.BLACK);
            }
            setText(String.format("%.2f%%", variation));
        }
        else if (column == 2 && value instanceof Double) {
            setText(String.format("%.2f€", (Double)value));
        }
        
        return c;
    }
}
