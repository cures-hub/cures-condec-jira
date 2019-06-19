package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.util.Vector;

// TODO Remove this file. Instead introduce JsonIgnore annotations in the ChangedFileImpl class: https://fasterxml.github.io/jackson-annotations/javadoc/2.5/com/fasterxml/jackson/annotation/JsonIgnore.html
public class SimplifiedChangedFile {
	public String className;
	public Vector<String> changedMethods;
	public double probability;
	public boolean isCorrect;

	public SimplifiedChangedFile(String className, Vector<String> changedMethods, double probability) {
		this.className = className;
		this.changedMethods = changedMethods;
		this.probability = probability;
		this.isCorrect = true;
	}
}
