/*
 * GenesGOTerms.java     1.0    9/22/2008
 *
 * Copyright (c) 2008 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used for match genes to Gene Ontology categories/terms
 */

package com.sciome.bmdexpress2.util.categoryanalysis.catmap;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.category.identifier.CategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.annotation.FileInfo;
import com.sciome.bmdexpress2.util.annotation.URLUtils;
import com.sciome.bmdexpress2.util.categoryanalysis.ProbeGeneMaps;

/**
 * The class of GenesGOTerms
 *
 * match genes to pathways
 *
 * @version 1.0 4/7/2008
 * @author Longlong Yang
 */
public abstract class CategoryMapBase
{
	protected ProbeGeneMaps					probeGeneMaps;
	protected String						organismCode;
	protected Hashtable<String, String>		titleHash;
	protected Hashtable<String, Vector>		subHash, allHash, dataSetGeneHash;
	protected Vector<CategoryIdentifier>	categoryIdentifiers;

	protected Hashtable<String, String>		categoryHash;

	protected ChipInfo						chipInfo;
	private long							categoryFileVersionDate;

	/**
	 * class contructor used for gene's pathways
	 *
	 */
	public CategoryMapBase()
	{
	}

	public CategoryMapBase(ProbeGeneMaps probeGeneMaps, ChipInfo chipInfo)
	{

		this.probeGeneMaps = probeGeneMaps;
		this.chipInfo = chipInfo;
	}

	protected File checkDownload(String http, String path, String fName)
	{
		File inFile = new File(path);

		if (!inFile.exists())
		{
			inFile.mkdirs();
		}

		inFile = new File(inFile.getAbsolutePath(), fName);
		long httpmod = getDateOfHTTPFile(http);
		long localmod = inFile.lastModified();

		if (inFile.exists() && BMDExpressProperties.getInstance().isConsole())
		{
			// System.out.println("This is console application and the file: " + inFile.getName()
			// + " exists. Not looking for update on server.");
			return inFile;
		}

		if (!inFile.exists() || httpmod > localmod)
		{
			FileInfo fInfo = URLUtils.download(http, inFile);

			categoryFileVersionDate = fInfo.getLastModified();
			if (fInfo.getLastModified() > 0)
			{
				inFile.setLastModified(fInfo.getLastModified());
			}
			else
			{
				if (fInfo.getException() != null)
				{
					// parent.showException("Download File - " + fName, fInfo.getException());
				}
			}
		}
		else
		{
			categoryFileVersionDate = inFile.lastModified();
		}

		return inFile;
	}

	public Hashtable<String, Vector> subHash()
	{
		return subHash;
	}

	public Hashtable<String, Vector> allHash()
	{
		return allHash;
	}

	public Hashtable<String, Vector> dataSetGeneHash()
	{
		return dataSetGeneHash;
	}

	public int getAllGeneCount()
	{
		Set<String> geneSet = new HashSet<>();
		for (String key : allHash.keySet())
		{
			for (Object gene : allHash.get(key))
			{
				geneSet.add(gene.toString());
			}
		}

		return geneSet.size();

	}

	public int getSubGeneCount()
	{
		Set<String> geneSet = new HashSet<>();
		for (String key : subHash.keySet())
		{
			for (Object gene : subHash.get(key))
			{
				geneSet.add(gene.toString());
			}
		}

		return geneSet.size();

	}

	public CategoryIdentifier getCategoryIdentifier(int row)
	{
		return categoryIdentifiers.get(row);
	}

	public int categoryMappingCount()
	{
		if (categoryIdentifiers == null)
		{
			return 0;
		}
		else
		{
			return categoryIdentifiers.size();
		}
	}

	public String getOrganismCode()
	{
		return organismCode;
	}

	public void setOrganismCode(String organismCode)
	{
		this.organismCode = organismCode;
	}

	public long getCategoryFileVersionDate()
	{
		return categoryFileVersionDate;
	}

	public void setCategoryFileVersionDate(long categoryFileVersionDate)
	{
		this.categoryFileVersionDate = categoryFileVersionDate;
	}

	private long getDateOfHTTPFile(String httpPath)
	{
		URL url = null;
		HttpURLConnection httpCon = null;
		try
		{
			url = new URL(httpPath);
			httpCon = (HttpURLConnection) url.openConnection();

		}
		catch (IOException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		long date = httpCon.getLastModified();
		if (date == 0)
			System.out.println("No last-modified information.");
		else
			System.out.println("Last-Modified: " + new Date(date));

		return date;
	}

}
