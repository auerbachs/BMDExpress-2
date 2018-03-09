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

package com.sciome.bmdexpress2.mvp.model.chip;

import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/*
 * Used from originial source to store chip information.  This is useful for various analyses.  It is 
 * associated with Experiement data.
 */
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class ChipInfo implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -3922295568888064743L;
	private String				chipId, chipName, provider, species, geoID, geoName;

	public static String		AFFYMETRIX			= "Affymetrix";
	public static String		BIOSPYDER			= "BioSpyder";
	public static String		AGILENT				= "Agilent";
	public static String		REFSEQ				= "RefSeq";
	public static String		ENSEMBL				= "Ensembl";

	public ChipInfo()
	{
	}

	public ChipInfo(String name)
	{
		chipName = name;
	}

	public ChipInfo(String[] values)
	{
		chipName = values[0];
		chipName = values[1];
		provider = values[2];
		species = values[3];

		if (values.length > 4)
		{
			geoID = values[4];
			geoName = values[5];
		}
	}

	@JsonIgnore
	public String getId()
	{
		return chipId;
	}

	/**
	 * Setter functions
	 */
	public void setId(String id)
	{
		chipId = id;
	}

	public void setName(String name)
	{
		chipName = name;
	}

	public void setProvider(String provider)
	{
		this.provider = provider;
	}

	public void setSpecies(String sp)
	{
		species = sp;
	}

	/**
	 * Getter functions
	 */
	public String getChipId()
	{
		return chipId;
	}

	public String getName()
	{
		return chipName;
	}

	public String getProvider()
	{
		return provider;
	}

	public String getSpecies()
	{
		return species;
	}

	public String getGeoID()
	{
		return geoID;
	}

	public String getGeoName()
	{
		return geoName;
	}

	@Override
	public String toString()
	{
		if (geoID == null)
			return "Generic";
		if (provider.equals(ChipInfo.BIOSPYDER) || provider.equals(ChipInfo.REFSEQ)
				|| provider.equals(ChipInfo.ENSEMBL))
			return geoName;
		return geoName + "(" + geoID + ")";

	}

	// backwards compatibility. We went from using the name as unique identifier to using the
	// GEO id as unique indentifier. Use this mapping to grab the geoID from legacy instances
	// of chipinfo stored in bm2 files

	private void nameToGeoIDLegacy()
	{
		if (geoID != null)
			return;
		if (getName().equals("DrosGenome1") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL72";
			geoName = "Affymetrix Drosophila Genome Array";
		}
		else if (getName().equals("Drosophila_2") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL1322";
			geoName = "Drosophila Genome 2.0 Array";
		}
		else if (getName().equals("HG-Focus") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL201";
			geoName = "Human HG-Focus Target Array";
		}
		else if (getName().equals("HG-U133A") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL96";
			geoName = "Human Genome U133A Array";
		}
		else if (getName().equals("HG-U133A_2") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL570";
			geoName = "Human Genome U133 Plus 2.0 Array";
		}
		else if (getName().equals("HG-U133_Plus_2") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL571";
			geoName = "Human Genome U133A 2.0 Array";
		}
		else if (getName().equals("HT_HG-U133_Plus_PM") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL13158";
			geoName = "HT HG-U133+ PM Array Plate";
		}
		else if (getName().equals("HT_MG-430_PM") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL11180";
			geoName = "HT MG-430 PM Array Plate";
		}
		else if (getName().equals("HT_Rat230_PM") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL16985";
			geoName = "HT RG-230 PM Array Plate";
		}
		else if (getName().equals("MG_U74A") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL32";
			geoName = "Murine Genome U74A Array";
		}
		else if (getName().equals("MG_U74Av2") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL81";
			geoName = "Murine Genome U74A Version 2 Array";
		}
		else if (getName().equals("MOE430A") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL339";
			geoName = "Mouse Expression 430A Array";
		}
		else if (getName().equals("MOE430B") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL340";
			geoName = "Mouse Expression 430B Array";
		}
		else if (getName().equals("Mouse430A_2") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL8321";
			geoName = "Mouse Genome 430A 2.0 Array";
		}
		else if (getName().equals("Mouse430_2") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL1261";
			geoName = "Mouse Genome 430 2.0 Array";
		}
		else if (getName().equals("RAE230A") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL341";
			geoName = "Rat Expression 230A Array";
		}
		else if (getName().equals("RAE230B") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL342";
			geoName = "Rat Expression 230B Array";
		}
		else if (getName().equals("Rat230_2") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL1355";
			geoName = "Rat Genome 230 2.0 Array";
		}
		else if (getName().equals("RG_U34A") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL85";
			geoName = "Rat Genome U34 Array";
		}
		else if (getName().equals("Zebrafish") && getProvider().equals(ChipInfo.AFFYMETRIX))
		{
			geoID = "GPL1319";
			geoName = "Zebrafish Genome Array";
		}
		else if (getName().equals("Drosophila") && getProvider().equals(ChipInfo.AGILENT))
		{
			geoID = "GPL17080";
			geoName = "Agilent-021791 D. melanogaster (FruitFly) Oligo Microarray - V2";
		}
		else if (getName().equals("Human_Genome_Whole") && getProvider().equals(ChipInfo.AGILENT))
		{
			geoID = "GPL6480";
			geoName = "Agilent-014850 Whole Human Genome Microarray 4x44K G4112F";
		}
		else if (getName().equals("Mouse_Genome_Whole") && getProvider().equals(ChipInfo.AGILENT))
		{
			geoID = "GPL6480";
			geoName = "Agilent-014850 Whole Human Genome Microarray 4x44K G4112F";
		}
		else if (getName().equals("Rat_Genome_Whole") && getProvider().equals(ChipInfo.AGILENT))
		{
			geoID = "GPL11280";
			geoName = "Agilent-024196 Whole Rat Genome Microarray 4x44K";
		}
		else if (getName().equals("Zebrafish_(Agilent)") && getProvider().equals(ChipInfo.AGILENT))
		{
			geoID = "GPL14664";
			geoName = "Agilent-026437 D. rerio (Zebrafish) Oligo Microarray V3";
		}
		else if (getName().equals("hg19_ensembl") && getProvider().equals(ChipInfo.ENSEMBL))
		{
			geoID = "hg19_ensembl";
			geoName = "hg19_ensembl";
		}
		else if (getName().equals("mm10_ensembl") && getProvider().equals(ChipInfo.ENSEMBL))
		{
			geoID = "mm10_ensembl";
			geoName = "mm10_ensembl";
		}
		else if (getName().equals("rn6_ensembl") && getProvider().equals(ChipInfo.ENSEMBL))
		{
			geoID = "rn6_ensembl";
			geoName = "rn6_ensembl";
		}

		else if (getName().equals("hg19_ensembl") && getProvider().equals(ChipInfo.REFSEQ))
		{
			geoID = "hg19_ensembl";
			geoName = "hg19_ensembl";
		}
		else if (getName().equals("mm10_ensembl") && getProvider().equals(ChipInfo.REFSEQ))
		{
			geoID = "mm10_ensembl";
			geoName = "mm10_ensembl";
		}
		else if (getName().equals("rn6_ensembl") && getProvider().equals(ChipInfo.REFSEQ))
		{
			geoID = "rn6_ensembl";
			geoName = "rn6_ensembl";
		}
		else if (getName().equals("rat") && getProvider().equals(ChipInfo.BIOSPYDER))
		{
			geoID = "S1500_rat";
			geoName = "S1500+_rat";
		}
		else if (getName().equals("human") && getProvider().equals(ChipInfo.BIOSPYDER))
		{
			geoID = "S1500_human";
			geoName = "S1500+_human";
		}

	}

	/*
	 * deserializing this object requires translating the byte array into a list of doses.
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		// read the object so that the responsesBlob gets filled up
		in.defaultReadObject();
		nameToGeoIDLegacy();

	}

	public static String getURL(String provider, String id)
	{
		if (provider.equalsIgnoreCase(ChipInfo.BIOSPYDER) || provider.equalsIgnoreCase(ChipInfo.ENSEMBL)
				|| provider.equalsIgnoreCase(ChipInfo.REFSEQ))
			return "";

		return "https://www.ncbi.nlm.nih.gov/geo/query/acc.cgi?acc=" + id;
	}
}
