package com.sciome.bmdexpress2.util.bmds.shared;

public enum RestrictPowerEnum {
	NO_RESTRICTION("No Restriction"), GREATER_OR_EQUAL_TO_ONE(">=1");

	private final String text;

	/**
	 * @param text
	 */
	private RestrictPowerEnum(final String text)

	{
		this.text = text;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString()
	{
		return text;
	}
}
