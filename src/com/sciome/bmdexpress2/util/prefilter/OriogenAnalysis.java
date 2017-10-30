package com.sciome.bmdexpress2.util.prefilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.commons.interfaces.SimpleProgressUpdater;
import com.sciome.commons.math.MathUtil;
import com.sciome.commons.math.oriogen.Origen_Data;
import com.sciome.commons.math.oriogen.OriogenTestResult;
import com.sciome.commons.math.oriogen.OriogenUtil;

public class OriogenAnalysis {
	private OriogenUtil util = new OriogenUtil();
	private boolean cancel = false;
	
	public OriogenResults analyzeDoseResponseData(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, int initialBootstraps, int maxBootstraps, 
			float s0Adjustment, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, SimpleProgressUpdater updater) {
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
		
		ArrayList<OriogenTestResult> result = util.oriogen(data, updater);

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

		performFoldFilter(oriogenResultList, processableData, Float.valueOf(foldFilterValue),
				isLogTransformation, baseValue, useFoldFilter);

		// create a new OriogenResults object and put it on the Event BuS
		OriogenResults oriogenResults = new OriogenResults();
		oriogenResults.setDoseResponseExperiement(doseResponseExperiment);
		oriogenResults.setOriogenResults(oriogenResultList);

		DecimalFormat df = new DecimalFormat("#.####");
		String name = doseResponseExperiment.getName() + "_oriogen_" + df.format(pCutOff);

		AnalysisInfo analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		notes.add("Oriogen");
		notes.add("Data Source: " + processableData);
		notes.add("Work Source: " + processableData.getParentDataSetName());
		notes.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
		notes.add("Timestamp: " + BMDExpressProperties.getInstance().getTimeStamp());
		notes.add("Adjusted P-Value Cutoff: " + df.format(pCutOff));
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
	/*
	 * perform fold filter
	 */
	private void performFoldFilter(List<OriogenResult> oriogenResults,
			IStatModelProcessable processableData, Float foldFilterValue, boolean isLogTransformation,
			double baseValue, boolean useFoldFilter)
	{

		int resultSize = oriogenResults.size();

		FoldChange foldChange = new FoldChange(
				processableData.getProcessableDoseResponseExperiment().getTreatments(), isLogTransformation,
				baseValue);
		for (int i = 0; i < resultSize; i++)
		{
			Float bestFoldChange = foldChange
					.getBestFoldChangeValue(oriogenResults.get(i).getProbeResponse().getResponses());
			oriogenResults.get(i).setBestFoldChange(bestFoldChange);
			oriogenResults.get(i).setFoldChanges(foldChange.getFoldChanges());

			if (useFoldFilter && Math.abs(bestFoldChange) < Math.abs(foldFilterValue))
			{
				oriogenResults.remove(i);
				i--;
				resultSize--;
			}
		}
	}
	
	
	public void cancel() {
		util.cancel();
	}
}
