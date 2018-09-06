package de.uhd.ifi.se.decision.management.jira.extraction.model;

import com.atlassian.jira.component.ComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class Sentence extends DecisionKnowledgeElementImpl {

	private boolean isTagged;

	private boolean isRelevant;

	private String body = "";

	private long activeObjectId;

	private int startSubstringCount;

	private int endSubstringCount;

	private boolean isTaggedManually;

	private boolean isTaggedFineGrained;

	private String argument = "";

	private boolean isPlanText;

	public Sentence() {
	}

	public Sentence(String body, long aoId, long jiraCommentId) {
		super();
		this.setBody(body);

		super.type = KnowledgeType.OTHER;
		this.setValuesFromAoId(aoId);

		super.setDescription(this.body);
		super.setId(aoId);
		super.setKey(jiraCommentId + "-" + aoId);
		super.setSummary(body);
		if (ComponentGetter.getProjectService() != null) {
			super.setProject(
					new DecisionKnowledgeProjectImpl(ComponentGetter.getProjectService().getProjectKeyDescription()));
		} else {
			super.setProject(new DecisionKnowledgeProjectImpl(""));
		}
	}

	public Sentence(long aoId) {
		super();
		super.type = KnowledgeType.OTHER;
		this.setValuesFromAoId(aoId);
		this.setSuperValues(aoId);
	}

	private void setSuperValues(long aoId) {
		com.atlassian.jira.issue.comments.Comment c = ComponentAccessor.getCommentManager()
				.getCommentById(ActiveObjectsManager.getElementFromAO(aoId).getCommentId());
		super.setDescription((String) c.getBody().subSequence(startSubstringCount, endSubstringCount));
		super.setSummary(super.getDescription());
		this.setBody(super.getDescription());
		super.setProject(
				new DecisionKnowledgeProjectImpl(ComponentGetter.getProjectService().getProjectKeyDescription()));
		super.setKey(c.getIssue().getId() + "-" + aoId);
		super.setId(aoId);
	}

	private void setValuesFromAoId(long aoId) {
		this.setActiveObjectId(aoId);
		this.isTagged(ActiveObjectsManager.checkCommentExistingInAO(aoId, true));
		this.setRelevant(ActiveObjectsManager.getElementFromAO(aoId).getIsRelevant());
		this.setTaggedFineGrained(ActiveObjectsManager.getElementFromAO(aoId).getIsTaggedFineGrained());
		this.setTaggedManually(ActiveObjectsManager.getElementFromAO(aoId).getIsTaggedManually());
		this.setStartSubstringCount(ActiveObjectsManager.getElementFromAO(aoId).getStartSubstringCount());
		this.setEndSubstringCount(ActiveObjectsManager.getElementFromAO(aoId).getEndSubstringCount());
		this.setArgument(ActiveObjectsManager.getElementFromAO(aoId).getArgument());

		String kt = ActiveObjectsManager.getElementFromAO(aoId).getKnowledgeType();
		if (kt == null || kt.equals("")) {
			super.type = KnowledgeType.OTHER;
		} else if (this.isTaggedFineGrained) {
			super.type = KnowledgeType.getKnowledgeType(kt);
		}

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

	public long getActiveObjectId() {
		return activeObjectId;
	}

	public void setActiveObjectId(long activeObjectId) {
		this.activeObjectId = activeObjectId;
	}

	public String toString() {
		return this.body;
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

	public KnowledgeType getKnowledgeType() throws NullPointerException {
		return super.type;
	}

	public void setKnowledgeType(KnowledgeType knowledgeType) {
		super.type = knowledgeType;
	}

	public String getArgument() {
		if (this.argument == null) {
			return "";
		}
		return argument;
	}

	public void setArgument(String linkType) {
		this.argument = linkType;
	}

	public boolean isPlanText() {
		return isPlanText;
	}

	public void setPlanText(boolean isPlanText) {
		this.isPlanText = isPlanText;
	}

	public void setKnowledgeType(double[] resultArray) {
		for (int i = 0; i < resultArray.length; i++) {
			if (resultArray[i] == 1. && i == 0) {
				super.type = KnowledgeType.ALTERNATIVE;
				break;
			}
			if (resultArray[i] == 1. && i == 1) {
				super.type = KnowledgeType.ARGUMENT;
				this.argument = "Pro";
				break;
			}
			if (resultArray[i] == 1. && i == 2) {
				super.type = KnowledgeType.ARGUMENT;
				this.argument = "Con";
				break;
			}
			if (resultArray[i] == 1. && i == 3) {
				super.type = KnowledgeType.DECISION;
				break;
			}
			if (resultArray[i] == 1. && i == 4) {
				super.type = KnowledgeType.ISSUE;
				break;
			}
		}
	}

	public void setKnowledgeType(String type) {
		if (type.toLowerCase().equals("pro")) {
			super.type = KnowledgeType.ARGUMENT;
			this.argument = "Pro";
		} else if (type.toLowerCase().equals("con")) {
			super.type = KnowledgeType.ARGUMENT;
			this.argument = "Con";
		} else {
			super.type = KnowledgeType.getKnowledgeType(type);
		}
	}

	public String getKnowledgeTypeString() {
		if (super.type == null) {
			return "";
		}
		if (super.type.equals(KnowledgeType.ARGUMENT)) {
			return this.argument;
		}
		return super.type.toString();
	}

	public String getOpeningTagSpan() {
		if (super.type == null || super.type == KnowledgeType.OTHER || !this.isRelevant) {
			return "<span class =tag></span>";
		}
		String typeText = super.type.toString();
		if (super.type.equals(KnowledgeType.ARGUMENT)) {
			typeText = this.argument;
		}
		return "<span class =tag>[" + typeText + "]</span>";
	}

	public String getClosingTagSpan() {
		if (super.type == null || super.type == KnowledgeType.OTHER || !this.isRelevant) {
			return "<span class =tag></span>";
		}
		String typeText = super.type.toString();
		if (super.type.equals(KnowledgeType.ARGUMENT)) {
			typeText = this.argument;
		}
		return "<span class =tag>[/" + typeText + "]</span>";
	}

}
