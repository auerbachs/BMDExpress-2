package com.toxicR;

public class Priors
{

	double[] power;
	double[] hill;
	double[] exp3;
	double[] exp5;

	boolean isLogNormal = false;
	boolean isMLE = false;
	int distType = 2;

	// intialialize priors
	public Priors(boolean ln, boolean ism)
	{
		isLogNormal = ln;
		ism = isMLE;
		if (isLogNormal)
			distType = 1;

		if (!isLogNormal && !isMLE)
		{
			// normal bayesian
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 1, 0, 100, 2, 0, 0.5, 0, 30, 1,
					0, 1, -20, 20, 2, Math.log(1.5), 0.3, 0, 18, 2, 0, 0.5, 0, 18, 1, 0, 2, -18, 18 }, 6, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 1, 0, 100, 1, 0, 1, -1e4, 1e4,
					2, Math.log(1.5), 0.5, 0, 40, 2, 0, 0.250099980007996, 0, 18, 1, 0, 2, -18, 18 }, 5, 5);
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 1, 0, 1, -100, 100,
					2, 0, 1, 0, 100, 2, 0, 0.3, 0, 100, 2, 0, 0.5, 0, 100, 1, 0, 2, -18, 18 }, 6, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 1, 0, 1, -30, 30, 1,
					0, 1, -20, 20, 2, Math.log(1.5), 0.3, 0, 18, 2, 0, 0.5, 0, 18, 1, 0, 2, -18, 18 }, 6, 5);
		}

		else if (!isLogNormal && isMLE)
		{
			// normal MLE
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 1, 0, 100, 0, 0, 0.5, 0, 30, 0,
					0, 1, -20, 20, 0, Math.log(1.5), 0.3, 0, 18, 0, 0, 0.5, 0, 18, 0, 0, 2, -18, 18 }, 6, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 1, 0, 100, 0, 0, 1, -1e4, 1e4,
					0, Math.log(1.5), 0.5, 0, 40, 0, 0, 0.250099980007996, 0, 18, 0, 0, 2, -18, 18 }, 5, 5);
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, -100, 100,
					0, 0, 1, 0, 100, 0, 0, 0.3, 0, 100, 0, 0, 0.5, 0, 100, 0, 0, 2, -18, 18 }, 6, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, -30, 30, 0,
					0, 1, -20, 20, 0, Math.log(1.5), 0.3, 0, 18, 0, 0, 0.5, 0, 18, 0, 0, 2, -18, 18 }, 6, 5);
		}
		else if (isLogNormal && !isMLE)
		{
			// lognormal bayesian
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 1, 0, 1, -100, 100,
					2, 0, 0.33, 0, 100, 2, 0, 0.33, 0, 18, 1, 0, 2, -18, 18 }, 5, 5);
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 2, 0, 1, 0, 30, 1,
					0, 1, -20, 20, 2, 0, 0.3, 0, 18, 1, 0, 2, -18, 18 }, 5, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 1, 0, 1, -1e4, 1e4,
					2, 0, 0.5, 0, 40, 1, 0, 2, -18, 18 }, 4, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 1, 0, 1, -30, 30, 1,
					0, 2, -20, 20, 2, Math.log(1.5), 0.3, 0, 18, 1, 0, 2, -18, 18 }, 5, 5);
		}
		else if (isLogNormal && isMLE)
		{
			// lognomrla MLE
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, -100, 100,
					0, 0, 0.33, 0, 100, 0, 0, 0.33, 0, 18, 0, 0, 2, -18, 18 }, 5, 5);
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, 0, 30, 0,
					0, 1, -20, 20, 0, 0, 0.3, 0, 18, 0, 0, 2, -18, 18 }, 5, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, -1e4, 1e4,
					0, 0, 0.5, 0, 40, 0, 0, 2, -18, 18 }, 4, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, -30, 30, 0,
					0, 2, -20, 20, 0, Math.log(1.5), 0.3, 0, 18, 0, 0, 2, -18, 18 }, 5, 5);
		}

	}

	double[] getCombined(int[] models)
	{
		int finalLength = 0;
		for (Integer model : models)
		{
			if (model.equals(ToxicRConstants.EXP3))
				finalLength += exp3.length;
			else if (model.equals(ToxicRConstants.POWER))
				finalLength += power.length;
			else if (model.equals(ToxicRConstants.HILL))
				finalLength += hill.length;
			else if (model.equals(ToxicRConstants.EXP5))
				finalLength += exp5.length;
		}
		double[] returnD = new double[finalLength];
		int currIndex = 0;
		for (Integer model : models)
		{
			double[] priors = null;
			if (model.equals(ToxicRConstants.EXP3))
				priors = exp3;
			else if (model.equals(ToxicRConstants.POWER))
				priors = power;
			else if (model.equals(ToxicRConstants.HILL))
				priors = hill;
			else if (model.equals(ToxicRConstants.EXP5))
				priors = exp5;
			if (priors != null)
				for (int i = 0; i < priors.length; i++)
					returnD[currIndex++] = priors[i];
		}
		return returnD;
	}

	int[] getRowCounts(int[] models)
	{
		int[] returnI = new int[models.length];
		int currIndex = 0;

		// log normal priors have 1 less number of rows.
		int diff = 0;
		if (isLogNormal)
			diff = 1;
		for (Integer model : models)
		{
			if (model.equals(ToxicRConstants.EXP3))
				returnI[currIndex++] = getExp3RowCount();
			else if (model.equals(ToxicRConstants.POWER))
				returnI[currIndex++] = getPowerRowCount();
			else if (model.equals(ToxicRConstants.HILL))
				returnI[currIndex++] = getHillRowCount();
			else if (model.equals(ToxicRConstants.EXP5))
				returnI[currIndex++] = getExp5RowCount();
		}
		return returnI;
	}

	int[] getColCounts(int[] models)
	{
		int[] returnI = new int[models.length];
		int currIndex = 0;
		for (int model : models)
			returnI[currIndex++] = 5;
		return returnI;
	}

	public int getHillRowCount()
	{
		return 6 - (isLogNormal ? 1 : 0);
	}

	public int getExp3RowCount()
	{
		return 6 - (isLogNormal ? 1 : 0);
	}

	public int getExp5RowCount()
	{
		return 6 - (isLogNormal ? 1 : 0);
	}

	public int getPowerRowCount()
	{
		return 5 - (isLogNormal ? 1 : 0);
	}

	public int getHillColCount()
	{
		return 5;
	}

	public int getExp3ColCount()
	{
		return 5;
	}

	public int getExp5ColCount()
	{
		return 5;
	}

	public int getPowerColCount()
	{
		return 5;
	}

	public double[] getExp3Priors()
	{
		return exp3;
	}

	public double[] getExp5Priors()
	{
		return exp5;
	}

	public double[] getHillPriors()
	{
		return hill;
	}

	public double[] getPowerPriors()
	{
		return power;
	}

	public int getDistType()
	{
		return distType;
	}

	public double[] getPriors(int model)
	{

		if (model == ToxicRConstants.HILL)
			return getHillPriors();
		else if (model == ToxicRConstants.EXP3)
			return getExp3Priors();
		else if (model == ToxicRConstants.EXP5)
			return getExp5Priors();
		else if (model == ToxicRConstants.POWER)
			return getPowerPriors();
		return null;
	}

	public int getRowCount(int model)
	{

		if (model == ToxicRConstants.HILL)
			return getHillRowCount();
		else if (model == ToxicRConstants.EXP3)
			return getExp3RowCount();
		else if (model == ToxicRConstants.EXP5)
			return getExp5RowCount();
		else if (model == ToxicRConstants.POWER)
			return getPowerRowCount();

		return 0;
	}

	public int getColCounts(int model)
	{
		if (model == ToxicRConstants.HILL)
			return getHillColCount();
		else if (model == ToxicRConstants.EXP3)
			return getExp3ColCount();
		else if (model == ToxicRConstants.EXP5)
			return getExp5ColCount();
		else if (model == ToxicRConstants.POWER)
			return getPowerColCount();

		return 0;
	}

}
