/*
 * ArrayAnnotation.java     1.0    5/18/2009
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used for match all probes to genes and symbols
 */

package com.sciome.bmdexpress2.util.annotation;

import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;

/**
 * The class of ArrayAnnotation
 *
 * Input chip name, all probes Matched all probes to genes and symbols
 *
 * @version 1.0 5/18/2009
 * @author Eric Healy, Longlong Yang
 */
public class ArrayAnnotation
{
	public String						chip, chipId, provider, species;
	public Hashtable<String, Integer>	probesHash;
	public Hashtable<String, Vector>	probe2GeneHash, gene2ProbeHash;
	public Hashtable<String, String>	gene2SymbolHash;
	public ChipInfo						chipInfo;

	private String[][]					columnNames	= { { "Probe Set ID", "Enrez Genes", "Gene Symbols" },
			{ "Enrez Gene", "Gene Symbol", "Probe Set IDs" } };

	public static final String			newLine		= "\n",
												tab = "\t", semiColon = ";", comma = ",";

	public ArrayAnnotation()
	{
	}

	public void setProbesHash(Hashtable<String, Integer> hash)
	{
		probesHash = hash;
	}

	/**
	 * Need Override by subclass
	 *
	 * Assign probe2GeneHash and gene2ProbeHash objects public void arrayProbesGenes() { }
	 */

	/**
	 * Need Override by subclass
	 *
	 * For annotation of gene2SymbolHash public void arrayGenesSymbols() { }
	 * 
	 * public Integer probeIndexObject(String probe) { return probesHash.get(probe); }
	 * 
	 * public int indexOfProbe(String probe) { return probesHash.get(probe).intValue(); }
	 */

	public boolean hasProbe(String probe)
	{
		return probe2GeneHash.containsKey(probe);
	}

	public Vector<String> getGenesOfProbe(String probe)
	{
		return probe2GeneHash.get(probe);
	}

	public Vector<String> getProbesOfGene(String gene)
	{
		return gene2ProbeHash.get(gene);
	}

	public Hashtable<String, Vector> getProbe2GeneHash()
	{
		return probe2GeneHash;
	}

	public Hashtable<String, Vector> getGene2ProbeHash()
	{
		return gene2ProbeHash;
	}

	public Hashtable<String, String> getGene2SymbolHash()
	{
		return gene2SymbolHash;
	}

	/**
	 * (gene, symbol) as (key, value)
	 *
	 * @params return symbol as value given gene as key
	 */
	public String gene2Symbol(String gene)
	{
		String symbol = gene2SymbolHash.get(gene);

		if (symbol == null)
		{
			symbol = gene;
		}

		return symbol;
	}

	/**
	 * Only works for Affymetrix and Agilent arrays
	 */
	private boolean isControlGene(String probe)
	{
		boolean isControl = false;

		if ((provider.equals("Affymetrix") && probe.startsWith("AFFX"))
				|| (provider.equals("Agilent") && !probe.startsWith("A_")))
		{
			isControl = true;
		}

		return isControl;
	}

	public void setChip(String chip)
	{
		this.chip = chip;
	}

	public String getChipId()
	{
		return chipId;
	}

	public String getSpecies()
	{
		return species;
	}

	public String getProvider()
	{
		return provider;
	}

	public String vectorGenes2String(Vector<String> genes)
	{
		StringBuffer bf = new StringBuffer(genes.get(0));

		for (int i = 1; i < genes.size(); i++)
		{
			bf.append(semiColon + genes.get(i));
		}

		return bf.toString();
	}

	public String genes2SybmolsString(Vector<String> genes)
	{
		StringBuffer bf = new StringBuffer(gene2Symbol(genes.get(0)));

		for (int i = 1; i < genes.size(); i++)
		{
			bf.append(semiColon + gene2Symbol(genes.get(i)));
		}

		return bf.toString();
	}

	private String arrayToString(String[] array)
	{
		StringBuffer bf = new StringBuffer(array[0]);

		for (int i = 1; i < array.length; i++)
		{
			bf.append(semiColon + array[i]);
		}

		return bf.toString();
	}

	public String[] getAnnotationColumnNames(String name)
	{
		if (name.startsWith("Probe"))
		{
			return columnNames[0];
		}
		else
		{
			return columnNames[1];
		}
	}

	public Object[][] getAnnotationData(String name)
	{
		Object[][] data = null;
		int cols = 3;

		if (name.startsWith("Probe"))
		{
			String[] probes = orderedString(probe2GeneHash.keySet());
			int length = probes.length;
			// System.out.println("Probes: " + length);
			data = new Object[length][cols];

			for (int i = 0; i < length; i++)
			{
				data[i][0] = probes[i];
				Vector<String> genes = probe2GeneHash.get(probes[i]);

				if (genes != null)
				{
					// String[] genes = orderedString(values);
					data[i][1] = vectorGenes2String(genes);
					data[i][2] = genes2SybmolsString(genes);
				}
			}
		}
		else
		{
			String[] genes = orderedString(gene2ProbeHash.keySet());
			int length = genes.length;
			// System.out.println("Genes: " + length);
			data = new Object[length][cols];

			for (int i = 0; i < length; i++)
			{
				data[i][0] = genes[i];
				Vector<String> values = gene2ProbeHash.get(genes[i]);
				String[] probes = orderedString(values);
				data[i][1] = gene2Symbol(genes[i]);
				data[i][2] = arrayToString(probes);
			}
		}

		return data;
	}

	private String[] orderedString(Collection<String> keySet)
	{
		int size = keySet.size();
		Comparator comparator = new NumberCollator();
		String[] keys = keySet.toArray(new String[size]);
		// Arrays.sort(keys, comparator);

		return keys;
	}
}
