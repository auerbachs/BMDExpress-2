/**
 * PowerFitThread.java
 *
 *
 */

package com.sciome.bmdexpress2.util.bmds.thread;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.stat.ExponentialResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.bmds.FileExponentialFit;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;

public class ExponentialFitThread extends Thread implements IFitThread
{
	private CountDownLatch			cdLatch;
	private FileExponentialFit		fExponentialFit		= null;

	private ModelInputParameters	inputParameters;

	private float[]					doses;

	private final int[]				adversDirections	= { 0, 1, -1 };
	private List<ProbeResponse>		probeResponses;
	private List<StatResult>		powerResults;
	private int						numThread;
	private int						instanceIndex;
	private IModelProgressUpdater	progressUpdater;
	private IProbeIndexGetter		probeIndexGetter;
	private boolean					cancel				= false;
	private int						expOption			= 0;
	private String					tmpFolder;

	public ExponentialFitThread(CountDownLatch cdLatch, List<ProbeResponse> probeResponses,
			List<StatResult> powerResults, int numThread, int instanceIndex, int option, int killTime,
			String tmpFolder, IModelProgressUpdater progressUpdater, IProbeIndexGetter probeIndexGetter)
	{
		this.progressUpdater = progressUpdater;
		this.cdLatch = cdLatch;
		this.probeResponses = probeResponses;
		this.powerResults = powerResults;
		this.numThread = numThread;
		this.instanceIndex = instanceIndex;
		this.probeIndexGetter = probeIndexGetter;
		this.expOption = option;
		this.tmpFolder = tmpFolder;
		if (tmpFolder == null || tmpFolder.equals(""))
			this.tmpFolder = BMDExpressConstants.getInstance().TEMP_FOLDER;

		fExponentialFit = new FileExponentialFit(option, killTime, tmpFolder);

	}

	public void setFilePowerFit(FileExponentialFit eFit)
	{
		this.fExponentialFit = eFit;
	}

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
		if (fExponentialFit != null)
		{
			filedExponential();
		}

		try
		{
			cdLatch.countDown();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void filedExponential()
	{
		inputParameters.setAdversDirection(adversDirections[0]);

		Random rand = new Random(System.nanoTime());
		int randInt = Math.abs(rand.nextInt());

		Integer probeIndex = probeIndexGetter.getNextProbeIndex();
		while (probeIndex != null)
		{

			ExponentialResult expResult = (ExponentialResult) powerResults.get(probeIndex);

			if (cancel)
			{
				break;
			}

			try
			{
				String id = probeResponses.get(probeIndex).getProbe().getId().replaceAll("\\s", "_");
				id = String.valueOf(randInt) + "_" + BMDExpressProperties.getInstance().getNextTempFile(
						this.tmpFolder, String.valueOf(Math.abs(id.hashCode())), "_exponential.(d)");
				float[] responses = probeResponses.get(probeIndex).getResponseArray();
				double[] results = fExponentialFit.fitModel(id, inputParameters, doses, responses);

				if (results != null)
				{
					fillOutput(results, expResult);
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

	private void fillOutput(double[] results, ExponentialResult expResult)
	{
		expResult.setBMD(results[0]);
		expResult.setBMDL(results[1]);
		expResult.setBMDU(results[2]);
		expResult.setFitPValue(results[3]);
		expResult.setFitLogLikelihood(results[4]);
		expResult.setAIC(results[5]);
		expResult.setOption(expOption);

		int direction = 1;

		if (results[6] < 0)
		{
			direction = -1;
		}
		expResult.setCurveParameters(Arrays.copyOfRange(results, 6, results.length));
		expResult.setAdverseDirection((short) direction);
		expResult.setSuccess("" + fExponentialFit.isSuccess());
	}

	@Override
	public void cancel()
	{
		cancel = true;
	}
}
