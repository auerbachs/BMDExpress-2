Basic Workflow
--------------
[Quickstart Video](https://www.youtube.com/watch?v=yWWG0bojLdc&index=1&list=PLX2Rd5DjtiTeR84Z4wRSUmKYMoAbilZEc)

![](https://github.com/auerbachs/BMDExpress-2/blob/master/media/workflow.png?raw=true)

[Before working with data, verify that necessary gene annotations are present, and up to date.](How-to-Use-the-Application#update-annotation-file) Annotations for the various genomic platforms are stored and maintained on the BMD Express 2 GitHub site. Before importing data, annotations must be present locally.

[Gene expression data is first imported into BMDExpress.](How-to-Use-the-Application#import-dose-response-data) The data must be correctly formatted. Excel files are not currently supported, but tab-delimited `.txt` files are supported. Data sets can be prefiltered outside of BMDExpress (i.e., features can be removed through statistical filtering), and a subset of the data can be loaded and modeled. Alternatively the data set can be loaded in its entirety and filtered within BMDExpress (see below).

[Gene expression dose-response data is then (optionally) processed](Statistical-and-Fold-Change-Filtering) using one of several choices of statistical model, and a fold change filter to identify probes/probe sets that demonstrate dose-response behavior in accordance with user-specified thresholds. Filtering the probe sets for such a threshold in dose-response behavior is not required, but will reduce noise in the data and the computation time required in the subsequent steps in the analysis.

[Dose response data is then fit to one or more parameterized models](Benchmark-Dose-Analysis):

-   Power
-   Linear
-   Polynomial 2-4
-   Hill
-   Exponential 2-5

The model that best describes the data without having too much complexity is selected for subsequent procedures. [The user can apply two approaches for model selection](Benchmark-Dose-Analysis#benchmark-dose-data-options) including 1) a nested likelihood ratio test for the linear and polynomial models followed by an Akaike information criterion (AIC) that compares the best nested model to the exponential model, Hill model and the power model; or 2) a completely AIC-based selection process to compare all models. 

Once the best model is identified, [probe/probeset identifiers are mapped onto unique genes](Functional-Classifications) based on [NCBI Entrez Gene identifiers](https://www.ncbi.nlm.nih.gov/gene). Entrez Gene IDs are subsequently matched to corresponding [Gene Ontology](http://www.geneontology.org/), Signaling Pathway (e.g., [Reactome](http://www.reactome.org/)), or user defined categories. Summary values representing the central tendencies and associated variability of the BMD, benchmark dose lower confidence limits (BMDL) and benchmark dose upper confidence limits (BMDU) for all the genes in each category are then computed.

Batchwise processing of multiple data sets is available at every step of the workflow. This is accomplished by standard multi-select keyboard/mouse click combinations, depending on the host operating system.

[Results can then be exported](Overview-of-the-Main-View#exporting-analyses) for further analysis in other software packages.

Project files from BMDExpress 2 are saved in `.bm2` format or alternatively in `.json` format (much larger than `.bm2`). However, `.bmd` project files from the original BMDExpress can be imported in to the software and transformed into the `.bm2` format. **Note:** `.bmd` files loaded into BMDExpress retain all annotations and results contained in the original file, hence, it will appear that there is missing data in the results files and probe annotations may not match updated annotations that would be applied if the expression data was re-analyzed in BMDExpress 2.

BMDExpress 2 can analyze any continuous dose-response data. An example video on how to perform analysis on nongenomic data in BMDExpress 2 can be found [here](https://youtu.be/AhZHLbkLAuA).