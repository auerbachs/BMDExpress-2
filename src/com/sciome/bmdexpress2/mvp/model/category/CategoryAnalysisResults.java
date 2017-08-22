package com.sciome.bmdexpress2.mvp.model.category;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.stat.BMDResult;
import com.sciome.charts.annotation.ChartableData;
import com.sciome.charts.annotation.ChartableDataLabel;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class CategoryAnalysisResults extends BMDExpressAnalysisDataSet implements Serializable
{

	/**
	 * 
	 */
	private static final long				serialVersionUID			= -5369568518892492798L;

	private String							name;
	private List<CategoryAnalysisResult>	categoryAnalsyisResults;
	private AnalysisInfo					analysisInfo;

	private BMDResult						bmdResult;

	private transient List<String>			columnHeader;

	private Long							id;

	/* define chartabble and/or filterable key values */
	public static final String				BMD_MEAN					= "BMD Mean";
	public static final String				BMD_MEDIAN					= "BMD Median";
	public static final String				BMD_SD						= "BMD Standard Deviation";
	public static final String				BMD_MINIUMUM				= "BMD Minimum";
	public static final String				BMD_MAXIMUM					= "BMD Maximum";
	public static final String				BMDUP_MEAN					= "BMD Up Mean";
	public static final String				BMDUP_MEDIAN				= "BMD Up Median";
	public static final String				BMDUP_SD					= "BMD Up Standard Deviation";
	public static final String				BMDDOWN_MEAN				= "BMD Down Mean";
	public static final String				BMDDOWN_MEDIAN				= "BMD Down Median";
	public static final String				BMDDOWN_SD					= "BMD Down Standard Deviation";
	public static final String				BMD_FIFTH_MEAN				= "BMD 5th Percentile";
	public static final String				BMD_TENTH_MEAN				= "BMD 10th Percentile";
	public static final String				BMDL_MEAN					= "BMDL Mean";
	public static final String				BMDL_MEDIAN					= "BMDL Median";
	public static final String				BMDL_SD						= "BMDL Standard Deviation";
	public static final String				BMDL_MINIUMUM				= "BMDL Minimum";
	public static final String				BMDL_MAXIMUM				= "BMDL Maximum";
	public static final String				BMDLUP_MEAN					= "BMDL Up Mean";
	public static final String				BMDLUP_MEDIAN				= "BMDL Up Median";
	public static final String				BMDLUP_SD					= "BMDL Up Standard Deviation";
	public static final String				BMDLDOWN_MEAN				= "BMDL Down Mean";
	public static final String				BMDLDOWN_MEDIAN				= "BMDL Down Median";
	public static final String				BMDLDOWN_SD					= "BMDL Down Standard Deviation";

	public static final String				BMDU_MEAN					= "BMDU Mean";
	public static final String				BMDU_MEDIAN					= "BMDU Median";
	public static final String				BMDU_SD						= "BMDU Standard Deviation";
	public static final String				BMDU_MINIUMUM				= "BMDU Minimum";
	public static final String				BMDU_MAXIMUM				= "BMDU Maximum";
	public static final String				BMDUUP_MEAN					= "BMDU Up Mean";
	public static final String				BMDUUP_MEDIAN				= "BMDU Up Median";
	public static final String				BMDUUP_SD					= "BMDU Up Standard Deviation";
	public static final String				BMDUDOWN_MEAN				= "BMDU Down Mean";
	public static final String				BMDUDOWN_MEDIAN				= "BMDU Down Median";
	public static final String				BMDUDOWN_SD					= "BMDU Down Standard Deviation";

	public static final String				FISHERS_TWO_TAIL			= "Fisher's Exact Two Tail";
	public static final String				NUM_SIGNFICANT_GENES		= "Input Genes";

	public static final String				FISHERS_LEFT				= "Fisher's Exact Left P-Value";
	public static final String				FISHERS_RIGHT				= "Fisher's Exact Right P-Value";
	public static final String				FISHERS_TWO_TAIL_NEG_LOG	= "Negative Log of Fisher's Two Tail";

	public static final String				BMDU_BMDL_MEDIAN_RATIO		= "BMDU Median/BMDL Median";
	public static final String				BMD_BMDL_MEDIAN_RATIO		= "BMD Median/BMDL Median";
	public static final String				BMDU_BMD_MEDIAN_RATIO		= "BMDU Median/BMD Median";

	public static final String				BMDU_BMDL_MEAN_RATIO		= "BMDU Mean/BMDL Mean";
	public static final String				BMD_BMDL_MEAN_RATIO			= "BMD Mean/BMDL Mean";
	public static final String				BMDU_BMD_MEAN_RATIO			= "BMDU Mean/BMD Mean";

	public static final String				CATEGORY_ID					= "Category ID";
	public static final String				CATEGORY_DESCRIPTION		= "Category Description";
	public static final String				GO_TERM_LEVEL				= "GO Term Level";

	@JsonIgnore
	public Long getID()
	{
		return id;
	}

	public void setID(Long id)
	{
		this.id = id;
	}

	public BMDResult getBmdResult()
	{
		return bmdResult;
	}

	public void setBmdResult(BMDResult bmdResult)
	{
		this.bmdResult = bmdResult;
	}

	@Override
	@ChartableDataLabel(key = "Category Results Name")
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@ChartableData(key = "Category Analysis Result")
	public List<CategoryAnalysisResult> getCategoryAnalsyisResults()
	{
		return categoryAnalsyisResults;
	}

	public void setCategoryAnalsyisResults(List<CategoryAnalysisResult> categoryAnalsyisResult)
	{
		this.categoryAnalsyisResults = categoryAnalsyisResult;
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public AnalysisInfo getAnalysisInfo()
	{
		return analysisInfo;
	}

	public void setAnalysisInfo(AnalysisInfo analysisInfo)
	{
		this.analysisInfo = analysisInfo;
	}

	/*
	 * fill the column header for table display or file export purposes.
	 */
	private void fillColumnHeader()
	{
		columnHeader = new ArrayList<>();
		if (categoryAnalsyisResults == null || categoryAnalsyisResults.size() == 0)
		{
			return;
		}
		CategoryAnalysisResult catResult = categoryAnalsyisResults.get(0);

		columnHeader = catResult.generateColumnHeader();
	}

	@Override
	@JsonIgnore
	public List<String> getColumnHeader()
	{
		if (columnHeader == null || columnHeader.size() == 0)
			fillColumnHeader();
		return columnHeader;
	}

	@Override
	@JsonIgnore
	public List getAnalysisRows()
	{
		return categoryAnalsyisResults;
	}

	@Override
	@JsonIgnore
	public List<Object> getColumnHeader2()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
