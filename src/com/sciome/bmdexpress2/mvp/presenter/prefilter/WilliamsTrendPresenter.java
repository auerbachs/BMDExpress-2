package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.MatrixUtils;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IWilliamsTrendView;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.util.prefilter.FoldChange;
import com.sciome.commons.math.WilliamsTrendTestResult;
import com.sciome.commons.math.WilliamsTrendTestUtil;

public class WilliamsTrendPresenter extends PresenterBase<IWilliamsTrendView>{
	public WilliamsTrendPresenter(IWilliamsTrendView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	/*
	 * do williams trend filter
	 */
	public void performWilliamsTrend(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue)
	{

		for (IStatModelProcessable pData : processableData)
		{
			performWilliamsTrend(pData, pCutOff, multipleTestingCorrection, filterOutControlGenes,
					useFoldFilter, foldFilterValue);
		}

	}

	/*
	 * do williams trend filter
	 */
	public WilliamsTrendResults performWilliamsTrend(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue)
	{
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
		
		WilliamsTrendTestResult result = WilliamsTrendTestUtil.williams(MatrixUtils.createRealMatrix(numericMatrix),
																		MatrixUtils.createRealVector(doseVector),
																		23524,
																		100,
																		null);
		List<WilliamsTrendResult> williamsTrendResultList = new ArrayList<WilliamsTrendResult>();
		for(int i = 0; i < result.getTestStatistic().getDimension(); i++) {
			WilliamsTrendResult singleResult = new WilliamsTrendResult();
			singleResult.setAdjustedPValue(result.getAdjustedPValue().getEntry(i));
			singleResult.setpValue(result.getpValue().getEntry(i));
			singleResult.setProbeResponse(responses.get(i));
			williamsTrendResultList.add(singleResult);
		}

		// now apply the filters to the list and remove items that don't match up
		int resultSize = williamsTrendResultList.size();

		for (int i = 0; i < resultSize; i++)
		{
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

		// post the new oneway object to the event bus so folks can do the right thing.
		getEventBus().post(new WilliamsTrendDataLoadedEvent(williamsTrendResults));

		return williamsTrendResults;

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

	/*
	 * Private Methods
	 */
	private void init()
	{
	}

	@Subscribe
	public void onProjectLoadedEvent(BMDProjectLoadedEvent event)
	{

		getView().closeWindow();
	}

	@Subscribe
	public void onProjectClosedEvent(CloseProjectRequestEvent event)
	{

		getView().closeWindow();
	}
}
