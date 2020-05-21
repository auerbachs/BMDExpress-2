package com.sciome.bmdexpress2.mvp.model.pca;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;

public class IntensityResult extends BMDExpressAnalysisRow {
	private String name;
	private Float response;
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Float getResponse() {
		return response;
	}

	public void setResponse(Float response) {
		this.response = response;
	}

	public void setRow(List<Object> row) {
		this.row = row;
	}

	@JsonIgnore
	protected transient List<Object>	row;
	
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
		row.add(name);
		row.add(response);
	}
	
	@Override
	public String toString()
	{
		return name;
	}
}
