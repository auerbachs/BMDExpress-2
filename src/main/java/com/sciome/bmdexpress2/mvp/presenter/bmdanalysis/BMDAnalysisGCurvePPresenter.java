package com.sciome.bmdexpress2.mvp.presenter.bmdanalysis;

import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.bmdanalysis.IBMDAnalysisGCurvePView;
import com.sciome.bmdexpress2.serviceInterface.IBMDAnalysisService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.bmdexpress2.util.bmds.BMDSTool;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.curvep.GCurvePInputParameters;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class BMDAnalysisGCurvePPresenter extends
		ServicePresenterBase<IBMDAnalysisGCurvePView, IBMDAnalysisService> implements IBMDSToolProgress
{

	BMDSTool							bMDSTool;

	private List<IStatModelProcessable>	processableDatas;
	private boolean						cancel	= false;
	/*
	 * Constructors
	 */

	public BMDAnalysisGCurvePPresenter(IBMDAnalysisGCurvePView view, IBMDAnalysisService service,
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

	@SuppressWarnings("restriction")
	public void performBMDAnalysisGCurveP(GCurvePInputParameters inputParameters)
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
						BMDResult bMDResults = getService().bmdAnalysisGCurveP(processableData,
								inputParameters, me);

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
							BMDAnalysisGCurvePPresenter.this.getEventBus()
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

	@SuppressWarnings("restriction")
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

	@SuppressWarnings("restriction")
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
