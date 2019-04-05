package com.sciome.bmdexpress2.commandline.config.category;

import java.util.ArrayList;

import com.sciome.bmdexpress2.util.categoryanalysis.IVIVEParameters.DoseUnits;

public class IVIVEConfig {
	private Boolean oneCompartment;
	private Boolean pbtk;
	private Boolean threeCompartment;
	private Boolean threeCompartmentSS;
	
	//Auto-populate fields
	private Boolean useAutoPopulate;
	private String compoundName;
	private String compoundCASRN;
	private String compoundSMILES;
	
	private Double mw;
	private Double logP;
	private ArrayList<Double> pkaDonor;
	private ArrayList<Double> pkaAcceptor;
	private Double CLint;
	private Double fractionUnboundPlamsa;
	private DoseUnits doseUnits;
	
	public Boolean getOneCompartment() {
		return oneCompartment;
	}
	public void setOneCompartment(Boolean oneCompartment) {
		this.oneCompartment = oneCompartment;
	}
	public Boolean getPbtk() {
		return pbtk;
	}
	public void setPbtk(Boolean pbtk) {
		this.pbtk = pbtk;
	}
	public Boolean getThreeCompartment() {
		return threeCompartment;
	}
	public void setThreeCompartment(Boolean threeCompartment) {
		this.threeCompartment = threeCompartment;
	}
	public Boolean getThreeCompartmentSS() {
		return threeCompartmentSS;
	}
	public void setThreeCompartmentSS(Boolean threeCompartmentSS) {
		this.threeCompartmentSS = threeCompartmentSS;
	}
	public Boolean getUseAutoPopulate() {
		return useAutoPopulate;
	}
	public void setUseAutoPopulate(Boolean useAutoPopulate) {
		this.useAutoPopulate = useAutoPopulate;
	}
	public String getCompoundName() {
		return compoundName;
	}
	public void setCompoundName(String compoundName) {
		this.compoundName = compoundName;
	}
	public String getCompoundCASRN() {
		return compoundCASRN;
	}
	public void setCompoundCASRN(String compoundCASRN) {
		this.compoundCASRN = compoundCASRN;
	}
	public String getCompoundSMILES() {
		return compoundSMILES;
	}
	public void setCompoundSMILES(String compoundSMILES) {
		this.compoundSMILES = compoundSMILES;
	}
	public Double getMw() {
		return mw;
	}
	public void setMw(Double mw) {
		this.mw = mw;
	}
	public Double getLogP() {
		return logP;
	}
	public void setLogP(Double logP) {
		this.logP = logP;
	}
	public ArrayList<Double> getPkaDonor() {
		return pkaDonor;
	}
	public void setPkaDonor(ArrayList<Double> pkaDonor) {
		this.pkaDonor = pkaDonor;
	}
	public ArrayList<Double> getPkaAcceptor() {
		return pkaAcceptor;
	}
	public void setPkaAcceptor(ArrayList<Double> pkaAcceptor) {
		this.pkaAcceptor = pkaAcceptor;
	}
	public Double getCLint() {
		return CLint;
	}
	public void setCLint(Double cLint) {
		CLint = cLint;
	}
	public Double getFractionUnboundPlamsa() {
		return fractionUnboundPlamsa;
	}
	public void setFractionUnboundPlamsa(Double fractionUnboundPlamsa) {
		this.fractionUnboundPlamsa = fractionUnboundPlamsa;
	}
	public DoseUnits getDoseUnits() {
		return doseUnits;
	}
	public void setDoseUnits(DoseUnits doseUnits) {
		this.doseUnits = doseUnits;
	}
}
