package com.toxicR;

public class PriorsMA
{

	double[] power;
	double[] hill;
	double[] exp3;
	double[] exp5;
	double[] funl;

	boolean isNCV = false;
	int distType = 1;
	private final int LNORM = 2;
	private final int NORM = 1;

	// intialialize priors
	public PriorsMA(boolean ln, double variance)
	{
		this(ln, 1, variance);

	}

	public PriorsMA(boolean ln, double powerrestrict, double logVariance)
	{

		isNCV = ln;
		if (isNCV)
			distType = 2;

		// hill k is 3rd
		// hill n is 4th
		// power power is 3rd

		// lnormprior is 2
		// normprior is 1

		if (isNCV)
		{
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(NORM), 0, 1, -100, 100, // 1
					(LNORM), 0, 6.9, 0, 10000, // 2
					(NORM), 0, 1, -20, 20, // 3
					(LNORM), Math.log(1.6), 0.4214036, 0, 18, // 4
					(LNORM), 0, 0.5, 0, 18, // 5
					(NORM), logVariance, 1, -30, 30// 6
			}, 6, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(NORM), 0, 1, -100, 100, // 1
					(NORM), 0, 10, -1e4, 1e4, // 2
					(LNORM), (Math.log(1.6)), 0.4214036, (0), 40, // 3
					(LNORM), 0, 0.5, 0, 18, // 4
					(NORM), logVariance, 1, -18, 18 // 5
			}, 5, 5);
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(NORM), 1, 1, -100, 100, // 1
					(NORM), 0, 1000, -10000, 10000, // 2
					(LNORM), 0, 2, 0, 100, // 3
					(LNORM), (Math.log(1.6)), 0.4214036, (0), 18, // 4
					(LNORM), 0, 1, 0, 100, // 5
					(NORM), logVariance, 1, -18, 18// 6
			}, 6, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(LNORM), 0, 1, 0, 100, // 1
					(NORM), 0, 1000, -10000, 10000, // 2
					(NORM), 0, 1, -100, 100, // 3
					(LNORM), (Math.log(1.6)), 0.4214036, (0), 18, // 4
					(LNORM), 0, 0.75, 0, 18, // 5
					(NORM), logVariance, 1, -18, 18// 6
			}, 6, 5);

			funl = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(NORM), 0, 10, -100, 100, // 1
					(NORM), 0, 10, -1e4, 1e4, // 2
					(LNORM), 0, 0.5, 0, 100, // 3
					(NORM), 0.5, 1, 0, 100, // 4
					(LNORM), 0, 0.5, 0, 100, // 5
					(NORM), 0, 10, -200, 200, // 6
					(LNORM), 0, 0.75, 0, 18, // 7
					(NORM), logVariance, 2, -18, 18// 8
			}, 8, 5);

		}

		else if (!isNCV)
		{
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(NORM), 1, 1, -100, 100, // 1
					(NORM), 0, 1000, -10000, 10000, // 2
					(LNORM), 0, 2, 0, 100, // 3
					(LNORM), (Math.log(1.6)), .4214036, 0, 18, // 4
					(NORM), logVariance, 1, -30, 30 // 5
			}, 5, 5);
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(NORM), 0, 1, -100, 100, // 1
					(LNORM), 0, 6.9, 0, 10000, // 2
					(NORM), 0, 1, -20, 20, // 3
					(LNORM), Math.log(1.6), 0.4214036, 0, 18, // 4
					(NORM), logVariance, 2, -18, 18// 5
			}, 5, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(NORM), 0, 1, -100, 100, // 1
					(NORM), 0, 1, -100, 100, // 2
					(LNORM), (Math.log(1.6)), 0.4214036, (0), 40, // 3
					(NORM), logVariance, 1, -18, 18// 4
			}, 4, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(LNORM), 0, 1, 0, 100, // 1
					(NORM), 0, 1000, -10000, 10000, // 2
					(NORM), 0, 1, -100, 100, // 3
					(LNORM), (Math.log(1.6)), 0.4214036, (0), 18, // 4
					(NORM), logVariance, 1, -18, 18// 5
			}, 5, 5);

			funl = ToxicRUtils.convert2ColumnMajorOrder(new double[] { // priors
					(NORM), 0, 10, -100, 100, // 1
					(NORM), 0, 10, -1e4, 1e4, // 2
					(LNORM), 0, 0.5, 0, 100, // 3
					(NORM), 0.5, 1, 0, 100, // 4
					(LNORM), 0, 0.5, 0, 100, // 5
					(NORM), 0, 10, -200, 200, // 6
					(NORM), logVariance, 2, -18, 18// 7
			}, 7, 5);

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
			else if (model.equals(ToxicRConstants.FUNL))
				finalLength += funl.length;
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
			else if (model.equals(ToxicRConstants.FUNL))
				priors = funl;
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
		if (!isNCV)
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
			else if (model.equals(ToxicRConstants.FUNL))
				returnI[currIndex++] = getFunlRowCount();
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
		return 6 - (!isNCV ? 1 : 0);
	}

	public int getExp3RowCount()
	{
		return 6 - (!isNCV ? 1 : 0);
	}

	public int getExp5RowCount()
	{
		return 6 - (!isNCV ? 1 : 0);
	}

	public int getPowerRowCount()
	{
		return 5 - (!isNCV ? 1 : 0);
	}

	public int getFunlRowCount()
	{
		return 8 - (!isNCV ? 1 : 0);
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

	public int getFunlColCount()
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

	public double[] getFunlPriors()
	{
		return funl;
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

		else if (model == ToxicRConstants.FUNL)
			return getFunlPriors();
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

		else if (model == ToxicRConstants.FUNL)
			return getFunlRowCount();

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
		else if (model == ToxicRConstants.FUNL)
			return getFunlColCount();

		return 0;
	}

}
