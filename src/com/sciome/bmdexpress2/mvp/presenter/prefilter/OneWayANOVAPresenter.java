package com.sciome.bmdexpress2.mvp.presenter.prefilter;

import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.prefilter.IOneWayANOVAView;
import com.sciome.bmdexpress2.serviceInterface.IPrefilterService;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;

//Soure
public class OneWayANOVAPresenter extends ServicePresenterBase<IOneWayANOVAView, IPrefilterService>
{
	public OneWayANOVAPresenter(IOneWayANOVAView view, IPrefilterService service, BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
		init();
	}

	/*
	 * do one way anova filter
	 */
	public void performOneWayANOVA(List<IStatModelProcessable> processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue)
	{
		for (IStatModelProcessable pData : processableData)
			performOneWayANOVA(pData, pCutOff, multipleTestingCorrection, filterOutControlGenes,
					useFoldFilter, foldFilterValue);
	}

	/*
	 * do one way anova filter
	 */
	public void performOneWayANOVA(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue)
	{
		OneWayANOVAResults oneWayResults = getService().oneWayANOVAAnalysis(processableData, pCutOff, multipleTestingCorrection, 
																		filterOutControlGenes, useFoldFilter, foldFilterValue);
		
		// post the new oneway object to the event bus so folks can do the right thing.
		getEventBus().post(new OneWayANOVADataLoadedEvent(oneWayResults));
	}
	
	/*
	 * Private Methods
	 */
	private void init()
	{
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
