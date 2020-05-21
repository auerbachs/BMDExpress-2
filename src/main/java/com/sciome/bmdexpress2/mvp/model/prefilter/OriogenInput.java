package com.sciome.bmdexpress2.mvp.model.prefilter;

public class OriogenInput extends PrefilterInput {
	private int numInitialBootstraps;
	private int numMaximumBootstraps;
	private double shrinkagePercentile;
	
	public OriogenInput() {
		super();
		this.numInitialBootstraps = 500;
		this.numMaximumBootstraps = 1000;
		this.shrinkagePercentile = 5.0;
	}
	
	public int getNumInitialBootstraps() {
		return numInitialBootstraps;
	}
	
	public void setNumInitialBootstraps(int numInitialBootstraps) {
		this.numInitialBootstraps = numInitialBootstraps;
	}
	
	public int getNumMaximumBootstraps() {
		return numMaximumBootstraps;
	}
	
	public void setNumMaximumBootstraps(int numMaximumBootstraps) {
		this.numMaximumBootstraps = numMaximumBootstraps;
	}
	
	public double getShrinkagePercentile() {
		return shrinkagePercentile;
	}
	
	public void setShrinkagePercentile(double shrinkagePercentile) {
		this.shrinkagePercentile = shrinkagePercentile;
	}
}
