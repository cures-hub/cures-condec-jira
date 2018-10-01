package de.uhd.ifi.se.decision.management.jira.extraction.model.impl;

import java.beans.PropertyChangeListener;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.util.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import net.java.ao.EntityManager;
import net.java.ao.RawEntity;

public class SentenceImpl extends DecisionKnowledgeElementImpl implements Sentence, DecisionKnowledgeInCommentEntity {

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

	private long issueId;
	
	private Date created;

	public SentenceImpl() {
		super();
		super.type = KnowledgeType.OTHER;
	}

	public SentenceImpl(long id) {
		this();
		super.setId(id);
		retrieveAttributesFromActievObjects();
		retrieveBodyFromJiraComment();// TODO: Maybe remove this, needs to be tested
	}

	public SentenceImpl(String body, long id) {
		super.setId(id);
		retrieveAttributesFromActievObjects();
		this.setBody(body);
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
		if (super.type == null ) {
			return "";
		}
		if(this.knowledgeTypeString == null) {
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
			if (prediction[0] == 1. ) {
				super.type = KnowledgeType.ALTERNATIVE;
			}
			if (prediction[1] == 1.) {
				super.type = KnowledgeType.ARGUMENT;
				this.argument = "Pro";
			}
			if (prediction[2] == 1.) {
				super.type = KnowledgeType.ARGUMENT;
				this.argument = "Con";
			}
			if (prediction[3] == 1. ) {
				super.type = KnowledgeType.DECISION;
			}
			if (prediction[4] == 1. ) {
				super.type = KnowledgeType.ISSUE;
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
		this.isPlainText = true;
		if (StringUtils.indexOfAny(body, CommentSplitter.excludedTagList) >= 0) {
			this.isPlainText = false;
		}
		if (StringUtils.indexOfAny(body, CommentSplitter.manualRationaleTagList) >= 0
				|| (ConfigPersistence.isIconParsingEnabled(projectKey)
						&& StringUtils.indexOfAny(body, CommentSplitter.manualRationalIconList) >= 0)) {
			this.setKnowledgeTypeString(CommentSplitter.getKnowledgeTypeFromManuallIssueTag(body, this.projectKey));
			setManuallyTagged();
			stripTagsFromBody(body);
		}
	}

	private void stripTagsFromBody(String body) {
		if (StringUtils.indexOfAny(body, CommentSplitter.manualRationaleTagList) >= 0) {
			int tagLength = 2 + CommentSplitter.getKnowledgeTypeFromManuallIssueTag(body, this.projectKey).length();
			super.setDescription(body.substring(tagLength, body.length() - (1 + tagLength)));
			super.setSummary(super.getDescription());
		} else { // Icon case TODO: add full icon support
			super.setDescription(body.substring(3));
			super.setSummary(super.getDescription());
		}

	}

	private void setManuallyTagged() {
		this.setPlainText(false);
		this.setRelevant(true);
		this.setTagged(true);
		this.setTaggedManually(true);
		this.setTaggedFineGrained(true);
		ActiveObjectsManager.updateSentenceElement(this);
	}

	public boolean isPlainText() {
		return isPlainText;
	}

	public void setPlainText(boolean isPlainText) {
		this.isPlainText = isPlainText;
	}

	private void retrieveBodyFromJiraComment() {
		String text = ComponentAccessor.getCommentManager().getCommentById(this.commentId).getBody();
		if(this.endSubstringCount < text.length()) {
			text = text.substring(this.startSubstringCount, this.endSubstringCount);
		} else if(this.endSubstringCount == text.length()) {
			text = text.substring(this.startSubstringCount);
		}
		this.setBody(text);
		this.created = ComponentAccessor.getCommentManager().getCommentById(this.commentId).getCreated();
	}

	private void retrieveAttributesFromActievObjects() {
		DecisionKnowledgeInCommentEntity aoElement = ActiveObjectsManager.getElementFromAO(super.getId());
		this.setEndSubstringCount(aoElement.getEndSubstringCount());
		this.setStartSubstringCount(aoElement.getStartSubstringCount());
		this.setUserId(aoElement.getUserId());
		this.setTagged(aoElement.isTagged());
		this.setTaggedFineGrained(aoElement.isTaggedFineGrained());
		this.setTaggedManually(aoElement.isTaggedFineGrained());
		this.setProjectKey(aoElement.getProjectKey());
		this.setArgument(aoElement.getArgument());
		this.setCommentId(aoElement.getCommentId());
		this.setIssueId(aoElement.getIssueId());
		super.setProject(new DecisionKnowledgeProjectImpl(getProjectDescription()));
		String kt = aoElement.getKnowledgeTypeString();
		if (kt == null || kt.equals("")) {
			super.type = KnowledgeType.OTHER;
		} else {
			super.type = KnowledgeType.getKnowledgeType(kt);
		}
		IssueManager im = ComponentAccessor.getIssueManager();
		MutableIssue mi = im.getIssueObject(this.getIssueId());
		super.setKey(mi.getKey() + ":" + this.getId());
	}

	private String getProjectDescription() {
		if (ComponentGetter.getProjectService() != null) {
			return ComponentGetter.getProjectService().getProjectKeyDescription();
		} else {
			return "TEST";
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

	@Override
	public void setIssueId(long issueId) {
		this.issueId = issueId;

	}

	@Override
	public long getIssueId() {
		return this.issueId;
	}

	@Override
	public Date getCreated() {
		return this.created;
	}

	@Override
	public void setCreated(Date date) {
		this.created = date;
	}

}
