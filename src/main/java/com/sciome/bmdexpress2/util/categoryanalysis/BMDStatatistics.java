/*
 * BMDStatatistics.java     0.5    8/1/2008
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used to compute various statistics values based on BMD results.
 */

package com.sciome.bmdexpress2.util.categoryanalysis;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.AdverseDirectionEnum;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.ReferenceGeneProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.bmdexpress2.util.stat.FishersExact;
import com.sciome.bmdexpress2.util.stat.LinearCorrelation;
import com.sciome.bmdexpress2.util.stat.SampleStats;

/**
 * The class for BMDStatatistics
 *
 * @version 0.5, 10/12/2005
 * @author Longlong Yang
 */
public class BMDStatatistics
{

	private BMDResult		bmdResults;
	private ProbeGeneMaps	probeGeneMaps;
	private GenesBMDs		genesBMDs;
	private BestBMDModels	bestBMDModels;
	private final String	BMD		= "BMD";
	private final String	BMDL	= "BMDL";
	private final String	BMDU	= "BMDU";

	private boolean			removeMax, doRemovePCut, hasData, doneCorrelation, doEnrichment;
	private double			fitPCutoff, rCutoff, pCutoff = 0.05, maxDose = 0, minDose, minPositiveDose;
	private Vector<String>	subGenes, bmdProbes;
	private int[]			proIndices;
	private double[]		minCorrelations;
	private double[][]		bmds;

	private final String	title	= "BMD Statistics";

	/**
	 * Class constructor
	 * 
	 * @param bmd
	 *            is a BenchmarkDose object
	 * @param fileName
	 *            is a property file name
	 */
	public BMDStatatistics()
	{
	}

	public BMDStatatistics(ProbeGeneMaps pgMaps, BMDResult bmdResults)
	{

		this.bmdResults = bmdResults;
		probeGeneMaps = pgMaps;
		subGenes = probeGeneMaps.probesGenes();
		bmdProbes = probeGeneMaps.getUniqueProbes();
		removeMax = hasData = doneCorrelation = doEnrichment = false;
	}

	public void setEnrichment(boolean bool, double p)
	{
		doEnrichment = bool;
		pCutoff = p;
	}

	public void setRemoveMaxDose(boolean bool)
	{
		removeMax = bool;
	}

	public void setMaximumDose(double dose)
	{
		maxDose = dose;
	}

	public double getMinPositiveDose()
	{
		return minPositiveDose;
	}

	public void setMinPositiveDose(double minPositiveDose)
	{
		this.minPositiveDose = minPositiveDose;
	}

	public void setFitPvalueCutoff(boolean bool, double p)
	{
		doRemovePCut = bool;
		fitPCutoff = p;
	}

	public double getMinDose()
	{
		return minDose;
	}

	public void setMinDose(double minDose)
	{
		this.minDose = minDose;
	}

	public void readBMDValues()
	{
		bestBMDModels = new BestBMDModels();
		bestBMDModels.setMaximumDose(maxDose);

		if (doRemovePCut)
		{
			bestBMDModels.setFitPvalueCutoff(doRemovePCut, fitPCutoff);
		}

		bestBMDModels.readBMDValues(removeMax, bmdResults, bmdProbes);
	}

	public void readExpressionData()
	{
		Vector<String> subAllProbes = probeGeneMaps.subAllProbes();
		// System.out.println("readExpressionData(): " + subAllProbes.size());
		int size = subAllProbes.size();
		proIndices = new int[size];
		DoseResponseExperiment doseResponseExperiment = bmdResults.getDoseResponseExperiment();

		int i = 0;
		for (ProbeResponse probeResponse : doseResponseExperiment.getProbeResponses())
		{
			String probe = probeResponse.getProbe().getId();
			int idx = subAllProbes.indexOf(probe);

			if (idx >= 0)
			{
				proIndices[subAllProbes.indexOf(probe)] = i;
			}
			i++;

		}

		hasData = true;

	}

	public void computeCorrelation(double cutOff)
	{
		String[][] subG2Probes = probeGeneMaps.subG2Probes();
		Vector<String> subAllProbes = probeGeneMaps.subAllProbes();
		rCutoff = cutOff;

		int n = subGenes.size();
		minCorrelations = new double[n];
		doneCorrelation = true;

		for (int i = 0; i < n; i++)
		{
			minCorrelations[i] = linearCorrelation(subG2Probes[i], subAllProbes);
		}
	}

	private double linearCorrelation(String[] probes, Vector<String> subAllProbes)
	{
		int n = probes.length;
		double minR = 1.0;

		if (n > 1)
		{

			for (int i = 0; i < n - 1; i++)
			{
				for (int j = i + 1; j < n; j++)
				{
					int x = subAllProbes.indexOf(probes[i]);
					int y = subAllProbes.indexOf(probes[j]);
					float[] xsFloat = bmdResults.getDoseResponseExperiment().getProbeResponses()
							.get(proIndices[x]).getResponseArray();
					float[] ysFloat = bmdResults.getDoseResponseExperiment().getProbeResponses()
							.get(proIndices[y]).getResponseArray();

					// convert xsFloat and ysFloat to double
					double[] xs = new double[xsFloat.length];
					for (int i1 = 0; i1 < xsFloat.length; i1++)
					{
						xs[i1] = xsFloat[i1];
					}
					double[] ys = new double[ysFloat.length];
					for (int i1 = 0; i1 < ysFloat.length; i1++)
					{
						ys[i1] = ysFloat[i1];
					}

					double r = LinearCorrelation.correlation(xs, ys);

					if (r < minR)
					{
						minR = r;
					}
				}
			}
		}

		try
		{
			minR = NumberManager.numberFormat(4, minR);
		}
		catch (Exception e)
		{}
		return minR;
	}

	/*
	 * get subhashg2ids for a vector o
	 */
	public Hashtable<String, Vector> getSubHashG2Ids(Vector<String> vectGenes)
	{

		Hashtable<String, Vector> subHashG2Ids = new Hashtable<>();
		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<String>(probeGeneMaps.subHashG2Ids().get(geneId));
				if (probes == null)
					probes = new Vector<String>();

				subHashG2Ids.put(geneId, probes);

			}
		}

		return subHashG2Ids;
	}

	/**
	 * Remove genes with BMD > highest dose
	 */
	public Vector<String> checkHighestDose(Vector<String> vectGenes, Hashtable<String, Vector> subHashG2Ids,
			Set<String> removedProbes)
	{
		Vector<String> doseGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<>(subHashG2Ids.get(geneId));
				// passed = new Vector<String>();

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						if (bestBMDModels.removedHDoseProbes().contains(st))
						{
							probes.remove(st);
							removedProbes.add(st);
						}
					}
				}

				if (probes == null || probes.isEmpty())
				{
					// vectGenes.remove(geneId);
				}
				else
				{
					doseGenes.add(geneId);
				}
			}
		}

		return doseGenes;
	}

	/**
	 * Remove genes with fit p_value < p-Value cutoff
	 */
	public Vector<String> checkFitPCutoff(Vector<String> vectGenes, Hashtable<String, Vector> subHashG2Ids,
			Set<String> removedProbes)
	{
		Vector<String> pcGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<>(subHashG2Ids.get(geneId));

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						if (bestBMDModels.removedPCutoffProbes().contains(st))
						{
							probes.remove(st);
							removedProbes.add(st);
						}
					}
				}

				if (probes == null || probes.isEmpty())
				{
					// vectGenes.remove(geneId);
				}
				else
				{
					pcGenes.add(geneId);
				}
			}
		}

		return pcGenes;
	}

	public String genesConfilt(Vector<String> vectGenes)
	{
		StringBuffer bf = new StringBuffer();

		for (int i = 0; i < vectGenes.size(); i++)
		{
			int idx = subGenes.indexOf(vectGenes.get(i));

			if (idx >= 0)
			{
				if (minCorrelations[idx] < rCutoff)
				{
					if (bf.length() > 0)
					{
						bf.append(";");
					}

					bf.append(vectGenes.get(i) + "(" + minCorrelations[idx] + ")");
				}
			}
		}

		return bf.toString();
	}

	// int row, int col, int maxCol,Object[]
	public void computeStatistic(int col, Vector<String> vectSub, Vector<String> vectAll,
			CategoryAnalysisResult categoryAnalysisResult, Hashtable<String, Vector> subHashG2Ids)
	{
		int last = col - 1;
		int sub = vectSub.size();
		int subTotal = subGenes.size();
		int all = vectAll.size();
		int allTotal = probeGeneMaps.getAllGenes().size();

		if (doneCorrelation)
		{
			categoryAnalysisResult.setGenesWithConflictingProbeSets(genesConfilt(vectSub));
			if (categoryAnalysisResult.getGenesWithConflictingProbeSets() == null)
			{
				categoryAnalysisResult.setGenesWithConflictingProbeSets("");
			}
		}

		PerGeneBMDs[] perGBMDs = new PerGeneBMDs[sub];
		double[][] sortBmds = new double[2][sub];
		double[][] sortBmdls = new double[2][sub];
		double[][] sortBmdus = new double[2][sub];
		int cntUp = 0;
		int cntDown = 0;

		// Lets form the ReferenceGeneProbeStatResult object
		List<ReferenceGeneProbeStatResult> refGeneProbeStatResults = new ArrayList<>();
		for (String geneName : vectSub)
		{
			ReferenceGeneProbeStatResult refGeneProbeStatResult = new ReferenceGeneProbeStatResult();
			refGeneProbeStatResult.setReferenceGene(probeGeneMaps.getReferenceGeneMap().get(geneName));
			List<ProbeStatResult> probeStatResults = new ArrayList<>();

			Vector<String> someProbes = subHashG2Ids.get(geneName);
			if (someProbes != null)
			{
				for (Object probe : subHashG2Ids.get(geneName))
				{
					String probeName = (String) probe;
					probeStatResults.add(probeGeneMaps.getStatResultMap().get(probeName));

				}
			}

			refGeneProbeStatResult.setProbeStatResults(probeStatResults);

			if (doneCorrelation)
			{
				int idx = subGenes.indexOf(geneName);
				if (idx >= 0 && minCorrelations[idx] < rCutoff)
				{
					refGeneProbeStatResult.setConflictMinCorrelation(minCorrelations[idx]);
				}
			}
			refGeneProbeStatResults.add(refGeneProbeStatResult);

		}
		categoryAnalysisResult.setReferenceGeneProbeStatResults(refGeneProbeStatResults);
		for (int i = 0; i < sub; i++)
		{
			String gene = vectSub.get(i);
			if (subHashG2Ids.get(gene) != null)
			{
				perGBMDs[i] = perGeneBMDValues(subHashG2Ids.get(gene));
				perGBMDs[i].setGene(gene);
				insertDoubls(i, perGBMDs[i].avgBmd(), perGBMDs[i].avgPValue(), sortBmds);
				insertDoubls(i, perGBMDs[i].avgBmdl(), perGBMDs[i].avgPValue(), sortBmdls);
				insertDoubls(i, perGBMDs[i].avgBmdu(), perGBMDs[i].avgPValue(), sortBmdus);
				cntUp += perGBMDs[i].upCount();
				cntDown += perGBMDs[i].downCount();
			}
		}

		sampleStats(col, 0, sortBmds, categoryAnalysisResult, BMD);
		if (doEnrichment)
		{
			col = enrichmentBMD(col, all, allTotal, vectSub, categoryAnalysisResult);
			col = enrichmentBMD(col, sub, subTotal, vectSub, categoryAnalysisResult);
		}

		sampleStats(col, 0, sortBmdls, categoryAnalysisResult, BMDL);
		sampleStats(col, 0, sortBmdus, categoryAnalysisResult, BMDU);
		if (doEnrichment)
		{
			col = enrichmentBMDL(col, all, allTotal, vectSub, categoryAnalysisResult);
			col = enrichmentBMDL(col, sub, subTotal, vectSub, categoryAnalysisResult);
		}

		col = percentBMD(col, all, 0.05, sortBmds[0], categoryAnalysisResult, true);
		col = percentBMD(col, all, 0.1, sortBmds[0], categoryAnalysisResult, false);

		col = addDirectionalGeneStats(col, perGBMDs, categoryAnalysisResult, refGeneProbeStatResults);

		// TODO: figure out the probe model counts.
		// addProbeModelCounts(col, last, categoryAnalysisResult);
	}

	private PerGeneBMDs perGeneBMDValues(Vector<String> probes)
	{
		PerGeneBMDs perGBMDs = new PerGeneBMDs(probes.size());

		for (int i = 0; i < probes.size(); i++)
		{
			String key = probes.get(i);
			int idx = bmdProbes.indexOf(key);

			if (idx >= 0)
			{
				if (bestBMDModels.bmdsAt(idx, 4) > 0)
				{
					perGBMDs.addDirectionUp(bestBMDModels.bmdsAt(idx, 0), bestBMDModels.bmdsAt(idx, 1),
							bestBMDModels.bmdsAt(idx, 2), bestBMDModels.bmdsAt(idx, 3), key);
				}
				else
				{
					perGBMDs.addDirectionDown(bestBMDModels.bmdsAt(idx, 0), bestBMDModels.bmdsAt(idx, 1),
							bestBMDModels.bmdsAt(idx, 2), bestBMDModels.bmdsAt(idx, 3), key);
				}
			}
			else
			{

				return null;
			}
		}

		return perGBMDs;
	}

	private int sampleStats(int col, double t, double[][] sortDbls,
			CategoryAnalysisResult categoryAnalysisResult, String whichValue)
	{

		SampleStats stats = new SampleStats(t, sortDbls[0]);
		if (whichValue.equals(BMD))
		{
			categoryAnalysisResult.setBmdMean(stats.mean());
			categoryAnalysisResult.setBmdMedian(stats.median());
			categoryAnalysisResult.setBmdMinimum(stats.minimum());
			categoryAnalysisResult.setBmdSD(stats.standardDeviation());
		}
		else if (whichValue.equals(BMDL))
		{
			categoryAnalysisResult.setBmdlMean(stats.mean());
			categoryAnalysisResult.setBmdlMedian(stats.median());
			categoryAnalysisResult.setBmdlMinimum(stats.minimum());
			categoryAnalysisResult.setBmdlSD(stats.standardDeviation());
		}
		else if (whichValue.equals(BMDU))
		{
			categoryAnalysisResult.setBmduMean(stats.mean());
			categoryAnalysisResult.setBmduMedian(stats.median());
			categoryAnalysisResult.setBmduMinimum(stats.minimum());
			categoryAnalysisResult.setBmduSD(stats.standardDeviation());
		}

		stats = new SampleStats(t, sortDbls[0], sortDbls[1]);
		if (whichValue.equals(BMD))
		{
			categoryAnalysisResult.setBmdWMean(stats.mean());
			categoryAnalysisResult.setBmdWSD(stats.standardDeviation());
		}
		else if (whichValue.equals(BMDL))
		{
			categoryAnalysisResult.setBmdlWMean(stats.mean());
			categoryAnalysisResult.setBmdlWSD(stats.standardDeviation());
		}
		else if (whichValue.equals(BMDU))
		{
			categoryAnalysisResult.setBmduWMean(stats.mean());
			categoryAnalysisResult.setBmduWSD(stats.standardDeviation());
		}

		return col;
	}

	private int enrichmentBMD(int col, int all, int allTotal, Vector<String> genes,
			CategoryAnalysisResult categoryAnalysisResult)
	{
		if (genesBMDs != null)
		{
			// outMatrix[row][col] = null;
			int[] sIndices = genesBMDs.sortedBMDindices(genes);
			int n = sIndices.length;

			for (int i = 0; i < n; i++)
			{
				int a = i + 1;
				int b = sIndices[i] + 1;
				double[] pValues = fisherExactTest(a, all, b, allTotal);

				if (pValues[1] <= pCutoff)
				{
					double bmd = genesBMDs.sortedBMDAt(sIndices[i]);
					String st = bmd + " (" + a + "," + all + "," + b + "," + allTotal + ")=" + pValues[1];

					// TODO: figure out this enrichment stuff. is it even being used?
					// output[col] = st; // new Double(bmd);
					break;
				}
			}

			col += 1;
		}

		return col;
	}

	private int enrichmentBMDL(int col, int all, int allTotal, Vector<String> genes,
			CategoryAnalysisResult categoryAnalysisResult)
	{
		if (genesBMDs != null)
		{
			int[] sIndices = genesBMDs.sortedBMDLindices(genes);
			int n = sIndices.length;

			for (int i = 0; i < n; i++)
			{
				int a = i + 1;
				int b = sIndices[i] + 1;
				double[] pValues = fisherExactTest(a, all, b, allTotal);

				if (pValues[1] <= pCutoff)
				{
					double bmdl = genesBMDs.sortedBMDLAt(sIndices[i]);
					String st = bmdl + " (" + a + "," + all + "," + b + "," + allTotal + ")=" + pValues[1];
					// TODO: figure out this enrichment stuff. is it even being used?
					// output[col] = st;// new Double(bmdl);
					break;
				}
			}

			col += 1;
		}

		return col;
	}

	private double[] fisherExactTest(int sub, int subTotal, int all, int allTotal)
	{
		int a = sub;
		int b = subTotal - a;
		int c = all - a;
		int d = allTotal - a - b - c;
		double[] pValues = { 1, 1, 1 };

		if (a >= 0 && b >= 0 && c >= 0 && d >= 0)
		{
			FishersExact test = new FishersExact(a, b, c, d);
			pValues[0] = NumberManager.numberFormat(5, test.pLeft());
			pValues[1] = NumberManager.numberFormat(5, test.pRight());
			pValues[2] = NumberManager.numberFormat(5, test.twoTail());
		}

		return pValues;// NumberManager.numberFormat(4, pValues);
	}

	/*
	 * Insertion sorting ascending based on d0 value
	 */
	private void insertDoubls(int n, double d0, double d1, double[][] sortDbls)
	{
		for (int i = n; i >= 0; i--)
		{
			if (i == 0 || d0 >= sortDbls[0][i - 1])
			{
				sortDbls[0][i] = d0;
				sortDbls[1][i] = d1;
				break;
			}
			else
			{
				sortDbls[0][i] = sortDbls[0][i - 1];
				sortDbls[1][i] = sortDbls[1][i - 1];
			}
		}
	}

	private int percentBMD(int col, int all, double percent, double[] sortBmds,
			CategoryAnalysisResult categoryAnalysisResult, boolean isFifthPercentile)
	{
		int n = sortBmds.length;
		double one = 1.0;

		for (int i = 0; i < n; i++)
		{
			double pct = (i + one) / all;

			if (pct >= percent)
			{
				if (pct == percent || i == 0)
				{
					if (isFifthPercentile)
					{
						categoryAnalysisResult.setFifthPercentileIndex((double) i);
						categoryAnalysisResult.setBmdFifthPercentileTotalGenes(sortBmds[i]);
					}
					else
					{
						categoryAnalysisResult.setTenthPercentileIndex((double) i);
						categoryAnalysisResult.setBmdTenthPercentileTotalGenes(sortBmds[i]);

					}
					// output[col] = new Double((double) i);
					// output[col + 1] = new Double(sortBmds[i]);
				}
				else
				{
					double avg = (sortBmds[i] + sortBmds[i - 1]) / 2;

					if (isFifthPercentile)
					{
						categoryAnalysisResult.setFifthPercentileIndex(new Double(i - one / 2));
						categoryAnalysisResult.setBmdFifthPercentileTotalGenes(avg);

					}
					else
					{
						categoryAnalysisResult.setTenthPercentileIndex(new Double(i - one / 2));
						categoryAnalysisResult.setBmdTenthPercentileTotalGenes(avg);
					}
					// output[col] = new Double(i - one / 2);
					// output[col + 1] = new Double(avg);
				}
				break;
			}
		}

		return col + 2;
	}

	private String doubleArray2String(double[] dbs)
	{
		StringBuffer bf = new StringBuffer();

		for (int i = 0; i < dbs.length; i++)
		{
			if (i == 0)
			{
				bf.append(dbs[i]);
			}
			else
			{
				bf.append(";" + dbs[i]);
			}
		}

		return bf.toString();
	}

	private void addProbeModelCounts(int col, int idx, Object[] output)
	{
		String probeString = ((String) output[idx]).replaceAll(",", ";");
		String[] probes = probeString.split(";");
		// System.out.println(probes.length + ": " + probeString);

		Vector<String> uniqueModelNames = bestBMDModels.uniqueModelNames();
		int size = uniqueModelNames.size();
		int total = probes.length;
		double hundred = 100.0;

		if (size > 1 && total > 0)
		{
			int[] cnts = new int[size];

			for (int i = 0; i < size; i++)
			{
				cnts[i] = 0;
			}

			for (int i = 0; i < total; i++)
			{
				String modelName = bestBMDModels.probeModel(probes[i]);
				idx = uniqueModelNames.indexOf(modelName);

				if (idx >= 0)
				{
					cnts[idx] += 1;
				}
			}

			for (int i = 0; i < size; i++)
			{
				StringBuffer bf = new StringBuffer();
				bf.append(cnts[i]);

				if (cnts[i] > 0)
				{
					double percent = cnts[i] * hundred / total;
					percent = NumberManager.numberFormat(2, percent);
					bf.append("(" + percent + "%)");
				}

				if (col + i < output.length)
				{
					output[col + i] = bf.toString();
				}
				else
				{
					// System.out.println(i + " plus " + col + " < " + output.length);
				}
			}
		}
	}

	public Vector<String> uniqueModelNames()
	{
		return bestBMDModels.uniqueModelNames();
	}

	private int addDirectionalGeneStats(int col, PerGeneBMDs[] perGBMDs,
			CategoryAnalysisResult categoryAnalysisResult,
			List<ReferenceGeneProbeStatResult> refGeneProbeStatResults)
	{
		int max = perGBMDs.length;
		int up = 0, down = 0, conflict = 0;
		double[][] upBmds = new double[2][max];
		double[][] upBmdls = new double[2][max];
		double[][] upBmdus = new double[2][max];
		double[][] downBmds = new double[2][max];
		double[][] downBmdls = new double[2][max];
		double[][] downBmdus = new double[2][max];
		// [i][0] = genes; [i][1] = probes, [i][2] = bmds list, [i][3] = bmdls list
		StringBuffer[][] sbs = new StringBuffer[3][4];

		for (int i = 0; i < max; i++)
		{
			if (perGBMDs[i] == null)
				continue;

			double bmd = perGBMDs[i].avgBmd();
			double bmdl = perGBMDs[i].avgBmdl();
			double bmdu = perGBMDs[i].avgBmdu();

			double pValue = perGBMDs[i].avgPValue();

			if (perGBMDs[i].upCount() > 0 && perGBMDs[i].downCount() == 0)
			{
				refGeneProbeStatResults.get(i).setAdverseDirection(AdverseDirectionEnum.UP);
				insertDoubls(up, bmd, pValue, upBmds);
				insertDoubls(up, bmdl, pValue, upBmdls);
				insertDoubls(up, bmdu, pValue, upBmdus);

				if (sbs[0][0] == null)
				{
					sbs[0][0] = new StringBuffer(perGBMDs[i].getGene());
				}
				else
				{
					sbs[0][0].append(BMDExpressConstants.getInstance().SEMICOLON + perGBMDs[i].getGene());
				}

				if (sbs[0][1] == null)
				{
					sbs[0][1] = new StringBuffer(
							perGBMDs[i].upProbesToString(BMDExpressConstants.getInstance().COMMA));
				}
				else
				{
					sbs[0][1].append(BMDExpressConstants.getInstance().SEMICOLON
							+ perGBMDs[i].upProbesToString(BMDExpressConstants.getInstance().COMMA));
				}

				if (sbs[0][2] == null)
				{
					sbs[0][2] = new StringBuffer();
					sbs[0][2].append(bmd);
				}
				else
				{
					sbs[0][2].append(BMDExpressConstants.getInstance().SEMICOLON + bmd);
				}

				if (sbs[0][3] == null)
				{
					sbs[0][3] = new StringBuffer();
					sbs[0][3].append(bmdl);
				}
				else
				{
					sbs[0][3].append(BMDExpressConstants.getInstance().SEMICOLON + bmdl);
				}

				up += 1;
			}
			else if (perGBMDs[i].upCount() == 0 && perGBMDs[i].downCount() > 0)
			{
				refGeneProbeStatResults.get(i).setAdverseDirection(AdverseDirectionEnum.DOWN);
				insertDoubls(down, perGBMDs[i].avgBmd(), perGBMDs[i].avgPValue(), downBmds);
				insertDoubls(down, perGBMDs[i].avgBmdl(), perGBMDs[i].avgPValue(), downBmdls);
				insertDoubls(down, perGBMDs[i].avgBmdu(), perGBMDs[i].avgPValue(), downBmdus);

				if (sbs[1][0] == null)
				{
					sbs[1][0] = new StringBuffer(perGBMDs[i].getGene());
				}
				else
				{
					sbs[1][0].append(BMDExpressConstants.getInstance().SEMICOLON + perGBMDs[i].getGene());
				}

				if (sbs[1][1] == null)
				{
					sbs[1][1] = new StringBuffer(
							perGBMDs[i].downProbesToString(BMDExpressConstants.getInstance().COMMA));
				}
				else
				{
					sbs[1][1].append(BMDExpressConstants.getInstance().SEMICOLON
							+ perGBMDs[i].downProbesToString(BMDExpressConstants.getInstance().COMMA));
				}

				if (sbs[1][2] == null)
				{
					sbs[1][2] = new StringBuffer();
					sbs[1][2].append(bmd);
				}
				else
				{
					sbs[1][2].append(BMDExpressConstants.getInstance().SEMICOLON + bmd);
				}

				if (sbs[1][3] == null)
				{
					sbs[1][3] = new StringBuffer();
					sbs[1][3].append(bmdl);
				}
				else
				{
					sbs[1][3].append(BMDExpressConstants.getInstance().SEMICOLON + bmdl);
				}

				down += 1;
			}
			else
			{
				refGeneProbeStatResults.get(i).setAdverseDirection(AdverseDirectionEnum.CONFLICT);
				if (sbs[2][0] == null)
				{
					sbs[2][0] = new StringBuffer(perGBMDs[i].getGene());
				}
				else
				{
					sbs[2][0].append(BMDExpressConstants.getInstance().SEMICOLON + perGBMDs[i].getGene());
				}

				if (sbs[2][1] == null)
				{
					sbs[2][1] = new StringBuffer(
							perGBMDs[i].conflictProbesToString(BMDExpressConstants.getInstance().COMMA));
				}
				else
				{
					sbs[2][1].append(BMDExpressConstants.getInstance().SEMICOLON
							+ perGBMDs[i].conflictProbesToString(BMDExpressConstants.getInstance().COMMA));
				}

				if (sbs[2][2] == null)
				{
					sbs[2][2] = new StringBuffer();
					sbs[2][2].append(bmd);
				}
				else
				{
					sbs[2][2].append(BMDExpressConstants.getInstance().SEMICOLON + bmd);
				}

				if (sbs[2][3] == null)
				{
					sbs[2][3] = new StringBuffer();
					sbs[2][3].append(bmdl);
				}
				else
				{
					sbs[2][3].append(BMDExpressConstants.getInstance().SEMICOLON + bmdl);
				}

				conflict += 1;
			}
		}

		col = addPerGeneStats(col, up, upBmds, upBmdls, upBmdus, sbs[0], categoryAnalysisResult, true);
		col = addPerGeneStats(col, down, downBmds, downBmdls, downBmdus, sbs[1], categoryAnalysisResult,
				false);

		if ((max - up - down) > 0)
		{
			// categoryAnalysisResult.setGenesWithAdverseConflictCount(max - up - down);
			// categoryAnalysisResult.setGenesConflictList(sbs[2][0].toString());
			// categoryAnalysisResult.setGenesConflictProbeList(sbs[2][1].toString());
			// categoryAnalysisResult.setBmdConflictList(sbs[2][2].toString());
			// categoryAnalysisResult.setBmdlConflictList(sbs[2][3].toString());
		}

		return col + 5;
	}

	public int addPerGeneStats(int col, int count, double[][] sortBmds, double[][] sortBmdls,
			double[][] sortBmdus, StringBuffer[] sbf, CategoryAnalysisResult categoryAnalysisResult,
			boolean isUp)
	{
		if (count > 0)
		{

			SampleStats stats = new SampleStats(0, sortBmds[0], count);
			if (isUp)
			{
				categoryAnalysisResult.setGenesUpBMDMean(stats.mean());
				categoryAnalysisResult.setGenesUpBMDMedian(stats.median());
				categoryAnalysisResult.setGenesUpBMDSD(stats.standardDeviation());

			}
			else
			{
				categoryAnalysisResult.setGenesDownBMDMean(stats.mean());
				categoryAnalysisResult.setGenesDownBMDMedian(stats.median());
				categoryAnalysisResult.setGenesDownBMDSD(stats.standardDeviation());
			}

			stats = new SampleStats(0, sortBmdls[0], count);
			if (isUp)
			{
				categoryAnalysisResult.setGenesUpBMDLMean(stats.mean());
				categoryAnalysisResult.setGenesUpBMDLMedian(stats.median());
				categoryAnalysisResult.setGenesUpBMDLSD(stats.standardDeviation());
			}
			else
			{
				categoryAnalysisResult.setGenesDownBMDLMean(stats.mean());
				categoryAnalysisResult.setGenesDownBMDLMedian(stats.median());
				categoryAnalysisResult.setGenesDownBMDLSD(stats.standardDeviation());
			}

			stats = new SampleStats(0, sortBmdus[0], count);
			if (isUp)
			{
				categoryAnalysisResult.setGenesUpBMDUMean(stats.mean());
				categoryAnalysisResult.setGenesUpBMDUMedian(stats.median());
				categoryAnalysisResult.setGenesUpBMDUSD(stats.standardDeviation());
			}
			else
			{
				categoryAnalysisResult.setGenesDownBMDUMean(stats.mean());
				categoryAnalysisResult.setGenesDownBMDUMedian(stats.median());
				categoryAnalysisResult.setGenesDownBMDUSD(stats.standardDeviation());
			}

		}

		col += 11;

		return col;
	}

	public boolean hasData()
	{
		return hasData;
	}

	public boolean doneCorrelation()
	{
		return doneCorrelation;
	}

	public void averageGenesBMDs()
	{
		Hashtable<String, Vector> subHashG2Ids = probeGeneMaps.subHashG2Ids();
		int size = subGenes.size();
		String message = "Average per gene BMDs and BMDLs values.";
		genesBMDs = new GenesBMDs(size);

		for (int i = 0; i < size; i++)
		{
			String gene = subGenes.get(i);
			Vector<String> probes = subHashG2Ids.get(gene);

			for (int j = 0; j < probes.size(); j++)
			{
				String probe = probes.get(j);
				int idx = bmdProbes.indexOf(probe);

				if (idx >= 0 && !bestBMDModels.removedHDoseProbes().contains(probe))
				{
					genesBMDs.addPerGeneValues(gene, bestBMDModels.bmdsAt(idx, 0),
							bestBMDModels.bmdsAt(idx, 1));
				}
			}

		}

		genesBMDs.ascendSortBMDandBMDLs();
		// System.out.println(size + "Sorted BMDs: " + genesBMDs.maxGenes());
	}

	public Vector<String> checkBMDBMDLRatio(Vector<String> vectGenes, double bmdBmdlRatio,
			Hashtable<String, Vector> subHashG2Ids, Set<String> removedProbes)
	{

		Vector<String> pcGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<String>(subHashG2Ids.get(geneId));

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						ProbeStatResult probeStatResult = this.probeGeneMaps.getStatResultMap().get(st);
						if (probeStatResult == null)
							continue;

						double bmddivbmdl = Double.NaN;
						if (probeStatResult.getBestStatResult() != null)
							bmddivbmdl = probeStatResult.getBestStatResult().getBMDUdiffBMDL();

						if (Double.isNaN(bmddivbmdl) || probeStatResult.getBestStatResult() == null
								|| bmddivbmdl > bmdBmdlRatio)
						{
							probes.remove(st);
							removedProbes.add(st);
						}
					}
				}

				if (probes == null || probes.isEmpty())
				{
					// vectGenes.remove(geneId);
				}
				else
				{
					pcGenes.add(geneId);
				}
			}
		}

		return pcGenes;
	}

	public Vector<String> checkBMDUBMDLRatio(Vector<String> vectGenes, double ratio,
			Hashtable<String, Vector> subHashG2Ids, Set<String> removedProbes)
	{

		Vector<String> pcGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<>(subHashG2Ids.get(geneId));

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						ProbeStatResult probeStatResult = this.probeGeneMaps.getStatResultMap().get(st);
						if (probeStatResult == null)
							continue;

						double bmdudivbmdl = Double.NaN;
						if (probeStatResult.getBestStatResult() != null)
							bmdudivbmdl = probeStatResult.getBestStatResult().getBMDUdiffBMDL();
						// if this ratio is NaN, then allow passage.

						if (Double.isNaN(bmdudivbmdl) || probeStatResult.getBestStatResult() == null
								|| bmdudivbmdl > ratio)
						{
							probes.remove(st);
							removedProbes.add(st);
						}
					}
				}

				if (probes == null || probes.isEmpty())
				{
					// vectGenes.remove(geneId);
				}
				else
				{
					pcGenes.add(geneId);
				}
			}
		}

		return pcGenes;
	}

	public Vector<String> checkFoldChange(Vector<String> vectGenes, double foldchange,
			Hashtable<String, Vector> subHashG2Ids, Set<String> removedProbes)
	{

		Vector<String> pcGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<>(subHashG2Ids.get(geneId));

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						ProbeStatResult probeStatResult = this.probeGeneMaps.getStatResultMap().get(st);
						if (probeStatResult == null)
							continue;

						if (probeStatResult.getBestFoldChange() != null
								&& Math.abs(probeStatResult.getBestFoldChange()) < foldchange)
						{
							probes.remove(st);
							removedProbes.add(st);
						}
					}
				}

				if (probes == null || probes.isEmpty())
				{
					// vectGenes.remove(geneId);
				}
				else
				{
					pcGenes.add(geneId);
				}
			}
		}

		return pcGenes;
	}

	public Vector<String> checkBMDUBMDRatio(Vector<String> vectGenes, double ratio,
			Hashtable<String, Vector> subHashG2Ids, Set<String> removedProbes)
	{

		Vector<String> pcGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<String>(subHashG2Ids.get(geneId));

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						ProbeStatResult probeStatResult = this.probeGeneMaps.getStatResultMap().get(st);
						if (probeStatResult == null)
							continue;

						double bmdudivbmd = Double.NaN;
						if (probeStatResult.getBestStatResult() != null)
							bmdudivbmd = probeStatResult.getBestStatResult().getBMDUdiffBMDL();
						// if this ratio is NaN, then allow passage.

						if (Double.isNaN(bmdudivbmd) || probeStatResult.getBestStatResult() == null
								|| bmdudivbmd > ratio)
						{
							probes.remove(st);
							removedProbes.add(st);
						}
					}
				}

				if (probes == null || probes.isEmpty())
				{
					// vectGenes.remove(geneId);
				}
				else
				{
					pcGenes.add(geneId);
				}
			}
		}

		return pcGenes;
	}

	public Vector<String> checkNFoldBelowLowestDose(Vector<String> vectGenes,
			double nFoldbelowLowestDoseValue, Hashtable<String, Vector> subHashG2Ids,
			Set<String> removedProbes)
	{
		Vector<String> pcGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<>(subHashG2Ids.get(geneId));

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						ProbeStatResult probeStatResult = this.probeGeneMaps.getStatResultMap().get(st);
						if (probeStatResult == null)
							continue;

						if (probeStatResult.getBestStatResult() == null || minPositiveDose
								/ probeStatResult.getBestStatResult().getBMD() > nFoldbelowLowestDoseValue)
						{
							probes.remove(st);
							removedProbes.add(st);
						}
					}
				}

				if (probes == null || probes.isEmpty())
				{
					// vectGenes.remove(geneId);
				}
				else
				{
					pcGenes.add(geneId);
				}
			}
		}

		return pcGenes;
	}

	public Vector<String> checkPValueBelowDose(Vector<String> vectGenes, double pValue,
			Hashtable<String, Vector> subHashG2Ids, Set<String> removedProbes)
	{
		Vector<String> pcGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<>(subHashG2Ids.get(geneId));

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						ProbeStatResult probeStatResult = this.probeGeneMaps.getStatResultMap().get(st);
						if (probeStatResult == null)
							continue;

						if (probeStatResult.getPrefilterPValue() != null
								&& Math.abs(probeStatResult.getPrefilterPValue()) > pValue)
						{
							probes.remove(st);
							removedProbes.add(st);
						}
					}
				}

				if (probes == null || probes.isEmpty())
				{
					// vectGenes.remove(geneId);
				}
				else
				{
					pcGenes.add(geneId);
				}
			}
		}

		return pcGenes;
	}

	public Vector<String> checkAdjustedPValueBelowDose(Vector<String> vectGenes, double adjustedPValue,
			Hashtable<String, Vector> subHashG2Ids, Set<String> removedProbes)
	{
		Vector<String> pcGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = new Vector<>(subHashG2Ids.get(geneId));

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						ProbeStatResult probeStatResult = this.probeGeneMaps.getStatResultMap().get(st);
						if (probeStatResult == null)
							continue;

						if (probeStatResult.getPrefilterAdjustedPValue() != null
								&& Math.abs(probeStatResult.getPrefilterAdjustedPValue()) > adjustedPValue)
						{
							probes.remove(st);
							removedProbes.add(st);
						}
					}
				}

				if (probes == null || probes.isEmpty())
				{
					// vectGenes.remove(geneId);
				}
				else
				{
					pcGenes.add(geneId);
				}
			}
		}

		return pcGenes;
	}

	public Vector<String> getFinalList(Vector<String> vectGenes, Hashtable<String, Vector> subHashG2Ids,
			Set<String> removedProbes)
	{
		Vector<String> pcGenes = new Vector<String>();

		if (vectGenes != null && vectGenes.size() > 0)
		{
			for (int i = vectGenes.size() - 1; i >= 0; i--)
			{
				String geneId = vectGenes.get(i);
				Vector<String> probes = subHashG2Ids.get(geneId);

				if (probes != null && probes.size() > 0)
				{
					for (int j = probes.size() - 1; j >= 0; j--)
					{
						String st = probes.get(j);

						ProbeStatResult probeStatResult = this.probeGeneMaps.getStatResultMap().get(st);
						if (probeStatResult == null)
							continue;

						if (removedProbes.contains(st))
							probes.remove(st);
					}
				}

				if (probes == null || probes.isEmpty())
					vectGenes.remove(geneId);
				else
					pcGenes.add(geneId);
			}
		}

		return pcGenes;
	}
}
