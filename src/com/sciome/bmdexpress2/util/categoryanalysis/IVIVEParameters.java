package com.sciome.bmdexpress2.util.categoryanalysis;

import java.util.List;

import com.sciome.commons.math.httk.calc.calc_analytic_css.Model;
import com.sciome.commons.math.httk.model.Compound;

public class IVIVEParameters {
	public enum DoseUnits {
		uM, nM, pM;
	}
	
	private Compound						compound;
	private List<Model>						models;
	private DoseUnits						units;
	
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
	public DoseUnits getUnits() {
		return units;
	}
	public void setUnits(DoseUnits units) {
		this.units = units;
	}
}
