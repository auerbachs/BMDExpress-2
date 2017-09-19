package com.sciome.bmdexpress2.mvp.model.stat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.charts.annotation.ChartableDataPoint;
import com.sciome.charts.annotation.ChartableDataPointLabel;
import com.sciome.filter.annotation.Filterable;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class ProbeStatResult extends BMDExpressAnalysisRow implements Serializable
{

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 8457191085367967268L;

	private ProbeResponse			probeResponse;
	private StatResult				bestStatResult;
	private StatResult				bestPolyStatResult;
	private List<StatResult>		statResults;
	private List<ChiSquareResult>	chiSquaredResults;

	// convenience variables for easy query and reduced processing.
	private transient List<Object>	row;
	private transient String		genes;
	private transient String		geneSymbols;

	private transient Double		prefilterAdjustedPValue;
	private transient Double		prefilterPvalue;
	private transient Double		prefilterBestFoldChange;
	private transient Double		prefilterBestABSFoldChange;

	private Long					id;

	@JsonIgnore
	public Long getID()
	{
		return id;
	}

	public void setID(Long id)
	{
		this.id = id;
	}

	public ProbeResponse getProbeResponse()
	{
		return probeResponse;
	}

	public void setProbeResponse(ProbeResponse probeResponse)
	{
		this.probeResponse = probeResponse;
	}

	public StatResult getBestStatResult()
	{
		return bestStatResult;
	}

	public void setBestStatResult(StatResult bestStatResult)
	{
		this.bestStatResult = bestStatResult;
	}

	public StatResult getBestPolyStatResult()
	{
		return bestPolyStatResult;
	}

	public void setBestPolyStatResult(StatResult bestPolyStatResult)
	{
		this.bestPolyStatResult = bestPolyStatResult;
	}

	public List<StatResult> getStatResults()
	{
		return statResults;
	}

	public void setStatResults(List<StatResult> statResults)
	{
		this.statResults = statResults;
	}

	public List<ChiSquareResult> getChiSquaredResults()
	{
		return chiSquaredResults;
	}

	public void setChiSquaredResults(List<ChiSquareResult> chiSquaredResults)
	{
		this.chiSquaredResults = chiSquaredResults;
	}

	// calculate columns and rows. The purpose of this is to agregate all the results
	// so the data can be viewed by a table.
	public void createRowData(Map<String, ReferenceGeneAnnotation> referenceGeneAnnotations,
			Double adjustedPValue, Double pValue, Double bestFoldChange)
	{
		row = new ArrayList<Object>();
		row.add(probeResponse.getProbe().getId());

		// Add the gene and gene symbol information to the data.
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
		row.add(genes.toString());
		row.add(geneSymbols.toString());
		this.genes = genes.toString();
		this.geneSymbols = geneSymbols.toString();
		for (StatResult statResult : statResults)
		{
			row.addAll(statResult.getRow());
		}

		// chi squared values
		if (chiSquaredResults != null)
		{
			for (ChiSquareResult chiSquaredResult : chiSquaredResults)
			{
				row.add(String.valueOf(chiSquaredResult.getValue()));
			}

			// chi squared p-values
			for (ChiSquareResult chiSquaredResult : chiSquaredResults)
			{
				row.add(String.valueOf(chiSquaredResult.getpValue()));
			}
		}
		// there doesn't have to be a poly result here.
		if (bestPolyStatResult != null)
		{
			row.add(String.valueOf(((PolyResult) bestPolyStatResult).getDegree()));
		}

		if (bestStatResult == null)
		{
			row.add("none");
			row.add("none");
			row.add("none");
			row.add("none");
			row.add("none");
			row.add("none");
			row.add("none");
			row.add("none");
			row.add("none");
		}
		else
		{
			row.add(bestStatResult.toString());
			row.add(bestStatResult.getBMD());
			row.add(bestStatResult.getBMDL());
			row.add(bestStatResult.getBMDU());
			row.add(bestStatResult.getFitPValue());
			row.add(bestStatResult.getFitLogLikelihood());
			row.add(bestStatResult.getAIC());
			row.add(bestStatResult.getAdverseDirection());
			row.add(bestStatResult.getBMD() / bestStatResult.getBMDL());
		}

		row.add(adjustedPValue);
		this.prefilterAdjustedPValue = adjustedPValue;

		row.add(pValue);
		this.prefilterPvalue = pValue;

		row.add(bestFoldChange);
		this.prefilterBestFoldChange = bestFoldChange;

		if (bestFoldChange == null)
			row.add(null);
		else
		{
			row.add(Math.abs(bestFoldChange));
			this.prefilterBestABSFoldChange = Math.abs(bestFoldChange);
		}

	}

	@Override
	@JsonIgnore
	public List<Object> getRow()
	{
		return row;
	}

	@JsonIgnore
	public List<String> generateColumnHeader()
	{
		List<String> columnHeader = new ArrayList<String>();
		columnHeader.add("Probe ID");
		columnHeader.add("Entrez Gene IDs");
		columnHeader.add("Genes Symbols");
		for (StatResult statResult : statResults)
		{
			columnHeader.addAll(statResult.getColumnNames());
		}

		// chisquared value headers
		if (chiSquaredResults != null)
		{
			for (ChiSquareResult chiSquaredResult : chiSquaredResults)
			{
				String header = "ChiSquare (";
				if (chiSquaredResult.getDegree1() == 1)
				{
					header += " Linear(1)";
				}
				else
				{
					header += " Polynomial " + chiSquaredResult.getDegree1();
				}
				header += " vs Polynomial " + chiSquaredResult.getDegree2() + ")";
				columnHeader.add(header);
			}

			// chisquared p-value headers
			for (ChiSquareResult chiSquaredResult : chiSquaredResults)
			{
				String header = "ChiSquare pValue (";
				if (chiSquaredResult.getDegree1() == 1)
				{
					header += "Linear";
				}
				else
				{
					header += "Polynomial " + chiSquaredResult.getDegree1();
				}
				header += " vs Polynomial " + chiSquaredResult.getDegree2() + ")";
				columnHeader.add(header);
			}
		}
		if (bestPolyStatResult != null)
		{
			columnHeader.add("Best Poly");
		}

		columnHeader.add("Best Model");
		columnHeader.add("Best BMD");
		columnHeader.add("Best BMDL");
		columnHeader.add("Best BMDU");
		columnHeader.add("Best fitPValue");
		columnHeader.add("Best fitLogLikelihood");
		columnHeader.add("Best AIC");
		columnHeader.add("Best adverseDirection");
		columnHeader.add("Best BMD/BMDL");

		return columnHeader;
	}

	@Filterable(key = "Entrez Gene ID")
	@JsonIgnore
	public String getGenes()
	{
		return genes;
	}

	public void setGenes(String genes)
	{
		this.genes = genes;
	}

	@Filterable(key = "Gene Symbols")
	@JsonIgnore
	public String getGeneSymbols()
	{
		return geneSymbols;
	}

	public void setGeneSymbols(String geneSymbols)
	{
		this.geneSymbols = geneSymbols;
	}

	/*
	 * the BMDS tool does special analysis on Poly models (Chi-Square test). This class is meant to provide a
	 * list of StatResults that are of PolyResult type. Though it could be used to get a list of other
	 * model(s) as well.
	 */
	@JsonIgnore
	public List<StatResult> getStatResultsOfClassType(Class type)
	{
		List<StatResult> returnList = new ArrayList<>();
		for (StatResult statResult : this.getStatResults())
		{
			if (statResult.getClass().equals(type))
			{
				returnList.add(statResult);
			}
		}
		return returnList;
	}

	public void refreshRowData()
	{
		row = null;

	}

	/*
	 * make charttable stuff work with best stat model
	 */

	@Filterable(key = BMDResult.BMD)
	@ChartableDataPoint(key = BMDResult.BMD)
	@JsonIgnore
	public Double getBestBMD()
	{
		if (bestStatResult == null)
			return null;
		return bestStatResult.getBMD();
	}

	@Filterable(key = BMDResult.BMDL)
	@ChartableDataPoint(key = BMDResult.BMDL)
	@JsonIgnore
	public Double getBestBMDL()
	{
		if (bestStatResult == null)
			return null;
		return bestStatResult.getBMDL();
	}

	@Filterable(key = BMDResult.FIT_PVALUE)
	@ChartableDataPoint(key = BMDResult.FIT_PVALUE)
	@JsonIgnore
	public Double getBestFitPValue()
	{
		if (bestStatResult == null)
			return null;
		return bestStatResult.getFitPValue();
	}

	@Filterable(key = BMDResult.FIT_LOG_LIKELIHOOD)
	@ChartableDataPoint(key = BMDResult.FIT_LOG_LIKELIHOOD)
	@JsonIgnore
	public Double getBestFitLogLikelihood()
	{
		if (bestStatResult == null)
			return null;
		return bestStatResult.getFitLogLikelihood();
	}

	@Filterable(key = BMDResult.BMDU)
	@ChartableDataPoint(key = BMDResult.BMDU)
	@JsonIgnore
	public Double getBestBMDU()
	{
		if (bestStatResult == null)
			return null;
		return bestStatResult.getBMDU();
	}

	@Filterable(key = BMDResult.BMD_BMDL_RATIO)
	@ChartableDataPoint(key = BMDResult.BMD_BMDL_RATIO)
	@JsonIgnore
	public Double getBestBMDdiffBMDL()
	{
		if (bestStatResult == null)
			return null;
		return bestStatResult.getBMD() / bestStatResult.getBMDL();
	}

	@Filterable(key = BMDResult.BMDU_BMDL_RATIO)
	@ChartableDataPoint(key = BMDResult.BMDU_BMDL_RATIO)
	@JsonIgnore
	public Double getBestBMDUdiffBMDL()
	{
		if (bestStatResult == null)
			return null;
		return bestStatResult.getBMDU() / bestStatResult.getBMDL();
	}

	@Filterable(key = BMDResult.BMDU_BMD_RATIO)
	@ChartableDataPoint(key = BMDResult.BMDU_BMD_RATIO)
	@JsonIgnore
	public Double getBestBMDUdiffBMD()
	{
		if (bestStatResult == null)
			return null;
		return bestStatResult.getBMDU() / bestStatResult.getBMD();
	}

	@Filterable(key = BMDResult.PREFILTER_ADJUSTEDPVALUE)
	@ChartableDataPoint(key = BMDResult.PREFILTER_ADJUSTEDPVALUE)
	@JsonIgnore
	public Double getPrefilterAdjustedPValue()
	{
		return prefilterAdjustedPValue;
	}

	@Filterable(key = BMDResult.PREFILTER_PVALUE)
	@ChartableDataPoint(key = BMDResult.PREFILTER_PVALUE)
	@JsonIgnore
	public Double getPrefilterPValue()
	{
		return prefilterPvalue;
	}

	@Filterable(key = BMDResult.BEST_FOLDCHANGE)
	@ChartableDataPoint(key = BMDResult.BEST_FOLDCHANGE)
	@JsonIgnore
	public Double getBestFoldChange()
	{
		return prefilterBestFoldChange;
	}

	@Filterable(key = BMDResult.BEST_ABSFOLDCHANGE)
	@ChartableDataPoint(key = BMDResult.BEST_ABSFOLDCHANGE)
	@JsonIgnore
	public Double getBestABSFoldChange()
	{
		return prefilterBestABSFoldChange;
	}

	@ChartableDataPointLabel
	@JsonIgnore
	public String getChartableDataLabel()
	{
		return getProbeResponse().getProbe().getId();
	}

	@Override
	public String toString()
	{
		return getProbeResponse().getProbe().getId() + " : " + genes + " : " + geneSymbols;
	}

}
