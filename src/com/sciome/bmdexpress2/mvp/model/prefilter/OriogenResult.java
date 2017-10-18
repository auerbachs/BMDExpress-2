package com.sciome.bmdexpress2.mvp.model.prefilter;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;

public class OriogenResult extends BMDExpressAnalysisRow implements Serializable, PrefilterResult {
	/**
	 * 
	 */
	private static final long			serialVersionUID	= -465506000834082809L;

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

	@Override
	public double getpValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAdjustedPValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Float getBestFoldChange() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Float> getFoldChanges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProbeID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getRow() {
		// TODO Auto-generated method stub
		return null;
	}
}
