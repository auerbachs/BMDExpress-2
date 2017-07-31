package com.sciome.bmdexpress2.util.bmds.shared;

public enum BestPolyModelTestEnum
{
	NESTED_CHI_SQUARED("Nested Chi Square"), LOWEST_AIC("Lowest AIC");

	private final String text;

	/**
	 * @param text
	 */
	private BestPolyModelTestEnum(final String text)
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
