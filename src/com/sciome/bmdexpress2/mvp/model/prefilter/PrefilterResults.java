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
@JsonSubTypes({ @Type(value = OneWayANOVAResults.class, name = "onewayanovaresults"),
		@Type(value = PathwayFilterResults.class, name = "pathwayfilterresults") })
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@ref")
public interface PrefilterResults
{
	public List<PrefilterResult> getPrefilterResults();

	public DoseResponseExperiment getDoseResponseExperiement();
}
