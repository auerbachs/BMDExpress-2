package com.sciome.bmdexpress2.mvp.model.prefilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.charts.annotation.ChartableData;
import com.sciome.charts.annotation.ChartableDataLabel;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class PathwayFilterResults extends BMDExpressAnalysisDataSet
		implements Serializable, IStatModelProcessable, PrefilterResults
{

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -5704632335867988973L;

	private String						name;

	private DoseResponseExperiment		doseResponseExperiment;
	private List<PathwayFilterResult>	pathwayFilterResults;
	private AnalysisInfo				analysisInfo;
	@JsonIgnore
	private transient List<String>		columnHeader;

	private Long						id;

	/* define chartabble key values */
	public static final String			UNADJUSTED_PVALUE	= "Unadj P-Value Mean";
	public static final String			FDR_PVALUE			= "FDR P-Value Mean";
	public static final String			FWER_PVALUE			= "FWER P-Value Mean";
	public static final String			GENE_ID				= "Gene ID";
	public static final String			GENE_SYMBOL			= "Gene Symbol";
	public static final String			PATHWAY				= "Pathway Name";
	public static final String			PROBE_ID			= "Probe ID";
	public static final String			PATHWAY_COUNT		= "Pathway Count";
	public static final String			GENE_COUNT			= "Gene Count";

	@JsonIgnore
	public Long getID()
	{
		return id;
	}

	public void setID(Long id)
	{
		this.id = id;
	}

	@ChartableData(key = "One Way ANOVA")
	public List<PathwayFilterResult> getPathwayFilterResults()
	{
		return pathwayFilterResults;
	}

	public void setPathwayFilterResults(List<PathwayFilterResult> pathwayFilterResults)
	{
		this.pathwayFilterResults = pathwayFilterResults;
	}

	@ChartableDataLabel(key = "Pathway Filter Results")
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	public DoseResponseExperiment getDoseResponseExperiement()
	{
		return doseResponseExperiment;
	}

	public void setDoseResponseExperiement(DoseResponseExperiment doseResponseExperiement)
	{
		this.doseResponseExperiment = doseResponseExperiement;
	}

	@Override
	public String toString()
	{
		return name;
	}

	public List<ProbeResponse> getProbeResponses()
	{
		List<ProbeResponse> probeResponses = new ArrayList<>();

		if (pathwayFilterResults != null)
		{
			for (PathwayFilterResult oneWayResult : pathwayFilterResults)
			{
				probeResponses.add(oneWayResult.getProbeResponse());
			}
		}

		return probeResponses;
	}

	/*
	 * Implement the IStatModelProcessable methods.
	 */

	@Override
	public DoseResponseExperiment getProcessableDoseResponseExperiment()
	{
		return this.doseResponseExperiment;
	}

	@Override
	public List<ProbeResponse> getProcessableProbeResponses()
	{
		return this.getProbeResponses();
	}

	@Override
	public String getParentDataSetName()
	{
		return this.doseResponseExperiment.toString();
	}

	@Override
	public AnalysisInfo getAnalysisInfo()
	{
		return analysisInfo;
	}

	public void setAnalysisInfo(AnalysisInfo analysisInfo)
	{
		this.analysisInfo = analysisInfo;
	}

	/*
	 * fill the column header for table display or file export purposes.
	 */
	private void fillColumnHeader()
	{
		columnHeader = new ArrayList<>();
		if (pathwayFilterResults == null || pathwayFilterResults.size() == 0)
		{
			return;
		}

		columnHeader.add("Probe ID");
		columnHeader.add("Genes");

		columnHeader.add("Gene Symbols");

		columnHeader.add("Pathways");

		// p value
		columnHeader.add("Unadjusted P-Values");

		columnHeader.add("FDR values");

		columnHeader.add("FWER values");

	}

	@Override
	public List<String> getColumnHeader()
	{
		if (columnHeader == null || columnHeader.size() == 0)
		{
			fillColumnHeader();
			fillRowData();
		}
		return columnHeader;
	}

	// This is called in order to generate data for each probe stat result fo viewing
	// data in a table or exporting it.
	private void fillRowData()
	{
		// This will allow gene/gene symbol lists to be come part of the table.
		Map<String, ReferenceGeneAnnotation> probeToGeneMap = new HashMap<>();

		if (this.doseResponseExperiment.getReferenceGeneAnnotations() != null)
		{
			for (ReferenceGeneAnnotation refGeneAnnotation : this.doseResponseExperiment
					.getReferenceGeneAnnotations())
			{
				probeToGeneMap.put(refGeneAnnotation.getProbe().getId(), refGeneAnnotation);
			}
		}
		for (PathwayFilterResult oneWayResult : pathwayFilterResults)
		{
			oneWayResult.createRowData(probeToGeneMap);
		}
	}

	private Double getMean(List<Double> doubles)
	{
		double sum = 0.0;
		for (Double d : doubles)
		{
			sum += d;
		}
		return new Double(sum / (double) doubles.size());
	}

	@Override
	public List getAnalysisRows()
	{
		return pathwayFilterResults;
	}

	@Override
	public List<Object> getColumnHeader2()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PrefilterResult> getPrefilterResults()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@JsonIgnore
	public LogTransformationEnum getLogTransformation()
	{
		return this.getDoseResponseExperiement().getLogTransformation();
	}

}
