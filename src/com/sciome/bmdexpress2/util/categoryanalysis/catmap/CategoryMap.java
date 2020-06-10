/*
 * CategoryMap.java     1.0    7/25/2008
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used for match genes to pathways
 */

package com.sciome.bmdexpress2.util.categoryanalysis.catmap;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.category.identifier.CategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.category.identifier.GenericCategoryIdentifier;
import com.sciome.bmdexpress2.util.categoryanalysis.ProbeGeneMaps;

/**
 * The class of CategoryMap
 *
 * match genes to pathways
 *
 * @version 1.0 4/7/2008
 * @author Longlong Yang
 */
public class CategoryMap extends CategoryMapBase
{

	/**
	 * class contructor used for gene's pathways
	 *
	 */
	public CategoryMap(int c0, int c1, int c2, Object[][] mData, ProbeGeneMaps probeGeneMaps)
	{
		super();
		this.probeGeneMaps = probeGeneMaps;
		arrayCategoryMap(c0, c1, c2, mData, probeGeneMaps.subGenes, probeGeneMaps.allGenes,
				probeGeneMaps.getDataSetGenes());
	}

	/**
	 * get all genes and maps given a chip (id) read to hash table of (key, Vecotor) = (map, genes)
	 */
	private void arrayCategoryMap(int c0, int c1, int c2, Object[][] mData, Vector<String> subGenes,
			Vector<String> allGenes, Vector<String> dataSetGenes)
	{
		int rows = mData.length;
		HashSet<String> allGHashSet = new HashSet(allGenes);
		subHash = new Hashtable<String, Vector>(); // key = map, Vecotor = genes
		allHash = new Hashtable<String, Vector>(); // key = map, Vecotor = genes
		dataSetGeneHash = new Hashtable<String, Vector>();
		titleHash = new Hashtable<String, String>();

		Vector<CategoryIdentifier> vectGos = new Vector<CategoryIdentifier>(subHash.size());
		for (int i = 0; i < rows; i++)
		{
			String categoryId = (String) mData[i][c0];
			String categoryName = (String) mData[i][c1];
			String componentSt = (String) mData[i][c2];
			String[] values = componentSt.split("[\t:,;]");

			for (int j = 0; j < values.length; j++)
			{
				if (allGHashSet.contains(values[j]))
				{
					if (!allHash.containsKey(categoryId))
					{
						allHash.put(categoryId, new Vector<String>());
					}

					if (!allHash.get(categoryId).contains(values[j]))
					{
						allHash.get(categoryId).add(values[j]);
						titleHash.put(categoryId, categoryName);
					}

					if (!allGenes.contains(values[j]))
					{
						allGenes.add(values[j]);
					}

					if (dataSetGenes.contains(values[j]))
					{
						if (!dataSetGeneHash.containsKey(categoryId))
						{
							dataSetGeneHash.put(categoryId, new Vector<String>());
						}

						if (!dataSetGeneHash.get(categoryId).contains(values[j]))
						{
							dataSetGeneHash.get(categoryId).add(values[j]);
							titleHash.put(categoryId, categoryName);
						}
					}

					if (subGenes.contains(values[j]))
					{
						if (!subHash.containsKey(categoryId))
						{
							subHash.put(categoryId, new Vector<String>());
							CategoryIdentifier gCatID = new GenericCategoryIdentifier();
							gCatID.setId(categoryId);
							gCatID.setTitle(categoryName);
							vectGos.add(gCatID);
						}

						if (!subHash.get(categoryId).contains(values[j]))
						{
							subHash.get(categoryId).add(values[j]);
						}
					}
				}
			}

		}
		categoryIdentifiers = vectGos;
	}
}
