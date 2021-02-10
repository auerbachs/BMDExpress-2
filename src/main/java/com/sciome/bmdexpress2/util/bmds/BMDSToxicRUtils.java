package com.sciome.bmdexpress2.util.bmds;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sciome.bmdexpress2.mvp.model.stat.ExponentialResult;
import com.sciome.bmdexpress2.mvp.model.stat.FunlResult;
import com.sciome.bmdexpress2.mvp.model.stat.HillResult;
import com.sciome.bmdexpress2.mvp.model.stat.ModelAveragingResult;
import com.sciome.bmdexpress2.mvp.model.stat.PowerResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.util.stat.ChiSquareCalculator;
import com.toxicR.ToxicRConstants;
import com.toxicR.ToxicRJNI;
import com.toxicR.ToxicRUtils;
import com.toxicR.model.ContinuousResult;
import com.toxicR.model.ContinuousResultMA;
import com.toxicR.model.NormalDeviance;

public class BMDSToxicRUtils
{

	public static double calculateAIC(int K, double maxlikelihood)
	{
		return 2 * K - 2 * maxlikelihood;
	}

	public static NormalDeviance calculateNormalDeviance(int model, double[] Y, double[] doses, int bmdType,
			double BMR, boolean isNCV) throws JsonMappingException, JsonProcessingException
	{
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		ToxicRJNI tRJNI = new ToxicRJNI();
		return tRJNI.calculateDeviance(model, Y, doses, bmdType, BMR, true, isNCV, isIncreasing);
	}

	public static double[] calculateToxicR(int model, double[] Y, double[] doses, int bmdType, double BMR,
			boolean isNCV, NormalDeviance deviance) throws JsonMappingException, JsonProcessingException
	{
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		return calculateToxicR(model, Y, doses, bmdType, BMR, isNCV, isIncreasing, deviance);
	}

	public static double[] calculateToxicR(int model, double[] Y, double[] doses, int bmdType, double BMR,
			boolean isNCV, boolean isIncreasing, NormalDeviance deviance)
			throws JsonMappingException, JsonProcessingException
	{

		if (bmdType == 1)
			bmdType = ToxicRConstants.BMD_TYPE_SD;
		else if (bmdType == 2)
			bmdType = ToxicRConstants.BMD_TYPE_REL;

		ToxicRJNI tRJNI = new ToxicRJNI();
		ContinuousResult continousResult = tRJNI.runContinuous(model, Y, doses, bmdType, BMR, true, isNCV,
				isIncreasing);

		Double maxconstant = doses.length * Math.log((1 / Math.sqrt(2 * Math.PI)));
		Double logMax = -1 * continousResult.getMax() - maxconstant;

		double aic = BMDSToxicRUtils.calculateAIC(continousResult.getNparms(), logMax);

		int extraparms = 1;

		if (isNCV)
			extraparms = 2;

		int extraoption = 0;

		// add directionality to exponential models
		// this goes into the results
		if (model == ToxicRConstants.EXP3 || model == ToxicRConstants.EXP5)
			extraoption = (isIncreasing ? 1 : -1);

		// special logic for EXP3
		double[] results = new double[6 + continousResult.getNparms() - extraparms
				+ (extraoption != 0 ? 1 : 0) + (model == ToxicRConstants.EXP3 ? 1 : 0)];
		results[0] = getBMD(continousResult.getBmdDist());
		results[1] = getBMDL(continousResult.getBmdDist());
		results[2] = getBMDU(continousResult.getBmdDist());

		double p1 = -9999.0;
		try
		{
			ChiSquaredDistribution csd = new ChiSquaredDistribution(
					continousResult.getTotalDF() - continousResult.getModelDF());
			p1 = csd.cumulativeProbability(
					2 * (continousResult.getMax().doubleValue() - deviance.getA3().doubleValue()));
		}
		catch (Exception e)
		{}

		// double wekaP = Maths.pchisq(
		// 2 * (continousResult.getMax().doubleValue() - deviance.getA3().doubleValue()),
		// continousResult.getTotalDF() - continousResult.getModelDF());
		// System.out.println("" + continousResult.getMax() + "\t" + deviance.getA3() + "\t"
		// + continousResult.getTotalDF() + "\t" + continousResult.getModelDF() + "\t" + p1);
		ChiSquareCalculator chisq = new ChiSquareCalculator();
		// double poP = chisq.pochisq(
		// 2 * (continousResult.getMax().doubleValue() - deviance.getA3().doubleValue()),
		// (int) Math.round(continousResult.getTotalDF())
		// - (int) Math.round(continousResult.getModelDF()));

		if (Double.isFinite(p1) && !Double.isNaN(p1))
			results[3] = p1;
		else
			results[3] = -9999.0;
		results[4] = logMax;
		results[5] = aic;

		int start = 6;
		if (extraoption != 0)
			results[start++] = extraoption;
		for (int i = start; i < results.length; i++)
			results[i] = continousResult.getParms().get(i - start);

		return results;
	}

	public static ModelAveragingResult calculateToxicRMA(int[] models, double[] Y, double[] doses,
			int bmdType, double BMR, boolean isNCV, boolean useMCMC)
			throws JsonMappingException, JsonProcessingException
	{
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		ModelAveragingResult maResult = new ModelAveragingResult();
		List<StatResult> maModels = new ArrayList<>();

		if (bmdType == 1)
			bmdType = ToxicRConstants.BMD_TYPE_SD;
		else if (bmdType == 2)
			bmdType = ToxicRConstants.BMD_TYPE_REL;

		ToxicRJNI tRJNI = new ToxicRJNI();
		ContinuousResultMA continousResultMA = null;

		if (!useMCMC)
			continousResultMA = tRJNI.runContinuousMA(models, Y, doses, bmdType, BMR, false, isNCV,
					isIncreasing);
		else
			continousResultMA = tRJNI.runContinuousMCMCMA(models, Y, doses, bmdType, BMR, 25000, 1000, false,
					isNCV, isIncreasing).getResult();

		Double maxconstant = doses.length * Math.log((1 / Math.sqrt(2 * Math.PI)));

		for (ContinuousResult continousResult : continousResultMA.getModels())
		{
			int model = continousResult.getModel();
			Double logMax = -1 * continousResult.getMax() - maxconstant;
			double aic = BMDSToxicRUtils.calculateAIC(continousResult.getNparms(), logMax);

			int extraparms = 1;

			if (isNCV)
				extraparms = 2;

			int extraoption = 0;

			// add directionality to exponential models
			// this goes into the results
			if (model == ToxicRConstants.EXP3 || model == ToxicRConstants.EXP5)
				extraoption = (isIncreasing ? 1 : -1);

			// special logic for EXP3
			double[] results = new double[continousResult.getNparms() - extraparms
					+ (extraoption != 0 ? 1 : 0) + (model == ToxicRConstants.EXP3 ? 1 : 0)];

			int start = 0;
			if (extraoption != 0)
				results[start++] = (double) extraoption;
			for (int i = start; i < results.length; i++)
				results[i] = continousResult.getParms().get(i - start);

			Double bmd = getBMD(continousResult.getBmdDist());
			Double bmdl = getBMDL(continousResult.getBmdDist());
			Double bmdu = getBMDU(continousResult.getBmdDist());

			Double fitp = 9999.0;

			StatResult theStatResult = null;
			if (model == ToxicRConstants.EXP3)
			{
				ExponentialResult r = new ExponentialResult();
				r.setOption(3);
				r.setAdverseDirection((short) (isIncreasing ? 1 : -1));
				results[3] = results[4];
				theStatResult = r;
			}
			else if (model == ToxicRConstants.EXP5)
			{
				ExponentialResult r = new ExponentialResult();
				r.setOption(5);
				theStatResult = r;
				r.setAdverseDirection((short) (isIncreasing ? 1 : -1));
				results[3] = Math.pow(Math.E, results[3]);
			}
			else if (model == ToxicRConstants.HILL)
			{
				HillResult r = new HillResult();
				theStatResult = r;
				r.setAdverseDirection((short) (results[1] > 0 ? 1 : -1));

				double tmpr = results[2];
				results[2] = results[3];
				results[3] = tmpr;
			}
			else if (model == ToxicRConstants.POWER)
			{
				PowerResult r = new PowerResult();
				r.setAdverseDirection((short) (results[1] > 0 ? 1 : -1));
				theStatResult = r;
			}
			else if (model == ToxicRConstants.FUNL)
			{
				FunlResult r = new FunlResult();
				r.setAdverseDirection((short) (results[1] > 0 ? 1 : -1));
				theStatResult = r;
			}
			if (theStatResult != null)
			{

				theStatResult.setAIC(aic);
				theStatResult.setBMD(bmd);
				theStatResult.setBMDL(bmdl);
				theStatResult.setBMDU(bmdu);
				theStatResult.setFitLogLikelihood(logMax);
				theStatResult.setFitPValue(fitp);
				maModels.add(theStatResult);
				theStatResult.setCurveParameters(results);
			}

		}

		maResult.setModelResults(maModels);
		List<Double> postProbs = new ArrayList<>();
		postProbs.addAll(continousResultMA.getPostProbs());
		maResult.setPosteriorProbabilities(postProbs);
		maResult.setBMD(getBMD(continousResultMA.getBmdDist()));
		maResult.setBMDL(getBMDL(continousResultMA.getBmdDist()));
		maResult.setBMDU(getBMDU(continousResultMA.getBmdDist()));
		maResult.setFitPValue(9999);
		maResult.setSuccess("true");
		maResult.setAdverseDirection((short) (isIncreasing ? 1 : -1));

		return maResult;
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
