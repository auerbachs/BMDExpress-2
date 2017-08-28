package com.sciome.bmdexpress2.mvp.presenter.mainstage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.google.common.eventbus.Subscribe;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResults;
import com.sciome.bmdexpress2.mvp.model.probe.Probe;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.refgene.EntrezGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.mvp.presenter.PresenterBase;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IProjectNavigationView;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.shared.TableViewCache;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.BMDAnalysisRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.CategoryAnalysisRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ExpressionDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.NoDataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVADataSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.OneWayANOVARequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterDataLoadedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.PathwayFilterSelectedEvent;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ShowBMDExpressDataAnalysisInSeparateWindow;
import com.sciome.bmdexpress2.shared.eventbus.analysis.ShowDoseResponseExperimentInSeparateWindowEvent;
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
import com.sciome.bmdexpress2.shared.eventbus.project.ShowMessageEvent;
import com.sciome.bmdexpress2.shared.eventbus.project.TryCloseProjectRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.DataVisualizationRequestRequestEvent;
import com.sciome.bmdexpress2.shared.eventbus.visualizations.ShowDataVisualizationEvent;
import com.sciome.bmdexpress2.util.DialogWithThreadProcess;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;

import javafx.scene.control.TreeItem;

public class ProjectNavigationPresenter extends PresenterBase<IProjectNavigationView>
{

	private BMDProject	currentProject				= new BMDProject();
	private File		currentProjectFile;

	private final int	MAX_FILES_FOR_MULTI_EXPORT	= 10;

	public ProjectNavigationPresenter(IProjectNavigationView view, BMDExpressEventBus eventBus)
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

	/*
	 * dose response data selected, post it to event bus
	 */
	public void doseResponseExperimentSelected(DoseResponseExperiment doseResponseExperiment)
	{
		getEventBus().post(new ExpressionDataSelectedEvent(doseResponseExperiment));
	}

	/*
	 * post the onewayaonove result was selected.
	 */
	public void BMDExpressAnalysisDataSetSelected(BMDExpressAnalysisDataSet dataset)
	{
		if (dataset instanceof OneWayANOVAResults)
			getEventBus().post(new OneWayANOVADataSelectedEvent((OneWayANOVAResults) dataset));
		else if (dataset instanceof PathwayFilterResults)
			getEventBus().post(new PathwayFilterSelectedEvent((PathwayFilterResults) dataset));
		else if (dataset instanceof CategoryAnalysisResults)
			getEventBus().post(new CategoryAnalysisDataSelectedEvent((CategoryAnalysisResults) dataset));
		else if (dataset instanceof BMDResult)
			getEventBus().post(new BMDAnalysisDataSelectedEvent((BMDResult) dataset));

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
		for (ProbeResponse probeResponse : experiments.get(0).getProbeResponses())
		{
			probeHash.put(probeResponse.getProbe().getId(), 1);
		}
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
		getView().addOneWayANOVAAnalysis(event.GetPayload(), true);
		currentProject.getOneWayANOVAResults().add(event.GetPayload());
	}

	/*
	 * load pathway filter results into the view.
	 */
	@Subscribe
	public void onLoadPathwayFilterResults(PathwayFilterDataLoadedEvent event)
	{
		getView().addPathwayFilterResults(event.GetPayload(), true);
		currentProject.getPathwayFilterResults().add(event.GetPayload());
	}

	/*
	 * load a bmdresults into the view.
	 */
	@Subscribe
	public void onLoadBMDAnalysis(BMDAnalysisDataLoadedEvent event)
	{
		getView().addBMDAnalysis(event.GetPayload(), true);
		currentProject.getbMDResult().add(event.GetPayload());
	}

	/*
	 * load a category analysis data set into the view.
	 */
	@Subscribe
	public void onLoadCategoryAnalysis(CategoryAnalysisDataLoadedEvent event)
	{
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
	 * someone asked to perform a pathway filter analysis.
	 */
	@Subscribe
	public void onPathwayFilterAnalysisRequest(PathwayFilterRequestEvent event)
	{

		getView().performPathwayFilter();

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

		// populate all the anova data
		for (PathwayFilterResults pathWayResults : bmdProject.getPathwayFilterResults())
		{
			getView().addPathwayFilterResults(pathWayResults, false);
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

		getView().expandTree();

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
			DialogWithThreadProcess loadDialog = new DialogWithThreadProcess(getView().getWindow());
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
			DialogWithThreadProcess loadDialog = new DialogWithThreadProcess(getView().getWindow());
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
			DialogWithThreadProcess loadDialog = new DialogWithThreadProcess(getView().getWindow());
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
		DialogWithThreadProcess saveDialog = new DialogWithThreadProcess(getView().getWindow());
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
		DialogWithThreadProcess saveDialog = new DialogWithThreadProcess(getView().getWindow());
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
	@SuppressWarnings("unchecked")
	public void assignArrayAnnotations(ChipInfo chipInfo, List<DoseResponseExperiment> experiments,
			FileAnnotation fileAnnotation)
	{

		// set up the analysis information
		for (DoseResponseExperiment doseResponseExperiment : experiments)
		{
			AnalysisInfo analysisInfo = new AnalysisInfo();
			List<String> notes = new ArrayList<>();

			if (chipInfo == null)
			{
				notes.add("Chip: Generic");
				chipInfo = new ChipInfo();
				chipInfo.setName("Generic");
				chipInfo.setSpecies("Generic");
				chipInfo.setProvider("Generic");
				chipInfo.setId("Generic");

			}
			else
			{
				notes.add("Chip: " + chipInfo.getGeoName());
				notes.add("Provider: " + chipInfo.getProvider());
			}
			notes.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
			notes.add("Timestamp: " + BMDExpressProperties.getInstance().getTimeStamp());
			analysisInfo.setNotes(notes);
			doseResponseExperiment.setAnalysisInfo(analysisInfo);

			doseResponseExperiment.setChip(chipInfo);

			// try to avoid storing duplicate genes.
			Map<String, ReferenceGene> refCache = new HashMap<>();
			List<ReferenceGeneAnnotation> referenceGeneAnnotations = new ArrayList<>();
			// if there is no chip selected, the set it as Generic and load empty
			// referencegeneannotation
			if (chipInfo.getName().equals("Generic"))
			{
				doseResponseExperiment.setReferenceGeneAnnotations(referenceGeneAnnotations);
				continue;
			}
			fileAnnotation.setChip(chipInfo.getGeoID());
			fileAnnotation.arrayProbesGenes();
			fileAnnotation.arrayGenesSymbols();

			fileAnnotation.getGene2ProbeHash();

			Hashtable<String, Vector> probesToGene = fileAnnotation.getProbe2GeneHash();
			Hashtable<String, String> geneSymbolHash = fileAnnotation.getGene2SymbolHash();

			try
			{

				// let's create referenceGeneAnnotations
				for (ProbeResponse probeResponse : doseResponseExperiment.getProbeResponses())
				{
					Probe probe = probeResponse.getProbe();
					Vector<String> genes = probesToGene.get(probe.getId());
					ReferenceGeneAnnotation referenceGeneAnnotation = new ReferenceGeneAnnotation();
					List<ReferenceGene> referenceGenes = new ArrayList<>();
					if (genes == null)
						continue;
					for (String gene : genes)
					{
						ReferenceGene refGene = refCache.get(gene);
						if (refGene == null)
						{
							refGene = new EntrezGene();
							refGene.setId(gene);
							refGene.setGeneSymbol(geneSymbolHash.get(gene));
							refCache.put(gene, refGene);
						}
						referenceGenes.add(refGene);
					}
					referenceGeneAnnotation.setReferenceGenes(referenceGenes);
					referenceGeneAnnotation.setProbe(probe);

					referenceGeneAnnotations.add(referenceGeneAnnotation);
				}

				doseResponseExperiment.setReferenceGeneAnnotations(referenceGeneAnnotations);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

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
		else if (catAnalysisResults instanceof PathwayFilterResults)
			this.currentProject.getPathwayFilterResults().remove(catAnalysisResults);
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

		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
			writer.write(String.join("\n", doseResponseExperiment.getAnalysisInfo().getNotes()));
			writer.write("\n");
			writer.write(getExperimentToWrite(doseResponseExperiment, false));
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private String getExperimentToWrite(DoseResponseExperiment doseResponseExperiment, boolean prependname)
	{
		StringBuffer sb = new StringBuffer();
		List<String> row = new ArrayList<>();
		row.add("Something");

		for (Treatment treatment : doseResponseExperiment.getTreatments())
		{
			row.add(treatment.getName());
		}
		if (prependname)
		{
			sb.append(doseResponseExperiment.getName() + "\t");
		}
		sb.append(String.join("\t", row) + "\n");
		row.clear();
		row.add("Doses");

		for (Treatment treatment : doseResponseExperiment.getTreatments())
		{
			row.add(String.valueOf(treatment.getDose()));
		}
		sb.append(String.join("\t", row) + "\n");

		for (ProbeResponse result : doseResponseExperiment.getProbeResponses())
		{
			row.clear();
			row.add(result.getProbe().getId());
			for (Float response : result.getResponses())
			{
				row.add(String.valueOf(response));
			}
			if (prependname)
			{
				sb.append(doseResponseExperiment.getName() + "\t");
			}
			sb.append(String.join("\t", row) + "\n");
		}

		return sb.toString();
	}

	/*
	 * write the bmdresults to a text file
	 */
	public void exportBMDExpressAnalysisDataSet(BMDExpressAnalysisDataSet bmdResults, File selectedFile)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
			writer.write(String.join("\n", bmdResults.getAnalysisInfo().getNotes()));
			writer.write("\n");
			writer.write(String.join("\t", bmdResults.getColumnHeader()) + "\n");
			writer.write(exportBMDExpressAnalysisDataSet(bmdResults, false));
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	private String exportBMDExpressAnalysisDataSet(BMDExpressAnalysisDataSet bmdResults, boolean prepend)
	{
		StringBuffer sb = new StringBuffer();

		for (BMDExpressAnalysisRow result : bmdResults.getAnalysisRows())
		{
			if (prepend)
				sb.append(bmdResults.getName() + "\t");
			sb.append(joinRowData(result.getRow(), "\t") + "\n");
		}
		return sb.toString();
	}

	/*
	 * write the best model for each probestat result to text file
	 */
	public void exportBMDResultBestModel(BMDResult bmdResults, File selectedFile)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
			writer.write(String.join("\n", bmdResults.getAnalysisInfo().getNotes()));
			writer.write("\n");

			boolean hasHill = false;
			for (ProbeStatResult result : bmdResults.getProbeStatResults())
			{
				if (result.getBestStatResult() != null && result.getBestStatResult() instanceof HillResult)
				{
					hasHill = true;
					break;
				}
			}

			writer.write("Probe Id\tBMDS Model\t");
			writer.write("\tGenes\tGene Symbols\t");
			writer.write("BMD\tBMDL\tBMDU\tfitPValue\tfitLogLikelihood\tAIC\tadverseDirection\t2BMD/BMDL");
			if (hasHill)
				writer.write("\tFlagged Hill");
			writer.write("\n");
			for (ProbeStatResult result : bmdResults.getProbeStatResults())
			{
				if (result.getBestStatResult() != null)
				{
					writer.write(result.getProbeResponse().getProbe().getId() + "\t"
							+ result.getBestStatResult() + "\t");
					writer.write("\t" + result.getGenes() + "\t" + result.getGeneSymbols() + "\t");
					writer.write(joinRowData(result.getBestStatResult().getRow(), "\t"));
					if (!(result.getBestStatResult() instanceof HillResult))
					{// add an extra column on account of hill's k-flag
						writer.write("\t");
					}
					writer.write("\n");
				}
				else
				{
					writer.write(result.getProbeResponse().getProbe().getId() + "\t" + "none");
					writer.write("\t" + result.getGenes() + "\t" + result.getGeneSymbols() + "\n");
				}
			}
			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * show the probe to genes matrix
	 */
	public void showProbeToGeneMatrix(DoseResponseExperiment doseResponseExperiment)
	{

		Object[][] matrixData = null;
		if (doseResponseExperiment.getReferenceGeneAnnotations() != null)
		{
			matrixData = new Object[doseResponseExperiment.getReferenceGeneAnnotations().size()][];

			int i = 0;
			for (ReferenceGeneAnnotation refGeneAnnotation : doseResponseExperiment
					.getReferenceGeneAnnotations())
			{
				StringBuilder symbolBuilder = new StringBuilder();
				StringBuilder geneBuilder = new StringBuilder();
				String probeId = refGeneAnnotation.getProbe().getId();
				for (ReferenceGene refGene : refGeneAnnotation.getReferenceGenes())
				{
					if (symbolBuilder.length() > 0)
					{
						symbolBuilder.append(";");
						geneBuilder.append(";");
					}
					symbolBuilder.append(refGene.getGeneSymbol());
					geneBuilder.append(refGene.getId());
				}
				Object[] rowData = { probeId, geneBuilder.toString(), symbolBuilder.toString() };
				matrixData[i] = rowData;
				i++;
			}
		}
		else
			matrixData = new Object[0][];

		String[] columnNames = { "Probe Set ID", "Entrez Genes", "Gene Symbols" };

		getView().showMatrixPreview("Probe to Genes: " + doseResponseExperiment.getName(),
				new MatrixData("", columnNames, matrixData));

	}

	/*
	 * show the genes to probe matrix.
	 */
	public void showGenesToProbeMatrix(DoseResponseExperiment doseResponseExperiment)
	{

		Object[][] matrixData = null;
		if (doseResponseExperiment.getReferenceGeneAnnotations() != null)
		{
			Map<ReferenceGene, List<String>> geneProbeMap = new HashMap<>();

			for (ReferenceGeneAnnotation refGeneAnnotation : doseResponseExperiment
					.getReferenceGeneAnnotations())
			{
				for (ReferenceGene refGene : refGeneAnnotation.getReferenceGenes())
				{
					if (!geneProbeMap.containsKey(refGene))
					{
						geneProbeMap.put(refGene, new ArrayList<>());
					}
					geneProbeMap.get(refGene).add(refGeneAnnotation.getProbe().getId());
				}
			}
			matrixData = new Object[geneProbeMap.keySet().size()][];
			int i = 0;

			for (ReferenceGene refGeneKey : geneProbeMap.keySet())
			{
				Object rowData[] = { refGeneKey.getId(), refGeneKey.getGeneSymbol(),
						String.join(";", geneProbeMap.get(refGeneKey)) };
				matrixData[i] = rowData;
				i++;
			}
		}
		else
		{
			matrixData = new Object[0][];
		}

		String[] columnNames = { "Entrez Gene", "Gene Symbol", "Probe Set ID's" };
		getView().showMatrixPreview("Gene to Probes: " + doseResponseExperiment.getName(),
				new MatrixData("", columnNames, matrixData));

	}

	private String joinRowData(List<Object> datas, String delimiter)
	{
		StringBuffer bf = new StringBuffer();
		int i = 0;
		if (datas == null)
		{
			return "";
		}
		for (Object data : datas)
		{
			if (data != null)
			{
				bf.append(data);
			}

			if (i < datas.size())
			{
				bf.append(delimiter);
			}
		}

		return bf.toString();
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

	/*
	 * A list of analysis data sets will be exported to one or more files. If there are datasets with varying
	 * headers, then we will export to more than one file.
	 */
	public void exportMultipleResults(List<TreeItem> selectedItems, File selectedFile)
	{
		Map<String, Set<BMDExpressAnalysisDataSet>> header2Rows = new HashMap<>();
		for (TreeItem treeItem : selectedItems)
		{
			if (treeItem.getValue() instanceof BMDExpressAnalysisDataSet)
			{
				// produce a key that will be the header joined by nothing.
				String headerKey = String.join("",
						((BMDExpressAnalysisDataSet) treeItem.getValue()).getColumnHeader());
				if (header2Rows.get(headerKey) == null)
					header2Rows.put(headerKey, new HashSet<>());

				header2Rows.get(headerKey).add((BMDExpressAnalysisDataSet) treeItem.getValue());
			}

		}
		if (header2Rows.keySet().size() > MAX_FILES_FOR_MULTI_EXPORT)
		{
			BMDExpressEventBus.getInstance().post(new ShowErrorEvent(
					"There are too many distinct data sets being created due to varying column headers.  There are "
							+ header2Rows.keySet().size()
							+ " files to be created but there can only be a maximum of "
							+ MAX_FILES_FOR_MULTI_EXPORT
							+ ".  Please reduce the number of distinct datasets that you wish to export."));
			return;
		}
		String filesCreateString = "The following file was created: ";
		if (header2Rows.keySet().size() > 1)
			filesCreateString = "The following files were created (please be aware the that multiple files were generated due to varying column headers : ";

		String fileName = selectedFile.getAbsolutePath();
		String fileNameWOExtension = fileName.replaceAll("\\.txt$", "");
		List<String> filesThatWereCreated = new ArrayList<>();
		int i = 0;
		for (String key : header2Rows.keySet())
		{
			BufferedWriter writer = null;
			i++;
			try
			{
				// if there are datasets with multiple headers, then we need to create separate files for each
				if (header2Rows.keySet().size() > 1)
					selectedFile = new File(fileNameWOExtension + "-" + i + ".txt");
				writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);
				Set<BMDExpressAnalysisDataSet> dataSets = header2Rows.get(key);
				filesThatWereCreated.add(selectedFile.getName());
				boolean started = false;
				for (BMDExpressAnalysisDataSet dataSet : dataSets)
				{
					if (dataSet instanceof BMDExpressAnalysisDataSet)
					{
						if (!started) // this will only allow the unique header to be written once.
						{
							// this ensures the row data is filled.
							List<String> header = dataSet.getColumnHeader();
							// write the type of data being exported.
							// write the header.
							writer.write("Analysis\t");
							writer.write(String.join("\t", header) + "\n");
						}
						writer.write(exportBMDExpressAnalysisDataSet(dataSet, true));
					}
					else if (dataSet instanceof DoseResponseExperiment)
					{
						writer.write(getExperimentToWrite((DoseResponseExperiment) dataSet, true));
					}
					started = true;
				}
				writer.close();

			}
			catch (IOException e)
			{
				BMDExpressEventBus.getInstance().post(new ShowErrorEvent(
						"There are too many distinct data sets being created due to varying column headers.  There are "
								+ header2Rows.keySet().size()
								+ " files to be created but there can only be a maximum of "
								+ MAX_FILES_FOR_MULTI_EXPORT
								+ ".  Please reduce the number of distinct datasets that you wish to export."));
				e.printStackTrace();
			}

		}
		filesCreateString += String.join(",", filesThatWereCreated);

		BMDExpressEventBus.getInstance().post(new ShowMessageEvent(filesCreateString));

	}

	/*
	 * dump all the curve parameters to a file for a project. this is kindof a quick request from Dan S. July
	 * 12, 2016. to help with visualizations in spotfire. though it's not a bad idea to incorporate into the
	 * regular export functionality in the future.
	 */
	public void exportModelParameters(BMDProject bmdProject)
	{
		File selectedFile = new File("/tmp/modelParams.txt");
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(selectedFile), 1024 * 2000);

			for (BMDResult bmdResults : bmdProject.getbMDResult())
			{

				for (ProbeStatResult result : bmdResults.getProbeStatResults())
				{
					for (StatResult statResult : result.getStatResults())
					{
						writer.write(bmdResults.getName() + "\t"
								+ result.getProbeResponse().getProbe().getId() + "\t"
								+ result.getBestStatResult().toString() + "\t" + statResult.toString());
						double[] params = statResult.getCurveParameters();

						for (int i = 0; i < params.length; i++)
						{
							writer.write("\t" + params[i]);
						}

						for (String pname : statResult.getParametersNames())
						{
							writer.write("\t" + pname);
						}

						writer.write("\n");
					}
				}

			}

			writer.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
