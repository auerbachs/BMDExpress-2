package com.sciome.bmdexpress2.mvp.model.stat;

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
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResult;
import com.sciome.bmdexpress2.mvp.model.prefilter.PrefilterResults;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.charts.annotation.ChartableData;
import com.sciome.charts.annotation.ChartableDataLabel;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class BMDResult extends BMDExpressAnalysisDataSet implements Serializable, IStatModelProcessable
{

	/**
	 * 
	 */
	private static final long		serialVersionUID			= 4821688005886618518L;

	private String					name;
	private List<ProbeStatResult>	probeStatResults;

	private DoseResponseExperiment	doseResponseExperiment;
	private AnalysisInfo			analysisInfo;

	private PrefilterResults		prefilterResults;

	private transient List<String>	columnHeader;

	private Long					id;

	/* define chartabble key values */
	public static final String		BMD							= "Best BMD";
	public static final String		BMDL						= "Best BMDL";
	public static final String		BMDU						= "Best BMDU";
	public static final String		BMD_BMDL_RATIO				= "Best BMD/BMDL";
	public static final String		BMDU_BMDL_RATIO				= "Best BMDU/BMDL";
	public static final String		BMDU_BMD_RATIO				= "Best BMDU/BMD";
	public static final String		FIT_PVALUE					= "Best Fit P-Value";
	public static final String		FIT_LOG_LIKELIHOOD			= "Best Fit Log-Likelihood";
	public static final String		PREFILTER_PVALUE			= "Prefilter P-Value";
	public static final String		PREFILTER_ADJUSTEDPVALUE	= "Prefilter Adjusted P-Value";
	public static final String		BEST_FOLDCHANGE				= "Best Fold Change";
	public static final String		BEST_ABSFOLDCHANGE			= "Best Fold Change Absolute Value";

	@JsonIgnore
	public Long getID()
	{
		return id;
	}

	public void setID(Long id)
	{
		this.id = id;
	}

	@Override
	@ChartableDataLabel(key = "Category Results Name")
	public String getName()
	{
		return name;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@ChartableData(key = "Probe Stat Results")
	public List<ProbeStatResult> getProbeStatResults()
	{
		return probeStatResults;
	}

	public void setProbeStatResults(List<ProbeStatResult> probeStatResults)
	{
		this.probeStatResults = probeStatResults;
	}

	public DoseResponseExperiment getDoseResponseExperiment()
	{
		return doseResponseExperiment;
	}

	public void setDoseResponseExperiment(DoseResponseExperiment doseResponseExperiment)
	{
		this.doseResponseExperiment = doseResponseExperiment;
	}

	public PrefilterResults getPrefilterResults()
	{
		return prefilterResults;
	}

	public void setPrefilterResults(PrefilterResults prefilterResults)
	{
		this.prefilterResults = prefilterResults;
	}

	/*
	 * fill the column header for table display or file export purposes.
	 */
	private void fillColumnHeader()
	{
		columnHeader = new ArrayList<>();
		if (probeStatResults == null || probeStatResults.size() == 0)
		{
			return;
		}
		ProbeStatResult probStatResult = probeStatResults.get(0);

		columnHeader = probStatResult.generateColumnHeader();

		columnHeader.add(PREFILTER_PVALUE);
		columnHeader.add(PREFILTER_ADJUSTEDPVALUE);
		columnHeader.add(BEST_FOLDCHANGE);
		columnHeader.add(BEST_ABSFOLDCHANGE);
	}

	@Override
	@JsonIgnore
	public List<String> getColumnHeader()
	{
		if (columnHeader == null || columnHeader.size() == 0)
			fillTableData();
		return columnHeader;
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

	// This is called in order to generate data for each probe stat result fo viewing
	// data in a table or exporting it.
	private void fillRowData()
	{

		// if there is a prefilter associate with this, then fill up this map
		// so the bmdresults can be associated with prefilter results
		// so that fold change/p-value/adjusted p-value can be shown in the grid.
		Map<String, PrefilterResult> probeToPrefilterMap = new HashMap<>();

		if (this.prefilterResults != null && prefilterResults.getPrefilterResults() != null)
			for (PrefilterResult prefilterResult : prefilterResults.getPrefilterResults())
				probeToPrefilterMap.put(prefilterResult.getProbeID(), prefilterResult);

		Map<String, ReferenceGeneAnnotation> probeToGeneMap = new HashMap<>();

		if (this.doseResponseExperiment.getReferenceGeneAnnotations() != null)
		{
			for (ReferenceGeneAnnotation refGeneAnnotation : this.doseResponseExperiment
					.getReferenceGeneAnnotations())
			{
				probeToGeneMap.put(refGeneAnnotation.getProbe().getId(), refGeneAnnotation);
			}
		}
		for (ProbeStatResult probeStatResult : probeStatResults)
		{
			Double adjustedPValue = null;
			Double pValue = null;
			Double bestFoldChange = null;

			PrefilterResult prefilter = probeToPrefilterMap
					.get(probeStatResult.getProbeResponse().getProbe().getId());

			// if the prefilter is not null, then add the prefilter metrics to allow user
			// to more easily sort and view the bmdresults
			if (prefilter != null)
			{
				adjustedPValue = prefilter.getAdjustedPValue();
				pValue = prefilter.getpValue();
				bestFoldChange = prefilter.getBestFoldChange().doubleValue();
			}
			probeStatResult.createRowData(probeToGeneMap, adjustedPValue, pValue, bestFoldChange);
		}

	}

	@Override
	public String toString()
	{
		return name;
	}

	public void refreshTableData()
	{
		columnHeader = null;
		for (ProbeStatResult probeStatResult : probeStatResults)
		{
			probeStatResult.refreshRowData();
		}
		fillTableData();

	}

	private void fillTableData()
	{
		if (columnHeader == null)
		{
			fillColumnHeader();
			fillRowData();
		}

	}

	@Override
	@JsonIgnore
	public DoseResponseExperiment getProcessableDoseResponseExperiment()
	{
		return doseResponseExperiment;
	}

	@Override
	@JsonIgnore
	public List<ProbeResponse> getProcessableProbeResponses()
	{

		List<ProbeResponse> probeResponse = new ArrayList<>();
		for (ProbeStatResult probeStatResult : this.probeStatResults)
		{
			probeResponse.add(probeStatResult.getProbeResponse());
		}

		return probeResponse;
	}

	@Override
	@JsonIgnore
	public String getParentDataSetName()
	{
		return doseResponseExperiment.getName();
	}

	@SuppressWarnings("unchecked")
	@Override
	@JsonIgnore
	public List getAnalysisRows()
	{
		return probeStatResults;
	}

	@Override
	@JsonIgnore
	public List<Object> getColumnHeader2()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@JsonIgnore
	public LogTransformationEnum getLogTransformation()
	{
		return this.getDoseResponseExperiment().getLogTransformation();
	}

}
