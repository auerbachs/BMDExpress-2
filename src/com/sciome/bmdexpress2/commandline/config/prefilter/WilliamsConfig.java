package com.sciome.bmdexpress2.commandline.config.prefilter;

public class WilliamsConfig extends PrefilterConfig {
	private String numberOfPermutations;
	private String numberOfThreads			= "1";

	public String getNumberOfPermutations() {
		return numberOfPermutations;
	}

	public void setNumberOfPermutations(String numberOfPermutations) {
		this.numberOfPermutations = numberOfPermutations;
	}

	public String getNumberOfThreads() {
		return numberOfThreads;
	}

	public void setNumberOfThreads(String numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
	}
}