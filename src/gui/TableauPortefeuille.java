package gui;

import javax.swing.table.DefaultTableModel;

public class TableauPortefeuille extends DefaultTableModel {
    public TableauPortefeuille(String[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    
}