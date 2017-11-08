package com.sciome.bmdexpress2.serviceInterface;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;

public interface IBMDAnalysisService {
	public BMDResult bmdAnalysis(IStatModelProcessable processableData, ModelInputParameters inputParameters, 
								 ModelSelectionParameters modelSelectionParameters, List<StatModel> modelsToRun, 
								 IBMDSToolProgress progressUpdater);
}
