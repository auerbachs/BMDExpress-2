package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMainView;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectSavedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
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
	 * listen for loading oneway anova results so we can add it to theproject
	 */
	@Subscribe
	public void onSelectOneWayAnova(OneWayANOVADataSelectedEvent event)
	{
		getView().updateSelectionLabel(event.GetPayload().getName());
	}

	/*
	 * listen for loading pathway filter results so we can add it to theproject
	 */
	@Subscribe
	public void onSelectPathwayFilter(PathwayFilterSelectedEvent event)
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
	}

	@Subscribe
	public void onProjectClosedEvent(CloseProjectRequestEvent event)
	{
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
}
