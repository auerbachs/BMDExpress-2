package com.sciome.bmdexpress2.mvp.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BMDExpressAnalysisRow
{
	@JsonIgnore
	public abstract Object getObject();

	public abstract List<Object> getRow();
}
