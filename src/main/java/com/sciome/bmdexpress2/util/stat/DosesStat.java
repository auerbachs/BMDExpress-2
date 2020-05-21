/*
 * DosesStat.java
 *
 * Compute doses statistics
 */

package com.sciome.bmdexpress2.util.stat;

import java.util.Vector;

public class DosesStat
{
	private int				N, groups, minReplicates;
	private double			maxDose, minDose;
	private int[]			uniOrderedIndices;
	private float[]			doses, sortedUniDoses;
	private int[][]			indices;

	private Vector<Double>	uniDoses;

	public DosesStat()
	{
	}

	public void asscendingSort(float[] doses)
	{
		N = doses.length;
		indices = new int[N][N];
		uniDoses = new Vector<Double>();

		for (int i = 0; i < N; i++)
		{
			Double varX = new Double(doses[i]);
			int row = uniDoses.indexOf(varX);

			if (row < 0)
			{
				uniDoses.add(varX);
				row = uniDoses.indexOf(varX);
				indices[row][0] = 0;
			}

			indices[row][0] += 1;
			indices[row][indices[row][0]] = i;
		}

		orderUniDoses();
	}

	private void orderUniDoses()
	{
		groups = uniDoses.size();

		if (groups > 0)
		{
			uniOrderedIndices = new int[groups];
			sortedUniDoses = new float[groups];

			for (int i = 0; i < groups; i++)
			{
				float x = uniDoses.get(i).floatValue();

				for (int j = i; j >= 0; j--)
				{
					if (j == 0 || sortedUniDoses[j - 1] <= x)
					{
						uniOrderedIndices[j] = i;
						sortedUniDoses[j] = x;
						break;
					}
					else
					{
						sortedUniDoses[j] = sortedUniDoses[j - 1];
						uniOrderedIndices[j] = uniOrderedIndices[j - 1];
					}
				}
			}
		}
	}

	private int numberDoses()
	{
		return N;
	}

	public int numberDosesGroups()
	{
		return groups;
	}

	public double minDose()
	{
		return sortedUniDoses[0];
	}

	public double maxDose()
	{
		return sortedUniDoses[groups - 1];
	}

	/**
	 * Find non-zero minimum dose
	 */
	public double noZeroMinDose()
	{
		int k = 0;

		for (int i = 0; i < groups; i++)
		{
			if (sortedUniDoses[i] > 0)
			{
				k = i;
				break;
			}
		}

		return sortedUniDoses[k];
	}

	public float[] sortedUniDoses()
	{
		return sortedUniDoses;
	}

	public int[][] dosesIndices()
	{
		int[][] dosesIndices = new int[groups][];

		for (int i = 0; i < groups; i++)
		{
			int n = indices[uniOrderedIndices[i]][0];
			dosesIndices[i] = new int[n];

			for (int j = 0; j < n; j++)
			{
				dosesIndices[i][j] = indices[uniOrderedIndices[i]][j + 1];
			}
		}

		return dosesIndices;
	}

	public int[] doseIndicesAt(int i)
	{
		return indices[uniOrderedIndices[i]];
	}

	public int minmumReplicate()
	{
		minReplicates = indices[0][0];

		for (int i = 1; i < groups; i++)
		{
			if (indices[i][0] < minReplicates)
			{
				minReplicates = indices[i][0];
			}
		}

		return minReplicates;
	}

	public String[] uniDosesToString(String offset)
	{
		String[] doses = new String[groups];

		if (offset == null)
		{
			offset = "";
		}

		for (int i = 0; i < groups; i++)
		{
			doses[i] = offset + sortedUniDoses[i];// Double.toString()
		}

		return doses;
	}

}
