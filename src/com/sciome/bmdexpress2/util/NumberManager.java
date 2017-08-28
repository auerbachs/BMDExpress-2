/*
 * NumberManager.java
 *
 * Created January 18, 2007
 * By Longlong Yang, The Hamner Institute for Health Sciences
 *
 * For manipulate numbers (integers, doubles, or their objects)
 */

package com.sciome.bmdexpress2.util;

import java.text.DecimalFormat;

public class NumberManager
{
	public static final String		LONGPATTERN	= "#.###############################################";

	public static final String[]	PATTERNS	= { "#.", "#.#", "#.##", "#.###", "#.####", "#.#####",
			"#.######", "#.#######", "#.########", "#.#########", "#.##########", "#.###########",
			"#.############", "#.#############", "#.##############", "#.###############" };

	private static final double[]	DEFAULTS	= { 0.1, 0.01, 0.001, 0.0001, 0.00001, 0.000001, 0.0000001,
			0.00000001, 0.000000001, 0.0000000001, 0.00000000001, 0.000000000001, 0.0000000000001,
			0.00000000000001, 0.000000000000001, 0.0000000000000001 };

	private static final int		MAX			= 15;

	public NumberManager()
	{
	}

	public static double[] objectDouble(int start, double defaultV, Object[] inputs)
	{
		double[] outputs = new double[inputs.length - start];

		for (int i = 0; i < inputs.length - start; i++)
		{
			try
			{
				outputs[i] = ((Double) inputs[i + start]).doubleValue();
			}
			catch (ClassCastException e)
			{
				outputs[i] = Double.valueOf((String) inputs[i + start]);
			}
			catch (Exception e)
			{
				outputs[i] = defaultV;
			}
		}

		return outputs;
	}

	public static int intValue(Object obj)
	{
		int num = -1;

		try
		{
			if (obj instanceof Integer)
			{
				num = ((Integer) obj).intValue();
			}
			else if (obj instanceof String)
			{
				num = Integer.parseInt((String) obj);
			}
			else if (obj instanceof Double)
			{
				num = ((Double) obj).intValue();
			}
		}
		catch (ClassCastException e)
		{
			System.out.println("intValue(): " + obj);
			e.printStackTrace();
		}

		return num;
	}

	public static double doubleValue(Object obj)
	{
		if (obj != null)
		{
			try
			{
				if (obj instanceof Double)
				{
					return ((Double) obj).doubleValue();
				}
				else if (obj instanceof Integer)
				{// in case of Integer
					return ((Integer) obj).doubleValue();
				}
			}
			catch (ClassCastException e)
			{
				// return below
			}
		}

		return Double.NaN;
	}

	public static int parseInt(String st, int dft)
	{
		try
		{
			return Integer.parseInt(st);
		}
		catch (Exception e)
		{
			return dft;
		}
	}

	public static double parseDouble(String st, double dft)
	{
		try
		{
			return Double.parseDouble(st);
		}
		catch (Exception e)
		{
			return dft;
		}
	}

	public static double[] initDoubles(int n, double initValue)
	{
		double[] array = new double[n];

		for (int i = 0; i < n; i++)
		{
			array[i] = initValue;
		}

		return array;
	}

	public static double precision(int n, double value)
	{
		double base = Math.pow(10, n);
		int num = (int) Math.round(value * base);
		value = num / base;

		return value;
	}

	public static double numberFormat(String pattern, double num)
	{
		DecimalFormat formatter = new DecimalFormat(pattern);
		String s = formatter.format(num);
		return Double.parseDouble(s);
	}

	/**
	 * Round a double value to defined decimals.
	 *
	 * In case the absolute value of a double < 0 then keep at least decimals no-zero digits
	 *
	 */
	public static double numberFormat(int decimals, double num)
	{
		String pattern;

		if (Math.abs(num) < DEFAULTS[0])
		{
			decimals += redicimal(0, Math.abs(num));
		}

		if (decimals >= MAX)
		{
			pattern = createPattern(decimals);
		}
		else
		{
			pattern = PATTERNS[decimals];
		}

		return numberFormat(pattern, num);
	}

	public static Double toDoubleObject(double value)
	{
		if (Double.compare(value, Double.NaN) == 0)
		{
			return null;
		}

		return Double.valueOf(value);
	}

	public static double negLog10(double v)
	{
		if (v == 0.0)
			v = 0.000000000000000000001;

		return -Math.log10(v);
	}

	private static int redicimal(int decimals, double smaller)
	{
		double ten = 10;
		double value = 1 / Math.pow(ten, (double) decimals);

		while (smaller < value)
		{
			decimals += 1;
			value = 1 / Math.pow(ten, (double) decimals);
		}

		return decimals - 1;
	}

	private static String createPattern(int decimals)
	{
		StringBuffer bf = new StringBuffer(PATTERNS[MAX - 1]);

		for (int i = MAX; i < decimals; i++)
		{
			bf.append("#");
		}

		return bf.toString();
	}

	public static void main(String[] ARGV)
	{
		double num = 1234.555555555555;
		double fNum = NumberManager.numberFormat("#.####", num);
		num = 0.00000555555567898765;
		fNum = NumberManager.numberFormat(10, num);
		System.out.println(num + ": " + fNum);
	}
}
