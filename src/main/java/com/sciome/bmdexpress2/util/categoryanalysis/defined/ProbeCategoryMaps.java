/*
 * ProbeCategoryMaps     1.0    7/25/2008
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used for match probes to genes
 */

package com.sciome.bmdexpress2.util.categoryanalysis.defined;

import java.io.File;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.refgene.CustomGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.categoryanalysis.ProbeGeneMaps;

/**
 * The class of ProbeCategoryMaps
 *
 * Input unique probes as Vector of Strings - uniqueProbes matched to unique genes as Vector of Strings -
 * subGenes
 *
 * Each gene as a key refers to probe(s) as Vector of Strings - subHashG2Ids
 *
 * @version 1.0 4/7/2008
 * @author Longlong Yang
 */
public class ProbeCategoryMaps extends ProbeGeneMaps
{
	private File						probeMapFile;
	// private CategoryTool categoryTool;
	public Hashtable<String, Integer>	probesHash;

	public ProbeCategoryMaps(BMDResult bmdResults)
	{
		super(bmdResults);
	}

	public void setProbesHash(Hashtable<String, Integer> hash)
	{
		probesHash = hash;
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
	public void probeGeneMaping(int p, int c, MatrixData mData)
	{
		subGenes = new Vector<String>();
		allGenes = new Vector<String>();
		subHashG2Ids = new Hashtable<String, Vector>();
		int rows = mData.rows();

		Set<String> genesFromDataset = new HashSet<>();

		for (int i = 0; i < rows; i++)
		{
			String probe = mData.valueAt(i, p).toString();
			String component = mData.valueAt(i, c).toString();
			if (!allGenes.contains(component))
			{
				allGenes.add(component);

				if (!this.getReferenceGeneMap().containsKey(component))
				{
					ReferenceGene refGene = new CustomGene();
					refGene.setId(component);
					this.getReferenceGeneMap().put(component, refGene);
				}
			}
			if (probesHash.containsKey(probe))
			{
				genesFromDataset.add(component);
				if (uniqueProbes.contains(probe))
				{
					if (!subGenes.contains(component))
					{
						subGenes.add(component);
						subHashG2Ids.put(component, new Vector<String>());
					}

					subHashG2Ids.get(component).add(probe);
				}
			}
		}

		// keep track of the number of genes from this dataset
		this.dataSetGenes = new Vector<>(genesFromDataset);
		subGenesAllProbes(p, c, mData);
	}

	/**
	 * Step 2
	 *
	 * Match a vector of genes to probes of whole array/chip
	 */
	private void subGenesAllProbes(int p, int c, MatrixData mData)
	{
		int size = subGenes.size();
		// System.out.println("subGenesAllProbes: " + size);
		subG2Probes = new String[size][];
		subAllProbes = new Vector<String>(size);
		Vector<String>[] vectProbes = new Vector[size];
		int rows = mData.rows();

		for (int i = 0; i < size; i++)
		{
			vectProbes[i] = new Vector<String>();
		}

		for (int i = 0; i < rows; i++)
		{
			String probe = mData.valueAt(i, p).toString();
			String component = mData.valueAt(i, c).toString();
			int idx = subGenes.indexOf(component);

			if (idx >= 0)
			{
				if (!subAllProbes.contains(probe))
				{
					subAllProbes.add(probe);
				}

				vectProbes[idx].add(probe);
			}
		}

		for (int i = 0; i < size; i++)
		{
			String[] probes = new String[vectProbes[i].size()];
			subG2Probes[i] = vectProbes[i].toArray(probes);
		}
	}
}
