
package com.sciome.bmdexpress2.util.bmds;

import com.sciome.bmdexpress2.util.bmds.shared.BestModelSelectionWithFlaggedHillModelEnum;
import com.sciome.bmdexpress2.util.bmds.shared.BestPolyModelTestEnum;
import com.sciome.bmdexpress2.util.bmds.shared.FlagHillModelDoseEnum;

public class ModelSelectionParameters
{
	private BestPolyModelTestEnum						bestPolyModelTest;
	private double										pValue;
	private boolean										flagHillModel;
	private FlagHillModelDoseEnum						flagHillModelDose;
	private BestModelSelectionWithFlaggedHillModelEnum	bestModelSelectionWithFlaggedHill;
	private double										modFlaggedHillBMDFractionMinBMD;

	public BestPolyModelTestEnum getBestPolyModelTest()
	{
		return bestPolyModelTest;
	}

	public void setBestPolyModelTest(BestPolyModelTestEnum bestPolyModelTest)
	{
		this.bestPolyModelTest = bestPolyModelTest;
	}

	public double getpValue()
	{
		return pValue;
	}

	public void setpValue(double pValue)
	{
		this.pValue = pValue;
	}

	public boolean isFlagHillModel()
	{
		return flagHillModel;
	}

	public void setFlagHillModel(boolean flagHillModel)
	{
		this.flagHillModel = flagHillModel;
	}

	public FlagHillModelDoseEnum getFlagHillModelDose()
	{
		return flagHillModelDose;
	}

	public void setFlagHillModelDose(FlagHillModelDoseEnum flagHillModelDose)
	{
		this.flagHillModelDose = flagHillModelDose;
	}

	public BestModelSelectionWithFlaggedHillModelEnum getBestModelSelectionWithFlaggedHill()
	{
		return bestModelSelectionWithFlaggedHill;
	}

	public void setBestModelSelectionWithFlaggedHill(
			BestModelSelectionWithFlaggedHillModelEnum bestModelSelectionWithFlaggedHill)
	{
		this.bestModelSelectionWithFlaggedHill = bestModelSelectionWithFlaggedHill;
	}

	public double getModFlaggedHillBMDFractionMinBMD()
	{
		return modFlaggedHillBMDFractionMinBMD;
	}

	public void setModFlaggedHillBMDFractionMinBMD(double modFlaggedHillBMDFractionMinBMD)
	{
		this.modFlaggedHillBMDFractionMinBMD = modFlaggedHillBMDFractionMinBMD;
	}

}
