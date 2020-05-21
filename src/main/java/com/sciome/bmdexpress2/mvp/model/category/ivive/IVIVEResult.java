package com.sciome.bmdexpress2.mvp.model.category.ivive;

import java.io.Serializable;

public abstract class IVIVEResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7856227651672012848L;
	
	private Double bmdMedianDose;
	private Double bmdlMedianDose;
	private Double bmduMedianDose;
	
	private Double bmdMeanDose;
	private Double bmdlMeanDose;
	private Double bmduMeanDose;
	
	private Double bmdMinimumDose;
	private Double bmdlMinimumDose;
	private Double bmduMinimumDose;
	
	private Double bmdFifthPercentile;
	private Double bmdlFifthPercentile;
	private Double bmduFifthPercentile;
	
	private Double bmdTenthPercentile;
	private Double bmdlTenthPercentile;
	private Double bmduTenthPercentile;
	
	public Double getBmdMedianDose() {
		return bmdMedianDose;
	}
	public void setBmdMedianDose(Double bmdMedianDose) {
		this.bmdMedianDose = bmdMedianDose;
	}
	public Double getBmdlMedianDose() {
		return bmdlMedianDose;
	}
	public void setBmdlMedianDose(Double bmdlMedianDose) {
		this.bmdlMedianDose = bmdlMedianDose;
	}
	public Double getBmduMedianDose() {
		return bmduMedianDose;
	}
	public void setBmduMedianDose(Double bmduMedianDose) {
		this.bmduMedianDose = bmduMedianDose;
	}
	public Double getBmdMeanDose() {
		return bmdMeanDose;
	}
	public void setBmdMeanDose(Double bmdMeanDose) {
		this.bmdMeanDose = bmdMeanDose;
	}
	public Double getBmdlMeanDose() {
		return bmdlMeanDose;
	}
	public void setBmdlMeanDose(Double bmdlMeanDose) {
		this.bmdlMeanDose = bmdlMeanDose;
	}
	public Double getBmduMeanDose() {
		return bmduMeanDose;
	}
	public void setBmduMeanDose(Double bmduMeanDose) {
		this.bmduMeanDose = bmduMeanDose;
	}
	public Double getBmdMinimumDose() {
		return bmdMinimumDose;
	}
	public void setBmdMinimumDose(Double bmdMinimumDose) {
		this.bmdMinimumDose = bmdMinimumDose;
	}
	public Double getBmdlMinimumDose() {
		return bmdlMinimumDose;
	}
	public void setBmdlMinimumDose(Double bmdlMinimumDose) {
		this.bmdlMinimumDose = bmdlMinimumDose;
	}
	public Double getBmduMinimumDose() {
		return bmduMinimumDose;
	}
	public void setBmduMinimumDose(Double bmduMinimumDose) {
		this.bmduMinimumDose = bmduMinimumDose;
	}
	public Double getBmdFifthPercentile() {
		return bmdFifthPercentile;
	}
	public void setBmdFifthPercentile(Double bmdFifthPercentile) {
		this.bmdFifthPercentile = bmdFifthPercentile;
	}
	public Double getBmdlFifthPercentile() {
		return bmdlFifthPercentile;
	}
	public void setBmdlFifthPercentile(Double bmdlFifthPercentile) {
		this.bmdlFifthPercentile = bmdlFifthPercentile;
	}
	public Double getBmduFifthPercentile() {
		return bmduFifthPercentile;
	}
	public void setBmduFifthPercentile(Double bmduFifthPercentile) {
		this.bmduFifthPercentile = bmduFifthPercentile;
	}
	public Double getBmdTenthPercentile() {
		return bmdTenthPercentile;
	}
	public void setBmdTenthPercentile(Double bmdTenthPercentile) {
		this.bmdTenthPercentile = bmdTenthPercentile;
	}
	public Double getBmdlTenthPercentile() {
		return bmdlTenthPercentile;
	}
	public void setBmdlTenthPercentile(Double bmdlTenthPercentile) {
		this.bmdlTenthPercentile = bmdlTenthPercentile;
	}
	public Double getBmduTenthPercentile() {
		return bmduTenthPercentile;
	}
	public void setBmduTenthPercentile(Double bmduTenthPercentile) {
		this.bmduTenthPercentile = bmduTenthPercentile;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public abstract String getName();
}
