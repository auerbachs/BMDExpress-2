package com.toxicR;

public class Priors
{

	double[] power;
	double[] hill;
	double[] exp3;
	double[] exp5;
	double[] linear;
	double[] poly2;
	double[] poly3;
	double[] poly4;
	double[] funl;

	boolean isNCV = false;
	boolean isMLE = false;
	int distType = 1;

	// intialialize priors
	public Priors(boolean ln, boolean ism)
	{
		isNCV = ln;
		isMLE = ism;
		if (isNCV)
			distType = 2;

		if (isNCV && !isMLE)
		{
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 1, 0, 100, 2, 0, 0.5, 0, 30, 1,
					0, 1, -20, 20, 2, Math.log(1.5), 0.3, 0, 18, 2, 0, 0.5, 0, 18, 1, 0, 2, -18, 18 }, 6, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 1, 0, 100, 1, 0, 1, -1e4, 1e4,
					2, Math.log(1.5), 0.5, 0, 40, 2, 0, 0.250099980007996, 0, 18, 1, 0, 2, -18, 18 }, 5, 5);
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 1, 0, 5, -100, 100, 1, 1, 5, -100, 100,
					2, 0, 1, 0, 100, 2, 0, 0.3, 0, 100, 2, 0, 0.5, 0, 100, 1, 0, 2, -18, 18 }, 6, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 1, 0, 1, -30, 30, 1,
					0, 1, -20, 20, 2, Math.log(1.5), 0.3, 0, 18, 2, 0, 0.5, 0, 18, 1, 0, 2, -18, 18 }, 6, 5);
			linear = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 1, 0, 5, -100, 100, 1, 0, 5, -100,
					100, 2, 0, 1, 0, 100, 1, 0, 1, -18, 18 }, 4, 5);
			poly2 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 1, 0, 5, -100, 100, 1, 0, 5, -100,
					100, 1, 0, 5, -100, 100, 2, 0, 1, 0, 100, 1, 0, 1, -18, 18 }, 5, 5);
			poly3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 1, 0, 5, -100, 100, 1, 0, 5, -100,
					100, 1, 0, 5, -100, 100, 1, 0, 5, -100, 100, 2, 0, 1, 0, 100, 1, 0, 1, -18, 18 }, 6, 5);
			poly4 = ToxicRUtils
					.convert2ColumnMajorOrder(
							new double[] { 1, 0, 5, -100, -100, 1, 0, 5, -100, 100, 1, 0, 5, -100, 100, 1, 0,
									5, -100, 100, 1, 0, 5, -100, 100, 2, 0, 1, 0, 100, 1, 0, 1, -18, 18 },
							7, 5);

			funl = ToxicRUtils.convert2ColumnMajorOrder(
					new double[] { 1, 0, 5, -100, 100, 1, 0, 5, -100, 100, 2, 0, 0.5, 0, 100, 2, 0, 1, 0, 100,
							2, 0, 0.5, 0, 100, 2, 0, 1, 0, 100, 2, 0, 0.5, 0, 18, 1, 0, 1, -18, 18 },
					8, 5);
		}

		else if (isNCV && isMLE)
		{
			// normal MLE
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 1, 0, 100, 0, 0, 0.5, 0, 30, 0,
					0, 1, -20, 20, 0, Math.log(1.5), 0.3, 0, 18, 0, 0, 0.5, 0, 18, 0, 0, 2, -18, 18 }, 6, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 1, 0, 100, 0, 0, 1, -1e4, 1e4,
					0, Math.log(1.5), 0.5, 0, 40, 0, 0, 0.250099980007996, 0, 18, 0, 0, 2, -18, 18 }, 5, 5);
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 5, -100, 100, 0, 1, 5, -100, 100,
					0, 0, 1, 0, 100, 0, 0, 0.3, 0, 100, 0, 0, 0.5, 0, 100, 0, 0, 2, -18, 18 }, 6, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, -30, 30, 0,
					0, 1, -20, 20, 0, Math.log(1.5), 0.3, 0, 18, 0, 0, 0.5, 0, 18, 0, 0, 2, -18, 18 }, 6, 5);
			linear = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100,
					100, 0, 0, 1, 0, 100, 0, 0, 1, -18, 18 }, 4, 5);
			poly2 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100,
					100, 0, 0, 5, -100, 100, 0, 0, 1, 0, 100, 0, 0, 1, -18, 18 }, 5, 5);
			poly3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100,
					100, 0, 0, 5, -100, 100, 0, 0, 5, -100, 100, 0, 0, 1, 0, 100, 0, 0, 1, -18, 18 }, 6, 5);
			poly4 = ToxicRUtils
					.convert2ColumnMajorOrder(
							new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100, 100, 0, 0, 5, -100, 100, 0, 0,
									5, -100, 100, 0, 0, 5, -100, 100, 0, 0, 1, 0, 100, 0, 0, 1, -18, 18 },
							7, 5);

			funl = ToxicRUtils.convert2ColumnMajorOrder(
					new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100, 100, 0, 0, 0.5, 0, 100, 0, 0, 1, 0, 100,
							0, 0, 0.5, 0, 100, 0, 0, 1, 0, 100, 0, 0, 0.5, 0, 18, 0, 0, 1, -18, 18 },
					8, 5);

		}
		else if (!isNCV && !isMLE)
		{
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 2, 0, 1, 0, 30, 1,
					0, 1, -20, 20, 2, 0, 0.3, 0, 18, 1, 0, 2, -18, 18 }, 5, 5);
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 2, 0, 1, 0, 30, 1,
					0, 1, -20, 20, 2, 0, 0.3, 0, 18, 1, 0, 2, -18, 18 }, 5, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 1, 0, 100, 0, 0, 1, -1e4, 1e4,
					0, Math.log(1.5), 0.5, 0, 40, 0, 0, 0.250099980007996, 0, 18, 0, 0, 2, -18, 18 }, 5, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 2, 0, 0.1, 0, 100, 1, 0, 1, -30, 30, 1,
					0, 2, -20, 20, 2, Math.log(1.5), 0.3, 0, 18, 1, 0, 2, -18, 18 }, 5, 5);

			linear = ToxicRUtils.convert2ColumnMajorOrder(
					new double[] { 1, 0, 5, -100, 100, 1, 0, 5, -100, 100, 1, 0, 1, -18, 18 }, 3, 5);
			poly2 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 1, 0, 5, -100, 100, 1, 0, 5, -100,
					100, 1, 0, 5, -100, 100, 1, 0, 1, -18, 18 }, 4, 5);
			poly3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 1, 0, 5, -100, 100, 1, 0, 5, -100,
					100, 1, 0, 5, -100, 100, 1, 0, 5, -100, 100, 1, 0, 1, -18, 18 }, 5, 5);
			poly4 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 1, 0, 5, -100, -100, 1, 0, 5, -100,
					100, 1, 0, 5, -100, 100, 1, 0, 5, -100, 100, 1, 0, 5, -100, 100, 1, 0, 1, -18, 18 }, 6,
					5);

			funl = ToxicRUtils
					.convert2ColumnMajorOrder(
							new double[] { 1, 0, 5, -100, 100, 1, 0, 5, -100, 100, 2, 0, 0.5, 0, 100, 2, 0,
									0.5, 0, 100, 2, 0, 0.5, 0, 100, 2, 0, 1, 0, 100, 1, 0, 1, -18, 18 },
							7, 5);

		}
		else if (!isNCV && isMLE)
		{
			hill = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, 0, 30, 0,
					0, 1, -20, 20, 0, 0, 0.3, 0, 18, 0, 0, 2, -18, 18 }, 5, 5);
			exp3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, 0, 30, 0,
					0, 1, -20, 20, 0, 0, 0.3, 0, 18, 0, 0, 2, -18, 18 }, 5, 5);
			power = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, -1e2, 1e2,
					0, 0, 0.3, 0, 18, 0, 0, 2, -18, 18 }, 4, 5);
			exp5 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 0.1, 0, 100, 0, 0, 1, -30, 30, 0,
					0, 2, -20, 20, 0, Math.log(1.5), 0.3, 0, 18, 0, 0, 2, -18, 18 }, 5, 5);

			linear = ToxicRUtils.convert2ColumnMajorOrder(
					new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100, 100, 0, 0, 1, -18, 18 }, 3, 5);
			poly2 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100,
					100, 0, 0, 5, -100, 100, 0, 0, 1, -18, 18 }, 4, 5);
			poly3 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100,
					100, 0, 0, 5, -100, 100, 0, 0, 5, -100, 100, 0, 0, 1, -18, 18 }, 5, 5);
			poly4 = ToxicRUtils.convert2ColumnMajorOrder(new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100,
					100, 0, 0, 5, -100, 100, 0, 0, 5, -100, 100, 0, 0, 5, -100, 100, 0, 0, 1, -18, 18 }, 6,
					5);

			funl = ToxicRUtils
					.convert2ColumnMajorOrder(
							new double[] { 0, 0, 5, -100, 100, 0, 0, 5, -100, 100, 0, 0, 0.5, 0, 100, 0, 0,
									0.5, 0, 100, 0, 0, 0.5, 0, 100, 0, 0, 1, 0, 100, 0, 0, 1, -18, 18 },
							7, 5);

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
			else if (model.equals(ToxicRConstants.LINEAR))
				finalLength += linear.length;
			else if (model.equals(ToxicRConstants.POLY2))
				finalLength += poly2.length;
			else if (model.equals(ToxicRConstants.POLY3))
				finalLength += poly3.length;
			else if (model.equals(ToxicRConstants.POLY4))
				finalLength += poly4.length;
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
			else if (model.equals(ToxicRConstants.LINEAR))
				priors = linear;
			else if (model.equals(ToxicRConstants.POLY2))
				priors = poly2;
			else if (model.equals(ToxicRConstants.POLY3))
				priors = poly3;
			else if (model.equals(ToxicRConstants.POLY4))
				priors = poly4;
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
			else if (model.equals(ToxicRConstants.LINEAR))
				returnI[currIndex++] = getLinearRowCount();
			else if (model.equals(ToxicRConstants.POLY2))
				returnI[currIndex++] = getPoly2RowCount();
			else if (model.equals(ToxicRConstants.POLY3))
				returnI[currIndex++] = getPoly3RowCount();
			else if (model.equals(ToxicRConstants.POLY4))
				returnI[currIndex++] = getPoly4RowCount();
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

	public int getLinearRowCount()
	{
		return 4 - (!isNCV ? 1 : 0);
	}

	public int getPoly2RowCount()
	{
		return 5 - (!isNCV ? 1 : 0);
	}

	public int getPoly3RowCount()
	{
		return 6 - (!isNCV ? 1 : 0);
	}

	public int getPoly4RowCount()
	{
		return 7 - (!isNCV ? 1 : 0);
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

	public int getLinearColCount()
	{
		return 5;
	}

	public int getPoly2ColCount()
	{
		return 5;
	}

	public int getPoly3ColCount()
	{
		return 5;
	}

	public int getPoly4ColCount()
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

	public double[] getLinearPriors()
	{
		return linear;
	}

	public double[] getPoly2Priors()
	{
		return poly2;
	}

	public double[] getPoly3Priors()
	{
		return poly3;
	}

	public double[] getPoly4Priors()
	{
		return poly4;
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
		else if (model == ToxicRConstants.LINEAR)
			return getLinearPriors();
		else if (model == ToxicRConstants.POLY2)
			return getPoly2Priors();
		else if (model == ToxicRConstants.POLY3)
			return getPoly3Priors();
		else if (model == ToxicRConstants.POLY4)
			return getPoly4Priors();
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
		else if (model == ToxicRConstants.LINEAR)
			return getLinearRowCount();
		else if (model == ToxicRConstants.POLY2)
			return getPoly2RowCount();
		else if (model == ToxicRConstants.POLY3)
			return getPoly3RowCount();
		else if (model == ToxicRConstants.POLY4)
			return getPoly4RowCount();
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
		else if (model == ToxicRConstants.LINEAR)
			return getLinearColCount();
		else if (model == ToxicRConstants.POLY2)
			return getPoly2ColCount();
		else if (model == ToxicRConstants.POLY3)
			return getPoly3ColCount();
		else if (model == ToxicRConstants.POLY4)
			return getPoly4ColCount();
		else if (model == ToxicRConstants.FUNL)
			return getFunlColCount();

		return 0;
	}

}
