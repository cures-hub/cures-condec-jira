package de.uhd.ifi.se.decision.management.jira.extraction.impl;


import java.util.Vector;

public class SimplifiedChangedFile {
    public String className;
    public Vector <String> changedMethods;
    public double probability;
    public boolean isCorrect;

    public SimplifiedChangedFile(String className, Vector<String> changedMethods, double probability){
        this.className = className;
        this.changedMethods = changedMethods;
        this.probability = probability;
        this.isCorrect = true;
    }
}
