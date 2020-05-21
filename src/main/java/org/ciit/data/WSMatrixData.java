/**
 * WSMatrixData.java
 *
 * Created 9/19/2008
 * Last Modified 9/19/2008
 * Author: Longlong Yang
 * Modified based on Eric Healy's MatrixData used to trace Work Source (WS)
 * so keep the original MatrixData as the basic class
 */

package org.ciit.data;

import org.ciit.util.MatrixData;

public class WSMatrixData extends MatrixData {
    public String srcName, workSrc;
    public String infoPaneData;

    public static final String DATASOURCE = "Data Source";
    public static final String WORKSOURCE = "Work Source";
    public static final String COLONSPACE = ": ";
    public static final String newLine = "\n";

    public WSMatrixData() {
        this("New", null, null);
    }

    public WSMatrixData(String name) {
        this(name, null, null);
    }

    public WSMatrixData(String name, String[] colNames, Object[][] dt) {
        super(name, colNames, dt);
        workSrc = null;
        //pwData = null;
        infoPaneData = null;
    }


    /**
     * Add (key, value) of String type to note separated by COLONSPACE = ": "
     *
     * @param key is a String
     * @param value is a String
     */
    public void addKeyValue(String key, String value) {
        String keyValue = key + this.COLONSPACE + value + this.newLine;

        if (note != null) {
            note = note.concat(keyValue);
        } else {
            note = keyValue;
        }
    }

    /**
     * Read the value from the note gvien a key and return the value
     *
     * @param key is a String for a value from the note
     * @return value is defined by the given key. return null if not key matches
     */
    public String readKeyValue(String key) {
        String value = null;

        if (note != null && !note.isEmpty()) {
            String[] lines = note.split(this.newLine);

            for (int i = 0; i < lines.length; i++) {
                if (lines[i].startsWith(key)) {
                    String[] array = lines[i].split(this.COLONSPACE);

                    if (array != null && array.length > 0) {
                        value = array[1];
                    }

                    break;
                }
            }
        }

        return value;
    }

    /**
     * Used for rename data source
     */
    public void setSource(String name) {
        String regex = this.DATASOURCE + this.COLONSPACE + srcName;
        String replacement = this.DATASOURCE + this.COLONSPACE + name;
        note  = note.replaceFirst(regex, replacement);

        srcName = name;
    }

    /**
     * Used for rename work source
     */
    public void setWorkSource(String src) {
        String regex = this.WORKSOURCE + this.COLONSPACE + workSrc;
        String replacement = this.WORKSOURCE + this.COLONSPACE + src;
        note  = note.replaceFirst(regex, replacement);

        workSrc = src;
    }

    public String getSource() {
        if (srcName == null) {
            srcName = readKeyValue(this.DATASOURCE);
        }

        return srcName;
    }

    public String getWorkSource() {
        if (workSrc == null) {
            workSrc = readKeyValue(this.WORKSOURCE);
        }

        return workSrc;
    }
}
