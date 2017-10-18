package com.sciome.bmdexpress2.commandline;

import java.io.File;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.OriogenResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.WilliamsTrendResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.presenter.mainstage.ProjectNavigationPresenter;
import com.sciome.bmdexpress2.mvp.viewinterface.mainstage.IProjectNavigationView;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.shared.eventbus.BMDExpressEventBus;
import com.sciome.bmdexpress2.util.ExperimentFileUtil;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;

import javafx.stage.Window;

public class ExpressionImportRunner implements IProjectNavigationView
{

	public DoseResponseExperiment runExpressionImport(File file, String chipID, String outputName,
			LogTransformationEnum logtransformation)
	{
		ProjectNavigationPresenter presenter = new ProjectNavigationPresenter(this,
				BMDExpressEventBus.getInstance());

		DoseResponseExperiment doseResponseExperiment = ExperimentFileUtil.getInstance().readFile(file);
		doseResponseExperiment.setLogTransformation(logtransformation);

		Hashtable<String, Integer> probeHash = new Hashtable<>();
		for (ProbeResponse probeResponse : doseResponseExperiment.getProbeResponses())
			probeHash.put(probeResponse.getProbe().getId(), 1);
		FileAnnotation ann = new FileAnnotation();
		ann.setProbesHash(probeHash);
		ann.readArraysInfo();

		ChipInfo chipInfo = ann.getChip(chipID);
		presenter.assignArrayAnnotations(chipInfo, Arrays.asList(doseResponseExperiment), ann);

		doseResponseExperiment.setName(outputName);
		return doseResponseExperiment;
	}

	@Override
	public void clearNavigationTree()
	{

	}

	@Override
	public void addDoseResponseExperiement(DoseResponseExperiment doseResponseExperiment, boolean selectIt)
	{

	}

	@Override
	public void addOneWayANOVAAnalysis(OneWayANOVAResults getPayload, boolean selectIt)
	{

	}

	@Override
	public void addWilliamsTrendAnalysis(WilliamsTrendResults getPayload, boolean selectIt) {
		
	}
	
	@Override
	public void addBMDAnalysis(BMDResult getPayload, boolean selectIt)
	{

	}

	@Override
	public void addCategoryAnalysis(CategoryAnalysisResults getPayload, boolean selectIt)
	{

	}

	@Override
	public void performOneWayANOVA()
	{

	}
	
	@Override
	public void performWilliamsTrend()
	{

	}

	@Override
	public void performBMDAnalysis()
	{

	}

	@Override
	public void performCategoryAnalysis(CategoryAnalysisEnum categoryAnalysisEnum)
	{

	}

	@Override
	public void expandTree()
	{

	}

	@Override
	public int askToSaveBeforeClose()
	{
		return 0;
	}

	@Override
	public File askForAProjectFile()
	{
		return null;
	}

	@Override
	public File askForAProjectFileToOpen()
	{
		return null;
	}

	@Override
	public void showMatrixPreview(String string, MatrixData matrixData)
	{

	}

	@Override
	public void setWindowSizeProperties()
	{

	}

	@Override
	public Window getWindow()
	{

		return null;
	}

	@Override
	public void getAChip(List<ChipInfo> choices, List<DoseResponseExperiment> doseResponseExperiment,
			FileAnnotation fileAnnotation)
	{

	}

	@Override
	public File askForABMDFileToImport()
	{
		return null;
	}

	@Override
	public File askForAJSONFileToImport()
	{
		return null;
	}

	@Override
	public void addOriogenAnalysis(OriogenResults getPayload, boolean selectIt) {
		
	}

	@Override
	public void performOriogen() {
		
	}


}
