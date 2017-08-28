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
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.charts.annotation.ChartableData;
import com.sciome.charts.annotation.ChartableDataLabel;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class OneWayANOVAResults extends BMDExpressAnalysisDataSet
		implements Serializable, IStatModelProcessable
{

	/**
	 * 
	 */
	private static final long		serialVersionUID			= -5704632335867988973L;

	private String					name;

	private DoseResponseExperiment	doseResponseExperiment;
	private List<OneWayANOVAResult>	oneWayANOVAResults;
	private AnalysisInfo			analysisInfo;
	private transient List<String>	columnHeader;

	private Long					id;

	/* define chartabble key values */
	public static final String		FVALUE						= "F-Value";
	public static final String		UNADJUSTED_PVALUE			= "Unadjusted P-Value";
	public static final String		ADJUSTED_PVALUE				= "Adjusted P-Value";
	public static final String		NEG_LOG_ADJUSTED_PVALUE		= "Negative Log 10 Adjusted P-Value";
	public static final String		BEST_FOLD_CHANGE			= "Max Fold Change";
	public static final String		BEST_FOLD_CHANGE_ABS		= "Max Fold Change Unsigned";
	public static final String		FOLD_CHANGE					= "Fold Change";
	public static final String		GENE_ID						= "Gene ID";
	public static final String		GENE_SYMBOL					= "Gene Symbol";
	public static final String		PROBE_ID					= "Probe ID";

	public static final String		NEG_LOG_UNADJUSTED_PVALUE	= "Negative Log 10 Unadjusted P-Value";

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
	public List<OneWayANOVAResult> getOneWayANOVAResults()
	{
		return oneWayANOVAResults;
	}

	public void setOneWayANOVAResults(List<OneWayANOVAResult> oneWayANOVAResults)
	{
		this.oneWayANOVAResults = oneWayANOVAResults;
	}

	@ChartableDataLabel(key = "One Way ANOVA")
	@Override
	public String getName()
	{
		return name;
	}

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

	@JsonIgnore
	public List<ProbeResponse> getProbeResponses()
	{
		List<ProbeResponse> probeResponses = new ArrayList<>();

		if (oneWayANOVAResults != null)
		{
			for (OneWayANOVAResult oneWayResult : oneWayANOVAResults)
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
	@JsonIgnore
	public DoseResponseExperiment getProcessableDoseResponseExperiment()
	{
		return this.doseResponseExperiment;
	}

	@Override
	@JsonIgnore
	public List<ProbeResponse> getProcessableProbeResponses()
	{
		return this.getProbeResponses();
	}

	@Override
	@JsonIgnore
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
		if (oneWayANOVAResults == null || oneWayANOVAResults.size() == 0)
		{
			return;
		}

		columnHeader.add("Probe ID");

		columnHeader.add("Genes");

		columnHeader.add("Gene Symbols");
		columnHeader.add("Df1");

		columnHeader.add("Df2");

		columnHeader.add("F-Value");

		// p value
		columnHeader.add("P-Value");

		// adjusted p value
		columnHeader.add("Adjusted P-Value");

		if (oneWayANOVAResults.get(0).getBestFoldChange() != null)
		{
			columnHeader.add("Max Fold Change Value");
		}

		if (oneWayANOVAResults.get(0).getFoldChanges() != null)
		{
			int i = 1;
			for (Float foldChange : oneWayANOVAResults.get(0).getFoldChanges())
			{
				columnHeader.add("FC Dose Level " + i);
				i++;
			}
		}

	}

	@Override
	@JsonIgnore
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
		for (OneWayANOVAResult oneWayResult : oneWayANOVAResults)
		{
			oneWayResult.createRowData(probeToGeneMap);
		}
	}

	@Override
	@JsonIgnore
	public List getAnalysisRows()
	{
		return oneWayANOVAResults;
	}

	@Override
	@JsonIgnore
	public List<Object> getColumnHeader2()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
