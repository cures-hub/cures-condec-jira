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

# get parts of text per type
rowsWithIrrelevantText <-
  which(
    trainingData$isIssue == 0 &
      trainingData$isDecision == 0 &
      trainingData$isCon == 0 &
      trainingData$isPro == 0 & trainingData$isAlternative == 0
  )
trainingData[rowsWithIrrelevantText,]

rowsWithIssues <- which(trainingData$isIssue == 1)
trainingData[rowsWithIssues,]

rowsWithDecisions <- which(trainingData$isDecision == 1)
trainingData[rowsWithDecisions,]

rowsWithAlternatives <- which(trainingData$isAlternative == 1)
trainingData[rowsWithAlternatives,]

rowsWithCons <- which(trainingData$isCon == 1)
trainingData[rowsWithCons,]

rowsWithPros <- which(trainingData$isPro == 1)
trainingData[rowsWithPros,]