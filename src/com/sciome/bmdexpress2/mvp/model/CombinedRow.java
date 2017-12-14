package com.sciome.bmdexpress2.mvp.model;

import java.util.ArrayList;
import java.util.List;

public class CombinedRow extends BMDExpressAnalysisRow
{

	private Object			object;
	private List<Object>	row;

	public CombinedRow(Object obj)
	{
		object = obj;
		row = new ArrayList<>();
	}

	@Override
	public Object getObject()
	{
		return object;
	}

	@Override
	public List<Object> getRow()
	{
		return row;
	}

}