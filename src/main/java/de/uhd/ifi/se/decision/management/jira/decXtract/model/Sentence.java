package de.uhd.ifi.se.decision.management.jira.decXtract.model;

import java.util.ArrayList;

public class Sentence {

	private boolean isRelevant;

	private ArrayList<Rationale> classification;

	private String body = "";

	public Sentence(String body) {
		this.setBody(body);
		this.classification = new ArrayList<Rationale>();
	}

	public Sentence(String body, boolean isRelevant) {
		this.setBody(body);
		this.setRelevant(isRelevant);
	}

	public boolean isRelevant() {
		return isRelevant;
	}

	public void setRelevant(boolean isRelevant) {
		this.isRelevant = isRelevant;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setRelevant(Double double1) {
		if (double1 == 1.) {
			setRelevant(true);
		} else {
			setRelevant(false);
		}

	}

	public ArrayList<Rationale> getClassification() {
		return classification;
	}

	public void setClassification(ArrayList<Rationale> classification) {
		this.classification = classification;
	}
}
