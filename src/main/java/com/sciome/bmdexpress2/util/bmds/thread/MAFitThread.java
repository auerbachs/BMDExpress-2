/**
 * HillFitThread.java
 *
 *
 */

package com.sciome.bmdexpress2.util.bmds.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.stat.ModelAveragingResult;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.bmds.BMDSToxicRUtils;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.sciome.bmdexpress2.util.bmds.shared.ExponentialModel;
import com.sciome.bmdexpress2.util.bmds.shared.FunlModel;
import com.sciome.bmdexpress2.util.bmds.shared.HillModel;
import com.sciome.bmdexpress2.util.bmds.shared.PowerModel;
import com.sciome.bmdexpress2.util.bmds.shared.StatModel;
import com.toxicR.ToxicRConstants;

public class MAFitThread extends Thread implements IFitThread
{
	private CountDownLatch cdLatch;
	private List<ModelAveragingResult> maResults = new ArrayList<>();
	private ModelInputParameters inputParameters;

	private float[] doses;

	private List<ProbeResponse> probeResponses;
	boolean useMCMC = false;

	private boolean cancel = false;

	private IModelProgressUpdater progressUpdater;
	private IProbeIndexGetter probeIndexGetter;
	int[] models;

	private String tmpFolder;

	public MAFitThread(CountDownLatch cdLatch, List<ProbeResponse> probeResponses,
			List<ModelAveragingResult> maResults, boolean useMCMC, List<StatModel> modelsToRun,
			IModelProgressUpdater progressUpdater, IProbeIndexGetter probeIndexGetter)
	{
		this.progressUpdater = progressUpdater;
		this.cdLatch = cdLatch;
		this.probeResponses = probeResponses;
		this.probeIndexGetter = probeIndexGetter;
		this.maResults = maResults;
		this.useMCMC = useMCMC;

		models = new int[modelsToRun.size()];

		int i = 0;
		for (StatModel m : modelsToRun)
		{
			if (m instanceof ExponentialModel && ((ExponentialModel) m).getOption() == 3)
				models[i] = ToxicRConstants.EXP3;
			else if (m instanceof ExponentialModel && ((ExponentialModel) m).getOption() == 5)
				models[i] = ToxicRConstants.EXP5;
			else if (m instanceof FunlModel)
				models[i] = ToxicRConstants.FUNL;
			else if (m instanceof PowerModel)
				models[i] = ToxicRConstants.POWER;
			else if (m instanceof HillModel)
				models[i] = ToxicRConstants.HILL;

			i++;
		}

	}

	/*
	 * public void setJNIHillFit(HillFit hillFit) { this.hillFit = hillFit; }
	 */

	public void setDoses(float[] doses)
	{
		this.doses = doses;
	}

	public void setObjects(ModelInputParameters inputParameters)
	{
		this.inputParameters = inputParameters;
	}

	@Override
	public void run()
	{

		toxicRFit();
		try
		{
			cdLatch.countDown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void toxicRFit()
	{
		double[] dosesd = new double[doses.length];
		int di = 0;
		for (float d : doses)
			dosesd[di++] = d;
		Random rand = new Random(System.nanoTime());
		int randInt = Math.abs(rand.nextInt());

		Integer probeIndex = probeIndexGetter.getNextProbeIndex();
		while (probeIndex != null)
		{

			if (cancel)
				break;

			try
			{
				String id = probeResponses.get(probeIndex).getProbe().getId().replaceAll("\\s", "_");
				id = String.valueOf(randInt) + "_" + BMDExpressProperties.getInstance()
						.getNextTempFile(this.tmpFolder, String.valueOf(Math.abs(id.hashCode())), ".(d)");
				float[] responses = probeResponses.get(probeIndex).getResponseArray();
				double[] responsesD = new double[responses.length];
				int ri = 0;
				for (float r : responses)
					responsesD[ri++] = r;

				ModelAveragingResult statResult = BMDSToxicRUtils.calculateToxicRMA(models, responsesD,
						dosesd, inputParameters.getBmrType(), inputParameters.getBmrLevel(),
						inputParameters.getConstantVariance() != 1, useMCMC);

				maResults.set(probeIndex, statResult);

			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			this.progressUpdater.incrementModelsComputed();
			probeIndex = probeIndexGetter.getNextProbeIndex();
		}

	}

	@Override
	public void cancel()
	{
		cancel = true;
	}

}
