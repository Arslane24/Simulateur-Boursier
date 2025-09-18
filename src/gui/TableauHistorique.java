package gui;

import javax.swing.table.DefaultTableModel;

public class TableauHistorique extends DefaultTableModel {
    public TableauHistorique(String[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}