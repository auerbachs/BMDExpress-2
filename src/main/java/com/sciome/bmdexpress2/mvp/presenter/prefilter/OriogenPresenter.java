package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IPrefilterView;
import com.sciome.bmdexpress2.serviceInterface.IPrefilterService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.commons.interfaces.SimpleProgressUpdater;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class OriogenPresenter extends ServicePresenterBase<IPrefilterView, IPrefilterService> implements SimpleProgressUpdater {
	private volatile boolean running = false;
	
	public OriogenPresenter(IPrefilterView view, IPrefilterService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
	}

	/*
	 * do oriogen filter for multiple data sets
	 */
	public void performOriogen(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, int initialBootstraps, int maxBootstraps, 
			float s0Adjustment, boolean filterOutControlGenes, boolean useFoldFilter, 
			String foldFilterValue, String loelPValue, String loelFoldChange, 
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
					for(int i = 0; i < processableData.size(); i++) {
						if(running) {
							setDatasetLabel((i + 1) + "/" + processableData.size());

							//Set cancel to be false in case the service was cancelled before
							getService().start();
							OriogenResults result = getService().oriogenAnalysis(processableData.get(i), pCutOff, multipleTestingCorrection,
									initialBootstraps, maxBootstraps, s0Adjustment, filterOutControlGenes, useFoldFilter, 
									Double.valueOf(foldFilterValue), Double.valueOf(loelPValue), Double.valueOf(loelFoldChange),
									Integer.valueOf(numThreads), me, tTest);
							me.setProgress(0);
							Platform.runLater(() ->
							{
								getEventBus().post(new OriogenDataLoadedEvent(result));
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
	 * do oriogen filter
	 */
	public void performOriogen(IStatModelProcessable processableData, double pCutOff, boolean multipleTestingCorrection, int initialBootstraps,
			int maxBootstraps, float s0Adjustment, boolean filterOutControlGenes, boolean useFoldFilter, String foldFilterValue, String loelPValue, 
			String loelFoldChange, String numThreads, boolean tTest)
	{
		SimpleProgressUpdater me = this;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					//Set cancel to be false in case the service was cancelled before
					getService().start();
					OriogenResults oriogenResults = getService().oriogenAnalysis(processableData, pCutOff, multipleTestingCorrection,
							initialBootstraps, maxBootstraps, s0Adjustment, filterOutControlGenes, useFoldFilter, 
							Double.valueOf(foldFilterValue), Double.valueOf(loelPValue), Double.valueOf(loelFoldChange),
							Integer.valueOf(numThreads), me, tTest);
					
					// post the new oriogen object to the event bus so folks can do the right thing.
					if(oriogenResults != null && running) {
						Platform.runLater(() ->
						{
							getEventBus().post(new OriogenDataLoadedEvent(oriogenResults));
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
		setMessage("");
		setDatasetLabel("");
		setProgress(0.0);
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
