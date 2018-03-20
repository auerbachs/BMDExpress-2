package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.util.ArrayList;
import java.util.List;

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
	private volatile boolean running = false;
	
	public WilliamsTrendPresenter(IWilliamsTrendView view, IPrefilterService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
		init();
	}

	/*
	 * do williams trend filter for multiple data sets
	 */
	public void performWilliamsTrend(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermutations)
	{
		SimpleProgressUpdater me = this;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					List<WilliamsTrendResults> resultList = new ArrayList<WilliamsTrendResults>();
					int count = 1;
					for (IStatModelProcessable pData : processableData)
					{
						if(running) {
							setMessage(count + "/" + processableData.size());
							resultList.add(getService().williamsTrendAnalysis(pData, pCutOff, multipleTestingCorrection,
									filterOutControlGenes, useFoldFilter, foldFilterValue, numberOfPermutations, me));
							
							me.setProgress(0);
							count++;
						}
					}
					
					// post the new williams object to the event bus so folks can do the right thing.
					if(resultList != null && resultList.size() > 0 && running) {
						Platform.runLater(() ->
						{
							for(int i = 0; i < resultList.size(); i++) {
								getEventBus().post(new WilliamsTrendDataLoadedEvent(resultList.get(i)));
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
	 * do williams trend filter
	 */
	public void performWilliamsTrend(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermutations)
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
							filterOutControlGenes, useFoldFilter, foldFilterValue, numberOfPermutations, me);
					
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
}
