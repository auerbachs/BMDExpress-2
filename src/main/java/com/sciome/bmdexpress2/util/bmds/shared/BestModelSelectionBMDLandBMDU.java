package com.sciome.bmdexpress2.util.bmds.shared;

public enum BestModelSelectionBMDLandBMDU
{
	COMPUTE_AND_UTILIZE_BMD_BMDL("Compute and utilize BMD and BMDL in best model selection"),
	COMPUTE_AND_UTILIZE("Compute and utilize BMD,BMDL and BMDU in best model selection"), COMPUTE_BUT_IGNORE(
			"Compute but ignore non-convergence in best model selection"), DO_NOT_COMPUTE("Do not compute");

	private final String text;

	/**
	 * @param text
	 */
	private BestModelSelectionBMDLandBMDU(final String text)

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
