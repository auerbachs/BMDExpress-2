package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResults;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IPathwayFilterView;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.PathWayFilterPValueEnum;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.util.prefilter.IPrefilterProgress;
import com.sciome.bmdexpress2.util.prefilter.PathwayFilterAnalysis;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class PathwayFilterPresenter extends PresenterBase<IPathwayFilterView> implements IPrefilterProgress
{
	private PathwayFilterAnalysis pathwayFilterAnalysis;

	public PathwayFilterPresenter(IPathwayFilterView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	private void validateRscript()
	{
		String rscriptPath = BMDExpressProperties.getInstance().getRscript();

		boolean cancel = false;
		while ((!new File(rscriptPath).exists() && !cancel) || !rscriptPath.toLowerCase().contains("rscript"))
		{
			File newRScriptFile = getView().checkRPath(rscriptPath);
			if (newRScriptFile == null)
			{
				cancel = true;
			}
			else
			{
				rscriptPath = newRScriptFile.getAbsolutePath();
				BMDExpressProperties.getInstance().setRscript(rscriptPath);
			}
		}

	}

	/*
	 * Private Methods
	 */
	private void init()
	{
	}

	@Override
	public void updateProgress(String label, double value)
	{
		Platform.runLater(() ->
		{
			getView().updateProgressBar(label, value);

		});

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

	public void performPathwayFilter(List<IStatModelProcessable> processableDataItems, Double pCutOff,
			Integer iterations, Double alpha, Integer threads, boolean filterOutControlGenes,
			boolean ignoreSingleTonDoseResponse, Integer minGenesPerPathway,
			PathWayFilterPValueEnum pathWayPValueEnum)
	{

		// validate that RScript is there
		validateRscript();

		// send this to the bmdanalysis tool so some progress can be updated.
		IPrefilterProgress me = this;

		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				for (IStatModelProcessable processableData : processableDataItems)
				{
					DoseResponseExperiment doseResponseExperiment = processableData
							.getProcessableDoseResponseExperiment();
					Platform.runLater(() ->
					{
						getView().updateProgressBar("Starting pathway filter", 0.0);
					});
					pathwayFilterAnalysis = new PathwayFilterAnalysis(pCutOff, iterations, alpha, threads,
							filterOutControlGenes, ignoreSingleTonDoseResponse, minGenesPerPathway,
							pathWayPValueEnum, me);

					DecimalFormat df = new DecimalFormat("#.####");

					AnalysisInfo analysisInfo = new AnalysisInfo();
					List<String> notes = new ArrayList<>();

					String name = doseResponseExperiment.getName() + "_pathwayfilter_"
							+ pathWayPValueEnum.name() + "_" + df.format(pCutOff) + "_" + iterations + "_"
							+ ignoreSingleTonDoseResponse + "_" + minGenesPerPathway;

					notes.add("Pathway Filter");
					notes.add("Data Source: " + processableData);
					notes.add("Work Source: " + processableData.getParentDataSetName());
					notes.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
					notes.add("Timestamp: " + BMDExpressProperties.getInstance().getTimeStamp());

					notes.add("Which adjusted PValue: " + pathWayPValueEnum.name());
					notes.add("P-Value Cutoff: " + df.format(pCutOff));
					notes.add("Alpha: " + df.format(alpha));
					notes.add("Threads: " + threads);
					notes.add("Iterations: " + iterations);
					notes.add("Remove Singleton Dose Responses: " + ignoreSingleTonDoseResponse);
					notes.add("Minimum Genes Per Pathway: " + minGenesPerPathway);
					notes.add("Filter Out Control Genes: " + String.valueOf(filterOutControlGenes));
					analysisInfo.setNotes(notes);
					// get a list of oneWayResults
					List<PathwayFilterResult> pathwayFilterResultList = pathwayFilterAnalysis
							.analyzeDoseResponseData(processableData, analysisInfo);

					if (pathwayFilterResultList == null)
						return 0;
					PathwayFilterResults pathwayFilterResults = new PathwayFilterResults();
					pathwayFilterResults.setDoseResponseExperiement(doseResponseExperiment);
					pathwayFilterResults.setPathwayFilterResults(pathwayFilterResultList);

					pathwayFilterResults.setName(name);

					pathwayFilterResults.setAnalysisInfo(analysisInfo);

					Platform.runLater(() ->
					{

						// post the new oneway object to the event bus so folks can do the right thing.
						getEventBus().post(new PathwayFilterDataLoadedEvent(pathwayFilterResults));

					});

				}

				Platform.runLater(() ->
				{
					getView().closeWindow();

				});
				return 0;
			}
		};

		new Thread(task).start();

	}

}
