package gui;

import javax.swing.table.DefaultTableModel;

public class Tableaudedonnes extends DefaultTableModel {
    public Tableaudedonnes(String[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false; 
    }

    
}