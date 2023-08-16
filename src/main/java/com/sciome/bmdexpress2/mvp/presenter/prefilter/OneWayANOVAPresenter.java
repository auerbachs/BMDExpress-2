package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IPrefilterView;
import com.sciome.bmdexpress2.serviceInterface.IPrefilterService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.commons.interfaces.SimpleProgressUpdater;

import javafx.application.Platform;
import javafx.concurrent.Task;

//Soure
public class OneWayANOVAPresenter extends ServicePresenterBase<IPrefilterView, IPrefilterService>
		implements SimpleProgressUpdater
{
	private volatile boolean running = false;

	public OneWayANOVAPresenter(IPrefilterView view, IPrefilterService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
	}

	/*
	 * Do multiple one way anova filter calculations
	 */
	public void performOneWayANOVA(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String loelPValue, String loelFoldChange, String numThreads,
			boolean tTest)
	{
		SimpleProgressUpdater me = this;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					for (int i = 0; i < processableData.size(); i++)
					{
						if (running)
						{
							setDatasetLabel((i + 1) + "/" + processableData.size());

							// Set cancel to be false in case the service was cancelled before
							getService().start();
							OneWayANOVAResults result = getService().oneWayANOVAAnalysis(
									processableData.get(i), pCutOff, multipleTestingCorrection,
									filterOutControlGenes, useFoldFilter, Double.valueOf(foldFilterValue),
									Double.valueOf(loelPValue), Double.valueOf(loelFoldChange),
									Integer.valueOf(numThreads), me, tTest);
							me.setProgress(0);

							Platform.runLater(() ->
							{
								getEventBus().post(new OneWayANOVADataLoadedEvent(result));
							});
						}
					}
				}
				catch (Exception e)
				{
					Platform.runLater(() ->
					{
						getEventBus().post(new ShowErrorEvent(e.toString()));

					});
					e.printStackTrace();
				}

				// Only close the view if the process was running
				if (running)
				{
					Platform.runLater(() ->
					{
						getView().closeWindow();
					});
				}
				return 0;
			}
		};
		new Thread(task).start();
	}

	/*
	 * do one way anova filter
	 */
	public void performOneWayANOVA(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String loelPValue, String loelFoldChange, String numThreads,
			boolean tTest)
	{
		SimpleProgressUpdater me = this;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					// Set cancel to be false in case the service was cancelled before
					getService().start();
					OneWayANOVAResults oneWayResults = getService().oneWayANOVAAnalysis(processableData,
							pCutOff, multipleTestingCorrection, filterOutControlGenes, useFoldFilter,
							Double.valueOf(foldFilterValue), Double.valueOf(loelPValue),
							Double.valueOf(loelFoldChange), Integer.valueOf(numThreads), me, tTest);

					// post the new oneway object to the event bus so folks can do the right thing.
					if (oneWayResults != null && running)
					{
						Platform.runLater(() ->
						{
							getEventBus().post(new OneWayANOVADataLoadedEvent(oneWayResults));
						});
					}
				}
				catch (Exception e)
				{
					Platform.runLater(() ->
					{
						getEventBus().post(new ShowErrorEvent(e.toString()));

					});
					e.printStackTrace();
				}
				// Only close the view if the process was running
				if (running)
				{
					Platform.runLater(() ->
					{
						getView().closeWindow();
					});
				}
				return 0;
			}
		};

		new Thread(task).start();
	}

	public boolean hasStartedTask()
	{
		return running;
	}

	public void cancel()
	{
		setMessage("");
		setDatasetLabel("");
		setProgress(0.0);
		running = false;
		getService().cancel();
	}

	@Override
	public void setProgress(double progress)
	{
		if (running)
		{
			Platform.runLater(() ->
			{
				getView().updateProgress(progress);
			});
		}
	}

	@Override
	public void setMessage(String message)
	{
		if (running)
		{
			Platform.runLater(() ->
			{
				getView().updateMessage(message);
			});
		}
	}

	public void setDatasetLabel(String message)
	{
		if (running)
		{
			Platform.runLater(() ->
			{
				getView().updateDatasetLabel(message);

			});
		}
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
