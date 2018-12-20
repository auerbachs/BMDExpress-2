package com.sciome.bmdexpress2.util.prefilter;

import java.util.ArrayList;
import java.util.List;

import com.sciome.bmdexpress2.mvp.model.probe.Treatment;

public class FoldChange
{
	List<Treatment>		treatments;

	// indexes of like dose'd doseresponses
	List<List<Integer>>	indexBuckets		= new ArrayList<>();
	boolean				isLogTransformation	= true;
	double				baseValue;
	List<Float>			foldChanges			= new ArrayList<>();

	public FoldChange(List<Treatment> treatments, boolean isLogTransformation, double baseValue)
	{
		this.treatments = treatments;
		this.isLogTransformation = isLogTransformation;
		this.baseValue = baseValue;
		Treatment prevTreatment = null;
		int i = 0;
		for (Treatment treatment : treatments)
		{
			if (prevTreatment == null || !treatment.getDose().equals(prevTreatment.getDose()))
			{
				indexBuckets.add(new ArrayList<>());

			}
			indexBuckets.get(indexBuckets.size() - 1).add(i);
			prevTreatment = treatment;
			i++;
		}

	}

	/*
	 * look at responses (which are ordered low to high in terms of dose) and compare them to the control (the
	 * lowest dose'd dosereponses)
	 * 
	 */
	public Float getBestFoldChangeValue(List<Float> responses)
	{
		Float averageB = getAverage(getListFromIndexList(indexBuckets.get(0), responses));

		Float bestFoldChange = 0.0f;
		foldChanges = new ArrayList<>();
		for (int i = 1; i < indexBuckets.size(); i++)
		{
			Float averageA = getAverage(getListFromIndexList(indexBuckets.get(i), responses));
			// perform fold change calculation
			Float thisFoldChange = performFoldCalculation(averageA, averageB);
			foldChanges.add(thisFoldChange);
			if (Math.abs(thisFoldChange) > Math.abs(bestFoldChange))
			{
				bestFoldChange = thisFoldChange;
			}
		}

		return bestFoldChange;
	}

	private Float getAverage(List<Float> floatsToAverage)
	{
		float sum = 0;
		if (floatsToAverage.size() == 0)
		{
			return 0.0f;
		}
		for (Float floatValue : floatsToAverage)
		{
			sum += floatValue;
		}

		return sum / floatsToAverage.size();
	}

	private List<Float> getListFromIndexList(List<Integer> indexes, List<Float> floatList)
	{
		List<Float> returnList = new ArrayList<>();
		for (Integer index : indexes)
		{
			returnList.add(floatList.get(index));
		}
		return returnList;
	}

	private Float performFoldCalculation(Float averageA, Float averageB)
	{
		int sign = 1;
		if (averageB > averageA)
		{
			sign = -1;
		}
		Float max = Math.max(averageA, averageB);
		Float min = Math.min(averageA, averageB);
		if (isLogTransformation)
		{
			return (float) (sign * Math.pow(baseValue, (double) (max - min)));
		}
		else
		{
			return sign * max / min;
		}
	}

	public List<Float> getFoldChanges()
	{
		return this.foldChanges;
	}

}
