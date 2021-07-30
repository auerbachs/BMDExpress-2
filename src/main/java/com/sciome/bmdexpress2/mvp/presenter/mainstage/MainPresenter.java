package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMainView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.NoDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectSavedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.SaveProjectAsJSONRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.SaveProjectAsRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.SaveProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowMessageEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowWarningEvent;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class MainPresenter extends PresenterBase<IMainView>
{

	public MainPresenter(IMainView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	/*
	 * listen for loading an experiement so we can add it to the project.
	 */
	@Subscribe
	public void onSelectExperiement(ExpressionDataSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading oneway anova results so we can add it to the project
	 */
	@Subscribe
	public void onSelectOneWayAnova(OneWayANOVADataSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading williams trend results so we can add it to the project
	 */
	@Subscribe
	public void onSelectWilliamsTrend(WilliamsTrendDataSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading curve fit prefilter results so we can add it to the project
	 */
	@Subscribe
	public void onSelectCurveFitPrefilter(CurveFitPrefilterDataSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading oriogen results so we can add it to the project
	 */
	@Subscribe
	public void onSelectOriogen(OriogenDataSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for getting a new BMDAnalysisResult set so we can add it to the project
	 */
	@Subscribe
	public void onSelectBMDAnalysisResults(BMDAnalysisDataSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for a new category analysis to add to the project
	 */
	@Subscribe
	public void onSelectCategoryAnalysis(CategoryAnalysisDataSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading an experiement so we can add it to the project.
	 */
	@Subscribe
	public void onSelectExperiement(ExpressionDataCombinedSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading oneway anova results so we can add it to the project
	 */
	@Subscribe
	public void onSelectOneWayAnova(OneWayANOVADataCombinedSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading williams trend results so we can add it to the project
	 */
	@Subscribe
	public void onSelectWilliamsTrend(WilliamsTrendDataCombinedSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading curve fit prefilter  results so we can add it to the project
	 */
	@Subscribe
	public void onSelectCurveFitPrefilter(CurveFitPrefilterDataCombinedSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading oriogen results so we can add it to the project
	 */
	@Subscribe
	public void onSelectOriogen(OriogenDataCombinedSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for getting a new BMDAnalysisResult set so we can add it to the project
	 */
	@Subscribe
	public void onSelectBMDAnalysisResults(BMDAnalysisDataCombinedSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for a new category analysis to add to the project
	 */
	@Subscribe
	public void onSelectCategoryAnalysis(CategoryAnalysisDataCombinedSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	@Subscribe
	public void onSaveProjectRequest(SaveProjectRequestEvent event)
	{
		getView().updateActionStatusLabel("");
	}

	@Subscribe
	public void onSaveProjectAsRequest(SaveProjectAsRequestEvent event)
	{
		getView().updateActionStatusLabel("");
	}

	@Subscribe
	void onSaveProjectAsJSONRequest(SaveProjectAsJSONRequestEvent event)
	{
		getView().updateActionStatusLabel("");
	}

	@Subscribe
	public void noDataSelectedEvent(NoDataSelectedEvent event)
	{
		getView().updateActionStatusLabel("");
		getView().updateSelectionLabel("");
	}

	@Subscribe
	public void onSavedProjectAsRequest(BMDProjectSavedEvent event)
	{
		getView().updateActionStatusLabel("Saved");

		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				try
				{
					Thread.sleep(3000);
				}
				catch (Exception e)
				{

				}
				Platform.runLater(() ->
				{
					getView().updateActionStatusLabel("");
				});

				return 0;
			}
		};

		new Thread(task).start();

	}

	@Subscribe
	public void onProjectedLoadedRequest(BMDProjectLoadedEvent event)
	{
		getView().updateProjectLabel(event.GetPayload().getName());
	}

	@Subscribe
	public void onProjectedLoadedRequest(BMDProjectSavedEvent event)
	{
		getView().updateProjectLabel(event.GetPayload().getName());
	}

	/*
	 * new the project event
	 */
	@Subscribe
	public void onProjectNewRequest(CloseProjectRequestEvent saveProjectRequest)
	{

		getView().updateProjectLabel("");
		getView().updateSelectionLabel("");
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
		getView().updateSelectionLabel("");
		getView().updateSelectionLabel("");
	}

	@Subscribe
	public void onProjectClosedEvent(CloseProjectRequestEvent event)
	{
		getView().updateSelectionLabel("");
		getView().updateSelectionLabel("");
	}

	@Subscribe
	public void onError(ShowErrorEvent event)
	{
		getView().showErrorAlert(event.GetPayload());
	}

	@Subscribe
	public void onMessage(ShowMessageEvent event)
	{
		getView().showMessageDialog(event.GetPayload());
	}

	@Subscribe
	public void onWarning(ShowWarningEvent event)
	{
		getView().showWarningDialog(event.GetPayload());
	}

	/*
	 * load oneway anova results into the view.
	 */
	@Subscribe
	public void onLoadOneWayANOVAAnalysis(OneWayANOVADataLoadedEvent event)
	{
		getView().updateActionStatusLabel("loaded one-way anova: " + event.GetPayload().getName());
	}

	/*
	 * load williams trend results into the view.
	 */
	@Subscribe
	public void onLoadWilliamsTrendAnalysis(WilliamsTrendDataLoadedEvent event)
	{
		getView().updateActionStatusLabel("loaded williams trend test: " + event.GetPayload().getName());
	}

	/*
	 * load curve fit prefilter results into the view.
	 */
	@Subscribe
	public void onLoadCurveFitPrefilterAnalysis(CurveFitPrefilterDataLoadedEvent event)
	{
		getView().updateActionStatusLabel(
				"loaded curve fit prefilter results: " + event.GetPayload().getName());
	}

	/*
	 * load williams trend results into the view.
	 */
	@Subscribe
	public void onLoadOriogenAnalysis(OriogenDataLoadedEvent event)
	{
		getView().updateActionStatusLabel("loaded oriogen: " + event.GetPayload().getName());
	}

	/*
	 * load a bmdresults into the view.
	 */
	@Subscribe
	public void onLoadBMDAnalysis(BMDAnalysisDataLoadedEvent event)
	{
		getView().updateActionStatusLabel("loaded bmd analysis: " + event.GetPayload().getName());
	}

	/*
	 * load a category analysis data set into the view.
	 */
	@Subscribe
	public void onLoadCategoryAnalysis(CategoryAnalysisDataLoadedEvent event)
	{
		getView().updateActionStatusLabel("loaded category analysis: " + event.GetPayload().getName());
	}

	/*
	 * load a category analysis data set into the view.
	 */
	@Subscribe
	public void onLoadCategoryAnalysis(ExpressionDataLoadedEvent event)
	{
		getView().updateActionStatusLabel("loaded expression data: " + event.GetPayload().toString());
	}
}
