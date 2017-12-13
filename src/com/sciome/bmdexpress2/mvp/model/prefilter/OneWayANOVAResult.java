package com.sciome.bmdexpress2.mvp.model.prefilter;

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
import com.sciome.bmdexpress2.util.NumberManager;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class OneWayANOVAResult extends BMDExpressAnalysisRow implements Serializable, PrefilterResult
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

	@JsonIgnore
	private transient String			genes;
	@JsonIgnore
	private transient String			geneSymbols;

	// row data for the table view.
	@JsonIgnore
	protected transient List<Object>	row;

	private Long						id;

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

	@JsonIgnore
	public String getProbeID()
	{
		return probeResponse.getProbe().getId();
	}

	public String getGenes()
	{
		return genes;
	}

	@JsonIgnore
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

	public double getfValue()
	{
		return fValue;
	}

	public void setfValue(double fValue)
	{
		this.fValue = fValue;
	}

	public double getpValue()
	{
		return pValue;
	}

	@JsonIgnore
	public double getNegativeLog10pValue()
	{

		return NumberManager.negLog10(this.pValue);
	}

	public void setpValue(double pValue)
	{
		this.pValue = pValue;
	}

	@JsonIgnore
	public double getNegativeLogAdjustedPValue()
	{
		return NumberManager.negLog10(this.adjustedPValue);
	}

	public double getAdjustedPValue()
	{
		return adjustedPValue;
	}

	public void setAdjustedPValue(double adjustedPValue)
	{
		this.adjustedPValue = adjustedPValue;
	}

	public Float getBestFoldChange()
	{
		return bestFoldChange;
	}

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

		row.add((degreesOfFreedomOne));
		row.add((degreesOfFreedomTwo));
		row.add((fValue));
		row.add((pValue));
		row.add((adjustedPValue));

		if (bestFoldChange != null)
		{
			row.add((bestFoldChange));
			row.add(this.getBestFoldChangeABS());
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

	@Override
	public Object getObject()
	{
		return this;
	}

}
