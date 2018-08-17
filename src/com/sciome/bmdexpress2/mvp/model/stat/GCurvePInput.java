package com.sciome.bmdexpress2.mvp.model.stat;

import com.sciome.bmdexpress2.util.bmds.shared.BMRFactor;

public class GCurvePInput
{
	private int			bootStrapIterations			= 1000;
	private float		pValueConfidenceInterval	= 0.05f;
	private BMRFactor	bmrFactor;

	public GCurvePInput()
	{
		bmrFactor = new BMRFactor("1.021 (5%)", "1.021");

	}

	public int getBootStrapIterations()
	{
		return bootStrapIterations;
	}

	public void setBootStrapIterations(int bootStrapIterations)
	{
		this.bootStrapIterations = bootStrapIterations;
	}

	public float getpValueConfidenceInterval()
	{
		return pValueConfidenceInterval;
	}

	public void setpValueConfidenceInterval(float pValueConfidenceInterval)
	{
		this.pValueConfidenceInterval = pValueConfidenceInterval;
	}

	public BMRFactor getBmrFactor()
	{
		return bmrFactor;
	}

	public void setBmrFactor(BMRFactor bmfFactor)
	{
		this.bmrFactor = bmfFactor;
	}

}
