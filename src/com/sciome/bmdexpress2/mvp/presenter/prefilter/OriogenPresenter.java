package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOriogenView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.bmdexpress2.util.prefilter.WilliamsTrendAnalysis;
import com.sciome.commons.interfaces.SimpleProgressUpdater;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class OriogenPresenter extends PresenterBase<IOriogenView> implements SimpleProgressUpdater {

	List<WilliamsTrendAnalysis> analyses;
	private volatile boolean running = false;
	
	public OriogenPresenter(IOriogenView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	/*
	 * do oriogen filter
	 */
	public void performOriogen(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String numberOfPermutations)
	{

		for (IStatModelProcessable pData : processableData)
		{
			performOriogen(pData, pCutOff, multipleTestingCorrection, filterOutControlGenes,
					useFoldFilter, foldFilterValue, numberOfPermutations);
		}

	}

	/*
	 * do oriogen filter
	 */
	public void performOriogen(IStatModelProcessable processableData, double pCutOff,
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
					WilliamsTrendAnalysis analysis = new WilliamsTrendAnalysis();
					analyses.add(analysis);
					WilliamsTrendResults williamsTrendResults = analysis.analyzeDoseResponseData(processableData, pCutOff, multipleTestingCorrection,
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
		for(WilliamsTrendAnalysis analysis : analyses)
			analysis.cancel();
	}
	
	@Override
	public void setProgress(double progress) {
		Platform.runLater(() ->
		{
			getView().updateProgress(progress);

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
		 analyses = new ArrayList<WilliamsTrendAnalysis>();
	}
}
