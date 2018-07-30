package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.ArrayList;
import java.util.List;

public class Sentence {

	private boolean isTagged;

	private boolean isRelevant;

	private List<Rationale> classification;

	private String body = "";

	private long activeObjectId;

	public Sentence(String body, long aoId) {
		this.setBody(body);
		this.classification = new ArrayList<Rationale>();
		this.setActiveObjectId(aoId);
	}

	public Sentence(String body, boolean isRelevant) {
		this.setBody(body);
		this.setRelevant(isRelevant);
	}


	public boolean isTagged() {
		return isTagged;
	}

	public void isTagged(boolean isTagged) {
		this.isTagged = isTagged;
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
		this.isTagged = true;
		if (double1 == 1.) {
			setRelevant(true);
		} else {
			setRelevant(false);
		}

	}

	public List<Rationale> getClassification() {
		return classification;
	}

	public void setClassification(List<Rationale> list) {
		this.isTagged = true;
		this.classification = list;
	}

	public void classificationToString() {
		String classI = "";
		for (Rationale classi : classification) {
			classI += Rationale.getString(classi);
		}
		System.out.println(classI);
	}

	public long getActiveObjectId() {
		return activeObjectId;
	}

	public void setActiveObjectId(long activeObjectId) {
		this.activeObjectId = activeObjectId;
	}

	public String toString() {
		String result ="";
		result+="isRelevant:\t"+isRelevant+"\n";
		result+="body:\t"+body+"\n";
		result+="activeObjects Id:\t"+activeObjectId+"\n";
		return result;
	}
}
