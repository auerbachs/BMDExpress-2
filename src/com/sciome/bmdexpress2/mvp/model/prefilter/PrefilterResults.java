package com.sciome.bmdexpress2.mvp.model.prefilter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.DoseResponseExperiment;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = OneWayANOVAResults.class, name = "onewayanovaresults") })
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public interface PrefilterResults
{
	/* define chartabble key values */
	public static final String	FVALUE					= "F-Value";
	public static final String	UNADJUSTED_PVALUE		= "Unadjusted P-Value";
	public static final String	ADJUSTED_PVALUE			= "Adjusted P-Value";
	public static final String	BEST_FOLD_CHANGE		= "Max Fold Change";
	public static final String	BEST_FOLD_CHANGE_ABS	= "Max Fold Change Unsigned";
	public static final String	FOLD_CHANGE				= "Fold Change";
	public static final String	GENE_ID					= "Gene ID";
	public static final String	GENE_SYMBOL				= "Gene Symbol";
	public static final String	PROBE_ID				= "Probe ID";
	public static final String	DF1						= "Df1";
	public static final String	DF2						= "Df2";

	public List<PrefilterResult> getPrefilterResults();

	public DoseResponseExperiment getDoseResponseExperiement();
}
