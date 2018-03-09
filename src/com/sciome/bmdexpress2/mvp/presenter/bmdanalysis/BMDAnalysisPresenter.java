package com.sciome.bmdexpress2.mvp.presenter.bmdanalysis;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.bmdanalysis.IBMDAnalysisView;
import com.sciome.bmdexpress2.serviceInterface.IBMDAnalysisService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.bmdexpress2.util.bmds.BMDSTool;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.HillModel;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class BMDAnalysisPresenter extends ServicePresenterBase<IBMDAnalysisView, IBMDAnalysisService>
		implements IBMDSToolProgress
{

	BMDSTool							bMDSTool;

	private List<IStatModelProcessable>	processableDatas;
	private boolean						cancel	= false;
	/*
	 * Constructors
	 */

	public BMDAnalysisPresenter(IBMDAnalysisView view, IBMDAnalysisService service,
			BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
		init();
	}

	private void init()
	{
	}

	// get a list of probeResponses to process. Also the the parentName could either be Experiment Data
	// or Anova filtered data. If it is Anova filtered data then a grandparent name will be the experiment
	// data.
	public void initData(List<IStatModelProcessable> processableData)
	{

		this.processableDatas = processableData;

	}

	public void performBMDAnalysis(ModelInputParameters inputParameters,
			ModelSelectionParameters modelSelectionParameters, List<StatModel> modelsToRun)
	{
		cancel = false;

		// send this to the bmdanalysis tool so some progress can be updated.
		IBMDSToolProgress me = this;

		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				for (IStatModelProcessable processableData : processableDatas)
				{
					if (cancel)
						continue;
					try
					{
						BMDResult bMDResults = getService().bmdAnalysis(processableData, inputParameters,
								modelSelectionParameters, modelsToRun, me);

						// post a the new result set to the event bus

						Platform.runLater(() ->
						{

							getView().finishedBMDAnalysis();
							if (bMDResults != null)
							{

								getEventBus().post(new BMDAnalysisDataLoadedEvent(bMDResults));

							}

						});
					}
					catch (Exception exception)
					{
						Platform.runLater(() ->
						{
							BMDAnalysisPresenter.this.getEventBus()
									.post(new ShowErrorEvent(exception.toString()));

						});
						exception.printStackTrace();
					}
				}
				Platform.runLater(() ->
				{
					getView().closeWindow();
				});
				return 0;
			}
		};

		getView().startedBMDAnalysis();
		new Thread(task).start();

	}

	public void performReselectParameters(ModelInputParameters inputParameters,
			ModelSelectionParameters modelSelectionParameters)
	{

		// hill model is what this is for.
		for (IStatModelProcessable processableData : processableDatas)
		{
			List<StatModel> modelsToRun = new ArrayList<>();
			modelsToRun.add(new HillModel());
			bMDSTool = new BMDSTool(processableData.getProcessableProbeResponses(),
					processableData.getProcessableDoseResponseExperiment().getTreatments(), inputParameters,
					modelSelectionParameters, modelsToRun, this, processableData);
			((BMDResult) processableData).getAnalysisInfo().getNotes().add("Parameter Reselect");
			bMDSTool.selectBestModels((BMDResult) processableData);

			// refresh the tabular data inside of bmdresults
			((BMDResult) processableData).getColumnHeader();

			getEventBus().post(new BMDAnalysisDataSelectedEvent(((BMDResult) processableData)));
		}

	}

	@Override
	public void updateProgress(String label, double value)
	{
		Platform.runLater(() ->
		{
			getView().updateProgressBar(label, value);

		});

	}

	public boolean cancel()
	{
		cancel = true;
		return getService().cancel();

	}

	@Override
	public void clearProgress()
	{
		Platform.runLater(() ->
		{
			getView().clearProgressBar();
		});

	}

	@Subscribe
	public void onProjectLoadedEvent(BMDProjectLoadedEvent event)
	{
		getService().cancel();
		getView().closeWindow();
	}

	@Subscribe
	public void onProjectClosedEvent(CloseProjectRequestEvent event)
	{
		getService().cancel();
		getView().closeWindow();
	}
}
