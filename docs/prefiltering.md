[Video tutorial demonstrating how to setup statistical filtering](https://www.youtube.com/watch?v=YmzF4rXagzo&list=PLX2Rd5DjtiTeR84Z4wRSUmKYMoAbilZEc&index=6)

The number of probes contained within a given genomics platform is typically on the order of tens of thousands. By selecting only probes with a statistically significant dose response, the computation required to complete the subsequent analysis steps is minimized. ANOVA, Williams trend [4], ORIOGEN (Order Restricted Inference for Ordered Gene Expression) [5], and fold change filters can be used to remove probes with small or statistically insignificant dose dependent expression changes. **Note:** The user can model all probes that are loaded without filtering beforehand. We recommend filtering to reduce noise and modeling run time.

The ANOVA test is a test of the null hypothesis that the responses at the different doses are all the same. The alternative hypothesis for ANOVA is that the responses are not all the same, with no restriction on the direction of change of the responses.

The two-sided William’s trend test compares a null hypothesis of no dose response with an alternative hypothesis of monotonically increasing or monotonically decreasing response (i.e., a response that either never decreases with increasing dose, or never decreases with increasing dose, with at least one change in response with increasing dose). An isotonic regression (nonparametric regression that fits a monotonic response to the data) is used to obtain estimates of dose specific response and resulting test statistics.

ORIOGEN utilizes non-parametric to simultaneously identify significant genes and groups them according to various patterns of inequalities. In the implementation of ORIOGEN in BMDExpress 2, the overall significance p-value for a gene is computed by testing null hypothesis of no dose response against union of alternate dose response profiles (such as monotone, umbrella shaped etc.).  This method  maximizes computation efficiency by utilizing adaptive bootstrap techniques during significance p-value computation and multiple correction stages. 

After choosing a data set(s) from the Data Tree, select 'One-way ANOVA', 'Williams Trend', or 'ORIOGEN' from the 'Tools' menu.

![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/select-anova.png?raw=true)

[Video tutorial describing data filtering setup in detail](https://www.youtube.com/watch?v=YmzF4rXagzo&index=6&list=PLX2Rd5DjtiTeR84Z4wRSUmKYMoAbilZEc)

### One-Way ANOVA / Williams Trend Test Options

-   **P-value Cutoff:** A filter based on the *p*-value. Set to 0.05 by default; also includes 0.1 and 0.01 as default options, but you can enter any value.
-   **Multiple Testing Correction:** False discovery rate correction applied to the selected *p*-value.[2]
-   **Filter Out Control Genes:** Remove platform specific internal control genes (e.g. AFFX\_xxxxx) from the analysis.

Configure ANOVA/Williams and Fold Change options. Click 'Start'.

![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/popup-anova-parameters.png?raw=true)

### ORIOGEN Options

-   **P-Value Cutoff:** A filter based on the *p*-value. Set to 0.05 by default; also includes 0.1 and 0.01 as default options, but you can enter any value.
-   **Number of Initial Bootstrap Samples:** ORIOGEN uses an adaptive bootstrap p-value computation to maximize computational efficiency. This option sets the starting number of bootstrap samples used to compute the p-value for all the probes. ORIOGEN will start with this number of bootstrap samples and then gradually increase it, if necessary, until the number of samples reaches the maximum (set in the next option).
-   **Number of Maximum Bootstrap Samples:** Maximum number of bootstrap samples used to compute the p-values for all the probes
-   **Shrinkage Adjustment Percentile:** Used to control for false positives that can be identified when probes exhibit minimal variability. Default is set to 5.0, which reflects 5th percentile standard deviation of all probes in the data set. As the parameter decreases fewer probes are likely to pass the filter. 
-   **Multiple Testing Correction:** False discovery rate correction.[2]
-   **Filter Out Control Genes:** Remove platform specific internal control genes (e.g. AFFX\_xxxxx) from the analysis.

Configure ORIOGEN and Fold Change options. Click 'Start'.

![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/popup-oriogen-parameters.png?raw=true)
#### Fold Change Parameters

-   **Use Fold Change Filter:** If this option is unchecked, all other options in this section will be disabled.
-   **Log Transformation:** Log base 2 transformation is the default for computing fold change, but base 10, *e*, or no transformation are also available as options should they be appropriate to your data set. If your data is log transformed when it is loaded into BMDExpress then the "Log Transformation" box should be checked with the appropriate base selected.
-   **Fold Change**: A minimum fold change for inclusion in the BMD computation may be selected.

### Prefilter Results

[Video tutorial describing data filtering results](https://www.youtube.com/watch?v=YDOwjQtfLLc&index=7&list=PLX2Rd5DjtiTeR84Z4wRSUmKYMoAbilZEc)

Results are tabulated in the results table. Output consists of:

-   **Probe ID:** Unique identifier for the probe/probe set in the row.
-   **Genes:** List of genes included in unique probe/probe set.
-   **Gene Symbols:** Gene symbols included in probe/probe set.
-   **Df1:** Between dose group degrees of freedom (ANOVA only)
-   **Df2:** Within dose group degrees of freedom (ANOVA only)
-   **F-Value:** F-value from the one-way ANOVA. An F-value is defined as the variation between sample means / variation within the samples.
-   **P-Value:** Nominal P-value from the ANOVA
-   **Adjusted P-Value:** P-value following Benjamini-Hochberg correction
-   **Max Fold Change Value:** Maximum value for all fold change dose levels.
-   **Max Fold Change Value Unsigned:** Absolute value of the Maximum value for all fold change dose levels.
-   **Fold Change Dose Level 1, 2, etc…:** Fold change for each dose level from control.
-   **Profile:** Pattern of response observed in the data over the different dose groups. "U" indicates up-regulation, and "D" indicates down-regulation. Each dose is compared to the previous dose. For example if you run a 4 dose study including "0" dose the profile of UUD indicates that the probe trend increased for the first 2 positive dose levels and decreased at the 3rd (ORIOGEN only)

![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/main-anova-complete.png?raw=true)

At the top of the main panel, there is a set of [toggles](Overview-of-the-Main-View#toggles-panel) that control various aspects of the ANOVA analysis results view.

### Prefilter Visualizations

The default visualizations are:

-   Max Fold Change Vs. *-log<sub>10</sub> Adjusted *P*-value*

    ![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/anova-charts/mfc--log10-adjusted-p-value.png?raw=true)
-   Max Fold Change Vs. *-log<sub>10</sub> Unadjusted *P*-value*

    ![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/anova-charts/mfc--log10-unadjusted-p-value.png?raw=true)

There are more visualizations available after clicking on `Select Graph View` dropdown list:

-   **Unadjusted P-Value Histogram**

    ![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/anova-charts/unadjusted-p-value-histogram.png?raw=true)
-   **Adjusted P-Value Histogram**

    ![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/anova-charts/adjusted-p-value-histogram.png?raw=true)
-   **Best Fold Change Histogram**

    ![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/anova-charts/max-fold-change-histogram.png?raw=true)
-   **Best Fold Change (Unsigned) Histogram**

    ![](https://github.com/auerbachs/BMDExpress-2.0/blob/master/media/anova-charts/max-fold-change-unsigned-histogram.png?raw=true)

### Statistical Filter Visualization Filters

These parameters are changed via the [filter panel](Overview-of-the-Main-View#filters-panel). You must also make sure that the `Apply Filter` box is checked in the [toggles panel](Overview-of-the-Main-View#toggles-panel) for these filters to be applied. The filters will be applied as soon as they are entered; there is no need to click any *apply* button other than the checkbox.

-   **Adjusted P-Value**
-   **Df1** (ANOVA only)
-   **Df2** (ANOVA only)
-   **F-Value** (ANOVA only)
-   **FC Dose Level _n_**
-   **Gene ID**
-   **Gene Symbol**
-   **Max Fold Change**
-   **Max Fold Change Unsigned**
-   **Probe ID**
-   **Unadjusted P-Value**
-   **Profile** (ORIOGEN only)