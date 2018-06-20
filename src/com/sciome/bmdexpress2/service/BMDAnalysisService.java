package com.sciome.bmdexpress2.service;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.serviceInterface.IBMDAnalysisService;
import com.sciome.bmdexpress2.util.bmds.BMDSTool;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;

public class BMDAnalysisService implements IBMDAnalysisService
{

	BMDSTool bMDSTool;

	@Override
	public BMDResult bmdAnalysis(IStatModelProcessable processableData, ModelInputParameters inputParameters,
			ModelSelectionParameters modelSelectionParameters, List<StatModel> modelsToRun, String tmpFolder,
			IBMDSToolProgress progressUpdater)
	{
		inputParameters.setObservations(
				processableData.getProcessableDoseResponseExperiment().getTreatments().size());
		bMDSTool = new BMDSTool(processableData.getProcessableProbeResponses(),
				processableData.getProcessableDoseResponseExperiment().getTreatments(), inputParameters,
				modelSelectionParameters, modelsToRun, progressUpdater, processableData, tmpFolder);
		BMDResult bMDResults = bMDSTool.bmdAnalyses();
		if (bMDResults == null)
			return null;
		bMDResults.setDoseResponseExperiment(processableData.getProcessableDoseResponseExperiment());
		if (processableData instanceof PrefilterResults)
			bMDResults.setPrefilterResults((PrefilterResults) processableData);
		return bMDResults;
	}

	@Override
	public boolean cancel()
	{
		if (bMDSTool != null)
		{
			bMDSTool.cancel();
			return true;
		}
		return false;
	}

}
