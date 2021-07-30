package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMainDataView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.NoDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ShowBMDExpressDataAnalysisInSeparateWindow;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ShowDoseResponseExperimentInSeparateWindowEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataSelectedEvent;

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

	@Subscribe
	public void onSelectOneWayAnalysis(OneWayANOVADataSelectedEvent event)
	{
		getView().loadOneWayANOVAAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectOneWayAnalysis(OneWayANOVADataCombinedSelectedEvent event)
	{
		getView().loadOneWayANOVAAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectWilliamsAnalysis(WilliamsTrendDataSelectedEvent event)
	{
		getView().loadWilliamsTrendAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectCurveFitPrefilterAnalysis(CurveFitPrefilterDataSelectedEvent event)
	{
		getView().loadCurveFitPrefilterAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectWilliamsAnalysis(WilliamsTrendDataCombinedSelectedEvent event)
	{
		getView().loadWilliamsTrendAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectCurveFitPrefilterAnalysis(CurveFitPrefilterDataCombinedSelectedEvent event)
	{
		getView().loadCurveFitPrefilterAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectOriogenAnalysis(OriogenDataSelectedEvent event)
	{
		getView().loadOriogenAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectOriogenAnalysis(OriogenDataCombinedSelectedEvent event)
	{
		getView().loadOriogenAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectBMDResultAnalysis(BMDAnalysisDataSelectedEvent event)
	{
		getView().loadBMDResultAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectBMDResultAnalysis(BMDAnalysisDataCombinedSelectedEvent event)
	{
		getView().loadBMDResultAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectCategoryAnalysisResult(CategoryAnalysisDataSelectedEvent event)
	{
		getView().loadCategoryAnalysis(event.GetPayload());
	}

	@Subscribe
	public void onSelectCategoryAnalysisResult(CategoryAnalysisDataCombinedSelectedEvent event)
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
