/*
 * ChipInfo.java    1.0    4/14/2008
 *
 * Copyright (c) 2008 The Hamner Institutes for Health Sciences
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used to read chip information from local file related to annotations
 */

package org.ciit.annot;

public class ChipInfo {
    private String chipId, chipName, provider, species;

    public ChipInfo() {
    }

    public ChipInfo(String name) {
        chipName = name;
    }

    public ChipInfo(String[] values) {
        chipName = values[0];
        chipName = values[1];
        provider = values[2];
        species = values[3];
    }

    /**
     * Setter functions
     */
    public void setId(String id) {
        chipId = id;
    }

    public void setName(String name) {
        chipName = name;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setSpecies(String sp) {
        species = sp;
    }

    /**
     * Getter functions
     */
    public String getId() {
        return chipId;
    }

    public String getName() {
        return chipName;
    }

    public String getProvider() {
        return  provider;
    }

    public String getSpecies() {
        return species;
    }
}

