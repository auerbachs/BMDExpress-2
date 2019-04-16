How To Use the Application
==========================

Update Annotations
------------------

[Video tutorial demonstrating how to update annotations](https://www.youtube.com/watch?v=z1nf2GX93uk&index=3&list=PLX2Rd5DjtiTeR84Z4wRSUmKYMoAbilZEc)

Before beginning an analysis, it is recommended to update any annotation files needed for the analysis. (Click `File > Update Annotations`)

![Select update annotations](https://raw.githubusercontent.com/auerbachs/BMDExpress-2/master/media/select-update-annotations.png)

Use the checkboxes on the left side of the popup to choose which annotation(s) to update. When finished, click `Update`.

![Popup update annotations](https://raw.githubusercontent.com/auerbachs/BMDExpress-2.0/master/media/popup-update-annotations.png)

Import Dose-Response Data
-------------------------

[Video tutorial demonstrating data import into BMDExpress 2](https://www.youtube.com/watch?v=TuF31IGblnQ&list=PLX2Rd5DjtiTeR84Z4wRSUmKYMoAbilZEc&index=4)

The first step in the workflow is to import gene expression data from a tab-delimited plain text file. We recommend using log-transformed data although this is not required. Each column in the data matrix must correspond to an individual expression experiment, and the first row must contain the doses at which the corresponding sample was treated. Subsequent rows contain the data for one probe/gene. An optional header row may also be included, in which case it must be the first row and the doses must be in the second row. You will be prompted when loading the data to indicate if the first row contains sample labels. Example data files are provided in the BMDExpress 2 installation folder.

**Note:** BMDExpress can be used to perform dose response modeling on other continuous data types (e.g., clinical chemistry). The data simply needs to be formatted in the same manner as the genomic data and loaded into the software and identified as a "generic" platform.

Click `File > Import Expression Data`.

![Select import data](https://raw.githubusercontent.com/auerbachs/BMDExpress-2.0/master/media/select-import-data.png)

Navigate to the folder containing your file(s), and select your data. You may import multiple files at once on this screen. Then click `Open`.

![Popup import data](https://raw.githubusercontent.com/auerbachs/BMDExpress-2.0/master/media/popup-import-data.png)

After the file is read by the program, an array platform will be suggested. If the default platform is incorrect, select the correct platform from the dropdown list or alternatively if you platform is not contained in our [annotation set](how-to-use-the-application.md#update-annotations) you can select "generic" from the drop down. Then click `OK`. If "generic" platform is selected probe annotations will be empty in the subsequent results tables and in order to perform [Functional Classifications](functional-classifications.md) a [Defined Category Analysis](functional-classifications.md#defined-category-analysis) will need to be carried out.

![Popup platform selection](https://raw.githubusercontent.com/auerbachs/BMDExpress-2.0/master/media/popup-platform-selection.png)

Next, select the type of log transformation your data was prepared with.

![Popup log transform](https://raw.githubusercontent.com/auerbachs/BMDExpress-2.0/master/media/popup-log-transform.png)

Once the file(s) are loaded into the program, they will be displayed in the lower section of the main window. In the chart area, scatter plots of 6 pairs of principal components are shown. To identify data points in the PCA plot hold shift and click on the point of interest.

Switch between expression data files using the data selection panel to the left. In this section only one data set at time can be loaded. In other sections of the application multiple data sets can selected and evaluated simultaneously.

![Main expression data loaded](https://raw.githubusercontent.com/auerbachs/BMDExpress-2.0/master/media/main-expression-data-loaded.png)

Once you have loaded your data you should proceed sequentially through [Prefiltering](statistical-and-fold-change-prefiltering.md), [Benchmark Dose Analysis](benchmark-dose-analysis.md), and [Functional Classification](functional-classifications.md).