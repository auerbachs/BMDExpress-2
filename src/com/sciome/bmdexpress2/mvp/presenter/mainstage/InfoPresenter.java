package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IInfoView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterSelectedEvent;
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
	 * listen for loading an experiement so we can add it to the project.
	 */
	@Subscribe
	public void onLoadExperiement(ExpressionDataSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	/*
	 * listen for loading oneway anova results so we can add it to theproject
	 */
	@Subscribe
	public void onLoadOneWayAnova(OneWayANOVADataSelectedEvent event)
	{
		getView().showAnalysisInfo(event.GetPayload().getAnalysisInfo());
	}

	/*
	 * listen for loading pathway filter results so we can add it to theproject
	 */
	@Subscribe
	public void onLoadPathwayFilter(PathwayFilterSelectedEvent event)
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
	public void onProjectLoadedEvent(BMDProjectLoadedEvent event)
	{
		getView().clearList();
	}

	@Subscribe
	public void onProjectClosedEvent(CloseProjectRequestEvent event)
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
