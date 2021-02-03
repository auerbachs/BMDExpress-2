/**
 * HillFitThread.java
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
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.bmds.BMDSToxicRUtils;
import com.sciome.bmdexpress2.util.bmds.BMD_METHOD;
import com.sciome.bmdexpress2.util.bmds.FileHillFit;
import com.sciome.bmdexpress2.util.bmds.ModelInputParameters;
import com.toxicR.ToxicRConstants;
import com.toxicR.model.NormalDeviance;

public class FitDevianceThread extends Thread implements IFitThread
{
	private CountDownLatch cdLatch;

	private ModelInputParameters inputParameters;
	private float[] doses;

	private List<ProbeResponse> probeResponses;

	private boolean cancel = false;

	private IModelProgressUpdater progressUpdater;
	private IProbeIndexGetter probeIndexGetter;

	private String tmpFolder;
	private Map<String,NormalDeviance> deviance;

	public FitDevianceThread(CountDownLatch cdLatch, List<ProbeResponse> probeResponses,
			IModelProgressUpdater progressUpdater, IProbeIndexGetter probeIndexGetter, Map<String, NormalDeviance> deviance)
	{
		this.deviance = deviance;
		this.progressUpdater = progressUpdater;
		this.cdLatch = cdLatch;
		this.probeResponses = probeResponses;
		this.probeIndexGetter = probeIndexGetter;

		if (tmpFolder == null || tmpFolder.equals(""))
			this.tmpFolder = BMDExpressConstants.getInstance().TEMP_FOLDER;

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
			{
				break;
			}

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

				NormalDeviance returnDev = BMDSToxicRUtils.calculateNormalDeviance(ToxicRConstants.HILL, responsesD, dosesd,
						inputParameters.getBmrType(), inputParameters.getBmrLevel(),
						inputParameters.getConstantVariance() != 1);
				deviance.put(probeResponses.get(probeIndex).getProbe().getId(), returnDev);

				
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
