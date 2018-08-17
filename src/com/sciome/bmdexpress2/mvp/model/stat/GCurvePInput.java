package com.sciome.bmdexpress2.mvp.model.stat;

public class GCurvePInput
{
	private int bootStrapIterations = 1000;

	public GCurvePInput()
	{

	}

	public int getBootStrapIterations()
	{
		return bootStrapIterations;
	}

	public void setBootStrapIterations(int bootStrapIterations)
	{
		this.bootStrapIterations = bootStrapIterations;
	}

}
