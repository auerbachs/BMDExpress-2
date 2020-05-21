/*
 * GenesPathways.java     1.0    7/25/2008
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used for match genes to pathways
 */

package com.sciome.bmdexpress2.util.categoryanalysis.catmap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;
//import java.util.Date;
import java.util.zip.GZIPInputStream;

import com.sciome.bmdexpress2.mvp.model.category.identifier.CategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.category.identifier.GenericCategoryIdentifier;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.FileIO;
import com.sciome.bmdexpress2.util.categoryanalysis.ProbeGeneMaps;

/**
 * The class of GenesPathways
 *
 * match genes to pathways
 *
 * @version 1.0 4/7/2008
 * @author Longlong Yang
 */
public class GenesPathways extends CategoryMapBase
{

	/**
	 * class contructor used for gene's pathways
	 *
	 */
	public GenesPathways()
	{
	}

	public GenesPathways(ProbeGeneMaps probeGeneMaps, String pathwayDb)
	{
		this.probeGeneMaps = probeGeneMaps;
		fileGenes2Maps(pathwayDb);
		filePathwayTitles(pathwayDb);
	}

	private void fileGenes2Maps(String pathwayDb)
	{
		readTaxonomyInfo();

		// for (int i = 0; i <
		Vector<String> subGenes = probeGeneMaps.probesGenes();
		Vector<String> allGenes = probeGeneMaps.getAllGenes();
		Vector<String> dataSetGenes = probeGeneMaps.getDataSetGenes();

		HashSet<String> allGHashSet = new HashSet(allGenes);

		subHash = new Hashtable<String, Vector>(); // key = map, Vecotor = genes
		allHash = new Hashtable<String, Vector>(); // key = map, Vecotor = genes
		dataSetGeneHash = new Hashtable<String, Vector>();

		String fName = organismCode + BMDExpressConstants.getInstance().KEGGFILES[2];
		String relativePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + File.separator + pathwayDb
				+ File.separator + BMDExpressConstants.getInstance().PATHWAYDIRS[1] + File.separator
				+ organismCode + File.separator;
		String httpPath = BMDExpressProperties.getInstance().getUpdateURL()
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + "/" + pathwayDb + "/"
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[1] + "/" + organismCode + "/" + fName;

		File inFile = checkDownload(httpPath, relativePath, fName);

		if (inFile != null)
		{
			try
			{
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
				String line;

				while ((line = reader.readLine()) != null)
				{
					String[] geneMaps = line.split(BMDExpressConstants.getInstance().TAB);

					if (geneMaps != null && geneMaps.length > 1)
					{
						String pathwayID = geneMaps[1];

						if (allGHashSet.contains(geneMaps[0]))
						{
							String[] maps = pathwayID.split(" ");

							if (maps != null)
							{
								for (int j = 0; j < maps.length; j++)
								{
									String mapName = maps[j];
									if (pathwayDb.equals("REACTOME"))
										mapName = "R-" + organismCode.toUpperCase() + "-" + mapName;

									if (allGenes.contains(geneMaps[0]))
									{
										if (!allHash.containsKey(mapName))
										{
											allHash.put(mapName, new Vector<String>());
										}

										if (!allHash.get(mapName).contains(geneMaps[0]))
										{
											allHash.get(mapName).add(geneMaps[0]);
										}
									}

									if (dataSetGenes.contains(geneMaps[0]))
									{
										if (!dataSetGeneHash.containsKey(mapName))
										{
											dataSetGeneHash.put(mapName, new Vector<String>());
										}

										if (!dataSetGeneHash.get(mapName).contains(geneMaps[0]))
										{
											dataSetGeneHash.get(mapName).add(geneMaps[0]);
										}
									}

									if (subGenes.contains(geneMaps[0]))
									{
										if (!subHash.containsKey(mapName))
										{
											subHash.put(mapName, new Vector<String>());
										}

										if (!subHash.get(mapName).contains(geneMaps[0]))
										{
											subHash.get(mapName).add(geneMaps[0]);
										}
									}
								}
							}
						}
					}
				}
			}
			catch (IOException e)
			{
				// System.out.println("Read preferences problem: " + e);
				e.printStackTrace();
			}
		}
	}

	private void readTaxonomyInfo()
	{
		String species = probeGeneMaps.getSpecies();
		String fName = BMDExpressConstants.getInstance().KEGGFILES[0];
		String relativePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + File.separator;

		String httpPath = BMDExpressProperties.getInstance().getUpdateURL() + "/"
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + "/" + fName;
		File inFile = checkDownload(httpPath, relativePath, fName);

		if (inFile != null)
		{
			Vector<String> input = FileIO.readVectorString(inFile);

			for (int i = 0; i < input.size(); i++)
			{
				String[] array = input.get(i).split(BMDExpressConstants.getInstance().TAB);

				if (array[0].startsWith(species) || species.startsWith(array[0]))
				{
					organismCode = array[1];
					break;
				}
			}
		}
	}

	private void filePathwayTitles(String pathwayDb)
	{
		String fName = BMDExpressConstants.getInstance().KEGGFILES[1];
		String relativePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + File.separator + pathwayDb
				+ File.separator;

		String httpPath = BMDExpressProperties.getInstance().getUpdateURL()
				+ BMDExpressConstants.getInstance().PATHWAYDIRS[0] + "/" + pathwayDb + "/" + fName;
		File inFile = checkDownload(httpPath, relativePath, fName);

		if (inFile != null)
		{
			try
			{
				Vector<CategoryIdentifier> vectGos = new Vector<CategoryIdentifier>(subHash.size());
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
				String line;
				titleHash = new Hashtable<String, String>();

				while ((line = reader.readLine()) != null)
				{
					String[] values = line.split(BMDExpressConstants.getInstance().TAB);

					String pathwayID = values[0];
					if (pathwayDb.equals("REACTOME"))
						pathwayID = "R-" + organismCode.toUpperCase() + "-" + pathwayID;
					if (values != null && values.length > 1)
					{
						titleHash.put(pathwayID, values[1]);

						if (subHash.containsKey(pathwayID))
						{
							// goLevelTerms[i++] = goValues;
							CategoryIdentifier gCatID = new GenericCategoryIdentifier();
							gCatID.setId(pathwayID);
							gCatID.setTitle(values[1]);
							vectGos.add(gCatID);

						}
					}
				}
				categoryIdentifiers = vectGos;
			}
			catch (IOException e)
			{
				// System.out.println("Read preferences problem: " + e);
				e.printStackTrace();
			}
		}
	}

	public String getTitle(String id)
	{
		return titleHash.get(id);
	}

	public String organismCode()
	{
		return organismCode;
	}

}