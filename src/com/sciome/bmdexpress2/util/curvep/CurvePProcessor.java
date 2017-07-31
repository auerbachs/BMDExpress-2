package com.sciome.bmdexpress2.util.curvep;

import java.util.ArrayList;
import java.util.List;

public class CurvePProcessor
{
	// input
	private List<Float>	doses;
	private List<Float>	responses;
	private Float		baseLineZScore;

	// results
	private List<Float>	correctedResponses	= new ArrayList<>();
	private int			numberOfCorrections	= 0;
	private Float		aUC					= 0.0f;
	private Float		wAUC				= 0.0f;

	public CurvePProcessor(List<Float> doses, List<Float> responses, Float baseLineZScore)
	{
		super();
		this.doses = doses;
		this.responses = responses;
		this.baseLineZScore = baseLineZScore;
	}

	public void calculateCorrectedListOfResponses() throws Exception
	{
		// Quantiles.percentiles()
		// Stats.of(1.0, 2.0, 2.1, 3.0).populationStandardDeviation();
		if (doses.size() != responses.size())
			throw new Exception("Doses and Responses do not corresponde");

	}

	public List<Float> getCorrectedResponses()
	{
		return correctedResponses;
	}

	public int getNumberOfCorrections()
	{
		return numberOfCorrections;
	}

	public Float getaUC()
	{
		return aUC;
	}

	public Float getwAUC()
	{
		return wAUC;
	}

	public static void main(String args[])
	{

		CurvePProcessor curvePProcessor = new CurvePProcessor(new ArrayList<>(), new ArrayList<>(), 0.0f);

		System.out.println("hello world");
		try
		{
			curvePProcessor.calculateCorrectedListOfResponses();
		}
		catch (Exception e)
		{

		}

		System.out.println("good bye world");

	}

}
