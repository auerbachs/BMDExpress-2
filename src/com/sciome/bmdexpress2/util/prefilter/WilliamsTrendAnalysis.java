package com.sciome.bmdexpress2.util.prefilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.commons.interfaces.SimpleProgressUpdater;
import com.sciome.commons.math.WilliamsTrendTestResult;
import com.sciome.commons.math.WilliamsTrendTestUtil;

public class WilliamsTrendAnalysis {
	WilliamsTrendTestUtil util = new WilliamsTrendTestUtil();
	private boolean cancel = false;
	
	public WilliamsTrendResults analyzeDoseResponseData(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermuatations, SimpleProgressUpdater updater) {
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
		
		WilliamsTrendTestResult result = util.williams(MatrixUtils.createRealMatrix(numericMatrix),
				MatrixUtils.createRealVector(doseVector),
				23524,
				Integer.valueOf(numberOfPermuatations),
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

		performFoldFilter(williamsTrendResultList, processableData, Float.valueOf(foldFilterValue),
				isLogTransformation, baseValue, useFoldFilter);

		// create a new WilliamsTrendResults object and put it on the Event BuS
		WilliamsTrendResults williamsTrendResults = new WilliamsTrendResults();
		williamsTrendResults.setDoseResponseExperiement(doseResponseExperiment);
		williamsTrendResults.setWilliamsTrendResults(williamsTrendResultList);

		DecimalFormat df = new DecimalFormat("#.####");
		String name = doseResponseExperiment.getName() + "_williams_" + df.format(pCutOff);

		AnalysisInfo analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		notes.add("William's Trend");
		notes.add("Data Source: " + processableData);
		notes.add("Work Source: " + processableData.getParentDataSetName());
		notes.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
		notes.add("Timestamp: " + BMDExpressProperties.getInstance().getTimeStamp());

		notes.add("Adjusted P-Value Cutoff: " + df.format(pCutOff));
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
	
	public void cancel() {
		cancel = true;
		util.cancel();
	}
	
	/*
	 * perform fold filter
	 */
	private void performFoldFilter(List<WilliamsTrendResult> williamsTrendResults,
			IStatModelProcessable processableData, Float foldFilterValue, boolean isLogTransformation,
			double baseValue, boolean useFoldFilter)
	{

		int resultSize = williamsTrendResults.size();

		FoldChange foldChange = new FoldChange(
				processableData.getProcessableDoseResponseExperiment().getTreatments(), isLogTransformation,
				baseValue);
		for (int i = 0; i < resultSize; i++)
		{
			Float bestFoldChange = foldChange
					.getBestFoldChangeValue(williamsTrendResults.get(i).getProbeResponse().getResponses());
			williamsTrendResults.get(i).setBestFoldChange(bestFoldChange);
			williamsTrendResults.get(i).setFoldChanges(foldChange.getFoldChanges());

			if (useFoldFilter && Math.abs(bestFoldChange) < Math.abs(foldFilterValue))
			{
				williamsTrendResults.remove(i);
				i--;
				resultSize--;
			}

		}
	}
}
