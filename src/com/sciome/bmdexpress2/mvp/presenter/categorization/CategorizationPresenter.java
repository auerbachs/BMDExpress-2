package com.sciome.bmdexpress2.mvp.presenter.categorization;

import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.categorization.ICategorizationView;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryAnalysisParameters;
import com.sciome.bmdexpress2.util.categoryanalysis.CategoryMapTool;
import com.sciome.bmdexpress2.util.categoryanalysis.ICategoryMapToolProgress;

import javafx.application.Platform;
import javafx.concurrent.Task;

public class CategorizationPresenter extends PresenterBase<ICategorizationView>
		implements ICategoryMapToolProgress
{
	private List<BMDResult>			bmdResults;
	private CategoryMapTool			catMapTool;

	private CategoryAnalysisEnum	catAnalysisEnum;

	/*
	 * Constructors
	 */

	public CategorizationPresenter(ICategorizationView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	/*
	 * Private Methods
	 */
	private void init()
	{
	}

	public void initData(List<BMDResult> bmdResults, CategoryAnalysisEnum catAnalysisEnum)
	{
		this.bmdResults = bmdResults;
		this.catAnalysisEnum = catAnalysisEnum;

	}

	public void startAnalyses(CategoryAnalysisParameters params)
	{

		// send this to the bmdanalysis tool so some progress can be updated.
		ICategoryMapToolProgress me = this;

		Task<Integer> task = new Task<Integer>() {
			@Override
			protected Integer call() throws Exception
			{
				for (BMDResult bmdResult : bmdResults)
				{
					Platform.runLater(() ->
					{
						getView().startedCategorization();
					});
					try
					{

						AnalysisInfo analysisInfo = new AnalysisInfo();
						List<String> notes = new ArrayList<>();

						analysisInfo.setNotes(notes);

						catMapTool = new CategoryMapTool(params, bmdResult, catAnalysisEnum, me,
								analysisInfo);
						CategoryAnalysisResults categoryAnalysisResults = catMapTool.startAnalyses();

						categoryAnalysisResults.setAnalysisInfo(analysisInfo);

						Platform.runLater(() ->
						{

							getView().finishedCategorization();
							if (categoryAnalysisResults != null)
							{

								getEventBus()
										.post(new CategoryAnalysisDataLoadedEvent(categoryAnalysisResults));

							}

						});

					}
					catch (Exception exception)
					{
						Platform.runLater(() ->
						{
							CategorizationPresenter.this.getEventBus().post(
									new ShowErrorEvent("Category Analysis Failure: " + exception.toString()));
							getView().enableButtons();
						});
						exception.printStackTrace();
					}
				}

				Platform.runLater(() ->
				{
					getView().closeWindow();
				});
				return 0;
			}
		};

		new Thread(task).start();

	}

	@Override
	public void updateProgress(String label, double value)
	{
		Platform.runLater(() ->
		{
			getView().updateProgressBar(label, value);

		});

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
