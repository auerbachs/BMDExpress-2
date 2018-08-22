package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.math3.util.Precision;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.GCurvePResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.serviceInterface.IBMDAnalysisService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.bmds.BMDSTool;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;
import com.sciome.bmdexpress2.util.curvep.CurvePProcessor;
import com.sciome.bmdexpress2.util.curvep.GCurvePInputParameters;

public class BMDAnalysisService implements IBMDAnalysisService
{

	BMDSTool	bMDSTool;
	boolean		cancel	= false;

	/*
	 * Run parametric bmd analylsis via epa models on processable data
	 */
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

		// someone canceled this. so just uncancel it before returning.
		if (cancel)
			cancel = false;
		if (bMDResults == null)
			return null;

		DoseResponseExperiment doseResponseExperiment = processableData
				.getProcessableDoseResponseExperiment();
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
		cancel = true;
		if (bMDSTool != null)
		{
			bMDSTool.cancel();

			return true;
		}
		return false;
	}

	/*
	 * Run GcurveP on the processable data given
	 * 
	 */
	@Override
	public BMDResult bmdAnalysisGCurveP(IStatModelProcessable processableData,
			GCurvePInputParameters inputParameters, IBMDSToolProgress me)
	{

		BMDResult bMDResults = new BMDResult();

		DoseResponseExperiment doseResponseExperiment = processableData
				.getProcessableDoseResponseExperiment();
		bMDResults.setDoseResponseExperiment(doseResponseExperiment);
		if (processableData instanceof PrefilterResults)
			bMDResults.setPrefilterResults((PrefilterResults) processableData);

		List<ProbeResponse> responses = processableData.getProcessableProbeResponses();
		List<Treatment> treatments = doseResponseExperiment.getTreatments();
		List<ArrayList<Float>> numericMatrix = new ArrayList<ArrayList<Float>>();
		List<Float> doseVector = new ArrayList<Float>();
		// Fill numeric matrix
		for (int i = 0; i < responses.size(); i++)
			numericMatrix.add((ArrayList<Float>) responses.get(i).getResponses());

		// Fill doseVector
		for (int i = 0; i < treatments.size(); i++)
			doseVector.add(treatments.get(i).getDose());

		/* do the gcurvep processing here! */

		List<ProbeStatResult> probeStatResults = new ArrayList<>();
		for (int i = 0; i < responses.size(); i++)
		{

			// someone canceled the process
			if (cancel)
			{
				cancel = false;
				return null;
			}
			List<Float> correctedPoints = new ArrayList<>();

			int mono = -1;
			if (CurvePProcessor.calc_AUC(doseVector, numericMatrix.get(i)) > 0)
				mono = 1;

			List<Float> values = CurvePProcessor.curvePcorr(doseVector, numericMatrix.get(i), correctedPoints,
					inputParameters.getBMR(), mono, inputParameters.getBootStraps(),
					inputParameters.getpValueCutoff());

			ProbeStatResult psR = new ProbeStatResult();
			GCurvePResult gResult = new GCurvePResult();
			gResult.setFitPValue(values.get(0).doubleValue());
			gResult.setAIC(Double.NaN);
			gResult.setCorrectedDoseResponseValues(correctedPoints);
			gResult.setCurveParameters(null);
			gResult.setFitLogLikelihood(Double.NaN);
			gResult.setSuccess("true");
			gResult.setBMDL(Math.pow(10.0, values.get(4).doubleValue())); 
			gResult.setBMD(Math.pow(10.0, values.get(5).doubleValue()));
			gResult.setBMDU(Math.pow(10.0, values.get(6).doubleValue()));
			gResult.setBMDLauc(values.get(1).doubleValue());
			gResult.setBMDauc(values.get(2).doubleValue());
			gResult.setBMDUauc(values.get(3).doubleValue());
			gResult.setBMDLwAuc(values.get(7).doubleValue());
			gResult.setBMDwAuc(values.get(8).doubleValue());
			gResult.setBMDUwAuc(values.get(9).doubleValue());
			psR.setBestPolyStatResult(null);

			psR.setBestPolyStatResult(null);
			psR.setBestStatResult(gResult);
			psR.setChiSquaredResults(null);
			psR.setProbeResponse(responses.get(i));
			psR.setStatResults(new ArrayList<>(Arrays.asList(gResult)));
			probeStatResults.add(psR);
			float percentComplete = (float) i / (float) processableData.getProcessableProbeResponses().size();
			me.updateProgress("Progress: " + Precision.round(100 * percentComplete, 2) + "% complete for "
					+ processableData.getDataSetName(), percentComplete);

		}

		bMDResults.setName(processableData.toString() + "_SciomeGCurveP");
		bMDResults.setProbeStatResults(probeStatResults);
		if (processableData instanceof PrefilterResults)
			bMDResults.setPrefilterResults((PrefilterResults) processableData);

		bMDResults.setDoseResponseExperiment(processableData.getProcessableDoseResponseExperiment());
		bMDResults.setwAUC(null);
		bMDResults.setLogwAUC(null);

		AnalysisInfo analysisInfo = new AnalysisInfo();

		analysisInfo.setNotes(new ArrayList<>());
		analysisInfo.getNotes().add("Benchmark Dose Analyses With Sciome GCurveP");
		analysisInfo.getNotes().add("Data Source: " + processableData.getParentDataSetName());
		analysisInfo.getNotes().add("Work Source: " + processableData.toString());
		analysisInfo.getNotes()
				.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
		analysisInfo.getNotes()
				.add("Timestamp (Start Time): " + BMDExpressProperties.getInstance().getTimeStamp());
		analysisInfo.getNotes().add("Operating System: " + System.getProperty("os.name"));
		analysisInfo.getNotes().add("Number of bootstraps: " + inputParameters.getBootStraps());
		analysisInfo.getNotes().add("BMR: " + inputParameters.getBMR());
		analysisInfo.getNotes().add("pValue for intervals: " + inputParameters.getpValueCutoff());
		bMDResults.setAnalysisInfo(analysisInfo);

		return bMDResults;

	}

}
