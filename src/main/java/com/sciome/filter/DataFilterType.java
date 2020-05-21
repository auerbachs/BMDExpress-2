package com.sciome.filter;

/*
 * what types of comparisons allowed by filtration.
 */
public enum DataFilterType
{
	EQUALS("equals"), CONTAINS("contains"), BETWEEN("between"), LESS_THAN("<"), LESS_THAN_EQUAL(
			"<="), GREATER_THAN(">"), GREATER_THAN_EQUAL(">=");

	private final String text;

	/**
	 * @param text
	 */
	private DataFilterType(final String text)
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
