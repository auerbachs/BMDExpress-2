package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMainDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.NoDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ShowBMDExpressDataAnalysisInSeparateWindow;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ShowDoseResponseExperimentInSeparateWindowEvent;

public class MainDataPresenter extends PresenterBase<IMainDataView>
{

	public MainDataPresenter(IMainDataView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	private void init()
	{
	}

	@Subscribe
	public void onSelectExperiement(ExpressionDataSelectedEvent event)
	{
		getView().loadDoseResponseExperiment(event.GetPayload());
	}

	// handle loading and selecting oneway analysis data.
	@Subscribe
	public void onLoadOneWayANOVAAnalysis(OneWayANOVADataLoadedEvent event)
	{
		getView().loadOneWayANOVAAnalysis(event.GetPayload());
	}

	// handle loading and selecting oneway analysis data.
	@Subscribe
	public void onLoadPathWayAnalyis(PathwayFilterDataLoadedEvent event)
	{
		getView().loadPathwayFilterAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectOneWayAnalysis(OneWayANOVADataSelectedEvent event)
	{
		getView().loadOneWayANOVAAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectPathwayFilterAnalysis(PathwayFilterSelectedEvent event)
	{
		getView().loadPathwayFilterAnalysis(event.GetPayload());
	}

	// handle loading and selecting bmd result analysis data.
	@Subscribe
	public void onLoadBMDResultAnalysis(BMDAnalysisDataLoadedEvent event)
	{
		getView().loadBMDResultAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectBMDResultAnalysis(BMDAnalysisDataSelectedEvent event)
	{
		getView().loadBMDResultAnalysis(event.GetPayload());
	}

	// handle loading and selecting functional category result analysis data.
	@Subscribe
	public void onLoadCategoryAnalysisResult(CategoryAnalysisDataLoadedEvent event)
	{
		getView().loadCategoryAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectCategoryAnalysisResult(CategoryAnalysisDataSelectedEvent event)
	{
		getView().loadCategoryAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectBMDResultAnalysis(NoDataSelectedEvent event)
	{
		getView().clearTableView();
	}

	@Subscribe
	public void onShowBMDExpressDataSetInSeparateWindow(ShowBMDExpressDataAnalysisInSeparateWindow event)
	{
		getView().showBMDExpressAnalysisInSeparateWindow(event.GetPayload());
	}

	@Subscribe
	public void onShowExperimentInSeparateWindow(ShowDoseResponseExperimentInSeparateWindowEvent event)
	{
		getView().showExpressDataInSeparateWindow(event.GetPayload());
	}

}
