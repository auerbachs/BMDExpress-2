package com.sciome.bmdexpress2.mvp.model;

/*
 * this class will store the key (aka column header) for values
 * that can be charted. 
 * it can be modified by a math string that will
 * return a mathematical transformation of the value that is 
 * retrievable by getValue(value)
 */
public class ChartKey
{

	public final static String	NEGLOG	= "Negative Log";
	public final static String	LOG		= "Log";
	public final static String	SQRT	= "Square Root";
	public final static String	ABS		= "Absolute Value";

	String						key;
	String						math;

	public ChartKey(String key, String math)
	{
		super();
		this.key = key;
		this.math = math;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public String getMath()
	{
		return math;
	}

	public void setMath(String math)
	{
		this.math = math;
	}

	public Double getValue(Double value)
	{
		if (math == null || math.equals(""))
			return value;
		else if (math.equals(NEGLOG) && value > 0)
			return -Math.log10(value);
		else if (math.equals(LOG) && value > 0)
			return Math.log10(value);
		else if (math.equals(ABS) && value > 0)
			return Math.abs(value);
		else if (math.equals(SQRT) && value > 0)
			return Math.sqrt(value);

		return null;
	}

	@Override
	public String toString()
	{
		return math + " of " + key;
	}

}
