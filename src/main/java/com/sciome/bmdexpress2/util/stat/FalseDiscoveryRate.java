/**
 * FalseDiscoveryRate.java
 */

package com.sciome.bmdexpress2.util.stat;

public class FalseDiscoveryRate
{
	private int			N;
	private int[]		indices;
	private double[]	pValues;

	public FalseDiscoveryRate(int max)
	{
		indices = new int[max];
		pValues = new double[max];
		N = 0;
	}

	public FalseDiscoveryRate(double[] ps)
	{
		indices = new int[ps.length];
		pValues = new double[ps.length];
		N = 0;

		for (int i = 0; i < ps.length; i++)
		{
			addPValues(i, ps[i]);
		}
	}

	public FalseDiscoveryRate(int max, double[] ps)
	{
		indices = new int[max];
		pValues = new double[max];
		N = 0;

		for (int i = 0; i < max; i++)
		{
			addPValues(i, ps[i]);
		}
	}

	/*
	 * add/insert p-value at position k in decending order starting from k = 0
	 *
	 * @param k is the original index or position of the p-value
	 * 
	 * @param p is a p-value.
	 * 
	 * @return no
	 */
	public void addPValues(int k, double p)
	{
		for (int i = k; i >= 0; i--)
		{
			if (i == 0 || p < pValues[i - 1])
			{
				pValues[i] = p;
				indices[i] = k;
				break;
			}
			else
			{
				pValues[i] = pValues[i - 1];
				indices[i] = indices[i - 1];
			}
		}

		N += 1;
	}

	/*
	 * calculate false discovery rate for the sorted p value return the the results in its original order.
	 *
	 */
	public double[] falseDRate()
	{
		double[] fdr = new double[N];

		for (int i = 0; i < N; i++)
		{
			fdr[indices[i]] = pValues[i] * N / (i + 1);
		}

		return fdr;
	}

	public double[] falseDiscoveryRate()
	{
		double[] fdr = new double[N];

		for (int i = 0; i < N; i++)
		{
			if (i == 0)
			{
				fdr[indices[i]] = pValues[i];
			}
			else
			{
				double pi = pValues[i] * N / (N - i);
				fdr[indices[i]] = (fdr[indices[i - 1]] < pi) ? fdr[indices[i - 1]] : pi;
			}
		}

		return fdr;
	}

}
