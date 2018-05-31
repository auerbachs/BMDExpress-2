package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IWilliamsTrendView;
import com.sciome.bmdexpress2.serviceInterface.IPrefilterService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.commons.interfaces.SimpleProgressUpdater;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class WilliamsTrendPresenter extends ServicePresenterBase<IWilliamsTrendView, IPrefilterService> implements SimpleProgressUpdater {
	/** Time interval to update the UI for multiple datasets (milliseconds)*/
	private static final int UPDATE_TIME = 100;
	
	private volatile boolean running = false;
	
	public WilliamsTrendPresenter(IWilliamsTrendView view, IPrefilterService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
		init();
	}

	/*
	 * do williams trend filter for multiple data sets (threaded)
	 */
	public void performWilliamsTrend(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermutations, String loelPValue, String loelFoldChange)
	{
		WilliamsTrendResults[] resultList = new WilliamsTrendResults[processableData.size()];
		List<WilliamsUpdater> updaters = Collections.synchronizedList(new ArrayList<WilliamsUpdater>());

		int count = 0;
		for (IStatModelProcessable pData : processableData) {
			final int threadCount = count;
			Task<Integer> task = new Task<Integer>() {
				@Override
				protected Integer call() throws Exception
				{
					running = true;
					try
					{
						//Create a new updater to keep track of the progress for each data set
						WilliamsUpdater updater = new WilliamsUpdater();
						synchronized(updaters) {
							updaters.add(updater);
						}
						if(running) {
							resultList[threadCount] = getService().williamsTrendAnalysis(pData, pCutOff, multipleTestingCorrection,
									filterOutControlGenes, useFoldFilter, foldFilterValue, numberOfPermutations, loelPValue, loelFoldChange,
									updater);
							//Once the method is finished, set progress to 1
							updater.setProgress(1);
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
					return 0;
				}
			};
			new Thread(task).start();
			count++;
		}
		
		//A progress updater that updates the view every 100 milliseconds and posts the results when all threads are finished
		ScheduledExecutorService updateProgress = Executors.newSingleThreadScheduledExecutor();
		updateProgress.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				//Only update the progress if the threads are actually running
				if(running) {
					double totalProgress = 0;
					synchronized(updaters) {
						for(WilliamsUpdater updater : updaters)
						{
							totalProgress += updater.getProgress();
						}
						final double progress = totalProgress / updaters.size();
						
						//If the threads are all finished running william's we need to send the results to the event bus,
						//close the window, and shut down the updater
						if(progress == 1) {
							//Post the list of williams trend result objects to event bus
							if(resultList != null && resultList.length > 0) {
								Platform.runLater(() ->
								{
									for(int i = 0; i < resultList.length; i++) {
										getEventBus().post(new WilliamsTrendDataLoadedEvent(resultList[i]));
									}
								});
							}
							
							Platform.runLater(() ->
							{
								getView().closeWindow();
							});
							updateProgress.shutdown();
						} else {
							//Otherwise we just set the progress bar
							setProgress(progress);
						}
					}
				} else {
					//If the threads aren't running we need to reset the progress bar and shutdown the updater
					setProgress(0);
					updateProgress.shutdown();
				}
			}
			
		}, 0, UPDATE_TIME, TimeUnit.MILLISECONDS);
		
	}

	/*
	 * do williams trend filter
	 */
	public void performWilliamsTrend(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermutations, String loelPValue, String loelFoldChange)
	{
		SimpleProgressUpdater me = this;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					WilliamsTrendResults williamsTrendResults = getService().williamsTrendAnalysis(processableData, pCutOff, multipleTestingCorrection,
							filterOutControlGenes, useFoldFilter, foldFilterValue, numberOfPermutations, loelPValue, loelFoldChange, me);
					
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
		running = false;
		getService().cancel();
		setMessage("");
	}
	
	@Override
	public void setProgress(double progress) {
		Platform.runLater(() ->
		{
			getView().updateProgress(progress);

		});
	}

	public void setMessage(String message) {
		Platform.runLater(() ->
		{
			getView().updateMessage(message);

		});
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
	
	/*
	 * private methods
	 */
	private void init() {
	}
	
	/**
	 * Updater for multi threaded williams trend test
	 *
	 */
	private class WilliamsUpdater implements SimpleProgressUpdater {
		private double progress;
		
		@Override
		public void setProgress(double progress) {
			this.progress = progress;
		}
		
		public double getProgress()
		{
			return this.progress;
		}
	}
}
