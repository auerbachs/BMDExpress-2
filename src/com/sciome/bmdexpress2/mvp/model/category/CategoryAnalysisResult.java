package com.sciome.bmdexpress2.mvp.model.category;

import java.awt.Color;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.IGeneContainer;
import com.sciome.bmdexpress2.mvp.model.IMarkable;
import com.sciome.bmdexpress2.mvp.model.category.identifier.CategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.category.identifier.GOCategoryIdentifier;
import com.sciome.bmdexpress2.mvp.model.category.ivive.IVIVEResult;
import com.sciome.bmdexpress2.mvp.model.stat.ProbeStatResult;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;
import com.sciome.bmdexpress2.util.NumberManager;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = GOAnalysisResult.class, name = "go"),
		@Type(value = PathwayAnalysisResult.class, name = "pathway"),
		@Type(value = DefinedCategoryAnalysisResult.class, name = "defined") })
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public abstract class CategoryAnalysisResult extends BMDExpressAnalysisRow
		implements Serializable, IGeneContainer, IMarkable
{
	/**
	 * 
	 */
	private static final long					serialVersionUID				= -3651047352571831492L;

	private Long								id;
	protected CategoryIdentifier				categoryIdentifier;

	private List<ReferenceGeneProbeStatResult>	referenceGeneProbeStatResults;
	private Integer								geneAllCountFromExperiment;
	private Integer								geneAllCount;
	private Integer								geneCountSignificantANOVA;
	private Double								percentage;

	// filter count vars
	private Integer								genesWithBMDLessEqualHighDose;
	private Integer								genesWithBMDpValueGreaterEqualValue;
	private Integer								genesWithBMDBMDLRatioBelowValue;
	private Integer								genesWithBMDUBMDLRatioBelowValue;
	private Integer								genesWithBMDUBMDRatioBelowValue;
	private Integer								genesWithNFoldBelowLowPostiveDoseValue;
	private Integer								genesWithFoldChangeAboveValue;
	private Integer								genesWithPrefilterPValueAboveValue;
	private Integer								genesWithPrefilterAdjustedPValueAboveValue;

	private Integer								genesThatPassedAllFilters;

	private Double								fishersExactLeftPValue;
	private Double								fishersExactRightPValue;
	private Double								fishersExactTwoTailPValue;

	private String								genesWithConflictingProbeSets	= null;

	// bmd/bmdl/bmdu stats
	private Double								bmdMean;
	private Double								bmdMedian;
	private Double								bmdMinimum;
	private Double								bmdSD;
	private Double								bmdWMean;
	private Double								bmdWSD;

	private Double								bmdlMean;
	private Double								bmdlMedian;
	private Double								bmdlMinimum;
	private Double								bmdlSD;
	private Double								bmdlWMean;
	private Double								bmdlWSD;

	private Double								bmduMean;
	private Double								bmduMedian;
	private Double								bmduMinimum;
	private Double								bmduSD;
	private Double								bmduWMean;
	private Double								bmduWSD;

	// percentile fields
	private Double								fifthPercentileIndex;
	private Double								bmdFifthPercentileTotalGenes;

	private Double								tenthPercentileIndex;
	private Double								bmdTenthPercentileTotalGenes;

	private Double								genesUpBMDMean;
	private Double								genesUpBMDMedian;
	private Double								genesUpBMDSD;

	private Double								genesUpBMDLMean;
	private Double								genesUpBMDLMedian;
	private Double								genesUpBMDLSD;

	private Double								genesUpBMDUMean;
	private Double								genesUpBMDUMedian;
	private Double								genesUpBMDUSD;

	private Double								genesDownBMDMean;
	private Double								genesDownBMDMedian;
	private Double								genesDownBMDSD;

	private Double								genesDownBMDLMean;
	private Double								genesDownBMDLMedian;
	private Double								genesDownBMDLSD;

	private Double								genesDownBMDUMean;
	private Double								genesDownBMDUMedian;
	private Double								genesDownBMDUSD;

	// statResult counts
	// could be computed from probestatresult
	private Map<StatResult, Integer>			statResultCounts;

	// row data for the table view.
	protected transient List<Object>			row;

	private transient String					genes;
	private transient String					geneSymbols;

	// converting the object data to row data will require lots of string buffers.
	// let them all use the same object to reduce instantiation
	private transient StringBuffer				stringBuffer					= new StringBuffer();

	// this is calculated and provides a general direction of the dose response curves
	private transient AdverseDirectionEnum		overallDirection;
	private transient Double					percentWithOverallDirectionUP;
	private transient Double					percentWithOverallDirectionDOWN;
	private transient Double					percentWithOverallDirectionConflict;

	// fold change stats
	private transient Double					totalFoldChange;
	private transient Double					meanFoldChange;
	private transient Double					medianFoldChange;
	private transient Double					maxFoldChange;
	private transient Double					minFoldChange;
	private transient Double					stdDevFoldChange;

	// 95% confidence interval stats
	private transient Double					bmdLower95;
	private transient Double					bmdUpper95;
	private transient Double					bmdlLower95;
	private transient Double					bmdlUpper95;
	private transient Double					bmduUpper95;
	private transient Double					bmduLower95;
	
	private IVIVEResult							ivive;

	private StringBuffer getStringBuffer()
	{
		if (this.stringBuffer == null)
			stringBuffer = new StringBuffer();

		return stringBuffer;
	}

	@JsonIgnore
	public Long getID()
	{
		return id;
	}

	public void setID(Long id)
	{
		this.id = id;
	}

	public List<ReferenceGeneProbeStatResult> getReferenceGeneProbeStatResults()
	{
		return referenceGeneProbeStatResults;
	}

	public void setReferenceGeneProbeStatResults(
			List<ReferenceGeneProbeStatResult> referenceGeneProbeStatResults)
	{
		this.referenceGeneProbeStatResults = referenceGeneProbeStatResults;
	}

	@JsonIgnore
	public String getCategoryID()
	{
		return categoryIdentifier.getId();
	}

	@JsonIgnore
	public String getCategoryDescription()
	{
		return categoryIdentifier.getTitle();
	}

	@JsonIgnore
	public Integer getGotermLevel()
	{
		if (categoryIdentifier instanceof GOCategoryIdentifier)
		{
			GOCategoryIdentifier goCatID = (GOCategoryIdentifier) categoryIdentifier;
			try
			{
				return Integer.valueOf(goCatID.getGoLevel());
			}
			catch (Exception e) // this should always be an integer but in case it happens not to be just
								// return null;
			{

			}
		}

		return null;
	}

	@JsonIgnore
	public String getGenes()
	{
		if (genes == null)
			getGenesIds();
		return genes;
	}

	@JsonIgnore
	public String getGeneSymbols()
	{
		if (geneSymbols == null)
			getGeneSymbolsPrivate();
		return geneSymbols;
	}

	@JsonIgnore
	public Double getNegLogOfFishers2Tail()
	{
		if (fishersExactTwoTailPValue == null)
			return null;
		return NumberManager.negLog10(fishersExactTwoTailPValue);
	}

	public Integer getGeneCountSignificantANOVA()
	{
		return geneCountSignificantANOVA;
	}

	public void setGeneCountSignificantANOVA(Integer geneCountSignificantANOVA)
	{
		this.geneCountSignificantANOVA = geneCountSignificantANOVA;
	}

	public Integer getGenesWithBMDBMDLRatioBelowValue()
	{
		return genesWithBMDBMDLRatioBelowValue;
	}

	public void setGenesWithBMDBMDLRatioBelowValue(Integer genesWithBMDBMDLRatioBelowValue)
	{
		this.genesWithBMDBMDLRatioBelowValue = genesWithBMDBMDLRatioBelowValue;
	}

	public void setGenesWithBMDUBMDLRatioBelowValue(Integer value)
	{
		this.genesWithBMDUBMDLRatioBelowValue = value;
	}

	public Integer getGenesWithBMDUBMDLRatioBelowValue()
	{
		return genesWithBMDUBMDLRatioBelowValue;
	}

	public void setGenesWithBMDUBMDRatioBelowValue(Integer value)
	{
		this.genesWithBMDUBMDRatioBelowValue = value;
	}

	public Integer getGenesWithBMDUBMDRatioBelowValue()
	{
		return genesWithBMDUBMDRatioBelowValue;
	}

	public Integer getGenesWithNFoldBelowLowPostiveDoseValue()
	{
		return genesWithNFoldBelowLowPostiveDoseValue;
	}

	public void setGenesWithNFoldBelowLowPostiveDoseValue(Integer genesWithNFoldBelowLowPostiveDoseValue)
	{
		this.genesWithNFoldBelowLowPostiveDoseValue = genesWithNFoldBelowLowPostiveDoseValue;
	}

	public Integer getGenesWithFoldChangeAboveValue()
	{
		return genesWithFoldChangeAboveValue;
	}

	public void setGenesWithFoldChangeAboveValue(Integer genesWithFoldChangeAboveValue)
	{
		this.genesWithFoldChangeAboveValue = genesWithFoldChangeAboveValue;
	}

	public Integer getGenesWithPrefilterPValueAboveValue()
	{
		return genesWithPrefilterPValueAboveValue;
	}

	public void setGenesWithPrefilterPValueAboveValue(Integer genesWithPValueAboveValue)
	{
		this.genesWithPrefilterPValueAboveValue = genesWithPValueAboveValue;
	}

	public Integer getGenesWithPrefilterAdjustedPValueAboveValue()
	{
		return genesWithPrefilterAdjustedPValueAboveValue;
	}

	public void setGenesWithPrefilterAdjustedPValueAboveValue(Integer genesWithAdjustedPValueAboveValue)
	{
		this.genesWithPrefilterAdjustedPValueAboveValue = genesWithAdjustedPValueAboveValue;
	}

	public Double getPercentage()
	{
		return percentage;
	}

	public Integer getGeneAllCount()
	{
		return geneAllCount;
	}

	public void setGeneAllCount(Integer geneAllCount)
	{
		this.geneAllCount = geneAllCount;
	}

	public Integer getGeneAllCountFromExperiment()
	{
		return geneAllCountFromExperiment;
	}

	public void setGeneAllCountFromExperiment(Integer geneAllCountFromExperiment)
	{
		this.geneAllCountFromExperiment = geneAllCountFromExperiment;
	}

	public void setPercentage(Double percentage)
	{
		this.percentage = percentage;
	}

	public Double getBmdMean()
	{
		return bmdMean;
	}

	public void setBmdMean(Double bmdMean)
	{
		this.bmdMean = bmdMean;
	}

	public Double getBmdMedian()
	{
		return bmdMedian;
	}

	public void setBmdMedian(Double bmdMedian)
	{
		this.bmdMedian = bmdMedian;
	}

	public Double getBmdMinimum()
	{
		return bmdMinimum;
	}

	public void setBmdMinimum(Double bmdMinimum)
	{
		this.bmdMinimum = bmdMinimum;
	}

	@JsonIgnore
	public Double getBMDMaximum()
	{
		Double currentMaxBMD = null;
		if (referenceGeneProbeStatResults == null)
			return null;
		for (ReferenceGeneProbeStatResult result : referenceGeneProbeStatResults)
		{
			for (ProbeStatResult probeStatResult : result.getProbeStatResults())
			{
				if (currentMaxBMD == null || (probeStatResult.getBestBMD() != null
						&& probeStatResult.getBestBMD() > currentMaxBMD))
				{
					currentMaxBMD = probeStatResult.getBestBMD();
				}
			}
		}
		return currentMaxBMD;
	}

	@JsonIgnore
	public Double getBMDLMaximum()
	{
		Double currentMaxBMDL = null;
		if (referenceGeneProbeStatResults == null)
			return null;
		for (ReferenceGeneProbeStatResult result : referenceGeneProbeStatResults)
		{
			for (ProbeStatResult probeStatResult : result.getProbeStatResults())
			{
				if (currentMaxBMDL == null || (probeStatResult.getBestBMDL() != null
						&& probeStatResult.getBestBMDL() > currentMaxBMDL))
				{
					currentMaxBMDL = probeStatResult.getBestBMDL();
				}
			}
		}
		return currentMaxBMDL;
	}

	public Double getBmdSD()
	{
		if (bmdSD != null && bmdSD.isNaN())
			return null;
		return bmdSD;
	}

	public void setBmdSD(Double bmdSD)
	{
		if (bmdSD != null && bmdSD.isNaN())
			this.bmdSD = null;
		else
			this.bmdSD = bmdSD;
	}

	public Double getBmdWMean()
	{
		return bmdWMean;
	}

	public void setBmdWMean(Double bmdWMean)
	{
		this.bmdWMean = bmdWMean;
	}

	public Double getBmdWSD()
	{
		if (bmdWSD != null && bmdWSD.isNaN())
		{
			return null;
		}
		return bmdWSD;
	}

	public void setBmdWSD(Double bmdWSD)
	{
		if (bmdWSD != null && bmdWSD.isNaN())
			this.bmdWSD = null;
		else
			this.bmdWSD = bmdWSD;
	}

	@JsonIgnore
	public String getChartableDataLabel()
	{
		return this.getCategoryIdentifier().getId();
	}

	public Double getBmduMean()
	{
		return bmduMean;
	}

	public void setBmduMean(Double bmduMean)
	{
		this.bmduMean = bmduMean;
	}

	public Double getBmduMedian()
	{
		return bmduMedian;
	}

	public void setBmduMedian(Double bmduMedian)
	{
		this.bmduMedian = bmduMedian;
	}

	public Double getBmduMinimum()
	{
		return bmduMinimum;
	}

	public void setBmduMinimum(Double bmduMinimum)
	{
		this.bmduMinimum = bmduMinimum;
	}

	public Double getBmduSD()
	{
		if (bmduSD != null && bmduSD.isNaN())
		{
			return null;
		}
		return bmduSD;
	}

	public void setBmduSD(Double bmduSD)
	{
		if (bmduSD != null && bmduSD.isNaN())
			this.bmduSD = null;
		else
			this.bmduSD = bmduSD;
	}

	public Double getBmduWMean()
	{
		return bmduWMean;
	}

	public void setBmduWMean(Double bmduWMean)
	{
		this.bmduWMean = bmduWMean;
	}

	public Double getBmduWSD()
	{
		if (bmduWSD != null && bmduWSD.isNaN())
			return null;
		return bmduWSD;
	}

	public void setBmduWSD(Double bmduWSD)
	{
		if (bmduWSD != null && bmduWSD.isNaN())
			this.bmduWSD = null;
		else
			this.bmduWSD = bmduWSD;
	}

	public Double getBmdlMean()
	{
		return bmdlMean;
	}

	public void setBmdlMean(Double bmdlMean)
	{
		this.bmdlMean = bmdlMean;
	}

	public Double getBmdlMedian()
	{
		return bmdlMedian;
	}

	public void setBmdlMedian(Double bmdlMedian)
	{
		this.bmdlMedian = bmdlMedian;
	}

	public Double getBmdlMinimum()
	{
		return bmdlMinimum;
	}

	public void setBmdlMinimum(Double bmdlMinimum)
	{
		this.bmdlMinimum = bmdlMinimum;
	}

	public Double getBmdlSD()
	{
		if (bmdlSD != null && bmdlSD.isNaN())
		{
			return null;
		}
		return bmdlSD;
	}

	public void setBmdlSD(Double bmdlSD)
	{
		if (bmdlSD != null && bmdlSD.isNaN())
			this.bmdlSD = null;
		else
			this.bmdlSD = bmdlSD;
	}

	public Double getBmdlWMean()
	{
		return bmdlWMean;
	}

	public void setBmdlWMean(Double bmdlWMean)
	{
		this.bmdlWMean = bmdlWMean;
	}

	public Double getBmdlWSD()
	{
		if (bmdlWSD != null && bmdlWSD.isNaN())
			return null;
		return bmdlWSD;
	}

	public void setBmdlWSD(Double bmdlWSD)
	{
		if (bmdlWSD != null && bmdlWSD.isNaN())
			this.bmdlWSD = null;
		else
			this.bmdlWSD = bmdlWSD;
	}

	public Double getFifthPercentileIndex()
	{
		return fifthPercentileIndex;
	}

	public void setFifthPercentileIndex(Double fifthPercentileIndex)
	{
		this.fifthPercentileIndex = fifthPercentileIndex;
	}

	public Double getBmdFifthPercentileTotalGenes()
	{
		return bmdFifthPercentileTotalGenes;
	}

	public void setBmdFifthPercentileTotalGenes(Double bmdFifthPercentileTotalGenes)
	{
		this.bmdFifthPercentileTotalGenes = bmdFifthPercentileTotalGenes;
	}

	public Double getTenthPercentileIndex()
	{
		return tenthPercentileIndex;
	}

	public void setTenthPercentileIndex(Double tenthPercentileIndex)
	{
		this.tenthPercentileIndex = tenthPercentileIndex;
	}

	public Double getBmdTenthPercentileTotalGenes()
	{
		return bmdTenthPercentileTotalGenes;
	}

	public void setBmdTenthPercentileTotalGenes(Double bmdTenthPercentileTotalGenes)
	{
		this.bmdTenthPercentileTotalGenes = bmdTenthPercentileTotalGenes;
	}

	public Double getGenesUpBMDMean()
	{
		return genesUpBMDMean;
	}

	public void setGenesUpBMDMean(Double genesUpBMDMean)
	{
		this.genesUpBMDMean = genesUpBMDMean;
	}

	public Double getGenesUpBMDMedian()
	{
		return genesUpBMDMedian;
	}

	public void setGenesUpBMDMedian(Double genesUpBMDMedian)
	{
		this.genesUpBMDMedian = genesUpBMDMedian;
	}

	public Double getGenesUpBMDSD()
	{
		if (genesUpBMDSD != null && genesUpBMDSD.isNaN())
			return null;
		return genesUpBMDSD;
	}

	public void setGenesUpBMDSD(Double genesUpBMDSD)
	{
		this.genesUpBMDSD = genesUpBMDSD;
	}

	public Double getGenesUpBMDLMean()
	{
		return genesUpBMDLMean;
	}

	public void setGenesUpBMDLMean(Double genesUpBMDLMean)
	{
		this.genesUpBMDLMean = genesUpBMDLMean;
	}

	public Double getGenesUpBMDLMedian()
	{
		return genesUpBMDLMedian;
	}

	public void setGenesUpBMDLMedian(Double genesUpBMDLMedian)
	{
		this.genesUpBMDLMedian = genesUpBMDLMedian;
	}

	public Double getGenesUpBMDLSD()
	{
		if (genesUpBMDLSD != null && genesUpBMDLSD.isNaN())
			return null;
		return genesUpBMDLSD;
	}

	public void setGenesUpBMDLSD(Double genesUpBMDLSD)
	{
		this.genesUpBMDLSD = genesUpBMDLSD;
	}

	public Double getGenesUpBMDUMean()
	{
		return genesUpBMDUMean;
	}

	public void setGenesUpBMDUMean(Double genesUpBMDUMean)
	{
		this.genesUpBMDUMean = genesUpBMDUMean;
	}

	public Double getGenesUpBMDUMedian()
	{
		return genesUpBMDUMedian;
	}

	public void setGenesUpBMDUMedian(Double genesUpBMDUMedian)
	{
		this.genesUpBMDUMedian = genesUpBMDUMedian;
	}

	public Double getGenesUpBMDUSD()
	{
		if (genesUpBMDUSD != null && genesUpBMDUSD.isNaN())
			return null;
		return genesUpBMDUSD;
	}

	public void setGenesUpBMDUSD(Double genesUpBMDUSD)
	{
		this.genesUpBMDUSD = genesUpBMDUSD;
	}

	public Double getGenesDownBMDMean()
	{
		return genesDownBMDMean;
	}

	public void setGenesDownBMDMean(Double genesDownBMDMean)
	{
		this.genesDownBMDMean = genesDownBMDMean;
	}

	public Double getGenesDownBMDMedian()
	{
		return genesDownBMDMedian;
	}

	public void setGenesDownBMDMedian(Double genesDownBMDMedian)
	{
		this.genesDownBMDMedian = genesDownBMDMedian;
	}

	public Double getGenesDownBMDSD()
	{
		if (genesDownBMDSD != null && genesDownBMDSD.isNaN())
			return null;
		return genesDownBMDSD;
	}

	public void setGenesDownBMDSD(Double genesDownBMDSD)
	{
		this.genesDownBMDSD = genesDownBMDSD;
	}

	public Double getGenesDownBMDLMean()
	{
		return genesDownBMDLMean;
	}

	public void setGenesDownBMDLMean(Double genesDownBMDLMean)
	{
		this.genesDownBMDLMean = genesDownBMDLMean;
	}

	public Double getGenesDownBMDLMedian()
	{
		return genesDownBMDLMedian;
	}

	public void setGenesDownBMDLMedian(Double genesDownBMDLMedian)
	{
		this.genesDownBMDLMedian = genesDownBMDLMedian;
	}

	public Double getGenesDownBMDLSD()
	{
		if (genesDownBMDLSD != null && genesDownBMDLSD.isNaN())
			return null;
		return genesDownBMDLSD;
	}

	public void setGenesDownBMDLSD(Double genesDownBMDLSD)
	{
		this.genesDownBMDLSD = genesDownBMDLSD;
	}

	public Double getGenesDownBMDUMean()
	{
		return genesDownBMDUMean;
	}

	public void setGenesDownBMDUMean(Double genesDownBMDUMean)
	{
		this.genesDownBMDUMean = genesDownBMDUMean;
	}

	public Double getGenesDownBMDUMedian()
	{
		return genesDownBMDUMedian;
	}

	public void setGenesDownBMDUMedian(Double genesDownBMDUMedian)
	{
		this.genesDownBMDUMedian = genesDownBMDUMedian;
	}

	public Double getGenesDownBMDUSD()
	{
		if (genesDownBMDUSD != null && genesDownBMDUSD.isNaN())
			return null;
		return genesDownBMDUSD;
	}

	public void setGenesDownBMDUSD(Double genesDownBMDUSD)
	{
		this.genesDownBMDUSD = genesDownBMDUSD;
	}

	@JsonIgnore
	public Map<StatResult, Integer> getStatResultCounts()
	{
		return statResultCounts;
	}

	public void setStatResultCounts(Map<StatResult, Integer> statResultCounts)
	{
		this.statResultCounts = statResultCounts;
	}

	public String getGenesWithConflictingProbeSets()
	{
		return genesWithConflictingProbeSets;
	}

	public void setGenesWithConflictingProbeSets(String genesWithConflictingProbeSets)
	{
		this.genesWithConflictingProbeSets = genesWithConflictingProbeSets;
	}

	public Integer getGenesWithBMDLessEqualHighDose()
	{
		return genesWithBMDLessEqualHighDose;
	}

	public void setGenesWithBMDLessEqualHighDose(Integer genesWithBMDLessEqualHighDose)
	{
		this.genesWithBMDLessEqualHighDose = genesWithBMDLessEqualHighDose;
	}

	public Integer getGenesWithBMDpValueGreaterEqualValue()
	{
		return genesWithBMDpValueGreaterEqualValue;
	}

	public void setGenesWithBMDpValueGreaterEqualValue(Integer genesWithBMDpValueGreaterEqualValue)
	{
		this.genesWithBMDpValueGreaterEqualValue = genesWithBMDpValueGreaterEqualValue;
	}

	public Double getFishersExactLeftPValue()
	{
		return fishersExactLeftPValue;
	}

	public void setFishersExactLeftPValue(Double fishersExactLeftPValue)
	{
		this.fishersExactLeftPValue = fishersExactLeftPValue;
	}

	public Double getFishersExactRightPValue()
	{
		return fishersExactRightPValue;
	}

	public void setFishersExactRightPValue(Double fishersExactRightPValue)
	{
		this.fishersExactRightPValue = fishersExactRightPValue;
	}

	public Double getFishersExactTwoTailPValue()
	{
		return fishersExactTwoTailPValue;
	}

	public void setFishersExactTwoTailPValue(Double fishersExactTwoTailPValue)
	{
		this.fishersExactTwoTailPValue = fishersExactTwoTailPValue;
	}
	
	public IVIVEResult getIvive() {
		return ivive;
	}

	public void setIvive(IVIVEResult ivive) {
		this.ivive = ivive;
	}

	// add in the ratios filters
	@JsonIgnore
	public Double getBMDUdivBMDLMEDIAN()
	{
		if (bmduMedian == null)
			return null;
		if (bmduMedian.equals(0.0))
			return null;
		return this.bmduMedian / this.bmdlMedian;
	}

	@JsonIgnore
	public Double getBMDdivBMDLMEDIAN()
	{
		if (bmduMedian == null)
			return null;

		if (bmdMedian.equals(0.0))
			return null;
		return this.bmdMedian / this.bmdlMedian;
	}

	@JsonIgnore
	public Double getBMDUdivBMDMEDIAN()
	{
		if (bmduMedian == null)
			return null;
		if (bmduMedian.equals(0.0))
			return null;
		return this.bmduMedian / this.bmdMedian;
	}

	@JsonIgnore
	public Double getBMDUdivBMDLMEAN()
	{
		if (bmduMean == null)
			return null;
		if (bmduMean.equals(0.0))
			return null;
		return this.bmduMean / this.bmdlMean;
	}

	@JsonIgnore
	public Double getBMDdivBMDLMEAN()
	{
		if (bmduMean == null)
			return null;
		if (bmdMean.equals(0.0))
			return null;
		return this.bmdMean / this.bmdlMean;
	}

	@JsonIgnore
	public Double getBMDUdivBMDMEAN()
	{
		if (bmduMean == null)
			return null;
		if (bmduMean.equals(0.0))
			return null;
		return this.bmduMean / this.bmdMean;
	}

	@Override
	@JsonIgnore
	public List<Object> getRow()
	{
		if (row == null || row.size() == 0)
			createRowData();
		return row;
	}

	public CategoryIdentifier getCategoryIdentifier()
	{
		return categoryIdentifier;
	}

	public void setCategoryIdentifier(CategoryIdentifier categoryIdentifier)
	{
		this.categoryIdentifier = categoryIdentifier;
	}

	public List<String> generateColumnHeader()
	{
		List<String> headers = new ArrayList<>();

		headers.add("All Genes (Expression Data)");
		headers.add("All Genes (Platform)");
		headers.add("Input Genes");
		if (genesWithBMDLessEqualHighDose != null)
		{
			headers.add("Genes with BMD <= Highest Dose");
		}
		if (genesWithBMDpValueGreaterEqualValue != null)
		{
			headers.add("Genes with BMD p-Value >= ");
		}

		if (genesWithBMDBMDLRatioBelowValue != null)
		{
			headers.add("Genes with BMD/BMDL <= ");
		}

		if (genesWithBMDUBMDRatioBelowValue != null)
		{
			headers.add("Genes with BMDU/BMD <= ");
		}

		if (genesWithBMDUBMDLRatioBelowValue != null)
		{
			headers.add("Genes with BMDU/BMDL <= ");
		}

		if (genesWithNFoldBelowLowPostiveDoseValue != null)
		{
			headers.add("Genes with BMD <= N-Fold Lowest Positive Dose ");
		}

		if (genesWithFoldChangeAboveValue != null)
			headers.add("Genes with max Fold Change >=");

		if (genesWithPrefilterPValueAboveValue != null)
			headers.add("Genes with Prefilter P-Value <=");

		if (genesWithPrefilterAdjustedPValueAboveValue != null)
			headers.add("Genes with Prefilter Adjusted P-Value <=");

		headers.add("Genes That Passed All Filters");

		headers.add(CategoryAnalysisResults.FISHERS_LEFT);
		headers.add(CategoryAnalysisResults.FISHERS_RIGHT);
		headers.add(CategoryAnalysisResults.FISHERS_TWO_TAIL);

		headers.add("Percentage");
		headers.add("Entrez Gene IDs");
		headers.add("Gene Symbols");
		headers.add("Probe IDs");

		if (genesWithConflictingProbeSets != null)
		{
			headers.add("Genes with Conflicting Probesets");
		}

		headers.add(CategoryAnalysisResults.BMD_MEAN);
		headers.add(CategoryAnalysisResults.BMD_MEDIAN);
		headers.add(CategoryAnalysisResults.BMD_MINIUMUM);
		headers.add(CategoryAnalysisResults.BMD_SD);
		headers.add("BMD wMean");
		headers.add("BMD wSD");

		headers.add(CategoryAnalysisResults.BMDL_MEAN);
		headers.add(CategoryAnalysisResults.BMDL_MEDIAN);
		headers.add(CategoryAnalysisResults.BMDL_MINIUMUM);
		headers.add(CategoryAnalysisResults.BMDL_SD);
		headers.add("BMDL wMean");
		headers.add("BMDL wSD");

		headers.add(CategoryAnalysisResults.BMDU_MEAN);
		headers.add(CategoryAnalysisResults.BMDU_MEDIAN);
		headers.add(CategoryAnalysisResults.BMDU_MINIUMUM);
		headers.add(CategoryAnalysisResults.BMDU_SD);
		headers.add("BMDU wMean");
		headers.add("BMDU wSD");

		headers.add("5th Percentile Index");
		headers.add("BMD at 5th Percentile of Total Genes");
		headers.add("10th Percentile Index");
		headers.add("BMD at 10th Percentile of Total Genes");

		headers.add("BMD List");
		headers.add("BMDL List");
		headers.add("BMDU List");

		headers.add("Probes with Adverse Direction Up");
		headers.add("Probes with Adverse Direction Down");

		headers.add("Genes with Adverse Direction Up");
		headers.add("Genes Up List");
		headers.add("Genes Up Probes List");
		headers.add("Genes Up BMD Mean");
		headers.add("Genes Up BMD Median");
		headers.add("Genes Up SD");
		headers.add("Genes Up BMDL Mean");
		headers.add("Genes Up BMDL Median");
		headers.add("Genes Up BMDL SD");
		headers.add("Genes Up BMDU Mean");
		headers.add("Genes Up BMDU Median");
		headers.add("Genes Up BMDU SD");
		headers.add("BMD list (up)");
		headers.add("BMDL List (up)");
		headers.add("BMDU List (up)");

		headers.add("Genes with Adverse Direction Down");
		headers.add("Genes Down List");
		headers.add("Genes Down Probes List");
		headers.add("Genes Down BMD Mean");
		headers.add("Genes Down BMD Median");
		headers.add("Genes Down SD");
		headers.add("Genes Down BMDL Mean");
		headers.add("Genes Down BMDL Median");
		headers.add("Genes Down BMDL SD");
		headers.add("Genes Down BMDU Mean");
		headers.add("Genes Down BMDU Median");
		headers.add("Genes Down BMDU SD");
		headers.add("BMD list (down)");
		headers.add("BMDL List (down)");
		headers.add("BMDU List (down)");

		headers.add("Genes with Adverse Conflict Count");
		headers.add("Genes Conflict List");
		headers.add("Genes Conflict Probes List");
		headers.add("BMD list (Conflict)");
		headers.add("BMDL list (Conflict)");
		headers.add("BMDU list (Conflict)");
		headers.add("Model Counts");
		
		//IVIVE header values
		headers.add("ThreeCompSS BMD Mean Dose");
		headers.add("ThreeCompSS BMDL Mean Dose");
		headers.add("ThreeCompSS BMDU Mean Dose");
		
		headers.add("ThreeCompSS BMD Median Dose");
		headers.add("ThreeCompSS BMDL Median Dose");
		headers.add("ThreeCompSS BMDU Median Dose");
		
		headers.add("ThreeCompSS BMD Minimum Dose");
		headers.add("ThreeCompSS BMDL Minimum Dose");
		headers.add("ThreeCompSS BMDU Minimum Dose");

		return headers;
	}

	protected void createRowData()
	{
		stringBuffer = getStringBuffer();
		if (row != null)
			return;
		row = new ArrayList<>();
		row.add(this.geneAllCountFromExperiment);
		row.add(this.geneAllCount);
		row.add(this.geneCountSignificantANOVA);
		if (genesWithBMDLessEqualHighDose != null)
			row.add(this.genesWithBMDLessEqualHighDose);
		if (genesWithBMDpValueGreaterEqualValue != null)
			row.add(this.genesWithBMDpValueGreaterEqualValue);
		if (genesWithBMDBMDLRatioBelowValue != null)
			row.add(this.genesWithBMDBMDLRatioBelowValue);
		if (genesWithBMDUBMDRatioBelowValue != null)
			row.add(this.genesWithBMDUBMDRatioBelowValue);
		if (genesWithBMDUBMDLRatioBelowValue != null)
			row.add(this.genesWithBMDUBMDLRatioBelowValue);

		if (genesWithNFoldBelowLowPostiveDoseValue != null)
			row.add(this.genesWithNFoldBelowLowPostiveDoseValue);

		if (genesWithFoldChangeAboveValue != null)
			row.add(genesWithFoldChangeAboveValue);

		if (genesWithPrefilterPValueAboveValue != null)
			row.add(genesWithPrefilterPValueAboveValue);

		if (genesWithPrefilterAdjustedPValueAboveValue != null)
			row.add(genesWithPrefilterAdjustedPValueAboveValue);

		row.add(getGenesThatPassedAllFilters());
		row.add(this.fishersExactLeftPValue);
		row.add(this.fishersExactRightPValue);
		row.add(this.fishersExactTwoTailPValue);

		row.add(this.percentage);
		row.add(getGenesIds());
		row.add(getGeneSymbolsPrivate());
		row.add(getProbeIds());

		if (genesWithConflictingProbeSets != null)
			row.add(grabGenesWithConflictingProbeSets());

		row.add(this.bmdMean);
		row.add(this.bmdMedian);
		row.add(this.bmdMinimum);
		row.add(this.getBmdSD());
		row.add(this.bmdWMean);
		row.add(this.getBmdWSD());

		row.add(this.bmdlMean);
		row.add(this.bmdlMedian);
		row.add(this.bmdlMinimum);
		row.add(this.getBmdlSD());
		row.add(this.bmdlWMean);
		row.add(this.getBmdlWSD());

		row.add(this.bmduMean);
		row.add(this.bmduMedian);
		row.add(this.bmduMinimum);
		row.add(this.getBmduSD());
		row.add(this.bmduWMean);
		row.add(this.getBmduWSD());

		row.add(this.fifthPercentileIndex);
		row.add(this.bmdFifthPercentileTotalGenes);
		row.add(this.tenthPercentileIndex);
		row.add(this.bmdTenthPercentileTotalGenes);

		row.add(getBMDList());
		row.add(getBMDLList());
		row.add(getBMDUList());

		row.add(getProbesAdversUpCount());
		row.add(getProbesAdverseDownCount());

		row.add(getGenesAdverseUpCount());
		row.add(getGenesUp());
		row.add(getProbesUp());
		row.add(this.genesUpBMDMean);
		row.add(this.genesUpBMDMedian);
		row.add(this.getGenesUpBMDSD());
		row.add(this.genesUpBMDLMean);
		row.add(this.genesUpBMDLMedian);
		row.add(this.getGenesUpBMDLSD());
		row.add(this.genesUpBMDUMean);
		row.add(this.genesUpBMDUMedian);
		row.add(this.getGenesUpBMDUSD());
		row.add(getBMDUp());
		row.add(getBMDLUp());
		row.add(getBMDUUp());

		row.add(getGenesAdverseDownCount());
		row.add(getGenesDown());
		row.add(getProbesDown());
		row.add((this.genesDownBMDMean));
		row.add((this.genesDownBMDMedian));
		row.add((this.getGenesDownBMDSD()));
		row.add((this.genesDownBMDLMean));
		row.add((this.genesDownBMDLMedian));
		row.add((this.getGenesDownBMDLSD()));
		row.add((this.genesDownBMDUMean));
		row.add((this.genesDownBMDUMedian));
		row.add((this.getGenesDownBMDUSD()));
		row.add(getBMDDown());
		row.add(getBMDLDown());
		row.add(getBMDUDown());

		row.add((getAdverseConflictCount()));
		row.add(getGenesConflictList());
		row.add(getProbesConflictList());
		row.add(getBMDConflictList());
		row.add(getBMDLConflictList());
		row.add(getBMDUConflictList());
		row.add(calculateStatResultCounts());

		// calculate fold change stats for this row,
		// then add them
		calculateFoldChangeStats();

		row.add(this.meanFoldChange);
		row.add(this.totalFoldChange);
		row.add(this.minFoldChange);
		row.add(this.maxFoldChange);
		row.add(this.stdDevFoldChange);
		row.add(this.medianFoldChange);

		// calculate 95% confidence intervals for this
		// row and then ad them.
		calculate95ConfidenceIntervals();

		row.add(this.bmdLower95);
		row.add(this.bmdUpper95);
		row.add(this.bmdlLower95);
		row.add(this.bmdlUpper95);
		row.add(this.bmduLower95);
		row.add(this.bmduUpper95);

		// calculate the overall adverse direction of this pathway
		calculateOverAllDirection();

		row.add(this.overallDirection);
		row.add(this.percentWithOverallDirectionUP);
		row.add(this.percentWithOverallDirectionDOWN);
		row.add(this.percentWithOverallDirectionConflict);
		
		//IVIVE values
		row.add(this.getIvive().getBmdMeanDose());
		row.add(this.getIvive().getBmdlMeanDose());
		row.add(this.getIvive().getBmduMeanDose());
		
		row.add(this.getIvive().getBmdMedianDose());
		row.add(this.getIvive().getBmdlMedianDose());
		row.add(this.getIvive().getBmduMedianDose());
		
		row.add(this.getIvive().getBmdMinimumDose());
		row.add(this.getIvive().getBmdlMinimumDose());
		row.add(this.getIvive().getBmduMinimumDose());
	}

	public void setGenesThatPassedAllFilters(Integer number)
	{
		genesThatPassedAllFilters = number;
	}

	public Integer getGenesThatPassedAllFilters()
	{

		if (genesThatPassedAllFilters != null)
			return genesThatPassedAllFilters;

		return null;
	}

	private String grabGenesWithConflictingProbeSets()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}

		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getConflictMinCorrelation() == null)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			stringBuffer.append(ref.getReferenceGene().getId());
			stringBuffer.append("(");
			stringBuffer.append(ref.getConflictMinCorrelation());
			stringBuffer.append(")");

		}
		return stringBuffer.toString();
	}

	private String getBMDLConflictList()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.CONFLICT)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{

				if (i > 0)
					stringBuffer.append(",");
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
				{
					stringBuffer.append(ref.getProbeStatResults().get(i).getBestStatResult().getBMDL());
				}
				else
					stringBuffer.append("none");

			}

		}
		return stringBuffer.toString();
	}

	private String getBMDUConflictList()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.CONFLICT)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{

				if (i > 0)
					stringBuffer.append(",");
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
				{
					stringBuffer.append(ref.getProbeStatResults().get(i).getBestStatResult().getBMDU());
				}
				else
					stringBuffer.append("none");

			}

		}
		return stringBuffer.toString();
	}

	private String getBMDConflictList()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{

			if (ref.getAdverseDirection() != AdverseDirectionEnum.CONFLICT)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{

				if (i > 0)
					stringBuffer.append(",");
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
				{
					stringBuffer.append(ref.getProbeStatResults().get(i).getBestStatResult().getBMD());
				}
				else
					stringBuffer.append("none");

			}

		}
		return stringBuffer.toString();
	}

	private String getProbesConflictList()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.CONFLICT)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (i > 0)
					stringBuffer.append(",");
				stringBuffer.append(ref.getProbeStatResults().get(i).getProbeResponse().getProbe().getId());

			}

		}
		return stringBuffer.toString();
	}

	private String getGenesConflictList()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.CONFLICT)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			stringBuffer.append(ref.getReferenceGene().getId());

		}
		return stringBuffer.toString();
	}

	private Integer getAdverseConflictCount()
	{
		Integer count = 0;
		if (referenceGeneProbeStatResults == null)
		{
			return 0;
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() == AdverseDirectionEnum.CONFLICT)
			{
				count++;
			}

		}
		return count;
	}

	private String getBMDLDown()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.DOWN)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			int size = ref.getProbeStatResults().size();
			double total = 0.0;
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
					total += ref.getProbeStatResults().get(i).getBestStatResult().getBMDL();
				else
					size--;

			}
			if (size > 0)
				stringBuffer.append(total / size);
			else
				stringBuffer.append("NA");

		}
		return stringBuffer.toString();
	}

	private String getBMDUDown()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.DOWN)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			int size = ref.getProbeStatResults().size();
			double total = 0.0;
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
					total += ref.getProbeStatResults().get(i).getBestStatResult().getBMDU();
				else
					size--;

			}
			if (size > 0)
				stringBuffer.append(total / size);
			else
				stringBuffer.append("NA");

		}
		return stringBuffer.toString();
	}

	private String getBMDDown()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.DOWN)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			double total = 0.0;
			int size = ref.getProbeStatResults().size();
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
					total += ref.getProbeStatResults().get(i).getBestStatResult().getBMD();
				else
					size--;

			}
			if (size > 0)
				stringBuffer.append(total / size);
			else
				stringBuffer.append("NA");

		}
		return stringBuffer.toString();
	}

	private String getProbesDown()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.DOWN)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (i > 0)
					stringBuffer.append(",");
				stringBuffer.append(ref.getProbeStatResults().get(i).getProbeResponse().getProbe().getId());

			}

		}
		return stringBuffer.toString();
	}

	private String getGenesDown()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.DOWN)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			stringBuffer.append(ref.getReferenceGene().getId());

		}
		return stringBuffer.toString();
	}

	private Integer getGenesAdverseDownCount()
	{
		Integer count = 0;
		if (referenceGeneProbeStatResults == null)
		{
			return 0;
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() == AdverseDirectionEnum.DOWN)
			{
				count++;
			}

		}
		return count;
	}

	private String getBMDLUp()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.UP)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			double total = 0.0;
			int size = ref.getProbeStatResults().size();
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
					total += ref.getProbeStatResults().get(i).getBestStatResult().getBMDL();
				else
					size--;

			}
			if (size > 0)
				stringBuffer.append(total / size);
			else
				stringBuffer.append("NA");

		}
		return stringBuffer.toString();
	}

	private String getBMDUUp()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.UP)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			double total = 0.0;
			int size = ref.getProbeStatResults().size();
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
					total += ref.getProbeStatResults().get(i).getBestStatResult().getBMDU();
				else
					size--;

			}
			if (size > 0)
				stringBuffer.append(total / size);
			else
				stringBuffer.append("NA");

		}
		return stringBuffer.toString();
	}

	private String getBMDUp()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.UP)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			double total = 0.0;
			int size = ref.getProbeStatResults().size();
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
					total += ref.getProbeStatResults().get(i).getBestStatResult().getBMD();
				else
					size--;

			}
			if (size > 0)
				stringBuffer.append(total / size);
			else
				stringBuffer.append("NA");

		}
		return stringBuffer.toString();
	}

	private String getProbesUp()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.UP)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (i > 0)
					stringBuffer.append(",");
				stringBuffer.append(ref.getProbeStatResults().get(i).getProbeResponse().getProbe().getId());

			}

		}
		return stringBuffer.toString();
	}

	private String getGenesUp()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() != AdverseDirectionEnum.UP)
				continue;
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			stringBuffer.append(ref.getReferenceGene().getId());

		}
		return stringBuffer.toString();
	}

	private Integer getGenesAdverseUpCount()
	{
		Integer count = 0;
		if (referenceGeneProbeStatResults == null)
		{
			return 0;
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() == AdverseDirectionEnum.UP)
			{
				count++;
			}

		}
		return count;
	}

	private Integer getProbesAdverseDownCount()
	{
		Integer count = 0;
		if (referenceGeneProbeStatResults == null)
		{
			return 0;
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() == AdverseDirectionEnum.DOWN)
			{
				count += ref.getProbeStatResults().size();
			}

		}
		return count;
	}

	private Integer getProbesAdversUpCount()
	{
		Integer count = 0;
		if (referenceGeneProbeStatResults == null)
		{
			return 0;
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (ref.getAdverseDirection() == AdverseDirectionEnum.UP)
			{
				count += ref.getProbeStatResults().size();
			}

		}
		return count;
	}

	private String getBMDLList()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			double total = 0.0;
			int size = ref.getProbeStatResults().size();
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
					total += ref.getProbeStatResults().get(i).getBestStatResult().getBMDL();
				else
					size--;
			}
			if (size > 0)
				stringBuffer.append(total / size);
			else
				stringBuffer.append("NA");

		}
		return stringBuffer.toString();
	}

	private String getBMDUList()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			double total = 0.0;
			int size = ref.getProbeStatResults().size();
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
					total += ref.getProbeStatResults().get(i).getBestStatResult().getBMDU();
				else
					size--;
			}
			if (size > 0)
				stringBuffer.append(total / size);
			else
				stringBuffer.append("NA");

		}
		return stringBuffer.toString();
	}

	private String getBMDList()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			double total = 0.0;
			int size = ref.getProbeStatResults().size();
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (ref.getProbeStatResults().get(i).getBestStatResult() != null)
					total += ref.getProbeStatResults().get(i).getBestStatResult().getBMD();
				else
					size--;

			}
			if (size > 0)
				stringBuffer.append(total / size);
			else
				stringBuffer.append("NA");

		}
		return stringBuffer.toString();
	}

	private String getProbeIds()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}
		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			for (int i = 0; i < ref.getProbeStatResults().size(); i++)
			{
				if (i > 0)
					stringBuffer.append(",");

				ProbeStatResult prs = ref.getProbeStatResults().get(i);
				if (prs == null || prs.getProbeResponse() == null
						|| prs.getProbeResponse().getProbe() == null)
					System.out.println();
				else
					stringBuffer
							.append(ref.getProbeStatResults().get(i).getProbeResponse().getProbe().getId());

			}

		}
		return stringBuffer.toString();
	}

	private String getGenesIds()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (this.genes != null)
			return this.genes;
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}

		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			if (ref != null && ref.getReferenceGene() != null)
				stringBuffer.append(ref.getReferenceGene().getId());

		}
		genes = stringBuffer.toString();
		return stringBuffer.toString();
	}

	private String getGeneSymbolsPrivate()
	{
		getStringBuffer();
		stringBuffer.setLength(0);
		if (this.geneSymbols != null)
			return this.geneSymbols;
		if (referenceGeneProbeStatResults == null)
		{
			return "";
		}

		for (ReferenceGeneProbeStatResult ref : this.referenceGeneProbeStatResults)
		{
			if (stringBuffer.length() > 0)
				stringBuffer.append(";");
			if (ref != null && ref.getReferenceGene() != null)
				stringBuffer.append(ref.getReferenceGene().getGeneSymbol());

		}
		geneSymbols = stringBuffer.toString();
		return stringBuffer.toString();
	}

	public AdverseDirectionEnum getOverallDirection()
	{
		return overallDirection;
	}

	// fold change stats

	public Double gettotalFoldChange()
	{
		return totalFoldChange;
	}

	public Double getmeanFoldChange()
	{
		return meanFoldChange;
	}

	public Double getmedianFoldChange()
	{
		return medianFoldChange;
	}

	public Double getmaxFoldChange()
	{
		return maxFoldChange;
	}

	public Double getminFoldChange()
	{
		return minFoldChange;
	}

	public Double getstdDevFoldChange()
	{
		return stdDevFoldChange;
	}

	// 95% confidence interval stats
	public Double getbmdLower95()
	{
		return bmdLower95;
	}

	public Double getbmdUpper95()
	{
		return bmdUpper95;
	}

	public Double getbmdlLower95()
	{
		return bmdlLower95;
	}

	public Double getbmdlUpper95()
	{
		return bmdlUpper95;
	}

	public Double getbmduUpper95()
	{
		return bmduUpper95;
	}

	public Double getbmduLower95()
	{
		return bmduLower95;
	}

	private void calculateOverAllDirection()
	{
		if (referenceGeneProbeStatResults == null)
			return;

		int upcount = 0;
		int downcount = 0;
		int totalcount = 0;
		int conflictcount = 0;

		for (ReferenceGeneProbeStatResult rp : referenceGeneProbeStatResults)
		{
			int pupcount = 0;
			int pdowncount = 0;
			for (ProbeStatResult probeStatResult : rp.getProbeStatResults())
			{
				if (probeStatResult.getBestStatResult() != null)
				{
					if (probeStatResult.getBestStatResult().getAdverseDirection() == 1)
						pupcount++;
					else if (probeStatResult.getBestStatResult().getAdverseDirection() == -1)
						pdowncount++;
				}
			}
			if (pupcount > 0 && pdowncount == 0)
				upcount++;

			else if (pdowncount > 0 && pupcount == 0)
				downcount++;
			else
				conflictcount++;
			totalcount++;
		}

		if (totalcount > 0)
		{
			this.percentWithOverallDirectionDOWN = (double) downcount / (double) totalcount;
			this.percentWithOverallDirectionUP = (double) upcount / (double) totalcount;
			this.percentWithOverallDirectionConflict = (double) conflictcount / (double) totalcount;
			if ((float) upcount / totalcount >= 0.6f)
			{
				this.overallDirection = AdverseDirectionEnum.UP;

			}
			else if ((float) downcount / totalcount >= 0.6f)
			{
				this.overallDirection = AdverseDirectionEnum.DOWN;

			}
			else
			{
				this.overallDirection = AdverseDirectionEnum.CONFLICT;
			}
		}

	}

	/*
	 * method to calculate fold change stats. these are transient values and should be calculate sometime post
	 * deserialization or prior to being displayed in a table. This should use the maxfold change per
	 * bestmodel result
	 */
	private void calculateFoldChangeStats()
	{
		if (referenceGeneProbeStatResults == null)
			return;

		DescriptiveStatistics summaryStats = new DescriptiveStatistics();
		List<Double> bestFoldChanges = new ArrayList<>();
		for (ReferenceGeneProbeStatResult rp : referenceGeneProbeStatResults)
			for (ProbeStatResult probeStatResult : rp.getProbeStatResults())
				if (probeStatResult.getBestABSFoldChange() != null)
					summaryStats.addValue(probeStatResult.getBestABSFoldChange());

		this.meanFoldChange = summaryStats.getGeometricMean();
		this.totalFoldChange = summaryStats.getSum();
		this.minFoldChange = summaryStats.getMin();
		this.maxFoldChange = summaryStats.getMax();
		this.stdDevFoldChange = summaryStats.getStandardDeviation();
		this.medianFoldChange = summaryStats.getPercentile(50);

	}

	/*
	 * method to calculate 95% confidence intervals for bmd, bmdl and bmdu
	 */
	private void calculate95ConfidenceIntervals()
	{
		if (this.referenceGeneProbeStatResults == null)
			return;

		SummaryStatistics statsBMD = new SummaryStatistics();
		SummaryStatistics statsBMDL = new SummaryStatistics();
		SummaryStatistics statsBMDU = new SummaryStatistics();

		for (ReferenceGeneProbeStatResult rgp : referenceGeneProbeStatResults)
		{
			int i = 0;
			double BMDValue = 0.0;
			double BMDLValue = 0.0;
			double BMDUValue = 0.0;
			for (ProbeStatResult probeStatResult : rgp.getProbeStatResults())
			{
				if (probeStatResult.getBestStatResult() == null)
					continue;
				BMDValue += probeStatResult.getBestBMD();
				BMDLValue += probeStatResult.getBestBMDL();
				BMDUValue += probeStatResult.getBestBMDU();

				i++;
			}
			if (i > 0)
			{
				statsBMD.addValue(BMDValue / i);
				statsBMDL.addValue(BMDLValue / i);
				statsBMDU.addValue(BMDUValue / i);
			}
		}

		double ninetyfiveBMD = calculate95(statsBMD);
		double ninetyfiveBMDL = calculate95(statsBMDL);
		double ninetyfiveBMDU = calculate95(statsBMDU);

		bmdLower95 = statsBMD.getMean() - ninetyfiveBMD;
		bmdUpper95 = statsBMD.getMean() + ninetyfiveBMD;

		bmdlLower95 = statsBMDL.getMean() - ninetyfiveBMDL;
		bmdlUpper95 = statsBMDL.getMean() + ninetyfiveBMDL;

		bmduLower95 = statsBMDU.getMean() - ninetyfiveBMDU;
		bmduUpper95 = statsBMDU.getMean() + ninetyfiveBMDU;

	}

	private static double calculate95(SummaryStatistics stats)
	{
		try
		{
			// use z-distribution value that gets us to 95% confidence level
			return stats.getStandardDeviation() * 1.96 / Math.sqrt(stats.getN());
		}
		catch (Exception e)
		{
			return Double.NaN;
		}
	}

	/*
	 * calculate statresult counts
	 */

	private String calculateStatResultCounts()
	{

		Map<String, List<StatResult>> statResultCounts = new HashMap<>();
		int total = 0;
		if (referenceGeneProbeStatResults == null)
			return "";
		for (ReferenceGeneProbeStatResult refGeneProbeStat : referenceGeneProbeStatResults)
		{
			for (ProbeStatResult probeStatResult : refGeneProbeStat.getProbeStatResults())
			{
				StatResult statResult = probeStatResult.getBestStatResult();

				if (statResult != null)
				{
					if (!statResultCounts.containsKey(statResult.toString()))
					{
						statResultCounts.put(statResult.toString(), new ArrayList<>());
					}
					statResultCounts.get(statResult.toString()).add(statResult);
					total++;
				}
			}
		}

		// Generate the String
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		StringBuilder sb = new StringBuilder();
		List<String> sortedList = new ArrayList<>(statResultCounts.keySet());
		Collections.sort(sortedList);
		for (String key : sortedList)
		{
			if (sb.length() > 0)
				sb.append(";");

			sb.append(key);
			sb.append(",");
			sb.append(statResultCounts.get(key).size());
			sb.append("(");
			sb.append(df.format(statResultCounts.get(key).size() / (double) total));
			sb.append(")");
		}

		return sb.toString();
	}

	private String double2String(Double d)
	{
		if (d == null)
		{
			return "";
		}
		else if (d.isNaN())
		{
			return "";
		}

		return String.valueOf(d);

	}

	private String int2String(Integer i)
	{
		if (i == null)
		{
			return "";
		}

		return String.valueOf(i);

	}

	@JsonIgnore
	@Override
	public Set<String> containsGenes(Set<String> genes)
	{
		Set<String> genesContained = new HashSet<>();
		for (ReferenceGeneProbeStatResult rg : referenceGeneProbeStatResults)
			if (genes.contains(rg.getReferenceGene().getId()))
				genesContained.add(rg.getReferenceGene().getId());
			else if (genes.contains(rg.getReferenceGene().getGeneSymbol().toLowerCase()))
				genesContained.add(rg.getReferenceGene().getGeneSymbol());

		return genesContained;
	}

	@JsonIgnore
	@Override
	public Set<String> getMarkableKeys()
	{
		Set<String> returnSet = new HashSet<>();
		returnSet.add(this.getCategoryDescription());
		return returnSet;
	}

	@JsonIgnore
	@Override
	public String getMarkableLabel()
	{
		return this.getCategoryDescription();
	}

	@JsonIgnore
	@Override
	public Color getMarkableColor()
	{
		return Color.YELLOW;
	}

}
