/*
 * FileAnnotation 6/10/2009
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * Modified based on ProbeGeneMaps.java
 *
 * This program is created for Bench Mark Dose project
 * It is used for match probes to genes using local files
 */

package com.sciome.bmdexpress2.util.annotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;

/**
 * The class of FileAnnotation
 *
 * Each gene as a key refers to probe(s) as Vector of Strings - subHashG2Ids
 *
 * @version 1.0 4/7/2008
 * @author Longlong Yang
 */
public class FileAnnotation extends ArrayAnnotation
{
	private String						baseURL, basePath;
	private Hashtable<String, ChipInfo>	chipsHash	= new Hashtable<>();
	private ChipInfo[]					probesChips;
	public long							timeStamp	= 0;
	private Set<String>					geneSet		= new HashSet<>();

	/**
	 * class contructor used for local files and http url file download
	 *
	 */
	public FileAnnotation()
	{
		super();
		baseURL = BMDExpressProperties.getInstance().getUpdateURL();
		File file = new File("data");
		basePath = file.getAbsolutePath();

	}

	@Override
	public void setChip(String chip)
	{
		this.chip = chip;
		if (chip == null)
			chip = "Generic";
		chipInfo = chipsHash.get(chip);

		if (chipInfo != null)
		{
			chipId = chipInfo.getId();
			provider = chipInfo.getProvider();
			species = chipInfo.getSpecies();
		}
	}

	public ChipInfo getChip(String chip)
	{
		return chipsHash.get(chip);
	}

	/**
	 * File Based implementation below
	 *
	 * Fields: arrayId, arrayName, provider, species
	 */
	public void readArraysInfo()
	{
		chipsHash = ChipInfoReader.readChipsInfo(baseURL);
		Collection<ChipInfo> values = chipsHash.values();
		int size = values.size();
		probesChips = new ChipInfo[size];
		probesChips = values.toArray(probesChips);
	}

	public ChipInfo[] getProbesChips()
	{
		return probesChips;
	}

	/**
	 * Find possible chips matching given probes
	 */
	public ChipInfo[] findChips()
	{
		// probes2Hash(probes);
		int length = probesChips.length;
		// progressBar.setMessage("Match probes to array");
		// progressBar.setIndeterminate(true);
		// progressBar.setVisible(true);
		String fName = "probe2gene.gz";
		Vector<ChipInfo> vectChips = new Vector<>();
		int maxRead = probesHash.size() / 4;

		for (int i = 0; i < length; i++)
		{
			String chip = probesChips[i].getGeoID();
			ChipInfo chipInfo = chipsHash.get(chip);
			String provider = chipInfo.getProvider();
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ BMDExpressConstants.getInstance().ARRAYDIR + File.separator + provider + File.separator
					+ chip + File.separator;
			File inFile = new File(filePath, fName);

			if (inFile.exists())
			{

				int matched = pooledProbesGenes(inFile, maxRead);

				if (matched > 0 && matched > maxRead / 10)
				{
					vectChips.add(chipInfo);
				}
			}
		}

		// progressBar.setVisible(false);

		if (vectChips.size() > 0)
		{
			ChipInfo[] chips = new ChipInfo[vectChips.size()];
			chips = vectChips.toArray(chips);
			// chips = Arrays.sort(chips);

			return chips;
		}
		else
		{
			return probesChips;
		}
	}

	private int pooledProbesGenes(File inFile, int maxRead)
	{
		try
		{
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
			String line = reader.readLine();
			int count = 0, matched = 0;

			while ((line = reader.readLine()) != null)
			{
				if (!line.isEmpty())
				{
					String[] values = line.split(";");

					if (probesHash.containsKey(values[0]))
					{
						matched++;
					}

					count++;
				}

				if (count > maxRead)
				{
					break;
				}
			}

			reader.close();

			return matched;
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			// ExceptionDialog.showException(parent, "Read From File - " + inFile.getName(), e);
		}

		return 0;

	}

	/**
	 * For array annotation, added 5/19/2009, last modified 5/27/2011 Read annotation files to match probes to
	 * genes and genes to probes as hashtables limited to only probes of original expression data
	 *
	 *
	 * @param return
	 *            probe2GeneHash and gene2ProbeHash Hashtables
	 */
	public void arrayProbesGenes()
	{
		// System.out.println("FileAnnotation.arrayProbesGenes()" + probesHash.size());
		probe2GeneHash = null;
		gene2ProbeHash = null;
		String message = "Match array probes to genes";
		// progressBar.setMessage(message);
		String fName = "probe2gene.gz";
		String http = BMDExpressProperties.getInstance().getUpdateURL() + "/arrays/" + provider + "/" + chip
				+ "/" + fName;

		try
		{
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ BMDExpressConstants.getInstance().ARRAYDIR + File.separator + provider + File.separator
					+ chip + File.separator;
			File inFile = checkDownload(http, filePath, fName);

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
			String line = reader.readLine();

			if (line != null && !line.isEmpty())
			{
				int len = Integer.parseInt(line);
				probe2GeneHash = new Hashtable<String, Vector>(len * 4 / 3 + 1);
				gene2ProbeHash = new Hashtable<String, Vector>(len);
				// probeGenePairs = new String[rows.length - 2][];
				// progressBar.setIndeterminate(false);
				// progressBar.setMaximum(len);
				// progressBar.setMinimum(0);
				// progressBar.setString(null);
				int i = 1;

				while ((line = reader.readLine()) != null)
				{
					if (!line.isEmpty())
					{
						String[] values = line.split(";"); // (probe;genes)
						String[] genes = values[1].split(tab);

						for (int j = 0; j < genes.length; j++)
							geneSet.add(genes[j]);

						if (values.length > 1 && probesHash != null && probesHash.containsKey(values[0]))
						{
							// add probe / gene
							try
							{
								Vector<String> vectGenes = probe2GeneHash.get(values[0]);

								if (vectGenes == null)
								{
									vectGenes = new Vector<String>();
									probe2GeneHash.put(values[0], vectGenes);
								}

								for (int j = 0; j < genes.length; j++)
								{
									vectGenes.add(genes[j]);
								}
							}
							catch (NullPointerException e)
							{}

							// add gene / probe
							for (int j = 0; j < genes.length; j++)
							{
								try
								{
									Vector<String> vectProbes = gene2ProbeHash.get(genes[j]);

									if (vectProbes == null)
									{
										vectProbes = new Vector<String>();
										gene2ProbeHash.put(genes[j], vectProbes);
									}

									vectProbes.add(values[0]);
								}
								catch (NullPointerException e)
								{}
							}
						}
					}

					// progressBar.setValue(i++);
				}
			}

			reader.close();
			timeStamp = inFile.lastModified();
			// System.out.println(inFile.getName() + " setTimeStamp(): " + timeStamp);
		}
		catch (Exception e)
		{
			// e.printStackTrace();
			// ExceptionDialog.showException(parent, "Read From File - " + fName, e);
		}
	}

	/**
	 * For array annotation, added 5/19/2009 Dependant on the hashtable gene2ProbeHash
	 */
	public void arrayGenesSymbols()
	{
		gene2SymbolHash = null;
		String message = "Read genes and symbols of " + chip;
		// progressBar.setMessage(message);
		// progressBar.setIndeterminate(true);

		String fName = "genes2symbols.gz";
		String http = BMDExpressProperties.getInstance().getUpdateURL() + "/arrays/" + provider + "/" + chip
				+ "/" + fName;

		try
		{
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ BMDExpressConstants.getInstance().ARRAYDIR + File.separator + provider + File.separator
					+ chip + File.separator;
			File inFile = checkDownload(http, filePath, fName);

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
			String line = reader.readLine();

			if (line != null && !line.isEmpty())
			{
				try
				{
					int len = Integer.parseInt(line);
					int capacity = len * 4 / 3;
					gene2SymbolHash = new Hashtable<String, String>(capacity);
					// progressBar.setIndeterminate(false);
					// progressBar.setMaximum(len);
					// progressBar.setMinimum(0);
					// progressBar.setString(null);
					int i = 1;

					while ((line = reader.readLine()) != null)
					{
						if (!line.isEmpty())
						{
							String[] values = line.split(tab);

							if (values.length > 1 && gene2ProbeHash.containsKey(values[0]))
							{
								gene2SymbolHash.put(values[0], values[1]);
							}

						}

						// progressBar.setValue(i++);
					}
				}
				catch (Exception e)
				{
					// e.printStackTrace();
					// ExceptionDialog.showException(parent, "Read From File - " + fName, e);
				}
			}

			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			// ExceptionDialog.showException(parent, "Read From File - " + fName, e);
		}
	}

	public boolean isUpdated()
	{
		String fName = "probe2gene.gz";
		String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().ARRAYDIR + File.separator + provider + File.separator
				+ chip + File.separator;
		File inFile = new File(filePath, fName);

		if (inFile.exists() && inFile.lastModified() > timeStamp)
		{
			return true;
		}

		return false;
	}

	public Set<String> getGeneSet()
	{
		return geneSet;
	}

	/**
	 * Check if local file exists, if not then download from http
	 *
	 * @param http
	 *            is the URL address of a remote host/server
	 * @param path
	 *            is the relative path to the file (local and remote)
	 * @param fName
	 *            is the name of the file
	 */
	public File checkDownload(String http, String path, String fName)
	{
		File inFile = new File(path);

		if (!inFile.exists())
		{
			inFile.mkdirs();
		}

		inFile = new File(inFile.getAbsolutePath(), fName);

		if (!inFile.exists())
		{
			FileInfo fInfo = URLUtils.download(http, inFile);

			if (fInfo.getLastModified() > 0)
			{
				inFile.setLastModified(fInfo.getLastModified());
			}
			else
			{
				if (fInfo.getException() != null)
				{
					// ExceptionDialog.showException(parent, "Download File - " + fName,
					// fInfo.getException());
				}
			}
		}

		return inFile;
	}
}
