package com.sciome.bmdexpress2.mvp.model;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;

public abstract class BMDExpressAnalysisDataSet
{

	public abstract List<String> getColumnHeader();

	public abstract List<Object> getColumnHeader2();

	public abstract AnalysisInfo getAnalysisInfo();

	public abstract String getName();

	public abstract void setName(String name);

	public abstract List<BMDExpressAnalysisRow> getAnalysisRows();

}
