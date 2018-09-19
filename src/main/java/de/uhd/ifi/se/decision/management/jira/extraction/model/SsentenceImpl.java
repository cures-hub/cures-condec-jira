package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.beans.PropertyChangeListener;
import java.io.EOFException;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager2;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DdecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

public class SsentenceImpl extends DecisionKnowledgeElementImpl implements Ssentence,DdecisionKnowledgeInCommentEntity {

	private boolean isTagged;

	private boolean isRelevant;

	private int startSubstringCount;

	private int endSubstringCount;

	private boolean isTaggedManually;

	private boolean isTaggedFineGrained;

	private String argument = "";

	private boolean isPlainText;

	private String projectKey;

	private long commentId;

	private long userId;

	private String knowledgeTypeString;

	public SsentenceImpl() {
		super();
		super.type = KnowledgeType.OTHER;
	}
	
	public SsentenceImpl(long id) {
		this();
		super.setId(id);
		retrieveAttributesFromActievObjects();
		retrieveBodyFromJiraComment();//TODO: Maybe remove this, needs to be tested
	}

	public SsentenceImpl(String body, long id) {
		this(id);
		this.setBody(body);
		retrieveAttributesFromActievObjects();
	}

	@Override
	public boolean isRelevant() {
		return this.isRelevant;
	}

	@Override
	public void setRelevant(boolean isRelevant) {
		this.isRelevant = isRelevant;
	}

	@Override
	public void setRelevant(Double prediction) {
		if (prediction == 1.) {
			this.setRelevant(true);
		} else {
			this.setRelevant(false);
		}
	}

	@Override
	public boolean isTagged() {
		return this.isTagged;
	}

	@Override
	public void setTagged(boolean isTagged) {
		this.isTagged = isTagged;
	}

	@Override
	public boolean isTaggedManually() {
		return this.isTaggedManually;
	}

	@Override
	public void setTaggedManually(boolean isTaggedManually) {
		this.isTaggedManually = isTaggedManually;
	}

	@Override
	public boolean isTaggedFineGrained() {
		return this.isTaggedFineGrained;
	}

	@Override
	public void setTaggedFineGrained(boolean isTaggedFineGrained) {
		this.isTaggedFineGrained = isTaggedFineGrained;

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
		return this.userId;
	}

	@Override
	public void setUserId(long id) {
		this.userId = id;
	}

	@Override
	public int getStartSubstringCount() {
		return this.startSubstringCount;
	}

	@Override
	public void setStartSubstringCount(int count) {
		this.startSubstringCount = count;

	}

	@Override
	public int getEndSubstringCount() {
		return this.endSubstringCount;
	}

	@Override
	public void setEndSubstringCount(int count) {
		this.endSubstringCount = count;
	}

	@Override
	public String getKnowledgeTypeString() {
		if (super.type == null) {
			return "";
		}
		if (super.type.equals(KnowledgeType.ARGUMENT)) {
			return this.argument;
		}
		return this.knowledgeTypeString;
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
		this.knowledgeTypeString = super.type.toString();
	}

	public void setKnowledgeType(double[] prediction) {
		for (int i = 0; i < prediction.length; i++) {
			if (prediction[i] == 1. && i == 0) {
				super.type = KnowledgeType.ALTERNATIVE;
				break;
			}
			if (prediction[i] == 1. && i == 1) {
				super.type = KnowledgeType.ARGUMENT;
				this.argument = "Pro";
				break;
			}
			if (prediction[i] == 1. && i == 2) {
				super.type = KnowledgeType.ARGUMENT;
				this.argument = "Con";
				break;
			}
			if (prediction[i] == 1. && i == 3) {
				super.type = KnowledgeType.DECISION;
				break;
			}
			if (prediction[i] == 1. && i == 4) {
				super.type = KnowledgeType.ISSUE;
				break;
			}
		}
		this.setKnowledgeTypeString(super.type.toString());
	}

	@Override
	public void setArgument(String argument) {
		this.argument = argument;
	}

	@Override
	public String getArgument() {
		if (this.argument == null) {
			return "";
		}
		return this.argument;
	}

	@Override
	public String getProjectKey() {
		return this.projectKey;
	}

	@Override
	public void setProjectKey(String key) {
		this.projectKey = key;
	}

	public String getBody() {
		return super.getSummary();
	}

	public void setBody(String body) {
		super.setDescription(body);
		super.setSummary(body);
		checkForPlainText(body);
	}

	private void checkForPlainText(String body) {
		if (StringUtils.indexOfAny(body, CommentSplitter.excludedTagList) >= 0) {
			this.isPlainText = false;
		} else {
			this.isPlainText = true;
		}
		if (super.getSummary().contains("[issue]")) {
			this.setKnowledgeTypeString(KnowledgeType.ISSUE.toString());
			this.setRelevant(true);
			this.setTagged(true);
			this.setTaggedManually(true);
			this.setTaggedFineGrained(true);
			// ActiveObjectsManager.updateSentenceElement(this); //TODO: Update this.
		}
	}

	public boolean isPlainText() {
		return isPlainText;
	}

	public void setPlainText(boolean isPlainText) {
		this.isPlainText = isPlainText;
	}
	
	private void retrieveBodyFromJiraComment() {
		String text = ComponentAccessor.getCommentManager().getCommentById(this.commentId).getBody();
		text = text.substring(this.startSubstringCount, this.endSubstringCount);
		this.setBody(text);
	}
	

	private void retrieveAttributesFromActievObjects() {
		DdecisionKnowledgeInCommentEntity aoElement = ActiveObjectsManager2.getElementFromAO(super.getId());
		this.setEndSubstringCount(aoElement.getEndSubstringCount());
		this.setStartSubstringCount(aoElement.getStartSubstringCount());
		this.setUserId(aoElement.getUserId());
		this.setTagged(aoElement.isTagged());
		this.setTaggedFineGrained(aoElement.isTaggedFineGrained());
		this.setTaggedManually(aoElement.isTaggedFineGrained());
		this.setProjectKey(aoElement.getProjectKey());
		this.setArgument(aoElement.getArgument());
		this.setCommentId(aoElement.getCommentId());
		super.setProject(
				new DecisionKnowledgeProjectImpl(ComponentGetter.getProjectService().getProjectKeyDescription()));
		String kt = aoElement.getKnowledgeTypeString();
		if (kt == null || kt.equals("")) {
			super.type = KnowledgeType.OTHER;
		} else {
			super.type = KnowledgeType.getKnowledgeType(kt);
		}
		
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


}
