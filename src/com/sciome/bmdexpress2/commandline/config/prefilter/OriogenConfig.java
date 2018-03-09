package com.sciome.bmdexpress2.commandline.config.prefilter;

public class OriogenConfig extends PrefilterConfig{
	private boolean mpc;
	private int initialBootstraps;
	private int maxBootstraps;
	private float s0Adjustment;
	
	public boolean isMpc() {
		return mpc;
	}
	public void setMpc(boolean mpc) {
		this.mpc = mpc;
	}
	public int getInitialBootstraps() {
		return initialBootstraps;
	}
	public void setInitialBootstraps(int initialBootstraps) {
		this.initialBootstraps = initialBootstraps;
	}
	public int getMaxBootstraps() {
		return maxBootstraps;
	}
	public void setMaxBootstraps(int maxBootstraps) {
		this.maxBootstraps = maxBootstraps;
	}
	public float getS0Adjustment() {
		return s0Adjustment;
	}
	public void setS0Adjustment(float s0Adjustment) {
		this.s0Adjustment = s0Adjustment;
	}
	
	
}
