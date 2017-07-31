package com.sciome.bmdexpress2.util.prefilter;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.prefilter.OneWayANOVAResult;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.probe.Treatment;
import com.sciome.bmdexpress2.util.stat.FalseDiscoveryRate;

public class OneWayANOVAAnalysis
{

	public List<OneWayANOVAResult> analyzeDoseResponseData(IStatModelProcessable processableData)
	{

		DoseResponseExperiment doseResponseExperiment = processableData
				.getProcessableDoseResponseExperiment();
		List<OneWayANOVAResult> oneWayANOVAResults = new ArrayList<>();
		OnewayAnova oneway = new OnewayAnova();

		double[] unsortP = new double[processableData.getProcessableProbeResponses().size()];

		// set up the doses
		double[] xx = new double[doseResponseExperiment.getTreatments().size()];
		int i = 0;
		for (Treatment treatment : doseResponseExperiment.getTreatments())
		{
			xx[i] = treatment.getDose();
			i++;
		}
		oneway.setVariablesXX(0, xx);
		int k = 0;

		for (ProbeResponse probeResponse : processableData.getProcessableProbeResponses())
		{

			// assign the probe name
			String probe = probeResponse.getProbe().getId();

			// set up the dose responses

			float[] yy = probeResponse.getResponseArray();
			// convert it to double
			double[] yyDouble = new double[yy.length];
			for (int i1 = 0; i1 < yy.length; i1++)
			{
				yyDouble[i1] = yy[i1];
			}

			oneway.onewayANOVA(yyDouble);

			OneWayANOVAResult oneWayResult = new OneWayANOVAResult();
			oneWayResult.setProbeResponse(probeResponse);
			oneWayResult.setfValue(oneway.fValue());
			oneWayResult.setpValue(oneway.pValue());
			oneWayResult.setDegreesOfFreedomOne((short) oneway.dfTreatment());
			oneWayResult.setDegreesOfFreedomTwo((short) oneway.dfError());

			oneWayANOVAResults.add(oneWayResult);
			unsortP[k] = oneway.pValue();
			k += 1;
		}

		FalseDiscoveryRate fdRate = new FalseDiscoveryRate(k, unsortP);

		double[] fdrPs = fdRate.falseDiscoveryRate();

		i = 0;
		for (OneWayANOVAResult oneWayResult : oneWayANOVAResults)
		{
			oneWayResult.setAdjustedPValue(fdrPs[i]);
			i++;

		}

		return oneWayANOVAResults;

	}
}
