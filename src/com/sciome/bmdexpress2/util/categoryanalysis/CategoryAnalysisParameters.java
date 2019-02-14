package com.sciome.bmdexpress2.util.categoryanalysis;

import java.util.List;

import com.sciome.bmdexpress2.util.categoryanalysis.defined.DefinedCategoryFileParameters;
import com.sciome.commons.math.httk.calc.calc_analytic_css.Model;
import com.sciome.commons.math.httk.model.Compound;

public class CategoryAnalysisParameters
{

	private DefinedCategoryFileParameters	probeFileParameters;
	private DefinedCategoryFileParameters	categoryFileParameters;

	private String							goCat;
	private int								goTermIdx;

	private String							pathwayDB;

	private boolean							removeBMDGreaterHighDose		= false;
	private boolean							removePromiscuousProbes			= false;
	private boolean							removeBMDPValueLessCuttoff		= false;
	private boolean							removeBMDBMDLRatio				= false;
	private boolean							removeNFoldBelowLowestDose		= false;
	private double							bmdBmdlRatio;
	private double							nFoldbelowLowestDoseValue;
	private double							pValueCutoff;

	private boolean							removeBMDUBMDLRatio				= false;
	private double							bmduBmdlRatio;

	private boolean							removeBMDUBMDRatio				= false;
	private double							bmduBmdRatio;

	private boolean							identifyConflictingProbeSets	= false;
	private double							correlationCutoffConflictingProbeSets;

	private boolean							userFoldChangeFilter			= false;
	private double							maxFoldChange;

	private boolean							userPValueFilter				= false;
	private double							pValue;

	private boolean							userAdjustedPValueFilter		= false;
	private double							adjustedPValue;

	private double							minDose;
	private double							maxDose;
	private double							minPositiveDose;

	private boolean							deduplicateGeneSets				= false;
	
	//IVIVE calculation
	private Compound						compound;
	private List<Model>						models;

	public DefinedCategoryFileParameters getProbeFileParameters()
	{
		return probeFileParameters;
	}

	public void setProbeFileParameters(DefinedCategoryFileParameters probeFileParameters)
	{
		this.probeFileParameters = probeFileParameters;
	}

	public DefinedCategoryFileParameters getCategoryFileParameters()
	{
		return categoryFileParameters;
	}

	public void setCategoryFileParameters(DefinedCategoryFileParameters categoryFileParameters)
	{
		this.categoryFileParameters = categoryFileParameters;
	}

	public String getGoCat()
	{
		return goCat;
	}

	public void setGoCat(String goCat)
	{
		this.goCat = goCat;
	}

	public int getGoTermIdx()
	{
		return goTermIdx;
	}

	public void setGoTermIdx(int goTermIdx)
	{
		this.goTermIdx = goTermIdx;
	}

	public String getPathwayDB()
	{
		return pathwayDB;
	}

	public void setPathwayDB(String pathwayDB)
	{
		this.pathwayDB = pathwayDB;
	}

	public boolean isRemoveBMDGreaterHighDose()
	{
		return removeBMDGreaterHighDose;
	}

	public void setRemoveBMDGreaterHighDose(boolean removeBMDGreaterHighDose)
	{
		this.removeBMDGreaterHighDose = removeBMDGreaterHighDose;
	}

	public boolean isRemoveBMDPValueLessCuttoff()
	{
		return removeBMDPValueLessCuttoff;
	}

	public void setRemoveBMDPValueLessCuttoff(boolean removeBMDPValueLessCuttoff)
	{
		this.removeBMDPValueLessCuttoff = removeBMDPValueLessCuttoff;
	}

	public double getpValueCutoff()
	{
		return pValueCutoff;
	}

	public void setpValueCutoff(double pValueCutoff)
	{
		this.pValueCutoff = pValueCutoff;
	}

	public boolean isIdentifyConflictingProbeSets()
	{
		return identifyConflictingProbeSets;
	}

	public void setIdentifyConflictingProbeSets(boolean identifyConflictingProbeSets)
	{
		this.identifyConflictingProbeSets = identifyConflictingProbeSets;
	}

	public double getCorrelationCutoffConflictingProbeSets()
	{
		return correlationCutoffConflictingProbeSets;
	}

	public void setCorrelationCutoffConflictingProbeSets(double correlationCutoffConflictingProbeSets)
	{
		this.correlationCutoffConflictingProbeSets = correlationCutoffConflictingProbeSets;
	}

	public double getMinDose()
	{
		return minDose;
	}

	public void setMinDose(double minDose)
	{
		this.minDose = minDose;
	}

	public double getMaxDose()
	{
		return maxDose;
	}

	public void setMaxDose(double maxDose)
	{
		this.maxDose = maxDose;
	}

	public boolean isRemoveBMDBMDLRatio()
	{
		return removeBMDBMDLRatio;
	}

	public void setRemoveBMDBMDLRatio(boolean removeBMDBMDLRatio)
	{
		this.removeBMDBMDLRatio = removeBMDBMDLRatio;
	}

	public boolean isRemoveNFoldBelowLowestDose()
	{
		return removeNFoldBelowLowestDose;
	}

	public void setRemoveNFoldBelowLowestDose(boolean removeNFoldBelowLowestDose)
	{
		this.removeNFoldBelowLowestDose = removeNFoldBelowLowestDose;
	}

	public double getBmdBmdlRatio()
	{
		return bmdBmdlRatio;
	}

	public void setBmdBmdlRatio(double bmdBmdlRatio)
	{
		this.bmdBmdlRatio = bmdBmdlRatio;
	}

	public double getnFoldbelowLowestDoseValue()
	{
		return nFoldbelowLowestDoseValue;
	}

	public void setnFoldbelowLowestDoseValue(double nFoldbelowLowestDoseValue)
	{
		this.nFoldbelowLowestDoseValue = nFoldbelowLowestDoseValue;
	}

	public double getMinPositiveDose()
	{
		return minPositiveDose;
	}

	public void setMinPositiveDose(double minPositiveDose)
	{
		this.minPositiveDose = minPositiveDose;
	}

	public void setRemovePromiscuousProbes(boolean selected)
	{
		removePromiscuousProbes = selected;

	}

	public boolean getRemovePromiscuousProbes()
	{
		return removePromiscuousProbes;

	}

	public boolean isRemoveBMDUBMDLRatio()
	{
		return removeBMDUBMDLRatio;
	}

	public void setRemoveBMDUBMDLRatio(boolean removeBMDUBMDLRatio)
	{
		this.removeBMDUBMDLRatio = removeBMDUBMDLRatio;
	}

	public double getBmduBmdlRatio()
	{
		return bmduBmdlRatio;
	}

	public void setBmduBmdlRatio(double bmduBmdlRatio)
	{
		this.bmduBmdlRatio = bmduBmdlRatio;
	}

	public boolean isRemoveBMDUBMDRatio()
	{
		return removeBMDUBMDRatio;
	}

	public void setRemoveBMDUBMDRatio(boolean removeBMDUBMDRatio)
	{
		this.removeBMDUBMDRatio = removeBMDUBMDRatio;
	}

	public double getBmduBmdRatio()
	{
		return bmduBmdRatio;
	}

	public void setBmduBmdRatio(double bmduBmdRatio)
	{
		this.bmduBmdRatio = bmduBmdRatio;
	}

	public boolean isUserFoldChangeFilter()
	{
		return userFoldChangeFilter;
	}

	public void setUserFoldChangeFilter(boolean userFoldChangeFilter)
	{
		this.userFoldChangeFilter = userFoldChangeFilter;
	}

	public double getMaxFoldChange()
	{
		return maxFoldChange;
	}

	public void setMaxFoldChange(double maxFoldChange)
	{
		this.maxFoldChange = maxFoldChange;
	}

	public boolean isUserPValueFilter()
	{
		return userPValueFilter;
	}

	public void setUserPValueFilter(boolean userPValueFilter)
	{
		this.userPValueFilter = userPValueFilter;
	}

	public double getPValue()
	{
		return pValue;
	}

	public void setPValue(double pValue)
	{
		this.pValue = pValue;
	}

	public boolean isUserAdjustedPValueFilter()
	{
		return userAdjustedPValueFilter;
	}

	public void setUserAdjustedPValueFilter(boolean userAdjustedPValueFilter)
	{
		this.userAdjustedPValueFilter = userAdjustedPValueFilter;
	}

	public double getAdjustedPValue()
	{
		return adjustedPValue;
	}

	public void setAdjustedPValue(double adjustedPValue)
	{
		this.adjustedPValue = adjustedPValue;
	}

	public void setDeduplicateGeneSets(boolean selected)
	{
		deduplicateGeneSets = selected;

	}

	public boolean getDeduplicateGeneSets()
	{
		return deduplicateGeneSets;

	}

	public Compound getCompound() {
		return compound;
	}

	public void setCompound(Compound compound) {
		this.compound = compound;
	}

	public List<Model> getModels() {
		return models;
	}

	public void setModels(List<Model> models) {
		this.models = models;
	}
	
	
}
