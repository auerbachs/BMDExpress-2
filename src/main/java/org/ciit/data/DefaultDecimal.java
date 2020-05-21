/**
 *  DefaultDecimal.java
 *  Create 5/23/2010
 *
 *  Determin the maximum decimal number from the imput ArrayMatrixData
 */

package org.ciit.data;


import javax.swing.*;

public class DefaultDecimal extends Thread {
    private JFrame parent;
    private ArrayMatrixData inMatrix;

    public DefaultDecimal() {
    }

    public DefaultDecimal(JFrame jf, ArrayMatrixData inMatrix) {
        parent = jf;
        this.inMatrix = inMatrix;
    }

    public void run() {
        if (inMatrix != null) {
            processData();
        }

        terminate();
    }

    public void terminate() {
        try {
            interrupt();
        } catch (SecurityException e) {
        }
    }

    public void processData(){
        int max = 0;

        try{
            Object[][] data = inMatrix.getData();

            if (data != null) {
                int rows = data.length;
                int cols = data[0].length;

                for (int j = 1; j < cols; j++) {
                    for (int i = 1; i < rows; i++) {
                        if (data[i][j] != null) {
                            if (data[i][j] instanceof Double) {
                                String st = data[i][j].toString();
                                int index = st.lastIndexOf(".");

                                if (index >= 0) {
                                    int dif = st.length() - index - 1;

                                    if (max < dif) {
                                        max = dif;
                                    }
                                }
                            } else { // not an Double object
                                System.out.println("(" + i + ", " + j + ")");
                                break;
                            }
                        }
                    }
                }
            }

            //System.out.println("Maximum Decimal: " + max);
            inMatrix.setMaxDecimal(max);
        } catch(Exception e){
            //parent.showException("Apply Precision", e);
            JOptionPane.showMessageDialog(parent,
                                          e.toString(),
                                          "Find Maximum Decimal",
                                          JOptionPane.WARNING_MESSAGE);
        }
    }
}
