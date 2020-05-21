/*
 * HttpUpdateWork.java    1.0    4/14/2008
 *
 * Copyright (c) 2008 The Hamner Institutes for Health Sciences
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used to read array information from local file related to annotations
 */

package com.sciome.bmdexpress2.util.annotation;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;

/**
 * The class of HttpUpdateWork
 *
 * Read information of supported chips/arrays
 *
 * @version 1.0 4/14/2008
 * @author Longlong Yang
 */
public class HttpUpdateWork
{
	// private BenchmarkDose parent;
	// private MutableProgressBar progressBar;
	private int goTermIdx = 0;
	private String httpURL, provider, chip, chipId;
	// private String[][] arrayInfo = null, pathwayInfo = null;
	private Hashtable<String, ChipInfo> chipsHash;

	private List<Object[]> tableData;

	private final String[] columns = { "Select", "GEO Name", "ID", "Provider", "Species", "Size",
			"Last Updated Date", "Local Updated Data" };

	private final int THOUSAND = 1000;

	public HttpUpdateWork()
	{
	}

	/**
	 * class contructor used for local files and http url file download
	 *
	 */
	public HttpUpdateWork(String http)
	{
		// parent = bmd;
		httpURL = http;
	}

	/*
	 * Main task. Executed in background thread.
	 */
	public void doInBackground()
	{
		// progressBar = new MutableProgressBar(parent, TITLE, TYPES[0]);
		// progressBar.setIndeterminate(true);
		// progressBar.setString("Search Available Annotations");
		chipsHash = ChipInfoReader.readChipsInfo(httpURL);
		updateArrays();
		updateGeneOntology();
		// progressBar.setDone();
	}

	public void checkPath(String path)
	{
		File inFile = new File(path);

		if (!inFile.exists())
		{
			inFile.mkdirs();
		}
	}

	public File download(String http, String path, String fName)
	{
		checkPath(path);
		File inFile = new File(path, fName);

		FileInfo fInfo = URLUtils.download(http, inFile);
		// System.out.println("Web downloaded: " + inFile.getAbsolutePath());

		if (fInfo.getLastModified() > 0)
		{
			inFile.setLastModified(fInfo.getLastModified());
		}

		return inFile;
	}

	/**
	 * Get value of arrayInfo at[row, col] public String arrayInfoAt(int row, int col) { return
	 * arrayInfo[row][col]; }
	 */

	public int arrayCount()
	{
		return chipsHash.size();
	}

	public boolean httpAccessible()
	{
		String httpAddress = httpURL + BMDExpressConstants.getInstance().ARRAYDIR + "/"
				+ BMDExpressConstants.getInstance().MICROARRAYGZ;
		FileInfo fInfo = URLUtils.httpAccessible(httpAddress);

		if (fInfo.getException() != null)
		{
			// parent.showException("Inernet Accessing " + MICROARRAYGZ, fInfo.getException());
		}

		return fInfo.isAccessible();
	}

	public FileInfo checkRelease(String provider, String chip)
	{
		String httpAddress = httpURL + BMDExpressConstants.getInstance().ARRAYDIR + "/" + provider + "/"
				+ chip + "/" + BMDExpressConstants.getInstance().ANNOTFILES[0];
		String path = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().ARRAYDIR + "/" + provider + "/" + chip;
		File inFile = new File(path, BMDExpressConstants.getInstance().ANNOTFILES[0]);
		FileInfo fInfo = new FileInfo(httpAddress, inFile);
		fInfo = URLUtils.updateAvailable(fInfo);

		if (fInfo.getException() != null)
		{
			// parent.showException("Internet Accessing " + ANNOTFILES[0], fInfo.getException());
		}

		return fInfo;
	}

	public void updateArrays()
	{
		int max = chipsHash.size();

		if (max > 0)
		{
			Collection<ChipInfo> ChipsValues = chipsHash.values();
			tableData = new ArrayList<Object[]>(max + 1);
			String[] urls = new String[max];
			File[] localFiles = new File[max];
			DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);

			for (ChipInfo chip : ChipsValues)
			{
				String chipName = chip.getGeoID();
				String geoName = chip.getGeoName();
				String provider = chip.getProvider();
				String species = chip.getSpecies();
				FileInfo fileInfo = checkRelease(provider, chipName);

				Object[] rowDt = new Object[columns.length + 1];
				rowDt[0] = Boolean.FALSE;
				rowDt[1] = chipName;
				rowDt[2] = geoName;
				rowDt[3] = provider;
				rowDt[4] = species;
				int size = fileInfo.getSize();

				if (size > THOUSAND)
				{
					size = (int) Math.ceil(size / 1000.0);
					rowDt[5] = size + " KB";
				}
				else
				{
					rowDt[5] = size + " Bytes";
				}

				rowDt[6] = dateFormat.format(fileInfo.getLastModified());

				if (fileInfo.getFile().exists())
				{
					rowDt[7] = dateFormat.format(fileInfo.getFile().lastModified());
				}
				else
				{
					rowDt[7] = "-";
				}
				if (fileInfo.isAccessible() && fileInfo.getLastModified() > 0)
				{}

				int cur = tableData.size();
				tableData.add(rowDt);
				urls[cur] = fileInfo.getHttpURL();
				localFiles[cur] = fileInfo.getFile();
			}
		}
	}

	public void processUpdate(String chip, String provider)
	{
		// progressBar.setString("Processing " + chip);

		for (int i = 0; i < BMDExpressConstants.getInstance().ANNOTFILES.length; i++)
		{
			String http = httpURL + BMDExpressConstants.getInstance().ARRAYDIR + "/" + provider + "/" + chip
					+ "/" + BMDExpressConstants.getInstance().ANNOTFILES[i];
			String path = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ BMDExpressConstants.getInstance().ARRAYDIR + "/" + provider + "/" + chip;
			File inFile = download(http, path, BMDExpressConstants.getInstance().ANNOTFILES[i]);
		}
	}

	/**
	 * Update Gene Ontology annotation file
	 */
	private void updateGeneOntology()
	{
		String http = httpURL + BMDExpressConstants.getInstance().GODIR + "/"
				+ BMDExpressConstants.getInstance().GOFILENAME;
		String path = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().GODIR;
		File localFile = new File(path, BMDExpressConstants.getInstance().GOFILENAME);
		// progressBar.setMessage("Check and update Gene Ontology annotations");
		// progressBar.setString("Processing " + GOFILENAME);
		checkPath(path);
		// check available update
		boolean isAvailable = URLUtils.updateAvailable(http, localFile);

		if (isAvailable)
		{
			// update
			FileInfo fInfo = URLUtils.download(http, localFile);

			if (fInfo.getLastModified() > 0)
			{
				localFile.setLastModified(fInfo.getLastModified());
			}
		}
	}

	public List<Object[]> getTableData()
	{
		return tableData;
	}

	public void setTableData(List<Object[]> tableData)
	{
		this.tableData = tableData;
	}

	public Hashtable<String, ChipInfo> getChipsHash()
	{
		return chipsHash;
	}

	public void setChipsHash(Hashtable<String, ChipInfo> chipsHash)
	{
		this.chipsHash = chipsHash;
	}

	private void processUpdate(FileInfo fileInfo)
	{
		String http = fileInfo.getHttpURL();
		File localFile = fileInfo.getFile();
		// progressBar.setString("Processing " + localFile.getName());

		if (!localFile.getParentFile().exists())
		{
			localFile.getParentFile().mkdirs();
		}

		FileInfo fInfo = URLUtils.download(http, localFile);

		if (fInfo.getLastModified() > 0)
		{
			localFile.setLastModified(fInfo.getLastModified());
		}
	}
}
