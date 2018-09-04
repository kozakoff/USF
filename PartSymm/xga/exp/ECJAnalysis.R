# Auth: Stephen Kozakoff
# Desc: Utilities for aggregating and evaluating ECJ Short Statistics output files.

doAnalysis <- function(files) 
{
  s <- strsplit(names(files)[1],".",fixed=TRUE)
  cols <- c("Gen",paste("Job",s[[1]][2]))
  
  t <- files[[1]]
  ecjdata <- t[,1:2]
  names(ecjdata) <- cols
  
  if(length(files[]) > 1) 
  { 
  
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
  }
  else
  {
    #Copy the only column to be the rowMeans
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=ecjdata[,-1]),by=1)
  }

  return(ecjdata)
}

doHammingDistanceAnalysis <- function(files) 
{
  s <- strsplit(names(files)[1],".",fixed=TRUE)
  cols <- c("Gen",paste("Job",s[[1]][2]))
  
  t <- files[[1]]
  ecjdata <- t[,1:5]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  
  names(ecjdata) <- cols
  
  if(length(files[]) > 1) 
  { 
    for(file in 2:length(files)) 
    {
      t <- files[[file]]
      e <- t[,1:5]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      ecjdata <- merge(ecjdata,e[,1:2],by=1)
      s <- strsplit(names(files)[file],".",fixed=TRUE)
      cols <- c(cols,paste("Job",s[[1]][2]))
      names(ecjdata) <- cols
    } 
  
    #Add row means
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=rowMeans(ecjdata[,-1])),by=1)
  }
  else
  {
    #Copy the only column to be the rowMeans
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=ecjdata[,-1]),by=1)
  }
  
  return(ecjdata)
}

doLevenshteinDistanceAnalysis <- function(files) 
{
  s <- strsplit(names(files)[1],".",fixed=TRUE)
  cols <- c("Gen",paste("Job",s[[1]][2]))
  
  t <- files[[1]]
  ecjdata <- t[,1:6]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  
  names(ecjdata) <- cols
  
  if(length(files[]) > 1) 
  { 
    for(file in 2:length(files)) 
    {
      t <- files[[file]]
      e <- t[,1:6]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      ecjdata <- merge(ecjdata,e[,1:2],by=1)
      s <- strsplit(names(files)[file],".",fixed=TRUE)
      cols <- c(cols,paste("Job",s[[1]][2]))
      names(ecjdata) <- cols
    } 
  
    #Add row means
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=rowMeans(ecjdata[,-1])),by=1)
  }
  else
  {
    #Copy the only column to be the rowMeans
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=ecjdata[,-1]),by=1)
  }
  
  return(ecjdata)
}

doZeroCountAnalysis <- function(files) 
{
  s <- strsplit(names(files)[1],".",fixed=TRUE)
  cols <- c("Gen",paste("Job",s[[1]][2]))
  
  t <- files[[1]]
  ecjdata <- t[,1:7]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  
  names(ecjdata) <- cols
  
  if(length(files[]) > 1) 
  { 
    for(file in 2:length(files)) 
    {
      t <- files[[file]]
      e <- t[,1:7]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      ecjdata <- merge(ecjdata,e[,1:2],by=1)
      s <- strsplit(names(files)[file],".",fixed=TRUE)
      cols <- c(cols,paste("Job",s[[1]][2]))
      names(ecjdata) <- cols
    } 
    
    #Add row means
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=rowMeans(ecjdata[,-1])),by=1)
  }
  else
  {
    #Copy the only column to be the rowMeans
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=ecjdata[,-1]),by=1)
  }
  
  return(ecjdata)
}

doOneCountAnalysis <- function(files) 
{
  s <- strsplit(names(files)[1],".",fixed=TRUE)
  cols <- c("Gen",paste("Job",s[[1]][2]))
  
  t <- files[[1]]
  ecjdata <- t[,1:8]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  
  names(ecjdata) <- cols
  
  if(length(files[]) > 1) 
  { 
    for(file in 2:length(files)) 
    {
      t <- files[[file]]
      e <- t[,1:8]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      ecjdata <- merge(ecjdata,e[,1:2],by=1)
      s <- strsplit(names(files)[file],".",fixed=TRUE)
      cols <- c(cols,paste("Job",s[[1]][2]))
      names(ecjdata) <- cols
    } 
  
    #Add row means
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=rowMeans(ecjdata[,-1])),by=1)
  }
  else
  {
    #Copy the only column to be the rowMeans
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=ecjdata[,-1]),by=1) 
  }
  
  return(ecjdata)
}

doTwoCountAnalysis <- function(files) 
{
  s <- strsplit(names(files)[1],".",fixed=TRUE)
  cols <- c("Gen",paste("Job",s[[1]][2]))
  
  t <- files[[1]]
  ecjdata <- t[,1:9]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  ecjdata <- ecjdata[,-2]
  
  names(ecjdata) <- cols
  
  if(length(files[]) > 1) 
  { 
    for(file in 2:length(files)) 
    {
      t <- files[[file]]
      e <- t[,1:9]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      e <- e[,-2]
      ecjdata <- merge(ecjdata,e[,1:2],by=1)
      s <- strsplit(names(files)[file],".",fixed=TRUE)
      cols <- c(cols,paste("Job",s[[1]][2]))
      names(ecjdata) <- cols
    } 
  
    #Add row means
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=rowMeans(ecjdata[,-1])),by=1)
  }
  else
  {
    #Copy the only column to be the rowMeans
    ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=ecjdata[,-1]),by=1)
  }
  
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

doPlots <- function(fga,dga,sga,hcrr,title="") 
{
  plot(hcrr[,"Gen"],hcrr[,"rowMeans"],main=title,xlab="Generation",ylab="Average Fitness",yaxt="n",pch=".",col="blue", ylim = c(0.5, 1.0))
  grid(NA, NULL, col = "lightgray", lty = "dotted", lwd = par("lwd"), equilogs = TRUE)
  at <- pretty(hcrr[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=2))
  #lines(hc[,"Gen"],hc[,"rowMeans"],col="blue")
  lines(hcrr[,"Gen"],hcrr[,"rowMeans"],col="blue")
  lines(dga[,"Gen"],dga[,"rowMeans"],col="green")
  lines(fga[,"Gen"],fga[,"rowMeans"],col="red")
  lines(sga[,"Gen"],sga[,"rowMeans"],col="black")
  legend("topleft",inset=0.04,legend=c("DGA","FGA","HC","SGA"),col=c("green","red","blue","black"), lty=c(1,1,1,1), cex=0.5, box.lty=0, pt.cex=.3)
}

plotOne <- function(ecjstat,title="") 
{
  plot(ecjstat[,"Gen"],ecjstat[,"rowMeans"],main=title,xlab="Generation",ylab="Average Fitness",yaxt="n",pch=".",col="blue")
  at <- pretty(ecjstat[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=2))
  lines(ecjstat[,"Gen"],ecjstat[,"rowMeans"],col="blue")
}

plotTwo <- function(v1,v1Title="",v2,v2Title="",mainTitle="") 
{
  plot(v2[,"Gen"],v2[,"rowMeans"],main=mainTitle,xlab="Generation",ylab="Average",yaxt="n",pch=".",col="blue")
  at <- pretty(v2[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=2))
  lines(v1[,"Gen"],v1[,"rowMeans"],col="red")
  lines(v2[,"Gen"],v2[,"rowMeans"],col="blue")
  legend("bottomright",inset=0.02,legend=c(v1Title,v2Title),col=c("red","blue"), lty=1:2, cex=0.8,box.lty=0)
}

plotThree <- function(v1,v1Title="",v2,v2Title="",v3,v3Title="",mainTitle="") 
{
  plot(v1[,"Gen"],v1[,"rowMeans"],main=mainTitle,xlab="Generation",ylab="Average",yaxt="n",pch=".",col="blue")
  at <- pretty(v1[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=2))
  lines(v1[,"Gen"],v1[,"rowMeans"],col="blue",lty="solid")
  lines(v2[,"Gen"],v2[,"rowMeans"],col="red",lty="solid")
  lines(v3[,"Gen"],v3[,"rowMeans"],col="green",lty="solid")
  legend("bottomright",inset=0.02,legend=c(v1Title,v2Title,v3Title),col=c("blue","red","green"), lty=c(1), cex=0.8,box.lty=0)
}

plotCompareThree <- function(v1,v1Title="",v2,v2Title="",v3,v3Title="",v4,v4Title="",v5,v5Title="",v6,v6Title="",mainTitle="") 
{
  plot(v1[,"Gen"],v1[,"rowMeans"],main=mainTitle,xlab="Generation",ylab="Average",yaxt="n",pch=".",col="blue", ylim = c(0.0, 1.0))
  grid(NA, NULL, col = "lightgray", lty = "dotted", lwd = par("lwd"), equilogs = TRUE)
  at <- pretty(v1[,"rowMeans"]) #c(0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0) #
  axis(side=2,las=2, at=at, labels=formatC(at, format="f", digits=2))
  lines(v1[,"Gen"],v1[,"rowMeans"],col="blue",lty="dotted")
  lines(v2[,"Gen"],v2[,"rowMeans"],col="red",lty="dotted")
  lines(v3[,"Gen"],v3[,"rowMeans"],col="green",lty="dotted")
  lines(v4[,"Gen"],v4[,"rowMeans"],col="black",lty="solid")
  lines(v5[,"Gen"],v5[,"rowMeans"],col="magenta",lty="solid")
  lines(v6[,"Gen"],v6[,"rowMeans"],col="cyan",lty="solid")
  legend("bottomright",inset=0.02,legend=c(v1Title,v2Title,v3Title,v4Title,v5Title,v6Title),col=c("blue","red","green","black","magenta","cyan"), lty=c(3,3,3,1,1,1), cex=0.65,box.lty=1)
}

runStatsOneAlgo <- function(type1,title)
{
  t1 = ""
  if(type1 == "HCRR") { t1 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/hcrr") }
  if(type1 == "HC") { t1 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/hc") }
  if(type1 == "DGA") { t1 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/dga") }
  if(type1 == "FGA") { t1 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/fga") }

  e1 <- doAnalysis(t1)
  e2 <- doHammingDistanceAnalysis(t1)
  e3 <- doLevenshteinDistanceAnalysis(t1)
  plotThree(e1,"Fitness",e2,"Hamming",e3,"Levenshtein",title)
}

runStatsTwoAlgo <- function(type1,type2,title)
{
  t1 = ""
  if(type1 == "HCRR") { t1 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/hcrr") }
  if(type1 == "HC") { t1 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/hc") }
  if(type1 == "DGA") { t1 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/dga") }
  if(type1 == "FGA") { t1 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/fga") }
  
  t2 = ""
  if(type2 == "HCRR") { t2 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/hcrr") }
  if(type2 == "HC") { t2 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/hc") }
  if(type2 == "DGA") { t2 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/dga") }
  if(type2 == "FGA") { t2 <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/fga") }
  
  e1 <- doAnalysis(t1)
  e2 <- doLevenshteinDistanceAnalysis(t1)
  e3 <- doOneCountAnalysis(t1)
  e4 <- doAnalysis(t2)
  e5 <- doLevenshteinDistanceAnalysis(t2)
  e6 <- doOneCountAnalysis(t2)
  plotCompareThree(e1,paste(type1," Fitness"),e2,paste(type1," Levenshtein"),e3,paste(type1," Meta Gene Count (1s)"),e4,paste(type2," Fitness"),e5,paste(type2," Levenshtein"),e6,paste(type2," Meta Gene Count (1s)"),title)
}

runCompareAllFitness <- function()
{
  hcrrFiles <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/hcrr")
  #hcFiles <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/hc")
  sgaFiles <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/sga")
  dgaFiles <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/dga")
  fgaFiles <- readFiles("C:/Users/kozaksj/git/USF/PartSymm/xga/exp/fga")
  
  hcrr <- doAnalysis(hcrrFiles)
  #hc <- doAnalysis(hcFiles)
  sga <- doAnalysis(sgaFiles)
  dga <- doAnalysis(dgaFiles)
  fga <- doAnalysis(fgaFiles)
  
  #doPlots(hc=hc,fga=fga,dga=dga,sga=sga,hcrr=hcrr,title="RR Fitness Calculation w/Chunk Size=10 and Metamask Mutation Rate=0.0025")
  doPlots(fga=fga,dga=dga,sga=sga,hcrr=hcrr,title="Fitness Comparison - MaxOnes")
}

