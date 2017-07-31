/*
 *  LinearCorrelation.java
 *
 *  Created by Longlong Yang on July 2, 2007
 *  Modified on September 27, 2007
 *  CopyRight at The Hamner Institute for Health Sciences
 */

package com.sciome.bmdexpress2.util.stat;

public class LinearCorrelation
{
	private double Sx, Sxx, Sy, Syy, Sxy, r;
	private int n;

	public LinearCorrelation()
	{
	}

	public LinearCorrelation(double[] x, double[] y)
	{
		computeCorrelation(x, y);
	}

	public void computeCorrelation(double[] x, double[] y)
	{
		Sx = Sxx = Sy = Syy = Sxy = r = 0;
		n = x.length;

		if (n > 0)
		{
			for (int i = 0; i < n; i++)
			{
				Sx += x[i];
				Sxx += x[i] * x[i];
				Sy += y[i];
				Syy += y[i] * y[i];
				Sxy += x[i] * y[i];
			}

			// by the off chance that the denominator is 0, let's deal with it here.
			double denominator = Math.sqrt((Sxx - Sx * Sx / n) * (Syy - Sy * Sy / n));
			// if (denominator == 0.0)
			// r = 0.0;
			// else
			r = (Sxy - (Sx * Sy / n)) / denominator;

			// if square root value above is negative, it causes -Infinity.
			// let's deal with here by reassigning to zero
			// if (Double.isInfinite(r))
			// r = 0.0;
		}
	}

	public double correlation()
	{
		return r;
	}

	public static double correlation(double[] x, double[] y)
	{
		LinearCorrelation lc = new LinearCorrelation(x, y);
		return lc.correlation();
	}
}
