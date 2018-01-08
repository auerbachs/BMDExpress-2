package com.sciome.bmdexpress2.mvp.model.pca;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;

public class PCAResult extends BMDExpressAnalysisRow {
	
	private	String					dosage;
	private String					name;
	private List<Float>				principleComponents;
	
	// row data for the table view.
	@JsonIgnore
	protected transient List<Object>	row;
	
	
	public String getDosage() {
		return dosage;
	}

	public void setDosage(String dosage) {
		this.dosage = dosage;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Float> getPrincipleComponents() {
		return principleComponents;
	}

	public void setPrincipleComponents(List<Float> principleComponents) {
		this.principleComponents = principleComponents;
	}

	@Override
	public Object getObject() {
		return this;
	}

	@Override
	public List<Object> getRow() {
		return row;
	}

	public void createRowData() {
		if(row != null) {
			return;
		}
		
		row = new ArrayList<>();
		row.add(dosage);
		for(int i = 0; i < principleComponents.size(); i++) {
			row.add(principleComponents.get(i));
		}
	}
	
}
