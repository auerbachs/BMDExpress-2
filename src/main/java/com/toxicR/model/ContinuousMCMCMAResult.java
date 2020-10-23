package com.toxicR.model;

import java.util.List;

public class ContinuousMCMCMAResult
{

	ContinuousResultMA result;
	List<ContinuousMCMCOutput> output;

	public ContinuousResultMA getResult()
	{
		return result;
	}

	public void setResult(ContinuousResultMA result)
	{
		this.result = result;
	}

	public List<ContinuousMCMCOutput> getOutput()
	{
		return output;
	}

	public void setOutput(List<ContinuousMCMCOutput> output)
	{
		this.output = output;
	}

}
