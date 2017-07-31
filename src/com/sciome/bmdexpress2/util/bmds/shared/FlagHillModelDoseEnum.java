package com.sciome.bmdexpress2.util.bmds.shared;

public enum FlagHillModelDoseEnum
{
	LOWEST_DOSE("Lowest Positive Dose"), ONE_HALF_OF_LOWEST_DOSE(
			"1/2 of Lowest Positive Dose"), ONE_THIRD_OF_LOWEST_DOSE("1/3 of Lowest Positive Dose");

	private final String text;

	/**
	 * @param text
	 */
	private FlagHillModelDoseEnum(final String text)

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
