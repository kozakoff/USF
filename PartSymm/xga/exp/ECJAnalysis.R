# Auth: Stephen Kozakoff
# Desc: Utilities for aggregating and evaluating ECJ Short Statistics output files.

doAnalysis <- function(files) 
{
  s <- strsplit(names(files)[1],".",fixed=TRUE)
  cols <- c("Gen",paste("Job",s[[1]][2]))
  
  t <- files[[1]]
  ecjdata <- t[,1:2]
  names(ecjdata) <- cols
  
  for(file in 2:length(files)) 
  {
    t <- files[[file]]
    ecjdata <- merge(ecjdata,t[,1:2],by=1)
    s <- strsplit(names(files)[file],".",fixed=TRUE)
    cols <- c(cols,paste("Job",s[[1]][2]))
    names(ecjdata) <- cols
  } 

  #Add row means
  ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=rowMeans(ecjdata[,-1])),by=1)

  return(ecjdata)
}

readFiles <- function(path) 
{
  pattern = paste("*.out.stat",sep="")
 
  files <- list.files(path=path, pattern=pattern, full.names=T, recursive=FALSE)
  
  cat(paste("Found ",length(files)," files that match ",path,"/",pattern,". Begin processing...",sep=""))
  
  invisible(ecjShortStatFiles <- lapply(X=files, FUN=read.delim, sep=" ", header=FALSE))
  
  names(ecjShortStatFiles) <- gsub("^.*/","",files) 
  
  cat("Done!\n")
  
  return(ecjShortStatFiles)
}

doPlots <- function(hc,fga,dga,sga,hcrr,title="") 
{
  plot(hc[,"Gen"],hc[,"rowMeans"],main=title,xlab="Generation",ylab="Average Fitness",yaxt="n",pch=".",col="blue")
  at <- pretty(hc[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=3))
  lines(hc[,"Gen"],hc[,"rowMeans"],col="blue")
  lines(hcrr[,"Gen"],hcrr[,"rowMeans"],col="cyan")
  lines(dga[,"Gen"],dga[,"rowMeans"],col="green")
  lines(fga[,"Gen"],fga[,"rowMeans"],col="red")
  lines(sga[,"Gen"],sga[,"rowMeans"],col="black")
  legend("bottomright",inset=0.02,legend=c("DGA","FGA","HC","HCRR","SGA"),col=c("green","red","blue","cyan","black"), lty=1:2, cex=0.8,box.lty=0)
}

plotOne <- function(ecjstat,title="") 
{
  plot(ecjstat[,"Gen"],ecjstat[,"rowMeans"],main=title,xlab="Generation",ylab="Average Fitness",yaxt="n",pch=".",col="blue")
  at <- pretty(ecjstat[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=3))
  lines(ecjstat[,"Gen"],ecjstat[,"rowMeans"],col="blue")
}

plotTwo <- function(v1,v1Title="",v2,v2Title="",mainTitle="") 
{
  plot(v2[,"Gen"],v2[,"rowMeans"],main=mainTitle,xlab="Generation",ylab="Average Fitness",yaxt="n",pch=".",col="blue")
  at <- pretty(v2[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=3))
  lines(v1[,"Gen"],v1[,"rowMeans"],col="red")
  lines(v2[,"Gen"],v2[,"rowMeans"],col="blue")
  legend("bottomright",inset=0.02,legend=c(v1Title,v2Title),col=c("blue","red"), lty=1:2, cex=0.8,box.lty=0)
}

rerunStats <- function()
{
  hcrrFiles <- readFiles("C:/Users/steve/git/USF/PartSymm/xga/exp/hcrr")
  hcFiles <- readFiles("C:/Users/steve/git/USF/PartSymm/xga/exp/hc")
  sgaFiles <- readFiles("C:/Users/steve/git/USF/PartSymm/xga/exp/sga")
  dgaFiles <- readFiles("C:/Users/steve/git/USF/PartSymm/xga/exp/dga")
  fgaFiles <- readFiles("C:/Users/steve/git/USF/PartSymm/xga/exp/fga")
  
  hcrr <- doAnalysis(hcrrFiles)
  hc <- doAnalysis(hcFiles)
  sga <- doAnalysis(sgaFiles)
  dga <- doAnalysis(dgaFiles)
  fga <- doAnalysis(fgaFiles)
  
  doPlots(hc=hc,fga=fga,dga=dga,sga=sga,hcrr=hcrr,title="MaxOnes Fitness Calculation")
}

