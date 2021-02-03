package com.toxicR.model;

import com.fasterxml.jackson.annotation.JsonSetter;

public class NormalDeviance {

	Double A1;
	Integer N1;
	Double A2;
	Integer N2;
	Double A3;
	Integer N3;
	
	
	public Double getA1() {
		return A1;
	}
	
	@JsonSetter("A1")
	public void setA1(Double a1) {
		A1 = a1;
	}
	public Integer getN1() {
		return N1;
	}
	
	@JsonSetter("N1")
	public void setN1(Integer n1) {
		N1 = n1;
	}
	public Double getA2() {
		return A2;
	}
	
	@JsonSetter("A2")
	public void setA2(Double a2) {
		A2 = a2;
	}
	public Integer getN2() {
		return N2;
	}
	
	@JsonSetter("N2")
	public void setN2(Integer n2) {
		N2 = n2;
	}
	public Double getA3() {
		return A3;
	}
	
	@JsonSetter("A3")
	public void setA3(Double a3) {
		A3 = a3;
	}
	public Integer getN3() {
		return N3;
	}
	
	@JsonSetter("N3")
	public void setN3(Integer n3) {
		N3 = n3;
	}
	
	
}
