/**
 *  FileInfo.java
 *
 *  For hold some information of a file's features
 */

package com.sciome.bmdexpress2.util.annotation;

import java.io.File;

public class FileInfo {
    private String httpURL = null, fName = null;
    private File file = null;
    private int fSize = 0;
    private long lastModified = 0;
    private boolean accessible = false;
    private Exception fException = null;

    public FileInfo() {
    }

    public FileInfo(String url, File f) {
        httpURL = url;
        file = f;
    }

    public FileInfo(String name) {
        fName = name;
    }

    public FileInfo(File f) {
        file = f;
    }

    public void setName(String name) {
        fName = name;
    }

    public void setLastModified(long date) {
        lastModified = date;
    }

    public void setSize(int size) {
        fSize = size;
    }

    public void setAccessible(boolean bool) {
        accessible = bool;
    }

    public void setException(Exception e) {
        fException = e;
    }

    /**
     *  return info
     */
    public String getName() {
        return fName;
    }

    public String getHttpURL() {
        return httpURL;
    }

    public File getFile() {
        return file;
    }

    public long getLastModified() {
        return lastModified;
    }

    public int getSize() {
        return fSize;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public Exception getException() {
        return fException;
    }
}
