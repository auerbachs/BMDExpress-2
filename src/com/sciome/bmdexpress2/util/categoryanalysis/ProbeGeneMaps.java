/*
 * ProbeGeneMaps     1.0    7/25/2008
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used for match probes to genes
 */

package com.sciome.bmdexpress2.util.categoryanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;
import com.sciome.bmdexpress2.util.annotation.FileInfo;
import com.sciome.bmdexpress2.util.annotation.URLUtils;

/**
 * The class of ProbeGeneMaps
 *
 * Input unique probes as Vector of Strings - uniqueProbes matched to unique genes as Vector of Strings -
 * subGenes
 *
 * Each gene as a key refers to probe(s) as Vector of Strings - subHashG2Ids
 *
 * @version 1.0 4/7/2008
 * @author Longlong Yang
 */
public class ProbeGeneMaps
{

	public String							httpURL, basePath, provider, chip, chipId, species;
	public String[]							probesChips;
	public String[][]						subG2Probes, arrayInfo = null;
	public Vector<String>					uniqueProbes, subGenes, allGenes, subAllProbes;
	public Hashtable<String, Integer>		probesHash;
	public Hashtable<String, Vector>		subHashG2Ids;

	private Map<String, ProbeStatResult>	statResultMap		= new HashMap<>();
	private Map<String, ReferenceGene>		referenceGeneMap	= new HashMap<>();
	private ChipInfo						chipInfo;

	private final int						BATCHMAX			= 2000;
	protected BMDResult						bmdResults;
	protected Set<String>					geneSet				= new HashSet<>();
	protected Vector<String>				dataSetGenes		= null;

	public ProbeGeneMaps(BMDResult bmdResults)
	{
		this.chipInfo = bmdResults.getDoseResponseExperiment().getChip();
		this.bmdResults = bmdResults;
		getAnnotationToCategoryCounts(bmdResults.getDoseResponseExperiment());

	}

	public void setProbesHash(Hashtable<String, Integer> hash)
	{
		probesHash = hash;
	}

	public String[] getProbesChips()
	{
		return probesChips;
	}

	/**
	 * An anlternative and more efficient function of readUniqueProbes w/o checking uniqueness. Assumming
	 * probes in input matrix are unique
	 */
	public void readProbes(boolean custom)
	{

		uniqueProbes = new Vector<String>(bmdResults.getProbeStatResults().size());

		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			String probe = probeStatResult.getProbeResponse().getProbe().getId();
			uniqueProbes.add(probe);
			statResultMap.put(probe, probeStatResult);
		}

		// if there are not any custom probe2gene mappings,
		// use that which is asociated with the probe ids in the dose response experiment
		if (!custom)
		{
			if (bmdResults.getDoseResponseExperiment().getReferenceGeneAnnotations() != null)
			{
				for (ReferenceGeneAnnotation referenceAnnotation : bmdResults.getDoseResponseExperiment()
						.getReferenceGeneAnnotations())
				{
					for (ReferenceGene referenceGene : referenceAnnotation.getReferenceGenes())
					{
						referenceGeneMap.put(referenceGene.getId(), referenceGene);
					}
				}
			}
		}
	}

	public Map<String, ReferenceGene> getReferenceGeneMap()
	{
		return referenceGeneMap;
	}

	public Map<String, ProbeStatResult> getStatResultMap()
	{
		return statResultMap;
	}

	public void readUniqueProbes()
	{

		uniqueProbes = new Vector<String>(bmdResults.getProbeStatResults().size());

		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			String probe = probeStatResult.getProbeResponse().getProbe().getId();

			if (probe != null && !uniqueProbes.contains(probe))
			{
				uniqueProbes.add(probe);
			}
		}
	}

	public void setUniqueProbes(Vector<String> probes)
	{
		uniqueProbes = probes;
	}

	/**
	 *
	 * Match probes to gene Ids. The hash table will keep the probes per gene as key and probes as value
	 *
	 * Need to send all probes one time to services and return a matrix Instead of one query per probe.
	 *
	 * @param probes
	 *            is a String Vector of probes
	 */
	public void probeGeneMaping(String chip, boolean toAll)
	{
		this.chip = chip;
		subGenes = new Vector<String>();
		allGenes = new Vector<String>();
		subHashG2Ids = new Hashtable<String, Vector>();
		probes2GeneIds(uniqueProbes, toAll);

	}

	private void filedGeneIds2Symbols(Vector<String> geneIds, String[][] geneSymbols)
	{
		//
	}

	public Double percentage(int numorator, int denominator, int decimals)
	{
		double hundred = 100.0;
		Double percent = null;

		try
		{
			double ratio = numorator * hundred / denominator;
			ratio = NumberManager.numberFormat(decimals, ratio);
			percent = new Double(ratio);// numorator * hundred / denominator);
		}
		catch (Exception e)
		{}

		return percent;
	}

	public String vectorGenes2String(Vector<String> genes)
	{
		StringBuffer bf = new StringBuffer();

		for (int i = 0; i < genes.size(); i++)
		{
			if (i > 0)
			{
				bf.append(BMDExpressConstants.getInstance().SEMICOLON);
			}

			bf.append(genes.get(i));
		}

		return bf.toString();
	}

	public String genesProbes2String(Vector<String> genes)
	{
		StringBuffer bf = new StringBuffer();

		for (int i = 0; i < genes.size(); i++)
		{
			String gene = genes.get(i);
			Vector<String> probes = subHashG2Ids.get(gene);

			if (i > 0)
			{
				bf.append(BMDExpressConstants.getInstance().SEMICOLON);
			}

			for (int j = 0; j < probes.size(); j++)
			{
				if (j > 0)
				{
					bf.append(BMDExpressConstants.getInstance().COMMA);
				}

				bf.append(probes.get(j));
			}
		}

		return bf.toString();
	}

	public Map<String, List<ProbeStatResult>> genesProbes2ProbeStatList(Vector<String> genes)
	{
		StringBuffer bf = new StringBuffer();

		Map<String, List<ProbeStatResult>> returnMap = new HashMap<>();

		for (int i = 0; i < genes.size(); i++)
		{
			String gene = genes.get(i);
			Vector<String> probes = subHashG2Ids.get(gene);

			if (i > 0)
			{
				bf.append(BMDExpressConstants.getInstance().SEMICOLON);
			}

			List<ProbeStatResult> probeStatList = new ArrayList<>();
			for (int j = 0; j < probes.size(); j++)
			{
				probeStatList.add(this.statResultMap.get(probes.get(j)));
			}

			returnMap.put(genes.get(i), probeStatList);
		}

		return returnMap;
	}

	/**
	 * File Based implementation below
	 *
	 * Fields: arrayId, arrayName, provider, species
	 */
	public void readArraysInfo()
	{
		String fName = BMDExpressConstants.getInstance().MICROARRAYGZ;
		String http = BMDExpressProperties.getInstance().getUpdateURL() + "/arrays/" + fName;

		try
		{
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ BMDExpressConstants.getInstance().ARRAYDIR;
			File inFile = checkDownload(http, filePath, fName);

			BufferedReader reader = new BufferedReader(
					new InputStreamReader(new GZIPInputStream(new FileInputStream(inFile))));
			String line = reader.readLine();

			if (line != null && !line.isEmpty())
			{
				try
				{
					int n = Integer.parseInt(line);
					arrayInfo = new String[n][];
					probesChips = new String[n];
					int i = 0;

					while ((line = reader.readLine()) != null)
					{
						if (!line.isEmpty() && i < n)
						{
							arrayInfo[i] = line.split(BMDExpressConstants.getInstance().TAB);

							if (arrayInfo[i].length > 1)
							{
								probesChips[i] = arrayInfo[i][1];
							}
						}

						i++;
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
			// e.printStackTrace();
		}
	}

	/*
	 * match probes to genes from annotation file
	 *
	 * @param probes is a Vecotor of String of unique probes
	 */
	private void filedProbes2GeneIds(Vector<String> probes, boolean toAll)
	{

		chipId = chipInfo.getId();
		provider = chipInfo.getProvider();
		species = chipInfo.getSpecies();
		String fName = "probe2gene.gz";

		String http = BMDExpressProperties.getInstance().getUpdateURL() + "/arrays/" + provider + "/"
				+ chipInfo.getGeoID() + "/" + fName;

		try
		{
			String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator
					+ BMDExpressConstants.getInstance().ARRAYDIR + "/" + provider + "/" + chipInfo.getGeoID()
					+ "/";
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
							String[] probeGenes = line.split(";");

							if (probeGenes.length > 1 && probesHash.containsKey(probeGenes[0]))
							{
								String[] genes = probeGenes[1].split(BMDExpressConstants.getInstance().TAB);

								for (int j = 0; j < genes.length; j++)
								{
									if (toAll && !allGenes.contains(genes[j]))
									{
										allGenes.add(genes[j]);
									}

									if (probes.contains(probeGenes[0]))
									{
										if (!subGenes.contains(genes[j]))
										{
											subGenes.add(genes[j]);
											subHashG2Ids.put(genes[j], new Vector<String>());
										}

										subHashG2Ids.get(genes[j]).add(probeGenes[0]);
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

			this.dataSetGenes = new Vector<String>();
			for (String gene : allGenes)
				dataSetGenes.add(gene);
			// check the geneSet...if it's not empty, then use it
			if (geneSet != null && geneSet.size() > 0)
			{
				allGenes.clear();
				for (String gene : geneSet)
					allGenes.add(gene);
			}

			reader.close();
			filedSubGenesAllProbes(inFile);
		}
		catch (Exception e)
		{
			// e.printStackTrace();
		}

	}

	private void filedSubGenesAllProbes(File inFile)
	{
		int size = subGenes.size();
		subG2Probes = new String[size][];
		subAllProbes = new Vector<String>(size);
		Vector<String>[] vectProbes = new Vector[size];

		for (int i = 0; i < size; i++)
		{
			vectProbes[i] = new Vector<String>();
		}

		try
		{
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
							String[] probeGenes = line.split(";");
							String probe = probeGenes[0];

							if (probeGenes.length > 1 && probesHash.containsKey(probe))
							{
								String[] genes = probeGenes[1].split(BMDExpressConstants.getInstance().TAB);

								for (int j = 0; j < genes.length; j++)
								{
									int idx = subGenes.indexOf(genes[j]);

									if (idx > -1)
									{
										if (!subAllProbes.contains(probe))
										{
											subAllProbes.add(probe);
										}

										vectProbes[idx].add(probe);
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
			// e.printStackTrace();
		}

		for (int i = 0; i < size; i++)
		{
			String[] probes = new String[vectProbes[i].size()];
			subG2Probes[i] = vectProbes[i].toArray(probes);
		}
	}

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
				{}
			}
		}

		return inFile;
	}

	/**
	 * @param subPath
	 *            is a relative path under the 'annotations' directory, keep the same structure for both
	 *            remote server and local
	 * @param fName
	 *            is the name of the file requested
	 *
	 * @return the file of given name
	 */
	public File getFile(String subPath, String fName)
	{
		String http = httpURL + subPath + fName;
		String filePath = BMDExpressConstants.getInstance().ANNOTATION_BASE_PATH + File.separator + subPath;

		return checkDownload(http, filePath, fName);
	}

	/**
	 * public functions to return data of a type
	 *
	 * @param is
	 *            no
	 * @return the species
	 */
	public String getSpecies()
	{
		if (chipInfo == null)
			return "Generic";
		return chipInfo.getSpecies();
	}

	public int subTotalGenes()
	{
		return subGenes.size();
	}

	public int totalGenes()
	{
		return allGenes.size();
	}

	public Vector<String> getUniqueProbes()
	{
		return uniqueProbes;
	}

	public Vector<String> probesGenes()
	{
		return subGenes;
	}

	public Vector<String> getAllGenes()
	{
		return allGenes;
	}

	public Vector<String> subAllProbes()
	{
		return subAllProbes;
	}

	public Hashtable<String, Vector> subHashG2Ids()
	{
		return subHashG2Ids;
	}

	public String[] subG2ProbesAt(int r)
	{
		return subG2Probes[r];
	}

	public String[][] subG2Probes()
	{
		return subG2Probes;
	}

	/**
	 * Get value of arrayInfo at[row, col]
	 */
	public String arrayInfoAt(int row, int col)
	{
		return arrayInfo[row][col];
	}

	public int arrayCount()
	{
		return arrayInfo.length;
	}

	/*
	 * match probes to genes from annotation file
	 *
	 * @param probes is a Vecotor of String of unique probes
	 */
	private void probes2GeneIds(Vector<String> probes, boolean toAll)
	{

		if (bmdResults.getDoseResponseExperiment().getReferenceGeneAnnotations() != null)
		{
			for (ReferenceGeneAnnotation referenceGeneAnnotation : bmdResults.getDoseResponseExperiment()
					.getReferenceGeneAnnotations())
			{

				if (!probesHash.containsKey(referenceGeneAnnotation.getProbe().getId()))
					continue;
				for (ReferenceGene referenceGene : referenceGeneAnnotation.getReferenceGenes())
				{
					if (toAll && !allGenes.contains(referenceGene.getId()))
					{
						allGenes.add(referenceGene.getId());
					}

					if (probes.contains(referenceGeneAnnotation.getProbe().getId()))
					{
						if (!subGenes.contains(referenceGene.getId()))
						{
							subGenes.add(referenceGene.getId());
							subHashG2Ids.put(referenceGene.getId(), new Vector<String>());
						}

						subHashG2Ids.get(referenceGene.getId())
								.add(referenceGeneAnnotation.getProbe().getId());
					}
				}
			}
		}

		// check the geneSet...if it's not empty, then use it
		this.dataSetGenes = new Vector<String>();
		for (String gene : allGenes)
			dataSetGenes.add(gene);
		if (geneSet != null && geneSet.size() > 0)
		{
			allGenes.clear();
			for (String gene : geneSet)
				allGenes.add(gene);
		}

		subGenesAllProbes();
	}

	private void subGenesAllProbes()
	{
		int size = subGenes.size();
		subG2Probes = new String[size][];
		subAllProbes = new Vector<String>(size);
		Vector<String>[] vectProbes = new Vector[size];

		for (int i = 0; i < size; i++)
		{
			vectProbes[i] = new Vector<String>();
		}

		if (bmdResults.getDoseResponseExperiment().getReferenceGeneAnnotations() != null)
		{
			for (ReferenceGeneAnnotation referenceGeneAnnotation : bmdResults.getDoseResponseExperiment()
					.getReferenceGeneAnnotations())
			{
				if (probesHash.containsKey(referenceGeneAnnotation.getProbe().getId()))
				{
					for (ReferenceGene referenceGene : referenceGeneAnnotation.getReferenceGenes())
					{
						int idx = subGenes.indexOf(referenceGene.getId());

						if (idx > -1)
						{
							if (!subAllProbes.contains(referenceGeneAnnotation.getProbe().getId()))
							{
								subAllProbes.add(referenceGeneAnnotation.getProbe().getId());
							}

							vectProbes[idx].add(referenceGeneAnnotation.getProbe().getId());
						}
					}
				}
			}
		}

		for (int i = 0; i < size; i++)
		{
			String[] probes = new String[vectProbes[i].size()];
			subG2Probes[i] = vectProbes[i].toArray(probes);
		}
	}

	/*
	 * If a prefilter was applied prior to uploading data, then the percentages will be off, int terms of All
	 * genes We need to get an accurate reflection of the counts of genes in each category
	 */
	private void getAnnotationToCategoryCounts(DoseResponseExperiment de)
	{
		FileAnnotation fileAnnotation = new FileAnnotation();
		fileAnnotation.readArraysInfo();
		fileAnnotation.setChip(de.getChip().getGeoID());

		fileAnnotation.arrayProbesGenes();
		fileAnnotation.arrayGenesSymbols();

		fileAnnotation.getGene2ProbeHash();

		geneSet = fileAnnotation.getGeneSet();

	}

	public Vector<String> getDataSetGenes()
	{
		return dataSetGenes;
	}

	public void setDataSetGenes(Vector<String> dataSetGenes)
	{
		this.dataSetGenes = dataSetGenes;
	}

	public void removePromiscuousProbes(Hashtable<String, Vector> paramHashtable)
	{
		Vector localVector1 = new Vector();
		Iterator localIterator = uniqueProbes.iterator();
		while (localIterator.hasNext())
		{
			String str = (String) localIterator.next();
			Vector localVector2 = paramHashtable.get(str);
			if ((localVector2 != null) && (localVector2.size() == 1))
			{
				localVector1.add(str);
			}
		}
		uniqueProbes = localVector1;
	}

}
