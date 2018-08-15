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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.io.FileUtils;

import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.bmdexpress2.mvp.model.stat.ChiSquareResult;
import com.sciome.bmdexpress2.mvp.model.stat.ExponentialResult;
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.PolyResult;
import com.sciome.bmdexpress2.mvp.model.stat.PowerResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.bmdexpress2.util.bmds.shared.BestModelSelectionBMDLandBMDU;
import com.sciome.bmdexpress2.util.bmds.shared.BestModelSelectionWithFlaggedHillModelEnum;
import com.sciome.bmdexpress2.util.bmds.shared.BestPolyModelTestEnum;
import com.sciome.bmdexpress2.util.bmds.shared.ExponentialModel;
import com.sciome.bmdexpress2.util.bmds.shared.FlagHillModelDoseEnum;
import com.sciome.bmdexpress2.util.bmds.shared.HillModel;
import com.sciome.bmdexpress2.util.bmds.shared.PolyModel;
import com.sciome.bmdexpress2.util.bmds.shared.PowerModel;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;
import com.sciome.bmdexpress2.util.bmds.thread.ExponentialFitThread;
import com.sciome.bmdexpress2.util.bmds.thread.HillFitThread;
import com.sciome.bmdexpress2.util.bmds.thread.IFitThread;
import com.sciome.bmdexpress2.util.bmds.thread.IModelProgressUpdater;
import com.sciome.bmdexpress2.util.bmds.thread.IProbeIndexGetter;
import com.sciome.bmdexpress2.util.bmds.thread.PolyFitThread;
import com.sciome.bmdexpress2.util.bmds.thread.PowerFitThread;
import com.sciome.bmdexpress2.util.stat.ChiSquareCalculator;
import com.sciome.bmdexpress2.util.stat.DosesStat;

/**
 * The class for BMDSTool
 *
 * @version 1.5,
 * @Last Modified 4/15/2011
 * @author Longlong Yang
 * 
 */
public class BMDSTool implements IModelProgressUpdater, IProbeIndexGetter
{
	private Vector<File>				tempFiles;
	private BufferedWriter				LOGOUT;

	private double						maxDose, lowPDose, flagDose, flagRatio;

	private final String				TEMPDIR				= "temp";

	private final double				DEFAULTDOUBLE		= -9999;
	private final double				p05					= 0.05;

	private List<ProbeResponse>			probeResponses;
	private ModelInputParameters		inputParameters;
	private ModelSelectionParameters	modelSelectionParameters;
	private List<StatModel>				modelsToRun;
	private float[]						doses;
	private BMDResult					bmdResults			= new BMDResult();

	private int							numberOfProbesRun	= 0;
	private String						currentMessage		= "";

	// the calling thing that needs to update progress to a view or something.
	private IBMDSToolProgress			progressReciever	= null;

	private List<IFitThread>			fitThreads			= new ArrayList<>();
	private boolean						cancel				= false;
	private AnalysisInfo				analysisInfo;
	private DosesStat					dosesStat;
	private List<Integer>				doseResponseQueue	= new ArrayList<>();
	private String						tmpFolder			= null;
	private boolean						isCustomTmpFolder	= false;

	/**
	 * Class constructor
	 */
	public BMDSTool(List<ProbeResponse> probeResponses, List<Treatment> treatments,
			ModelInputParameters inputParameters, ModelSelectionParameters modelSelectionParameters,
			List<StatModel> modelsToRun, IBMDSToolProgress progressReciever,
			IStatModelProcessable processableData, String tmpFolder)
	{
		this.progressReciever = progressReciever;
		this.probeResponses = probeResponses;
		this.inputParameters = inputParameters;
		this.modelSelectionParameters = modelSelectionParameters;
		this.modelsToRun = modelsToRun;

		if (tmpFolder != null && !tmpFolder.equals(""))
		{
			isCustomTmpFolder = true;
			String processName = ManagementFactory.getRuntimeMXBean().getName();
			processName = processName.replace(' ', '_');
			processName = processName.replace('@', '-');

			String userName = System.getProperty("user.name");
			if (userName != null)
				processName = userName + "-" + processName;

			// in the tmp folder, make a special folder based on process id/host name and user name
			tmpFolder += File.separator + processName;
			File tmpFolderFile = new File(tmpFolder);
			if (!tmpFolderFile.exists())
				tmpFolderFile.mkdirs();

			// now copy over all the lib stuff to the tmp folder to
			// run executables from local space.
			BMDExpressProperties.getInstance().copyLibToTmpFoler(tmpFolderFile + File.separator + "lib/");

		}
		this.tmpFolder = tmpFolder;

		bmdResults.setName(processableData.toString() + "_BMD");
		// create an array of doubles for the doses for the old code to user.
		doses = new float[treatments.size()];
		for (int i = 0; i < treatments.size(); i++)
		{
			doses[i] = treatments.get(i).getDose();
		}

		checkDoses();
		checkOptions();
		flagDose = lowPDose * flagRatio;

		analysisInfo = new AnalysisInfo();
		List<String> notes = new ArrayList<>();

		notes.add("Benchmark Dose Analyses");
		notes.add("Data Source: " + processableData.getParentDataSetName());
		notes.add("Work Source: " + processableData.toString());
		notes.add("BMDExpress2 Version: " + BMDExpressProperties.getInstance().getVersion());
		notes.add("Timestamp (Start Time): " + BMDExpressProperties.getInstance().getTimeStamp());
		notes.add("Operating System: " + System.getProperty("os.name"));
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
				notes.add(statModel.getName() + " version: " + statModel.getVersion());
				modelsIndex++;

			}
		}

		notes.add("Models fit: " + modelsToFit);
		notes.add("Maximum Iterations: " + inputParameters.getIterations());
		notes.add("Confidence Level: " + inputParameters.getConfidence());

		notes.add("Constant Variance: " + inputParameters.getConstantVariance());
		notes.add("BMR Factor: " + inputParameters.getBmrLevel());
		if (modelsToRun != null && isModelInThere("power", modelsToRun))
			notes.add("Restrict Power: " + inputParameters.getRestirctPower());
		notes.add("Highest Dose: " + maxDose);
		notes.add("Lowest Positive Dose: " + lowPDose);

		if (modelSelectionParameters.getBestPolyModelTest() == BestPolyModelTestEnum.NESTED_CHI_SQUARED)
		{
			notes.add(
					"Best Model Selection: Nested Chi Square to select best poly model followed by lowest AIC");
			notes.add("Nested Chi Square p-value cutoff: " + modelSelectionParameters.getpValue());
		}
		else if (modelSelectionParameters.getBestPolyModelTest() == BestPolyModelTestEnum.LOWEST_AIC)
		{
			notes.add("Best Model Selection: Lowest AIC");
		}

		notes.add("Fit Selected Models with Multiple Threads: " + inputParameters.getNumThreads());
		notes.add("Number of Available Processors On Machine: " + Runtime.getRuntime().availableProcessors());
		if (inputParameters.getKillTime() > 0)
			notes.add("Destory Model Processes If Run More Than: " + inputParameters.getKillTime()
					+ " milliseconds.");
		else
			notes.add("Destory Model Processes If Run More Than: none");

		analysisInfo.setNotes(notes);

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

			tempFiles = new Vector<File>();

			boolean pass = fitSelectedModels();
			closeOutFile(LOGOUT);

			if (pass)
			{

				fixBestModel(bmdResults);
			}

			Map<String, Integer> modelFailCount = new HashMap<>();
			for (ProbeStatResult result : bmdResults.getProbeStatResults())
			{
				for (StatResult sr : result.getStatResults())
				{
					if (sr.getSuccess().equals("false"))
					{
						if (modelFailCount.containsKey(sr.getModel()))
							modelFailCount.put(sr.getModel(), modelFailCount.get(sr.getModel()) + 1);
						else
							modelFailCount.put(sr.getModel(), 1);

					}
				}
			}

			long endTime = System.currentTimeMillis();

			long runTime = endTime - startTime;
			bmdResults.getAnalysisInfo().getNotes().add("Total Run Time: " + runTime / 1000 + " seconds");

			for (String key : modelFailCount.keySet())
				bmdResults.getAnalysisInfo().getNotes()
						.add(key + " # of model timeouts: " + modelFailCount.get(key));

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

		if (modelSelectionParameters.isFlagHillModel())
		{

			double i = 0.0;
			if (modelSelectionParameters
					.getFlagHillModelDose() == FlagHillModelDoseEnum.ONE_HALF_OF_LOWEST_DOSE)
			{
				i = 2.0;
			}
			else if (modelSelectionParameters
					.getFlagHillModelDose() == FlagHillModelDoseEnum.ONE_THIRD_OF_LOWEST_DOSE)
			{
				i = 3.0;
			}

			flagRatio = 1.0 / (1.0 + i);
			flagRatio = NumberManager.numberFormat(8, flagRatio);
		}

	}

	/**
	 * Step 3
	 *
	 * Fit data to selected model(s)
	 */
	private boolean fitSelectedModels()
	{
		// polys = new Vector<Integer>();

		for (int i = 0; i < modelsToRun.size(); i++)
		{

			StatModel modelToRun = modelsToRun.get(i);

			List<StatResult> statResults = null;

			if (!cancel)
			{
				if (modelToRun instanceof HillModel)
				{
					currentMessage = "running Hill Model";
					numberOfProbesRun = 0;
					progressReciever.updateProgress(currentMessage, 0.0);
					statResults = fitHillModel();

				}
				else if (modelToRun instanceof PowerModel)
				{
					currentMessage = "running Power Model";
					numberOfProbesRun = 0;
					progressReciever.updateProgress(currentMessage, 0.0);
					statResults = fitPowerModel();
				}
				else if (modelToRun instanceof ExponentialModel)
				{
					currentMessage = "running Exp " + ((ExponentialModel) modelToRun).getOption() + " Model";
					numberOfProbesRun = 0;
					progressReciever.updateProgress(currentMessage, 0.0);
					statResults = fitExponentialModel(((ExponentialModel) modelToRun).getOption());
				}
				else if (modelToRun instanceof PolyModel)
				{
					String polyString = "Linear";
					numberOfProbesRun = 0;
					if (((PolyModel) modelToRun).getDegree() > 1)
					{
						polyString = "Poly" + ((PolyModel) modelToRun).getDegree();
					}
					currentMessage = "running " + polyString + " Model";
					progressReciever.updateProgress(currentMessage, 0.0);
					statResults = fitPolynomialModel(((PolyModel) modelToRun).getDegree());
				}

				// if user said do not comput, then assign the ever so non-value -9999
				// I would assign null but these are primitives.
				if (modelSelectionParameters.getBestModelSelectionBMDLandBMDU()
						.equals(BestModelSelectionBMDLandBMDU.DO_NOT_COMPUTE))
				{
					for (StatResult statResult : statResults)
					{
						statResult.setBMDL(-9999);
						statResult.setBMDU(-9999);
					}
				}

				// deal with 0.0 values for bmdl and bmdu when compute but ignore.
				// these should never be 0.0 but if they are make them invalid
				// using our special number
				if (modelSelectionParameters.getBestModelSelectionBMDLandBMDU()
						.equals(BestModelSelectionBMDLandBMDU.COMPUTE_BUT_IGNORE))
				{
					for (StatResult statResult : statResults)
					{
						if (statResult.getBMDL() == 0.0)
							statResult.setBMDL(-9999);
						if (statResult.getBMDU() == 0.0)
							statResult.setBMDU(-9999);
					}
				}
			}

			if (statResults != null && !cancel)
			{
				// The statResults list returned by the model will be in one2one correspondence to the
				// probstatResults which is one2one correspondence with the list of ProbeResponses that was
				// passed into this class instance.
				for (int j = 0; j < bmdResults.getProbeStatResults().size(); j++)
				{
					bmdResults.getProbeStatResults().get(j).getStatResults().add(statResults.get(j));
				}
			}

		}

		if (cancel)
		{
			return true;
		}

		selectBestModels(bmdResults);

		return true;
	}

	public void selectBestModels(BMDResult bmdResults)
	{
		polyModelsTest(bmdResults);

		if (modelsToRun != null && isModelInThere("hill", modelsToRun))
		{
			if (modelSelectionParameters.isFlagHillModel())
			{
				bmdResults.getAnalysisInfo().getNotes().add("Flag Hill Model with 'k' Parameter <: "
						+ modelSelectionParameters.getFlagHillModelDose());
				bmdResults.getAnalysisInfo().getNotes().add("Best Model Selection with Flagged Hill Model: "
						+ modelSelectionParameters.getBestModelSelectionWithFlaggedHill());

				if (modelSelectionParameters
						.getBestModelSelectionWithFlaggedHill() == BestModelSelectionWithFlaggedHillModelEnum.MODIFY_BMD_IF_FLAGGED_HILL_BEST)
				{
					bmdResults.getAnalysisInfo().getNotes()
							.add("Modify BMD of flagged Hill as Best Models with Fraction of Minimum BMD: "
									+ modelSelectionParameters.getModFlaggedHillBMDFractionMinBMD());
				}

			}

		}

		if (!modelSelectionParameters.isFlagHillModel() || modelSelectionParameters
				.getBestModelSelectionWithFlaggedHill() == BestModelSelectionWithFlaggedHillModelEnum.INCLUDE_FLAGGED_HILL)
		{ // include hill flagged model
			selectBestModel(bmdResults);
		}
		else if (modelSelectionParameters
				.getBestModelSelectionWithFlaggedHill() == BestModelSelectionWithFlaggedHillModelEnum.EXCLUDE_FLAGGED_HILL_FROM_BEST)

		{ // ingnore flagged Hill
			selectBest1Model(bmdResults);
		}
		else if (modelSelectionParameters
				.getBestModelSelectionWithFlaggedHill() == BestModelSelectionWithFlaggedHillModelEnum.EXCLUDE_ALL_HILL_FROM_BEST)
		{ // ingnore All Hill
			selectBest2Model(bmdResults);
		}
		else if (modelSelectionParameters
				.getBestModelSelectionWithFlaggedHill() == BestModelSelectionWithFlaggedHillModelEnum.MODIFY_BMD_IF_FLAGGED_HILL_BEST)
		{ // consider hill flagged and modify
			selectBest3Model(bmdResults);
		}

		else if (modelSelectionParameters
				.getBestModelSelectionWithFlaggedHill() == BestModelSelectionWithFlaggedHillModelEnum.SELECT_NEXT_BEST_PVALUE_GREATER_OO5)
		{ // consider next best with p-value > 0.005
			selectBest4Model(bmdResults);
		}
	}

	/**
	 * Modified 1/6/2009 to avoid "java.io.IOException: Stream closed..."
	 *
	 * Read TempModelParams first
	 */
	private List<StatResult> fitHillModel()
	{

		// instantiate a List of StatResults
		List<StatResult> statResults = new ArrayList<>();

		// populate with empty hill result objects so the threads can process and populate
		int probeIndex = 0;
		doseResponseQueue.clear();
		for (@SuppressWarnings("unused")
		ProbeResponse probeRespone : probeResponses)
		{
			HillResult hillResult = new HillResult();
			statResults.add(hillResult);
			doseResponseQueue.add(probeIndex);
			probeIndex++;
		}

		CountDownLatch cDownLatch = new CountDownLatch(inputParameters.getNumThreads());

		// kick off a bunch of threads. HillThread class will break the data up into chunks so that
		// each thread will work on part of the data.
		for (int i = 0; i < inputParameters.getNumThreads(); i++)
		{
			HillFitThread hillThread = new HillFitThread(cDownLatch, probeResponses, statResults,
					inputParameters.getNumThreads(), i, inputParameters.getKillTime(), tmpFolder, this, this);
			hillThread.setFlag(modelSelectionParameters.isFlagHillModel(), flagDose);
			hillThread.setDoses(doses);
			hillThread.setObjects(inputParameters);
			hillThread.start();
			fitThreads.add(hillThread);
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

	private List<StatResult> fitPowerModel()
	{

		// instantiate a List of StatResults
		List<StatResult> statResults = new ArrayList<>();

		// populate with empty hill result objects so the threads can process and populate
		int probeIndex = 0;
		doseResponseQueue.clear();
		for (@SuppressWarnings("unused")
		ProbeResponse probeRespone : probeResponses)
		{
			PowerResult powerResult = new PowerResult();
			statResults.add(powerResult);
			doseResponseQueue.add(probeIndex);
			probeIndex++;
		}
		CountDownLatch cDownLatch = new CountDownLatch(inputParameters.getNumThreads());

		for (int i = 0; i < inputParameters.getNumThreads(); i++)
		{

			PowerFitThread powerThread = new PowerFitThread(cDownLatch, probeResponses, statResults,
					inputParameters.getNumThreads(), i, inputParameters.getKillTime(), tmpFolder, this, this);

			powerThread.setDoses(doses);
			powerThread.setObjects(inputParameters);
			powerThread.start();

			fitThreads.add(powerThread);
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

	private List<StatResult> fitPolynomialModel(int degree)
	{

		// instantiate a List of StatResults
		List<StatResult> statResults = new ArrayList<>();

		// populate with empty hill result objects so the threads can process and populate
		int probeIndex = 0;
		doseResponseQueue.clear();
		for (@SuppressWarnings("unused")
		ProbeResponse probeRespone : probeResponses)
		{
			PolyResult polyResult = new PolyResult();
			polyResult.setDegree(degree);
			statResults.add(polyResult);
			doseResponseQueue.add(probeIndex);
			probeIndex++;
		}

		CountDownLatch cDownLatch = new CountDownLatch(inputParameters.getNumThreads());

		for (int i = 0; i < inputParameters.getNumThreads(); i++)
		{

			inputParameters.setPolyDegree(degree);
			PolyFitThread polyThread = new PolyFitThread(cDownLatch, degree, probeResponses, statResults,
					inputParameters.getNumThreads(), i, inputParameters.getKillTime(), tmpFolder, this, this);
			polyThread.setDoses(doses);
			polyThread.setObjects(degree, inputParameters);
			polyThread.start();
			fitThreads.add(polyThread);
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

	private List<StatResult> fitExponentialModel(int option)
	{

		// instantiate a List of StatResults
		List<StatResult> statResults = new ArrayList<>();

		// populate with empty hill result objects so the threads can process and populate
		int probeIndex = 0;
		doseResponseQueue.clear();
		for (@SuppressWarnings("unused")
		ProbeResponse probeRespone : probeResponses)
		{
			ExponentialResult hillResult = new ExponentialResult();
			statResults.add(hillResult);
			doseResponseQueue.add(probeIndex);
			probeIndex++;
		}

		CountDownLatch cDownLatch = new CountDownLatch(inputParameters.getNumThreads());

		// kick off a bunch of threads. HillThread class will break the data up into chunks so that
		// each thread will work on part of the data.
		for (int i = 0; i < inputParameters.getNumThreads(); i++)
		{
			ExponentialFitThread expThread = new ExponentialFitThread(cDownLatch, probeResponses, statResults,
					inputParameters.getNumThreads(), i, option, inputParameters.getKillTime(), tmpFolder,
					this, this);
			expThread.setDoses(doses);
			expThread.setObjects(inputParameters);
			expThread.start();
			fitThreads.add(expThread);
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

	private void polyModelsTest(BMDResult bmdResult)
	{

		if (modelSelectionParameters.getBestPolyModelTest() == BestPolyModelTestEnum.NESTED_CHI_SQUARED)
		{
			nestedChi2Polys(bmdResult);
		}
		else
		{
			lowestAICPolys(bmdResult);
		}
	}

	private void nestedChi2Polys(BMDResult bmdResult)
	{
		ChiSquareCalculator chi = new ChiSquareCalculator();

		// how many poly models are there?
		int polyCount = 0;
		for (StatModel statModel : modelsToRun)
		{
			if (statModel instanceof PolyModel)
			{
				polyCount++;
			}
		}
		if (polyCount == 0)
		{
			return; // no poly models to deal with
		}

		int n = (polyCount - 1) * 2;
		int df = 1;
		for (ProbeStatResult probeStatResult : bmdResult.getProbeStatResults())
		{
			double[] chiOut = new double[n];

			List<StatResult> polyResults = probeStatResult.getStatResultsOfClassType(PolyResult.class);
			StatResult bestPoly = polyResults.get(0);
			for (int i = 0; i < polyResults.size() - 1; i++)
			{
				double lkp1 = polyResults.get(i).getFitLogLikelihood();
				double lkp2 = polyResults.get(i + 1).getFitLogLikelihood();

				if (lkp1 >= lkp2)
				{
					chiOut[i] = 0;
				}
				else
				{
					double llk = (-2) * (lkp1 - lkp2);
					chiOut[i] = NumberManager.numberFormat(8, llk);
				}

				double p = chi.pochisq(chiOut[i], df);
				chiOut[i + polyCount - 1] = NumberManager.numberFormat(5, p);
			}

			for (int i = 0; i < polyResults.size() - 1; i++)
			{
				double bmd1 = polyResults.get(i).getBMD();
				double bmdl1 = polyResults.get(i).getBMDL();
				double bmd2 = polyResults.get(i + 1).getBMD();
				double bmdl2 = polyResults.get(i + 1).getBMDL();

				double bmdu1 = polyResults.get(i).getBMDU();
				double bmdu2 = polyResults.get(i + 1).getBMDU();

				if ((chiOut[polyCount - 1 + i] > modelSelectionParameters.getpValue() && bmd1 != DEFAULTDOUBLE
						&& bmdl1 != DEFAULTDOUBLE && bmdu1 != DEFAULTDOUBLE)
						|| (chiOut[polyCount - 1 + i] < modelSelectionParameters.getpValue()
								&& (bmd2 == DEFAULTDOUBLE || bmdl2 == DEFAULTDOUBLE
										|| bmdu2 == DEFAULTDOUBLE)))
				{
					bestPoly = polyResults.get(i);
					break;
				}
				else
				{
					bestPoly = polyResults.get(i + 1);
				}
			}

			if (polyResults.size() > 1)
			{
				fillChiOutput(probeStatResult, polyResults, chiOut);
			}
			probeStatResult.setBestPolyStatResult(bestPoly);
		}
	}

	private void fillChiOutput(ProbeStatResult probeStatResult, List<StatResult> polyResults,
			double[] results)
	{
		PolyResult currResult = (PolyResult) polyResults.get(0);
		List<ChiSquareResult> chiSquaredResults = new ArrayList<>();
		for (int i = 1; i < polyResults.size(); i++)
		{
			PolyResult nextResult = (PolyResult) polyResults.get(i);
			ChiSquareResult chiSquaredResult = new ChiSquareResult();
			chiSquaredResult.setDegree1(currResult.getDegree());
			chiSquaredResult.setDegree2(nextResult.getDegree());
			chiSquaredResult.setValue(results[i - 1]);
			chiSquaredResult.setpValue(results[polyResults.size() - 1 + i - 1]);
			chiSquaredResults.add(chiSquaredResult);
			currResult = nextResult;
		}
		probeStatResult.setChiSquaredResults(chiSquaredResults);

	}

	private void lowestAICPolys(BMDResult bmdResult)
	{

		for (ProbeStatResult probeStatResult : bmdResult.getProbeStatResults())
		{
			StatResult bestPolyResult = null;

			int polyCount = 0;
			for (StatResult statResult : probeStatResult.getStatResults())
			{
				if (!(statResult instanceof PolyResult))
					continue;

				polyCount++;
				if (bestPolyResult == null)
				{
					bestPolyResult = statResult;
				}
				else
				{

					// if compute and utilize bmdl and bmdu, make sure they converged
					// in the process of selection best
					if (modelSelectionParameters.getBestModelSelectionBMDLandBMDU()
							.equals(BestModelSelectionBMDLandBMDU.COMPUTE_AND_UTILIZE))
					{
						double bmd1 = bestPolyResult.getBMD();
						double bmdl1 = bestPolyResult.getBMDL();
						double bmdu1 = bestPolyResult.getBMDU();
						double aic1 = bestPolyResult.getAIC();
						double bmd2 = statResult.getBMD();
						double bmdl2 = statResult.getBMDL();
						double bmdu2 = statResult.getBMDU();
						double aic2 = statResult.getAIC();

						// || (bmd1 != DEFAULTDOUBLE && bmdl2 != DEFAULTDOUBLE
						// the originial had bmd1 rather than bmd2 != DEFAULTDOUBLE. I changed it to bmd2
						if (((aic1 > aic2 && aic2 != DEFAULTDOUBLE) || bmd1 == DEFAULTDOUBLE
								|| bmdl1 == DEFAULTDOUBLE || bmdu1 == DEFAULTDOUBLE)
								|| ((aic1 > aic2 && aic2 != DEFAULTDOUBLE) && bmd2 != DEFAULTDOUBLE
										&& bmdl2 != DEFAULTDOUBLE && bmdu2 != DEFAULTDOUBLE))
						{
							bestPolyResult = statResult;
						}
					}
					else // don't worry about bmdl or bmdu convergence.
					{
						double bmd1 = bestPolyResult.getBMD();
						double aic1 = bestPolyResult.getAIC();
						double bmd2 = statResult.getBMD();
						double aic2 = statResult.getAIC();

						// || (bmd1 != DEFAULTDOUBLE && bmdl2 != DEFAULTDOUBLE
						// the originial had bmd1 rather than bmd2 != DEFAULTDOUBLE. I changed it to bmd2
						if (((aic1 > aic2 && aic2 != DEFAULTDOUBLE) || bmd1 == DEFAULTDOUBLE)
								|| ((aic1 > aic2 && aic2 != DEFAULTDOUBLE) && bmd2 != DEFAULTDOUBLE))
						{
							bestPolyResult = statResult;
						}

					}

				}
			}
			probeStatResult.setBestPolyStatResult(bestPolyResult);
		}
	}

	/**
	 * Given first column indices of two models, compare AIC, bmd, and bmdls and return true if the second
	 * model is better, otherwise false (i.e., as default, the first one is the better one)
	 */
	private boolean nextAICBetter(StatResult statResult1, StatResult statResult2)
	{

		double bmd1 = statResult1.getBMD();
		double bmdl1 = statResult1.getBMDL();
		double aic1 = statResult1.getAIC();
		double bmd2 = statResult2.getBMD();
		double bmdl2 = statResult2.getBMDL();
		double aic2 = statResult2.getAIC();

		double bmdu1 = statResult1.getBMDU();
		double bmdu2 = statResult2.getBMDU();

		// use bmdl and bmdu in chosing best
		if (modelSelectionParameters.getBestModelSelectionBMDLandBMDU()
				.equals(BestModelSelectionBMDLandBMDU.COMPUTE_AND_UTILIZE))
		{
			boolean better = (aic2 < aic1 && bmd2 != DEFAULTDOUBLE && bmdl2 != DEFAULTDOUBLE
					&& bmdu2 != DEFAULTDOUBLE);

			// don't allow 0's no matter what.
			if (bmd1 == 0.0 || bmdl1 == 0.0 || bmdu1 == 0.0)
				return false;

			if (aic1 < aic2)
			{ // second AIC smaller
				if ((bmd1 == DEFAULTDOUBLE || bmdl1 == DEFAULTDOUBLE || bmdu1 == DEFAULTDOUBLE)
						&& (bmd2 != DEFAULTDOUBLE && bmdl2 != DEFAULTDOUBLE && bmdu2 != DEFAULTDOUBLE)
						&& bmd2 > 0.0 && bmdl2 > 0.0 && bmdu2 > 0.0)
				{
					better = true;
				}
			}

			return better;
		}
		else // disregard bmdl and bmdu from chosing best
		{
			boolean better = (aic2 < aic1 && bmd2 != DEFAULTDOUBLE);

			// don't allow 0's no matter what.
			if (bmd1 == 0.0)
				return false;

			if (aic1 < aic2)
			{ // second AIC smaller
				if ((bmd1 == DEFAULTDOUBLE) && (bmd2 != DEFAULTDOUBLE) && bmd2 > 0.0)
				{
					better = true;
				}
			}

			return better;
		}
	}

	/**
	 * Select best model based on AIC of all available models
	 */
	private StatResult lowestAICAllModels(ProbeStatResult probeStatResult)
	{
		StatResult bestResult = null;

		for (StatResult currResult : probeStatResult.getStatResults())
		{
			if (bestResult == null)
			{
				bestResult = currResult;
			}
			else if (nextAICBetter(bestResult, currResult))
			{
				bestResult = currResult;
			}

		}

		return bestResult;
	}

	/**
	 * Select best model among no-polynomial models such as Hill, Power, etc..
	 */
	private StatResult nonPolyBetterModel(ProbeStatResult probeStatResult)
	{
		StatResult bestNonPolyResult = null;

		// for (int k = 1; k < selectedModels[0].size(); k++)
		for (StatResult currentStatResult : probeStatResult.getStatResults())
		{
			if (!(currentStatResult instanceof PolyResult))
			{
				if (bestNonPolyResult == null)
				{
					bestNonPolyResult = currentStatResult;
				}
				else if (nextAICBetter(bestNonPolyResult, currentStatResult))
				{
					bestNonPolyResult = currentStatResult;
				}
			}
		}

		return bestNonPolyResult;
	}

	/**
	 * Compare a non-poly model with the best polynomial model and select the better one
	 */
	private StatResult betterPolyModel(StatResult bestPolyResult, StatResult nonPolyResult)
	{

		if (bestPolyResult == null)
		{
			return nonPolyResult;
		}
		else if (nonPolyResult == null)
		{
			return bestPolyResult;
		}
		else if (nextAICBetter(bestPolyResult, nonPolyResult))
		{
			return nonPolyResult;
		}
		else
		{
			return bestPolyResult;
		}
	}

	/**
	 * Default: Select the best among all models based on AIC values If there are more than one polynomial
	 * model including linear, use the best polynomial model to compare with other models
	 *
	 * Modified 4/11/2011
	 */
	private void selectBestModel(BMDResult bmdResults)
	{
		// System.out.println("selectBestModel()");

		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			StatResult bestResult = null;

			bestResult = nonPolyBetterModel(probeStatResult);
			bestResult = betterPolyModel(probeStatResult.getBestPolyStatResult(), bestResult);

			probeStatResult.setBestStatResult(bestResult);
		}
	}

	/**
	 * Select the best among all models based on AIC values if two or more models Modified 11/2/2009
	 *
	 * Modified based on selectBestModel() above with Hill Flagged ignored when selecting best models based on
	 * AIC values
	 */
	private void selectBest1Model(BMDResult bmdResults)
	{
		System.out.println("selectBest1Model()");

		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			StatResult bestResult = null;

			for (StatResult statResult : probeStatResult.getStatResults())
			{
				if (statResult instanceof HillResult && isFlaggedHill((HillResult) statResult))
				{
					continue;
				}
				else if (bestResult == null)
				{
					bestResult = statResult;
				}
				else if (nextAICBetter(bestResult, statResult))
				{
					bestResult = statResult;

				}
			}

			bestResult = betterPolyModel(probeStatResult.getBestPolyStatResult(), bestResult);

			probeStatResult.setBestStatResult(bestResult);

		}
	}

	/**
	 * Select the best among all models based on AIC values if two or more models but exclude all Hill Models
	 * Modified 4/21/2010
	 *
	 * Modified based on selectBestModel() above with AllHill ignored
	 */
	private void selectBest2Model(BMDResult bmdResults)
	{
		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			StatResult bestResult = null;

			for (StatResult statResult : probeStatResult.getStatResults())
			{
				if (statResult instanceof HillResult)
				{
					continue;
				}
				else if (bestResult == null)
				{
					bestResult = statResult;
				}
				else if (nextAICBetter(bestResult, statResult))
				{
					bestResult = statResult;

				}
			}

			bestResult = betterPolyModel(probeStatResult.getBestPolyStatResult(), bestResult);

			probeStatResult.setBestStatResult(bestResult);

		}
	}

	private boolean isFlaggedHill(HillResult hillResult)
	{// , String model
		boolean isFlagged = false;

		if (modelSelectionParameters.isFlagHillModel())
		{
			short flag = hillResult.getkFlag();

			if (flag >= 1)
			{
				isFlagged = true;
			}
		}

		return isFlagged;
	}

	/**
	 * Select the best among all models based on AIC values if two or more models
	 *
	 * Modified based on original selectBestModel() above with additional consideration of Hill Flags, i.e.,
	 * if flagged Hill is the best model then modify Hill's BMD as flag ration * minimum BMD of allover other
	 * best models
	 */
	private void selectBest3Model(BMDResult bmdResults)
	{
		System.out.println("selectBest3Model()");

		int fCnts = 0;
		int[] flaggedRows = new int[bmdResults.getProbeStatResults().size()]; // row index with flagged Hill
																				// the best
		double minBMD = 0, minBMDL = 0, minBMDU = 0; // initialized and keep minimum BMD of best selected
														// model

		int i = 0;
		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			double bestBMD = maxDose, bestBMDL, bestBMDU = 0;

			StatResult bestResult = nonPolyBetterModel(probeStatResult);

			bestResult = betterPolyModel(probeStatResult.getBestPolyStatResult(), bestResult);
			probeStatResult.setBestStatResult(bestResult);

			bestBMD = bestResult.getBMD();
			bestBMDL = bestResult.getBMDL();
			bestBMDU = bestResult.getBMDU();

			if (bestResult instanceof HillResult)
			{ // Hill is the best model
				boolean hillFlag = isFlaggedHill((HillResult) bestResult);

				if (hillFlag)
				{
					flaggedRows[fCnts] = i;
					fCnts++;
				}
				else
				{ // minimum BMD from best model of non-flagged probes
					if (minBMD == 0 || minBMD > bestBMD)
					{
						minBMD = bestBMD;
						minBMDL = bestBMDL;
						minBMDU = bestBMDU;
					}
				}
			}
			i++;
		}

		if (fCnts > 0 && minBMD > 0 && minBMDL > 0 && minBMDU > 0)
		{
			modifyFlaggedHillBMDs(fCnts, flaggedRows, minBMD, minBMDL, minBMDU, bmdResults);
		}
	}

	/**
	 * Select the best among all models based on AIC values if two or more models
	 *
	 * Modified based on original selectBest2Model() above with additional cosideration of Hill Flags and the
	 * next best model, i.e., if flagged Hill is the best model, then if the next best model has the p-value >
	 * 0.05 then select the next best one, otherwise, modify Hill's BMD as flag ration * minimum BMD of
	 * allover other best models
	 */
	private void selectBest4Model(BMDResult bmdResults)
	{
		System.out.println("select4BestModel()");

		int fCnts = 0;
		int[] flaggedRows = new int[bmdResults.getProbeStatResults().size()]; // row index with flagged Hill
																				// the best
		double minBMD = 0, minBMDL = 0, minBMDU = 0; // initialized and keep minimum BMD of best selected
														// model

		int i = 0;
		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			double bestBMD = maxDose, bestBMDL, bestBMDU = 0;

			StatResult bestResult = nonPolyBetterModel(probeStatResult);

			bestResult = betterPolyModel(probeStatResult.getBestPolyStatResult(), bestResult);
			probeStatResult.setBestStatResult(bestResult);

			if (bestResult instanceof HillResult)
			{ // Hill is the best model
				boolean hillFlag = isFlaggedHill((HillResult) bestResult);

				if (hillFlag)
				{
					boolean nextBest = checkNextBestModel2(probeStatResult);

					if (!nextBest)
					{
						flaggedRows[fCnts] = i;
						fCnts++;
					}
				}
				else
				{ // minimum BMD from best model of non-flagged probes
					bestBMD = bestResult.getBMD();
					bestBMDL = bestResult.getBMDL();
					bestBMDU = bestResult.getBMDU();
					if (minBMD == 0 || minBMD > bestBMD)
					{
						minBMD = bestBMD;
						minBMDL = bestBMDL;
						minBMDU = bestBMDU;

					}
				}
			}
			i++;
		}

		if (fCnts > 0 && minBMD > 0 && minBMDL > 0 && minBMDU > 0)
		{
			modifyFlaggedHillBMDs(fCnts, flaggedRows, minBMD, minBMDL, minBMDU, bmdResults);
		}
	}

	private void modifyFlaggedHillBMDs(int max, int[] flaggedRows, double minBMD, double minBMDL,
			double minBMDU, BMDResult bmdResults)
	{
		System.out.println("BMD ratio field: ");
		double flagBMD = minBMD * modelSelectionParameters.getModFlaggedHillBMDFractionMinBMD();
		Double flagMinBMD = Double.valueOf(flagBMD);
		double flagBMDL = minBMDL * modelSelectionParameters.getModFlaggedHillBMDFractionMinBMD();
		Double flagMinBMDL = Double.valueOf(flagBMDL);

		double flagBMDU = minBMDU * modelSelectionParameters.getModFlaggedHillBMDFractionMinBMD();
		Double flagMinBMDU = Double.valueOf(flagBMDU);

		bmdResults.getAnalysisInfo().getNotes()
				.add("Minimum BMD of Best Models (excluding flagged Hill models): " + minBMD);
		bmdResults.getAnalysisInfo().getNotes()
				.add("BMD assigned to flagged Hill as Best Models: " + flagBMD);
		bmdResults.getAnalysisInfo().getNotes()
				.add("Minimum BMDL of Best Models (excluding flagged Hill models): " + minBMDL);
		bmdResults.getAnalysisInfo().getNotes()
				.add("BMDL assigned to flagged Hill as Best Models: " + flagBMDL);

		short newflag = 1;
		if (modelSelectionParameters
				.getBestModelSelectionWithFlaggedHill() == BestModelSelectionWithFlaggedHillModelEnum.MODIFY_BMD_IF_FLAGGED_HILL_BEST)
		{
			newflag = 3;
		}
		else if (modelSelectionParameters
				.getBestModelSelectionWithFlaggedHill() == BestModelSelectionWithFlaggedHillModelEnum.SELECT_NEXT_BEST_PVALUE_GREATER_OO5)
		{
			newflag = 4;
		}
		for (int i = 0; i < max; i++)
		{
			int row = flaggedRows[i];

			ProbeStatResult probeStatResult = bmdResults.getProbeStatResults().get(row);
			StatResult bestResult = probeStatResult.getBestStatResult();

			if (bestResult instanceof HillResult)
			{

				((HillResult) bestResult).setBMD(flagMinBMD);
				((HillResult) bestResult).setBMDL(flagMinBMDL);
				((HillResult) bestResult).setBMDU(flagMinBMDU);
				((HillResult) bestResult).setkFlag(newflag);
				// outMatrix[row][hillCol + 6] = flag;
			}
			// modelParams.modifyBMD(row, singleModels[1], flagBMD);
		}
	}

	/**
	 * Ignore Hill model and find the next best model if its p-value > 0.05 Modified 4/13/2011
	 */
	private boolean checkNextBestModel2(ProbeStatResult probeStatResult)
	{
		boolean nextBest = false;

		StatResult nextBestResult = null;

		for (StatResult result : probeStatResult.getStatResults())
		{
			if (!(result instanceof HillResult) && !(result instanceof PolyResult))
			{
				if (nextBestResult == null)
				{
					nextBestResult = result;
				}
				else if (nextAICBetter(nextBestResult, result))
				{
					nextBestResult = result;
				}
			}
		}

		nextBestResult = betterPolyModel(probeStatResult.getBestPolyStatResult(), nextBestResult);

		if (nextBestResult != null && nextBestResult.getFitPValue() > p05)
		{
			probeStatResult.setBestStatResult(nextBestResult);
			nextBest = true;
		}
		return nextBest;
	}

	private void fixBestModel(BMDResult bmdResults)
	{

		Integer negativeOne = new Integer(-1);
		Double negativeDef = new Double(DEFAULTDOUBLE);

		for (ProbeStatResult probeStatResult : bmdResults.getProbeStatResults())
		{
			try
			{
				// check to see if the beststatresult is indeed valid

				// compute and utilize means to force the bmdu and bmdl to be present.
				if (modelSelectionParameters.getBestModelSelectionBMDLandBMDU()
						.equals(BestModelSelectionBMDLandBMDU.COMPUTE_AND_UTILIZE))
				{
					if (negativeDef
							.compareTo(Double.valueOf(probeStatResult.getBestStatResult().getBMD())) == 0
							|| negativeDef.compareTo(
									Double.valueOf(probeStatResult.getBestStatResult().getBMDL())) == 0
							|| negativeDef.compareTo(
									Double.valueOf(probeStatResult.getBestStatResult().getBMDU())) == 0
							|| probeStatResult.getBestStatResult().getBMD() == 0.0
							|| probeStatResult.getBestStatResult().getBMDL() == 0.0
							|| probeStatResult.getBestStatResult().getBMDU() == 0.0)
					{
						probeStatResult.setBestStatResult(null);
					}
				}
				else // don't worry about the bmdl and bmdu in best model selection
				{
					if (negativeDef
							.compareTo(Double.valueOf(probeStatResult.getBestStatResult().getBMD())) == 0
							|| probeStatResult.getBestStatResult().getBMD() == 0.0)
					{
						probeStatResult.setBestStatResult(null);
					}
				}

			}
			catch (Exception e)
			{}
		}
	}

	private void initLogFile(String name)
	{
		name = name.replaceAll(" ", "_");
		File file = new File(TEMPDIR, name + ".log");
		boolean append = file.exists(); // false if new file
		LOGOUT = null;

		try
		{
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}

			LOGOUT = new BufferedWriter(new FileWriter(file, append));
			tempFiles.add(file);
		}
		catch (IOException e)
		{}
	}

	private BufferedWriter initModelFileOut(File file, String[] columnNamess)
	{
		// BufferedWriter OUT = null;
		// boolean append = file.exists(); // false if new file
		//
		// if (numThreads > 1)
		// { // multiple thread
		// append = false;
		// }
		//
		// try
		// {
		// OUT = new BufferedWriter(new FileWriter(file, append));
		// tempFiles.add(file);
		//
		// if (!append)
		// { // file not exists
		// tempFileOut(OUT, arrayMatrix.getName() + "\n");
		// modelColomns2FileOut(OUT, columnNamess);
		// }
		// }
		// catch (IOException e)
		// {}
		//
		// return OUT;

		return null;
	}

	/**
	 * Write column names to parameter file per model
	 */
	private void modelColomns2FileOut(BufferedWriter OUT, String[] columnNamess)
	{
		if (columnNamess != null)
		{
			try
			{
				OUT.write(columnNamess[0]);

				for (int i = 1; i < columnNamess.length; i++)
				{
					OUT.write("\t" + columnNamess[i]);
				}

				OUT.write("\n");
				OUT.flush();
			}
			catch (IOException e)
			{}
		}
	}

	private void tempFileOut(BufferedWriter OUT, String st)
	{
		try
		{
			OUT.write(st);
			OUT.flush();
		}
		catch (IOException e)
		{}
	}

	private void model2FileOut(BufferedWriter OUT, String id, double[] array)
	{
		try
		{
			OUT.write(id);

			for (int i = 0; i < array.length; i++)
			{
				OUT.write("\t" + array[i]);
			}

			OUT.write("\n");
			OUT.flush();
		}
		catch (IOException e)
		{}
	}

	private void threadedParameters2File(BufferedWriter OUT, int col, double[][] parameters)
	{
		// System.out.println("modelParametersToFile(): " + parameters.length);

		// try
		// {
		// String[] ids = modelParams.idNames();
		// System.out.println("IDs(): " + ids.length);

		// for (int i = 0; i < ids.length; i++)
		// {
		// model2FileOut(OUT, ids[i], parameters[i]);
		// OUT.write(ids[i]);

		// for (int j = 0; j < 5; j++) {
		// OUT.write("\t" + outMatrix[i][col + j]);
		// }

		// for (int j = 0; j < parameters[i].length; j++) {
		// OUT.write("\t" + parameters[i][j]);
		// }

		// OUT.write("\t" + outMatrix[i][col + 5] + "\n"); // direction
		// }

		// OUT.flush();
		// }
		// catch (IOException e)
		// {}
	}

	private void closeOutFile(BufferedWriter OUT)
	{
		try
		{
			if (OUT != null)
			{
				OUT.close();
			}
		}
		catch (IOException e)
		{}
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
		if (isCustomTmpFolder)
			try
			{
				FileUtils.deleteDirectory(new File(this.tmpFolder));
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
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
