package de.uhd.ifi.se.decision.management.jira.model.text;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
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
	private long jiraIssueId;
	private long commentId;

	protected static final Logger LOGGER = LoggerFactory.getLogger(PartOfJiraIssueText.class);

	public PartOfJiraIssueText() {
		this.documentationLocation = DocumentationLocation.JIRAISSUETEXT;
	}

	public PartOfJiraIssueText(int startPosition, int endPosition) {
		this.startPosition = startPosition;
		this.endPosition = endPosition;
	}

	public static PartOfJiraIssueText getFirstPartOfTextInComment(Comment comment) {
		String projectKey = comment.getIssue().getProjectObject().getKey();
		List<PartOfJiraIssueText> partsOfText = new TextSplitter().getPartsOfText(comment.getBody(), projectKey);
		if (partsOfText.isEmpty()) {
			return null;
		}
		partsOfText.get(0).setComment(comment);
		return partsOfText.get(0);
	}

	public PartOfJiraIssueText(PartOfJiraIssueTextInDatabase databaseEntry) {
		this(databaseEntry.getId(), databaseEntry.getEndPosition(), databaseEntry.getStartPosition(),
				databaseEntry.isValidated(), databaseEntry.isRelevant(), databaseEntry.getProjectKey(),
				databaseEntry.getCommentId(), databaseEntry.getJiraIssueId(), databaseEntry.getType(),
				databaseEntry.getStatus());
	}

	public PartOfJiraIssueText(long id, int endSubstringCount, int startSubstringCount, boolean isValidated,
			boolean isRelevant, String projectKey, long commentId, long issueId, String type, String status) {
		this();
		this.setId(id);
		this.setEndPosition(endSubstringCount);
		this.setStartPosition(startSubstringCount);
		this.setValidated(isValidated);
		this.setRelevant(isRelevant);
		this.setProject(projectKey);
		this.setCommentId(commentId);
		this.setJiraIssueId(issueId);
		this.setProject(new DecisionKnowledgeProject(projectKey));
		this.setType(type);
		this.setStatus(status);
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject(issueId);
		if (issue != null) {
			this.setKey(issue.getKey() + ":" + this.getId());
		}
		String text = "";
		Comment comment = this.getComment();
		if (comment == null) {
			text = getJiraIssueDescription();
			if (issue != null) {
				this.setCreated(issue.getCreated());
			}
		} else {
			text = comment.getBody();
			this.setCreated(comment.getCreated());
		}
		try {
			if (endSubstringCount < text.length()) {
				text = text.substring(startSubstringCount, endSubstringCount);
			} else if (endSubstringCount == text.length()) {
				text = text.substring(startSubstringCount);
			}
		} catch (NullPointerException | StringIndexOutOfBoundsException e) {
			LOGGER.error("Constructor faild to create object of PartOfJiraIssueText. Message: " + e.getMessage());
		}
		if (text == null) {
			text = "";
		}
		this.setDescription(text);
		this.setPlainText(!containsExcludedTag(text));
		stripTagsFromBody(text);
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
	 * @return true if the text is decision knowledge.
	 */
	public boolean isRelevant() {
		return this.isRelevant;
	}

	/**
	 * Sets whether the part of the text is decision knowledge, i.e., relevant.
	 * 
	 * @param isRelevant
	 *            true of the text is decision knowledge.
	 */
	public void setRelevant(boolean isRelevant) {
		this.isRelevant = isRelevant;
	}

	/**
	 * @return true if the classification of the text within the Jira issue comment
	 *         is validated, i.e., is manually performed, updated, or checked by a
	 *         human beeing.
	 */
	public boolean isValidated() {
		return this.isValidated;
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
		return this.getType() != KnowledgeType.OTHER;
	}

	/**
	 * @return start position (number of characters) of the decision knowledge
	 *         element or the irrelevant text within the entire text.
	 */
	public int getStartPosition() {
		return this.startPosition;
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
		return this.endPosition;
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
		return this.endPosition - this.startPosition;
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
	 * Sets the id of the Jira issue that the decision knowledge element or
	 * irrelevant text is part of.
	 * 
	 * @param id
	 *            of the Jira issue.
	 */
	public void setJiraIssueId(long issueId) {
		this.jiraIssueId = issueId;
	}

	/**
	 * @return id of the Jira issue that the decision knowledge element or
	 *         irrelevant text is part of.
	 */
	public long getJiraIssueId() {
		return this.jiraIssueId;
	}

	@Override
	public Issue getJiraIssue() {
		return ComponentAccessor.getIssueManager().getIssueObject(jiraIssueId);
	}

	private boolean containsExcludedTag(String body) {
		if (body == null) {
			return false;
		}
		return StringUtils.indexOfAny(body.toLowerCase(), TextSplitter.EXCLUDED_TAGS) >= 0;
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
		Comment comment = this.getComment();
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

	private void stripTagsFromBody(String body) {
		if (body == null) {
			return;
		}
		String projectKey = this.getProject().getProjectKey();
		if (TextSplitter.isAnyKnowledgeTypeTwiceExisting(body, projectKey)) {
			int tagLength = 2 + TextSplitter.getKnowledgeTypeFromTag(body, projectKey).toString().length();
			super.setDescription(body.substring(tagLength, body.length() - (tagLength)));
			super.setSummary(super.getDescription());
		} else {
			super.setDescription(body.replaceAll("\\(.*?\\)", ""));
			super.setSummary(super.getDescription());
		}
	}

	/**
	 * @return id of the Jira issue comment that the decision knowledge element or
	 *         irrelevant text is part of. Returns 0 if the part of text is within
	 *         the description.
	 */
	public long getCommentId() {
		return this.commentId;
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
			this.setCommentId(comment.getId());
			this.setJiraIssueId(comment.getIssue().getId());
			this.setCreated(comment.getCreated());
		}
	}

	/**
	 * @return Jira issue description that the decision knowledge element or
	 *         irrelevant text is part of.
	 */
	public String getJiraIssueDescription() {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueObject(this.getJiraIssueId());
		if (issue == null) {
			return super.getSummary();
		}
		return issue.getDescription();
	}

	@Override
	public ApplicationUser getCreator() {
		Comment comment = this.getComment();
		if (comment != null) {
			return comment.getAuthorApplicationUser();
		}
		Issue issue = this.getJiraIssue();
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
		if (this.getEndPosition() == 0 && this.getStartPosition() == 0) {
			return false;
		}
		if (this.getCommentId() <= 0) {
			// documented in Jira issue description
			return true;
		}
		return this.getComment() != null;
	}
}
