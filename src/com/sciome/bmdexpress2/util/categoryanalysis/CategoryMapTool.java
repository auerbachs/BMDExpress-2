/*
 * GOTermsTool.java     0.5    1/04/2007
 *
 * Copyright (c) 2005 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used to set up model fits.
 */

package com.sciome.bmdexpress2.util.categoryanalysis;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.category.DefinedCategoryAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.GOAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.category.PathwayAnalysisResult;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.CategoryMap;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.CategoryMapBase;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.GOTermMap;
import com.sciome.bmdexpress2.util.categoryanalysis.catmap.GenesPathways;
import com.sciome.bmdexpress2.util.categoryanalysis.defined.ProbeCategoryMaps;
import com.sciome.bmdexpress2.util.stat.DosesStat;
import com.sciome.bmdexpress2.util.stat.FishersExact;

/**
 * The class for GOTermsTool
 *
 * @version 0.5, 10/12/2005
 * @author Longlong Yang
 */
public class CategoryMapTool
{

	private BMDResult bmdResults;
	private ProbeGeneMaps probeGeneMaps;
	private BMDStatatistics bmdStats;
	private CategoryMapBase categoryGeneMap;

	private String rstName;
	private ICategoryMapToolProgress categoryMapProgress;
	private AnalysisInfo analysisInfo;

	private CategoryAnalysisParameters params;

	/**
	 * Class constructor
	 * 
	 * @param keyAll2
	 * @param maxDose2
	 * @param rstName2
	 * @param genesGOTerms2
	 * @param probeGeneMaps2
	 * 
	 * @param bmd
	 *            is a BenchmarkDose object
	 * @param fileName
	 *            is a property file name
	 */

	public CategoryMapTool(CategoryAnalysisParameters params, BMDResult bmdResults,
			CategoryAnalysisEnum catAnalysisEnum, ICategoryMapToolProgress categoryMapProgress,
			AnalysisInfo analysisInfo)
	{

		// generate probegenemaps

		this.analysisInfo = analysisInfo;
		this.categoryMapProgress = categoryMapProgress;
		this.bmdResults = bmdResults;
		this.params = params;
		String srcName = bmdResults.getName();
		String chip = "Generic";
		if (bmdResults.getDoseResponseExperiment().getChip() != null)
			chip = bmdResults.getDoseResponseExperiment().getChip().getGeoID();
		String rstName = srcName + "_" + chip;

		// need to get the max dose
		DoseResponseExperiment doseResponseExperiment = bmdResults.getDoseResponseExperiment();

		DosesStat dosesStat = new DosesStat();
		float[] doses = new float[doseResponseExperiment.getTreatments().size()];
		int i = 0;
		for (Treatment treatment : doseResponseExperiment.getTreatments())
		{
			doses[i] = treatment.getDose();
			i++;
		}
		dosesStat.asscendingSort(doses);
		double maxDose = dosesStat.maxDose();
		double minDose = dosesStat.minDose();
		double minPositiveDose = dosesStat.noZeroMinDose();

		params.setMinDose(minDose);
		params.setMaxDose(maxDose);
		params.setMinPositiveDose(minPositiveDose);

		// create a hashtable of probes.
		Hashtable<String, Integer> probeHash = new Hashtable<>();
		for (ProbeResponse probeResponse : doseResponseExperiment.getProbeResponses())
			probeHash.put(probeResponse.getProbe().getId(), 1);

		ProbeGeneMaps probeGeneMaps = new ProbeGeneMaps(bmdResults);
		probeGeneMaps.readProbes(false);
		// probeGeneMaps.readArraysInfo();
		probeGeneMaps.setProbesHash(probeHash);

		CategoryMapBase catMap = null;

		// which type of analysis are we doing here?
		if (catAnalysisEnum == CategoryAnalysisEnum.GO)
		{
			if (params.getRemovePromiscuousProbes())
				removePromiscuousProbes(doseResponseExperiment.getReferenceGeneAnnotations(), probeHash);
			probeGeneMaps.probeGeneMaping(chip, true);
			catMap = new GOTermMap(probeGeneMaps, bmdResults.getDoseResponseExperiment().getChip(),
					params.getGoTermIdx());
			rstName += "_GO_" + BMDExpressConstants.getInstance().GO_SHORTS[params.getGoTermIdx()];
			analysisInfo.getNotes().add("Gene Ontology Analyses");
			analysisInfo.getNotes().add(
					"GO Category: " + BMDExpressConstants.getInstance().GO_SHORTS[params.getGoTermIdx()]);
			Date date = new Date(catMap.getCategoryFileVersionDate());
			SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yy");
			analysisInfo.getNotes().add("GO File Creation Date: " + df2.format(date));
		}
		else if (catAnalysisEnum == CategoryAnalysisEnum.PATHWAY)
		{
			if (params.getRemovePromiscuousProbes())
				removePromiscuousProbes(doseResponseExperiment.getReferenceGeneAnnotations(), probeHash);
			probeGeneMaps.probeGeneMaping(chip, true);
			catMap = new GenesPathways(probeGeneMaps, params.getPathwayDB());
			rstName += "_" + params.getPathwayDB();
			analysisInfo.getNotes().add("Signaling Pathway Analyses");
			analysisInfo.getNotes().add("Organism Code: " + catMap.getOrganismCode());

			Date date = new Date(catMap.getCategoryFileVersionDate());
			SimpleDateFormat df2 = new SimpleDateFormat("MM/dd/yy");
			analysisInfo.getNotes().add("Signaling Pathway File Creation Date: " + df2.format(date));

		}
		else if (catAnalysisEnum == CategoryAnalysisEnum.DEFINED)
		{
			if (params.getRemovePromiscuousProbes())
				removePromiscuousProbes(params.getProbeFileParameters().getUsedColumns()[0],
						params.getProbeFileParameters().getUsedColumns()[1],
						params.getProbeFileParameters().getMatrixData(), probeHash);
			probeGeneMaps.probeGeneMaping(chip, true);
			ProbeCategoryMaps probeCategoryGeneMaps = new ProbeCategoryMaps(bmdResults);
			probeCategoryGeneMaps.readProbes(true);
			// probeCategoryGeneMaps.readArraysInfo();
			probeCategoryGeneMaps.setProbesHash(probeHash);
			probeCategoryGeneMaps.probeGeneMaping(params.getProbeFileParameters().getUsedColumns()[0],
					params.getProbeFileParameters().getUsedColumns()[1],
					params.getProbeFileParameters().getMatrixData());
			catMap = new CategoryMap(params.getCategoryFileParameters().getUsedColumns()[0],
					params.getCategoryFileParameters().getUsedColumns()[1],
					params.getCategoryFileParameters().getUsedColumns()[2],
					params.getCategoryFileParameters().getMatrixData().getData(), probeCategoryGeneMaps);

			File catFile = new File(params.getCategoryFileParameters().getFileName());
			String catFileName = catFile.getName();

			rstName += "_DEFINED-" + catFileName.substring(0, catFileName.lastIndexOf('.'));;
			analysisInfo.getNotes().add("Defined Category Analyses");

			probeGeneMaps = probeCategoryGeneMaps;
		}

		analysisInfo.getNotes().add("Data Source: " + bmdResults.getDoseResponseExperiment().getName());
		analysisInfo.getNotes().add("Work Source: " + bmdResults.getName());
		analysisInfo.getNotes()
				.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
		analysisInfo.getNotes().add("Timestamp: " + BMDExpressProperties.getInstance().getTimeStamp());

		DecimalFormat df1 = new DecimalFormat("#.####");
		DecimalFormat df2 = new DecimalFormat("#.#");
		// analysis info for parameters
		if (params.getRemovePromiscuousProbes())
		{
			analysisInfo.getNotes().add("Remove Promiscuous Probes: true");
			rstName += "_true";
		}
		if (params.isRemoveBMDGreaterHighDose())
		{
			analysisInfo.getNotes()
					.add("Remove BMD > Highest Dose from Category Descriptive Statistics: true");
			rstName += "_true";
		}
		if (params.isRemoveBMDPValueLessCuttoff())
		{
			analysisInfo.getNotes().add("Remove BMD with p-Value < Cutoff: " + params.getpValueCutoff());
			rstName += "_pval" + df1.format(params.getpValueCutoff());
		}

		if (params.isRemoveBMDBMDLRatio())
		{
			analysisInfo.getNotes().add("Remove genes with BMD/BMDL >: " + params.getBmdBmdlRatio());
			rstName += "_ratio" + df1.format(params.getBmdBmdlRatio());
		}

		if (params.isRemoveBMDUBMDLRatio())
		{
			analysisInfo.getNotes().add("Remove genes with BMDU/BMDL >: " + params.getBmduBmdlRatio());
			rstName += "_ratio" + df1.format(params.getBmduBmdlRatio());
		}

		if (params.isRemoveBMDUBMDRatio())
		{
			analysisInfo.getNotes().add("Remove genes with BMDU/BMD >: " + params.getBmduBmdRatio());
			rstName += "_ratio" + df1.format(params.getBmduBmdRatio());
		}

		if (params.isRemoveNFoldBelowLowestDose())
		{
			analysisInfo.getNotes()
					.add("Remove genes with BMD values > N fold below the lowest positive does: "
							+ params.getnFoldbelowLowestDoseValue());
			rstName += "_nfold" + df2.format(params.getnFoldbelowLowestDoseValue());
		}
		if (params.isIdentifyConflictingProbeSets())
		{
			analysisInfo.getNotes().add(
					"Identify conflicting probe sets: " + params.getCorrelationCutoffConflictingProbeSets());
			rstName += "_conf" + df1.format(params.getCorrelationCutoffConflictingProbeSets());
		}

		if (catAnalysisEnum == CategoryAnalysisEnum.DEFINED)
		{
			analysisInfo.getNotes().add("Probe File: " + params.getProbeFileParameters().getFileName());
			analysisInfo.getNotes().add("Category File: " + params.getCategoryFileParameters().getFileName());
		}
		this.probeGeneMaps = probeGeneMaps;
		this.categoryGeneMap = catMap;
		this.rstName = rstName;

	}

	public CategoryAnalysisResults startAnalyses()
	{
		return categoryAnalysis();

	}

	public CategoryAnalysisResults categoryAnalysis()
	{
		bmdStats = null;

		bmdStats = new BMDStatatistics(probeGeneMaps, bmdResults);
		bmdStats.setRemoveMaxDose(params.isRemoveBMDGreaterHighDose());
		bmdStats.setMaximumDose(params.getMaxDose());
		bmdStats.setMinDose(params.getMinDose());
		bmdStats.setMinPositiveDose(params.getMinPositiveDose());

		bmdStats.setFitPvalueCutoff(params.isRemoveBMDPValueLessCuttoff(), params.getpValueCutoff());
		bmdStats.readBMDValues();

		if (params.isIdentifyConflictingProbeSets())
		{
			bmdStats.readExpressionData();

			if (!bmdStats.hasData())
			{
				params.setIdentifyConflictingProbeSets(false);
			}
			else
			{
				// Computer BMD correlation
				bmdStats.computeCorrelation(params.getCorrelationCutoffConflictingProbeSets());
			}
		}

		return createAnalysisOut();
	}

	private double parseCutoff(String value)
	{
		try
		{
			return Double.parseDouble(value);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	/*
	 * Step 4
	 */
	private CategoryAnalysisResults createAnalysisOut()
	{
		CategoryAnalysisResults categoryAnalysisResults = termsOut();

		if (categoryAnalysisResults == null)
		{
			categoryAnalysisResults = new CategoryAnalysisResults(); // return an empyt instance if termsOut()
																		// returned null;
			categoryAnalysisResults.setCategoryAnalsyisResults(new ArrayList<>());
		}
		categoryAnalysisResults.setName(rstName);

		analysisInfo.getNotes()
				.add("Category Count: " + categoryAnalysisResults.getCategoryAnalsyisResults().size());
		return categoryAnalysisResults;
	}

	/*
	 * Step 4, nonBMD
	 */
	public void abortAnalyses()
	{
		String message = "<html>This process is aborted due to data preparation failure.<br>"
				+ "Please try again or contact the software provider.</html>";
	}

	@SuppressWarnings("unchecked")
	private CategoryAnalysisResults termsOut()
	{
		CategoryAnalysisResults categoryAnalysisResults = new CategoryAnalysisResults();
		categoryAnalysisResults.setCategoryAnalsyisResults(new ArrayList<CategoryAnalysisResult>());

		int rows = categoryGeneMap.categoryMappingCount();

		if (rows == 0)
			return null;

		// get total number of genes

		int allTotal = categoryGeneMap.getAllGeneCount();

		// get total number of genes that are in this set that shows some relation here.
		int chgTotal = categoryGeneMap.getSubGeneCount();

		for (int i = 0; i < rows; i++)
		{
			// TODO: turn this into a factory
			CategoryAnalysisResult categoryAnalysisResult = null;
			if (categoryGeneMap instanceof GenesPathways)
			{
				categoryAnalysisResult = new PathwayAnalysisResult();
			}
			else if (categoryGeneMap instanceof CategoryMap)
			{
				categoryAnalysisResult = new DefinedCategoryAnalysisResult();
			}
			else
			{
				categoryAnalysisResult = new GOAnalysisResult();
			}

			categoryAnalysisResults.getCategoryAnalsyisResults().add(categoryAnalysisResult);
			categoryAnalysisResult.setCategoryIdentifier(categoryGeneMap.getCategoryIdentifier(i));

			int sub = 0, all = 0, dataSetCount = 0;
			Vector<String> subList = categoryGeneMap.subHash()
					.get(categoryGeneMap.getCategoryIdentifier(i).getId());
			Vector<String> allList = categoryGeneMap.allHash()
					.get(categoryGeneMap.getCategoryIdentifier(i).getId());

			Vector<String> dataSetList = categoryGeneMap.dataSetGeneHash()
					.get(categoryGeneMap.getCategoryIdentifier(i).getId());

			if (dataSetList != null)
			{
				dataSetCount = dataSetList.size();
			}
			if (allList != null)
			{
				all = allList.size();
			}

			if (subList != null)
			{
				sub = subList.size();
			}

			categoryAnalysisResult.setGeneAllCountFromExperiment(dataSetCount);
			categoryAnalysisResult.setGeneAllCount(all);
			categoryAnalysisResult.setGeneCountSignificantANOVA(sub);
			int currunt = 5; // current column index

			// apply the gene filters
			// first get applicable geneid to probe set hastable

			Hashtable<String, Vector> subHashG2Ids = bmdStats.getSubHashG2Ids(subList);
			Set<String> removedProbes = new HashSet<>();

			if (bmdStats != null && params.isRemoveBMDGreaterHighDose())
			{
				sub = bmdStats.checkHighestDose(subList, subHashG2Ids, removedProbes).size();

				categoryAnalysisResult.setGenesWithBMDLessEqualHighDose(sub);
			}

			if (params.isRemoveBMDPValueLessCuttoff())
			{
				sub = bmdStats.checkFitPCutoff(subList, subHashG2Ids, removedProbes).size();

				categoryAnalysisResult.setGenesWithBMDpValueGreaterEqualValue(sub);
			}

			if (params.isRemoveBMDBMDLRatio())
			{
				sub = bmdStats
						.checkBMDBMDLRatio(subList, params.getBmdBmdlRatio(), subHashG2Ids, removedProbes)
						.size();
				categoryAnalysisResult.setGenesWithBMDBMDLRatioBelowValue(sub);
			}

			if (params.isRemoveBMDUBMDRatio())
			{
				sub = bmdStats
						.checkBMDUBMDRatio(subList, params.getBmduBmdRatio(), subHashG2Ids, removedProbes)
						.size();
				categoryAnalysisResult.setGenesWithBMDUBMDRatioBelowValue(sub);
			}
			if (params.isRemoveBMDUBMDLRatio())
			{
				sub = bmdStats
						.checkBMDUBMDLRatio(subList, params.getBmduBmdlRatio(), subHashG2Ids, removedProbes)
						.size();
				categoryAnalysisResult.setGenesWithBMDUBMDLRatioBelowValue(sub);
			}

			if (params.isRemoveNFoldBelowLowestDose())
			{
				sub = bmdStats.checkNFoldBelowLowestDose(subList, params.getnFoldbelowLowestDoseValue(),
						subHashG2Ids, removedProbes).size();
				categoryAnalysisResult.setGenesWithNFoldBelowLowPostiveDoseValue(sub);
			}

			subList = bmdStats.getFinalList(subList, subHashG2Ids, removedProbes);

			sub = subList.size();
			categoryAnalysisResult.setAllGenesPassedAllFilters(sub);
			// calculate Fisher's exact test values
			double[] triple = fisherExactTest(sub, chgTotal, all, allTotal);
			categoryAnalysisResult.setFishersExactLeftPValue(triple[0]);
			categoryAnalysisResult.setFishersExactRightPValue(triple[1]);
			categoryAnalysisResult.setFishersExactTwoTailPValue(triple[2]);
			categoryAnalysisResult.setPercentage(probeGeneMaps.percentage(sub, all, 2));
			// categoryAnalysisResult.setGeneIDs(probeGeneMaps.vectorGenes2String(subList));
			// categoryAnalysisResult.setProbeIds(probeGeneMaps.genesProbes2String(subList));

			if (params.isIdentifyConflictingProbeSets()) // need to initialize this
			{
				categoryAnalysisResult.setGenesWithConflictingProbeSets("");
			}
			if (bmdStats != null)
			{
				if (sub > 0)
				{ // bmdStats.hasData() &&
					bmdStats.computeStatistic(currunt, subList, allList, categoryAnalysisResult,
							subHashG2Ids);
				}
			}

			if (i % 10 == 0)
			{
				categoryMapProgress.updateProgress(
						"Processing record: " + String.valueOf(i) + "/" + String.valueOf(rows),
						(double) i / (double) rows);
			}

		}

		return categoryAnalysisResults;
	}

	/**
	 *
	 * @return left, right and two-tail p-Values
	 */
	private double[] fisherExactTest(int sub, int chgTotal, int all, int allTotal)
	{
		// sub = number of genes that has bmd for a go term
		// chgTotal = total number of genes that has a bmd
		// all = number genes for the go term
		// allTotal = total number genes associated with goterm mapping.
		int a = sub;
		int b = chgTotal - a;
		int c = all - a;
		int d = allTotal - a - b - c;
		double[] pValues = { 1, 1, 1 };

		if (a >= 0 && b >= 0 && c >= 0 && d >= 0)
		{
			FishersExact test = new FishersExact(a, b, c, d);
			pValues[0] = NumberManager.numberFormat(5, test.pLeft());
			pValues[1] = NumberManager.numberFormat(5, test.pRight());
			pValues[2] = NumberManager.numberFormat(5, test.twoTail());
			if (pValues[2] < .01)
				System.out.println();
		}

		return pValues;// NumberManager.numberFormat(4, pValues);
	}

	// for default annotations.
	private void removePromiscuousProbes(List<ReferenceGeneAnnotation> refGeneAnnotations,
			Hashtable<String, Integer> probeHash)
	{
		for (ReferenceGeneAnnotation rga : refGeneAnnotations)
			if (rga.getReferenceGenes().size() > 1)
				probeHash.remove(rga.getProbe().getId());
	}

	// for defined category analysis
	private void removePromiscuousProbes(int probeIndex, int geneIndex, MatrixData md,
			Hashtable<String, Integer> probeHash)
	{

		// create mapping
		Map<String, Set<String>> genes2Probe = new HashMap<>();

		for (int i = 0; i < md.getData().length; i++)
		{
			String probe = (String) md.getData()[i][probeIndex];
			String gene = (String) md.getData()[i][geneIndex];

			if (!genes2Probe.containsKey(probe))
				genes2Probe.put(probe, new HashSet<>());
			else if (genes2Probe.get(probe).size() > 1)
				probeHash.remove(probe);

			genes2Probe.get(probe).add(gene);

		}

	}

}
