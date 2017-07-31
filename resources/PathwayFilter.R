#' @func maximum.response.significance.test
#' @descr computes significance p-values for pathway-level maximum dose response (relative to control) using Dunnet-like statisitc and boostrap approach as proposed by Dr. Shyamal Pedada.
#' @param data.file  name of file containing expression matrix and dose level information (see template file for layout detail)
#' @param pathways.file  name of file containing pathway definitions (see template file for layout detail)
#' @param num.bootrap.samples an integer denoting number of bootstrap samples to use while computing significance p-value (default=1000)
#' @param seed an integer seed value to mainitain reproducibility (default=12330)
#' @param verbose a logical denoting the whether to report execution progress 
#' @param mc.cores an integer denoting how many parallel cores to be utilized (ignored if parallel package is not available).
#' @param alpha type-I error rate used for NES confidence interval computation (default=0.05 produces 95% confidence interval)
#' @param ... additional parameters that are not used currently
#' @value returns data.frame with Enrichment Score (ES), Normalized ES (NES), P-value, False Discovery Rate (FDR) and Family-wise error rate for pathway.

maximum.response.significance.test <- function(data.file, pathways.file, num.bootstrap.samples=100, seed=12330, verbose=TRUE, mc.cores=NULL, alpha=0.05, ...) {
	
	data <- read.delim(file=data.file, sep="\t", header=TRUE, stringsAsFactors=FALSE, check.names=FALSE);
	x <- as.matrix(data[-1,-1]); storage.mode(x) <- "double";
	y <- drop(t(as.matrix(data[1,-1])));
	dimnames(x) <- list(data[-1,1],names(data)[-1]);
	names(y) <- names(data)[-1];
	remove(data);

	pathways <- read.delim(file=pathways.file, sep="\t", header=FALSE, stringsAsFactors=FALSE,check.names=FALSE);
	pathways.names <- pathways[,1];
	pathways <- lapply(strsplit(pathways[,2],split=","), as.integer);
	names(pathways) <- pathways.names;
	remove(pathways.names);

	x <- t(x);
	parallel.packageName <-ifelse(Sys.info()[["sysname"]] == "Windows", "parallelsugar", "parallel");
       	
	## check whether "parallel" (for non-windows) or "parallellsugar" (for windows) is available;
	check  <- parallel.packageName %in% rownames(installed.packages());
	if(check & is.null(mc.cores)) mc.cores <- ceiling(0.5*parallel::detectCores());

	if(verbose) cat("preprocessing to compute design matrices\n")
	X <- model.matrix(~0+I(factor(y)), model.frame(~0+I(factor(y)),  data=data.frame(y=y)));
	
	d <- ncol(X); 
	p <- ncol(x); 
	n <- nrow(x);
	nvec <- colSums(X);
	
	retain.doses <- which(nvec>1);
	if(length(retain.doses)!=d) {
		warning("some doses with only one replicate are excluded from pathway filtering method")
	 	X <- X[,retain.doses,drop=FALSE];
		retain.samples <- which(rowSums(X)>0);

		d <- length(retain.doses);
		x <- x[retain.samples,,drop=FALSE];
		X <- X[retain.samples,,drop=FALSE];
		nvec <- colSums(X);
		n <- nrow(x);
	}	

	
	if(d<2) stop("Fewer than two doses with at least replicate found");

	num.pathways <- length(pathways);

	G.matrix <- chol2inv(chol(t(X)%*%X))%*%t(X);
	Hat.matrix  <- diag(1,n)-X%*%G.matrix;

	L1.matrix <- L2.matrix <- matrix(0,d-1,d);
	L1.matrix[,-1] <- diag(1, d-1);	L1.matrix[,1] <- -1; 
	L2.matrix[,-1] <- diag(1/nvec[-1]); L2.matrix[,1] <- 1/nvec[1];

	L3.matrix <- diag(1/(nvec-1));
	
	L3.matrix <- L3.matrix%*%t(X);

	L2.matrix <- L2.matrix%*%L3.matrix; 
	L1.matrix <- L1.matrix%*%G.matrix;  ## Contrast Matrix (L*Beta);
	
	xbar <- matrix(1,nrow=n,ncol=1,dimnames=list(rownames(x),NULL))%*%matrix(colMeans(x),nrow=1,ncol=p, dimnames=list(NULL,colnames(x)));

	### Calculate Studentized Residuals;
	obs.residuals <- Hat.matrix%*%x;
	sigma <- X%*%sqrt(L3.matrix%*%(obs.residuals^2));
	eta <- obs.residuals/sigma;
	
	### A function to calculate Test-statistic for each pathway
	compute.pathway.stat <- function(eX) {
		denominator <- L2.matrix%*%((Hat.matrix%*%eX)^2);
		denominator[denominator < .Machine$double.eps] <- .Machine$double.eps;
		numerator <- (L1.matrix%*%eX)^2;
		Z.matrix <- numerator/denominator;
		Tvalue <- sapply(pathways, function(w) max(rowMeans(Z.matrix[,w,drop=FALSE], na.rm=TRUE)));
		Tvalue
	}
	
	if(verbose) cat("computing observed pathway signficance stat\n");

	Tobs <- compute.pathway.stat(x); 	
	set.seed(seed);
	
	if(verbose) cat("computing pathway signficance stat for bootstrap samples\n");
	### bootstrap sampling with replacement;
	bootstrap.samples <- matrix(ceiling(n*runif(n*num.bootstrap.samples)), nrow=num.bootstrap.samples, ncol=n); 
	compute.permuted.pathway.stat <- function(k) {
			print(k);
			index <- bootstrap.samples[k,];
			xstar <- xbar + sigma*eta[index,,drop=FALSE];
			return(compute.pathway.stat(xstar));
	}

	if(check) {
		library(parallel.packageName,quietly=TRUE,character.only=TRUE);
 		Tstar <- mclapply(1:num.bootstrap.samples, FUN=compute.permuted.pathway.stat, mc.cores=mc.cores, mc.preschedule=TRUE)
	} else {
		Tstar <- lapply(1:num.bootstrap.samples, FUN=compute.permuted.pathway.stat);
	}

	Tstar <- do.call(cbind,Tstar);
	
	pvalue <- rowMeans(Tstar > matrix(Tobs, nrow=num.pathways, ncol=1)%*%matrix(1, nrow=1, ncol=num.bootstrap.samples), na.rm=TRUE);
	TCI <- apply(Tstar, MARGIN=1, quantile, probs=c(alpha/2, 0.5, 1-alpha/2), na.rm=TRUE)

	if(verbose) cat("computing FDR and FWER\n");
        
	### Perform multiple test correction using False Discovery Rate and Family-wise WER
	fdr <- p.adjust(pvalue, method="BH");
	fwer <- pmin(pvalue*num.pathways,1);

	result <- data.frame(Pathway=if(is.null(names(pathways))) 1:num.pathways else names(pathways), "Pathway Size"=sapply(pathways, length), 
				ES=Tobs, NES=Tobs/TCI[2,], "P-value"=pvalue, "Adj. P-value"=fdr,FWER=fwer, "NES LCL"=Tobs/TCI[3,], "NES UCL"=Tobs/TCI[1,],
				stringsAsFactors=FALSE, check.names=FALSE);

       	return(result);

}



args <- commandArgs(TRUE);
result <- maximum.response.significance.test(args[1], args[2], as.numeric(args[3]), 12330, FALSE, as.numeric(args[4]), as.numeric(args[5]));

write.table(result,file=args[6], sep="\t", quote=FALSE);
