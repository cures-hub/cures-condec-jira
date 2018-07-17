//package de.uhd.ifi.se.decision.management.jira.decXtract.classification;
//
//import weka.classifiers.evaluation.Evaluation;
//
//public class Evaluater {
//
//	public static void printStats(Evaluation rate) {
//
//	    System.out.println(rate.toSummaryString());
//	    System.out.println("Correct % = " + String.format( "%.2f", rate.pctCorrect()));
//	    System.out.println("Incorrect % = " + String.format( "%.2f", rate.pctIncorrect())+"\n");
//	    double[][] cf = rate.confusionMatrix();
//	    System.out.println(cf[0][0] +"\t"+cf[0][1]+"\n"+cf[1][0]+"\t"+cf[1][1]+"\n");
//	    System.out.println("Recall 0: " +String.format( "%.2f", rate.recall(0)));
//	    System.out.println("Recall 1: " +String.format( "%.2f",  rate.recall(1)));
//
//	}
////
//}
