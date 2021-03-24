package com.sciome.bmdexpress2.util.categoryanalysis;

import java.util.List;

import com.sciome.commons.math.httk.calc.calc_analytic_css.Model;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Units;
import com.sciome.commons.math.httk.model.Compound;

public class IVIVEParameters {
	public enum DoseUnits {
		uM, nM, pM;
	}
	
	private Compound						compound;
	private List<Model>						models;
	private DoseUnits						doseUnits;
	private Units							outputUnits;
	private String							species;
	private double							quantile;
	private boolean							invivo;
	private double							finalTime;
	private double							doseSpacing;
	
	public Compound getCompound() {
		return compound;
	}
	public void setCompound(Compound compound) {
		this.compound = compound;
	}
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}
	public DoseUnits getDoseUnits() {
		return doseUnits;
	}
	public void setDoseUnits(DoseUnits doseUnits) {
		this.doseUnits = doseUnits;
	}
	public Units getOutputUnits() {
		return outputUnits;
	}
	public void setOutputUnits(Units outputUnits) {
		this.outputUnits = outputUnits;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public double getQuantile() {
		return quantile;
	}
	public void setQuantile(double quantile) {
		this.quantile = quantile;
	}
	public boolean isInvivo() {
		return invivo;
	}
	public void setInvivo(boolean invivo) {
		this.invivo = invivo;
	}
	public double getFinalTime() {
		return finalTime;
	}
	public void setFinalTime(double finalTime) {
		this.finalTime = finalTime;
	}
	public double getDoseSpacing() {
		return doseSpacing;
	}
	public void setDoseSpacing(double doseSpacing) {
		this.doseSpacing = doseSpacing;
	}
}
