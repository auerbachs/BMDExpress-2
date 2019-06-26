package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IPrefilterView;
import com.sciome.bmdexpress2.serviceInterface.IPrefilterService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.commons.interfaces.SimpleProgressUpdater;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class WilliamsTrendPresenter extends ServicePresenterBase<IPrefilterView, IPrefilterService> implements SimpleProgressUpdater {
	private volatile boolean running = false;
	
	public WilliamsTrendPresenter(IPrefilterView view, IPrefilterService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
	}

	/*
	 * do williams trend filter for multiple data sets (threaded)
	 */
	public void performWilliamsTrend(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermutations, String loelPValue, String loelFoldChange,
			String numThreads, boolean tTest)
	{
		SimpleProgressUpdater updater = this;

		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					for (int i = 0; i < processableData.size(); i++) {
						//Create a new updater to keep track of the progress for each data set
						if(running) {
							setDatasetLabel(i + "/" + processableData.size());
							//Set cancel to be false in case the service was cancelled before
							getService().start();
							WilliamsTrendResults result = getService().williamsTrendAnalysis(processableData.get(i), pCutOff, multipleTestingCorrection,
									filterOutControlGenes, useFoldFilter, Double.valueOf(foldFilterValue), Integer.valueOf(numberOfPermutations),
									Double.valueOf(loelPValue), Double.valueOf(loelFoldChange), Integer.valueOf(numThreads), updater, tTest);
							//Once the method is finished, set progress to 1
							updater.setProgress(1);
							//post the results as they are completed
							Platform.runLater(() ->
							{
								getEventBus().post(new WilliamsTrendDataLoadedEvent(result));
							});
						}
					}
				}
				catch (Exception exception)
				{
					Platform.runLater(() ->
					{
						getEventBus().post(new ShowErrorEvent(exception.toString()));

					});
					exception.printStackTrace();
				}
				//Only close the view if the process was running
				if(running) {
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
	 * do williams trend filter
	 */
	public void performWilliamsTrend(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermutations, String loelPValue, String loelFoldChange,
			String numThreads, boolean tTest)
	{
		SimpleProgressUpdater me = this;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					getService().start();
					WilliamsTrendResults williamsTrendResults = getService().williamsTrendAnalysis(processableData, pCutOff, multipleTestingCorrection,
							filterOutControlGenes, useFoldFilter, Double.valueOf(foldFilterValue), Integer.valueOf(numberOfPermutations),
							Double.valueOf(loelPValue), Double.valueOf(loelFoldChange), Integer.valueOf(numThreads), me, tTest);
					
					// post the new williams object to the event bus so folks can do the right thing.
					if(williamsTrendResults != null && running) {
						Platform.runLater(() ->
						{
							getEventBus().post(new WilliamsTrendDataLoadedEvent(williamsTrendResults));
						});
					}
				}
				catch (Exception exception)
				{
					Platform.runLater(() ->
					{
						getEventBus().post(new ShowErrorEvent(exception.toString()));

					});
					exception.printStackTrace();
				}
				//Only close the view if the process was running
				if(running) {
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
	
	public boolean hasStartedTask() {
		return running;
	}
	
	public void cancel() {
		setProgress(0.0);
		setMessage("");
		setDatasetLabel("");
		running = false;
		getService().cancel();
	}
	
	@Override
	public void setProgress(double progress) {
		if(running) {
			Platform.runLater(() ->
			{
				getView().updateProgress(progress);
	
			});
		}
	}
	
	@Override
	public void setMessage(String message) {
		if(running) {
			Platform.runLater(() ->
			{
				getView().updateMessage(message);
	
			});
		}
	}
	
	public void setDatasetLabel(String message) {
		if(running) {
			Platform.runLater(() ->
			{
				getView().updateDatasetLabel(message);
	
			});
		}
	}
	
	@Subscribe
	public void onProjectLoadedEvent(BMDProjectLoadedEvent event)
	{
		Platform.runLater(() ->
		{
			getView().closeWindow();
		});
	}

	@Subscribe
	public void onProjectClosedEvent(CloseProjectRequestEvent event)
	{
		Platform.runLater(() ->
		{
			getView().closeWindow();
		});
	}
}
