package com.sciome.bmdexpress2.mvp.model.prefilter;

import java.io.Serializable;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisDataSet;
import com.sciome.bmdexpress2.mvp.model.BMDExpressAnalysisRow;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;
import com.sciome.bmdexpress2.mvp.model.IStatModelProcessable;
import com.sciome.bmdexpress2.mvp.model.LogTransformationEnum;
import com.sciome.bmdexpress2.mvp.model.info.AnalysisInfo;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;

public class OriogenResults extends BMDExpressAnalysisDataSet implements Serializable, IStatModelProcessable, PrefilterResults {
	/**
	 * 
	 */
	private static final long			serialVersionUID			= -5704632335867988973L;

	private String						name;

	private DoseResponseExperiment		doseResponseExperiment;
	private List<OriogenResult>			oriogenResults;
	private AnalysisInfo				analysisInfo;
	private transient List<String>		columnHeader;

	private Long						id;

	/* define chartabble key values */
	public static final String			UNADJUSTED_PVALUE			= "Unadjusted P-Value";
	public static final String			ADJUSTED_PVALUE				= "Adjusted P-Value";
	public static final String			NEG_LOG_ADJUSTED_PVALUE		= "Negative Log 10 Adjusted P-Value";
	public static final String			BEST_FOLD_CHANGE			= "Max Fold Change";
	public static final String			BEST_FOLD_CHANGE_ABS		= "Max Fold Change Unsigned";
	public static final String			FOLD_CHANGE					= "Fold Change";
	public static final String			GENE_ID						= "Gene ID";
	public static final String			GENE_SYMBOL					= "Gene Symbol";
	public static final String			PROBE_ID					= "Probe ID";

	public static final String			NEG_LOG_UNADJUSTED_PVALUE	= "Negative Log 10 Unadjusted P-Value";

	@Override
	public List<PrefilterResult> getPrefilterResults() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoseResponseExperiment getDoseResponseExperiement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DoseResponseExperiment getProcessableDoseResponseExperiment() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProbeResponse> getProcessableProbeResponses() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getParentDataSetName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LogTransformationEnum getLogTransformation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getColumnHeader() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getColumnHeader2() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnalysisInfo getAnalysisInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<BMDExpressAnalysisRow> getAnalysisRows() {
		// TODO Auto-generated method stub
		return null;
	}
}
