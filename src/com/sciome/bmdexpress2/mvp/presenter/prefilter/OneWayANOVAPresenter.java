package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOneWayANOVAView;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.util.prefilter.FoldChange;
import com.sciome.bmdexpress2.util.prefilter.OneWayANOVAAnalysis;

//Soure
public class OneWayANOVAPresenter extends PresenterBase<IOneWayANOVAView>
{
	public OneWayANOVAPresenter(IOneWayANOVAView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	/*
	 * do one way anova filter
	 */
	public void performOneWayANOVA(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, boolean isLogTransformation, double baseValue)
	{

		for (IStatModelProcessable pData : processableData)
		{
			performOneWayANOVA(pData, pCutOff, multipleTestingCorrection, filterOutControlGenes,
					useFoldFilter, foldFilterValue, isLogTransformation, baseValue);
		}

	}

	/*
	 * do one way anova filter
	 */
	public void performOneWayANOVA(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, boolean isLogTransformation, double baseValue)
	{
		DoseResponseExperiment doseResponseExperiment = processableData
				.getProcessableDoseResponseExperiment();
		OneWayANOVAAnalysis aNOVAAnalysis = new OneWayANOVAAnalysis();

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
			((Double.isNaN(pValueToCheck) && pCutOff <9999) || pValueToCheck >= pCutOff) ||
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

		performFoldFilter(oneWayResultList, processableData, Float.valueOf(foldFilterValue),
				isLogTransformation, baseValue, useFoldFilter);

		// create a new OneWayANOVAAnaylisResults object and put it on the Event BuS
		OneWayANOVAResults oneWayResults = new OneWayANOVAResults();
		oneWayResults.setDoseResponseExperiement(doseResponseExperiment);
		oneWayResults.setOneWayANOVAResults(oneWayResultList);

		DecimalFormat df = new DecimalFormat("#.####");
		String name = doseResponseExperiment.getName() + "_oneway_" + df.format(pCutOff);

		AnalysisInfo analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		notes.add("One-way ANOVA");
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
		oneWayResults.setName(name);
		analysisInfo.setNotes(notes);
		oneWayResults.setAnalysisInfo(analysisInfo);

		// post the new oneway object to the event bus so folks can do the right thing.
		getEventBus().post(new OneWayANOVADataLoadedEvent(oneWayResults));

	}

	/*
	 * fold filter
	 */
	private void performFoldFilter(List<OneWayANOVAResult> oneWayResults,
			IStatModelProcessable processableData, Float foldFilterValue, boolean isLogTransformation,
			double baseValue, boolean useFoldFilter)
	{

		int resultSize = oneWayResults.size();

		FoldChange foldChange = new FoldChange(
				processableData.getProcessableDoseResponseExperiment().getTreatments(), isLogTransformation,
				baseValue);
		for (int i = 0; i < resultSize; i++)
		{

			Float bestFoldChange = foldChange
					.getBestFoldChangeValue(oneWayResults.get(i).getProbeResponse().getResponses());
			oneWayResults.get(i).setBestFoldChange(bestFoldChange);
			oneWayResults.get(i).setFoldChanges(foldChange.getFoldChanges());

			if (useFoldFilter && Math.abs(bestFoldChange) < Math.abs(foldFilterValue))
			{
				oneWayResults.remove(i);
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
