
doAnalysis <- function(files) {
  #cat(paste("Found ",length(files)," files that match ",path,"/",pattern,". Begin processing...",sep=""))
  
  s <- strsplit(files[1],".",fixed=TRUE)
  cols <- c("Gen",paste("Job",s[[1]][2]))
  
  t <- read.delim(files[1],sep = " ",header = FALSE)
  ecjdata <- t[,1:2]
  names(ecjdata) <- cols
  
  for(file in 2:length(files)) 
  {
    t <- read.delim(files[file],sep = " ",header = FALSE)
    ecjdata <- merge(ecjdata,t[,1:2],by=1)
    s <- strsplit(files[file],".",fixed=TRUE)
    cols <- c(cols,paste("Job",s[[1]][2]))
    names(ecjdata) <- cols
  } 
  
  cat("Done!\n")
  
  ecjdata <- merge(ecjdata,data.frame(Gen=ecjdata[,1], rowMeans=rowMeans(ecjdata[,-1])),by=1)

  return(ecjdata)
}

readFiles <- function(path,exp) 
{
  pattern = paste("job.*.",exp,".out.stat",sep="")
 
  files <- list.files(path=path, pattern=pattern, full.names=T, recursive=FALSE)
  
  ecjdata <- lapply(X=files, FUN=read.delim, sep=" ", header=FALSE)
  
  #cat(paste("Found ",length(files)," files that match ",path,"/",pattern,". Begin processing...",sep=""))
  
  cols <- c("Filename","Data")
  
  names(ecjdata) <- gsub("^/(.+/)*(.+)/", "", files) #\\.out.stat$
  
  #t <- read.delim(files[1],sep = " ",header = FALSE)
  
  #ecjdata <- as.vector(c(files[1],as.vector(t)))
  #names(ecjdata) <- cols
  
  #for(file in 2:length(files)) 
  #{
  #  t <- read.delim(files[file],sep = " ",header = FALSE)
  #  new <- as.vector(c(files[file],as.vector(t)))
  #  names(new) <- cols
  #  ecjdata <- rbind(ecjdata,new)
  #} 
  
  cat("Done!\n")
  
  return(ecjdata)
}

doPlots <- function(hc,fga,dga,sga) 
{
  plot(hc[,"Gen"],hc[,"rowMeans"],main="Metamask Comparison",xlab="Generation",ylab="Average Fitness",yaxt="n",pch=".",col="blue")
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
  at <- pretty(hc[,"rowMeans"])
  axis(side=2,las=2,at=at,labels=formatC(at, format="f", digits=2))
  lines(ecjstat[,"Gen"],ecjstat[,"rowMeans"],col="blue")
}

#cat("\n")
#dga <- doAnalysis("~/exp/dga","dga")
#fga <- doAnalysis("~/exp/fga","fga")
#sga <- doAnalysis("~/exp/sga","sga")
#hc <- doAnalysis("~/exp/hc","hc")
#cat("\n")

#doPlots(hc=hc,dga=dga,fga=fga,sga=sga)
