package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;

public class Sentence {

	private boolean isTagged;

	private boolean isRelevant;

	private List<Rationale> classification;

	private String body = "";

	private long activeObjectId;

	private int startSubstringCount;

	private int endSubstringCount;

	private boolean isTaggedManually;

	private boolean isTaggedFineGrained;

	public Sentence(String body, long aoId) {
		this.setBody(body);
		this.classification = new ArrayList<Rationale>();
		this.setActiveObjectId(aoId);
		this.isTagged(ActiveObjectsManager.checkCommentExistingInAO(aoId,true));
		this.setRelevant(ActiveObjectsManager.getElementFromAO(aoId).getIsRelevant());
		this.setTaggedFineGrained(ActiveObjectsManager.getElementFromAO(aoId).getIsTaggedFineGrained());
		this.setTaggedManually(ActiveObjectsManager.getElementFromAO(aoId).getIsTaggedManually());
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
		this.classification = list;
	}

	public void addClassification(Rationale element) {
		this.classification.add(element);
	}

	public String classificationToString() {
		String classI = "";
		for (Rationale classi : classification) {
			classI += Rationale.getString(classi);
		}
		return classI;
	}

	public long getActiveObjectId() {
		return activeObjectId;
	}

	public void setActiveObjectId(long activeObjectId) {
		this.activeObjectId = activeObjectId;
	}

	public String toString() {
		String result = "";
		result += "isRelevant:\t" + isRelevant + "\n";
		result += "body:\t" + body + "\n";
		result += "activeObjects Id:\t" + activeObjectId + "\n";
		result += "isTagged Manually:\t"+isTaggedManually +"\n";
		result += "isTagged Fine:\t"+isTaggedFineGrained +"\n";
		result += "isTagged Binary:\t"+isTaggedFineGrained +"\n";
		return result;
	}

	public int getStartSubstringCount() {
		return startSubstringCount;
	}

	public void setStartSubstringCount(int startSubstringCount) {
		this.startSubstringCount = startSubstringCount;
	}

	public int getEndSubstringCount() {
		return endSubstringCount;
	}

	public void setEndSubstringCount(int endSubstringCount) {
		this.endSubstringCount = endSubstringCount;
	}

	public boolean isTaggedManually() {
		return isTaggedManually;
	}

	public void setTaggedManually(boolean isTaggedManually) {
		this.isTaggedManually = isTaggedManually;
	}

	public boolean isTaggedFineGrained() {
		return isTaggedFineGrained;
	}

	public void setTaggedFineGrained(boolean isTaggedFineGrained) {
		this.isTaggedFineGrained = isTaggedFineGrained;
	}
}
