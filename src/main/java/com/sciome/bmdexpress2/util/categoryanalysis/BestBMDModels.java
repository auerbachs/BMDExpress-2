/*
 * BestBMDModels.java     1.3    4/05/2008
 *
 * Copyright (c) 2008 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used to hold BMD values of the best model.
 */

package com.sciome.bmdexpress2.util.categoryanalysis;

import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.util.NumberManager;

/**
 * The class for BestBMDModels
 *
 * @version 1.3 4/05/2008
 * @author Longlong Yang
 */
public class BestBMDModels
{
	private int MAXROWS = 0;
	private String[] probeModel;
	private double maxDose = 0, pCutoff = 0;
	private double[][] bmds;
	private boolean fitPvalue;
	private Vector<String> bmdProbes, highDoseProbes, pCuttoffProbes, uniModelNames;

	/* fields for bmds 2-D array */
	private final String[] bmdColNames = { "BMD", "BMDL", "BMDU", "pValue", "Adverse Direction" };

	public BestBMDModels()
	{
	}

	public void setFitPvalueCutoff(boolean bool, double p)
	{
		fitPvalue = bool;
		pCutoff = p;
	}

	public void setMaximumDose(double dose)
	{
		maxDose = dose;
	}

	public void readBMDValues(boolean removeMax, BMDResult bmdResults, Vector<String> bmdProbes)
	{
		int MAXCOL = bmdColNames.length; // "BMD", "BMDL", "pValue", "Adverse Direction"
		int MAXROW = bmdResults.getProbeStatResults().size();
		// int last = bmdMatrix.columns() - 1;
		this.bmdProbes = bmdProbes;
		probeModel = new String[MAXROW];
		highDoseProbes = new Vector<String>();
		pCuttoffProbes = new Vector<String>();
		uniModelNames = new Vector<String>();
		bmds = new double[MAXROW][MAXCOL];

		String message = "Read BMDs and BMDLs from benchmark dose analyses";

		for (int i = 0; i < MAXROW; i++)
		{

			try
			{

				String probe = bmdResults.getProbeStatResults().get(i).getProbeResponse().getProbe().getId();
				if (bmdResults.getProbeStatResults().get(i).getBestStatResult() == null)
				{
					pCuttoffProbes.add(probe);
					highDoseProbes.add(probe);
					continue;
				}
				Object value = bmdResults.getProbeStatResults().get(i).getBestStatResult().toString();
				int idx = bmdProbes.indexOf(probe);

				if (idx >= 0)
				{
					// asign "BMD", "BMDL", "pValue", "Adverse Direction"
					bmds[idx][0] = NumberManager.doubleValue(
							bmdResults.getProbeStatResults().get(i).getBestStatResult().getBMD());
					bmds[idx][1] = NumberManager.doubleValue(
							bmdResults.getProbeStatResults().get(i).getBestStatResult().getBMDL());
					bmds[idx][2] = NumberManager.doubleValue(
							bmdResults.getProbeStatResults().get(i).getBestStatResult().getBMDU());
					bmds[idx][3] = NumberManager.doubleValue(
							bmdResults.getProbeStatResults().get(i).getBestStatResult().getFitPValue());
					bmds[idx][4] = NumberManager.doubleValue((int) bmdResults.getProbeStatResults().get(i)
							.getBestStatResult().getAdverseDirection());

					// compare the values to filter parameters
					if (removeMax && maxDose > 0 && bmds[idx][0] > maxDose)
					{
						highDoseProbes.add(probe);
					}

					if (fitPvalue && bmds[idx][3] < pCutoff)
					{
						pCuttoffProbes.add(probe);
					}

					if (value instanceof String)
					{
						probeModel[idx] = (String) value;

						if (!uniModelNames.contains(probeModel[idx]))
						{
							uniModelNames.add(probeModel[idx]);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

		}
		// printTest(highDoseProbes, "Highest dose probes");
		// printTest(pCuttoffProbes, "pCuttoff Probes");
		if (uniModelNames.size() > 1)

		{
			// orderUniqueModels(bmdMatrix.getMP().modelNames());
		}

	}

	public int totalProbes()
	{
		return bmdProbes.size();
	}

	public String probeModel(String probe)
	{
		int idx = bmdProbes.indexOf(probe);

		if (idx >= 0)
		{
			return probeModel[idx];
		}

		return null;
	}

	public Vector<String> uniqueModelNames()
	{
		return uniModelNames;
	}

	public Vector<String> bmdProbes()
	{
		return bmdProbes;
	}

	public Vector<String> removedHDoseProbes()
	{
		return highDoseProbes;
	}

	public Vector<String> removedPCutoffProbes()
	{
		return pCuttoffProbes;
	}

	public double bmdsAt(int r, int c)
	{
		return bmds[r][c];
	}
}
