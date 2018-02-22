/*
 * 
 *
 */

package com.sciome.bmdexpress2.util.bmds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.NumberManager;

/**
 * Fit the does response data to exponential model
 */
public class FileExponentialFit extends FileFitBase
{
	private String exponentialEXE, dPath;
	private int[] intParams;
	private final int SIX = 6;
	private final int outMax = 11;
	private final double minDouble = -9999;
	private final String NEGPARAM = "-9999";
	private final String newline = "\n";
	private final String space1 = " ";
	private int expOption = 0;

	private final String[] FLAGS = { "Parameter Estimates", "Asymptotic Correlation Matrix",
			"Likelihoods of Interest", "Tests of Interest", " A1 ", " Test 1 ", "BMD = ", "BMDL = ",
			"BMDU = " };

	public FileExponentialFit(int option, int killTime)
	{
		super(killTime);
		this.exponentialEXE = BMDExpressProperties.getInstance().getExponentialEXE();
		this.dPath = BMDExpressConstants.getInstance().TEMP_FOLDER;
		this.expOption = option;
	}

	public void setAdverseDirection(int i)
	{
		intParams[2] = i;
	}

	public void setResitictPoly(int r)
	{
		intParams[5] = r;
	}

	@Override
	public double[] fitModel(String name, ModelInputParameters inputParameters, float[] inputX,
			float[] inputY)
	{
		File infile = createDataFile(name, inputParameters, inputX, inputY);
		double[] outputs = NumberManager.initDoubles(outMax, minDouble);

		if (infile != null)
		{
			// System.out.println("Pathf = " + infile.getPath());
			executeModel(exponentialEXE, infile.getPath());// infile.getAbsolutePath());
			File outFile = readOutputs("M" + expOption + name, outputs);
			infile.delete();

			if (outFile != null)
			{
				outFile.delete();
				(new File(dPath, "M" + expOption + name + "_exponential.002")).delete();
				(new File(dPath, "M" + expOption + name + "-_exponential.log")).delete();
			}
			(new File(dPath, name + "_exponential.out")).delete();
			(new File(dPath, name + "_exponential.002")).delete();
			(new File(dPath, name + "-_exponential.log")).delete();
			(new File(dPath, name + "_exponential-Exp.log")).delete();
		}

		return outputs;
	}

	private File createDataFile(String name, ModelInputParameters inputParameters, float[] inputX,
			float[] inputY)
	{

		String optionString = "";
		String span = "14";
		if (expOption == 2)
		{
			optionString = "1000";
			span = "11";
		}
		else if (expOption == 3)
		{
			optionString = "0100";
			span = "22";
		}
		else if (expOption == 4)
		{
			optionString = "0010";
			span = "33";
		}
		else if (expOption == 5)
		{
			optionString = "0001";
			span = "44";
		}

		StringBuilder bf = new StringBuilder();
		bf.append("Exponential" + newline);
		bf.append("BMDS MODEL RUN" + newline);
		bf.append(name + newline);;
		bf.append("Exponential" + newline);

		bf.append(inputParameters.getInputType() + space1);
		bf.append(inputParameters.getObservations() + space1);
		bf.append(inputParameters.getAdversDirection() + space1); // adverse direction

		// run all models
		bf.append(optionString + space1);
		bf.append(span + space1);
		bf.append("0" + space1);
		bf.append("1" + newline);

		bf.append(inputParameters.getIterations() + space1);
		bf.append(inputParameters.getRelFuncConvergence() + space1);
		bf.append(inputParameters.getParamConvergence() + space1);
		bf.append(inputParameters.getBmdlCalculation() + space1);
		bf.append(inputParameters.getBmdCalculation() + space1);
		bf.append(inputParameters.getAppend() + space1);
		bf.append(inputParameters.getSmooth() + newline);// 1.00E-08 1.00E-08 0 1 1 0 0

		bf.append(inputParameters.getBmrType() + space1);
		bf.append(inputParameters.getBmrLevel() + space1);
		bf.append(inputParameters.getConstantVariance() + space1);
		bf.append(inputParameters.getConfidence() + newline);

		for (int i = 0; i < 4; i++)
		{

			bf.append(NEGPARAM + space1 + NEGPARAM + space1 + NEGPARAM + space1 + NEGPARAM + space1 + NEGPARAM
					+ space1 + NEGPARAM + newline);
			bf.append("0" + newline);
			bf.append(NEGPARAM + space1 + NEGPARAM + space1 + NEGPARAM + space1 + NEGPARAM + space1 + NEGPARAM
					+ space1 + NEGPARAM + newline);
		}

		bf.append("DOSE RESPONSE" + newline);

		for (int i = 0; i < inputX.length; i++)
		{

			bf.append(inputX[i] + space1 + inputY[i] + newline);

		}

		try
		{
			File file = new File(dPath, name + "_exponential.(d)");
			// System.out.println(file.getAbsolutePath());
			PrintWriter out = new PrintWriter(new FileWriter(file, false));
			out.write(bf.toString());
			out.close();

			return file;
		}
		catch (IOException ie)
		{
			System.out.println("Write to file problem: " + ie);
		}

		return null;
	}

	private File readOutputs(String name, double[] outputs)
	{
		int afterSix = 1;
		List<Double> means = new ArrayList<>();
		try
		{
			File file = new File(dPath, name + "_exponential.out");
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line = "";
			int status = 0;
			Pattern patternLNAlpha = Pattern.compile(" lnalpha +(\\S+) +");// "intercept +(\\d+\\.?\\d*)");
			Pattern patternA = Pattern.compile(" a +(\\S+) +"); // " v +(-?\\d+\\.?\\d*)");
			Pattern patternB = Pattern.compile(" b +(\\S+) +"); // " n +(-?\\d+\\.?\\d*)");
			Pattern patternC = Pattern.compile(" c +(\\S+) +"); // " k +(-?\\d+\\.?\\d*)");
			Pattern patternD = Pattern.compile(" d +(\\S+) +"); // " k +(-?\\d+\\.?\\d*)");

			try
			{
				while ((line = br.readLine()) != null)
				{
					if (line.indexOf(FLAGS[0]) > 0)
					{
						status = 1;
					}
					else if (line.indexOf(FLAGS[1]) > 0)
					{
						status = 2;
					}
					else if (line.indexOf(FLAGS[2]) > 0)
					{
						status = 3;
					}
					else if (line.indexOf(FLAGS[3]) > 0)
					{
						status = 4;
					}
					else if (line.indexOf("Estimated Values of Interest") > 0)
					{
						status = 6;
					}
					else if (status == 6)
					{
						String lineTrime = line.trim();
						String[] values = lineTrime.split("\\s+");
						if (values.length > 1)
						{
							Double value = NumberManager.parseDouble(values[1], minDouble);
							if (value != minDouble)
								means.add(value);
						}
					}
					else if (line.indexOf("Other models for which likelihoods") > 0)
					{
						status = 10;
					}
					else if (status == 1)
					{// && line.indexOf(FLAGS[4]) > 0) {
						if (line.indexOf("lnalpha") >= 0)
						{
							Matcher matcher = patternLNAlpha.matcher(line);

							if (matcher.find())
							{
								String intercept = matcher.group(1);
								// System.out.println("intercept: " + intercept);
								outputs[SIX] = NumberManager.parseDouble(intercept, minDouble);
							}
						}
						else if (line.indexOf(" a  ") > 0)
						{
							Matcher matcher = patternA.matcher(line);

							if (matcher.find())
							{
								String v = matcher.group(1);
								// System.out.println("v: " + v);
								outputs[SIX + afterSix++] = NumberManager.parseDouble(v, minDouble);
							}
						}
						else if (line.indexOf(" b  ") > 0)
						{
							Matcher matcher = patternB.matcher(line);

							if (matcher.find())
							{
								String n = matcher.group(1);
								// System.out.println("n: " + n);
								outputs[SIX + afterSix++] = NumberManager.parseDouble(n, minDouble);
							}
						}
						else if (line.indexOf(" c  ") > 0)
						{
							Matcher matcher = patternC.matcher(line);

							if (matcher.find())
							{
								String k = matcher.group(1);
								// System.out.println("k: " + k);
								outputs[SIX + afterSix++] = NumberManager.parseDouble(k, minDouble);
							}
						}
						else if (line.indexOf(" d  ") > 0)
						{
							Matcher matcher = patternD.matcher(line);

							if (matcher.find())
							{
								String k = matcher.group(1);
								// System.out.println("k: " + k);
								outputs[SIX + afterSix++] = NumberManager.parseDouble(k, minDouble);
							}
						}
					}
					else if (status == 3 && line.trim().indexOf(String.valueOf(expOption)) == 0)
					{
						int idx1 = 1;
						int idx2 = line.indexOf(" ", idx1);
						line = line.substring(idx2).trim();
						idx1 = line.indexOf(" ");
						idx2 = line.lastIndexOf(" ");

						String llh = line.substring(0, idx1).trim();
						String aic = line.substring(idx2).trim();

						outputs[4] = NumberManager.parseDouble(llh, minDouble);
						outputs[5] = NumberManager.parseDouble(aic, minDouble);
						status = 10;

					}

					else if (status == 4 && (line.indexOf("Test 4") > 0 || line.indexOf("Test 5a") > 0
							|| line.indexOf("Test 6a") > 0 || line.indexOf("Test 7a") > 0))
					{
						String p = line.substring(line.lastIndexOf(" ")).trim();
						if (p.startsWith("<"))
						{
							p = p.replace('<', '0');
							p = p.replaceFirst("1", "09");
						}

						status = 5;
						outputs[3] = NumberManager.parseDouble(p, minDouble);
					}

					else if (line.indexOf(FLAGS[6]) > 0)
					{
						String bmd = line.substring(line.indexOf(FLAGS[6]) + FLAGS[6].length()).trim();
						outputs[0] = NumberManager.parseDouble(bmd, minDouble);
					}
					else if (line.indexOf(FLAGS[7]) > 0)
					{
						String bmdl = line.substring(line.indexOf(FLAGS[7]) + FLAGS[7].length()).trim();
						outputs[1] = NumberManager.parseDouble(bmdl, minDouble);
					}
					else if (line.indexOf(FLAGS[8]) > 0)
					{
						String bmdu = line.substring(line.indexOf(FLAGS[8]) + FLAGS[8].length()).trim();
						outputs[2] = NumberManager.parseDouble(bmdu, minDouble);
					}
				}

				fr.close();
			}
			catch (IOException e)
			{
				System.out.println("Read file problem: " + e);
			}

			outputs[SIX] = 1;

			int upcount = 0;
			int downcount = 0;

			// look at the trend of the curve to see if it's going up or down
			if (means.size() > 1 && means.get(0) > means.get(means.size() - 1))
			{
				for (int i = 1; i < means.size(); i++)
					if (means.get(0) < means.get(i))
						upcount++;
					else
						downcount++;
			}

			if (downcount > upcount)
				outputs[SIX] = -1;
			return file;
		}
		catch (FileNotFoundException e)
		{
			System.out.println("Input file problem: " + e);
			return null;
		}
	}

}
