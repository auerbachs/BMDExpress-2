package com.sciome.bmdexpress2.mvp.model.stat;

import com.sciome.bmdexpress2.util.bmds.shared.BMRFactor;

public class BMDMAInput
{
	private boolean exp3;
	private boolean exp5;
	private boolean funl;
	private boolean hill;
	private boolean power;
	private boolean constantVariance;
	private boolean flagHillModel;
	private boolean useWald;
	private BMRFactor BMRFactor;
	private String bmrType;
	private int numThreads;
	private boolean laplace;

	public BMDMAInput()
	{
		exp3 = true;
		exp5 = true;
		hill = true;
		power = true;
		constantVariance = true;
		flagHillModel = true;
		laplace = true;
		numThreads = 1;
		bmrType = "Standard Deviation";
		useWald = false;

		BMRFactor = new BMRFactor("1.021 (5%)", "1.021");
	}

	public boolean isLaplace()
	{
		return laplace;
	}

	public void setLaplace(boolean laplace)
	{
		this.laplace = laplace;
	}

	public boolean isUseWald()
	{
		return useWald;
	}

	public void setUseWald(boolean useWald)
	{
		this.useWald = useWald;
	}

	public boolean isFunl()
	{
		return funl;
	}

	public void setFunl(boolean funl)
	{
		this.funl = funl;
	}

	public boolean isExp3()
	{
		return exp3;
	}

	public void setExp3(boolean exp3)
	{
		this.exp3 = exp3;
	}

	public boolean isExp5()
	{
		return exp5;
	}

	public void setExp5(boolean exp5)
	{
		this.exp5 = exp5;
	}

	public boolean isHill()
	{
		return hill;
	}

	public void setHill(boolean hill)
	{
		this.hill = hill;
	}

	public boolean isPower()
	{
		return power;
	}

	public void setPower(boolean power)
	{
		this.power = power;
	}

	public boolean isConstantVariance()
	{
		return constantVariance;
	}

	public void setConstantVariance(boolean constantVariance)
	{
		this.constantVariance = constantVariance;
	}

	public boolean isFlagHillModel()
	{
		return flagHillModel;
	}

	public void setFlagHillModel(boolean flagHillModel)
	{
		this.flagHillModel = flagHillModel;
	}

	public int getNumThreads()
	{
		return numThreads;
	}

	public void setNumThreads(int numThreads)
	{
		this.numThreads = numThreads;
	}

	public BMRFactor getBMRFactor()
	{
		return BMRFactor;
	}

	public void setBMRFactor(BMRFactor bMRFactor)
	{
		BMRFactor = bMRFactor;
	}

	public String getBmrType()
	{
		return bmrType;
	}

	public void setBmrType(String bmrType)
	{
		this.bmrType = bmrType;
	}

}
