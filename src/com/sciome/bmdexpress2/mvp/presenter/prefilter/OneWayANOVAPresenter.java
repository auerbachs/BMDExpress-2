package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOneWayANOVAView;
import com.sciome.bmdexpress2.serviceInterface.IPrefilterService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.commons.interfaces.SimpleProgressUpdater;

import javafx.application.Platform;
import javafx.concurrent.Task;

//Soure
public class OneWayANOVAPresenter extends ServicePresenterBase<IOneWayANOVAView, IPrefilterService>  implements SimpleProgressUpdater 
{
	private volatile boolean running = false;
	
	public OneWayANOVAPresenter(IOneWayANOVAView view, IPrefilterService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
	}

	/*
	 * Do multiple one way anova filter calculations
	 */
	public void performOneWayANOVA(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String loelPValue, String loelFoldChange, String foldFilterValue, boolean tTest)
	{
		SimpleProgressUpdater me = this;
		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				running = true;
				try
				{
					List<OneWayANOVAResults> resultList = new ArrayList<OneWayANOVAResults>();
					for (int i = 0; i < processableData.size(); i++) {
						if(running) {
							setMessage((i + 1) + "/" + processableData.size());

							//Set cancel to be false in case the service was cancelled before
							getService().setCancel(false);
							resultList.add(getService().oneWayANOVAAnalysis(processableData.get(i), pCutOff, multipleTestingCorrection, 
									filterOutControlGenes, useFoldFilter, foldFilterValue,
									loelPValue, loelFoldChange, me, tTest));
							me.setProgress(0);
						}
					}
					// post the new oneway object to the event bus so folks can do the right thing.
					if(resultList != null && running) {
						Platform.runLater(() ->
						{
							for(int i = 0; i < resultList.size(); i++) {
								getEventBus().post(new OneWayANOVADataLoadedEvent(resultList.get(i)));
							}
						});
					}
				} catch (Exception e)
				{
					Platform.runLater(() ->
					{
						getEventBus().post(new ShowErrorEvent(e.toString()));

					});
					e.printStackTrace();
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
	 * do one way anova filter
	 */
	public void performOneWayANOVA(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String loelPValue, String loelFoldChange, boolean tTest)
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
					getService().setCancel(false);
					OneWayANOVAResults oneWayResults = getService().oneWayANOVAAnalysis(processableData, pCutOff, multipleTestingCorrection, 
																		filterOutControlGenes, useFoldFilter, foldFilterValue,
																		loelPValue, loelFoldChange, me, tTest);

					// post the new oneway object to the event bus so folks can do the right thing.
					if(oneWayResults != null && running) {
						Platform.runLater(() ->
						{
							getEventBus().post(new OneWayANOVADataLoadedEvent(oneWayResults));
						});
					}
				} catch(Exception e) {
					Platform.runLater(() ->
					{
						getEventBus().post(new ShowErrorEvent(e.toString()));

					});
					e.printStackTrace();
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
		setProgress(0.0);
		running = false;
		getService().setCancel(true);
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
