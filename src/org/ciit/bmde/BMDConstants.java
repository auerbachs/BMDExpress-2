/**
 *  BMDConstants.java
 *  Created 7/17/2007 By Longlong Yang
 *
 *  Define constants objects used for Affymetrix Otholog project
 */

package org.ciit.bmde;


public class BMDConstants {
    //first output to mark the file as .BMD project
    public static final String BMDTAG = "[BMD]";
    //public static final String COLONSPACE = ": ";
    public static final String WORKSOURCE = "Work Source";
    public static final String newLine = "\n",
                               tab = "\t",
                               semiColon = ";",
                               comma = ",",
                               COLONSPACE = ": ";

    public static final String[] pFilters = {"0.01", "0.05", "0.10", "0.5", "1"};

    public static final String[] dataTypes = {
            "Expression Data",
            "One-way ANOVA",
            "Benchmark Dose Analyses",
            "Functional Classifications"};

            //"Threshold Detection Analyses",
            //"Dose-Responses Data",
            //"NONE"};
            //"Signaling Pathway Analyses",
            //"Defined Category Analyses"

    public static final String[] TOOLITEMS = {
            "One-way ANOVA",
            "Benchmark Dose Analyses",
            "Gene Ontology Analyses",
            "Signaling Pathway Analyses",
            "Defined Category Analyses"};

            //"Threshold Detection Analyses"};
            //"Fit Multistage Model",

    /* DATATOOLS defines abaliable TOOLITEMS for each dataTypes above */
    public static final int[][] DATATOOLS = {
            {0, 1},
            {1},
            {2, 3, 4},
            {}};

            //{},
            //{6},

    public static final String[] TTEST_METHODS = {
            "Assume Equal Variance",
            "Assume Unequal Variance"};

    public static final String[] TTEST_COLUMN_NAMES = {
            "ID",
            "Control df",
            "Control Mean",
            "Treatment df",
            "Treatment Mean",
            "t-Value",
            "p-Value",
            "Adjusted p-Value"};

    public static final String[] goCategories = {
            "universal",
            "biological_process",
            "cellular_component",
            "molecular_function"};

    public static final String[] staColNames = {
            "BMD Mean",
            "BMD Median",
            "BMD Minimum",
            "BMD SD",
            "BMD wMean",
            "BMD wSD",
            //"BMD at Significant GO Reduction (p=",
            "BMD at Significant GO Enrichment (p=",
            "BMD <= Highest Dose at Significant GO Enrichment (p=",
            "BMDL Mean",
            "BMDL Median",
            "BMDL Minimum",
            "BMDL SD",
            "BMDL wMean",
            "BMDL wSD",
            //"BMDL at Significant GO Reduction (p=",
            "BMDL at Significant GO Enrichment (p=",
            "BMDL <= Highest Dose at Significant GO Enrichment (p=",
            "5th Percentile Index",
            "BMD at 5th Percentile of Total Genes",
            "10th Percentile Index",
            "BMD at 10th Percentile of Total Genes",
            "BMD List",
            "BMDL List",
            "Probes with Adverse Direction Up",
            "Probes with Adverse Direction Down"};

    public static final String[] AdverseColNames = {
            "Genes with Adverse Direction Up Count",
            "Genes Up List",
            "Genes Up Probes List",
            "Genes Up BMD Mean",
            "Genes Up BMD Median",
            "Genes Up BMD SD",
            "Genes Up BMDL Mean",
            "Genes Up BMDL Median",
            "Genes Up BMDL SD",
            "BMD list (up)",
            "BMDL list (up)",
            "Genes with Adverse Direction Down Count",
            "Genes Down List",
            "Genes Down Probes List",
            "Genes Down BMD Mean",
            "Genes Down BMD Median",
            "Genes Down BMD SD",
            "Genes Down BMDL Mean",
            "Genes Down BMDL Median",
            "Genes Down BMDL SD",
            "BMD list (Down)",
            "BMDL list (Down)",
            "Genes with Adverse Confilct Count",
            "Genes Confilct List",
            "Genes Confilct Probes List",
            "BMD list (Confilct)",
            "BMDL list (Confilct)"};

    public static final String[] ANNOTATION = {
            "Probe to Genes",
            "Gene to Probes"};
}