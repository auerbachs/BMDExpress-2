package com.sciome.bmdexpress2.commandline.config;

import java.util.List;

import com.sciome.bmdexpress2.commandline.config.bmds.BMDSConfig;
import com.sciome.bmdexpress2.commandline.config.category.CategoryConfig;
import com.sciome.bmdexpress2.commandline.config.expression.ExpressionDataConfig;
import com.sciome.bmdexpress2.commandline.config.prefilter.PrefilterConfig;

public class RunConfig
{

	private List<ExpressionDataConfig>	expressionDataConfigs;
	private List<PrefilterConfig>		preFilterConfigs;
	private List<BMDSConfig>			bmdsConfigs;
	private List<CategoryConfig>		categoryAnalysisConfigs;

	private String						bm2FileName;

	private String						jsonExportFileName;
	private String						basePath;

	private String						preprendToNames	= "";
	private String						appendToNames	= "";
	private Boolean						overwrite		= false;

	public String getBm2FileName()
	{
		return bm2FileName;
	}

	public void setBm2FileName(String bm2FileName)
	{
		this.bm2FileName = bm2FileName;
	}

	public String getJsonExportFileName()
	{
		return jsonExportFileName;
	}

	public void setJsonExportFileName(String jsonExportFileName)
	{
		this.jsonExportFileName = jsonExportFileName;
	}

	public String getPreprendToNames()
	{
		return preprendToNames;
	}

	public void setPreprendToNames(String preprendToNames)
	{
		this.preprendToNames = preprendToNames;
	}

	public String getAppendToNames()
	{
		return appendToNames;
	}

	public void setAppendToNames(String appendToNames)
	{
		this.appendToNames = appendToNames;
	}

	public List<ExpressionDataConfig> getExpressionDataConfigs()
	{
		return expressionDataConfigs;
	}

	public void setExpressionDataConfigs(List<ExpressionDataConfig> expressionDataConfig)
	{
		this.expressionDataConfigs = expressionDataConfig;
	}

	public List<PrefilterConfig> getPreFilterConfigs()
	{
		return preFilterConfigs;
	}

	public void setPreFilterConfigs(List<PrefilterConfig> preFilterConfigs)
	{
		this.preFilterConfigs = preFilterConfigs;
	}

	public List<BMDSConfig> getBmdsConfigs()
	{
		return bmdsConfigs;
	}

	public void setBmdsConfigs(List<BMDSConfig> bmdsConfigs)
	{
		this.bmdsConfigs = bmdsConfigs;
	}

	public List<CategoryConfig> getCategoryAnalysisConfigs()
	{
		return categoryAnalysisConfigs;
	}

	public void setCategoryAnalysisConfigs(List<CategoryConfig> categoryAnalysisConfigs)
	{
		this.categoryAnalysisConfigs = categoryAnalysisConfigs;
	}

	public Boolean getOverwrite()
	{
		return overwrite;
	}

	public void setOverwrite(Boolean overwrite)
	{
		this.overwrite = overwrite;
	}

	public String getBasePath()
	{
		return basePath;
	}

	public void setBasePath(String basePath)
	{
		this.basePath = basePath;
	}

}
