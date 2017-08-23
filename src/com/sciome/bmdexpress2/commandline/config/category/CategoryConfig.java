package com.sciome.bmdexpress2.commandline.config.category;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = DefinedConfig.class, name = "defined"),
		@Type(value = GOConfig.class, name = "go"), @Type(value = PathwayConfig.class, name = "pathway") })
public abstract class CategoryConfig
{

	// name of bmdanalysis to cateogorize
	private String	inputName;

	// name of output for the analysis
	private String	outputName;

	private Integer	removePromiscuousProbes;
	private Integer	removeBMDGreaterHighDose;

	private Double	bmdPValueCutoff;
	private Double	bmdBMDLRatioMin;
	private Double	bmduBMDRatioMin;
	private Double	bmduBMDLRatioMin;
	private Double	nFoldBelowLowestDose;

	private Integer	identifyConflictingProbeSets;
	private Double	correlationCutoffForConflictingProbeSets;

	public String getInputName()
	{
		return inputName;
	}

	public void setInputName(String inputName)
	{
		this.inputName = inputName;
	}

	public String getOutputName()
	{
		return outputName;
	}

	public void setOutputName(String outputName)
	{
		this.outputName = outputName;
	}

	public Integer getRemovePromiscuousProbes()
	{
		return removePromiscuousProbes;
	}

	public void setRemovePromiscuousProbes(Integer removePromiscuousProbes)
	{
		this.removePromiscuousProbes = removePromiscuousProbes;
	}

	public Integer getRemoveBMDGreaterHighDose()
	{
		return removeBMDGreaterHighDose;
	}

	public void setRemoveBMDGreaterHighDose(Integer removeBMDGreaterHighDose)
	{
		this.removeBMDGreaterHighDose = removeBMDGreaterHighDose;
	}

	public Double getBmdPValueCutoff()
	{
		return bmdPValueCutoff;
	}

	public void setBmdPValueCutoff(Double bmdPValueCutoff)
	{
		this.bmdPValueCutoff = bmdPValueCutoff;
	}

	public Double getBmdBMDLRatioMin()
	{
		return bmdBMDLRatioMin;
	}

	public void setBmdBMDLRatioMin(Double bmdBMDLRatioMin)
	{
		this.bmdBMDLRatioMin = bmdBMDLRatioMin;
	}

	public Double getBmduBMDRatioMin()
	{
		return bmduBMDRatioMin;
	}

	public void setBmduBMDRatioMin(Double bmduBMDRatioMin)
	{
		this.bmduBMDRatioMin = bmduBMDRatioMin;
	}

	public Double getBmduBMDLRatioMin()
	{
		return bmduBMDLRatioMin;
	}

	public void setBmduBMDLRatioMin(Double bmduBMDLRatioMin)
	{
		this.bmduBMDLRatioMin = bmduBMDLRatioMin;
	}

	public Double getnFoldBelowLowestDose()
	{
		return nFoldBelowLowestDose;
	}

	public void setnFoldBelowLowestDose(Double nFoldBelowLowestDose)
	{
		this.nFoldBelowLowestDose = nFoldBelowLowestDose;
	}

	public Integer getIdentifyConflictingProbeSets()
	{
		return identifyConflictingProbeSets;
	}

	public void setIdentifyConflictingProbeSets(Integer identifyConflictingProbeSets)
	{
		this.identifyConflictingProbeSets = identifyConflictingProbeSets;
	}

	public Double getCorrelationCutoffForConflictingProbeSets()
	{
		return correlationCutoffForConflictingProbeSets;
	}

	public void setCorrelationCutoffForConflictingProbeSets(Double correlationCutoffForConflictingProbeSets)
	{
		this.correlationCutoffForConflictingProbeSets = correlationCutoffForConflictingProbeSets;
	}

}
