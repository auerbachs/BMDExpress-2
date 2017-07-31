/*
 * GenesBMDs.java     0.5    1/04/2007
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used to cross references bewteen probes, genes and GO terms.
 */

package com.sciome.bmdexpress2.util.categoryanalysis;

import java.util.Vector;

/**
 * The class for gene's BMD and BMDL values averaged based on values of probes per gene. Assort genes based on
 * BMD or BMDL values.
 *
 * @version 1.0 2/22/2008
 * @author Longlong Yang
 */
public class GenesBMDs
{
	private Vector<String>	vectGenes, bmdSortGenes, bmdlSortGenes;
	private int[]			counts, bmdIndices, bmdlIndices;
	private double[]		avgBMDs, avgBMDLs;
	private int				max;

	public GenesBMDs()
	{
	}

	public GenesBMDs(int n)
	{
		vectGenes = new Vector<String>(n);
		avgBMDs = new double[n];
		avgBMDLs = new double[n];
		counts = new int[n];
		// init(n);
	}

	public void addPerGeneValues(String gene, double bmd, double bmdl)
	{
		int idx = vectGenes.indexOf(gene);

		if (idx < 0)
		{
			vectGenes.add(gene);
			idx = vectGenes.indexOf(gene);

			// initialization arrays
			avgBMDs[idx] = 0;
			avgBMDLs[idx] = 0;
			counts[idx] = 0;
		}

		avgBMDs[idx] += bmd;
		avgBMDLs[idx] += bmdl;
		counts[idx] += 1;
	}

	public boolean ascendSortBMDandBMDLs()
	{
		max = vectGenes.size();

		if (max > 0)
		{
			bmdIndices = new int[max];
			bmdlIndices = new int[max];

			// System.out.println("BMD before sort:");
			avgBMDs = ascendSort(avgBMDs, bmdIndices);
			// System.out.println("BMDL before sort:");
			avgBMDLs = ascendSort(avgBMDLs, bmdlIndices);
			// System.out.println("BMD after sort:");
			bmdSortGenes = orderGenes(bmdIndices);
			// printOut(bmdSortGenes, avgBMDs);
			// System.out.println("BMDL after sort:");
			bmdlSortGenes = orderGenes(bmdlIndices);
			// printOut(bmdlSortGenes, avgBMDLs);
			vectGenes.removeAllElements();
			vectGenes = null;

			return true;
		}

		return false;
	}

	private double[] ascendSort(double[] values, int[] indices)
	{
		double[] sorted = new double[max];

		for (int i = 0; i < max; i++)
		{
			double value = values[i] / counts[i];
			// System.out.println(vectGenes.get(i) + "\t" + value);

			for (int j = i; j >= 0; j--)
			{
				if (j == 0 || value >= sorted[j - 1])
				{
					sorted[j] = value;
					indices[j] = i;
					break;
				}
				else
				{
					sorted[j] = sorted[j - 1];
					indices[j] = indices[j - 1];
				}
			}
		}

		return sorted;
	}

	private Vector<String> orderGenes(int[] indices)
	{
		Vector<String> orderedGenes = new Vector<String>(max);

		for (int i = 0; i < max && i < indices.length; i++)
		{
			if (indices[i] >= 0)
			{
				orderedGenes.add(vectGenes.get(indices[i]));
			}
		}

		return orderedGenes;
	}

	private void printOut(Vector<String> genes, double[] values)
	{
		for (int i = 0; i < max; i++)
		{
			System.out.println(genes.get(i) + "\t" + values[i]);
		}
	}

	public int maxGenes()
	{
		return max;
	}

	public int[] sortedBMDindices(Vector<String> genes)
	{
		return sortIndices(bmdSortGenes, genes);
	}

	public int[] sortedBMDLindices(Vector<String> genes)
	{
		return sortIndices(bmdlSortGenes, genes);
	}

	public double sortedBMDAt(int i)
	{
		return avgBMDs[i];
	}

	public double sortedBMDLAt(int i)
	{
		return avgBMDLs[i];
	}

	private int[] sortIndices(Vector<String> sortedGenes, Vector<String> subGenes)
	{
		int n = subGenes.size();
		int[] indices = new int[n];

		for (int i = 0; i < n; i++)
		{
			int idx = sortedGenes.indexOf(subGenes.get(i));

			for (int j = i; j >= 0; j--)
			{
				if (j == 0 || indices[j - 1] < idx)
				{
					indices[j] = idx;
					break;
				}
				else
				{
					indices[j] = indices[j - 1];
				}
			}
		}

		return indices;
	}
}
