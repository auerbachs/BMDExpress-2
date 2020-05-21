package com.sciome.bmdexpress2.mvp.model.prefilter;

public abstract class PrefilterInput {
	private boolean 	useFoldChange;
	private boolean 	filterControlGenes;
	private boolean		useBenAndHoch;
	private boolean		tTest;
	private double		pValueCutOff;
	private double		foldChangeValue;
	private double		loelFoldChangeValue;
	private double		loelPValue;
	private int			numThreads;
	
	public PrefilterInput() {
		this.useFoldChange = true;
		this.filterControlGenes = true;
		this.useBenAndHoch = false;
		this.tTest = true;
		this.pValueCutOff = .05;
		this.foldChangeValue = 2;
		this.loelFoldChangeValue = 2;
		this.loelPValue = .05;
		this.numThreads = 4;
	}
	
	public boolean isUseFoldChange() {
		return useFoldChange;
	}
	
	public void setUseFoldChange(boolean useFoldChange) {
		this.useFoldChange = useFoldChange;
	}
	
	public boolean isFilterControlGenes() {
		return filterControlGenes;
	}
	
	public void setFilterControlGenes(boolean filterControlGenes) {
		this.filterControlGenes = filterControlGenes;
	}
	
	public boolean isUseBenAndHoch() {
		return useBenAndHoch;
	}
	
	public void setUseBenAndHoch(boolean useBenAndHoch) {
		this.useBenAndHoch = useBenAndHoch;
	}
	
	public double getpValueCutOff() {
		return pValueCutOff;
	}
	public void setpValueCutOff(double pValueCutOff) {
		this.pValueCutOff = pValueCutOff;
	}
	
	public double getFoldChangeValue() {
		return foldChangeValue;
	}
	
	public void setFoldChangeValue(double foldChangeValue) {
		this.foldChangeValue = foldChangeValue;
	}

	public double getLoelFoldChangeValue() {
		return loelFoldChangeValue;
	}

	public void setLoelFoldChangeValue(double loelFoldChangeValue) {
		this.loelFoldChangeValue = loelFoldChangeValue;
	}

	public double getLoelPValue() {
		return loelPValue;
	}

	public void setLoelPValue(double loelPValue) {
		this.loelPValue = loelPValue;
	}

	public boolean istTest() {
		return tTest;
	}

	public void settTest(boolean tTest) {
		this.tTest = tTest;
	}

	public int getNumThreads() {
		return numThreads;
	}

	public void setNumThreads(int numThreads) {
		this.numThreads = numThreads;
	}
}
