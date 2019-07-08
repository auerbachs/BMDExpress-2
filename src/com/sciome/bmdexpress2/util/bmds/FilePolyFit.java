/*
 * FilePolyFit.java
 * Created 11/2/2006
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

import com.sciome.bmdexpress2.shared.BMDExpressConstants;
import com.sciome.bmdexpress2.shared.BMDExpressProperties;
import com.sciome.bmdexpress2.util.NumberManager;

/**
 * Fit the does response data to power model
 */
public class FilePolyFit extends FileFitBase
{
	private String			polyEXE, dPath;

	private final int		SIX			= 6;
	private final double	minDouble	= -9999;
	private final String	newline		= "\n";
	private final String	space1		= " ";

	private final String[]	FLAGS		= { "Wald Confidence Interval", "Asymptotic Correlation Matrix",
			"Likelihoods of Interest", "Tests of Interest", "beta_", "fitted ", "BMD = ", "BMDL = ",
			"BMDU = " };

	public FilePolyFit(int killTime, String tmpFolder)
	{
		super(killTime);
		this.polyEXE = BMDExpressProperties.getInstance().getPolyEXE(tmpFolder);
		if (tmpFolder != null && !tmpFolder.equals(""))
			this.dPath = tmpFolder;
		else
			this.dPath = BMDExpressConstants.getInstance().TEMP_FOLDER;
	}

	@Override
	public double[] fitModel(String name, ModelInputParameters inputParameters, float[] inputX,
			float[] inputY)
	{
		String fileName = name + "_poly" + inputParameters.getPolyDegree();

		File infile = createDataFile(fileName, inputParameters, inputX, inputY);
		int OUTMAX = inputParameters.getPolyDegree() + 7;
		double[] outputs = NumberManager.initDoubles(OUTMAX, minDouble);
		outputs[3] = 0.0;

		if (infile != null)
		{
			// System.out.println("Pathf = " + infile.getPath());
			executeModel(polyEXE, infile.getPath());// infile.getAbsolutePath());
			File outFile = readOutputs(fileName, outputs);
			infile.delete();
			if (outFile != null && outFile.exists())
				outFile.delete();
			try
			{
				(new File(dPath, fileName + ".002")).delete();
			}
			catch (Exception e)
			{}
			try
			{
				(new File(dPath, fileName + "-poly.log")).delete();
			}
			catch (Exception e)
			{}
		}

		return outputs;
	}

	private File createDataFile(String fileName, ModelInputParameters inputParameters, float[] inputX,
			float[] inputY)
	{
		int degree = inputParameters.getPolyDegree();
		StringBuilder bf = new StringBuilder();
		bf.append("Polynomial" + newline);
		bf.append("BMDS MODEL RUN" + newline);
		bf.append(fileName + ".(d)" + newline);;
		bf.append(fileName + ".out" + newline);
		bf.append(degree + newline);

		bf.append(inputParameters.getInputType() + space1);
		bf.append(inputParameters.getObservations() + space1);
		bf.append(inputParameters.getAdversDirection() + newline);

		bf.append(inputParameters.getIterations() + space1);
		bf.append(inputParameters.getRelFuncConvergence() + space1);
		bf.append(inputParameters.getParamConvergence() + space1);
		bf.append(inputParameters.getBmdlCalculation() + space1);
		bf.append(inputParameters.getRestrictPolyCoef() + space1);
		bf.append(inputParameters.getBmdCalculation() + space1);
		bf.append(inputParameters.getAppend() + space1);
		bf.append(inputParameters.getSmooth() + newline);

		double bmrLevel = inputParameters.getBmrLevel();
		int bmrType = inputParameters.getBmrType();
		if (bmrType == 2)
		{
			bmrType = 1;
			bmrLevel = this.recalculateBMRFactorForRelativeDevaition(inputX, inputY,
					inputParameters.getBmrLevel());

		}

		bf.append(bmrType + space1);
		bf.append(bmrLevel + space1);

		bf.append(inputParameters.getConstantVariance() + space1);
		bf.append(inputParameters.getConfidence() + newline);

		bf.append(inputParameters.getAlpha() + space1);
		bf.append(inputParameters.getRho() + space1);

		for (int i = 0; i < degree + 1; i++)
		{
			bf.append(space1 + inputParameters.getDefNegative());
		}

		bf.append(newline);

		bf.append(inputParameters.getInitialParams() + newline);

		bf.append(inputParameters.getAlpha() + space1);
		bf.append(minDouble + space1);

		for (int i = 0; i < degree + 1; i++)
		{
			bf.append(space1 + inputParameters.getDefNegative());
		}

		bf.append(newline);
		bf.append("DOSE RESPONSE" + newline);

		for (int i = 0; i < inputX.length; i++)
		{
			bf.append(inputX[i] + space1 + inputY[i] + newline);
		}

		try
		{
			File file = new File(dPath, fileName + ".(d)");
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

	/**
	 * Pattern Matcher has poorer performance than String.indexOf() so the indeOf() is used when possible
	 */
	private File readOutputs(String fileName, double[] outputs)
	{

		if (!success)
			return new File(dPath, fileName + ".out");
		try
		{
			File file = new File(dPath, fileName + ".out");

			if (!file.exists())
			{
				checkOutFile(file);
			}

			if (file.exists())
			{
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = "";
				int status = 0;
				// Pattern pattern = Pattern.compile("(\\d+)");
				// Matcher matcher = pattern.matcher(model);

				try
				{
					while ((line = br.readLine()) != null)
					{
						line = line.trim();

						if (line.indexOf(FLAGS[0]) >= 0)
						{
							status = 1;
						}
						else if (line.indexOf(FLAGS[1]) >= 0)
						{
							status = 2;
						}
						else if (line.indexOf(FLAGS[2]) >= 0)
						{
							status = 3;
						}
						else if (line.indexOf(FLAGS[3]) >= 0)
						{
							status = 4;
						}
						else if (status == 1 && line.indexOf(FLAGS[4]) >= 0)
						{
							int idx = line.indexOf("_");
							String beta = line.substring(idx + 1, idx + 2).trim();
							line = line.substring(idx + 2).trim();
							String value = line.substring(0, line.indexOf(" ")).trim();
							int b = NumberManager.parseInt(beta, 0);
							outputs[SIX + b] = NumberManager.parseDouble(value, minDouble);
						}
						else if (status == 3 && line.indexOf(FLAGS[5]) >= 0)
						{
							int idx1 = line.indexOf(FLAGS[5]) + FLAGS[5].length();
							int idx2 = line.indexOf(" ", idx1);
							line = line.substring(idx2).trim();
							idx1 = line.indexOf(" ");
							idx2 = line.lastIndexOf(" ");

							String llh = line.substring(0, idx1).trim();
							String aic = line.substring(idx2).trim();

							outputs[4] = NumberManager.parseDouble(llh, minDouble);
							outputs[5] = NumberManager.parseDouble(aic, minDouble);

							if (polyEXE.startsWith("poly2_2"))
							{
								outputs[5] += 2;
							}
						}
						else if (status == 4 && (line.startsWith("Test 3") || line.startsWith("Test 4")))
						{
							String p = line.substring(line.lastIndexOf(" ")).trim();

							if (p.startsWith("<"))
							{
								p = p.replace('<', '0');
								p = p.replaceFirst("1", "09"); // because of '<'
							}

							outputs[3] = NumberManager.parseDouble(p, minDouble);
						}
						else if (line.indexOf(FLAGS[6]) >= 0)
						{
							String bmd = line.substring(line.indexOf(FLAGS[6]) + FLAGS[6].length()).trim();
							outputs[0] = NumberManager.parseDouble(bmd, minDouble);
						}
						else if (line.indexOf(FLAGS[7]) >= 0)
						{
							String bmdl = line.substring(line.indexOf(FLAGS[7]) + FLAGS[7].length()).trim();
							outputs[1] = NumberManager.parseDouble(bmdl, minDouble);
						}
						else if (line.indexOf(FLAGS[8]) >= 0)
						{
							String bmdu = line.substring(line.indexOf(FLAGS[8]) + FLAGS[8].length()).trim();
							outputs[2] = NumberManager.parseDouble(bmdu, minDouble);
						}
					}
					fr.close();

				}
				catch (IOException e)
				{

				}
			}

			return file;
		}
		catch (FileNotFoundException e)
		{
			return null;
		}
	}

	/**
	 * To avoid "FileNotFoundException" when read output file created by poly.exe in come cases the ".out"
	 * file is not ready immediately after the process to execute "poly.exe" so waite for the file is ready.
	 *
	 * @param file
	 *            is the file for reading
	 * @return true is the file exists or false if not exists
	 */
	private boolean checkOutFile(File file)
	{
		int loop = 0;

		while (!file.exists() && loop < 10)
		{
			Thread fthread = new Thread() {
				@Override
				public void run()
				{
					try
					{
						this.sleep(100);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			};

			fthread.start();
			loop++;
		}

		return file.exists();
	}

}
