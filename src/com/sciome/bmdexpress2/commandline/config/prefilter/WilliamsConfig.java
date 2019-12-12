package com.sciome.bmdexpress2.commandline.config.prefilter;

public class WilliamsConfig extends PrefilterConfig
{
	private Integer numberOfPermutations = 10000;

	public Integer getNumberOfPermutations()
	{
		return numberOfPermutations;
	}

	public void setNumberOfPermutations(Integer numberOfPermutations)
	{
		this.numberOfPermutations = numberOfPermutations;
	}
}