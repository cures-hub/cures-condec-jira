package de.uhd.ifi.se.decision.management.jira.extraction.model.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;

import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.util.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeInCommentEntity;

public class SentenceImpl extends DecisionKnowledgeElementImpl implements Sentence {

	private boolean isTagged;

	private boolean isRelevant;

	private int startSubstringCount;

	private int endSubstringCount;

	private boolean isTaggedManually;

	private boolean isTaggedFineGrained;

	private boolean isPlainText;

	private String projectKey;

	private long commentId;

	private long userId;

	private long issueId;

	private Date created;

	public SentenceImpl() {
		super();
		this.documentationLocation = DocumentationLocation.JIRAISSUECOMMENT;
	}

	public SentenceImpl(DecisionKnowledgeInCommentEntity databaseEntry) throws NullPointerException {
		this(databaseEntry.getId(), databaseEntry.getEndSubstringCount(), databaseEntry.getStartSubstringCount(),
				databaseEntry.getUserId(), databaseEntry.isTagged(), databaseEntry.isRelevant(),
				databaseEntry.isTaggedFineGrained(), databaseEntry.isTaggedManually(), databaseEntry.getProjectKey(),
				databaseEntry.getCommentId(), databaseEntry.getIssueId(), databaseEntry.getType());
	}

	public SentenceImpl(long id, int endSubstringCount, int startSubstringCount, long userId, boolean isTagged,
			boolean isRelevant, boolean isTaggedFineGrained, boolean isTaggedManually, String projectKey,
			long commentId, long issueId, String type) {
		this();
		this.setId(id);
		this.setEndSubstringCount(endSubstringCount);
		this.setStartSubstringCount(startSubstringCount);
		this.setUserId(userId);
		this.setTagged(isTagged);
		this.setRelevant(isRelevant);
		this.setTaggedFineGrained(isTaggedFineGrained);
		this.setTaggedManually(isTaggedManually);
		this.setProjectKey(projectKey);
		this.setCommentId(commentId);
		this.setIssueId(issueId);
		this.setProject(new DecisionKnowledgeProjectImpl(projectKey));
		this.type = KnowledgeType.getKnowledgeType(type);
		MutableIssue mutableIssue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
		if (mutableIssue != null) {
			this.setKey(mutableIssue.getKey() + ":" + this.getId());
		}
		retrieveBodyFromJiraComment();
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

	public void setType(double[] prediction) {
		if (prediction[0] == 1.) {
			this.type = KnowledgeType.ALTERNATIVE;
		} else if (prediction[3] == 1.) {
			this.type = KnowledgeType.DECISION;
		} else if (prediction[4] == 1.) {
			this.type = KnowledgeType.ISSUE;
		} else if (prediction[1] == 1.) {
			this.type = KnowledgeType.PRO;
		} else if (prediction[2] == 1.) {
			this.type = KnowledgeType.CON;
		}
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
		if (StringUtils.indexOfAny(body.toLowerCase(), CommentSplitter.excludedTagList) >= 0) {
			this.isPlainText = false;
		}
		if (CommentSplitter.isAnyKnowledgeTypeTwiceExisintg(body, this.projectKey)
				|| (ConfigPersistenceManager.isIconParsing(projectKey)
						&& StringUtils.indexOfAny(body, CommentSplitter.manualRationalIconList) >= 0)) {
			this.setType(CommentSplitter.getKnowledgeTypeFromManuallIssueTag(body, this.projectKey, true));
			setManuallyTagged();
			stripTagsFromBody(body);
		}
	}

	private void stripTagsFromBody(String body) {
		if (CommentSplitter.isAnyKnowledgeTypeTwiceExisintg(body, this.projectKey)) {
			int tagLength = 2
					+ CommentSplitter.getKnowledgeTypeFromManuallIssueTag(body, this.projectKey, true).length();
			super.setDescription(body.substring(tagLength, body.length() - (tagLength)));
			super.setSummary(super.getDescription());
		} else {
			super.setDescription(body.replaceAll("\\(.*?\\)", ""));
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
		try {
			if (this.commentId != 0 && this.commentId > 0) {
				String text = ComponentAccessor.getCommentManager().getCommentById(this.commentId).getBody();
				if (this.endSubstringCount < text.length()) {
					text = text.substring(this.startSubstringCount, this.endSubstringCount);
				} else if (this.endSubstringCount == text.length()) {
					text = text.substring(this.startSubstringCount);
				}
				this.setBody(text);
				this.created = ComponentAccessor.getCommentManager().getCommentById(this.commentId).getCreated();
			}
		} catch (StringIndexOutOfBoundsException e) {
			this.setBody("");
		}
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
