package com.sciome.bmdexpress2.mvp.model.prefilter;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.IMarkable;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGene;
import com.sciome.bmdexpress2.mvp.model.refgene.ReferenceGeneAnnotation;
import com.sciome.bmdexpress2.util.NumberManager;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public class CurveFitPrefilterResult extends BMDExpressAnalysisRow
		implements Serializable, PrefilterResult, IMarkable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -465506000834082809L;

	private ProbeResponse probeResponse;
	private double pValue;
	private Float bestFoldChange;
	private Float loelDose;
	private Float noelDose;
	private String bestModel;
	private Double bmdl;
	private Double bmd;
	

	private List<Float> foldChanges;

	private List<Float> noelLoelPValues;

	@JsonIgnore
	private transient String genes;
	@JsonIgnore
	private transient String geneSymbols;
	@JsonIgnore
	private transient Set<String> geneSymbolSet;

	// row data for the table view.
	@JsonIgnore
	protected transient List<Object> row;

	private Long id;

	@Override
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
	
	
	

	public String getBestModel() {
		return bestModel;
	}

	public void setBestModel(String bestModel) {
		this.bestModel = bestModel;
	}

	public Double getBmdl() {
		return bmdl;
	}

	public void setBmdl(Double bmdl) {
		this.bmdl = bmdl;
	}

	public Double getBmd() {
		return bmd;
	}

	public void setBmd(Double bmd) {
		this.bmd = bmd;
	}

	@Override
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

	@Override
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


	@Override
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

	@Override
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
		geneSymbolSet = new HashSet<>();
		row.add(probeResponse.getProbe().getId());
		row.add(this.bestModel);
		row.add(this.bmdl);
		row.add(this.bmd);

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
				geneSymbolSet.add(refGene.getGeneSymbol());
			}
		}

		this.genes = genes.toString();
		this.geneSymbols = geneSymbols.toString();
		row.add(genes.toString());
		row.add(geneSymbols.toString());

		row.add((pValue));

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
		if (noelLoelPValues != null)
		{
			for (Float pv : noelLoelPValues)
			{
				row.add(pv);
			}
		}
		row.add(noelDose);
		row.add(loelDose);
	}

	public List<Float> getNoelLoelPValues()
	{
		return this.noelLoelPValues;
	}

	@Override
	public void setNoelLoelPValues(List<Float> fcs)
	{
		this.noelLoelPValues = fcs;
	}

	@Override
	public List<Float> getFoldChanges()
	{
		return this.foldChanges;
	}

	@Override
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

	@JsonIgnore
	@Override
	public Object getObject()
	{
		return this;
	}

	@JsonIgnore
	@Override
	public Set<String> getMarkableKeys()
	{
		if (geneSymbolSet == null)
			return new HashSet<>();
		return geneSymbolSet;
	}

	@JsonIgnore
	@Override
	public String getMarkableLabel()
	{
		return this.getGeneSymbols();
	}

	@JsonIgnore
	@Override
	public Color getMarkableColor()
	{
		return Color.YELLOW;
	}

	@Override
	public Float getLoelDose()
	{
		return loelDose;
	}

	@Override
	public void setLoelDose(Float loelDose)
	{
		this.loelDose = loelDose;
	}

	@Override
	public Float getNoelDose()
	{
		return noelDose;
	}

	@Override
	public void setNoelDose(Float noelDose)
	{
		this.noelDose = noelDose;
	}
	
	
	
	

	@Override
	public double getAdjustedPValue() {
		return pValue;
	}

}
