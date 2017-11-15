package com.sciome.bmdexpress2.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.serviceInterface.IPrefilterService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.prefilter.FoldChange;
import com.sciome.bmdexpress2.util.prefilter.OneWayANOVAAnalysis;
import com.sciome.commons.interfaces.SimpleProgressUpdater;
import com.sciome.commons.math.MathUtil;
import com.sciome.commons.math.oriogen.Origen_Data;
import com.sciome.commons.math.oriogen.OriogenTestResult;
import com.sciome.commons.math.oriogen.OriogenUtil;
import com.sciome.commons.math.williams.WilliamsTrendTestResult;
import com.sciome.commons.math.williams.WilliamsTrendTestUtil;

public class PrefilterService implements IPrefilterService {
	private WilliamsTrendTestUtil williamsUtil = new WilliamsTrendTestUtil();
	private OriogenUtil oriogenUtil = new OriogenUtil();
	private boolean cancel = false;

	/**
	 * Performs a william's trend analysis and returns the corresponding WilliamsTrendResult object
	 */
	@Override
	public WilliamsTrendResults williamsTrendAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermutations, SimpleProgressUpdater updater) {
		DoseResponseExperiment doseResponseExperiment = processableData
				.getProcessableDoseResponseExperiment();
		
		double baseValue = 2.0;
		boolean isLogTransformation = true;
		if (processableData.getLogTransformation().equals(LogTransformationEnum.BASE10))
			baseValue = 10.0f;
		else if (processableData.getLogTransformation().equals(LogTransformationEnum.NATURAL))
			baseValue = 2.718281828459045;
		else if (processableData.getLogTransformation().equals(LogTransformationEnum.NONE))
			isLogTransformation = false;
		
		// get a list of williamsTrendResult
		List<ProbeResponse> responses = doseResponseExperiment.getProbeResponses();
		List<Treatment> treatments = doseResponseExperiment.getTreatments();
		double[][] numericMatrix = new double[responses.size()][responses.get(0).getResponses().size()];
		double[] doseVector = new double[treatments.size()];
		
		//Fill numeric matrix
		for(int i = 0; i < numericMatrix.length; i++) {
			for(int j = 0; j < numericMatrix[i].length; j++) {
				numericMatrix[i][j] = responses.get(i).getResponses().get(j);
			}
		}
		
		//Fill doseVector
		for(int i = 0; i < doseVector.length; i++) {
			doseVector[i] = treatments.get(i).getDose();
		}
		
		WilliamsTrendTestResult result = williamsUtil.williams(MatrixUtils.createRealMatrix(numericMatrix),
				MatrixUtils.createRealVector(doseVector),
				23524,
				Integer.valueOf(numberOfPermutations),
				null,
				updater);

		if(result == null) {
			updater.setProgress(0);
			return null;
		}
		
		List<WilliamsTrendResult> williamsTrendResultList = new ArrayList<WilliamsTrendResult>();
		for(int i = 0; i < result.getTestStatistic().getDimension(); i++) {
			if(!cancel) {
				WilliamsTrendResult singleResult = new WilliamsTrendResult();
				singleResult.setAdjustedPValue(result.getAdjustedPValue().getEntry(i));
				singleResult.setpValue(result.getpValue().getEntry(i));
				singleResult.setProbeResponse(responses.get(i));
				williamsTrendResultList.add(singleResult);
			} else {
				updater.setProgress(0);
				return null;
			}
		}
		// now apply the filters to the list and remove items that don't match up
		int resultSize = williamsTrendResultList.size();

		for (int i = 0; i < resultSize; i++)
		{
			if(!cancel) {
				WilliamsTrendResult williamsTrendResult = williamsTrendResultList.get(i);

				double pValueToCheck = williamsTrendResult.getpValue();

				if (multipleTestingCorrection)
				{
					pValueToCheck = williamsTrendResult.getAdjustedPValue();
				}

				// check if control gene
				if (
				// first check the pValue
				((Double.isNaN(pValueToCheck) && pCutOff < 9999) || pValueToCheck >= pCutOff) ||
				// second check if it is a control gene
						(filterOutControlGenes
								&& williamsTrendResult.getProbeResponse().getProbe().getId().startsWith("AFFX"))

				)
				{
					williamsTrendResultList.remove(i);
					i--;
					resultSize--;
				}
			} else {
				updater.setProgress(0);
				return null;
			}
		}
		

		// create a new WilliamsTrendResults object and put it on the Event BuS
		WilliamsTrendResults williamsTrendResults = new WilliamsTrendResults();
		williamsTrendResults.setDoseResponseExperiement(doseResponseExperiment);
		williamsTrendResults.setWilliamsTrendResults(williamsTrendResultList);
		
		performFoldFilter(williamsTrendResults, processableData, Float.valueOf(foldFilterValue),
				isLogTransformation, baseValue, useFoldFilter);
		
		DecimalFormat df = new DecimalFormat("#.####");
		String name = doseResponseExperiment.getName() + "_williams_" + df.format(pCutOff);

		AnalysisInfo analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		notes.add("William's Trend");
		notes.add("Data Source: " + processableData);
		notes.add("Work Source: " + processableData.getParentDataSetName());
		notes.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
		notes.add("Timestamp: " + BMDExpressProperties.getInstance().getTimeStamp());

		notes.add("Number of Permutations: " + String.valueOf(numberOfPermutations));
		
		if(multipleTestingCorrection)
			notes.add("Adjusted P-Value Cutoff: " + df.format(pCutOff));
		else
			notes.add("Unadjusted P-Value Cutoff: " + df.format(pCutOff));
		
		notes.add("Multiple Testing Correction: " + String.valueOf(multipleTestingCorrection));
		notes.add("Filter Out Control Genes: " + String.valueOf(filterOutControlGenes));

		if (multipleTestingCorrection)
		{
			name += "_MTC";
		}
		else
		{
			name += "_NOMTC";
		}
		if (useFoldFilter)
		{
			notes.add("Used Fold Filter with cuttoff: " + foldFilterValue);
			notes.add("Data marked as log transformation: " + String.valueOf(isLogTransformation));
			name += "_foldfilter" + foldFilterValue;
		}
		else
		{
			name += "_nofoldfilter";
		}
		williamsTrendResults.setName(name);
		analysisInfo.setNotes(notes);
		williamsTrendResults.setAnalysisInfo(analysisInfo);
		return williamsTrendResults;
	}

	/**
	 * Performs an oriogen analysis and returns the corresponding OriogenResults object
	 */
	@Override
	public OriogenResults oriogenAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, int initialBootstraps, int maxBootstraps, float s0Adjustment,
			boolean filterOutControlGenes, boolean useFoldFilter, String foldFilterValue,
			SimpleProgressUpdater updater) {
		DoseResponseExperiment doseResponseExperiment = processableData
				.getProcessableDoseResponseExperiment();

		Origen_Data data = new Origen_Data();
		
		double baseValue = 2.0;
		boolean isLogTransformation = true;
		if (processableData.getLogTransformation().equals(LogTransformationEnum.BASE10)) {
			data.setLogTransformType(4);
			baseValue = 10.0f;
		}
		else if (processableData.getLogTransformation().equals(LogTransformationEnum.NATURAL)) {
			data.setLogTransformType(3);
			baseValue = 2.718281828459045;
		}
		else if (processableData.getLogTransformation().equals(LogTransformationEnum.NONE)) {
			data.setLogTransformType(1);
			isLogTransformation = false;
		} else {
			data.setLogTransformType(2);
		}
		
		// get a list of oriogenResult
		List<ProbeResponse> responses = doseResponseExperiment.getProbeResponses();
		List<Treatment> treatments = doseResponseExperiment.getTreatments();
		double[][] numericMatrix = new double[responses.size()][responses.get(0).getResponses().size()];
		double[] doseVector = new double[treatments.size()];
		
		//Fill numeric matrix
		for(int i = 0; i < numericMatrix.length; i++) {
			for(int j = 0; j < numericMatrix[i].length; j++) {
				numericMatrix[i][j] = responses.get(i).getResponses().get(j);
			}
		}
		
		//Fill doseVector
		double current = doseVector[0];
		int count = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < doseVector.length; i++) {
			doseVector[i] = treatments.get(i).getDose();
			if(current == doseVector[i]) {
				count++;
			} else {
				list.add(count);
				count = 1;
			}
			current = doseVector[i];
		}
		
		data.setInputData(MatrixUtils.createRealMatrix(numericMatrix));
		data.setNumTimePoints(MathUtil.uniqueValues(MatrixUtils.createRealVector(doseVector)));
		data.setNumInitialBootStraps(initialBootstraps);
		data.setFDRLevel(1);
		data.setRandomSeed(23524);
		data.setSubRegionPValue(.1);
		data.setTranspose(false);
		data.setMaxNumBootStraps(maxBootstraps);
		data.setLongitudinalSampling(false);
		data.setS0Percentile(s0Adjustment);
		data.setmdFdr(false);
		data.setTwoGroups(false);
		
		int[] values = new int[30];
		for(int i = 0; i < values.length; i++) {
			if(i < list.size()) {
				values[i] = list.get(i);
			} else {
				values[i] = list.get(list.size() - 1);
			}
		}
		data.setSampleSizeDefault(values);
		data.setNumGenes(numericMatrix.length);
		
		ArrayList<OriogenTestResult> result = oriogenUtil.oriogen(data, updater);

		if(result == null) {
			updater.setProgress(0);
			return null;
		}
		
		List<OriogenResult> oriogenResultList = new ArrayList<OriogenResult>();
		for(int i = 0; i < result.size(); i++) {
			if(!cancel) {
				OriogenResult singleResult = new OriogenResult();
				singleResult.setAdjustedPValue(result.get(i).getqValue());
				singleResult.setpValue(result.get(i).getpValue());
				singleResult.setProbeResponse(responses.get(i));
				singleResult.setProfile(result.get(i).getProfileString());
				oriogenResultList.add(singleResult);
			} else {
				updater.setProgress(0);
				return null;
			}
		}
		// now apply the filters to the list and remove items that don't match up
		int resultSize = oriogenResultList.size();

		for (int i = 0; i < resultSize; i++)
		{
			if(!cancel) {
				OriogenResult oriogenResult = oriogenResultList.get(i);

				double pValueToCheck = oriogenResult.getpValue();

				if (multipleTestingCorrection)
				{
					pValueToCheck = oriogenResult.getAdjustedPValue();
				}

				// check if control gene
				if (
				// first check the pValue
				((Double.isNaN(pValueToCheck) && pCutOff < 9999) || pValueToCheck >= pCutOff) ||
				// second check if it is a control gene
						(filterOutControlGenes
								&& oriogenResult.getProbeResponse().getProbe().getId().startsWith("AFFX"))

				)
				{
					oriogenResultList.remove(i);
					i--;
					resultSize--;
				}
			} else {
				updater.setProgress(0);
				return null;
			}
		}


		// create a new OriogenResults object and put it on the Event BuS
		OriogenResults oriogenResults = new OriogenResults();
		oriogenResults.setDoseResponseExperiement(doseResponseExperiment);
		oriogenResults.setOriogenResults(oriogenResultList);

		performFoldFilter(oriogenResults, processableData, Float.valueOf(foldFilterValue),
				isLogTransformation, baseValue, useFoldFilter);
		
		DecimalFormat df = new DecimalFormat("#.####");
		String name = doseResponseExperiment.getName() + "_oriogen_" + df.format(pCutOff);

		AnalysisInfo analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		notes.add("Oriogen");
		notes.add("Data Source: " + processableData);
		notes.add("Work Source: " + processableData.getParentDataSetName());
		notes.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
		notes.add("Timestamp: " + BMDExpressProperties.getInstance().getTimeStamp());
		
		if(multipleTestingCorrection)
			notes.add("Adjusted P-Value Cutoff: " + df.format(pCutOff));
		else
			notes.add("Unadjusted P-Value Cutoff: " + df.format(pCutOff));
		
		notes.add("Number of Initial Bootstraps: " + String.valueOf(initialBootstraps));
		notes.add("Number of Maximum Bootstraps: " + String.valueOf(maxBootstraps));
		notes.add("Shrinkage Adjustment Percentile: " + String.valueOf(s0Adjustment));
		notes.add("Multiple Testing Correction: " + String.valueOf(multipleTestingCorrection));
		notes.add("Filter Out Control Genes: " + String.valueOf(filterOutControlGenes));

		if (multipleTestingCorrection)
		{
			name += "_MTC";
		}
		else
		{
			name += "_NOMTC";
		}
		if (useFoldFilter)
		{
			notes.add("Used Fold Filter with cuttoff: " + foldFilterValue);
			notes.add("Data marked as log transformation: " + String.valueOf(isLogTransformation));
			name += "_foldfilter" + foldFilterValue;
		}
		else
		{
			name += "_nofoldfilter";
		}
		oriogenResults.setName(name);
		analysisInfo.setNotes(notes);
		oriogenResults.setAnalysisInfo(analysisInfo);
		return oriogenResults;
	}

	/**
	 * Performs a one way anova analysis and returns the corresponding OneWayANOVAResults object
	 */
	@Override
	public OneWayANOVAResults oneWayANOVAAnalysis(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue) {
		DoseResponseExperiment doseResponseExperiment = processableData
				.getProcessableDoseResponseExperiment();
		//This class should eventually be moved to sciome commons
		OneWayANOVAAnalysis aNOVAAnalysis = new OneWayANOVAAnalysis();

		double baseValue = 2.0;
		boolean isLogTransformation = true;
		if (processableData.getLogTransformation().equals(LogTransformationEnum.BASE10))
			baseValue = 10.0f;
		else if (processableData.getLogTransformation().equals(LogTransformationEnum.NATURAL))
			baseValue = 2.718281828459045;
		else if (processableData.getLogTransformation().equals(LogTransformationEnum.NONE))
			isLogTransformation = false;

		// get a list of oneWayResults
		List<OneWayANOVAResult> oneWayResultList = aNOVAAnalysis.analyzeDoseResponseData(processableData);

		// now apply the filters to the list and remove items that don't match up
		int resultSize = oneWayResultList.size();

		for (int i = 0; i < resultSize; i++)
		{
			OneWayANOVAResult oneWayResult = oneWayResultList.get(i);

			double pValueToCheck = oneWayResult.getpValue();

			if (multipleTestingCorrection)
			{
				pValueToCheck = oneWayResult.getAdjustedPValue();
			}

			// check if control gene
			if (
			// first check the pValue
			((Double.isNaN(pValueToCheck) && pCutOff < 9999) || pValueToCheck >= pCutOff) ||
			// second check if it is a control gene
					(filterOutControlGenes
							&& oneWayResult.getProbeResponse().getProbe().getId().startsWith("AFFX"))

			)
			{
				oneWayResultList.remove(i);
				i--;
				resultSize--;
			}

		}

		// create a new OneWayANOVAAnaylisResults object and put it on the Event BuS
		OneWayANOVAResults oneWayResults = new OneWayANOVAResults();
		oneWayResults.setDoseResponseExperiement(doseResponseExperiment);
		oneWayResults.setOneWayANOVAResults(oneWayResultList);

		performFoldFilter(oneWayResults, processableData, Float.valueOf(foldFilterValue),
				isLogTransformation, baseValue, useFoldFilter);
		
		DecimalFormat df = new DecimalFormat("#.####");
		String name = doseResponseExperiment.getName() + "_oneway_" + df.format(pCutOff);

		AnalysisInfo analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		notes.add("One-way ANOVA");
		notes.add("Data Source: " + processableData);
		notes.add("Work Source: " + processableData.getParentDataSetName());
		notes.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
		notes.add("Timestamp: " + BMDExpressProperties.getInstance().getTimeStamp());

		if(multipleTestingCorrection)
			notes.add("Adjusted P-Value Cutoff: " + df.format(pCutOff));
		else
			notes.add("Unadjusted P-Value Cutoff: " + df.format(pCutOff));
		
		notes.add("Multiple Testing Correction: " + String.valueOf(multipleTestingCorrection));
		notes.add("Filter Out Control Genes: " + String.valueOf(filterOutControlGenes));

		if (multipleTestingCorrection)
		{
			name += "_MTC";
		}
		else
		{
			name += "_NOMTC";
		}
		if (useFoldFilter)
		{
			notes.add("Used Fold Filter with cuttoff: " + foldFilterValue);
			notes.add("Data marked as log transformation: " + String.valueOf(isLogTransformation));
			name += "_foldfilter" + foldFilterValue;
		}
		else
		{
			name += "_nofoldfilter";
		}
		oneWayResults.setName(name);
		analysisInfo.setNotes(notes);
		oneWayResults.setAnalysisInfo(analysisInfo);

		return oneWayResults;
	}
	
	public void cancel() {
		williamsUtil.cancel();
		oriogenUtil.cancel();
		this.cancel = true;
	}
	
	private void performFoldFilter(PrefilterResults prefilterResults, IStatModelProcessable processableData,
			Float foldFilterValue, boolean isLogTransformation, double baseValue, boolean useFoldFilter) {
		int resultSize = prefilterResults.getPrefilterResults().size();

		FoldChange foldChange = new FoldChange(
				processableData.getProcessableDoseResponseExperiment().getTreatments(), isLogTransformation,
				baseValue);
		for (int i = 0; i < resultSize; i++)
		{
			Float bestFoldChange = foldChange
					.getBestFoldChangeValue(prefilterResults.getPrefilterResults().get(i).getProbeResponse().getResponses());
			prefilterResults.getPrefilterResults().get(i).setBestFoldChange(bestFoldChange);
			prefilterResults.getPrefilterResults().get(i).setFoldChanges(foldChange.getFoldChanges());

			if (useFoldFilter && Math.abs(bestFoldChange) < Math.abs(foldFilterValue))
			{
				prefilterResults.getPrefilterResults().remove(i);
				i--;
				resultSize--;
			}

		}
	}
}
