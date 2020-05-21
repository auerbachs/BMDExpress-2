/*
 * NumeralTableModel.java
 *
 * Created on 4/28/2011
 *
 * Extends DefaultTableModel to handle null value for column sorting
 */


package com.sciome.bmdexpress2.util.categoryanalysis.defined;

import javax.swing.table.DefaultTableModel;

public class NumeralTableModel extends DefaultTableModel{

    public NumeralTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    public Class getColumnClass(int columnIndex) {
        Object obj = getValueAt(0, columnIndex);

        if (obj == null) {
            obj = new String();
        }

        return obj.getClass();
    }
}
