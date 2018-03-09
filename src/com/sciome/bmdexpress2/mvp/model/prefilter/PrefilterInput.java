package com.sciome.bmdexpress2.mvp.model.prefilter;

public abstract class PrefilterInput {
	private boolean 	useFoldChange;
	private boolean 	filterControlGenes;
	private boolean		useBenAndHoch;
	private double		pValueCutOff;
	private double		foldChangeValue;
	
	public PrefilterInput() {
		this.useFoldChange = true;
		this.filterControlGenes = true;
		this.useBenAndHoch = false;
		this.pValueCutOff = .05;
		this.foldChangeValue = 2;
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
}
