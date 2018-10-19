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
			String loelPValue, String loelFoldChange, String foldFilterValue, boolean tTest)
	{
		for (IStatModelProcessable pData : processableData)
			performOneWayANOVA(pData, pCutOff, multipleTestingCorrection, filterOutControlGenes,
					useFoldFilter, foldFilterValue, loelPValue, loelFoldChange, tTest);
	}

	/*
	 * do one way anova filter
	 */
	public void performOneWayANOVA(IStatModelProcessable processableData, double pCutOff,
			boolean multipleTestingCorrection, boolean filterOutControlGenes, boolean useFoldFilter,
			String foldFilterValue, String loelPValue, String loelFoldChange, boolean tTest)
	{
		OneWayANOVAResults oneWayResults = getService().oneWayANOVAAnalysis(processableData, pCutOff, multipleTestingCorrection, 
																		filterOutControlGenes, useFoldFilter, foldFilterValue,
																		loelPValue, loelFoldChange, tTest);
		
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
