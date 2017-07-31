package com.sciome.bmdexpress2.mvp.model.prefilter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.charts.annotation.ChartableDataPoint;
import com.sciome.charts.annotation.ChartableDataPointLabel;
import com.sciome.filter.annotation.Filterable;

public class PathwayFilterResult extends BMDExpressAnalysisRow implements Serializable
{

	/**
	 * 
	 */
	private static final long			serialVersionUID	= -465506000834082809L;

	private ProbeResponse				probeResponse;
	private List<String>				pathways;
	private List<Double>				pValues;
	private List<Double>				FDRs;
	private List<Double>				FWERs;

	// row data for the table view.
	protected transient List<Object>	row;

	private transient String			genes;
	private transient String			geneSymbols;
	private transient int				geneCount			= 0;

	public ProbeResponse getProbeResponse()
	{
		return probeResponse;
	}

	public void setProbeResponse(ProbeResponse probeResponse)
	{
		this.probeResponse = probeResponse;
	}

	@Filterable(key = PathwayFilterResults.PROBE_ID)
	@ChartableDataPointLabel(key = PathwayFilterResults.PROBE_ID)
	public String getProbeID()
	{
		return probeResponse.getProbe().getId();
	}

	@Filterable(key = PathwayFilterResults.GENE_ID)
	public String getGenes()
	{
		return genes;
	}

	@Filterable(key = PathwayFilterResults.GENE_SYMBOL)
	public String getGeneSymbols()
	{
		return geneSymbols;
	}

	public List<String> getPathways()
	{
		return pathways;
	}

	public void setPathways(List<String> pathways)
	{
		this.pathways = pathways;
	}

	public List<Double> getpValues()
	{
		return pValues;
	}

	@Filterable(key = PathwayFilterResults.PATHWAY_COUNT)
	@ChartableDataPoint(key = PathwayFilterResults.PATHWAY_COUNT)
	public Double getPathWayCount()
	{
		if (pathways == null)
			return 0.0;
		return (double) pathways.size();
	}

	@Filterable(key = PathwayFilterResults.GENE_COUNT)
	@ChartableDataPoint(key = PathwayFilterResults.GENE_COUNT)
	public Double getGeneCount()
	{
		return (double) geneCount;
	}

	@Filterable(key = PathwayFilterResults.UNADJUSTED_PVALUE)
	@ChartableDataPoint(key = PathwayFilterResults.UNADJUSTED_PVALUE)
	public Double getpValueMean()
	{
		return getAverage(pValues);
	}

	public void setpValues(List<Double> pValues)
	{
		this.pValues = pValues;
	}

	public List<Double> getFDRs()
	{
		return FDRs;
	}

	@Filterable(key = PathwayFilterResults.FDR_PVALUE)
	@ChartableDataPoint(key = PathwayFilterResults.FDR_PVALUE)
	public Double getFDRMean()
	{
		return getAverage(FDRs);
	}

	public void setFDRs(List<Double> fDRs)
	{
		FDRs = fDRs;
	}

	public List<Double> getFWERs()
	{
		return FWERs;
	}

	@Filterable(key = PathwayFilterResults.FWER_PVALUE)
	@ChartableDataPoint(key = PathwayFilterResults.FWER_PVALUE)
	public Double getFWERMean()
	{
		return getAverage(FWERs);
	}

	public void setFWERs(List<Double> fWERs)
	{
		FWERs = fWERs;
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
				geneCount++;
			}
		}
		this.genes = genes.toString();
		this.geneSymbols = geneSymbols.toString();
		row.add(genes.toString());
		row.add(geneSymbols.toString());
		row.add(String.join(";", pathways));
		StringBuffer sb = new StringBuffer();
		for (Double pValue : pValues)
		{
			if (sb.length() > 0)
				sb.append(";");
			sb.append(pValue);
		}
		row.add(sb.toString());

		sb = new StringBuffer();
		if (FDRs != null)
		{
			for (Double pValue : FDRs)
			{
				if (sb.length() > 0)
					sb.append(";");
				sb.append(pValue);
			}
		}
		row.add(sb.toString());

		sb = new StringBuffer();
		if (FWERs != null)
		{
			for (Double pValue : FWERs)
			{
				if (sb.length() > 0)
					sb.append(";");
				sb.append(pValue);
			}
		}
		row.add(sb.toString());

	}

	@Override
	public List<Object> getRow()
	{
		return row;
	}

	public Double getAverage(List<Double> values)
	{
		Double sum = 0.0;
		if (values == null || values.size() == 0)
			return null;
		for (Double value : values)
		{
			sum += value;
		}
		return sum / values.size();
	}

	@Override
	public String toString()
	{
		return probeResponse.getProbe().getId() + " : " + genes + " : " + geneSymbols;
	}

}
