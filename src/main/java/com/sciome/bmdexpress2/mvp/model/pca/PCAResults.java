package com.sciome.bmdexpress2.mvp.model.pca;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;

/**
 * Object for storing PCAResults of an expression data set for charting purposes
 * @author Shyam
 */
public class PCAResults extends BMDExpressAnalysisDataSet {

	private String					name;

	private AnalysisInfo			analysisInfo;
	
	private List<PCAResult>			pcaResults;
	private transient List<String>	columnHeader;

	private Long					id;
	
	/* define chartabble key values */
	public static final String 		DOSAGE						= "Dosage";
	public static final String		PC1							= "PC1";
	public static final String		PC2							= "PC2";
	public static final String		PC3							= "PC3";
	public static final String		PC4							= "PC4";

	public List<PCAResult> getPcaResults() {
		return pcaResults;
	}

	public void setPcaResults(List<PCAResult> pcaResults) {
		this.pcaResults = pcaResults;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setAnalysisInfo(AnalysisInfo analysisInfo) {
		this.analysisInfo = analysisInfo;
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
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public List getAnalysisRows() {
		return pcaResults;
	}

	@Override
	public Object getObject() {
		return this;
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
		columnHeader.add(DOSAGE);
		for(int i = 1; i <= pcaResults.size(); i++) {
			columnHeader.add("PC" + i);
		}
	}
	
	// This is called in order to generate data for each probe stat result for viewing
	// data in a table or exporting it.
	private void fillRowData()
	{
		for(int i = 0; i < pcaResults.size(); i++) {
			pcaResults.get(i).createRowData();
		}
	}
}
