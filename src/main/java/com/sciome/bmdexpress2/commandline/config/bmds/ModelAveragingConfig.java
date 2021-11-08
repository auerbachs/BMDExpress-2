package com.sciome.bmdexpress2.commandline.config.bmds;

import java.util.ArrayList;
import java.util.List;

public class ModelAveragingConfig
{

	private Integer method = 1; // 1 for laplace 2 for mcmc
	private List<BMDSModelConfig> modelConfigs = new ArrayList<>();
	private BMDSInputConfig bmdsInputConfig;

	// this could be dose response data or prefiltered data.
	private String inputCategory;
	// name of the data set that is being input to bmds analysis.
	private String inputName;

	// name of output for the analysis
	private String outputName;

	private Integer numberOfThreads = 1;

	public String getInputCategory()
	{
		return inputCategory;
	}

	public void setInputCategory(String inputCategory)
	{
		this.inputCategory = inputCategory;
	}

	public String getInputName()
	{
		return inputName;
	}

	public void setInputName(String inputName)
	{
		this.inputName = inputName;
	}

	public String getOutputName()
	{
		return outputName;
	}

	public void setOutputName(String outputName)
	{
		this.outputName = outputName;
	}

	public List<BMDSModelConfig> getModelConfigs()
	{
		return modelConfigs;
	}

	public void setModelConfigs(List<BMDSModelConfig> modelConfigs)
	{
		this.modelConfigs = modelConfigs;
	}

	public BMDSInputConfig getBmdsInputConfig()
	{
		return bmdsInputConfig;
	}

	public void setBmdsInputConfig(BMDSInputConfig bmdsInputConfig)
	{
		this.bmdsInputConfig = bmdsInputConfig;
	}

	public Integer getNumberOfThreads()
	{
		return numberOfThreads;
	}

	public void setNumberOfThreads(Integer numberOfThreads)
	{
		this.numberOfThreads = numberOfThreads;
	}

	public Integer getMethod()
	{
		return method;
	}

	public void setMethod(Integer method)
	{
		this.method = method;
	}

}
