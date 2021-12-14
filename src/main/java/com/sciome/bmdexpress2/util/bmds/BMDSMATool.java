/*
 * BMDSTool.java
 *
 * Copyright (c) 2005-2011 CIIT Centers for Health Research
 * 6 Davis Drive, P.O. Box 12137, Research Triangle Park, NC USA 27709-2137
 * All rights reserved.
 *
 * This program is created for Bench Mark Dose project
 * It is used to set up model fits.
 *
 * Created 11/02/2006
 */

package com.sciome.bmdexpress2.util.bmds;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ModelAveragingResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;
import com.sciome.bmdexpress2.util.bmds.thread.IFitThread;
import com.sciome.bmdexpress2.util.bmds.thread.IModelProgressUpdater;
import com.sciome.bmdexpress2.util.bmds.thread.IProbeIndexGetter;
import com.sciome.bmdexpress2.util.bmds.thread.MAFitThread;
import com.sciome.bmdexpress2.util.stat.DosesStat;

/**
 * 
 * 
 */
public class BMDSMATool implements IModelProgressUpdater, IProbeIndexGetter
{

	private double maxDose, lowPDose, flagRatio;

	private List<ProbeResponse> probeResponses;
	private ModelInputParameters inputParameters;
	private List<StatModel> modelsToRun;
	private float[] doses;
	private BMDResult bmdResults = new BMDResult();

	private int numberOfProbesRun = 0;
	private String currentMessage = "";

	// the calling thing that needs to update progress to a view or something.
	private IBMDSToolProgress progressReciever = null;

	private List<IFitThread> fitThreads = new ArrayList<>();
	private boolean cancel = false;
	private AnalysisInfo analysisInfo;
	private DosesStat dosesStat;
	private List<Integer> doseResponseQueue = new ArrayList<>();
	private boolean useMCMC = false;

	/**
	 * Class constructor
	 */
	public BMDSMATool(List<ProbeResponse> probeResponses, List<Treatment> treatments,
			ModelInputParameters inputParameters, List<StatModel> modelsToRun, boolean useMCMC,
			IBMDSToolProgress progressReciever, IStatModelProcessable processableData)
	{
		this.progressReciever = progressReciever;
		this.probeResponses = probeResponses;
		this.inputParameters = inputParameters;
		this.modelsToRun = modelsToRun;
		this.useMCMC = useMCMC;

		// create an array of doubles for the doses for the old code to user.
		doses = new float[treatments.size()];
		for (int i = 0; i < treatments.size(); i++)
		{
			doses[i] = treatments.get(i).getDose();
		}

		// calculate flagDose before adjusting doses for bmd calculation
		checkDoses();
		checkOptions();
		analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		notes.add("Benchmark Dose Analyses");
		notes.add("Data Source: " + processableData.getParentDataSetName());
		notes.add("Work Source: " + processableData.toString());
		notes.add("BMDExpress3 Version: " + BMDExpressProperties.getInstance().getVersion());
		notes.add("Timestamp (Start Time): " + BMDExpressProperties.getInstance().getTimeStamp());
		notes.add("Operating System: " + System.getProperty("os.name"));
		notes.add("BMDS Major Version: " + inputParameters.getBMDSMajorVersion());
		notes.add("Model Averaging Method: " + inputParameters.getMAMethod());

		String modelsToFit = "";
		if (modelsToRun != null)
		{
			int modelsIndex = 0;
			for (StatModel statModel : modelsToRun)
			{
				modelsToFit += statModel.getName();
				if (modelsIndex < modelsToRun.size() - 1)
				{
					modelsToFit += ", ";
				}
				notes.add(statModel.getName() + " version: ToxicR");
				modelsIndex++;

			}
		}

		notes.add("Models fit: " + modelsToFit);
		notes.add("Maximum Iterations: " + inputParameters.getIterations());
		notes.add("Confidence Level: " + inputParameters.getConfidence());

		notes.add("Constant Variance: " + inputParameters.getConstantVariance());
		if (inputParameters.getBmrType() == 1)
			notes.add("BMR Type: Standard Deviation");
		else if (inputParameters.getBmrType() == 2)
			notes.add("BMR Type: Relative Deviation");
		notes.add("BMR Factor: " + inputParameters.getBmrLevel());
		// if (modelsToRun != null && isModelInThere("power", modelsToRun))
		// notes.add("Restrict Power: " + inputParameters.getRestirctPower());
		notes.add("Highest Dose: " + maxDose);
		notes.add("Lowest Positive Dose: " + lowPDose);

		notes.add("Fit Selected Models with Multiple Threads: " + inputParameters.getNumThreads());
		notes.add("Number of Available Processors On Machine: " + Runtime.getRuntime().availableProcessors());
		if (inputParameters.getKillTime() > 0)
			notes.add("Destory Model Processes If Run More Than: " + inputParameters.getKillTime()
					+ " milliseconds.");
		else
			notes.add("Destory Model Processes If Run More Than: none");

		analysisInfo.setNotes(notes);

		bmdResults.setName(processableData.toString() + "_BMD");

	}

	private boolean isModelInThere(String modelName, List<StatModel> modelsToFit)
	{
		for (StatModel statModel : modelsToRun)
		{
			if (statModel.getName().equals(modelName))
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	public BMDResult bmdAnalyses()
	{

		long startTime = System.currentTimeMillis();

		bmdResults.setAnalysisInfo(analysisInfo);

		if (probeResponses != null)
		{

			// set the values and initialize for the new bmdResults.
			bmdResults.setProbeStatResults(new ArrayList<ProbeStatResult>());
			for (ProbeResponse probeResponse : probeResponses)
			{
				ProbeStatResult probeStatResult = new ProbeStatResult();
				probeStatResult.setProbeResponse(probeResponse);
				probeStatResult.setStatResults(new ArrayList<StatResult>());
				bmdResults.getProbeStatResults().add(probeStatResult);
			}

			boolean pass = fitSelectedModels();

			long endTime = System.currentTimeMillis();

			long runTime = endTime - startTime;
			bmdResults.getAnalysisInfo().getNotes().add("Total Run Time: " + runTime / 1000 + " seconds");

		}
		else
		{}

		if (cancel)
		{
			currentMessage = "Cancelled.";
			numberOfProbesRun = 0;
			this.progressReciever.updateProgress(currentMessage, numberOfProbesRun);
			this.progressReciever.clearProgress();
			return null;
		}

		return bmdResults;
	}

	/**
	 * Determine number of doses and polynomial models
	 *
	 * @return polyModles lists names of possible polynomial models
	 */
	private void checkDoses()
	{
		dosesStat = new DosesStat();
		dosesStat.asscendingSort(doses);
	}

	/**
	 * Check benchmark dose analysis data optons, models and parameter setting
	 */
	private void checkOptions()
	{

		maxDose = dosesStat.maxDose();
		lowPDose = dosesStat.noZeroMinDose();

	}

	/**
	 * Step 3
	 *
	 * Fit data to selected model(s)
	 */
	private boolean fitSelectedModels()
	{

		List<ModelAveragingResult> statResults = null;

		if (!cancel)
		{
			currentMessage = "running Model Averaging";
			numberOfProbesRun = 0;
			progressReciever.updateProgress(currentMessage, 0.0);
			statResults = fitMAModel();

		}

		if (statResults != null && !cancel)
			// The statResults list returned by the model will be in one2one correspondence to the
			// probstatResults which is one2one correspondence with the list of ProbeResponses that was
			// passed into this class instance.
			for (int j = 0; j < bmdResults.getProbeStatResults().size(); j++)
			{
				for (StatResult sr : statResults.get(j).getModelResults())
					bmdResults.getProbeStatResults().get(j).getStatResults().add(sr);

				bmdResults.getProbeStatResults().get(j).getStatResults().add(statResults.get(j));
				// set the beststatresult to be the model averaging one.
				bmdResults.getProbeStatResults().get(j).setBestStatResult(statResults.get(j));
			}

		if (cancel)
			return true;

		return true;
	}

	/**
	 * Modified 1/6/2009 to avoid "java.io.IOException: Stream closed..."
	 *
	 * Read TempModelParams first
	 */
	private List<ModelAveragingResult> fitMAModel()
	{

		// instantiate a List of StatResults
		List<ModelAveragingResult> statResults = new ArrayList<>();

		// populate with empty hill result objects so the threads can process and populate
		int probeIndex = 0;
		doseResponseQueue.clear();
		for (@SuppressWarnings("unused")
		ProbeResponse probeRespone : probeResponses)
		{
			ModelAveragingResult maResult = new ModelAveragingResult();
			statResults.add(maResult);
			doseResponseQueue.add(probeIndex);
			probeIndex++;
		}

		CountDownLatch cDownLatch = new CountDownLatch(inputParameters.getNumThreads());

		// kick off a bunch of threads. HillThread class will break the data up into chunks so that
		// each thread will work on part of the data.
		for (int i = 0; i < inputParameters.getNumThreads(); i++)
		{
			MAFitThread maThread = new MAFitThread(cDownLatch, probeResponses, statResults, useMCMC,
					modelsToRun, this, this);
			maThread.setDoses(doses);
			maThread.setObjects(inputParameters);
			maThread.start();
			fitThreads.add(maThread);
		}

		try
		{
			cDownLatch.await();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		return statResults;

	}

	/*
	 * Update progress. the FitThreads will fire this off so that progress can be updated
	 */
	@Override
	public void incrementModelsComputed()
	{
		numberOfProbesRun++;
		progressReciever.updateProgress(
				currentMessage + ":  " + numberOfProbesRun + "/" + probeResponses.size(),
				(double) numberOfProbesRun / (double) probeResponses.size());

	}

	/*
	 * send a cancel request to all the model fitting threads that are running.
	 */
	public void cancel()
	{

		cancel = true;
		for (IFitThread fitThread : fitThreads)
		{
			if (fitThread != null)
			{
				fitThread.cancel();
			}
		}
		if (fitThreads.size() > 0)
		{
			currentMessage = "Cancelling...";
			this.progressReciever.updateProgress(currentMessage, numberOfProbesRun);
		}
		fitThreads.clear();

	}

	public void cleanUp()
	{

	}

	@Override
	public synchronized Integer getNextProbeIndex()
	{
		if (doseResponseQueue.size() > 0)
		{
			Integer val = doseResponseQueue.get(0);
			doseResponseQueue.remove(0);
			return val;
		}

		return null;
	}

}
