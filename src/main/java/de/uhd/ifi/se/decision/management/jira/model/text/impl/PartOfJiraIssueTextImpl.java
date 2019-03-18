package de.uhd.ifi.se.decision.management.jira.model.text.impl;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfText;
import de.uhd.ifi.se.decision.management.jira.model.text.TextSplitter;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;

/**
 * Model class for textual parts (substrings) of JIRA issue comments or the
 * description. These parts can either be relevant decision knowledge elements
 * or irrelevant text.
 */
public class PartOfJiraIssueTextImpl extends PartOfTextImpl implements PartOfJiraIssueText {

	private long commentId;

	public PartOfJiraIssueTextImpl() {
		super();
		this.documentationLocation = DocumentationLocation.JIRAISSUETEXT;
	}

	public PartOfJiraIssueTextImpl(Comment comment) {
		this();
		if (comment == null) {
			return;
		}
		this.setCommentId(comment.getId());
		this.setJiraIssueId(comment.getIssue().getId());
		this.setCreated(comment.getCreated());
	}

	public PartOfJiraIssueTextImpl(PartOfText partOfText, Comment comment) {
		this(comment);
		this.setEndSubstringCount(partOfText.getEndSubstringCount());
		this.setStartSubstringCount(partOfText.getStartSubstringCount());
		this.setRelevant(partOfText.isRelevant());
		this.setValidated(partOfText.isValidated());
		this.setType(partOfText.getType());
		this.setProject(partOfText.getProject());
	}

	public PartOfJiraIssueTextImpl(PartOfJiraIssueTextInDatabase databaseEntry) {
		this(databaseEntry.getId(), databaseEntry.getEndSubstringCount(), databaseEntry.getStartSubstringCount(),
				databaseEntry.isValidated(), databaseEntry.isRelevant(), databaseEntry.getProjectKey(),
				databaseEntry.getCommentId(), databaseEntry.getJiraIssueId(), databaseEntry.getType());
	}

	public PartOfJiraIssueTextImpl(long id, int endSubstringCount, int startSubstringCount, boolean isValidated,
			boolean isRelevant, String projectKey, long commentId, long issueId, String type) {
		this();
		this.setId(id);
		this.setEndSubstringCount(endSubstringCount);
		this.setStartSubstringCount(startSubstringCount);
		this.setValidated(isValidated);
		this.setRelevant(isRelevant);
		this.setProject(projectKey);
		this.setCommentId(commentId);
		this.setJiraIssueId(issueId);
		this.setProject(new DecisionKnowledgeProjectImpl(projectKey));
		this.setType(type);
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
		if (issue != null) {
			this.setKey(issue.getKey() + ":" + this.getId());
		}
		if (commentId <= 0) {
			return;
		}
		Comment comment = this.getComment();
		String text = comment.getBody();
		try {
			if (endSubstringCount < text.length()) {
				text = text.substring(startSubstringCount, endSubstringCount);
			} else if (endSubstringCount == text.length()) {
				text = text.substring(startSubstringCount);
			}
		} catch (StringIndexOutOfBoundsException e) {
			e.printStackTrace();
		}
		this.setDescription(text);
		this.setCreated(comment.getCreated());
		this.setPlainText(!containsExcludedTag(text));
		stripTagsFromBody(text);
	}

	private boolean containsExcludedTag(String body) {
		return StringUtils.indexOfAny(body.toLowerCase(), TextSplitter.EXCLUDED_TAGS) >= 0;
	}

	@Override
	public MutableComment getComment() {
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		if (commentManager == null) {
			return null;
		}
		return commentManager.getMutableComment(this.getCommentId());
	}

	@Override
	public String getText() {
		Comment comment = this.getComment();
		if (comment == null) {
			return super.getSummary();
		}
		String body = comment.getBody().substring(this.getStartSubstringCount(), this.getEndSubstringCount());
		return body.replaceAll("\\{.*?\\}", "");
	}

	@Override
	public void setDescription(String body) {
		super.setDescription(body);
		super.setSummary(body);
	}

	@Override
	public void setSummary(String body) {
		super.setDescription(body);
		super.setSummary(body);
	}

	private void stripTagsFromBody(String body) {
		String projectKey = this.getProject().getProjectKey();
		if (TextSplitterImpl.isAnyKnowledgeTypeTwiceExisting(body, projectKey)) {
			int tagLength = 2 + TextSplitterImpl.getKnowledgeTypeFromTag(body, projectKey).toString().length();
			super.setDescription(body.substring(tagLength, body.length() - (tagLength)));
			super.setSummary(super.getDescription());
		} else {
			super.setDescription(body.replaceAll("\\(.*?\\)", ""));
			super.setSummary(super.getDescription());
		}
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
	public String getJiraIssueDescription() {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueObject(this.getJiraIssueId());
		if (issue == null) {
			return super.getSummary();
		}
		return issue.getDescription();
	}
}
