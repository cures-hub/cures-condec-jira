package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.beans.PropertyChangeListener;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.component.ComponentAccessor;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

public class Sentence extends DecisionKnowledgeElementImpl implements DecisionKnowledgeInCommentEntity{

	private boolean isTagged;

	private boolean isRelevant;

	private String body = "";

	private long activeObjectId;

	private int startSubstringCount;

	private int endSubstringCount;

	private boolean isTaggedManually;

	private boolean isTaggedFineGrained;

	private String argument = "";

	private boolean isPlainText;
	
	private String projectKey;
	
	private long commentId;

	public Sentence() {
		super();
		super.type = KnowledgeType.OTHER;
	}

	public Sentence(String body, long aoId, long jiraCommentId, String projectKey) {
		this();
		this.setValuesFromAoId(aoId);
		this.setBody(body);
		this.projectKey = projectKey;
		
		super.setDescription(this.body);
		super.setId(aoId);
		super.setKey(jiraCommentId + "-" + aoId);
		this.setCommentId(jiraCommentId);
		super.setSummary(body);
		if (ComponentGetter.getProjectService() != null) {
			super.setProject(
					new DecisionKnowledgeProjectImpl(ComponentGetter.getProjectService().getProjectKeyDescription()));
			this.projectKey = ComponentGetter.getProjectService().getProjectKeyDescription();
		} else {
			super.setProject(new DecisionKnowledgeProjectImpl(""));
		}

	}

	public Sentence(long aoId) {
		this();
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
		super.setKey(c.getIssue().getKey() + ": " + aoId);
		super.setId(aoId);
	}
 
	private void setValuesFromAoId(long aoId) {
		this.setActiveObjectId(aoId);
		this.setIsTagged(ActiveObjectsManager.checkCommentExistingInAO(aoId, true));
		DecisionKnowledgeInCommentEntity aoElement = ActiveObjectsManager.getElementFromAO(aoId);
		this.setIsRelevant(aoElement.isRelevant());
		this.setIsTaggedFineGrained(aoElement.isTaggedFineGrained());
		this.setIsTaggedManually(aoElement.isTaggedManually());
		this.setStartSubstringCount(aoElement.getStartSubstringCount());
		this.setEndSubstringCount(aoElement.getEndSubstringCount());
		this.setArgument(aoElement.getArgument());
		this.setProjectKey(aoElement.getProjectKey());

		String kt = aoElement.getKnowledgeTypeString();
		if (kt == null || kt.equals("")) {
			super.type = KnowledgeType.OTHER;
		} else {
			super.type = KnowledgeType.getKnowledgeType(kt);
		}

	}
	
	@Override
	public boolean isTagged() {
		return isTagged;
	}

	@Override
	public void setIsTagged(boolean isTagged) {
		this.isTagged = isTagged;
	}

	public boolean isRelevant() {
		return isRelevant;
	}

	@Override
	public void setIsRelevant(boolean isRelevant) {
		this.isRelevant = isRelevant;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
		if(StringUtils.indexOfAny(body, CommentSplitter.excludedTagList) >= 0 ) {
			this.isPlainText = false;
		}else {
			this.isPlainText = true;
		}
		if(this.body.contains("[issue]")) {
			this.setKnowledgeType(KnowledgeType.ISSUE);
			this.setIsRelevant(true);
			this.setIsTagged(true);
			this.setIsTaggedManually(true);
			this.setIsTaggedFineGrained(true);
			ActiveObjectsManager.updateSentenceElement(this);
		}
	}

	public void setRelevant(Double double1) {
		if (double1 == 1.) {
			setIsRelevant(true);
		} else {
			setIsRelevant(false);
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

	@Override
	public boolean isTaggedManually() {
		return isTaggedManually;
	}

	@Override
	public void setIsTaggedManually(boolean isTaggedManually) {
		this.isTaggedManually = isTaggedManually;
	}

	@Override
	public boolean isTaggedFineGrained() {
		return isTaggedFineGrained;
	}
	
	@Override
	public void setIsTaggedFineGrained(boolean isTaggedFineGrained) {
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
		return isPlainText;
	}

	public void setPlanText(boolean isPlanText) {
		this.isPlainText = isPlanText;
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
	
	@Override
	public void setKnowledgeTypeString(String type) {
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
	
	@Override
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
	
	/**
	 * Returns html class codes for non plain text sentences
	 * @return class identifier if this sentence is a code, quote or noformat
	 */
	public String getSpecialClass() {
		if(this.body.contains("{code:")) {
			return " preformattedContent panelContent";
		}
		return "";
	}

	public String getSpecialBodyWithHTMLCodes() {
		//quotes are replaced on js side
		if(this.body.contains("{quote}")) {
			return this.body;
		}		
		//code and noformats need to be escaped in a special way
		return "<div class=\"preformatted panel\" style=\"border-width: 1px;\"><div class=\"preformattedContent panelContent\">"
		+"<pre> "+ this.getBody().replace("\"","\\\"").replaceAll("&","&amp").replaceAll("<", "&lt").replaceAll(">", "&gt")+ "</pre></div></div>";
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EntityManager getEntityManager() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <X extends RawEntity<Long>> Class<X> getEntityType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void save() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public long getCommentId() {
		return this.commentId;
	}

	@Override
	public void setCommentId(long id) {
		this.commentId = id;
	}

	@Override
	public long getUserId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setUserId(long id) {
		// TODO Auto-generated method stub
		
	}

}
