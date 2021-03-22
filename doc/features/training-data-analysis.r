setwd("~/gits/paper/2021-nlp4re/evaluation/")

trainingData <- read.csv("CONDEC-NLP4RE2021.csv")
summary(trainingData)
trainingData[880,]

numIssues <- table(trainingData$isIssue)[2] # 392
numDecisions <- table(trainingData$isDecision)[2] # 332
numAlternatives <- table(trainingData$isAlternative)[2] # 218
numPros <- table(trainingData$isPro)[2] # 288
numCons <- table(trainingData$isCon)[2] # 238

numRelevant <- numIssues + numDecisions + numAlternatives + numPros + numCons # 1468
numIrrelevant <- nrow(trainingData) - numRelevant # 220
