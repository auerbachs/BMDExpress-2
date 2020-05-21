/*
 *  PerGeneBMDs.java
 *
 *  Create 9/10/2008, by Longlong Yang
 *
 *  Used to calculate per gene based BMD and BMDL values from inputed probe(s)
 *  of a given gene.
 */

package com.sciome.bmdexpress2.util.categoryanalysis;

import java.util.Vector;

public class PerGeneBMDs
{
	private String gene;
	private Vector<String> upProbes, downProbes;
	private double upBmds, upBmdls, upBmdus, upPValues, downBmds, downBmdls, downBmdus, downPValues, avgBmd,
			avgBmdl, avgBmdu, avgPValue;
	private int upCount, downCount, total, current;

	private double[] bmds, bmdls, bmdus;

	public PerGeneBMDs(int max)
	{
		total = max;

		init();
	}

	private void init()
	{
		upBmds = upBmdls = upPValues = downBmds = downBmdls = downPValues = 0;
		avgBmd = avgBmdl = avgBmdu = avgPValue = -1;
		upCount = downCount = current = 0;
		upProbes = new Vector<String>();
		downProbes = new Vector<String>();
		bmds = new double[total];
		bmdls = new double[total];
		bmdus = new double[total];
	}

	public void addDirectionUp(double bmd, double bmdl, double bmdu, double pValue, String probe)
	{
		upBmds += bmd;
		upBmdls += bmdl;
		upBmdus += bmdu;
		upPValues += pValue;
		// addBmdBmdl(bmd, bmdl);
		upCount++;

		if (probe != null)
		{
			upProbes.add(probe);
		}
	}

	public void addDirectionDown(double bmd, double bmdl, double bmdu, double pValue, String probe)
	{
		downBmds += bmd;
		downBmdls += bmdl;
		downBmdus += bmdu;
		downPValues += pValue;
		// addBmdBmdl(bmd, bmdl);
		downCount++;

		if (probe != null)
		{
			downProbes.add(probe);
		}
	}

	private void addBmdBmdl(double bmd, double bmdl, double bmdu)
	{
		bmds[current] = bmd;
		bmdus[current] = bmdu;
		bmdls[current++] = bmdl;
	}

	public void setGene(String g)
	{
		gene = g;
	}

	/**
	 * Return functions below
	 */
	public String getGene()
	{
		return gene;
	}

	public String bmdsToString(String separator)
	{
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < current; i++)
		{
			if (i > 0)
			{
				sb.append(separator);
			}

			sb.append(bmds[i]);
		}

		return sb.toString();
	}

	public String bmdlsToString(String separator)
	{
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < current; i++)
		{
			if (i > 0)
			{
				sb.append(separator);
			}

			sb.append(bmdls[i]);
		}

		return sb.toString();
	}

	public String bmdusToString(String separator)
	{
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < current; i++)
		{
			if (i > 0)
			{
				sb.append(separator);
			}

			sb.append(bmdus[i]);
		}

		return sb.toString();
	}

	public String upProbesToString(String separator)
	{
		if (upProbes.size() == 0)
		{
			return null;
		}
		else
		{
			StringBuffer bf = new StringBuffer(upProbes.get(0));

			for (int i = 1; i < upProbes.size(); i++)
			{
				bf.append(separator + upProbes.get(i));
			}

			return bf.toString();
		}
	}

	public int upCount()
	{
		return upCount;
	}

	public double upBMDs()
	{
		return upBmds;
	}

	public double upBMDLs()
	{
		return upBmdls;
	}

	public double upBMDUs()
	{
		return upBmdus;
	}

	public double upPValues()
	{
		return upPValues;
	}

	public String downProbesToString(String separator)
	{
		if (downProbes.size() == 0)
		{
			return null;
		}
		else
		{
			StringBuffer bf = new StringBuffer(downProbes.get(0));

			for (int i = 1; i < downProbes.size(); i++)
			{
				bf.append(separator + downProbes.get(i));
			}

			return bf.toString();
		}
	}

	public int downCount()
	{
		return downCount;
	}

	public double downBMDs()
	{
		return downBmds;
	}

	public double downBMDLs()
	{
		return downBmdls;
	}

	public double downBMDUs()
	{
		return downBmdus;
	}

	public double downPValues()
	{
		return downPValues;
	}

	public String conflictProbesToString(String separator)
	{
		String upProbes = upProbesToString(separator);
		String downProbes = downProbesToString(separator);

		return upProbes + separator + downProbes;
	}

	/**
	 * Average functions
	 */
	public double avgBmd()
	{
		if (avgBmd == -1)
		{
			avgBmd = (upBmds + downBmds) / (upCount + downCount);
		}

		return avgBmd;
	}

	public double avgBmdl()
	{
		if (avgBmdl == -1)
		{
			avgBmdl = (upBmdls + downBmdls) / (upCount + downCount);
		}

		return avgBmdl;
	}

	public double avgBmdu()
	{
		if (avgBmdu == -1)
		{
			avgBmdu = (upBmdus + downBmdus) / (upCount + downCount);
		}

		return avgBmdu;
	}

	public double avgPValue()
	{
		if (avgPValue == -1)
		{
			avgPValue = (upPValues + downPValues) / (upCount + downCount);
		}

		return avgPValue;
	}
}