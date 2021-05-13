package com.toxicR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toxicR.model.ContinuousMCMCMAResult;
import com.toxicR.model.ContinuousMCMCResult;
import com.toxicR.model.ContinuousResult;
import com.toxicR.model.ContinuousResultMA;
import com.toxicR.model.NormalDeviance;

public class ToxicRJNI
{

	static
	{
		System.loadLibrary("DRBMD");
	}

	// Declare a native method sayHello() that receives no arguments and returns void
	
	 private native String calcDeviance(int model, boolean suff_stat, double[] Y, double[] doses,
				double[] sd, double[] n_group, double[] prior, int BMD_type, boolean isIncreasing, double BMR,
				double tail_prob, int disttype, double alpha, int samples, int burnin, int parms, int prior_cols,
				int degree);

	
	public native String runContinuousSingleJNI(int model, boolean suff_stat, double[] Y, double[] doses,
			double[] sd, double[] n_group, double[] prior, int BMD_type, boolean isIncreasing, double BMR,
			double tail_prob, int disttype, double alpha, int samples, int burnin, int parms, int prior_cols,
			int degree, boolean isFast);

	public native String runContinuousMAJNI(int nmodels, int[] models, int[] parms, int[] actual_parms,
			int[] prior_cols, int[] disttypes, double[] modelPriors, boolean suff_stat, double[] Y,
			double[] doses, double[] sd, double[] n_group, double[] prior, int BMD_type, boolean isIncreasing,
			double BMR, double tail_prob, double alpha, int samples, int burnin, boolean isFast);

	public native String runContinuousMCMCSingleJNI(int model, boolean suff_stat, double[] Y, double[] doses,
			double[] sd, double[] n_group, double[] prior, int BMD_type, boolean isIncreasing, double BMR,
			double tail_prob, int disttype, double alpha, int samples, int burnin, int parms, int prior_cols,
			int degree, boolean isFast);

	public native String runContinuousMCMCMAJNI(int nmodels, int[] models, int[] parms, int[] actual_parms,
			int[] prior_cols, int[] disttypes, double[] modelPriors, boolean suff_stat, double[] Y,
			double[] doses, double[] sd, double[] n_group, double[] prior, int BMD_type, boolean isIncreasing,
			double BMR, double tail_prob, double alpha, int samples, int burnin, boolean isFast);

	// entry point to run continuous
	public ContinuousResult runContinuous(int model, double[] Y, double[] doses, int bmdType, double BMR,
			boolean isMLE, boolean isLogNormal, boolean isIncreasing, boolean isFast)
			throws JsonMappingException, JsonProcessingException
	{

		Priors pr = new Priors(isLogNormal, isMLE);
		double[] sd = new double[10];
		double[] n_group = new double[10];
		int modelToRun = getModelToRun(model);
		int colCount = pr.getColCounts(model);
		int rowCount = pr.getRowCount(model);
		double[] priors = pr.getPriors(model);
		int distType = pr.getDistType();

		int degree = getDegree(model);

		String resultString = runContinuousSingleJNI(modelToRun, false, Y, doses, sd, n_group, priors,
				bmdType, isIncreasing, BMR, .001, distType, 0.005, 21000, 1000, rowCount, colCount, degree, isFast);

		ContinuousResult result = new ObjectMapper().readValue(fixNonNumerics(resultString),
				ContinuousResult.class);

		return result;
	}
	
	// entry point to run continuous
		public NormalDeviance calculateDeviance(int model, double[] Y, double[] doses, int bmdType, double BMR,
				boolean isMLE, boolean isLogNormal, boolean isIncreasing)
				throws JsonMappingException, JsonProcessingException
		{

			Priors pr = new Priors(isLogNormal, isMLE);
			double[] sd = new double[10];
			double[] n_group = new double[10];
			int modelToRun = getModelToRun(model);
			int colCount = pr.getColCounts(model);
			int rowCount = pr.getRowCount(model);
			double[] priors = pr.getPriors(model);
			int distType = pr.getDistType();

			int degree = getDegree(model);

			String resultString = calcDeviance(modelToRun, false, Y, doses, sd, n_group, priors,
					bmdType, isIncreasing, BMR, .001, distType, 0.005, 21000, 1000, rowCount, colCount, degree);

			NormalDeviance result = new ObjectMapper().readValue(fixNonNumerics(resultString),
					NormalDeviance.class);

			return result;
		}

	private int getDegree(int model)
	{
		if (model == 6661)
			return 1;
		else if (model == 6662)
			return 2;
		else if (model == 6663)
			return 3;
		else if (model == 6664)
			return 4;
		return 0;
	}

	// entry point to run coninuous model averaging
	public ContinuousResultMA runContinuousMA(int[] models, double[] Y, double[] doses, int bmdType,
			double BMR, boolean isMLE, boolean isLogNormal, boolean isIncreasing, boolean isFast)
			throws JsonMappingException, JsonProcessingException
	{

		// for (int i = 0; i < doses.length; i++)
		// System.out.print(doses[i] + ", ");
		// System.out.println();
		// for (int i = 0; i < Y.length; i++)
		// System.out.print(Y[i] + ", ");
		// System.out.println();

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
		int[] modelsToRun = getModelsToRun(models);
		String resultString = runContinuousMAJNI(modelsToRun.length, modelsToRun, parms, actualparms,
				prior_cols, disttypes, modelPriors, false, Y, doses, sd, n_group, priors, bmdType,
				isIncreasing, BMR, 0.001, 0.005, 25000, 1000,isFast);

		ContinuousResultMA result = new ObjectMapper().readValue(fixNonNumerics(resultString),
				ContinuousResultMA.class);
		return result;
	}

	// entry point to run continuous mcmc
	public ContinuousMCMCResult runContinuousMCMC(int model, double[] Y, double[] doses, int bmdType,
			double BMR, int samples, int burnnin, boolean isMLE, boolean isLogNormal, boolean isIncreasing, boolean isFast)
			throws JsonMappingException, JsonProcessingException
	{
		Priors pr = new Priors(isLogNormal, isMLE);
		double[] sd = new double[10];
		double[] n_group = new double[10];
		int modelToRun = getModelToRun(model);
		int degree = getDegree(model);
		String resultString = runContinuousMCMCSingleJNI(modelToRun, false, Y, doses, sd, n_group,
				pr.getPriors(model), bmdType, isIncreasing, BMR, .001, pr.getDistType(), 0.005, samples,
				burnnin, pr.getRowCount(model), pr.getColCounts(model), degree, isFast);
		ContinuousMCMCResult result = new ObjectMapper().readValue(fixNonNumerics(resultString),
				ContinuousMCMCResult.class);

		return result;
	}

	private int getModelToRun(int model)
	{
		// translate linear/poly2/poly3 to pure poly.
		if (model == 6661 || model == 6662 || model == 6663 || model == 6664)
			return ToxicRConstants.POLY;
		else
			return model;

	}

	// entry point to run coninuous mcmc model averaging
	public ContinuousMCMCMAResult runContinuousMCMCMA(int[] models, double[] Y, double[] doses, int bmdType,
			double BMR, int samples, int burnnin, boolean isMLE, boolean isLogNormal, boolean isIncreasing, boolean isFast)
			throws JsonMappingException, JsonProcessingException
	{
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
		int[] modelsToRun = getModelsToRun(models);
		String resultString = runContinuousMCMCMAJNI(models.length, modelsToRun, parms, actualparms,
				prior_cols, disttypes, modelPriors, false, Y, doses, sd, n_group, priors, bmdType,
				isIncreasing, BMR, 0.001, 0.005, samples, burnnin, isFast);
		ContinuousMCMCMAResult result = new ObjectMapper().readValue(fixNonNumerics(resultString),
				ContinuousMCMCMAResult.class);
		return result;
	}

	private int[] getModelsToRun(int[] models)
	{
		int[] modelsToRun = models.clone();
		// translate linear/poly2/poly3 to pure poly.
		for (int i = 0; i < modelsToRun.length; i++)
			if (modelsToRun[i] == 6661 || modelsToRun[i] == 6662 || modelsToRun[i] == 6663
					|| modelsToRun[i] == 6664)
				modelsToRun[i] = 666;
		return modelsToRun;

	}

	private String fixNonNumerics(String resultString)
	{
		return resultString.replace("-inf", "-9999").replace("inf", "-9999").replace("nan", "-9999");
	}

}
