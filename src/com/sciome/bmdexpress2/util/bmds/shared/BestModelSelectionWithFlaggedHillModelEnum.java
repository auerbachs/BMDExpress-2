package com.sciome.bmdexpress2.util.bmds.shared;

public enum BestModelSelectionWithFlaggedHillModelEnum
{
	INCLUDE_FLAGGED_HILL("Include Flagged Hill (default)"), EXCLUDE_FLAGGED_HILL_FROM_BEST(
			"Exclude Flagged Hill from Best Models"), EXCLUDE_ALL_HILL_FROM_BEST(
					"Exclude All Hill from Best Models"), MODIFY_BMD_IF_FLAGGED_HILL_BEST(
							"Modify BMD if Flagged Hill as Best Model"), SELECT_NEXT_BEST_PVALUE_GREATER_OO5(
									"Select Next Best Model with P-Value > 0.05");

	private final String text;

	/**
	 * @param text
	 */
	private BestModelSelectionWithFlaggedHillModelEnum(final String text)

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
