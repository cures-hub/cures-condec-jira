# ConDec log file parsing for usage analytics

library(ggplot2)

excludedUsers <- "name1|name2"
includedUsers <- "name3|name4|name5|name6|name7"

getLogLinesForProject <- function(file) {
  # all lines in log file
  lines <- readLines(file)
  
  # lines with INFO statement (no ERROR, WARNING or DEBUG) 
  # and related to ISE2020 project
  linesINFO <- c()
  for (line in lines) {
    if(grepl(x=line, pattern = "(INFO)(?:.+)(CONDEC)") # CONDEC is the project key
       & (grepl(x=line, pattern=includedUsers)) 
       & !(grepl(x=line, pattern=excludedUsers))) {
      linesINFO <- c(linesINFO, line)
    }
  }
  
  return (linesINFO)
}

splitAtInfo <- function(lines) {
  dataFrame <- do.call(rbind, strsplit(lines, "INFO ", fixed=T))
  return (dataFrame)
}

splitAtUserName <- function(dataFrameSplitAtInfo) {
  dataFrame <- do.call(rbind, strsplit(dataFrameSplitAtInfo[,2], " ", fixed=T))
}

getRestUsageDataFrame <- function(lines) {
  dataFrame1 <- splitAtInfo(lines)
  
  nameCol <- splitAtUserName(dataFrame1)
  
  REST.col <- do.call(rbind, strsplit(dataFrame1[,2], "/rest/condec/latest/|/secure/", fixed=F))
  REST.col.name <- do.call(rbind, strsplit(REST.col[,2], " [d.u.i", fixed=T))
  
  restCallPerDate <- data.frame("Date" = as.Date(dataFrame1[,1], format = "%Y-%m-%d"), 
                                "User" = factor(nameCol[,1]),
                                "REST" = factor(REST.col.name[,1]))
  
  excludedRestCalls <- setdiff(restCallPerDate$REST, c("Dashboard.jspa", 
                                                       "release-note/getAllReleaseNotes.json", 
                                                       "view/elementsFromBranchesOfProject.json", 
                                                       "view/getMatrix.json",                                                         
                                                       "view/getTreant.json",                                                     
                                                       "view/getTreeViewer.json",                                                        
                                                       "view/getVis.json",
                                                       "view/decisionTable.json",
                                                       "view/getDecisionTable.json",
                                                       "view/getEvolutionData.json"))
  
  return (na.omit(droplevels(restCallPerDate, exclude=excludedRestCalls)))
}

lines <- c(getLogLinesForProject("example.log.file"))

lines[1]
lines[length(lines)-4000]

dataFrameSplitAtInfo <- splitAtInfo(lines)
summary(dataFrameSplitAtInfo)

dataFrameSplitAtUserName <- splitAtUserName(dataFrameSplitAtInfo)
summary(dataFrameSplitAtUserName)
table(dataFrameSplitAtUserName[,1])

restUsageDataFrame <- getRestUsageDataFrame(lines)

summary(restUsageDataFrame)

# plot the total number of calls on ConDec REST API for views
usageTableForViews <- table(restUsageDataFrame$REST)

pdf("view_usage_total_per_view.pdf", width = 16, height = 8)
  par(mar=c(4,18,4,2))
  barplot(usageTableForViews, main = "Total Number of REST API Calls for ConDec Views",
          xlab="Number of clicks on view", horiz =T, las=1)
dev.off()

# plot total view usage per day
df <- data.frame(Date=character(), NumClicks=numeric())
sumRest = 0
currentDate = 0
for(i in 1:length(restUsageDataFrame$Date)) {
    if (currentDate == 0) {
      currentDate = restUsageDataFrame$Date[i]
    }
    sumRest = sumRest + 1
    if (currentDate != restUsageDataFrame$Date[i]) {
      df <- rbind(df,data.frame(Date=currentDate, NumClicks=sumRest))
      currentDate = restUsageDataFrame$Date[i]
      sumRest=0
    }
}

summary(df)

pdf("view_usage_per_date.pdf", width = 16, height = 8)
  ggplot(df, aes(Date)) +
    #scale_y_log10() +
    geom_bar(stat="identity", aes(y=NumClicks)) +
    ylab("Number of REST API Calls for ConDec Views per Day") +
    scale_x_continuous(breaks = pretty(df$Date, n = 12))
dev.off()

# per developer
usageTableForViewsPerUser <- table(restUsageDataFrame$User)
summary(usageTableForViewsPerUser)
pdf("view_usage_per_developer.pdf", width = 16, height = 8)
par(mar=c(4,8,4,2))
barplot(usageTableForViewsPerUser, main = "Total Number of REST API Calls for ConDec Views",
        xlab="Number of clicks on view", horiz =T, las=1)
dev.off()

ftable(restUsageDataFrame[,2:3])

pdf("detailed_view_usage_per_developer.pdf", width = 16, height = 8)
par(mar=c(4,18,4,2))
barplot(table(restUsageDataFrame[,2:3]), main = "Total Number of REST API Calls for ConDec Views Per User",
        xlab="Number of clicks on view", horiz =T, las=1, beside=T, col=rainbow(5),
        legend.text=TRUE, args.legend = list(bty="n", x="bottomright"))
dev.off()

# anonymized
restUsageDataFrameAnonimyzed <- restUsageDataFrame
levels(restUsageDataFrameAnonimyzed$User) <- c("dev 0", "dev 1", "dev 2", "dev 3", "dev 4")
summary(restUsageDataFrameAnonimyzed)

pdf("detailed_view_usage_per_developer_anonymized.pdf", width = 16, height = 8)
par(mar=c(4,18,4,2))
barplot(table(restUsageDataFrameAnonimyzed[,2:3]), main = "Total Number of REST API Calls for ConDec Views Per User",
        xlab="Number of clicks on view", horiz =T, las=1,  beside=T,  col=rainbow(5),
        legend.text=TRUE, args.legend = list(bty="n", x="bottomright"))
dev.off()