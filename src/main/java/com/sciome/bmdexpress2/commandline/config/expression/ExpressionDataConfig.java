package com.sciome.bmdexpress2.commandline.config.expression;

import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;

public class ExpressionDataConfig
{

	// name of file containing data
	private String					inputFileName;

	// name to store the expression data as
	private String					outputName;

	// is the first line labels or doses?
	private Boolean					hasHeaders;

	// GPL id for most things. selectBestModel()
	private String					platform;

	// specify the log transformation
	private LogTransformationEnum	logTransformation	= LogTransformationEnum.BASE2;

	public String getInputFileName()
	{
		return inputFileName;
	}

	public void setInputFileName(String inputFileName)
	{
		this.inputFileName = inputFileName;
	}

	public String getOutputName()
	{
		return outputName;
	}

	public void setOutputName(String outputName)
	{
		this.outputName = outputName;
	}

	public Boolean getHasHeaders()
	{
		return hasHeaders;
	}

	public void setHasHeaders(Boolean hasHeaders)
	{
		this.hasHeaders = hasHeaders;
	}

	public String getPlatform()
	{
		return platform;
	}

	public void setPlatform(String platform)
	{
		this.platform = platform;
	}

	public LogTransformationEnum getLogTransformation()
	{
		return logTransformation;
	}

	public void setLogTransformation(LogTransformationEnum logTransformationEnum)
	{
		this.logTransformation = logTransformationEnum;
	}

}
