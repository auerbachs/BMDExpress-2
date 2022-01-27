package com.sciome.bmdexpress2.mvp.model.prefilter;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({ @Type(value = OneWayANOVAResult.class, name = "onewayanovaresult"),
		@Type(value = WilliamsTrendResult.class, name = "williamstrendresult"),
		@Type(value = OriogenResult.class, name = "oriogentrendresult"),
		@Type(value = CurveFitPrefilterResult.class, name = "curvefittrendresult") })
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public interface PrefilterResult
{

	public double getpValue();

	public double getAdjustedPValue();

	public Float getBestFoldChange();

	public List<Float> getFoldChanges();

	public String getProbeID();

	public ProbeResponse getProbeResponse();

	public Float getLoelDose();

	public Float getNoelDose();

	public void setLoelDose(Float dose);

	public void setNoelDose(Float dose);

	public void setBestFoldChange(Float bestFoldChange);

	public void setFoldChanges(List<Float> foldChanges);

	public void setNoelLoelPValues(List<Float> pvalues);

}
