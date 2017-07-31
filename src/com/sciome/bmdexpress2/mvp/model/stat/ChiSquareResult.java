package com.sciome.bmdexpress2.mvp.model.stat;

import java.io.Serializable;

/*
 * After running bmds, all the poly model results are analyzed using ChiSquare.  
 * This class stores those results to associate with the ProbeStatResult.
 */
public class ChiSquareResult implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2243757602571053667L;
	private int					degree1;
	private int					degree2;
	private double				value;
	private double				pValue;

	public double getValue()
	{
		return value;
	}

	public void setValue(double value)
	{
		this.value = value;
	}

	public double getpValue()
	{
		return pValue;
	}

	public void setpValue(double pValue)
	{
		this.pValue = pValue;
	}

	public int getDegree1()
	{
		return degree1;
	}

	public void setDegree1(int degree1)
	{
		this.degree1 = degree1;
	}

	public int getDegree2()
	{
		return degree2;
	}

	public void setDegree2(int degree2)
	{
		this.degree2 = degree2;
	}

}
