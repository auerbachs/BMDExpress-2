/*
 * PropertiesParser.java
 */

package com.sciome.bmdexpress2.shared;

import java.io.*;
import java.util.Properties;

public class PropertiesParser {
    private Properties properties;
    private File propertyFile;
    private String fileName;

    private final String comment = "# Don't change phrase before '='";

    public PropertiesParser() {
        properties = new Properties();
    }
    public PropertiesParser(String name) {
        this(new File(name));
    }

    public PropertiesParser(File file) {
        this();
        propertyFile = file;
        loadProperties();
    }

    public PropertiesParser(InputStream inStream) {
        this();
        try {
            properties.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PropertiesParser(Properties prop) {
        properties = prop;
    }

    private void loadProperties() {
        try {
            FileInputStream ins = new FileInputStream(propertyFile);
            properties.load(ins);
        } catch (Exception e) {
            System.out.println("Problem to load property file '"
                              + propertyFile.getName());
            reLoadProperties(propertyFile.getName());
        }
    }
    /**
     * Read configuration from config.properties file
     * in the same jar file as this class.
     *
     * @param propertyFileName is the file name with properties
     * @return no
     */
    private void reLoadProperties(String propertyFileName) {
        try {
            //ClassLoader cl = Thread.currentThread().getContextClassLoader();
            ClassLoader cl = this.getClass().getClassLoader();
            InputStream ins = cl.getResourceAsStream(propertyFileName);
            //FileInputStream ins = new FileInputStream(new File(propertyFileName));
            properties = new Properties();
            properties.load(ins);
        } catch (IOException e) {
            System.out.println("Property file '" + propertyFileName
                        + "' not found.");
        } catch (Exception e) {
            System.out.println("Property file '" + propertyFileName
                        + "' problem. Perhaps a misnamed property?");
        }
    }

    public void loadFromXML(String xml) {
        try {
            //StringBufferInputStream in = new StringBufferInputStream(xml);
            ByteArrayInputStream in =
                    new ByteArrayInputStream(xml.getBytes());
            properties.loadFromXML(in);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed loadFromXML()");
        }
    }

    public void writeFile() {
        try {
            FileOutputStream fst = new FileOutputStream(propertyFile);
            properties.store(fst, comment);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to writFile()");
        }
    }

    public String storeToXML(String comment) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            properties.storeToXML(bos, comment);

            return bos.toString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to storeToXML()");
            return null;
        }
    }

    public boolean exists() {
        return propertyFile.exists();
    }

    public void setFile(File file) {
        propertyFile = file;
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public void setProperty(String key, double value) {
        setProperty(key, intString(value));
    }

    private String intString(double db) {
        return Integer.toString((new Double(db)).intValue());
    }

    public int getPropertyInt(String key) {
        try {
            String property = properties.getProperty(key);

            return Integer.parseInt(property.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public double getPropertyDouble(String key) {
        try {
            String property = properties.getProperty(key);

            return Double.parseDouble(property.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public String getProperty(String key) {
        try {
            String property = properties.getProperty(key);

            return property.trim();
        } catch (Exception e) {
            //System.out.println("Failed to get property '" + key
            //            + "': missing or misnamed?");

            return null;
        }
    }

    public boolean getPropertyBoolean(String key) {
        try {
            String property = properties.getProperty(key);

            if (property.trim().equals("true")) {
                return true;
            }
        } catch (Exception e) {
        }

        return false;
    }

    public Properties getProperties() {
        return properties;
    }

    public File getPropertyFile(String key) {
        try {
            String property = getProperty(key);

            if (property.isEmpty()) {
                return new File("");
            } else {
                return new File(property);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        return properties.toString();
    }
}
