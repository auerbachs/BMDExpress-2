/**
 * HillFitThread.java
 *
 *
 */

package com.sciome.bmdexpress2.util.bmds.thread;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.bmds.FileHillFit;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;

public class HillFitThread extends Thread implements IFitThread
{
	private CountDownLatch			cdLatch;
	private FileHillFit				fHillFit			= null;

	private ModelInputParameters	inputParameters;
	private boolean					flagHill			= false;

	private double					flagDose;
	private float[]					doses;

	private final int[]				adversDirections	= { 0, 1, -1 };
	private List<ProbeResponse>		probeResponses;
	private List<StatResult>		hillResults;

	private int						numThreads;
	private int						instanceIndex;

	private boolean					cancel				= false;

	private IModelProgressUpdater	progressUpdater;
	private IProbeIndexGetter		probeIndexGetter;

	public HillFitThread(CountDownLatch cdLatch, List<ProbeResponse> probeResponses,
			List<StatResult> hillResults, int numThreads, int instanceIndex, int killTime,
			IModelProgressUpdater progressUpdater, IProbeIndexGetter probeIndexGetter)
	{
		this.progressUpdater = progressUpdater;
		this.cdLatch = cdLatch;
		this.probeResponses = probeResponses;
		this.hillResults = hillResults;
		this.numThreads = numThreads;
		this.instanceIndex = instanceIndex;
		this.probeIndexGetter = probeIndexGetter;

		fHillFit = new FileHillFit(killTime);
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
		filedHillFit();
		try
		{
			cdLatch.countDown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void filedHillFit()
	{
		inputParameters.setAdversDirection(adversDirections[0]);
		Random rand = new Random(System.nanoTime());
		int randInt = Math.abs(rand.nextInt());

		Integer probeIndex = probeIndexGetter.getNextProbeIndex();
		while (probeIndex != null)
		{

			HillResult hillResult = (HillResult) hillResults.get(probeIndex);

			if (cancel)
			{
				break;

			}

			try
			{
				// get the probe id and responses

				String id = probeResponses.get(probeIndex).getProbe().getId().replaceAll("\\s", "_");

				id = String.valueOf(randInt) + "_"
						+ BMDExpressProperties.getInstance().getNextTempFile(
								BMDExpressConstants.getInstance().TEMP_FOLDER,
								String.valueOf(Math.abs(id.hashCode())), "_hill.(d)");
				float[] responses = probeResponses.get(probeIndex).getResponseArray();
				double[] results = fHillFit.fitModel(id, inputParameters, doses, responses);

				if (results != null)
				{
					fillOutput(results, hillResult);

					if (flagHill)
					{
						if (results[9] < flagDose)
						{
							hillResult.setkFlag((short) 1);
						}
						else
						{
							hillResult.setkFlag((short) 0);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}

			// tell the calling entity that a new one is being computed
			progressUpdater.incrementModelsComputed();
			probeIndex = probeIndexGetter.getNextProbeIndex();
		}
	}

	/*
	 * given the results double array, we need to fill up the hillResult Object with the results.
	 */
	private void fillOutput(double[] results, HillResult hillResult)
	{
		hillResult.setBMD(results[0]);
		hillResult.setBMDL(results[1]);
		hillResult.setBMDU(results[2]);
		hillResult.setFitPValue(results[3]);
		hillResult.setFitLogLikelihood(results[4]);
		hillResult.setAIC(results[5]);

		int direction = 1;

		if (results[7] < 0)
		{
			direction = -1;
		}
		hillResult.setCurveParameters(Arrays.copyOfRange(results, 6, results.length));
		hillResult.setAdverseDirection((short) direction);
		hillResult.setSuccess("" + fHillFit.isSuccess());
	}

	@Override
	public void cancel()
	{
		cancel = true;
	}

	public void setFlag(boolean flagHill, double flagDose)
	{
		this.flagHill = flagHill;
		this.flagDose = flagDose;

	}
}
