package com.sciome.bmdexpress2.mvp.model.stat;

import com.sciome.bmdexpress2.util.bmds.shared.BMRFactor;
import com.sciome.bmdexpress2.util.bmds.shared.BestModelSelectionBMDLandBMDU;
import com.sciome.bmdexpress2.util.bmds.shared.BestModelSelectionWithFlaggedHillModelEnum;
import com.sciome.bmdexpress2.util.bmds.shared.BestPolyModelTestEnum;
import com.sciome.bmdexpress2.util.bmds.shared.FlagHillModelDoseEnum;
import com.sciome.bmdexpress2.util.bmds.shared.RestrictPowerEnum;

public class BMDInput
{
	private boolean exp2;
	private boolean exp3;
	private boolean exp4;
	private boolean exp5;
	private boolean linear;
	private boolean poly2;
	private boolean poly3;
	private boolean poly4;
	private boolean funl;
	private boolean hill;
	private boolean power;
	private boolean constantVariance;
	private boolean flagHillModel;
	private boolean useWald;
	private int maxIterations;
	private int numThreads;
	private int killTime;
	private double confidenceLevel;
	private double pValueCutoff;
	private double modifyBMDFlaggedHill;
	private BMRFactor BMRFactor;
	private String bmrType;
	private RestrictPowerEnum restrictPower;
	private BestPolyModelTestEnum bestPolyModelTest;
	private FlagHillModelDoseEnum kParameterLessThan;
	private BestModelSelectionWithFlaggedHillModelEnum bestModelWithFlaggedHill;
	private BestModelSelectionBMDLandBMDU bestModelSelectionBMDLandBMDU;

	public BMDInput()
	{
		exp2 = true;
		exp3 = true;
		exp4 = true;
		exp5 = true;
		linear = true;
		poly2 = true;
		poly3 = true;
		poly4 = false;
		hill = true;
		power = true;
		constantVariance = true;
		flagHillModel = true;
		maxIterations = 250;
		confidenceLevel = .95;
		pValueCutoff = .05;
		modifyBMDFlaggedHill = 0.5;
		numThreads = 1;
		// default kill time to 600 seconds (akay 10 minutes)
		killTime = 600;
		bmrType = "Standard Deviation";
		useWald = false;

		BMRFactor = new BMRFactor("1.021 (5%)", "1.021");
		restrictPower = RestrictPowerEnum.NO_RESTRICTION;
		bestPolyModelTest = BestPolyModelTestEnum.NESTED_CHI_SQUARED;
		kParameterLessThan = FlagHillModelDoseEnum.ONE_THIRD_OF_LOWEST_DOSE;
		bestModelWithFlaggedHill = BestModelSelectionWithFlaggedHillModelEnum.SELECT_NEXT_BEST_PVALUE_GREATER_OO5;
		bestModelSelectionBMDLandBMDU = BestModelSelectionBMDLandBMDU.COMPUTE_AND_UTILIZE_BMD_BMDL;
	}

	
	
	
	public boolean isUseWald() {
		return useWald;
	}

	public void setUseWald(boolean useWald) {
		this.useWald = useWald;
	}

	public boolean isFunl()
	{
		return funl;
	}

	public void setFunl(boolean funl)
	{
		this.funl = funl;
	}

	public boolean isExp2()
	{
		return exp2;
	}

	public void setExp2(boolean exp2)
	{
		this.exp2 = exp2;
	}

	public boolean isExp3()
	{
		return exp3;
	}

	public void setExp3(boolean exp3)
	{
		this.exp3 = exp3;
	}

	public boolean isExp4()
	{
		return exp4;
	}

	public void setExp4(boolean exp4)
	{
		this.exp4 = exp4;
	}

	public boolean isExp5()
	{
		return exp5;
	}

	public void setExp5(boolean exp5)
	{
		this.exp5 = exp5;
	}

	public boolean isLinear()
	{
		return linear;
	}

	public void setLinear(boolean linear)
	{
		this.linear = linear;
	}

	public boolean isPoly2()
	{
		return poly2;
	}

	public void setPoly2(boolean poly2)
	{
		this.poly2 = poly2;
	}

	public boolean isPoly3()
	{
		return poly3;
	}

	public void setPoly3(boolean poly3)
	{
		this.poly3 = poly3;
	}

	public boolean isPoly4()
	{
		return poly4;
	}

	public void setPoly4(boolean poly4)
	{
		this.poly4 = poly4;
	}

	public boolean isHill()
	{
		return hill;
	}

	public void setHill(boolean hill)
	{
		this.hill = hill;
	}

	public boolean isPower()
	{
		return power;
	}

	public void setPower(boolean power)
	{
		this.power = power;
	}

	public boolean isConstantVariance()
	{
		return constantVariance;
	}

	public void setConstantVariance(boolean constantVariance)
	{
		this.constantVariance = constantVariance;
	}

	public boolean isFlagHillModel()
	{
		return flagHillModel;
	}

	public void setFlagHillModel(boolean flagHillModel)
	{
		this.flagHillModel = flagHillModel;
	}

	public int getMaxIterations()
	{
		return maxIterations;
	}

	public void setMaxIterations(int maxIterations)
	{
		this.maxIterations = maxIterations;
	}

	public int getNumThreads()
	{
		return numThreads;
	}

	public void setNumThreads(int numThreads)
	{
		this.numThreads = numThreads;
	}

	public int getKillTime()
	{
		return killTime;
	}

	public void setKillTime(int killTime)
	{
		this.killTime = killTime;
	}

	public double getConfidenceLevel()
	{
		return confidenceLevel;
	}

	public void setConfidenceLevel(double confidenceLevel)
	{
		this.confidenceLevel = confidenceLevel;
	}

	public double getpValueCutoff()
	{
		return pValueCutoff;
	}

	public void setpValueCutoff(double pValueCutoff)
	{
		this.pValueCutoff = pValueCutoff;
	}

	public double getModifyBMDFlaggedHill()
	{
		return modifyBMDFlaggedHill;
	}

	public void setModifyBMDFlaggedHill(double modifyBMDFlaggedHill)
	{
		this.modifyBMDFlaggedHill = modifyBMDFlaggedHill;
	}

	public BMRFactor getBMRFactor()
	{
		return BMRFactor;
	}

	public void setBMRFactor(BMRFactor bMRFactor)
	{
		BMRFactor = bMRFactor;
	}

	public RestrictPowerEnum getRestrictPower()
	{
		return restrictPower;
	}

	public void setRestrictPower(RestrictPowerEnum restrictPower)
	{
		this.restrictPower = restrictPower;
	}

	public BestPolyModelTestEnum getBestPolyModelTest()
	{
		return bestPolyModelTest;
	}

	public void setBestPolyModelTest(BestPolyModelTestEnum bestPolyModelTest)
	{
		this.bestPolyModelTest = bestPolyModelTest;
	}

	public FlagHillModelDoseEnum getkParameterLessThan()
	{
		return kParameterLessThan;
	}

	public void setkParameterLessThan(FlagHillModelDoseEnum kParameterLessThan)
	{
		this.kParameterLessThan = kParameterLessThan;
	}

	public BestModelSelectionWithFlaggedHillModelEnum getBestModelWithFlaggedHill()
	{
		return bestModelWithFlaggedHill;
	}

	public void setBestModelWithFlaggedHill(
			BestModelSelectionWithFlaggedHillModelEnum bestModelWithFlaggedHill)
	{
		this.bestModelWithFlaggedHill = bestModelWithFlaggedHill;
	}

	public BestModelSelectionBMDLandBMDU getBestModelSelectionBMDLandBMDU()
	{
		return bestModelSelectionBMDLandBMDU;
	}

	public void setBestModelSelectionBMDLandBMDU(BestModelSelectionBMDLandBMDU bestModelSelectionBMDLandBMDU)
	{
		this.bestModelSelectionBMDLandBMDU = bestModelSelectionBMDLandBMDU;
	}

	public String getBmrType()
	{
		return bmrType;
	}

	public void setBmrType(String bmrType)
	{
		this.bmrType = bmrType;
	}

}
