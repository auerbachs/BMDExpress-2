/**
 * PolyFitThread.java
 *
 *
 */

package com.sciome.bmdexpress2.util.bmds.thread;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.stat.PolyResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.bmds.BMDSToxicRUtils;
import com.sciome.bmdexpress2.util.bmds.BMD_METHOD;
import com.sciome.bmdexpress2.util.bmds.FilePolyFit;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.toxicR.ToxicRConstants;
import com.toxicR.model.NormalDeviance;

public class PolyFitThread extends Thread implements IFitThread
{
	private CountDownLatch cdLatch;
	private FilePolyFit fPolyFit = null;

	private int degree;
	private ModelInputParameters inputParameters;

	private float[] doses;

	private final int[] adversDirections = { 0, 1, -1 };

	List<ProbeResponse> probeResponses;
	List<StatResult> polyResults;
	int numThreads;
	int instanceIndex;
	private IModelProgressUpdater progressUpdater;
	private IProbeIndexGetter probeIndexGetter;

	private boolean cancel = false;

	private final double DEFAULTDOUBLE = -9999;

	private String tmpFolder;
	private Map<String,NormalDeviance> deviance;

	public PolyFitThread(CountDownLatch cDownLatch, int degree, List<ProbeResponse> probeResponses,
			List<StatResult> polyResults, int numThreads, int instanceIndex, int killTime, String tmpFolder,
			IModelProgressUpdater progressUpdater, IProbeIndexGetter probeIndexGetter,Map<String,NormalDeviance> deviance)
	{
		this.deviance = deviance;
		this.progressUpdater = progressUpdater;
		this.cdLatch = cDownLatch;
		this.degree = degree;
		this.probeResponses = probeResponses;
		this.instanceIndex = instanceIndex;
		this.numThreads = numThreads;
		this.polyResults = polyResults;
		this.probeIndexGetter = probeIndexGetter;
		this.tmpFolder = tmpFolder;
		if (tmpFolder == null || tmpFolder.equals(""))
			this.tmpFolder = BMDExpressConstants.getInstance().TEMP_FOLDER;
		fPolyFit = new FilePolyFit(killTime, tmpFolder);

	}

	public void setDoses(float[] doses)
	{
		this.doses = doses;
	}

	public void setObjects(int degree, ModelInputParameters inputParameters)
	{
		this.degree = degree;
		this.inputParameters = inputParameters;

	}

	@Override
	public void run()
	{
		if (inputParameters.getBmdMethod().equals(BMD_METHOD.ORIGINAL))
			doFiledPolyFit();
		else
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

			PolyResult polyResult = (PolyResult) polyResults.get(probeIndex);
			// System.out.println(probeResponses.get(probeIndex).getProbe().getId());

			if (cancel)
			{
				break;
			}

			try
			{
				NormalDeviance dev = deviance.get(probeResponses.get(probeIndex).getProbe().getId());
				String id = probeResponses.get(probeIndex).getProbe().getId().replaceAll("\\s", "_");
				id = String.valueOf(randInt) + "_" + BMDExpressProperties.getInstance()
						.getNextTempFile(this.tmpFolder, String.valueOf(Math.abs(id.hashCode())), ".(d)");
				float[] responses = probeResponses.get(probeIndex).getResponseArray();
				double[] responsesD = new double[responses.length];
				int ri = 0;
				for (float r : responses)
					responsesD[ri++] = r;

				int polyModelConstant = ToxicRConstants.LINEAR;
				if (inputParameters.getPolyDegree() == 2)
					polyModelConstant = ToxicRConstants.POLY2;
				else if (inputParameters.getPolyDegree() == 3)
					polyModelConstant = ToxicRConstants.POLY3;
				else if (inputParameters.getPolyDegree() == 4)
					polyModelConstant = ToxicRConstants.POLY4;
				double[] results = null;
				if (inputParameters.getPolyDegree() == 1)
					results = BMDSToxicRUtils.calculateToxicR(polyModelConstant, responsesD, dosesd,
							inputParameters.getBmrType(), inputParameters.getBmrLevel(),
							inputParameters.getConstantVariance() != 1,dev, inputParameters.isFast());
				else
				{
					// run it in both directions.
					double[] results1 = BMDSToxicRUtils.calculateToxicR(polyModelConstant, responsesD, dosesd,
							inputParameters.getBmrType(), inputParameters.getBmrLevel(),
							inputParameters.getConstantVariance() != 1, true,dev, inputParameters.isFast());
					double[] results2 = BMDSToxicRUtils.calculateToxicR(polyModelConstant, responsesD, dosesd,
							inputParameters.getBmrType(), inputParameters.getBmrLevel(),
							inputParameters.getConstantVariance() != 1, false,dev, inputParameters.isFast());

					if ((results1[0] > results2[0] && results2[0] != DEFAULTDOUBLE)
							|| results1[0] == DEFAULTDOUBLE)
						results = results2;
					else
						results = results1;
				}

				if (results != null)
				{
					fillOutput(results, polyResult);
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			this.progressUpdater.incrementModelsComputed();
			probeIndex = probeIndexGetter.getNextProbeIndex();
		}

	}

	private void doFiledPolyFit()
	{

		Random rand = new Random(System.nanoTime());
		int randInt = Math.abs(rand.nextInt());

		Integer probeIndex = probeIndexGetter.getNextProbeIndex();
		while (probeIndex != null)
		{

			PolyResult polyResult = (PolyResult) polyResults.get(probeIndex);
			try
			{
				String id = probeResponses.get(probeIndex).getProbe().getId().replaceAll("\\s", "_");
				id = String.valueOf(randInt) + "_"
						+ BMDExpressProperties.getInstance().getNextTempFile(this.tmpFolder,
								String.valueOf(Math.abs(id.hashCode())),
								"_poly" + inputParameters.getPolyDegree() + ".(d)");
				float[] responses = probeResponses.get(probeIndex).getResponseArray();

				inputParameters.setAdversDirection(adversDirections[0]);

				if (cancel)
					break;
				if (degree > 1)
					inputParameters.setAdversDirection(adversDirections[1]);

				double[] results = fPolyFit.fitModel(id, inputParameters, doses, responses);

				if (degree > 1)
				{
					inputParameters.setAdversDirection(adversDirections[2]);
					double[] pResults1 = fPolyFit.fitModel(id, inputParameters, doses, responses);

					if ((results[0] > pResults1[0] && pResults1[0] != DEFAULTDOUBLE)
							|| results[0] == DEFAULTDOUBLE)
						results = pResults1;
				}

				if (results != null)
					fillOutput(results, polyResult);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			this.progressUpdater.incrementModelsComputed();
			probeIndex = probeIndexGetter.getNextProbeIndex();
		}
	}

	private void fillOutput(double[] results, PolyResult polyResult)
	{
		polyResult.setBMD(results[0]);
		polyResult.setBMDL(results[1]);
		polyResult.setBMDU(results[2]);
		polyResult.setFitPValue(results[3]);
		polyResult.setFitLogLikelihood(results[4]);
		polyResult.setAIC(results[5]);

		int direction = 1;

		if (results[7] < 0)
		{
			direction = -1;
		}
		polyResult.setCurveParameters(Arrays.copyOfRange(results, 6, results.length));
		polyResult.setAdverseDirection((short) direction);
		polyResult.setSuccess("" + fPolyFit.isSuccess());
	}

	@Override
	public void cancel()
	{
		cancel = true;
	}
}
