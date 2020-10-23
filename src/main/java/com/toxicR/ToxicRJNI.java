package com.toxicR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toxicR.model.ContinuousMCMCMAResult;
import com.toxicR.model.ContinuousMCMCResult;
import com.toxicR.model.ContinuousResult;
import com.toxicR.model.ContinuousResultMA;

public class ToxicRJNI
{

	static
	{
		System.loadLibrary("DRBMD");
	}

	// Declare a native method sayHello() that receives no arguments and returns void
	public native String runContinuousSingleJNI(int model, boolean suff_stat, double[] Y, double[] doses,
			double[] sd, double[] n_group, double[] prior, int BMD_type, boolean isIncreasing, double BMR,
			double tail_prob, int disttype, double alpha, int samples, int burnin, int parms, int prior_cols);

	public native String runContinuousMAJNI(int nmodels, int[] models, int[] parms, int[] actual_parms,
			int[] prior_cols, int[] disttypes, double[] modelPriors, boolean suff_stat, double[] Y,
			double[] doses, double[] sd, double[] n_group, double[] prior, int BMD_type, boolean isIncreasing,
			double BMR, double tail_prob, double alpha, int samples, int burnin);

	public native String runContinuousMCMCSingleJNI(int model, boolean suff_stat, double[] Y, double[] doses,
			double[] sd, double[] n_group, double[] prior, int BMD_type, boolean isIncreasing, double BMR,
			double tail_prob, int disttype, double alpha, int samples, int burnin, int parms, int prior_cols);

	public native String runContinuousMCMCMAJNI(int nmodels, int[] models, int[] parms, int[] actual_parms,
			int[] prior_cols, int[] disttypes, double[] modelPriors, boolean suff_stat, double[] Y,
			double[] doses, double[] sd, double[] n_group, double[] prior, int BMD_type, boolean isIncreasing,
			double BMR, double tail_prob, double alpha, int samples, int burnin);

	// entry point to run continuous
	public ContinuousResult runContinuous(int model, double[] Y, double[] doses, int bmdType, double BMR,
			boolean isMLE, boolean isLogNormal) throws JsonMappingException, JsonProcessingException
	{
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;

		Priors pr = new Priors(isLogNormal, isMLE);
		double[] sd = new double[10];
		double[] n_group = new double[10];
		String resultString = runContinuousSingleJNI(model, false, Y, doses, sd, n_group, pr.getPriors(model),
				bmdType, isIncreasing, BMR, .001, pr.getDistType(), 0.005, 21000, 1000, pr.getRowCount(model),
				pr.getColCounts(model));

		ContinuousResult result = new ObjectMapper().readValue(fixNonNumerics(resultString),
				ContinuousResult.class);

		return result;
	}

	// entry point to run coninuous model averaging
	public ContinuousResultMA runContinuousMA(int[] models, double[] Y, double[] doses, int bmdType,
			double BMR, boolean isMLE, boolean isLogNormal)
			throws JsonMappingException, JsonProcessingException
	{
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		Priors pr = new Priors(isLogNormal, isMLE);
		int[] disttypes = new int[models.length];
		for (int i = 0; i < models.length; i++)
			disttypes[i] = pr.getDistType();

		int[] parms = pr.getRowCounts(models);
		int[] actualparms = {};

		int[] prior_cols = pr.getColCounts(models);
		double[] priors = pr.getCombined(models);
		double[] modelPriors = {};

		double[] sd = new double[10];
		double[] n_group = new double[10];
		String resultString = runContinuousMAJNI(4, models, parms, actualparms, prior_cols, disttypes,
				modelPriors, false, Y, doses, sd, n_group, priors, bmdType, isIncreasing, BMR, 0.001, 0.005,
				21000, 1000);

		ContinuousResultMA result = new ObjectMapper().readValue(fixNonNumerics(resultString),
				ContinuousResultMA.class);
		return result;
	}

	// entry point to run continuous mcmc
	public ContinuousMCMCResult runContinuousMCMC(int model, double[] Y, double[] doses, int bmdType,
			double BMR, int samples, int burnnin, boolean isMLE, boolean isLogNormal)
			throws JsonMappingException, JsonProcessingException
	{
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		Priors pr = new Priors(isLogNormal, isMLE);
		double[] sd = new double[10];
		double[] n_group = new double[10];
		String resultString = runContinuousMCMCSingleJNI(model, false, Y, doses, sd, n_group,
				pr.getPriors(model), bmdType, isIncreasing, BMR, .001, pr.getDistType(), 0.005, samples,
				burnnin, pr.getRowCount(model), pr.getColCounts(model));
		System.out.println(resultString);
		ContinuousMCMCResult result = new ObjectMapper().readValue(fixNonNumerics(resultString),
				ContinuousMCMCResult.class);

		return result;
	}

	// entry point to run coninuous mcmc model averaging
	public ContinuousMCMCMAResult runContinuousMCMCMA(int[] models, double[] Y, double[] doses, int bmdType,
			double BMR, int samples, int burnnin, boolean isMLE, boolean isLogNormal)
			throws JsonMappingException, JsonProcessingException
	{
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		Priors pr = new Priors(isLogNormal, isMLE);
		int[] disttypes = new int[models.length];
		for (int i = 0; i < models.length; i++)
			disttypes[i] = pr.getDistType();

		int[] parms = pr.getRowCounts(models);
		int[] actualparms = {};

		int[] prior_cols = pr.getColCounts(models);
		double[] priors = pr.getCombined(models);
		double[] modelPriors = {};

		double[] sd = new double[10];
		double[] n_group = new double[10];
		String resultString = runContinuousMCMCMAJNI(models.length, models, parms, actualparms, prior_cols,
				disttypes, modelPriors, false, Y, doses, sd, n_group, priors, bmdType, isIncreasing, BMR,
				0.001, 0.005, samples, burnnin);
		System.out.println(fixNonNumerics(resultString));
		ContinuousMCMCMAResult result = new ObjectMapper().readValue(fixNonNumerics(resultString),
				ContinuousMCMCMAResult.class);
		return result;
	}

	private String fixNonNumerics(String resultString)
	{
		return resultString.replace("-inf", "-9999").replace("inf", "-9999").replace("nan", "-9999");
	}

}
