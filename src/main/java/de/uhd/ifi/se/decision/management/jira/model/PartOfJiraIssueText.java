package de.uhd.ifi.se.decision.management.jira.model;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.extraction.parser.JiraIssueTextParser;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.PartOfJiraIssueTextInDatabase;

/**
 * Models textual parts (substrings) of Jira issue comments or the description.
 * These parts can either be relevant decision knowledge elements or irrelevant
 * text.
 */
public class PartOfJiraIssueText extends KnowledgeElement {

	private int startPosition;
	private int endPosition;
	private boolean isRelevant;
	private boolean isValidated;
	private boolean isPlainText;
	private Issue jiraIssue;
	private long commentId;

	protected static final Logger LOGGER = LoggerFactory.getLogger(PartOfJiraIssueText.class);

	public PartOfJiraIssueText() {
		this.documentationLocation = DocumentationLocation.JIRAISSUETEXT;
	}

	public PartOfJiraIssueText(PartOfJiraIssueTextInDatabase databaseEntry) {
		this();
		this.setId(databaseEntry.getId());
		this.setStartPosition(databaseEntry.getStartPosition());
		this.setEndPosition(databaseEntry.getEndPosition());
		this.setValidated(databaseEntry.isValidated());
		this.setRelevant(databaseEntry.isRelevant());
		this.setProject(databaseEntry.getProjectKey());
		this.setCommentId(databaseEntry.getCommentId());
		this.setJiraIssue(databaseEntry.getJiraIssueId());
		this.setType(databaseEntry.getType());
		this.setStatus(databaseEntry.getStatus());

		String text = "";
		Comment comment = getComment();
		if (comment == null) {
			text = getJiraIssueDescription();
		} else {
			text = comment.getBody();
		}
		try {
			if (endPosition < text.length()) {
				text = text.substring(startPosition, endPosition);
			} else if (endPosition == text.length()) {
				text = text.substring(startPosition);
			}
		} catch (NullPointerException | StringIndexOutOfBoundsException e) {
			LOGGER.error("Constructor faild to create object of PartOfJiraIssueText. Message: " + e.getMessage());
		}
		text = new JiraIssueTextParser(databaseEntry.getProjectKey()).stripTagsFromBody(text);
		this.setDescription(text);
		this.setPlainText(!containsExcludedTag(text));
	}

	public PartOfJiraIssueText(KnowledgeElement element) {
		this.setId(element.getId());
		this.setType(element.getType());
		this.setSummary(element.getSummary());
		this.setDescription(element.getDescription());
		this.setProject(element.getProject());
		this.setStatus(element.getStatus());
	}

	/**
	 * @return true if the text is decision knowledge. This attribute is necessary
	 *         for binary text classification.
	 */
	public boolean isRelevant() {
		return isRelevant;
	}

	/**
	 * Sets whether the part of the text is decision knowledge, i.e., relevant. This
	 * attribute is necessary for binary text classification.
	 * 
	 * @param isRelevant
	 *            true of the text is decision knowledge.
	 */
	public void setRelevant(boolean isRelevant) {
		this.isRelevant = isRelevant;
	}

	@Override
	public void setType(KnowledgeType type) {
		super.setType(type);
		setRelevant(type != KnowledgeType.OTHER);
	}

	/**
	 * @return true if the classification of the text within the Jira issue comment
	 *         is validated, i.e., is manually performed, updated, or checked by a
	 *         human beeing.
	 */
	public boolean isValidated() {
		return isValidated;
	}

	/**
	 * @param isValidated
	 *            true if the classification of the text within the Jira issue
	 *            comment is validated. That means that the classification is
	 *            manually performed, updated, or checked by a human beeing.
	 */
	public void setValidated(boolean isValidated) {
		this.isValidated = isValidated;
	}

	/**
	 * Determines whether the part of the text is relevant decision knowledge with a
	 * knowledge type different than KnowledgeType.OTHER. Then, the part of the text
	 * is tagged with the pattern: {knowledge type} text {knowledge type}.
	 * 
	 * @see KnowledgeType
	 * @return true if the part of the text is relevant decision knowledge with a
	 *         knowledge type different than KnowledgeType.OTHER.
	 */
	public boolean isTagged() {
		return getType() != KnowledgeType.OTHER;
	}

	/**
	 * @return start position (number of characters) of the decision knowledge
	 *         element or the irrelevant text within the entire text.
	 */
	public int getStartPosition() {
		return startPosition;
	}

	/**
	 * Set the start position (number of characters) of the decision knowledge
	 * element or the irrelevant text within the entire text.
	 * 
	 * @param startPosition
	 *            number of characters after that the decision knowledge element or
	 *            the irrelevant text starts.
	 */
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	/**
	 * @return end position (number of characters) of the decision knowledge element
	 *         or the irrelevant text within the entire text.
	 */
	public int getEndPosition() {
		return endPosition;
	}

	/**
	 * Set the end position (number of characters) of the decision knowledge element
	 * or the irrelevant text within the entire text.
	 * 
	 * @param endPosition
	 *            number of characters after that the decision knowledge element or
	 *            the irrelevant text ends.
	 */
	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	/**
	 * Gets the length of the part of the text (substring). This is a derived
	 * method.
	 * 
	 * @return end position - start position.
	 */
	public int getLength() {
		return endPosition - startPosition;
	}

	/**
	 * @return true if the text of the decision knowledge element or irrelevant text
	 *         is plain, e.g., does not contain any code or logger ouput.
	 */
	public boolean isPlainText() {
		return isPlainText;
	}

	/**
	 * Sets whether the text of the decision knowledge element or irrelevant text is
	 * plain, e.g., does not contain any code or logger ouput.
	 * 
	 * @param isPlainText
	 *            true if the text of the decision knowledge element or irrelevant
	 *            text is plain, e.g., does not contain any code or logger ouput.
	 */
	public void setPlainText(boolean isPlainText) {
		this.isPlainText = isPlainText;
	}

	/**
	 * Sets the the Jira issue that the decision knowledge element or irrelevant
	 * text is part of by its id.
	 * 
	 * @param jiraIssueId
	 *            of the Jira issue.
	 */
	public void setJiraIssue(long jiraIssueId) {
		this.jiraIssue = ComponentAccessor.getIssueManager().getIssueObject(jiraIssueId);
	}

	/**
	 * Sets the the Jira issue that the decision knowledge element or irrelevant
	 * text is part of by its id.
	 * 
	 * @param jiraIssue
	 *            Jira issue.
	 */
	public void setJiraIssue(Issue jiraIssue) {
		this.jiraIssue = jiraIssue;
	}

	/**
	 * @return Jira issue that the decision knowledge element or irrelevant text is
	 *         part of.
	 */
	@Override
	public Issue getJiraIssue() {
		return jiraIssue;
	}

	@Override
	public String getKey() {
		Issue issue = getJiraIssue();
		if (issue != null) {
			return issue.getKey() + ":" + getId();
		}
		return super.getKey();
	}

	@Override
	public Date getCreationDate() {
		Comment comment = getComment();
		if (comment != null) {
			return comment.getCreated();
		}
		Issue issue = getJiraIssue();
		if (issue != null) {
			return issue.getCreated();
		}
		return super.getCreationDate();
	}

	@Override
	public Date getUpdatingDate() {
		Comment comment = getComment();
		if (comment != null) {
			return comment.getUpdated();
		}
		Issue issue = getJiraIssue();
		if (issue != null) {
			return issue.getUpdated();
		}
		return super.getUpdatingDate();
	}

	private boolean containsExcludedTag(String body) {
		if (body == null) {
			return false;
		}
		return StringUtils.indexOfAny(body.toLowerCase(), JiraIssueTextParser.EXCLUDED_TAGS) >= 0;
	}

	/**
	 * @return Jira issue comment as a {@link MutableComment} object that the
	 *         decision knowledge element or irrelevant text is part of. Returns
	 *         null if the part of text is within the description.
	 */
	public MutableComment getComment() {
		long commentId = getCommentId();
		if (commentId <= 0) {
			return null;
		}
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		if (commentManager == null) {
			return null;
		}
		return commentManager.getMutableComment(commentId);
	}

	/**
	 * @return part of the text.
	 */
	public String getText() {
		Comment comment = getComment();
		if (comment == null) {
			return super.getSummary();
		}
		String body = comment.getBody().substring(this.getStartPosition(), this.getEndPosition());
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

	/**
	 * @return id of the Jira issue comment that the decision knowledge element or
	 *         irrelevant text is part of. Returns 0 if the part of text is within
	 *         the description.
	 */
	public long getCommentId() {
		return commentId;
	}

	/**
	 * Sets the id of the Jira issue comment that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @param id
	 *            of the Jira issue comment.
	 */
	public void setCommentId(long id) {
		this.commentId = id;
	}

	/**
	 * @param comment
	 *            the Jira issue comment that the decision knowledge element or
	 *            irrelevant text is part of.
	 */
	public void setComment(Comment comment) {
		if (comment != null) {
			setCommentId(comment.getId());
			setJiraIssue(comment.getIssue());
			setCreationDate(comment.getCreated());
		}
	}

	/**
	 * @return Jira issue description that the decision knowledge element or
	 *         irrelevant text is part of.
	 */
	public String getJiraIssueDescription() {
		Issue issue = getJiraIssue();
		if (issue == null) {
			return super.getSummary();
		}
		return issue.getDescription();
	}

	@Override
	public ApplicationUser getCreator() {
		Comment comment = getComment();
		if (comment != null) {
			return comment.getAuthorApplicationUser();
		}
		Issue issue = getJiraIssue();
		if (issue != null) {
			return issue.getReporter();
		}
		return null;
	}

	/**
	 * @return true if the textual parts (substrings) of Jira issue comments or the
	 *         description is valid.
	 */
	public boolean isValid() {
		if (getEndPosition() == 0 && getStartPosition() == 0) {
			return false;
		}
		if (getCommentId() <= 0) {
			// documented in Jira issue description
			return true;
		}
		return getComment() != null;
	}
}
