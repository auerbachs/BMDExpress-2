package com.sciome.bmdexpress2.mvp.viewinterface.mainstage;

import java.io.File;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.category.CategoryAnalysisResults;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResults;
import com.sciome.bmdexpress2.mvp.model.prefilter.PathwayFilterResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.shared.CategoryAnalysisEnum;
import com.sciome.bmdexpress2.util.MatrixData;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;

import javafx.stage.Window;

public interface IProjectNavigationView
{
	public void clearNavigationTree();

	public void addDoseResponseExperiement(DoseResponseExperiment doseResponseExperiment, boolean selectIt);

	public void addOneWayANOVAAnalysis(OneWayANOVAResults getPayload, boolean selectIt);

	public void addBMDAnalysis(BMDResult getPayload, boolean selectIt);

	public void addCategoryAnalysis(CategoryAnalysisResults getPayload, boolean selectIt);

	public void performOneWayANOVA();

	public void performBMDAnalysis();

	public void performCategoryAnalysis(CategoryAnalysisEnum categoryAnalysisEnum);

	void addPathwayFilterResults(PathwayFilterResults pathwayFilterResults, boolean selectIt);

	void performPathwayFilter();

	public void expandTree();

	public int askToSaveBeforeClose();

	public File askForAProjectFile();

	public File askForAProjectFileToOpen();

	public void showMatrixPreview(String string, MatrixData matrixData);

	public void setWindowSizeProperties();

	public Window getWindow();

	public void getAChip(List<ChipInfo> choices, List<DoseResponseExperiment> doseResponseExperiment,
			FileAnnotation fileAnnotation);

	File askForABMDFileToImport();

}
