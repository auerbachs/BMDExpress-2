package com.sciome.bmdexpress2.util.bmds;

public interface IBMDSToolProgress
{

	public void updateProgress(String label, double value);

	public void clearProgress();
}
