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

import com.sciome.bmdexpress2.util.categoryanalysis.ProbeGeneMaps;

/**
 *
 */
public class GeneLevelCategoryMap extends CategoryMap
{

	/**
	 *
	 */
	public GeneLevelCategoryMap(int c0, int c1, int c2, Object[][] mData, ProbeGeneMaps probeGeneMaps)
	{
		super(c0, c1, c2, mData, probeGeneMaps);

	}

}
