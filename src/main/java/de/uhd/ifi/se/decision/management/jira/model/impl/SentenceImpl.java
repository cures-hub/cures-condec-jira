package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.extraction.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeInCommentEntity;

/**
 * Model class for textual parts (substrings) of JIRA issue comments. These
 * parts can either be relevant decision knowledge elements or irrelevant text.
 */
public class SentenceImpl extends DecisionKnowledgeElementImpl implements Sentence {

	private long commentId;
	private int startSubstringCount;
	private int endSubstringCount;
	private boolean isRelevant;
	private boolean isValidated;

	private boolean isPlainText;
	private long issueId;
	private Date created;

	public SentenceImpl() {
		super();
		this.documentationLocation = DocumentationLocation.JIRAISSUECOMMENT;
	}

	public SentenceImpl(long id, int endSubstringCount, int startSubstringCount, boolean isValidated,
			boolean isRelevant, String projectKey, long commentId, long issueId, String type) {
		this();
		this.setId(id);
		this.setEndSubstringCount(endSubstringCount);
		this.setStartSubstringCount(startSubstringCount);
		this.setValidated(isValidated);
		this.setRelevant(isRelevant);
		this.setProject(projectKey);
		this.setCommentId(commentId);
		this.setIssueId(issueId);
		this.setProject(new DecisionKnowledgeProjectImpl(projectKey));
		this.setType(type);
		MutableIssue mutableIssue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
		if (mutableIssue != null) {
			this.setKey(mutableIssue.getKey() + ":" + this.getId());
		}
		retrieveBodyFromJiraComment();
	}

	public SentenceImpl(DecisionKnowledgeInCommentEntity databaseEntry) {
		this(databaseEntry.getId(), databaseEntry.getEndSubstringCount(), databaseEntry.getStartSubstringCount(),
				databaseEntry.isValidated(), databaseEntry.isRelevant(), databaseEntry.getProjectKey(),
				databaseEntry.getCommentId(), databaseEntry.getIssueId(), databaseEntry.getType());
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
	public boolean isValidated() {
		return this.isValidated;
	}

	@Override
	public void setValidated(boolean isValidated) {
		this.isValidated = isValidated;
	}

	@Override
	public boolean isTagged() {
		return this.getType() != KnowledgeType.OTHER;
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
	public MutableComment getComment() {
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		return commentManager.getMutableComment(this.getCommentId());
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
	public int getLength() {
		return this.endSubstringCount - this.startSubstringCount;
	}

	@Override
	public void setType(KnowledgeType type) {
		// if (this.type != type) {
		// this.updateTagsInComment(type);
		// }
		super.setType(type);
	}

	@Override
	public String getBody() {
		MutableComment mutableComment = this.getComment();
		if (mutableComment == null) {
			return super.getSummary();
		}
		String body = mutableComment.getBody().substring(this.getStartSubstringCount(), this.getEndSubstringCount());
		body = body.replaceAll("\\{.*?\\}", "");
		return body;
	}

	@Override
	public void setBody(String body) {
		super.setDescription(body);
		super.setSummary(body);
		checkForPlainText(body);
	}

	private void checkForPlainText(String body) {
		this.isPlainText = true;
		if (containsExcludedTag(body)) {
			this.isPlainText = false;
		}
		String projectKey = this.getProject().getProjectKey();
		if (CommentSplitter.isAnyKnowledgeTypeTwiceExisting(body, projectKey)
				|| (ConfigPersistenceManager.isIconParsing(projectKey)
						&& StringUtils.indexOfAny(body, CommentSplitter.RATIONALE_ICONS) >= 0)) {
			this.setType(CommentSplitter.getKnowledgeTypeFromTag(body, projectKey));
			setManuallyTagged();
			stripTagsFromBody(body);
		}
	}

	private void setManuallyTagged() {
		this.setPlainText(false);
		this.setRelevant(true);
		// this.setValidated(true);
		JiraIssueCommentPersistenceManager.updateInDatabase(this);
	}

	private boolean containsExcludedTag(String body) {
		return StringUtils.indexOfAny(body.toLowerCase(), CommentSplitter.EXCLUDED_TAGS) >= 0;
	}

	private void stripTagsFromBody(String body) {
		String projectKey = this.getProject().getProjectKey();
		if (CommentSplitter.isAnyKnowledgeTypeTwiceExisting(body, projectKey)) {
			int tagLength = 2 + CommentSplitter.getKnowledgeTypeFromTag(body, projectKey).toString().length();
			super.setDescription(body.substring(tagLength, body.length() - (tagLength)));
			super.setSummary(super.getDescription());
		} else {
			super.setDescription(body.replaceAll("\\(.*?\\)", ""));
			super.setSummary(super.getDescription());
		}
	}

	@Override
	public boolean isPlainText() {
		return isPlainText;
	}

	@Override
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
