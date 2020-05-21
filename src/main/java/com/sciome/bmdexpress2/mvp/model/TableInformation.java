package com.sciome.bmdexpress2.mvp.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TableInformation {
	private Map<String, Boolean>								williamsTrendMap;
	private LinkedList<String>									williamsTrendOrder;
	private Map<String, Boolean>								oneWayMap;
	private LinkedList<String>									oneWayOrder;
	private Map<String, Boolean>								oriogenMap;
	private LinkedList<String>									oriogenOrder;
	private Map<String, Boolean>								bmdMap;
	private LinkedList<String>									bmdOrder;
	private Map<String, Boolean>								categoryAnalysisMap;
	private LinkedList<String>									categoryAnalysisOrder;
	
	public TableInformation() {
		this.williamsTrendMap = new HashMap<String, Boolean>();
		this.oneWayMap = new HashMap<String, Boolean>();
		this.oriogenMap = new HashMap<String, Boolean>();
		this.bmdMap = new HashMap<String, Boolean>();
		this.categoryAnalysisMap = new HashMap<String, Boolean>();
		
		this.williamsTrendOrder = new LinkedList<String>();
		this.oneWayOrder = new LinkedList<String>();
		this.oriogenOrder = new LinkedList<String>();
		this.bmdOrder = new LinkedList<String>();
		this.categoryAnalysisOrder = new LinkedList<String>();
	}

	public Map<String, Boolean> getWilliamsTrendMap() {
		return williamsTrendMap;
	}

	public void setWilliamsTrendMap(Map<String, Boolean> williamsTrendMap) {
		this.williamsTrendMap = williamsTrendMap;
	}

	public LinkedList<String> getWilliamsTrendOrder() {
		return williamsTrendOrder;
	}

	public void setWilliamsTrendOrder(LinkedList<String> williamsTrendOrder) {
		this.williamsTrendOrder = williamsTrendOrder;
	}

	public Map<String, Boolean> getOneWayMap() {
		return oneWayMap;
	}

	public void setOneWayMap(Map<String, Boolean> oneWayMap) {
		this.oneWayMap = oneWayMap;
	}

	public LinkedList<String> getOneWayOrder() {
		return oneWayOrder;
	}

	public void setOneWayOrder(LinkedList<String> oneWayOrder) {
		this.oneWayOrder = oneWayOrder;
	}

	public Map<String, Boolean> getOriogenMap() {
		return oriogenMap;
	}

	public void setOriogenMap(Map<String, Boolean> oriogenMap) {
		this.oriogenMap = oriogenMap;
	}

	public LinkedList<String> getOriogenOrder() {
		return oriogenOrder;
	}

	public void setOriogenOrder(LinkedList<String> oriogenOrder) {
		this.oriogenOrder = oriogenOrder;
	}

	public Map<String, Boolean> getBmdMap() {
		return bmdMap;
	}

	public void setBmdMap(Map<String, Boolean> bmdMap) {
		this.bmdMap = bmdMap;
	}

	public LinkedList<String> getBmdOrder() {
		return bmdOrder;
	}

	public void setBmdOrder(LinkedList<String> bmdOrder) {
		this.bmdOrder = bmdOrder;
	}

	public Map<String, Boolean> getCategoryAnalysisMap() {
		return categoryAnalysisMap;
	}

	public void setCategoryAnalysisMap(Map<String, Boolean> categoryAnalysisMap) {
		this.categoryAnalysisMap = categoryAnalysisMap;
	}

	public LinkedList<String> getCategoryAnalysisOrder() {
		return categoryAnalysisOrder;
	}

	public void setCategoryAnalysisOrder(LinkedList<String> categoryAnalysisOrder) {
		this.categoryAnalysisOrder = categoryAnalysisOrder;
	}
}
