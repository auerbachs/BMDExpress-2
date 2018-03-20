package com.sciome.bmdexpress2.util.bmds.shared;

public class BMRFactor {
	private String description;
	private String value;

	public BMRFactor() {
		this.description = "1.021 (5%)";
		this.value =  "1.021";
	}
	
	public BMRFactor(String d, String v)
	{
		description = d;
		value = v;
	}

	@Override
	public String toString()
	{
		return description;
	}

	public String getValue()
	{
		return value;
	}

	/* This is only needed for the purpose of converting to json */
	public String getDescription() {
		return description;
	}
}
