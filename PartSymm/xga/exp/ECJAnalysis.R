# Auth: Stephen Kozakoff
# Desc: Utilities for aggregating and evaluating ECJ Short Statistics output files.


doAnalysis <- function(files) {
  
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
  pattern = paste("job.*.*.out.stat",sep="")
 
  files <- list.files(path=path, pattern=pattern, full.names=T, recursive=FALSE)
  
  cat(paste("Found ",length(files)," files that match ",path,"/",pattern,". Begin processing...",sep=""))
  
  invisible(ecjShortStatFiles <- lapply(X=files, FUN=read.delim, sep=" ", header=FALSE))
  
  names(ecjShortStatFiles) <- gsub("^.*/","",files) 
  
  cat("Done!\n")
  
  return(ecjShortStatFiles)
}

doPlots <- function(hc,fga,dga,sga,title="") 
{
  plot(hc[,"Gen"],hc[,"rowMeans"],main=title,xlab="Generation",ylab="Average Fitness",yaxt="n",pch=".",col="blue")
  at <- pretty(hc[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=2))
  lines(hc[,"Gen"],hc[,"rowMeans"],col="blue")
  lines(dga[,"Gen"],dga[,"rowMeans"],col="green")
  lines(fga[,"Gen"],fga[,"rowMeans"],col="red")
  lines(sga[,"Gen"],sga[,"rowMeans"],col="black")
  legend("bottomright",inset=0.05,legend=c("DGA","FGA","HC","SGA"),col=c("green", "red","blue","black"), lty=1:2, cex=0.8,box.lty=0)
}

plotOne <- function(ecjstat,title="") 
{
  plot(ecjstat[,"Gen"],ecjstat[,"rowMeans"],main=title,xlab="Generation",ylab="Average Fitness",yaxt="n",pch=".",col="blue")
  at <- pretty(ecjstat[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=2))
  lines(ecjstat[,"Gen"],ecjstat[,"rowMeans"],col="blue")
}
