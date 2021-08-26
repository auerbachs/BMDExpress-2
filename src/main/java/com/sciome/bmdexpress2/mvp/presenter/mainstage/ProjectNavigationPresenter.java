package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.CombinedDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.CurveFitPrefilterResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.presenterbases.ServicePresenterBase;
import com.sciome.bmdexpress2.mvp.view.mainstage.ProjectNavigationView;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IProjectNavigationView;
import com.sciome.bmdexpress2.service.DataCombinerService;
import com.sciome.bmdexpress2.serviceInterface.IDataCombinerService;
import com.sciome.bmdexpress2.serviceInterface.IProjectNavigationService;
import com.sciome.bmdexpress2.shared.TableViewCache;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedForProcessingEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisGCurvePRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisToxicRRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedForProcessingEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterDataSelectedForProcessingEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CurveFitPrefilterRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedForProcessingEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.NoDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.NoDataSelectedForProcessingEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedForProcessingEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVARequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenDataSelectedForProcessingEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OriogenRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ShowBMDExpressDataAnalysisInSeparateWindow;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ShowDoseResponseExperimentInSeparateWindowEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataCombinedSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendDataSelectedForProcessingEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.WilliamsTrendRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.AddProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.BMDProjectSavedEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseApplicationRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.CloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.GiveMeProjectRequest;
import com.sciome.bmdexpress2.shared.eventbus.project.HeresYourProjectEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ImportBMDEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ImportJSONEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.LoadProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.RequestFileNameForProjectSaveEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.SaveProjectAsJSONRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.SaveProjectAsRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.SaveProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.ShowErrorEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.TryCloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.DataVisualizationRequestRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.ShowDataVisualizationEvent;
import com.sciome.bmdexpress2.util.DialogWithThreadProcess;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.ProjectUtilities;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;

public class ProjectNavigationPresenter
		extends ServicePresenterBase<IProjectNavigationView, IProjectNavigationService>
{

	private BMDProject currentProject = new BMDProject();
	private File currentProjectFile;
	private IDataCombinerService combinerService = new DataCombinerService();

	public ProjectNavigationPresenter(IProjectNavigationView view, IProjectNavigationService service,
			BMDExpressEventBus eventBus)
	{
		super(view, service, eventBus);
		init();
	}

	/*
	 * Private Methods
	 */
	private void init()
	{
	}

	/*
	 * dose response data selected, post it to event bus
	 */
	public void doseResponseExperimentSelected(DoseResponseExperiment doseResponseExperiment)
	{
		getEventBus().post(new ExpressionDataSelectedEvent(doseResponseExperiment));
	}

	/*
	 * post the onewayaonova result was selected.
	 */
	public void BMDExpressAnalysisDataSetSelected(BMDExpressAnalysisDataSet dataset)
	{
		if (dataset instanceof OneWayANOVAResults)
			getEventBus().post(new OneWayANOVADataSelectedEvent((OneWayANOVAResults) dataset));
		else if (dataset instanceof WilliamsTrendResults)
			getEventBus().post(new WilliamsTrendDataSelectedEvent((WilliamsTrendResults) dataset));
		else if (dataset instanceof CurveFitPrefilterResults)
			getEventBus().post(new CurveFitPrefilterDataSelectedEvent((CurveFitPrefilterResults) dataset));
		else if (dataset instanceof OriogenResults)
			getEventBus().post(new OriogenDataSelectedEvent((OriogenResults) dataset));
		else if (dataset instanceof CategoryAnalysisResults)
			getEventBus().post(new CategoryAnalysisDataSelectedEvent((CategoryAnalysisResults) dataset));
		else if (dataset instanceof BMDResult)
			getEventBus().post(new BMDAnalysisDataSelectedEvent((BMDResult) dataset));

	}

	/*
	 * dose response data selected, post it to event bus
	 */
	public void doseResponseExperimentSelectedForProcessing(DoseResponseExperiment doseResponseExperiment)
	{
		getEventBus().post(new ExpressionDataSelectedForProcessingEvent(doseResponseExperiment));
	}

	/*
	 * post the onewayaonova result was selected.
	 */
	public void BMDExpressAnalysisDataSetSelectedForProcessing(BMDExpressAnalysisDataSet dataset)
	{
		if (dataset instanceof OneWayANOVAResults)
			getEventBus().post(new OneWayANOVADataSelectedForProcessingEvent((OneWayANOVAResults) dataset));
		else if (dataset instanceof WilliamsTrendResults)
			getEventBus()
					.post(new WilliamsTrendDataSelectedForProcessingEvent((WilliamsTrendResults) dataset));
		else if (dataset instanceof CurveFitPrefilterResults)
			getEventBus().post(
					new CurveFitPrefilterDataSelectedForProcessingEvent((CurveFitPrefilterResults) dataset));
		else if (dataset instanceof OriogenResults)
			getEventBus().post(new OriogenDataSelectedForProcessingEvent((OriogenResults) dataset));
		else if (dataset instanceof CategoryAnalysisResults)
			getEventBus().post(
					new CategoryAnalysisDataSelectedForProcessingEvent((CategoryAnalysisResults) dataset));
		else if (dataset instanceof BMDResult)
			getEventBus().post(new BMDAnalysisDataSelectedForProcessingEvent((BMDResult) dataset));

	}

	/*
	 * load new experiment data into the view.
	 */
	@Subscribe
	public void onLoadExperiement(ExpressionDataLoadedEvent event)
	{
		List<DoseResponseExperiment> experiments = event.GetPayload();

		if (experiments == null || experiments.size() == 0)
			return;
		// FileAnnotation uses the probe hash to help find a valid list of chips.
		Hashtable<String, Integer> probeHash = new Hashtable<>();
		for (DoseResponseExperiment exp : experiments)
			for (ProbeResponse probeResponse : exp.getProbeResponses())
				probeHash.put(probeResponse.getProbe().getId(), 1);
		FileAnnotation fileAnnotation = new FileAnnotation();
		fileAnnotation.setProbesHash(probeHash);
		fileAnnotation.readArraysInfo();

		ChipInfo[] chips = fileAnnotation.findChips();
		List<ChipInfo> choices = new ArrayList<>();
		for (int i = 0; i < chips.length; i++)
		{
			choices.add(chips[i]);
		}

		getView().getAChip(choices, event.GetPayload(), fileAnnotation);

		if (currentProject == null)
		{
			currentProject = new BMDProject();
		}
		for (DoseResponseExperiment experiment : experiments)
		{
			currentProject.giveBMDAnalysisUniqueName(experiment, experiment.getName());
			currentProject.getDoseResponseExperiments().add(experiment);

			getView().addDoseResponseExperiement(experiment, true);
		}

	}

	/*
	 * load oneway anova results into the view.
	 */
	@Subscribe
	public void onLoadOneWayANOVAAnalysis(OneWayANOVADataLoadedEvent event)
	{
		// first make sure the name is unique
		currentProject.giveBMDAnalysisUniqueName(event.GetPayload(), event.GetPayload().getName());
		getView().addOneWayANOVAAnalysis(event.GetPayload(), true);
		currentProject.getOneWayANOVAResults().add(event.GetPayload());
	}

	/*
	 * load williams trend results into the view.
	 */
	@Subscribe
	public void onLoadWilliamsTrendAnalysis(WilliamsTrendDataLoadedEvent event)
	{
		// first make sure the name is unique
		currentProject.giveBMDAnalysisUniqueName(event.GetPayload(), event.GetPayload().getName());
		getView().addWilliamsTrendAnalysis(event.GetPayload(), true);
		currentProject.getWilliamsTrendResults().add(event.GetPayload());
	}

	/*
	 * load curveFit prefilter results into the view.
	 */
	@Subscribe
	public void onLoadCurveFitPrefilterAnalysis(CurveFitPrefilterDataLoadedEvent event)
	{
		// first make sure the name is unique
		try
		{
			currentProject.giveBMDAnalysisUniqueName(event.GetPayload(), event.GetPayload().getName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		getView().addCurveFitPrefilterAnalysis(event.GetPayload(), true);
		currentProject.getCurveFitPrefilterResults().add(event.GetPayload());
	}

	/*
	 * load williams trend results into the view.
	 */
	@Subscribe
	public void onLoadOriogenAnalysis(OriogenDataLoadedEvent event)
	{
		// first make sure the name is unique
		currentProject.giveBMDAnalysisUniqueName(event.GetPayload(), event.GetPayload().getName());
		getView().addOriogenAnalysis(event.GetPayload(), true);
		currentProject.getOriogenResults().add(event.GetPayload());
	}

	/*
	 * load a bmdresults into the view.
	 */
	@Subscribe
	public void onLoadBMDAnalysis(BMDAnalysisDataLoadedEvent event)
	{
		// first make sure the name is unique
		currentProject.giveBMDAnalysisUniqueName(event.GetPayload(), event.GetPayload().getName());
		getView().addBMDAnalysis(event.GetPayload(), true);
		currentProject.getbMDResult().add(event.GetPayload());
	}

	/*
	 * load a category analysis data set into the view.
	 */
	@Subscribe
	public void onLoadCategoryAnalysis(CategoryAnalysisDataLoadedEvent event)
	{
		// first make sure the name is unique
		currentProject.giveBMDAnalysisUniqueName(event.GetPayload(), event.GetPayload().getName());
		getView().addCategoryAnalysis(event.GetPayload(), true);
		currentProject.getCategoryAnalysisResults().add(event.GetPayload());
	}

	/*
	 * some one asked to do a oneway anova. So let's tell the view about it so it can figure out what objects
	 * are selected and do the right thing
	 */
	@Subscribe
	public void onOneWayANOVAAnalsyisRequest(OneWayANOVARequestEvent event)
	{

		getView().performOneWayANOVA();

	}

	/*
	 * some one asked to do a williams trend. So let's tell the view about it so it can figure out what
	 * objects are selected and do the right thing
	 */
	@Subscribe
	public void onWilliamsTrendAnalsyisRequest(WilliamsTrendRequestEvent event)
	{

		getView().performWilliamsTrend();

	}

	/*
	 * some one asked to do a williams trend. So let's tell the view about it so it can figure out what
	 * objects are selected and do the right thing
	 */
	@Subscribe
	public void onCurveFitPrefilterAnalsyisRequest(CurveFitPrefilterRequestEvent event)
	{

		getView().performCurveFitPreFilter();

	}

	/*
	 * some one asked to do an oriogen test. So let's tell the view about it so it can figure out what objects
	 * are selected and do the right thing
	 */
	@Subscribe
	public void onOriogenAnalsyisRequest(OriogenRequestEvent event)
	{

		getView().performOriogen();

	}

	/*
	 * some one asked to perform bmd analysis. lets ask the view to figure out what is selected and then do
	 * it.
	 */
	@Subscribe
	public void onBMDAnalysisRequest(BMDAnalysisRequestEvent event)
	{

		getView().performBMDAnalysis();

	}

	/*
	 * some one asked to perform bmd analysis. lets ask the view to figure out what is selected and then do
	 * it.
	 */
	@Subscribe
	public void onBMDAnalysisGCurvePRequest(BMDAnalysisGCurvePRequestEvent event)
	{

		getView().performBMDAnalysisGCurveP();

	}

	@Subscribe
	public void onBMDAnalysisGCurvePRequest(BMDAnalysisToxicRRequestEvent event)
	{

		getView().performBMDAnalysisToxicR();

	}

	/*
	 * some one requested a category analysis. let's tell the view to figure out which input set is selected
	 * in the tree and then fire off the appropriate view
	 */
	@Subscribe
	public void onCategoryAnalysisRequest(CategoryAnalysisRequestEvent event)
	{
		getView().performCategoryAnalysis(event.GetPayload());
	}

	/*
	 * a project has been loaded. must update view.
	 */
	@Subscribe
	public void onProjectLoaded(BMDProjectLoadedEvent event)
	{
		getView().clearNavigationTree();

		BMDProject bmdProject = event.GetPayload();

		// temporary export the curve parameters
		// exportModelParameters(bmdProject);

		// populate all the expression data
		for (DoseResponseExperiment dRE : bmdProject.getDoseResponseExperiments())
		{
			getView().addDoseResponseExperiement(dRE, false);
		}

		// populate all the anova data
		for (OneWayANOVAResults oneWayResult : bmdProject.getOneWayANOVAResults())
		{
			getView().addOneWayANOVAAnalysis(oneWayResult, false);
		}

		// populate all the williams trend data
		for (WilliamsTrendResults williamsTrendResult : bmdProject.getWilliamsTrendResults())
		{
			getView().addWilliamsTrendAnalysis(williamsTrendResult, false);
		}

		// populate all the oriogen data
		for (OriogenResults oriogenResult : bmdProject.getOriogenResults())
		{
			getView().addOriogenAnalysis(oriogenResult, false);
		}

		// populate all the categorization data
		for (CategoryAnalysisResults catResult : bmdProject.getCategoryAnalysisResults())
		{
			getView().addCategoryAnalysis(catResult, false);
		}

		// populate all the bmdanalysis data.
		for (BMDResult bmdResult : bmdProject.getbMDResult())
		{
			getView().addBMDAnalysis(bmdResult, false);
		}

		// populate all the bmdanalysis data.
		for (CurveFitPrefilterResults cfpr : bmdProject.getCurveFitPrefilterResults())
		{
			getView().addCurveFitPrefilterAnalysis(cfpr, false);
		}

		getView().expandTree();

	}

	/*
	 * add project event has been fired.
	 */
	@Subscribe
	public void onProjectAddRequest(AddProjectRequestEvent addProjectRequestEvent)
	{
		try
		{
			if ((currentProject != null && !currentProject.isProjectEmpty()) && saveProjectFirstMaybe() == -1)
			{
				return;
			}
			File selectedFile = getView().askForAProjectFileToOpen();

			if (selectedFile == null)
			{
				return;
			}

			// TODO this is a hack. needs to be in the view.
			DialogWithThreadProcess loadDialog = new DialogWithThreadProcess(
					((ProjectNavigationView) getView()).getWindow());
			BMDProject newProject = loadDialog.addProject(selectedFile);

			if (newProject != null)
			{
				// add files to the current project
				ProjectUtilities.addProjectToProject(currentProject, newProject);

				// Set project file to null to request new file name for saving
				currentProjectFile = null;

				this.getEventBus().post(new BMDProjectLoadedEvent(currentProject));
			}
		}
		catch (Exception exception)
		{
			this.getEventBus().post(new ShowErrorEvent(exception.getMessage()));
		}

	}

	/*
	 * load project event has been fired.
	 */
	@Subscribe
	public void onProjectLoadRequest(LoadProjectRequestEvent loadProjectRequestEvent)
	{
		try
		{
			if ((currentProject != null && !currentProject.isProjectEmpty()) && saveProjectFirstMaybe() == -1)
			{
				return;
			}
			File selectedFile = getView().askForAProjectFileToOpen();

			if (selectedFile == null)
			{
				return;
			}

			// TODO this is a hack. needs to be in the view.
			DialogWithThreadProcess loadDialog = new DialogWithThreadProcess(
					((ProjectNavigationView) getView()).getWindow());
			BMDProject newProject = loadDialog.loadProject(selectedFile);

			if (newProject != null)
			{
				// since we are opening a project, tell everyone to close
				this.getEventBus().post(new CloseProjectRequestEvent(""));
				TableViewCache.getInstance().clear();
				currentProject = newProject;
				currentProjectFile = selectedFile;
				this.getEventBus().post(new BMDProjectLoadedEvent(currentProject));
			}
		}
		catch (Exception exception)
		{
			this.getEventBus().post(new ShowErrorEvent(exception.getMessage()));
		}

	}

	/*
	 * load project event has been fired.
	 * 
	 */
	@Subscribe
	public void importBMDFileRequest(ImportBMDEvent importBMDEvent)
	{
		try
		{
			if ((currentProject != null && !currentProject.isProjectEmpty()) && saveProjectFirstMaybe() == -1)
			{
				return;
			}
			File selectedFile = getView().askForABMDFileToImport();

			if (selectedFile == null)
			{
				return;
			}

			// TODO this is a hack. needs to be in the view.
			DialogWithThreadProcess loadDialog = new DialogWithThreadProcess(
					((ProjectNavigationView) getView()).getWindow());
			BMDProject newProject = loadDialog.importBMDFile(selectedFile);
			String newFileName = selectedFile.getAbsolutePath().replace(".bmd", ".bm2");

			if (newProject != null)
			{
				File newFile = new File(newFileName);
				newProject.setName(newFile.getName());
				currentProject = newProject;
				currentProjectFile = newFile;
				this.getEventBus().post(new BMDProjectLoadedEvent(currentProject));
			}
		}
		catch (Exception exception)
		{
			this.getEventBus().post(new ShowErrorEvent(exception.getMessage()));
		}

	}

	@Subscribe
	public void importJSONFileRequest(ImportJSONEvent importJSONEvent)
	{
		try
		{
			if ((currentProject != null && !currentProject.isProjectEmpty()) && saveProjectFirstMaybe() == -1)
			{
				return;
			}
			File selectedFile = getView().askForAJSONFileToImport();

			if (selectedFile == null)
			{
				return;
			}

			// TODO this is a hack. needs to be in the view.
			DialogWithThreadProcess loadDialog = new DialogWithThreadProcess(
					((ProjectNavigationView) getView()).getWindow());
			BMDProject newProject = loadDialog.importJSONFile(selectedFile);
			String newFileName = selectedFile.getAbsolutePath().replace(".json", ".bm2");

			if (newProject != null)
			{
				File newFile = new File(newFileName);
				newProject.setName(newFile.getName());
				currentProject = newProject;
				currentProjectFile = newFile;
				this.getEventBus().post(new BMDProjectLoadedEvent(currentProject));
			}
		}
		catch (Exception exception)
		{
			this.getEventBus().post(new ShowErrorEvent(exception.getMessage()));
		}

	}

	/*
	 * save as event was file
	 */
	@Subscribe
	public void onProjectSaveAsRequest(SaveProjectAsRequestEvent saveProjectAsRequestEvent)
	{
		File selectedFile = saveProjectAsRequestEvent.GetPayload();
		if (selectedFile == null)
		{
			return;
		}
		saveProject(selectedFile);
	}

	@Subscribe
	public void onSaveProjectAsJSONRequest(SaveProjectAsJSONRequestEvent event)
	{
		File selectedFile = event.GetPayload();
		if (selectedFile == null)
		{
			return;
		}
		saveJSONProject(selectedFile);
	}

	private void saveProject(File selectedFile)
	{

		if (selectedFile == null)
		{
			return;
		}

		// TODO this is a hack. needs to be in the view.
		DialogWithThreadProcess saveDialog = new DialogWithThreadProcess(
				((ProjectNavigationView) getView()).getWindow());
		saveDialog.saveProject(currentProject, selectedFile);
		currentProject.setName(selectedFile.getName());
		currentProjectFile = selectedFile;

		this.getEventBus().post(new BMDProjectSavedEvent(currentProject));

	}

	private void saveJSONProject(File selectedFile)
	{

		if (selectedFile == null)
		{
			return;
		}

		// TODO this is a hack. needs to be in the view.
		DialogWithThreadProcess saveDialog = new DialogWithThreadProcess(
				((ProjectNavigationView) getView()).getWindow());
		saveDialog.saveJSONProject(currentProject, selectedFile);
		currentProject.setName(selectedFile.getName());
		currentProjectFile = selectedFile;

		this.getEventBus().post(new BMDProjectSavedEvent(currentProject));

	}

	/*
	 * save the project event
	 */
	@Subscribe
	public void onProjectSaveRequest(SaveProjectRequestEvent saveProjectRequest)
	{

		if (currentProjectFile != null)
		{
			this.getEventBus().post(new SaveProjectAsRequestEvent(currentProjectFile));
		}
		else // need to prompt user for a file
		{
			this.getEventBus().post(new RequestFileNameForProjectSaveEvent(null));
		}
	}

	/*
	 * set up the gene annotation data for the dose response experiment
	 */

	public void assignArrayAnnotations(ChipInfo chipInfo, List<DoseResponseExperiment> experiments,
			FileAnnotation fileAnnotation)
	{
		getService().assignArrayAnnotations(chipInfo, experiments, fileAnnotation);
	}

	/*
	 * remove the category analysis data from the project.
	 */
	public void removeBMDExpressAnalysisDataSetFromProject(BMDExpressAnalysisDataSet catAnalysisResults)
	{
		if (catAnalysisResults instanceof CategoryAnalysisResults)
			this.currentProject.getCategoryAnalysisResults().remove(catAnalysisResults);
		else if (catAnalysisResults instanceof BMDResult)
			this.currentProject.getbMDResult().remove(catAnalysisResults);
		else if (catAnalysisResults instanceof OneWayANOVAResults)
			this.currentProject.getOneWayANOVAResults().remove(catAnalysisResults);
		else if (catAnalysisResults instanceof WilliamsTrendResults)
			this.currentProject.getWilliamsTrendResults().remove(catAnalysisResults);
		else if (catAnalysisResults instanceof OriogenResults)
			this.currentProject.getOriogenResults().remove(catAnalysisResults);
		else if (catAnalysisResults instanceof DoseResponseExperiment)
			this.currentProject.getDoseResponseExperiments().remove(catAnalysisResults);

	}

	/*
	 * remove the dose response experiement data from the project.
	 */
	public void removeDoseResponseExperimentFromProject(DoseResponseExperiment doseResponseExperiment)
	{
		this.currentProject.getDoseResponseExperiments().remove(doseResponseExperiment);

	}

	/*
	 * export the dose response experiment to a text file
	 */
	public void exportDoseResponseExperiment(DoseResponseExperiment doseResponseExperiment, File selectedFile)
	{
		getService().exportDoseResponseExperiment(doseResponseExperiment, selectedFile);
	}

	/*
	 * write the bmdresults to a text file
	 */
	public void exportBMDExpressAnalysisDataSet(BMDExpressAnalysisDataSet bmdResults, File selectedFile)
	{
		getService().exportBMDExpressAnalysisDataSet(bmdResults, selectedFile);
	}

	/*
	 * write the best model for each probestat result to text file
	 */
	public void exportBMDResultBestModel(BMDResult bmdResults, File selectedFile)
	{
		getService().exportBMDResultBestModel(bmdResults, selectedFile);
	}

	/*
	 * show the probe to genes matrix
	 */
	public void showProbeToGeneMatrix(DoseResponseExperiment doseResponseExperiment)
	{

		Object[][] matrixData = getService().showGenesToProbeMatrix(doseResponseExperiment);

		String[] columnNames = { "Probe Set ID", "Entrez Genes", "Gene Symbols" };

		getView().showMatrixPreview("Probe to Genes: " + doseResponseExperiment.getName(),
				new MatrixData("", columnNames, matrixData));

	}

	/*
	 * show the genes to probe matrix.
	 */
	public void showGenesToProbeMatrix(DoseResponseExperiment doseResponseExperiment)
	{

		Object[][] matrixData = getService().showGenesToProbeMatrix(doseResponseExperiment);

		String[] columnNames = { "Entrez Gene", "Gene Symbol", "Probe Set ID's" };
		getView().showMatrixPreview("Gene to Probes: " + doseResponseExperiment.getName(),
				new MatrixData("", columnNames, matrixData));

	}

	public void handle_DataAnalysisResultsSpreadSheetView(BMDExpressAnalysisDataSet results)
	{
		getEventBus().post(new ShowBMDExpressDataAnalysisInSeparateWindow(results));

	}

	public void handle_DoseResponseExperimentSpreadSheetView(DoseResponseExperiment results)
	{
		getEventBus().post(new ShowDoseResponseExperimentInSeparateWindowEvent(results));
	}

	@Subscribe
	public void onCloseApplication(CloseApplicationRequestEvent event)
	{
		if (saveProjectFirstMaybe() != -1)
		{
			getView().setWindowSizeProperties();
			System.exit(0);
		}

	}

	@Subscribe
	public void onSomeoneWantsProject(GiveMeProjectRequest event)
	{
		getEventBus().post(new HeresYourProjectEvent(this.currentProject));
	}

	/*
	 * new the project event
	 */
	@Subscribe
	public void onProjectNewRequest(TryCloseProjectRequestEvent saveProjectRequest)
	{

		if (saveProjectFirstMaybe() != -1)
		{
			currentProject = null;
			currentProjectFile = null;
			getView().clearNavigationTree();
			TableViewCache.getInstance().clear();
			this.getEventBus().post(new CloseProjectRequestEvent(""));
		}

	}

	private int saveProjectFirstMaybe()
	{

		if (currentProject == null || currentProject.isProjectEmpty())
			return 0;
		int response = getView().askToSaveBeforeClose();
		if (response == 0 || response == -1)
			return response;

		if (currentProjectFile != null)
		{
			saveProject(currentProjectFile);
		}
		else // ask for a project file
		{
			File selectedFile = getView().askForAProjectFile();

			if (selectedFile != null)
			{
				this.saveProject(selectedFile);
			}
			else
			{
				return -1;
			}
		}

		return response;

	}

	@Subscribe
	public void onDataVisualizationRequest(DataVisualizationRequestRequestEvent event)
	{
		// getView().showDataVisualization(currentProject);
		getEventBus().post(new ShowDataVisualizationEvent(currentProject));

	}

	public void clearMainDataView()
	{
		getEventBus().post(new NoDataSelectedEvent(null));

	}

	public void clearMenuViewForProcessing()
	{
		getEventBus().post(new NoDataSelectedForProcessingEvent(null));

	}

	/*
	 * A list of analysis data sets will be exported to one or more files. If there are datasets with varying
	 * headers, then we will export to more than one file.
	 */
	public void exportMultipleResults(List<BMDExpressAnalysisDataSet> selectedItems, File selectedFile)
	{
		List<BMDExpressAnalysisDataSet> datasets = new ArrayList<>();
		for (BMDExpressAnalysisDataSet item : selectedItems)
			datasets.add(item);

		CombinedDataSet combined = combinerService.combineBMDExpressAnalysisDataSets(datasets);

		getService().exportBMDExpressAnalysisDataSet(combined, selectedFile);

	}

	/*
	 * dump all the curve parameters to a file for a project. this is kindof a quick request from Dan S. July
	 * 12, 2016. to help with visualizations in spotfire. though it's not a bad idea to incorporate into the
	 * regular export functionality in the future.
	 */
	public void exportModelParameters(BMDProject bmdProject)
	{
		getService().exportModelParameters(bmdProject);
	}

	public void multipleDataSetsSelected(List<BMDExpressAnalysisDataSet> selectedItems)
	{

		if (!isDataListPure(selectedItems))
		{
			getEventBus().post(new NoDataSelectedEvent(null));
			return;
		}
		List<BMDExpressAnalysisDataSet> bmdAnalysisDataSet = new ArrayList<>();
		for (Object obj : selectedItems)
			bmdAnalysisDataSet.add((BMDExpressAnalysisDataSet) obj);

		CombinedDataSet combined = combinerService.combineBMDExpressAnalysisDataSets(bmdAnalysisDataSet);

		if (selectedItems.get(0) instanceof OneWayANOVAResults)
			getEventBus().post(new OneWayANOVADataCombinedSelectedEvent(combined));
		else if (selectedItems.get(0) instanceof WilliamsTrendResults)
			getEventBus().post(new WilliamsTrendDataCombinedSelectedEvent(combined));
		else if (selectedItems.get(0) instanceof CurveFitPrefilterResults)
			getEventBus().post(new CurveFitPrefilterDataCombinedSelectedEvent(combined));
		else if (selectedItems.get(0) instanceof OriogenResults)
			getEventBus().post(new OriogenDataCombinedSelectedEvent(combined));
		else if (selectedItems.get(0) instanceof CategoryAnalysisResults)
			getEventBus().post(new CategoryAnalysisDataCombinedSelectedEvent(combined));
		else if (selectedItems.get(0) instanceof BMDResult)
			getEventBus().post(new BMDAnalysisDataCombinedSelectedEvent(combined));
		else if (selectedItems.get(0) instanceof DoseResponseExperiment)
		{
			// clear the data view
			getEventBus().post(new NoDataSelectedEvent(""));
			// this will inform the menubar to update accordingingly
			getEventBus().post(new ExpressionDataCombinedSelectedEvent(combined));
		}

	}

	private boolean isDataListPure(List<BMDExpressAnalysisDataSet> selectedItems)
	{
		List<BMDExpressAnalysisDataSet> bmdAnalysisDataSet = new ArrayList<>();
		Map<Class, Integer> classesOfInterestMapCount = new HashMap<>();
		Set<Class> classesOfInterest = new HashSet<>();
		classesOfInterest.add(CategoryAnalysisResults.class);
		classesOfInterest.add(BMDResult.class);
		classesOfInterest.add(WilliamsTrendResults.class);
		classesOfInterest.add(OneWayANOVAResults.class);
		classesOfInterest.add(OriogenResults.class);
		classesOfInterest.add(DoseResponseExperiment.class);
		classesOfInterest.add(CurveFitPrefilterResults.class);
		for (Class c : classesOfInterest)
			classesOfInterestMapCount.put(c, 0);
		for (Object obj : selectedItems)
			for (Class c : classesOfInterest)
				if (c.isInstance(obj))
					classesOfInterestMapCount.put(c, classesOfInterestMapCount.get(c) + 1);

		boolean isPure = false;
		for (Integer val : classesOfInterestMapCount.values())
		{
			if (val.intValue() == selectedItems.size())
			{
				isPure = true;
				break;
			}
		}

		return isPure;
	}

	public void multipleDataSetsSelectedForProcessing(List<BMDExpressAnalysisDataSet> selectedItems)
	{

		if (!isDataListPure(selectedItems))
		{
			getEventBus().post(new NoDataSelectedForProcessingEvent(null));
			return;
		}

		if (selectedItems.get(0) instanceof OneWayANOVAResults)
			getEventBus().post(new OneWayANOVADataSelectedForProcessingEvent(null));
		else if (selectedItems.get(0) instanceof WilliamsTrendResults)
			getEventBus().post(new WilliamsTrendDataSelectedForProcessingEvent(null));
		else if (selectedItems.get(0) instanceof CurveFitPrefilterResults)
			getEventBus().post(new CurveFitPrefilterDataSelectedForProcessingEvent(null));
		else if (selectedItems.get(0) instanceof OriogenResults)
			getEventBus().post(new OriogenDataSelectedForProcessingEvent(null));
		else if (selectedItems.get(0) instanceof CategoryAnalysisResults)
			getEventBus().post(new CategoryAnalysisDataSelectedForProcessingEvent(null));
		else if (selectedItems.get(0) instanceof BMDResult)
			getEventBus().post(new BMDAnalysisDataSelectedForProcessingEvent(null));
		else if (selectedItems.get(0) instanceof DoseResponseExperiment)
		{
			// clear the data view
			getEventBus().post(new NoDataSelectedForProcessingEvent(""));
			// this will inform the menubar to update accordingingly
			getEventBus().post(new ExpressionDataSelectedForProcessingEvent(null));
		}

	}

	public void changeAnalysisName(BMDExpressAnalysisDataSet bmdAnalysisDataSet, String newName)
	{
		currentProject.giveBMDAnalysisUniqueName(bmdAnalysisDataSet, newName);
	}

}
