package com.sciome.bmdexpress2.mvp.model;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;

public class CombinedDataSet extends BMDExpressAnalysisDataSet
{

	List<String>				header;
	List<BMDExpressAnalysisRow>	rows;
	List<Object>				objects;
	AnalysisInfo				analysisInfo	= new AnalysisInfo();
	String						name;

	public CombinedDataSet(List<String> header, String name)
	{
		this.name = name;
		this.header = header;
		rows = new ArrayList<>();

		// maybe this can contain notes about which datasets were combined.
		analysisInfo.setNotes(new ArrayList<>());
	}

	@Override
	public List<String> getColumnHeader()
	{
		return header;
	}

	@Override
	public List<Object> getColumnHeader2()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnalysisInfo getAnalysisInfo()
	{
		return analysisInfo;
	}

	public void setObjects(List<Object> objects)
	{
		this.objects = objects;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;

	}

	@Override
	public List<BMDExpressAnalysisRow> getAnalysisRows()
	{
		return rows;
	}

	@Override
	public Object getObject()
	{
		return objects;
	}

}