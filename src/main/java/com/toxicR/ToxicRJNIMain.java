package com.toxicR;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.toxicR.model.ContinuousMCMCMAResult;
import com.toxicR.model.ContinuousMCMCResult;
import com.toxicR.model.ContinuousResult;
import com.toxicR.model.ContinuousResultMA;

public class ToxicRJNIMain
{
	public static void main(String[] args)
	{

		ToxicRJNIMain samp = new ToxicRJNIMain();
		samp.runHillExample();
		// samp.runPowerExample();
		// samp.runExp3Example();
		// samp.runHillMCMCExample();
		// samp.runMAExample();
		// samp.runHillMCMCExample();
		// samp.runMCMCMAExample();
	}

	private void runExp3Example()
	{
		// Constant

		Priors pr = new Priors(false, false);

		double[] doses = { 0, 0, 0, 0, 18, 18, 18, 18, 18, 20, 20, 20, 20, 30, 30, 30, 30, 35, 35, 35, 35, 40,
				40, 40, 40, 40 };
		double[] Y = { 39, 38.4, 36.3, 37.1, 30.2, 35.3, 32.1, 38.3, 35.9, 22.5, 25.2, 20.1, 29.8, 10.1, 13.4,
				11.2, 12.1, 6.1, 6.4, 6.2, 6.2, 3.1, 3.4, 3.3, 2.9, 3.7 };
		double[] sd = new double[10];
		double[] n_group = new double[10];
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		// ************************************ model suff_stat Y doses sd n_group prior BMD_type
		// isIncreasing*****************
		String results = new ToxicRJNI().runContinuousSingleJNI(3, false, Y, doses, sd, n_group,
				pr.getExp3Priors(), 2, false,
				// ************************************* BMR tail_prob disttype alpha samples burnin parms
				// prior_cols ************
				1.1, .001, pr.getDistType(), 0.005, 210, 100, pr.getExp3RowCount(), pr.getExp3ColCount(), 0);
		System.out.println(results);

	}

	private void runHillMCMCExample()
	{

		double[] doses = { 0, 0, 0, 0, 18, 18, 18, 18, 18, 20, 20, 20, 20, 30, 30, 30, 30, 35, 35, 35, 35, 40,
				40, 40, 40, 40 };
		double[] Y = { 39, 38.4, 36.3, 37.1, 40.2, 45.3, 42.1, 38.3, 35.9, 42.5, 45.2, 40.1, 39.8, 50.1, 53.4,
				48.2, 52.1, 56.1, 50.4, 53.2, 55.2, 55.1, 59.1, 56.3, 52.9, 53.7 };
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		ContinuousMCMCResult results = null;
		try
		{
			results = new ToxicRJNI().runContinuousMCMC(6, Y, doses, 2, 1.3, 2100, 1000, true, true,
					isIncreasing);
		}
		catch (JsonProcessingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(results);

	}

	private void runHillExample()
	{

		double[] doses = { 0, 0, 0, 0, 18, 18, 18, 18, 18, 20, 20, 20, 20, 30, 30, 30, 30, 35, 35, 35, 35, 40,
				40, 40, 40, 40 };
		double[] Y = { 39, 38.4, 36.3, 37.1, 40.2, 45.3, 42.1, 38.3, 35.9, 42.5, 45.2, 40.1, 39.8, 50.1, 53.4,
				48.2, 52.1, 56.1, 50.4, 53.2, 55.2, 55.1, 59.1, 56.3, 52.9, 53.7 };

		// sd and n_group are not being used because suff_stat is being set to false
		double[] sd = new double[1];
		double[] n_group = new double[1];
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		ContinuousResult results = null;
		try
		{
			results = new ToxicRJNI().runContinuous(6, Y, doses, 2, 1.3, true, true, isIncreasing);
		}
		catch (JsonProcessingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(results);

	}

	private void runPowerExample()
	{
		Priors pr = new Priors(false, false);

		double[] doses = { 0, 0, 0, 0, 18, 18, 18, 18, 18, 20, 20, 20, 20, 30, 30, 30, 30, 35, 35, 35, 35, 40,
				40, 40, 40, 40 };
		double[] Y = { 39, 38.4, 36.3, 37.1, 40.2, 45.3, 42.1, 38.3, 35.9, 42.5, 45.2, 40.1, 39.8, 50.1, 53.4,
				48.2, 52.1, 56.1, 50.4, 53.2, 55.2, 55.1, 59.1, 56.3, 52.9, 53.7 };

		double[] sd = new double[10];
		double[] n_group = new double[10];
		for (int i = 0; i < 10; i++)
		{
			sd[i] = 1.2 - i;
		}
		for (int i = 0; i < 10; i++)
		{
			n_group[i] = 1.2 / i;
		}
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		// ************************************ model suff_stat Y doses sd n_group prior BMD_type
		// isIncreasing*****************
		String results = new ToxicRJNI().runContinuousSingleJNI(8, false, Y, doses, sd, n_group,
				pr.getPowerPriors(), 2, true,
				// ************************************* BMR tail_prob disttype alpha samples burnin parms
				// prior_cols ************
				1.2, 0.001, pr.getDistType(), 0.005, 21000, 1000, pr.getPowerRowCount(),
				pr.getPowerColCount(), 0);
		System.out.println(results);

	}

	private void runMAExample()
	{

		// models are enum in the c...this is there values. in same order as modelsToRun.
		int[] models = { 6, 8, 3, 5 };

		double[] doses = { 0, 0, 0, 0, 18, 18, 18, 18, 18, 20, 20, 20, 20, 30, 30, 30, 30, 35, 35, 35, 35, 40,
				40, 40, 40, 40 };
		double[] Y = { 39, 38.4, 36.3, 37.1, 40.2, 45.3, 42.1, 38.3, 35.9, 42.5, 45.2, 40.1, 39.8, 50.1, 53.4,
				48.2, 52.1, 56.1, 50.4, 53.2, 55.2, 55.1, 59.1, 56.3, 52.9, 53.7 };
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;
		ContinuousResultMA results = null;
		try
		{
			results = new ToxicRJNI().runContinuousMA(models, Y, doses, 2, 1.3, true, false, isIncreasing);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(results);

	}

	private void runMCMCMAExample()
	{

		// models are enum in the c...this is there values. in same order as modelsToRun.
		int[] models = { 6, 8, 3, 5 };

		double[] doses = { 0, 0, 0, 0, 18, 18, 18, 18, 18, 20, 20, 20, 20, 30, 30, 30, 30, 35, 35, 35, 35, 40,
				40, 40, 40, 40 };
		double[] Y = { 39, 38.4, 36.3, 37.1, 40.2, 45.3, 42.1, 38.3, 35.9, 42.5, 45.2, 40.1, 39.8, 50.1, 53.4,
				48.2, 52.1, 56.1, 50.4, 53.2, 55.2, 55.1, 59.1, 56.3, 52.9, 53.7 };

		double[] sd = new double[10];
		double[] n_group = new double[10];
		for (int i = 0; i < 10; i++)
		{
			sd[i] = 1.2 - i;
		}
		for (int i = 0; i < 10; i++)
		{
			n_group[i] = 1.2 / i;
		}
		boolean isIncreasing = ToxicRUtils.calculateDirection(doses, Y) > 0;

		ContinuousMCMCMAResult results = null;
		try
		{
			results = new ToxicRJNI().runContinuousMCMCMA(models, Y, doses, 2, 1.3, 20000, 1000, false, true,
					isIncreasing);
		}
		catch (JsonProcessingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(results);

	}

}
