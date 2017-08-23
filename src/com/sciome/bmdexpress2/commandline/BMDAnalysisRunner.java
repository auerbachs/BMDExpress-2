package com.sciome.bmdexpress2.commandline;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.util.bmds.BMDSTool;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;

public class BMDAnalysisRunner implements IBMDSToolProgress
{

	public void runBMDAnalysis(BMDResult processableData, ModelSelectionParameters modelSelectionParameters,
			List<StatModel> modelsToRun, ModelInputParameters inputParameters)
	{
		inputParameters.setObservations(
				processableData.getProcessableDoseResponseExperiment().getTreatments().size());
		BMDSTool bMDSTool = new BMDSTool(processableData.getProcessableProbeResponses(),
				processableData.getProcessableDoseResponseExperiment().getTreatments(), inputParameters,
				modelSelectionParameters, modelsToRun, this, processableData);
		BMDResult bMDResults = bMDSTool.bmdAnalyses();

		bMDResults.setDoseResponseExperiment(processableData.getProcessableDoseResponseExperiment());
	}

	@Override
	public void updateProgress(String label, double value)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void clearProgress()
	{
		// TODO Auto-generated method stub

	}

}
