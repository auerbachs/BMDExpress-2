package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IMenuBarView;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVARequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseApplicationRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ImportBMDEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.LoadProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.RequestFileNameForProjectSaveEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.SaveProjectAsRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.SaveProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.TryCloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.DataVisualizationRequestRequestEvent;
import com.sciome.bmdexpress2.util.ExperimentFileUtil;

public class MenuBarPresenter extends PresenterBase<IMenuBarView>
{

	public MenuBarPresenter(IMenuBarView view, BMDExpressEventBus eventBus)
	{
		super(view, eventBus);
		init();
	}

	private void init()
	{
	}

	/*
	 * load new experiment
	 */
	public void loadExperiment(List<File> selectedFiles)
	{

		// load the file and fire and event to the bus
		List<DoseResponseExperiment> experiements = new ArrayList<>();
		for (File selectedFile : selectedFiles)
		{
			DoseResponseExperiment doseResponseExperiment = ExperimentFileUtil.getInstance()
					.readFile(selectedFile, getView().getWindow());
			experiements.add(doseResponseExperiment);

		}
		if (experiements != null)
		{
			getEventBus().post(new ExpressionDataLoadedEvent(experiements));
		}

	}

	/*
	 * request for someone to perform one way anova analysis
	 */
	public void performOneWayANOVA()
	{
		// fire off an event to tell somebody do this. MenuBar view doesn't have any information to figure out
		// which dataset to analyze
		getEventBus().post(new OneWayANOVARequestEvent(""));

	}

	/*
	 * request for someone to perform pathway filter analysis
	 */
	public void performPathWayFilter()
	{
		getEventBus().post(new PathwayFilterRequestEvent(""));

	}

	/*
	 * request for someone to perform bmd analsyis
	 */
	public void performBMDAnalsyis()
	{
		// fire off an event to tell somebody do this. MenuBar view doesn't have any information to figure out
		// which dataset to analyze
		getEventBus().post(new BMDAnalysisRequestEvent(""));

	}

	/*
	 * request for someone to perform category analysis
	 */
	public void performCategoryAnalysis(CategoryAnalysisEnum catAnalysisType)
	{
		// fire off an event to tell somebody do this. MenuBar view doesn't have any information to figure out
		// which dataset to analyze
		getEventBus().post(new CategoryAnalysisRequestEvent(catAnalysisType));

	}

	/*
	 * Load a BMD project file
	 */
	public void loadProject(File selectedFile)
	{
		this.getEventBus().post(new LoadProjectRequestEvent(selectedFile));
	}

	/*
	 * save project as
	 */
	public void saveProjectAs(File selectedFile)
	{
		this.getEventBus().post(new SaveProjectAsRequestEvent(selectedFile));

	}

	/*
	 * save the current working project
	 */
	public void saveProject()
	{
		this.getEventBus().post(new SaveProjectRequestEvent(null));

	}

	/*
	 * user selected close project
	 */
	public void closeProject()
	{
		this.getEventBus().post(new TryCloseProjectRequestEvent(""));

	}

	// When some of the analysis data sets are selected, that means certain items in the
	// MenuBar must change. These subscribers will tell the view to change what it needs to change.
	@Subscribe
	@AllowConcurrentEvents
	public void onExpressionDataSelected(ExpressionDataSelectedEvent event)
	{
		getView().expressionDataSelected();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void onBMDAnlysisDatasSelected(BMDAnalysisDataSelectedEvent event)
	{
		getView().bMDAnalysisDataSelected();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void onOneWayANOVASelected(OneWayANOVADataSelectedEvent event)
	{
		getView().oneWayANOVADataSelected();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void onPathwayFilterSelected(PathwayFilterSelectedEvent event)
	{
		getView().pathwayFilterSelected();
	}

	@Subscribe
	@AllowConcurrentEvents
	public void onCategoryAnalysisSelected(CategoryAnalysisDataSelectedEvent event)
	{
		getView().functionalCategoryDataSelected();
	}

	@Subscribe
	public void onFileNameForProjectSaveRequest(RequestFileNameForProjectSaveEvent event)
	{
		// tell the view to perform a save as
		this.getView().saveAs();
	}

	public void sendCloseEvent()
	{
		getEventBus().post(new CloseApplicationRequestEvent("close it"));

	}

	public void performDataVisualization()
	{
		getEventBus().post(new DataVisualizationRequestRequestEvent(""));

	}

	public void importBMDFile()
	{
		getEventBus().post(new ImportBMDEvent(null));

	}

}
