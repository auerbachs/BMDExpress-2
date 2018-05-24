package com.sciome.charts.venndis;

// Copyright (C) 2014 Vladimir Ignatchenko (vladimirsign@gmail.com)
// Dr. Thomas Kislinger laboratory (http://kislingerlab.uhnres.utoronto.ca/)
//
// This file is part of VennDIS software.
// VennDIS is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// VennDIS is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with VennDIS. If not, see <http://www.gnu.org/licenses/>.

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import javafx.collections.ObservableList;

public class VennCalc {
	private int vennType = 0;
	private String headerA = "";
	private String headerB = "";
	private String headerC = "";
	private String headerD = "";
	private String headerE = "";
	private ArrayList<String> datasetA = new ArrayList<String>();
	private ArrayList<String> datasetB = new ArrayList<String>();
	private ArrayList<String> datasetC = new ArrayList<String>();
	private ArrayList<String> datasetD = new ArrayList<String>();
	private ArrayList<String> datasetE = new ArrayList<String>();
	public int[] vennOverlap = new int[32];
	private ArrayList<ArrayList <String>> vennOverlapList;
	private int a_count, b_count, c_count, d_count, e_count;
	
	public VennCalc() {
		vennOverlapList = new ArrayList<ArrayList <String>>();
		for (int i=0; i<32; i++) {
			vennOverlapList.add(new ArrayList<String>());
		}
	}
	
	public void clearVennCalc() {
		vennOverlap = new int[32];
		vennOverlapList = new ArrayList<ArrayList <String>>();
		for (int i=0; i<32; i++) {
			vennOverlapList.add(new ArrayList<String>());
		}
		datasetA = new ArrayList<String>();
		datasetB = new ArrayList<String>();
		datasetC = new ArrayList<String>();
		datasetD = new ArrayList<String>();
		datasetE = new ArrayList<String>();
		headerA = "";
		headerB = "";
		headerC = "";
		headerD = "";
		headerE = "";
	}

	private static class hashV {
		public String key = "";
		public int a = 0;
		public int b = 0;
		public int c = 0;
		public int d = 0;
		public int e = 0;
	}

	public void setA(String stringArray) {
		datasetA = new ArrayList<String>(Arrays.asList(stringArray.split("\n")));
		for (int i=0; i<datasetA.size(); i++){
			datasetA.set(i, datasetA.get(i).trim());
			if (datasetA.get(i).equals("")) {
				datasetA.remove(i);
			}
		}
	}

	public void setB(String stringArray) {
		datasetB = new ArrayList<String>(Arrays.asList(stringArray.split("\n")));
		for (int i=0; i<datasetB.size(); i++){
			datasetB.set(i, datasetB.get(i).trim());
			if (datasetB.get(i).equals("")) {
				datasetB.remove(i);
			}
		}
	}

	public void setC(String stringArray) {
		datasetC = new ArrayList<String>(Arrays.asList(stringArray.split("\n")));
		for (int i=0; i<datasetC.size(); i++){
			datasetC.set(i, datasetC.get(i).trim());
			if (datasetC.get(i).equals("")) {
				datasetC.remove(i);
			}
		}
	}

	public void setD(String stringArray) {
		datasetD = new ArrayList<String>(Arrays.asList(stringArray.split("\n")));
		for (int i=0; i<datasetD.size(); i++){
			datasetD.set(i, datasetD.get(i).trim());
			if (datasetD.get(i).equals("")) {
				datasetD.remove(i);
			}
		}
	}

	public void setE(String stringArray) {
		datasetE = new ArrayList<String>(Arrays.asList(stringArray.split("\n")));
		for (int i=0; i<datasetE.size(); i++){
			datasetE.set(i, datasetE.get(i).trim());
			if (datasetE.get(i).equals("")) {
				datasetE.remove(i);
			}
		}
	}

	public int getSetsN() {
		int n = 0;
		if (vennOverlap[1]>0) { n = 1; }
		for (int i=2; i<4; i++) {
			if (vennOverlap[i]>0) { n = 2; }
		}
		for (int i=4; i<8; i++) {
			if (vennOverlap[i]>0) { n = 3; }
		}
		for (int i=8; i<16; i++) {
			if (vennOverlap[i]>0) { n = 4; }
		}
		for (int i=16; i<32; i++) {
			if (vennOverlap[i]>0) { n = 5; }
		}
		return n;
	}

	public void setEulerType() {
		int etype = 0;
		if (vennOverlap[1]>0) { etype = etype + 1; }
		if (vennOverlap[2]>0) { etype = etype + 2; }
		if (vennOverlap[3]>0) { etype = etype + 4; }
		if (vennOverlap[4]>0) { etype = etype + 8; }
		if (vennOverlap[5]>0) { etype = etype + 16; }
		if (vennOverlap[6]>0) { etype = etype + 32; }
		if (vennOverlap[7]>0) { etype = etype + 64; }
		vennType = etype;
	}

	public ArrayList<String> getDatasetA() {
		return datasetA;
	}

	public ArrayList<String> getDatasetB() {
		return datasetB;
	}

	public ArrayList<String> getDatasetC() {
		return datasetC;
	}

	public ArrayList<String> getDatasetD() {
		return datasetD;
	}

	public ArrayList<String> getDatasetE() {
		return datasetE;
	}

	public int getSizeA() {
		int totalA = vennOverlap[1] + vennOverlap[3] + vennOverlap[5] + vennOverlap[7] + vennOverlap[9] + vennOverlap[11] + vennOverlap[13] + vennOverlap[15] + vennOverlap[17] + vennOverlap[19] + vennOverlap[21] + vennOverlap[23] + vennOverlap[25] + vennOverlap[27] + vennOverlap[29] + vennOverlap[31];
		return totalA;
	}

	public int getSizeB() {
		int totalB = vennOverlap[2] + vennOverlap[3] + vennOverlap[6] + vennOverlap[7] + vennOverlap[10] + vennOverlap[11] + vennOverlap[14] + vennOverlap[15] + vennOverlap[18] + vennOverlap[19] + vennOverlap[22] + vennOverlap[23] + vennOverlap[26] + vennOverlap[27] + vennOverlap[30] + vennOverlap[31];
		return totalB;
	}

	public int getSizeC() {
		int totalC = vennOverlap[4] + vennOverlap[5] + vennOverlap[6] + vennOverlap[7] + vennOverlap[12] + vennOverlap[13] + vennOverlap[14] + vennOverlap[15] + vennOverlap[20] + vennOverlap[21] + vennOverlap[22] + vennOverlap[23] + vennOverlap[28] + vennOverlap[29] + vennOverlap[30] + vennOverlap[31];
		return totalC;
	}

	public int getSizeD() {
		int totalD = vennOverlap[8] + vennOverlap[9] + vennOverlap[10] + vennOverlap[11] + vennOverlap[12] + vennOverlap[13] + vennOverlap[14] + vennOverlap[15] + vennOverlap[24] + vennOverlap[25] + vennOverlap[26] + vennOverlap[27] + vennOverlap[28] + vennOverlap[29] + vennOverlap[30] + vennOverlap[31];
		return totalD;
	}

	public int getSizeE() {
		int totalE = vennOverlap[16] + vennOverlap[17] + vennOverlap[18] + vennOverlap[19] + vennOverlap[20] + vennOverlap[21] + vennOverlap[22] + vennOverlap[23] + vennOverlap[24] + vennOverlap[25] + vennOverlap[26] + vennOverlap[27] + vennOverlap[28] + vennOverlap[29] + vennOverlap[30] + vennOverlap[31];
		return totalE;
	}

	public int[] getOverlap() {
		return vennOverlap;	
	}

	public ArrayList<ArrayList <String>> getOverlapList() {
		return vennOverlapList;	
	}

	public void countVenn() {
		HashMap<String, hashV> vennHashMap = new HashMap<String, hashV>();
		hashV ft = new hashV();
		for (int i=0; i<datasetA.size(); i++){
			ft = new hashV();
			vennHashMap.put(datasetA.get(i), ft); 
		}
		for (int i=0; i<datasetB.size(); i++){
			ft = new hashV();
			vennHashMap.put(datasetB.get(i), ft); 
		}
		for (int i=0; i<datasetC.size(); i++){
			ft = new hashV();
			vennHashMap.put(datasetC.get(i), ft); 
		}
		for (int i=0; i<datasetD.size(); i++){
			ft = new hashV();
			vennHashMap.put(datasetD.get(i), ft); 
		}
		for (int i=0; i<datasetE.size(); i++){
			ft = new hashV();
			vennHashMap.put(datasetE.get(i), ft); 
		}
		for (int i=0; i<datasetA.size(); i++){
			vennHashMap.get(datasetA.get(i)).a = 1; 
		}
		for (int i=0; i<datasetB.size(); i++){
			vennHashMap.get(datasetB.get(i)).b = 2;
		}
		for (int i=0; i<datasetC.size(); i++){
			vennHashMap.get(datasetC.get(i)).c = 4;
		}
		for (int i=0; i<datasetD.size(); i++){
			vennHashMap.get(datasetD.get(i)).d = 8;
		}
		for (int i=0; i<datasetE.size(); i++){
			vennHashMap.get(datasetE.get(i)).e = 16;
		}
		vennOverlapList = new ArrayList<ArrayList <String>>();
		for (int i=0; i<32; i++) {
			vennOverlapList.add(new ArrayList<String>());
		}
		a_count = 0; b_count = 0; c_count = 0; d_count = 0; e_count = 0;
		Arrays.fill(vennOverlap, 0);
		vennOverlap[0] = vennHashMap.size();
		for (Iterator it = vennHashMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			int s = vennHashMap.get(key).a+vennHashMap.get(key).b+vennHashMap.get(key).c+vennHashMap.get(key).d+vennHashMap.get(key).e;
			vennOverlap[s]++;
			vennOverlapList.get(s).add((String)key);
			if (vennHashMap.get(key).a>0) { a_count++; }
			if (vennHashMap.get(key).b>0) { b_count++; }
			if (vennHashMap.get(key).c>0) { c_count++; }
			if (vennHashMap.get(key).d>0) { d_count++; }
			if (vennHashMap.get(key).e>0) { e_count++; }
		}
	}

	public void setTable(ArrayList<ArrayList> dataArray) {
		ArrayList<hashV> dataList = new ArrayList<hashV>();
		for (int i=0; i<dataArray.size(); i++) {
			hashV hV = new  hashV();
			hV.key = dataArray.get(i).get(0).toString();
			if (!dataArray.get(i).get(1).toString().equals("") && !dataArray.get(i).get(1).toString().equals("0")) { hV.a = 1; datasetA.add(dataArray.get(i).get(0).toString()); }
			if (!dataArray.get(i).get(2).toString().equals("") && !dataArray.get(i).get(2).toString().equals("0")) { hV.b = 2; datasetB.add(dataArray.get(i).get(0).toString()); }
			if (!dataArray.get(i).get(3).toString().equals("") && !dataArray.get(i).get(3).toString().equals("0")) { hV.c = 4; datasetC.add(dataArray.get(i).get(0).toString()); }
			if (!dataArray.get(i).get(4).toString().equals("") && !dataArray.get(i).get(4).toString().equals("0")) { hV.d = 8; datasetD.add(dataArray.get(i).get(0).toString()); }
			if (!dataArray.get(i).get(5).toString().equals("") && !dataArray.get(i).get(5).toString().equals("0")) { hV.e = 16; datasetE.add(dataArray.get(i).get(0).toString()); }
			dataList.add(hV);
		}
		vennOverlapList = new ArrayList<ArrayList <String>>();
		for (int i=0; i<32; i++) {
			vennOverlapList.add(new ArrayList<String>());
		}
		a_count = 0; b_count = 0; c_count = 0; d_count = 0; e_count = 0;
		Arrays.fill(vennOverlap, 0);
		for (int i=0; i<dataList.size(); i++) {
			int s = dataList.get(i).a + dataList.get(i).b + dataList.get(i).c + dataList.get(i).d + dataList.get(i).e;
			vennOverlap[s]++;
			vennOverlapList.get(s).add(dataList.get(i).key);
			if (dataList.get(i).a>0) { a_count++; }
			if (dataList.get(i).b>0) { b_count++; }
			if (dataList.get(i).c>0) { c_count++; }
			if (dataList.get(i).d>0) { d_count++; }
			if (dataList.get(i).e>0) { e_count++; }
		}
	}

	public int getVennType() {
		return vennType;
	}

	public void setVennType(int vennType) {
		this.vennType = vennType;
	}

	public String getHeaderA() {
		return headerA;
	}

	public void setHeaderA(String headerA) {
		this.headerA = headerA;
	}

	public String getHeaderB() {
		return headerB;
	}

	public void setHeaderB(String headerB) {
		this.headerB = headerB;
	}

	public String getHeaderC() {
		return headerC;
	}

	public void setHeaderC(String headerC) {
		this.headerC = headerC;
	}

	public String getHeaderD() {
		return headerD;
	}

	public void setHeaderD(String headerD) {
		this.headerD = headerD;
	}

	public String getHeaderE() {
		return headerE;
	}

	public void setHeaderE(String headerE) {
		this.headerE = headerE;
	}
}









