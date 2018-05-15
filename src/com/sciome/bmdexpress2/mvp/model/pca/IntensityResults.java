package com.sciome.bmdexpress2.mvp.model.pca;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;

public class IntensityResults extends BMDExpressAnalysisDataSet{

	private String					name;
	private List<IntensityResult>	intensityResults;
	private transient List<String>	columnHeader;
	private AnalysisInfo			analysisInfo;
	
	public static final String		PROBE_ID			="Probe ID";
	public static final String		RESPONSE			="Log 2 Response";
	
	
	public List<IntensityResult> getIntensityResults() {
		return intensityResults;
	}

	public void setIntensityResults(List<IntensityResult> intensityResults) {
		this.intensityResults = intensityResults;
	}

	public void setColumnHeader(List<String> columnHeader) {
		this.columnHeader = columnHeader;
	}

	@Override
	public List<String> getColumnHeader() {
		if (columnHeader == null || columnHeader.size() == 0)
			fillTableData();
		return columnHeader;
	}

	@Override
	public List<Object> getColumnHeader2() {
		return null;
	}

	@Override
	public AnalysisInfo getAnalysisInfo() {
		return analysisInfo;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List getAnalysisRows() {
		return intensityResults;
	}

	@Override
	public Object getObject() {
		return null;
	}

	private void fillTableData()
	{
		if (columnHeader == null)
		{
			fillColumnHeader();
			fillRowData();
		}
	}
	
	/*
	 * fill the column header for table display or file export purposes.
	 */
	private void fillColumnHeader()
	{
		columnHeader = new ArrayList<>();
		columnHeader.add(PROBE_ID);
		columnHeader.add(RESPONSE);
	}
	
	// This is called in order to generate data for each probe stat result for viewing
	// data in a table or exporting it.
	private void fillRowData()
	{
		for(int i = 0; i < intensityResults.size(); i++) {
			intensityResults.get(i).createRowData();
		}
	}
}
