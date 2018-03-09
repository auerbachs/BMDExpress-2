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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
//import java.util.Date;
import java.util.zip.GZIPInputStream;

import com.sciome.bmdexpress2.mvp.model.category.identifier.CategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.category.identifier.GOCategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.bmdexpress2.util.categoryanalysis.ProbeGeneMaps;

/**
 * The class of GenesGOTerms
 *
 * match genes to pathways
 *
 * @version 1.0 4/7/2008
 * @author Longlong Yang
 */
public class GOTermMap extends CategoryMapBase
{

	private int						goTermIdx	= 0;

	public static final String[]	folders		= { "go", "arrays" };

	/**
	 * class contructor used for gene's pathways
	 *
	 */
	public GOTermMap()
	{
	}

	public GOTermMap(ProbeGeneMaps probeGeneMaps, ChipInfo chipInfo, int goTermIdx)
	{
		super(probeGeneMaps, chipInfo);
		this.goTermIdx = goTermIdx;

		geneIdsToGo("all");

		goAccLevelTerm();
	}

	/**
	 * Step 2
	 *
	 * Depends on probes2GeneIds(..) above
	 */
	private void geneIdsToGo(String goAll)
	{
		Vector<String> allGenes = probeGeneMaps.getAllGenes();
		Vector<String> dataSetGenes = probeGeneMaps.getDataSetGenes();
		HashSet<String> allGHashSet = new HashSet<>(allGenes);
		// System.out.println("allGHashSet = " + allGHashSet.size());

		Vector<String> subGenes = probeGeneMaps.probesGenes();
		HashSet<String> subGHashSet = new HashSet<>(subGenes);
		HashSet<String> dataSetGenesHashSet = new HashSet<>(dataSetGenes);

		int size = subGenes.size();
		subHash = new Hashtable<String, Vector>(size / 2);
		allHash = new Hashtable<String, Vector>(size * 3 / 4);
		dataSetGeneHash = new Hashtable<String, Vector>();

		filedGeneIdsToGo(allGHashSet, subGHashSet, dataSetGenesHashSet);

		if (!allHash.containsKey(goAll)
				&& allHash.containsKey(BMDExpressConstants.getInstance().GO_CATEGORIES[0]))
		{
			allHash.put(goAll, allHash.get(BMDExpressConstants.getInstance().GO_CATEGORIES[0]));
		}

		if (goTermIdx != 0)
		{
			subHash.remove(goAll);
		}
	}

	/**
	 * Step 4
	 *
	 * Search GO category term counts of all genes given an array
	 */
	public boolean arrayGeneGoHash(String goAll)
	{
		allHash = new Hashtable<String, Vector>(subHash.size() + subHash.size() / 4);

		filedArrayGeneGoHash();

		if (allHash.size() > 0)
		{
			Enumeration<String> keys = allHash.keys();

			while (keys.hasMoreElements())
			{
				String key = keys.nextElement();
				Vector<String> vectGenes = allHash.get(key);

				if (vectGenes != null)
				{
					int n = vectGenes.size();
					vectGenes.removeAllElements();
					vectGenes.add(Integer.toString(n));
				}
			}
		}

		if (!allHash.containsKey(goAll)
				&& allHash.containsKey(BMDExpressConstants.getInstance().GO_CATEGORIES[0]))
		{
			allHash.put(goAll, allHash.get(BMDExpressConstants.getInstance().GO_CATEGORIES[0]));
		}

		return allHash.size() > 0;
	}

	/**
	 * Step 3
	 *
	 * Depends on geneIdsToGo(.) above
	 */
	private void goAccLevelTerm()
	{

		filedGoAccLevelTerm();
	}

	private void filedGeneIdsToGo(HashSet<String> allGHashSet, HashSet<String> subGHashSet,
			HashSet<String> dataSetGenesHashSet)
	{

		try
		{
			String fName = "genes2gos.gz";
			String http = BMDExpressProperties.getInstance().getUpdateURL() + "/arrays/"
					+ chipInfo.getProvider() + "/" + chipInfo.getGeoID() + "/" + fName;
			// System.out.println("URL: " + http);
			Vector<String> geneIds = new Vector<String>(probeGeneMaps.subTotalGenes());
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ folders[1] + File.separator + chipInfo.getProvider() + File.separator
					+ chipInfo.getGeoID() + File.separator;
			File inFile = checkDownload(http, filePath, fName);

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
			String line = reader.readLine();

			if (line != null && !line.isEmpty())
			{
				try
				{
					int n = Integer.parseInt(line);

					while ((line = reader.readLine()) != null)
					{
						if (!line.isEmpty())
						{
							String[] geneGos = line.split(";");

							if (geneGos.length > 1)
							{
								String geneId = geneGos[0];

								if (allGHashSet.contains(geneId))
								{
									String[] goAccs = geneGos[1].split(BMDExpressConstants.getInstance().TAB);
									int cnt = 0;

									for (int j = 0; j < goAccs.length; j++)
									{
										// goAccs[j] ia paired [accession,category]
										String[] goPairs = goAccs[j].split(",");

										if (goPairs != null && goPairs.length > 1)
										{
											int category = NumberManager.parseInt(goPairs[1], -1);

											if (goTermIdx == 0 || goTermIdx == category)
											{
												addToHash(allHash, goPairs[0], geneId);

												if (dataSetGenesHashSet.contains(geneId))
												{
													addToHash(dataSetGeneHash, goPairs[0], geneId);
												}
												if (subGHashSet.contains(geneId))
												{
													addToHash(subHash, goPairs[0], geneId);

													// if (!subHash.containsKey(goPairs[0])) {
													// subHash.put(goPairs[0], new Vector<String>());
													// }

													// subHash.get(goPairs[0]).add(geneId);
													cnt++;
												}
											}
										}
									}

									/* keep only genes with GO terms */
									if (cnt > 0)
									{
										geneIds.add(geneId);
									}
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					// e.printStackTrace();
				}
			}

			reader.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void filedGoAccLevelTerm()
	{
		int size = subHash.size();
		// System.out.println("subHash = " + size);
		String fName = "gotermlevel.gz";
		String http = BMDExpressProperties.getInstance().getUpdateURL() + "/go/" + fName;
		// System.out.println("URL: " + http);
		categoryHash = new Hashtable<String, String>();

		for (int i = 0; i < BMDExpressConstants.getInstance().GO_CATEGORIES.length; i++)
		{
			categoryHash.put(BMDExpressConstants.getInstance().GO_CATEGORIES[i],
					BMDExpressConstants.getInstance().GO_CATEGORIES[i]);
		}

		try
		{
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ folders[0] + File.separator;
			File inFile = checkDownload(http, filePath, fName);

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
			String line = reader.readLine();

			if (line != null && !line.isEmpty())
			{
				try
				{
					int n = Integer.parseInt(line);
					int i = 0;
					Vector<CategoryIdentifier> vectGos = new Vector<CategoryIdentifier>(size);

					while ((line = reader.readLine()) != null)
					{
						if (!line.isEmpty())
						{
							String[] goValues = line.split("\t");

							if (goValues.length > 1 && subHash.containsKey(goValues[0]))
							{
								// goLevelTerms[i++] = goValues;
								GOCategoryIdentifier gCatID = new GOCategoryIdentifier();
								gCatID.setId(goValues[0]);
								gCatID.setTitle(goValues[2]);
								gCatID.setGoLevel(goValues[1]);
								vectGos.add(gCatID);

							}

							if (categoryHash.containsKey(goValues[2]))
							{
								categoryHash.put(goValues[2], goValues[0]);
							}
						}
					}

					categoryIdentifiers = vectGos;
					// System.out.println(size + " gotermlevel " + goLevelTerms.length);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

			reader.close();
		}
		catch (Exception e)
		{}
	}

	private void filedArrayGeneGoHash()
	{
		String fName = "genes2gos.gz";

		try
		{
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ folders[1] + File.separator + chipInfo.getProvider() + File.separator
					+ chipInfo.getGeoID() + File.separator;
			// File inFile = new File(filePath);
			// inFile = new File(inFile.getAbsolutePath(), fName);
			File inFile = new File(filePath, fName);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
			String line = reader.readLine();

			if (line != null && !line.isEmpty())
			{
				try
				{
					int n = Integer.parseInt(line);

					while ((line = reader.readLine()) != null)
					{
						if (!line.isEmpty())
						{
							String[] geneGos = line.split(";");

							if (geneGos.length > 1)
							{
								String geneId = geneGos[0];
								String[] goAccs = geneGos[1].split(BMDExpressConstants.getInstance().TAB);

								for (int j = 0; j < goAccs.length; j++)
								{
									// goAccs[j] is "accession,category"
									String[] goPairs = goAccs[j].split(",");
									int category = NumberManager.parseInt(goPairs[1], -1);

									if (goTermIdx == 0 || goTermIdx == category)
									{
										if (subHash.containsKey(goPairs[0]))
										{
											if (!allHash.containsKey(goPairs[0]))
											{
												allHash.put(goPairs[0], new Vector<String>());
											}

											allHash.get(goPairs[0]).add(geneId);
										}
									}
								}
							}
						}
					}
				}
				catch (Exception e)
				{
					// e.printStackTrace();
					// parent.showException("Read From File - " + fName, e);
				}
			}

			reader.close();
		}
		catch (Exception e)
		{}
	}

	/**
	 * Add (key, value) to the hashtable
	 */
	private void addToHash(Hashtable<String, Vector> hash, String key, String value)
	{
		Vector<String> vect = hash.get(key);

		// if (!hash.containsKey(key)) {
		if (vect == null)
		{
			vect = new Vector<String>();
			hash.put(key, vect);
		}

		// hash.get(key).add(value);
		vect.add(value);
	}

	public String goTerm2Accession(String category)
	{
		String goAcc = null;

		goAcc = categoryHash.get(category);

		return goAcc;
	}

}
