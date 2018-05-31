package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOriogenView;
import com.sciome.bmdexpress2.serviceInterface.IPrefilterService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.commons.interfaces.SimpleProgressUpdater;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class OriogenPresenter extends ServicePresenterBase<IOriogenView, IPrefilterService> implements SimpleProgressUpdater {
	private volatile boolean running = false;
	
	public OriogenPresenter(IOriogenView view, IPrefilterService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
		init();
	}

	/*
	 * do oriogen filter for multiple data sets
	 */
	public void performOriogen(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, int initialBootstraps, 
			int maxBootstraps, float s0Adjustment, boolean filterOutControlGenes, 
			boolean useFoldFilter, String foldFilterValue, String loelPValue, String loelFoldChange)
	{
		SimpleProgressUpdater me = this;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					List<OriogenResults> resultList = new ArrayList<OriogenResults>();
					int count = 1;
					for(int i = 0; i < processableData.size(); i++) {
						if(running) {
							setMessage(count + "/" + processableData.size());
							resultList.add(getService().oriogenAnalysis(processableData.get(i), pCutOff, multipleTestingCorrection,
									initialBootstraps, maxBootstraps, s0Adjustment,
									filterOutControlGenes, useFoldFilter, foldFilterValue, 
									loelPValue, loelFoldChange, me));
							me.setProgress(0);
							count++;
						}
					}
					// post the new oriogen object to the event bus so folks can do the right thing.
					if(resultList != null && resultList.size() > 0 && running) {
						Platform.runLater(() ->
						{
							for(int i = 0; i < resultList.size(); i++) {
								getEventBus().post(new OriogenDataLoadedEvent(resultList.get(i)));
							}
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

	/*
	 * do oriogen filter
	 */
	public void performOriogen(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, int initialBootstraps, 
			int maxBootstraps, float s0Adjustment, boolean filterOutControlGenes, 
			boolean useFoldFilter, String foldFilterValue, String loelPValue, 
			String loelFoldChange)
	{
		SimpleProgressUpdater me = this;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					OriogenResults oriogenResults = getService().oriogenAnalysis(processableData, pCutOff, multipleTestingCorrection,
							initialBootstraps, maxBootstraps, s0Adjustment,
							filterOutControlGenes, useFoldFilter, foldFilterValue, 
							loelPValue, loelFoldChange, me);
					
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
}
