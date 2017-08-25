package com.sciome.bmdexpress2.commandline;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.util.bmds.BMDSTool;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;

/*
 * System.out.print
 */
public class BMDAnalysisRunner implements IBMDSToolProgress
{

	public BMDResult runBMDAnalysis(IStatModelProcessable processableData,
			ModelSelectionParameters modelSelectionParameters, List<StatModel> modelsToRun,
			ModelInputParameters inputParameters)
	{
		inputParameters.setObservations(
				processableData.getProcessableDoseResponseExperiment().getTreatments().size());
		BMDSTool bMDSTool = new BMDSTool(processableData.getProcessableProbeResponses(),
				processableData.getProcessableDoseResponseExperiment().getTreatments(), inputParameters,
				modelSelectionParameters, modelsToRun, this, processableData);
		BMDResult bMDResults = bMDSTool.bmdAnalyses();

		bMDResults.setDoseResponseExperiment(processableData.getProcessableDoseResponseExperiment());
		return bMDResults;
	}

	@Override
	public void updateProgress(String label, double value)
	{
		System.out.print(StringUtils.rightPad(label + ": " + value, 80, " ") + "\r");

	}

	@Override
	public void clearProgress()
	{
		// TODO Auto-generated method stub

	}

}
