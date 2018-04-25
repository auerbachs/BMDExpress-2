package com.sciome.bmdexpress2.mvp.model;

import com.sciome.bmdexpress2.util.NumberManager;

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
		else if (math.equals(NEGLOG))
			return NumberManager.negLog10(value);
		else if (math.equals(LOG) && value > 0)
			return NumberManager.log10(value);
		else if (math.equals(ABS))
			return Math.abs(value);
		else if (math.equals(SQRT) && value > 0)
			return Math.sqrt(value);

		return null;
	}

	@Override
	public String toString()
	{
		if (math == null)
			return key;
		return math + " of " + key;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((math == null) ? 0 : math.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChartKey other = (ChartKey) obj;
		if (key == null)
		{
			if (other.key != null)
				return false;
		}
		else if (!key.equals(other.key))
			return false;
		if (math == null)
		{
			if (other.math != null)
				return false;
		}
		else if (!math.equals(other.math))
			return false;
		return true;
	}

}
