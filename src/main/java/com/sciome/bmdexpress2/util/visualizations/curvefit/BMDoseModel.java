/**
 * BMDoseModel.java
 *
 * Created 10/18/2007
 *
 * Based on parameters of a Benchmark Dose model and statistical
 * estimates of OneWayAnova analysis of a set of dose-response
 * data, calculate and return values used for BMD graphicview
 */

package com.sciome.bmdexpress2.util.visualizations.curvefit;

import com.sciome.bmdexpress2.mvp.model.probe.Probe;
import com.sciome.bmdexpress2.mvp.model.stat.StatResult;

public class BMDoseModel
{
	private String identifier;
	private double minDose, maxDose, minResponse, maxResponse, mean;
	private double[] doses, responses, parameters;
	private double[][] estimates;
	private int row, col;
	private StatResult theStatResult;

	public BMDoseModel()
	{
	}

	/**
	 * Class Constructor
	 *
	 * @params name is the name of a model, e.g.. 'Power'
	 * @params id is the identifier of the data set, e.g. a probe
	 */
	public BMDoseModel(StatResult theStatResult, Probe probe)
	{
		this.theStatResult = theStatResult;
		identifier = probe.getId();
	}

	/**
	 * Set the parameters as a result from BMD fit models
	 *
	 * @params are values returned from BMD models
	 */
	public void setParameters(double[] params)
	{
		parameters = params;
	}

	/**
	 * Set the estimates of OneWayAnova of a dose-response data set
	 *
	 */
	public void setEstimates(double[][] estimates)
	{
		this.estimates = estimates;
		readEstimates();
	}

	private void readEstimates()
	{
		row = estimates.length;
		col = estimates[0].length;
		double sum = 0;
		double weight = 0;

		for (int i = 0; i < row; i++)
		{
			if (i == 0)
			{
				minDose = estimates[0][0];
				maxDose = estimates[row - 1][0];
				minResponse = estimates[0][col - 2];
				maxResponse = estimates[0][col - 1];
			}
			else
			{
				if (estimates[i][col - 2] < minResponse)
				{
					minResponse = estimates[i][col - 2];
				}

				if (estimates[i][col - 1] > maxResponse)
				{
					maxResponse = estimates[i][col - 1];
				}
			}

			sum += estimates[i][1] * estimates[i][2];
			weight += estimates[i][1];
		}

		mean = sum / weight;
	}

	/**
	 * @return name is the model name
	 */
	public String getName()
	{
		return this.theStatResult.getModel();
	}

	/**
	 * @return id is the identifier of the data, e.g. a probe name
	 */
	public String getID()
	{
		return identifier;
	}

	/**
	 * @return the number of distinct doses
	 */
	public int numberOfDoses()
	{
		return row;
	}

	/**
	 * @return the minimum of doses determining x-y ranges
	 */
	public double minimumDose()
	{
		return minDose;
	}

	/**
	 * @return the maximum of doses determining x-y ranges
	 */
	public double maximumDose()
	{
		return maxDose;
	}

	/**
	 * @return the minimum response determining x-y ranges
	 */
	public double minimumResponse()
	{
		return minResponse;
	}

	/**
	 * @return the maximum response determining x-y ranges
	 */
	public double maximumResponse()
	{
		return maxResponse;
	}

	/**
	 * @param i
	 *            is the row index of a two-D array
	 * @return estimates of a dose i
	 */
	public double[] doseValues(int i)
	{
		return estimates[i];
	}

	/**
	 * @param i
	 *            is the row index of a two-D array
	 * @return the dose i
	 */
	public double doseAt(int i)
	{
		return estimates[i][0];
	}

	/**
	 * @param i
	 *            is the row index of a two-D array
	 * @return the mean of dose i
	 */
	public double meanAt(int i)
	{
		return estimates[i][2];
	}

	/**
	 * @param i
	 *            is the row index of a two-D array
	 * @return lower (left) confidence interval of dose i
	 */
	public double ciLeft(int i)
	{
		return estimates[i][col - 2];
	}

	/**
	 * @param i
	 *            is the row index of a two-D array
	 * @return upper (right) confidence interval of dose i
	 */
	public double ciRight(int i)
	{
		return estimates[i][col - 1];
	}

	/**
	 * @return the bmd value of named BMD model
	 */
	public double bmd()
	{
		return parameters[0];
	}

	/**
	 * @return the bmdl value of named BMD model
	 */
	public double bmdl()
	{
		return parameters[1];
	}

	/**
	 * @return the bmdl value of named BMD model
	 */
	public double bmdu()
	{
		return parameters[2];
	}

	/**
	 * @return the mean of responses of a dose-response data set
	 */
	public double meanAll()
	{
		return mean;
	}

	/**
	 * @param dose
	 *            is a dose value for estimating response given a model and parameters
	 *
	 * @return the the response calculated based on the formula and the parameters given a model
	 */
	public double response(double dose)
	{

		return this.theStatResult.getResponseAt(dose);

	}

}
