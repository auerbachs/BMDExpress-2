/**
 * OnewayAnova.java
 */

package com.sciome.bmdexpress2.util.prefilter;

import java.util.Vector;

import com.sciome.bmdexpress2.util.stat.FDist;
import com.sciome.bmdexpress2.util.stat.StudentsT05;

public class OnewayAnova
{
	private int[][]			indices;
	private int[]			uniOrderXs, Ni;
	private int				N, groups, minReplicate, dft, dfe;
	private double			C, SStotal, SStreat, SSerror, F, pValue, mean;
	private double[][]		estimates;
	private Vector<Double>	vecXs;

	private final double	neg			= -1000000;
	private String[]		STATISTICS	= { "DOSE", "NI", "MEAN", "STD", "STR", "CIL", "CIR" };

	/*
	 * Class Constructor
	 */
	public OnewayAnova()
	{
		indices = null;
		groups = 0;

	}

	public OnewayAnova(int start, double[] xx, double[] yy)
	{
		this();
		setVariablesXX(start, xx);
		onewayANOVA(yy);
	}

	public void setVariablesXX(int start, double[] xx)
	{
		indices = new int[xx.length - start][xx.length - start];
		vecXs = new Vector<Double>();

		for (int i = start; i < xx.length; i++)
		{
			Double varX = new Double(xx[i]);
			int row = vecXs.indexOf(varX);

			if (row < 0)
			{
				vecXs.add(varX);
				row = vecXs.indexOf(varX);
				indices[row][0] = 0;
			}

			indices[row][0] += 1;
			indices[row][indices[row][0]] = i;
		}

		orderXs();
	}

	private void orderXs()
	{
		groups = vecXs.size();

		if (groups > 0)
		{
			uniOrderXs = new int[groups];
			double[] values = new double[groups];
			estimates = new double[groups][STATISTICS.length];

			for (int i = 0; i < groups; i++)
			{
				double x = vecXs.get(i).doubleValue();

				for (int j = i; j >= 0; j--)
				{
					if (j == 0 || values[j - 1] <= x)
					{
						uniOrderXs[j] = i;
						values[j] = x;
						estimates[j][0] = x;
						break;
					}
					else
					{
						values[j] = values[j - 1];
						uniOrderXs[j] = uniOrderXs[j - 1];
						estimates[j][0] = estimates[j - 1][0];
					}
				}
			}
		}
	}

	public void onewayANOVA(double[] yy)
	{
		if (indices != null)
		{
			double[][] sums = new double[groups][3];
			double SY2 = 0;
			double SY = 0;
			double SY2s = 0;
			int t = 0;
			N = 0;
			Ni = new int[groups];
			dft = dfe = 0;
			F = pValue = 0;

			for (int g = 0; g < groups; g++)
			{
				sums[g][0] = sums[g][1] = sums[g][2] = 0;
				int i = uniOrderXs[g];
				Ni[g] = 0;
				// int r = 0;

				for (int j = 1; j <= indices[i][0]; j++)
				{
					int idx = indices[i][j];

					try
					{
						sums[i][0] += yy[idx];
						sums[i][1] += yy[idx] * yy[idx];
						sums[i][2] += 1;
						Ni[g] += 1;
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}

				if (Ni[g] > 1)
				{
					SY += sums[i][0];
					SY2 += sums[i][1];
					double sSY2 = sums[i][0] * sums[i][0] / Ni[g];
					SY2s += sSY2;
					t += 1;
					N += Ni[g];
					double t05 = StudentsT05.t05Value(Ni[g]);

					estimates[g][1] = sums[i][2];
					estimates[g][2] = sums[i][0] / sums[i][2];
					estimates[g][3] = Math.sqrt((sums[i][1] - sSY2) / (Ni[g] - 1));
					estimates[g][4] = estimates[g][3] / Math.sqrt(Ni[g]);
					estimates[g][5] = estimates[g][2] - t05 * estimates[g][4];
					estimates[g][6] = estimates[g][2] + t05 * estimates[g][4];
				}
			}

			mean = SY / N;
			C = SY * SY / N;
			SStotal = SY2 - C;
			SStreat = SY2s - C;
			SSerror = SStotal - SStreat;
			dft = t - 1;
			dfe = N - t;
			double MSt = SStreat / dft;
			double MSe = SSerror / dfe;
			F = MSt / MSe;
			pValue = FDist.probabilityOf(F, dft, dfe);
		}
	}

	private int primeN()
	{
		return N;
	}

	public int groupXs()
	{
		return groups;
	}

	public int minmumReplicate()
	{
		minReplicate = indices[0][0];
		for (int i = 1; i < groups; i++)
		{
			if (indices[i][0] < minReplicate)
			{
				minReplicate = indices[i][0];
			}
		}

		return minReplicate;
	}

	public int dfTreatment()
	{
		return dft;
	}

	public int dfError()
	{
		return dfe;
	}

	public double mean()
	{
		return mean;
	}

	public double fValue()
	{
		return F;
	}

	public double pValue()
	{
		return pValue;
	}

	public double[][] estimates()
	{
		return estimates;
	}

	public int[] doseCounts()
	{
		return Ni;
	}

	public double[] estimatesAtColumn(int col)
	{
		double[] values = new double[groups];

		for (int i = 0; i < groups; i++)
		{
			values[i] = estimates[i][col];
		}

		return values;
	}

}
