/*
 * Program name: StringTableCellRenderer.java
 * Author: Longlong Yang
 * Created: 2/26/2010
 * Last modified: 2/26/2010
 */

package com.sciome.bmdexpress2.util.categoryanalysis.defined;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * A table cell renderer to display Object as simple String
 * and avoid formating a Double object to default precision
 *
 * @version 1.0   11/5/2002
 * @author  Longlong Yang
 */
public class StringTableCellRenderer extends DefaultTableCellRenderer {
    /* Overwrite getTableCellRendererComponent(...) */
    public StringTableCellRenderer() {
        super();
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);

        if (value == null) {
            setText("");
        } else {
            setText(value.toString());
        }

        return this;
    }
}
