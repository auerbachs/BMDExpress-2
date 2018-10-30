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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sciome.bmdexpress2.mvp.model.probe.Probe;

public class BMDoseModel
{
	private String		name, identifier;
	private double		minDose, maxDose, minResponse, maxResponse, mean;
	private double[]	doses, responses, parameters;
	private double[][]	estimates;
	private int			row, col, degree;

	private final int	base	= 6;										// parameter index of intercept

	public BMDoseModel()
	{
	}

	/**
	 * Class Constructor
	 *
	 * @params name is the name of a model, e.g.. 'Power'
	 * @params id is the identifier of the data set, e.g. a probe
	 */
	public BMDoseModel(String name, Probe probe)
	{
		this.name = name;
		identifier = probe.getId();
		degree = BMDoseModel.getDegree(name);
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
		return name;
	}

	/**
	 * @return id is the identifier of the data, e.g. a probe name
	 */
	public String getID()
	{
		return identifier;
	}

	/**
	 * @return the formula of a model (name)
	 */
	public String getFormula()
	{
		String formula = "";

		if (name.startsWith("Power"))
		{
			formula = "y[dose] = control + slop * dose^power";
		}
		else if (name.startsWith("Hill"))
		{
			formula = "intercept + v * dose^n/(k^n + dose^n)";
		}
		else if (name.startsWith("Linear") || name.startsWith("Polynomial"))
		{
			if (degree > 0)
			{ // should be always true
				StringBuilder sb = new StringBuilder("y[dose] = beta_0 + beta_1 * dose"); // degree == 1

				for (int d = 2; d <= degree; d++)
				{
					sb.append(" + beta_" + d + " * dose^" + d);
				}

				formula = sb.toString();
			}
		}
		else if (name.startsWith("Exp 2"))
		{
			formula = "a * exp(b * dose)";
		}
		else if (name.startsWith("Exp 3"))
		{
			formula = "a * exp((b * dose)^d)";
		}
		else if (name.startsWith("Exp 4"))
		{
			formula = "a * (c - (c - 1) * exp(-b * dose))";
		}
		else if (name.startsWith("Exp 5"))
		{
			formula = "a * (c - (c - 1) * exp(-(b * dose)^d))";
		}

		return formula;
	}

	public String getModelEquation(String model)
	{
		StringBuilder sb = new StringBuilder("RESPONSE = " + parameters[base]);

		if (model.equals("Power"))
		{
			if (parameters[base + 1] >= 0)
			{
				// the parameter is positive, so set display with a +
				sb.append(" + " + parameters[base + 1] + " * DOSE^");
			}
			else
			{
				// the parameter is negative, set display with a -
				sb.append(" " + parameters[base + 1] + " * DOSE^");
			}

			if (parameters[base + 2] >= 0)
			{
				sb.append(parameters[base + 2]);
			}
			else
			{
				sb.append("(" + parameters[base + 2] + ")");
			}
		}
		else if (model.equals("Hill"))
		{
			// Y[dose] = intercept + v*dose^n/(k^n + dose^n)
			if (parameters[base + 1] >= 0)
			{
				sb.append(" + " + parameters[base + 1] + " * DOSE^");
			}
			else
			{
				sb.append(" " + parameters[base + 1] + " * DOSE^");
			}

			String paramN = Double.toString(parameters[base + 2]);

			if (parameters[base + 2] < 0)
			{
				paramN = "(" + parameters[base + 2] + ")";
			}

			sb.append(paramN + "/(");

			if (parameters[base + 3] >= 0)
			{
				sb.append(parameters[base + 3] + "^" + paramN + " + DOSE^" + paramN + ")");
			}
			else
			{
				sb.append("(" + parameters[base + 2] + ")^" + paramN + " + DOSE^" + paramN + ")");
			}
		}
		else if (model.equals("Exp 2"))
		{
			sb = new StringBuilder("RESPONSE = ");
			double a = parameters[base + 1];
			double b = parameters[base + 2];
			sb.append(a + " * EXP(" + parameters[6] + " * " + b + " * DOSE)");
		}
		else if (model.equals("Exp 3"))
		{
			sb = new StringBuilder("RESPONSE = ");
			double a = parameters[base + 1];
			double b = parameters[base + 2];
			double d = parameters[base + 3];
			sb.append(a + " * EXP((" + parameters[6] + " * " + b + " * DOSE)^" + d + ")");
		}
		else if (model.equals("Exp 4"))
		{
			sb = new StringBuilder("RESPONSE = ");
			double a = parameters[base + 1];
			double b = parameters[base + 2];
			double c = parameters[base + 3];
			sb.append(a + " * (" + c + " - (" + c + " - 1) * EXP(-" + b + " * DOSE))");
		}
		else if (model.equals("Exp 5"))
		{
			sb = new StringBuilder("RESPONSE = ");
			double a = parameters[base + 1];
			double b = parameters[base + 2];
			double c = parameters[base + 3];
			double d = parameters[base + 4];
			sb.append(a + " * (" + c + " - (" + c + " - 1) * EXP(-(" + b + " * DOSE)^" + d + "))");
		}
		else
		{ // Linear (1d) and Polinominal d
			int degree = getDegree(model); // degree = 1, 2, 3...

			if (degree > 0)
			{
				for (int i = 1; i <= degree; i++)
				{
					if (parameters[base + i] >= 0)
					{ // positive
						sb.append(" + " + parameters[base + i] + " * DOSE");
					}
					else
					{ // negative
						sb.append(" " + parameters[base + i] + " * DOSE");
					}

					if (i > 1)
					{
						sb.append("^" + i);
					}
				}
			}
		}

		return sb.toString();
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
		double response = 0;

		if (name.startsWith("Power"))
		{
			response = powerFunction(dose);
		}
		else if (name.startsWith("Hill"))
		{
			response = hillFunction(dose);
		}
		else if (name.startsWith("Exp 2"))
		{
			response = exp2Function(dose);
		}
		else if (name.startsWith("Exp 3"))
		{
			response = exp3Function(dose);
		}
		else if (name.startsWith("Exp 4"))
		{
			response = exp4Function(dose);
		}
		else if (name.startsWith("Exp 5"))
		{
			response = exp5Function(dose);
		}
		else if (name.startsWith("Linear") || name.startsWith("Poly"))
		{
			response = polyFunction(dose, degree);
		}

		return response;
	}

	/**
	 * Hill functioin
	 */
	private double hillFunction(double dose)
	{
		double nom = parameters[base + 1] * Math.pow(dose, parameters[base + 2]);
		double denom = Math.pow(parameters[base + 3], parameters[base + 2])
				+ Math.pow(dose, parameters[base + 2]);

		return parameters[base] + nom / denom;
	}

	/**
	 * Exp functioin
	 */
	private double exp2Function(double dose)
	{
		double a = parameters[base + 1];
		double b = parameters[base + 2];

		return a * Math.exp(parameters[base] * b * dose);
	}

	/**
	 * Exp functioin
	 */
	private double exp3Function(double dose)
	{
		double a = parameters[base + 1];
		double b = parameters[base + 2];
		double d = parameters[base + 3];

		double expvalue = Math.pow(b * dose, d);
		return a * Math.exp(parameters[base] * expvalue);
	}

	/**
	 * Exp functioin
	 */
	private double exp4Function(double dose)
	{
		double a = parameters[base + 1];
		double b = parameters[base + 2];
		double c = parameters[base + 3];
		return a * (c - (c - 1) * Math.exp(-b * dose));
	}

	/**
	 * Exp functioin
	 */
	private double exp5Function(double dose)
	{
		double a = parameters[base + 1];
		double b = parameters[base + 2];
		double c = parameters[base + 3];
		double d = parameters[base + 4];
		double expvalue = Math.pow(b * dose, d);
		return a * (c - (c - 1) * Math.exp(-expvalue));
	}

	/**
	 * Power function
	 */
	private double powerFunction(double dose)
	{
		return parameters[base] + parameters[base + 1] * Math.pow(dose, parameters[base + 2]);
	}

	/**
	 * Linear or polynomial 1 function
	 */
	private double linearFunction(double dose)
	{
		return parameters[base] + parameters[base + 1] * dose;
	}

	/**
	 * Polynomial 2 function private double poly2Function(double dose) { return parameters[3] + parameters[4]
	 * * dose + parameters[5] * Math.pow(dose, 2); }
	 */

	/**
	 * Polynomial 3 function private double poly3Function(double dose) { return parameters[3] + parameters[4]
	 * * dose + parameters[5] * Math.pow(dose, 2) + parameters[6] * Math.pow(dose, 3); }
	 */

	/**
	 * Polynomial dynamic degree function
	 */
	private double polyFunction(double dose, int degree)
	{
		int start = base + 1;

		return parameters[base] + polyValue(dose, start, 1, degree);
	}

	/**
	 * Recursive function
	 */
	private double polyValue(double dose, int index, int cur, int max)
	{
		if (cur == max)
		{
			return parameters[index] * Math.pow(dose, max);
		}
		else
		{
			return parameters[index] * Math.pow(dose, cur) + polyValue(dose, index + 1, cur + 1, max);
		}
	}

	public static int getDegree(String model)
	{
		Pattern pattern = Pattern.compile("(\\d+)");
		Matcher matcher = pattern.matcher(model);
		int degree = 0;
		if (model.toLowerCase().equals("linear"))
		{
			degree = 1;
		}

		if (matcher.find())
		{
			String st = matcher.group(1);
			degree = Integer.parseInt(st); // degree = 1, 2, 3...
		}

		return degree;
	}
}
