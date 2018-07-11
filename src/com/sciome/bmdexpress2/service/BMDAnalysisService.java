package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.serviceInterface.IBMDAnalysisService;
import com.sciome.bmdexpress2.util.bmds.BMDSTool;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;
import com.sciome.bmdexpress2.util.curvep.CurvePProcessor;

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
		
		DoseResponseExperiment doseResponseExperiment = processableData.getProcessableDoseResponseExperiment();
		bMDResults.setDoseResponseExperiment(doseResponseExperiment);
		if (processableData instanceof PrefilterResults)
			bMDResults.setPrefilterResults((PrefilterResults) processableData);

		List<ProbeResponse> responses = processableData.getProcessableProbeResponses();
		List<Treatment> treatments = doseResponseExperiment.getTreatments();
		List<ArrayList<Float>> numericMatrix = new ArrayList<ArrayList<Float>>();
		List<Float> doseVector = new ArrayList<Float>();
		// Fill numeric matrix
		for (int i = 0; i < responses.size(); i++)
		{
			numericMatrix.add((ArrayList<Float>) responses.get(i).getResponses());
		}

		// Fill doseVector
		for (int i = 0; i < treatments.size(); i++)
		{
			doseVector.add(treatments.get(i).getDose());
		}

		// Calculate and set wAUC values
		float currBMR = (float) inputParameters.getBmrLevel();
		List<Float> wAUCList = new ArrayList<Float>();
		for (int i = 0; i < responses.size(); i++)
		{
			wAUCList.add(CurvePProcessor.curveP(doseVector, numericMatrix.get(i), currBMR));
		}
		bMDResults.setwAUC(wAUCList);

		// Calculate and set log 2 wAUC values
		List<Float> logwAUCList = CurvePProcessor.logwAUC(wAUCList);
		bMDResults.setLogwAUC(logwAUCList);
		
		// clean up any leftovers from this process
		bMDSTool.cleanUp();
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
