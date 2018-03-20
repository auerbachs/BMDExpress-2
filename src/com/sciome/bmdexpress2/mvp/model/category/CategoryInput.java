package com.sciome.bmdexpress2.mvp.model.category;

public class CategoryInput {
	private boolean removePromiscuousProbes;
	private boolean removeBMDGreaterThanHighestDose;
	private boolean removeBMDLessThanPValue;
	private boolean removeGenesWithBMD_BMDL;
	private boolean removeGenesWithBMDU_BMD;
	private boolean removeGenesWithBMDU_BMDL;
	private boolean removeGenesWithBMDValuesGreaterThanNFold;
	private boolean removeGenesWithMaxFoldChangeLessThan;
	private boolean removeGenesWithPrefilterPValue;
	private boolean removeGenesWithPrefilterAdjustedPValue;
	private boolean eliminateGeneSetRedundancy;
	private boolean identifyConflictingProbeSets;
	
	private double removeBMDLessThanPValueNumber;
	private double removeGenesWithBMD_BMDLNumber;
	private double removeGenesWithBMDU_BMDNumber;
	private double removeGenesWithBMDU_BMDLNumber;
	private double removeGenesWithBMDValuesGreaterThanNFoldNumber;
	private double removeGenesWithMaxFoldChangeLessThanNumber;
	private double removeGenesWithPrefilterPValueNumber;
	private double removeGenesWithPrefilterAdjustedPValueNumber;
	private double correlationCutoffForConflictingProbeSets;
	
	public CategoryInput() {
		super();
		this.removePromiscuousProbes = true;
		this.removeBMDGreaterThanHighestDose = true;
		this.removeBMDLessThanPValue = false;
		this.removeGenesWithBMD_BMDL = false;
		this.removeGenesWithBMDU_BMD = false;
		this.removeGenesWithBMDU_BMDL = false;
		this.removeGenesWithBMDValuesGreaterThanNFold = false;
		this.removeGenesWithMaxFoldChangeLessThan = false;
		this.removeGenesWithPrefilterPValue = false;
		this.removeGenesWithPrefilterAdjustedPValue = false;
		this.eliminateGeneSetRedundancy = false;
		this.identifyConflictingProbeSets = true;
		
		this.removeBMDLessThanPValueNumber = 0.1;
		this.removeGenesWithBMD_BMDLNumber = 20;
		this.removeGenesWithBMDU_BMDNumber = 20;
		this.removeGenesWithBMDU_BMDLNumber = 40;
		this.removeGenesWithBMDValuesGreaterThanNFoldNumber = 10;
		this.removeGenesWithMaxFoldChangeLessThanNumber = 1.2;
		this.removeGenesWithPrefilterPValueNumber = 0.05;
		this.removeGenesWithPrefilterAdjustedPValueNumber = 0.5;
		this.correlationCutoffForConflictingProbeSets = 0.5;
	}
	
	public boolean isRemovePromiscuousProbes() {
		return removePromiscuousProbes;
	}
	public void setRemovePromiscuousProbes(boolean removePromiscuousProbes) {
		this.removePromiscuousProbes = removePromiscuousProbes;
	}
	public boolean isRemoveBMDGreaterThanHighestDose() {
		return removeBMDGreaterThanHighestDose;
	}
	public void setRemoveBMDGreaterThanHighestDose(boolean removeBMDGreaterThanHighestDose) {
		this.removeBMDGreaterThanHighestDose = removeBMDGreaterThanHighestDose;
	}
	public boolean isRemoveBMDLessThanPValue() {
		return removeBMDLessThanPValue;
	}
	public void setRemoveBMDLessThanPValue(boolean removeBMDLessThanPValue) {
		this.removeBMDLessThanPValue = removeBMDLessThanPValue;
	}
	public boolean isRemoveGenesWithBMD_BMDL() {
		return removeGenesWithBMD_BMDL;
	}
	public void setRemoveGenesWithBMD_BMDL(boolean removeGenesWithBMD_BMDL) {
		this.removeGenesWithBMD_BMDL = removeGenesWithBMD_BMDL;
	}
	public boolean isRemoveGenesWithBMDU_BMD() {
		return removeGenesWithBMDU_BMD;
	}
	public void setRemoveGenesWithBMDU_BMD(boolean removeGenesWithBMDU_BMD) {
		this.removeGenesWithBMDU_BMD = removeGenesWithBMDU_BMD;
	}
	public boolean isRemoveGenesWithBMDU_BMDL() {
		return removeGenesWithBMDU_BMDL;
	}
	public void setRemoveGenesWithBMDU_BMDL(boolean removeGenesWithBMDU_BMDL) {
		this.removeGenesWithBMDU_BMDL = removeGenesWithBMDU_BMDL;
	}
	public boolean isRemoveGenesWithBMDValuesGreaterThanNFold() {
		return removeGenesWithBMDValuesGreaterThanNFold;
	}
	public void setRemoveGenesWithBMDValuesGreaterThanNFold(boolean removeGenesWithBMDValuesGreaterThanNFold) {
		this.removeGenesWithBMDValuesGreaterThanNFold = removeGenesWithBMDValuesGreaterThanNFold;
	}
	public boolean isRemoveGenesWithMaxFoldChangeLessThan() {
		return removeGenesWithMaxFoldChangeLessThan;
	}
	public void setRemoveGenesWithMaxFoldChangeLessThan(boolean removeGenesWithMaxFoldChangeLessThan) {
		this.removeGenesWithMaxFoldChangeLessThan = removeGenesWithMaxFoldChangeLessThan;
	}
	public boolean isRemoveGenesWithPrefilterPValue() {
		return removeGenesWithPrefilterPValue;
	}
	public void setRemoveGenesWithPrefilterPValue(boolean removeGenesWithPrefilterPValue) {
		this.removeGenesWithPrefilterPValue = removeGenesWithPrefilterPValue;
	}
	public boolean isRemoveGenesWithPrefilterAdjustedPValue() {
		return removeGenesWithPrefilterAdjustedPValue;
	}
	public void setRemoveGenesWithPrefilterAdjustedPValue(boolean removeGenesWithPrefilterAdjustedPValue) {
		this.removeGenesWithPrefilterAdjustedPValue = removeGenesWithPrefilterAdjustedPValue;
	}
	public boolean isEliminateGeneSetRedundancy() {
		return eliminateGeneSetRedundancy;
	}
	public void setEliminateGeneSetRedundancy(boolean eliminateGeneSetRedundancy) {
		this.eliminateGeneSetRedundancy = eliminateGeneSetRedundancy;
	}
	public boolean isIdentifyConflictingProbeSets() {
		return identifyConflictingProbeSets;
	}
	public void setIdentifyConflictingProbeSets(boolean identifyConflictingProbeSets) {
		this.identifyConflictingProbeSets = identifyConflictingProbeSets;
	}
	public double getRemoveBMDLessThanPValueNumber() {
		return removeBMDLessThanPValueNumber;
	}
	public void setRemoveBMDLessThanPValueNumber(double removeBMDLessThanPValueNumber) {
		this.removeBMDLessThanPValueNumber = removeBMDLessThanPValueNumber;
	}
	public double getRemoveGenesWithBMD_BMDLNumber() {
		return removeGenesWithBMD_BMDLNumber;
	}
	public void setRemoveGenesWithBMD_BMDLNumber(double removeGenesWithBMD_BMDLNumber) {
		this.removeGenesWithBMD_BMDLNumber = removeGenesWithBMD_BMDLNumber;
	}
	public double getRemoveGenesWithBMDU_BMDNumber() {
		return removeGenesWithBMDU_BMDNumber;
	}
	public void setRemoveGenesWithBMDU_BMDNumber(double removeGenesWithBMDU_BMDNumber) {
		this.removeGenesWithBMDU_BMDNumber = removeGenesWithBMDU_BMDNumber;
	}
	public double getRemoveGenesWithBMDU_BMDLNumber() {
		return removeGenesWithBMDU_BMDLNumber;
	}
	public void setRemoveGenesWithBMDU_BMDLNumber(double removeGenesWithBMDU_BMDLNumber) {
		this.removeGenesWithBMDU_BMDLNumber = removeGenesWithBMDU_BMDLNumber;
	}
	public double getRemoveGenesWithBMDValuesGreaterThanNFoldNumber() {
		return removeGenesWithBMDValuesGreaterThanNFoldNumber;
	}
	public void setRemoveGenesWithBMDValuesGreaterThanNFoldNumber(double removeGenesWithBMDValuesGreaterThanNFoldNumber) {
		this.removeGenesWithBMDValuesGreaterThanNFoldNumber = removeGenesWithBMDValuesGreaterThanNFoldNumber;
	}
	public double getRemoveGenesWithMaxFoldChangeLessThanNumber() {
		return removeGenesWithMaxFoldChangeLessThanNumber;
	}
	public void setRemoveGenesWithMaxFoldChangeLessThanNumber(double removeGenesWithMaxFoldChangeLessThanNumber) {
		this.removeGenesWithMaxFoldChangeLessThanNumber = removeGenesWithMaxFoldChangeLessThanNumber;
	}
	public double getRemoveGenesWithPrefilterPValueNumber() {
		return removeGenesWithPrefilterPValueNumber;
	}
	public void setRemoveGenesWithPrefilterPValueNumber(double removeGenesWithPrefilterPValueNumber) {
		this.removeGenesWithPrefilterPValueNumber = removeGenesWithPrefilterPValueNumber;
	}
	public double getRemoveGenesWithPrefilterAdjustedPValueNumber() {
		return removeGenesWithPrefilterAdjustedPValueNumber;
	}
	public void setRemoveGenesWithPrefilterAdjustedPValueNumber(double removeGenesWithPrefilterAdjustedPValueNumber) {
		this.removeGenesWithPrefilterAdjustedPValueNumber = removeGenesWithPrefilterAdjustedPValueNumber;
	}
	public double getCorrelationCutoffForConflictingProbeSets() {
		return correlationCutoffForConflictingProbeSets;
	}
	public void setCorrelationCutoffForConflictingProbeSets(double correlationCutoffForConflictingProbeSets) {
		this.correlationCutoffForConflictingProbeSets = correlationCutoffForConflictingProbeSets;
	}
	
	
}
