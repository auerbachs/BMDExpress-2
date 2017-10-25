package com.sciome.bmdexpress2.mvp.model.prefilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.charts.annotation.ChartableDataPoint;
import com.sciome.charts.annotation.ChartableDataPointLabel;
import com.sciome.filter.annotation.Filterable;

public class OriogenResult extends BMDExpressAnalysisRow implements Serializable, PrefilterResult {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4095025460096134903L;
	
	private ProbeResponse				probeResponse;
	private double						pValue;
	private double						adjustedPValue;
	private Float						bestFoldChange;

	private List<Float>					foldChanges;

	@JsonIgnore
	private transient String			genes;
	@JsonIgnore
	private transient String			geneSymbols;

	// row data for the table view.
	@JsonIgnore
	protected transient List<Object>	row;

	private Long						id;
	
	private String 						profile;

	public ProbeResponse getProbeResponse()
	{
		return probeResponse;
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

	public void setProbeResponse(ProbeResponse probeResponse)
	{
		this.probeResponse = probeResponse;
	}

	@Filterable(key = OriogenResults.PROBE_ID)
	@ChartableDataPointLabel(key = OriogenResults.PROBE_ID)
	@JsonIgnore
	public String getProbeID()
	{
		return probeResponse.getProbe().getId();
	}

	@Filterable(key = OriogenResults.GENE_ID)
	public String getGenes()
	{
		return genes;
	}

	@Filterable(key = OriogenResults.GENE_SYMBOL)
	@JsonIgnore
	public String getGeneSymbols()
	{
		return geneSymbols;
	}

	@Filterable(key = OriogenResults.UNADJUSTED_PVALUE)
	@ChartableDataPoint(key = OriogenResults.UNADJUSTED_PVALUE)
	public double getpValue()
	{
		return pValue;
	}

	@ChartableDataPoint(key = OriogenResults.NEG_LOG_UNADJUSTED_PVALUE)
	@JsonIgnore
	public double getNegativeLog10pValue()
	{

		return NumberManager.negLog10(this.pValue);
	}

	public void setpValue(double pValue)
	{
		this.pValue = pValue;
	}

	@ChartableDataPoint(key = OriogenResults.NEG_LOG_ADJUSTED_PVALUE)
	@JsonIgnore
	public double getNegativeLogAdjustedPValue()
	{
		return NumberManager.negLog10(this.adjustedPValue);
	}

	@Filterable(key = OriogenResults.ADJUSTED_PVALUE)
	@ChartableDataPoint(key = OriogenResults.ADJUSTED_PVALUE)
	public double getAdjustedPValue()
	{
		return adjustedPValue;
	}

	public void setAdjustedPValue(double adjustedPValue)
	{
		this.adjustedPValue = adjustedPValue;
	}

	@Filterable(key = OriogenResults.BEST_FOLD_CHANGE)
	@ChartableDataPoint(key = OriogenResults.BEST_FOLD_CHANGE)
	public Float getBestFoldChange()
	{
		return bestFoldChange;
	}

	@Filterable(key = OriogenResults.BEST_FOLD_CHANGE_ABS)
	@ChartableDataPoint(key = OriogenResults.BEST_FOLD_CHANGE_ABS)
	@JsonIgnore
	public Float getBestFoldChangeABS()
	{
		if (bestFoldChange == null)
			return null;
		return Math.abs(bestFoldChange);
	}

	public void setBestFoldChange(Float bestFoldChange)
	{
		this.bestFoldChange = bestFoldChange;
	}

	public void createRowData(Map<String, ReferenceGeneAnnotation> referenceGeneAnnotations)
	{
		if (row != null)
		{
			return;
		}

		row = new ArrayList<>();

		row.add(probeResponse.getProbe().getId());

		ReferenceGeneAnnotation refGeneAnnotation = referenceGeneAnnotations
				.get(probeResponse.getProbe().getId());
		StringBuffer geneSymbols = new StringBuffer();
		StringBuffer genes = new StringBuffer();
		if (refGeneAnnotation != null)
		{
			// get the genes and symboles
			for (ReferenceGene refGene : refGeneAnnotation.getReferenceGenes())
			{
				if (genes.length() > 0)
				{
					genes.append(";");
					geneSymbols.append(";");
				}
				genes.append(refGene.getId());
				geneSymbols.append(refGene.getGeneSymbol());
			}
		}

		this.genes = genes.toString();
		this.geneSymbols = geneSymbols.toString();
		row.add(genes.toString());
		row.add(geneSymbols.toString());
		
		row.add((pValue));
		row.add((adjustedPValue));

		if (bestFoldChange != null)
		{
			row.add((bestFoldChange));
		}
		if (foldChanges != null)
		{
			for (Float foldChange : foldChanges)
			{
				row.add(foldChange);
			}
		}
		row.add(profile);
	}

	// @Filterable(key = OriogenResults.FOLD_CHANGE)
	// @ChartableDataPoint(key = OriogenResults.FOLD_CHANGE)
	public List<Float> getFoldChanges()
	{
		return this.foldChanges;
	}

	public void setFoldChanges(List<Float> fcs)
	{
		this.foldChanges = fcs;
	}
	
	@Filterable(key = OriogenResults.PROFILE)
	@ChartableDataPoint(key = OriogenResults.PROFILE)
	public String getProfile() {
		return this.profile;
	}
	
	public void setProfile(String profile) {
		this.profile = profile;
	}

	@Override
	@JsonIgnore
	public List<Object> getRow()
	{
		return row;
	}

	@Override
	public String toString()
	{
		return probeResponse.getProbe().getId() + " : " + genes + " : " + geneSymbols;
	}
}
