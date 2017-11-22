package com.sciome.bmdexpress2.serviceInterface;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDProject;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.chip.ChipInfo;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.util.annotation.FileAnnotation;

public interface IProjectNavigationService {
	public void assignArrayAnnotations(ChipInfo chipInfo, List<DoseResponseExperiment> experiments,
			FileAnnotation fileAnnotation);
	public String exportMultipleFiles(Map<String, Set<BMDExpressAnalysisDataSet>> header2rows, File selectedFile);
	public void exportBMDExpressAnalysisDataSet(BMDExpressAnalysisDataSet bmdResults, File selectedFile);
	public void exportDoseResponseExperiment(DoseResponseExperiment doseResponseExperiment, File selectedFile);
	public void exportBMDResultBestModel(BMDResult bmdResults, File selectedFile);
	public Object[][] showGenesToProbeMatrix(DoseResponseExperiment doseResponseExperiment);
	public Object[][] showProbeToGeneMatrix(DoseResponseExperiment doseResponseExperiment);
	public void exportModelParameters(BMDProject bmdProject);
}
