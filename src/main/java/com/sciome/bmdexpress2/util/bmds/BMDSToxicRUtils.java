package com.sciome.bmdexpress2.util.bmds;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.toxicR.ToxicRConstants;
import com.toxicR.ToxicRJNI;
import com.toxicR.ToxicRUtils;
import com.toxicR.model.ContinuousResult;

public class BMDSToxicRUtils
{

	public static double calculateAIC(int K, double maxlikelihood, double n)
	{
		// K += 2;
		// return -2 * Math.log(maxlikelihood) + 2 * K + (2 * K * (K + 1) / (n - K - 1));
		return -2 * Math.log(maxlikelihood) + 2 * K;
	}

	public static double[] calculateToxicR(int model, double[] Y, double[] doses, int bmdType, double BMR,
			boolean isLogNormal) throws JsonMappingException, JsonProcessingException
	{

		if (bmdType == 1)
			bmdType = ToxicRConstants.BMD_TYPE_SD;
		else if (bmdType == 2)
			bmdType = ToxicRConstants.BMD_TYPE_REL;

		ToxicRJNI tRJNI = new ToxicRJNI();
		ContinuousResult continousResult = tRJNI.runContinuous(model, Y, doses, bmdType, BMR, true,
				isLogNormal);
		double sampleSize = 1;
		double currd = -9999;
		for (double dose : doses)
		{
			if (currd != dose && currd != -9999)
				sampleSize += 1;
			currd = dose;
		}

		double aic = BMDSToxicRUtils.calculateAIC(continousResult.getNparms(), continousResult.getMax(),
				sampleSize);

		int extraparms = 1;

		if (!isLogNormal)
			extraparms = 2;

		int extraoption = 0;

		// add directionality to exponential models
		// this goes into the results
		if (model == ToxicRConstants.EXP3 || model == ToxicRConstants.EXP5)
		{
			boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
			extraoption = (isIncreasing ? 1 : -1);
		}

		// special logic for EXP3.
		double[] results = new double[6 + continousResult.getNparms() - extraparms
				+ (extraoption != 0 ? 1 : 0) + (model == ToxicRConstants.EXP3 ? 1 : 0)];
		results[0] = getBMD(continousResult.getBmdDist());
		results[1] = getBMDL(continousResult.getBmdDist());
		results[2] = getBMDU(continousResult.getBmdDist());

		results[3] = 9999;
		results[4] = continousResult.getMax();
		results[5] = aic;

		int start = 6;
		if (extraoption != 0)
			results[start++] = extraoption;
		for (int i = start; i < results.length; i++)
			results[i] = continousResult.getParms().get(i - start);

		return results;
	}

	private static double getBMDL(List<Double> bmdDist)
	{
		return getValueAtPercentile(0.05, bmdDist);
	}

	private static double getBMDU(List<Double> bmdDist)
	{
		return getValueAtPercentile(0.95, bmdDist);
	}

	private static double getBMD(List<Double> bmdDist)
	{
		return getValueAtPercentile(0.5, bmdDist);
	}

	private static double getValueAtPercentile(double percentile, List<Double> bmdDist)
	{
		double currentPercentile = bmdDist.get(1);
		for (int i = 3; i < bmdDist.size(); i += 2)
		{
			if (percentile >= currentPercentile && percentile <= bmdDist.get(i))
				return bmdDist.get(i - 1);

			currentPercentile = bmdDist.get(i);
		}
		return Double.NaN;
	}

}
