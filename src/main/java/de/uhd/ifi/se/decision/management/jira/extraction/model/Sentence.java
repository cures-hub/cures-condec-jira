package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
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

	private String argument="";
	

	public Sentence(String body, long aoId, long jiraCommentId) {
		super();
		this.setBody(body);
		
		super.type = KnowledgeType.OTHER;
		this.setValuesFromAoId(aoId);

		super.setDescription(this.body);
		super.setId(aoId);
		super.setKey(jiraCommentId + "-" + aoId);
		super.setSummary(body);
		super.setProject(
				new DecisionKnowledgeProjectImpl(ComponentGetter.getProjectService().getProjectKeyDescription()));


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

		KnowledgeType kt =  ActiveObjectsManager.getElementFromAO(aoId).getKnowledgeType();
		if(kt == null) {
			super.type = KnowledgeType.OTHER;
		}else if(this.isTaggedFineGrained) {
			super.type = kt;
		}
		
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
		result += "isTagged Manually:\t" + isTaggedManually + "\n";
		result += "isTagged Fine:\t" + isTaggedFineGrained + "\n";
		result += "isTagged Binary:\t" + isTaggedFineGrained + "\n";
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

	public KnowledgeType getKnowledgeType() throws NullPointerException{
		return super.type;
	}

	public void setKnowledgeType(KnowledgeType knowledgeType) {
		super.type = knowledgeType;
	}

	public String getArgument() {
		if(this.argument == null) {
			return "";
		}
		return argument;
	}

	public void setArgument(String linkType) {
		this.argument = linkType;
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

	public void setKnowledgeType(String string) {
		super.type = KnowledgeType.getKnowledgeType(string);
	}

	public String getKnowledgeTypeString() {
		if(super.type == null) {
			return "";
		}
		if(super.type.equals(KnowledgeType.ARGUMENT)) {
			return this.argument;
		}
		return super.type.toString();
	}

	public String getOpeningTagSpan() {
		if(super.type == null  || super.type == KnowledgeType.OTHER || !this.isRelevant) {
			return "<span class =tag ></span>" ;
		}
		String typeText = super.type.toString();
		if(super.type.equals(KnowledgeType.ARGUMENT)) {
			typeText = this.argument;
		}
		return "<span class =tag>["+typeText+"]</span>";
	}
	
	public String getClosingTagSpan() {
		if(super.type == null  || super.type == KnowledgeType.OTHER || !this.isRelevant) {
			return "<span class =tag ></span>" ;
		}
		String typeText = super.type.toString();
		if(super.type.equals(KnowledgeType.ARGUMENT)) {
			typeText = this.argument;
		}
		return "<span class =tag>[/"+typeText+"]</span>";
	}
}
