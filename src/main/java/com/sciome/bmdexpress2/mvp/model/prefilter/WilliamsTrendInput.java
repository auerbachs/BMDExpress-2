package com.sciome.bmdexpress2.mvp.model.prefilter;

public class WilliamsTrendInput extends PrefilterInput {
	private double numPermutations;

	public WilliamsTrendInput() {
		super();
		this.numPermutations = 100;
	}
	
	public double getNumPermutations() {
		return numPermutations;
	}

	public void setNumPermutations(double numPermutations) {
		this.numPermutations = numPermutations;
	}
}
