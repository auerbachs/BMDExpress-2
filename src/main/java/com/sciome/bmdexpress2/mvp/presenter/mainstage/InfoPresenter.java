package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IInfoView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.NoDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;

public class InfoPresenter extends PresenterBase<IInfoView>
{

	public InfoPresenter(IInfoView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	/*
	 * listen for loading an experiment so we can add it to the project.
	 */
	@Subscribe
	public void onLoadExperiement(ExpressionDataSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	/*
	 * listen for loading oneway anova results so we can add it to the project
	 */
	@Subscribe
	public void onLoadOneWayAnova(OneWayANOVADataSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	/*
	 * listen for loading william's trend results so we can add it to the project
	 */
	@Subscribe
	public void onLoadWilliamsTrend(WilliamsTrendDataSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	/*
	 * listen for loading william's trend results so we can add it to the project
	 */
	@Subscribe
	public void onLoadCurveFitPrefilter(CurveFitPrefilterDataSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	/*
	 * listen for loading william's trend results so we can add it to the project
	 */
	@Subscribe
	public void onLoadOriogen(OriogenDataSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	/*
	 * listen for getting a new BMDAnalysisResult set so we can add it to the project
	 */
	@Subscribe
	public void onLoadBMDAnalysisResults(BMDAnalysisDataSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	/*
	 * listen for a new category analysis to add to the project
	 */
	@Subscribe
	public void onSelectCategoryAnalysis(CategoryAnalysisDataSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	@Subscribe
	public void onLoadCombinedExperiement(ExpressionDataCombinedSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	@Subscribe
	public void onLoadCombinedOneWayAnova(OneWayANOVADataCombinedSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	@Subscribe
	public void onLoaCombineddWilliamsTrend(WilliamsTrendDataCombinedSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	@Subscribe
	public void onLoaCombinedCurveFitPrefilter(CurveFitPrefilterDataCombinedSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	@Subscribe
	public void onLoadCombinedOriogen(OriogenDataCombinedSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	@Subscribe
	public void onLoadCombinedBMDAnalysisResults(BMDAnalysisDataCombinedSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	@Subscribe
	public void onSelectCombinedCategoryAnalysis(CategoryAnalysisDataCombinedSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	@Subscribe
	public void onProjectLoadedEvent(BMDProjectLoadedEvent event)
	{
		getView().clearList();
	}

	@Subscribe
	public void onProjectClosedEvent(CloseProjectRequestEvent event)
	{
		getView().clearList();
	}

	@Subscribe
	public void noDataSelectedEvent(NoDataSelectedEvent event)
	{
		getView().clearList();
	}

	/*
	 * Private Methods
	 */
	private void init()
	{
	}
}
