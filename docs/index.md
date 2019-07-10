Introduction
============

[Download BMDExpress 2 Software](https://github.com/auerbachs/BMDExpress-2/releases)

BMDExpress 2 is a desktop application for Windows, Mac or Linux ([See IMPORTANT warning](benchmark-dose-analysis.md#important)) that enables analysis of dose-response data produced in differential gene expression experiments. It provides stepwise workflows that combine benchmark dose (BMD) calculations with functional classification analysis based on Gene Ontology ([GO](http://www.geneontology.org/)), Signaling Pathways ([Reactome](http://reactome.org/)), or custom categories provided by the user. The end results are estimates of doses at which cellular processes are altered, based on an increase or decrease in response in expression levels compared to untreated controls. All of the dose-response curve fit models utilized by BMDExpress 2 are those contained within [USEPA BMDS software](https://www.epa.gov/bmds). For a detailed description of the models, the user is referred to the [BMDS User Manual](https://www.epa.gov/bmds/benchmark-dose-software-bmds-user-manual).

[Example BMDExpress 2 expression data files](https://github.com/auerbachs/BMDExpress-2/blob/master/example%20data%20files.zip)

**Note:** Compressed zip file that contains 3 expression data files. User will need to unzip before using.

[Example BMDExpress 2 project file (.bm2)](https://github.com/auerbachs/BMDExpress-2/blob/master/Example%20Data%20version%202.2.zip)

**Note:** Compressed zip file that contains the .bm2 file used in the [tutorial videos](https://www.youtube.com/playlist?list=PLX2Rd5DjtiTeR84Z4wRSUmKYMoAbilZEc). User will need to unzip the file before loading into BMDExpress 2.

Basic Workflow
--------------

[Quickstart Video](https://www.youtube.com/watch?v=yWWG0bojLdc&index=1&list=PLX2Rd5DjtiTeR84Z4wRSUmKYMoAbilZEc)

![BMDExpress-2 workflow image](https://raw.githubusercontent.com/auerbachs/BMDExpress-2/master/media/workflow.png)

[Before working with data, verify that necessary gene annotations are present, and up to date.](how-to-use-the-application.md#update-annotation-file) Annotations for the various genomic platforms are stored and maintained on the BMD Express 2 GitHub site. Before importing data, annotations must be present locally.

[Gene expression data is first imported into BMDExpress.](how-to-use-the-application.md#import-dose-response-data) The data must be correctly formatted. Excel files are not currently supported, but tab-delimited `.txt` files are supported. Data sets can be prefiltered outside of BMDExpress (i.e., features can be removed through statistical filtering), and a subset of the data can be loaded and modeled. Alternatively the data set can be loaded in its entirety and filtered within BMDExpress (see below).

[Gene expression dose-response data is then (optionally) processed](statistical-and-fold-change-prefiltering.md) using one of several choices of statistical model, together with a fold change filter to identify probes/probe sets that demonstrate dose-response behavior in accordance with user-specified thresholds. Filtering the probe sets for such a threshold in dose-response behavior is not required, but will reduce noise in the data and the computation time required in the subsequent steps in the analysis.

[Dose response data is then fit to one of the following models](benchmark-dose-analysis.md):

- parameteric
  - Power
  - Linear
  - Polynomial 2-4
  - Hill
  - Exponential 2-5
- non-parametric
  - gCurveP

In the case of parametric modelling, the model that best describes the data without having too much complexity is selected for subsequent procedures. [The user can apply two approaches for paremetric model selection](benchmark-dose-analysis.md#benchmark-dose-data-options-(parametric)) including 1) a nested likelihood ratio test for the linear and polynomial models followed by an Akaike information criterion (AIC) that compares the best nested model to the exponential model, Hill model and the power model; or 2) a completely AIC-based selection process to compare all models.

Non-parametric gCurveP produces a single output, [based on the choice of input parameters](benchmark-dose-analysis.md#benchmark-dose-data-options-(non-parametric)).

After modelling is complete (best model in the case of parametric), [probe/probeset identifiers are mapped onto unique genes](functional-classifications.md) based on [NCBI Entrez Gene identifiers](https://www.ncbi.nlm.nih.gov/gene). Entrez Gene IDs are subsequently matched to corresponding [Gene Ontology](http://www.geneontology.org/), Signaling Pathway (e.g., [Reactome](http://www.reactome.org/)), or user defined categories. Summary values representing the central tendencies and associated variability of the BMD, benchmark dose lower confidence limits (BMDL) and benchmark dose upper confidence limits (BMDU) for all the genes in each category are then computed.

Batchwise processing of multiple data sets is available at every step of the workflow. This is accomplished by standard multi-select keyboard/mouse click combinations, depending on the host operating system.

[Results can then be exported](overview-of-the-main-view.md#exporting-analyses) for further analysis in other software packages.

Project files from BMDExpress 2 are saved in `.bm2` format or alternatively in `.json` format (much larger than `.bm2`). However, `.bmd` project files from the original BMDExpress can be imported in to the software and transformed into the `.bm2` format. **Note:** `.bmd` files loaded into BMDExpress retain all annotations and results contained in the original file, hence, it will appear that there is missing data in the results files and probe annotations may not match updated annotations that would be applied if the expression data was re-analyzed in BMDExpress 2.

BMDExpress 2 can analyze any continuous dose-response data. An example video on how to perform analysis on nongenomic data in BMDExpress 2 can be found [here](https://youtu.be/AhZHLbkLAuA).

Tutorial Videos
---------------

A [playlist of video tutorials](https://www.youtube.com/playlist?list=PLX2Rd5DjtiTeR84Z4wRSUmKYMoAbilZEc) created by Scott Auerbach is available. Videos will also be linked in each section for their relevant functions.