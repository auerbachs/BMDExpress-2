package com.sciome.bmdexpress2.commandline.config.expression;

public class ExpressionDataConfig
{

	// name of file containing data
	private String	inputFileName;

	// name to store the expression data as
	private String	outputName;

	// is the first line labels or doses?
	private Integer	hasHeaders;

	// GPL id for most things.
	private String	platform;

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

	public Integer getHasHeaders()
	{
		return hasHeaders;
	}

	public void setHasHeaders(Integer hasHeaders)
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

}
