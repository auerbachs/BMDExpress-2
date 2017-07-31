package com.sciome.bmdexpress2.mvp.model.prefilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.util.NumberManager;
import com.sciome.charts.annotation.ChartableDataPoint;
import com.sciome.charts.annotation.ChartableDataPointLabel;
import com.sciome.filter.annotation.Filterable;

public class OneWayANOVAResult extends BMDExpressAnalysisRow implements Serializable
{

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -465506000834082809L;

	private ProbeResponse				probeResponse;
	private short						degreesOfFreedomOne;
	private short						degreesOfFreedomTwo;
	private double						fValue;
	private double						pValue;
	private double						adjustedPValue;
	private Float						bestFoldChange;

	private List<Float>					foldChanges;

	private transient String			genes;
	private transient String			geneSymbols;

	// row data for the table view.
	protected transient List<Object>	row;

	public ProbeResponse getProbeResponse()
	{
		return probeResponse;
	}

	public void setProbeResponse(ProbeResponse probeResponse)
	{
		this.probeResponse = probeResponse;
	}

	@Filterable(key = OneWayANOVAResults.PROBE_ID)
	@ChartableDataPointLabel(key = OneWayANOVAResults.PROBE_ID)
	public String getProbeID()
	{
		return probeResponse.getProbe().getId();
	}

	@Filterable(key = OneWayANOVAResults.GENE_ID)
	public String getGenes()
	{
		return genes;
	}

	@Filterable(key = OneWayANOVAResults.GENE_SYMBOL)
	public String getGeneSymbols()
	{
		return geneSymbols;
	}

	public short getDegreesOfFreedomOne()
	{
		return degreesOfFreedomOne;
	}

	public void setDegreesOfFreedomOne(short degreesOfFreedomOne)
	{
		this.degreesOfFreedomOne = degreesOfFreedomOne;
	}

	public short getDegreesOfFreedomTwo()
	{
		return degreesOfFreedomTwo;
	}

	public void setDegreesOfFreedomTwo(short degreesOfFreedomTwo)
	{
		this.degreesOfFreedomTwo = degreesOfFreedomTwo;
	}

	@Filterable(key = OneWayANOVAResults.FVALUE)
	@ChartableDataPoint(key = OneWayANOVAResults.FVALUE)
	public double getfValue()
	{
		return fValue;
	}

	public void setfValue(double fValue)
	{
		this.fValue = fValue;
	}

	@Filterable(key = OneWayANOVAResults.UNADJUSTED_PVALUE)
	@ChartableDataPoint(key = OneWayANOVAResults.UNADJUSTED_PVALUE)
	public double getpValue()
	{
		return pValue;
	}

	@ChartableDataPoint(key = OneWayANOVAResults.NEG_LOG_UNADJUSTED_PVALUE)
	public double getNegativeLog10pValue()
	{

		return NumberManager.negLog10(this.pValue);
	}

	public void setpValue(double pValue)
	{
		this.pValue = pValue;
	}

	@ChartableDataPoint(key = OneWayANOVAResults.NEG_LOG_ADJUSTED_PVALUE)
	public double getNegativeLogAdjustedPValue()
	{
		return NumberManager.negLog10(this.adjustedPValue);
	}

	@Filterable(key = OneWayANOVAResults.ADJUSTED_PVALUE)
	@ChartableDataPoint(key = OneWayANOVAResults.ADJUSTED_PVALUE)
	public double getAdjustedPValue()
	{
		return adjustedPValue;
	}

	public void setAdjustedPValue(double adjustedPValue)
	{
		this.adjustedPValue = adjustedPValue;
	}

	@Filterable(key = OneWayANOVAResults.BEST_FOLD_CHANGE)
	@ChartableDataPoint(key = OneWayANOVAResults.BEST_FOLD_CHANGE)
	public Float getBestFoldChange()
	{
		return bestFoldChange;
	}

	@Filterable(key = OneWayANOVAResults.BEST_FOLD_CHANGE_ABS)
	@ChartableDataPoint(key = OneWayANOVAResults.BEST_FOLD_CHANGE_ABS)
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

		row.add((degreesOfFreedomOne));
		row.add((degreesOfFreedomTwo));
		row.add((fValue));
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

	}

	// @Filterable(key = OneWayANOVAResults.FOLD_CHANGE)
	// @ChartableDataPoint(key = OneWayANOVAResults.FOLD_CHANGE)
	public List<Float> getFoldChanges()
	{
		return this.foldChanges;
	}

	public void setFoldChanges(List<Float> fcs)
	{
		this.foldChanges = fcs;
	}

	@Override
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
