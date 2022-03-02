package com.sciome.bmdexpress2.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.util.Precision;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ExponentialResult;
import com.sciome.bmdexpress2.mvp.model.stat.GCurvePResult;
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.PolyResult;
import com.sciome.bmdexpress2.mvp.model.stat.PowerResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.serviceInterface.IBMDAnalysisService;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.bmds.BMDSMATool;
import com.sciome.bmdexpress2.util.bmds.BMDSTool;
import com.sciome.bmdexpress2.util.bmds.IBMDSToolProgress;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.ModelSelectionParameters;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;
import com.sciome.bmdexpress2.util.curvep.CurvePProcessor;
import com.sciome.bmdexpress2.util.curvep.GCurvePInputParameters;

public class BMDAnalysisService implements IBMDAnalysisService
{

	BMDSTool bMDSTool;
	BMDSMATool bMDSMATool;
	boolean cancel = false;

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

		Set<String> doseGroups = new HashSet<>();
		for (Float dose : doseVector)
			doseGroups.add(dose.toString());

		// Calculate and set wAUC values
		if (doseGroups.size() > 2)
		{
			// float currBMR = (float) inputParameters.getBmrLevel();
			List<Float> wAUCList = new ArrayList<Float>();
			for (int i = 0; i < responses.size(); i++)
			{

				StatResult stat = bMDResults.getProbeStatResults().get(i).getBestStatResult();

				if (stat == null)
				{
					wAUCList.add(0.0f);
					continue;
				}

				// below, wAUC metric is calculated based on parametric curves, as such, values will differ
				// from gcurvep-based estimates
				List<Float> udoses = CurvePProcessor.CollapseDoses(doseVector);
				List<Float> logudoses = new ArrayList<Float>();
				try
				{
					logudoses = CurvePProcessor.logBaseDoses(udoses, -24);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				/*
				 * String CurrID = doseResponseExperiment.getProbeResponses().get(1).getProbe().getId(); if
				 * (CurrID.equals("1367733_at")) { List<Float> uu = CurvePProcessor.CollapseDoses( doseVector
				 * ); int ll = uu.size(); CurrID = Integer.toString(ll); }
				 */

				int type = -1; // unknown parametric curve type

				if (stat instanceof PolyResult)
					type = 0;
				// if(stat instanceof LogarithmicResult) type = 10; //reserved for future
				if (stat instanceof PowerResult)
					type = 20;
				if (stat instanceof HillResult)
					type = 30;
				if (stat instanceof ExponentialResult)
					type = 40 + ((ExponentialResult) stat).getOption();

				List<Float> coffs = new ArrayList<Float>();
				double[] dcoffs = stat.getCurveParameters();
				for (int nn = 0; nn < dcoffs.length; nn++)
				{
					coffs.add((float) dcoffs[nn]);
				}
				float aucv = CurvePProcessor.intg_log_AUC(udoses, stat, type, -24, 1000); // better implement
																							// and use
																							// getResponseAt()
																							// inside
																							// StatResult
																							// class instead
																							// of exporting &
																							// re0importing
																							// model pars
				// */

				// float aucv = CurvePProcessor.calc_AUC( logudoses,
				// CurvePProcessor.calc_WgtAvResponses(doseVector, numericMatrix.get(i)) );
				float ww = CurvePProcessor.calc_wAUC(aucv, (float) Math.log10(stat.getBMD()), logudoses);
				wAUCList.add(ww);

			}
			bMDResults.setwAUC(wAUCList);

			// Calculate and set log 2 wAUC values
			List<Float> logwAUCList = CurvePProcessor.logwAUC(wAUCList);
			bMDResults.setLogwAUC(logwAUCList);
		}

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
		if (bMDSMATool != null)
		{
			bMDSMATool.cancel();

			return true;
		}
		return false;
	}

	/*
	 * Run GcurveP on the proccessable data given
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
		/*
		 * String CurrID = doseResponseExperiment.getProbeResponses().get(1).getProbe().getId(); if
		 * (CurrID.equals("1370387_at")) { //dbg int dd = 0; CurrID = Integer.toString(dd); }//
		 */

		List<ProbeStatResult> probeStatResults = new ArrayList<>();
		for (int i = 0; i < responses.size(); i++)
		{

			// someone canceled the process
			if (cancel)
			{
				cancel = false;
				return null;
			}
			List<Float> correctedPointsMinus = new ArrayList<>();
			List<Float> correctedPointsPlus = new ArrayList<>();
			// List<Float> correctedPointsNeutral = new ArrayList<>();

			/*
			 * Supply BMR directly into CurveP calls! 07.16.2019
			 */

			List<Float> collapsedDoses = CurvePProcessor.CollapseDoses(doseVector);
			Float firstNonControlDose = collapsedDoses.get(1);
			if (inputParameters.getControlDoseAdjustment() != null)
				firstNonControlDose *= inputParameters.getControlDoseAdjustment().floatValue();
			else
				firstNonControlDose *= collapsedDoses.get(1) / collapsedDoses.get(2);

			Float firstNonControlDoseLogged10 = new Float(Math.log10(firstNonControlDose.doubleValue()));

			List<Float> weightedAvgs = CurvePProcessor.calc_WgtAvResponses(doseVector, numericMatrix.get(i));
			List<Float> weightedStdDeviations = CurvePProcessor.calc_WgtSdResponses(doseVector,
					numericMatrix.get(i));

			float BMR_neg = CurvePProcessor.calc_PODR_bySD(weightedAvgs.get(0), weightedStdDeviations.get(0),
					-inputParameters.getBMR());
			float BMR_poz = CurvePProcessor.calc_PODR_bySD(weightedAvgs.get(0), weightedStdDeviations.get(0),
					inputParameters.getBMR());

			List<Float> valuesMinus = CurvePProcessor.curvePcorr(doseVector, numericMatrix.get(i),
					correctedPointsMinus, BMR_neg, -1, inputParameters.getBootStraps(),
					inputParameters.getpValueCutoff(), firstNonControlDoseLogged10);

			List<Float> valuesPlus = CurvePProcessor.curvePcorr(doseVector, numericMatrix.get(i),
					correctedPointsPlus, BMR_poz, 1, inputParameters.getBootStraps(),
					inputParameters.getpValueCutoff(), firstNonControlDoseLogged10);

			List<Float> values = valuesPlus;
			List<Float> correctedPoints = correctedPointsPlus;
			int mono = 1;

			boolean allgoodminus = Double.isFinite(valuesMinus.get(5).doubleValue())
					&& Double.isFinite(valuesMinus.get(4).doubleValue())
					&& Double.isFinite(valuesMinus.get(6).doubleValue())
					&& !Double.isNaN(valuesMinus.get(5).doubleValue())
					&& !Double.isNaN(valuesMinus.get(4).doubleValue())
					&& !Double.isNaN(valuesMinus.get(6).doubleValue());
			boolean allgoodplus = Double.isFinite(valuesPlus.get(5).doubleValue())
					&& Double.isFinite(valuesPlus.get(4).doubleValue())
					&& Double.isFinite(valuesPlus.get(6).doubleValue())
					&& !Double.isNaN(valuesPlus.get(5).doubleValue())
					&& !Double.isNaN(valuesPlus.get(4).doubleValue())
					&& !Double.isNaN(valuesPlus.get(6).doubleValue());

			// first choose the direction where fitpvalue is not 0.0
			if (valuesMinus.get(0).doubleValue() == 0.0 && valuesPlus.get(0).doubleValue() != 0.0)
			{
				values = valuesPlus;
				correctedPoints = correctedPointsPlus;
				mono = 1;
			}
			else if (valuesPlus.get(0).doubleValue() == 0.0 && valuesMinus.get(0).doubleValue() != 0.0)
			{
				mono = -1;
				values = valuesMinus;
				correctedPoints = correctedPointsMinus;
			}
			// then after fit pvalue choose the one with convergence on bmdl/bmd/bmdu
			else if (allgoodminus && !allgoodplus)
			{
				mono = -1;
				values = valuesMinus;
				correctedPoints = correctedPointsMinus;
			}
			else if (!allgoodminus && allgoodplus)
			{
				values = valuesPlus;
				correctedPoints = correctedPointsPlus;
				mono = 1;
			}
			// if all converge, and there is a pvalue != 0.0, pick best fit (fraction of saved signal), as the
			// direction
			else if (valuesPlus.get(0).doubleValue() < valuesMinus.get(0).doubleValue())
			{// ..choose
				mono = -1;
				values = valuesMinus;
				correctedPoints = correctedPointsMinus;
			}

			List<Float> correctedPointsOffsets = new ArrayList<>();

			for (int j = 0; j < numericMatrix.get(i).size(); j++)
				correctedPointsOffsets.add(numericMatrix.get(i).get(j) - correctedPoints.get(j));

			ProbeStatResult psR = new ProbeStatResult();
			GCurvePResult gResult = new GCurvePResult();
			gResult.setFitPValue(values.get(0).doubleValue());
			gResult.setAIC(Double.NaN);
			gResult.setCorrectedDoseResponseOffsetValues(correctedPointsOffsets);
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
			gResult.setAdjustedControlDoseValue(firstNonControlDose.doubleValue());
			if (mono > 0)
				gResult.setBmr(BMR_poz);
			else
				gResult.setBmr(BMR_neg);

			gResult.setWeightedAverages(weightedAvgs);
			gResult.setWeightedStdDeviations(weightedStdDeviations);

			gResult.setAdverseDirection((short) mono);
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
				.add("BMDExpress3 Version: " + BMDExpressProperties.getInstance().getVersion());
		analysisInfo.getNotes()
				.add("Timestamp (Start Time): " + BMDExpressProperties.getInstance().getTimeStamp());
		analysisInfo.getNotes().add("Operating System: " + System.getProperty("os.name"));
		analysisInfo.getNotes().add("Number of bootstraps: " + inputParameters.getBootStraps());
		analysisInfo.getNotes().add("BMR: " + inputParameters.getBMR());
		analysisInfo.getNotes().add("pValue for intervals: " + inputParameters.getpValueCutoff());
		if (inputParameters.getControlDoseAdjustment() != null)
			analysisInfo.getNotes()
					.add("Control Dose Adjustment: " + inputParameters.getControlDoseAdjustment());
		bMDResults.setAnalysisInfo(analysisInfo);

		return bMDResults;

	}

	@Override
	public BMDResult bmdAnalysisLaPlaceMA(IStatModelProcessable processableData,
			ModelInputParameters inputParameters, List<StatModel> modelsToRun,
			IBMDSToolProgress progressUpdater)
	{
		return bmdAnalysisMA(processableData, inputParameters, modelsToRun, progressUpdater, false);
	}

	@Override
	public BMDResult bmdAnalysisMCMCMA(IStatModelProcessable processableData,
			ModelInputParameters inputParameters, List<StatModel> modelsToRun,
			IBMDSToolProgress progressUpdater)
	{
		return bmdAnalysisMA(processableData, inputParameters, modelsToRun, progressUpdater, true);
	}

	private BMDResult bmdAnalysisMA(IStatModelProcessable processableData,
			ModelInputParameters inputParameters, List<StatModel> modelsToRun,
			IBMDSToolProgress progressUpdater, boolean useMCMC)
	{
		inputParameters.setObservations(
				processableData.getProcessableDoseResponseExperiment().getTreatments().size());
		bMDSMATool = new BMDSMATool(processableData.getProcessableProbeResponses(),
				processableData.getProcessableDoseResponseExperiment().getTreatments(), inputParameters,
				modelsToRun, useMCMC, progressUpdater, processableData);
		BMDResult bMDResults = bMDSMATool.bmdAnalyses();
		if (cancel)
			cancel = false;
		if (bMDResults == null)
			return null;
		DoseResponseExperiment doseResponseExperiment = processableData
				.getProcessableDoseResponseExperiment();
		bMDResults.setDoseResponseExperiment(doseResponseExperiment);
		if (processableData instanceof PrefilterResults)
			bMDResults.setPrefilterResults((PrefilterResults) processableData);

		// someone canceled this. so just uncancel it before returning.

		// clean up any leftovers from this process
		bMDSMATool.cleanUp();
		return bMDResults;
	}

}
