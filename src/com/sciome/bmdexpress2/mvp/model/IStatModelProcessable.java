package com.sciome.bmdexpress2.mvp.model;

import java.util.List;

import com.sciome.bmdexpress2.mvp.model.probe.ProbeResponse;

/*
 * The reason for this interface is to allow more than one class to be processable by bmds.  
 * In the current system, OneWay Anova and Experiment Data can be analyzed 
 * (even though they are different classes)  They implement these methods so that the code can more 
 * easily send them to be processed by the BMDSTool.
 */
public interface IStatModelProcessable
{
	public DoseResponseExperiment getProcessableDoseResponseExperiment();

	public List<ProbeResponse> getProcessableProbeResponses();

	public String getParentDataSetName();

	public LogTransformationEnum getLogTransformation();

}
