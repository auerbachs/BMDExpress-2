package com.sciome.bmdexpress2.commandline.config.category;

public class DefinedConfig extends CategoryConfig
{
	private String	probeFilePath;
	private String	categoryFilePath;

	public String getProbeFilePath()
	{
		return probeFilePath;
	}

	public void setProbeFilePath(String probeFilePath)
	{
		this.probeFilePath = probeFilePath;
	}

	public String getCategoryFilePath()
	{
		return categoryFilePath;
	}

	public void setCategoryFilePath(String categoryFilePath)
	{
		this.categoryFilePath = categoryFilePath;
	}

}
