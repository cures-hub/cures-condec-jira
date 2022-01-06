package de.uhd.ifi.se.decision.management.jira.metric;

import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.git.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Origin;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

/**
 * Represents a Jira issue with information about the number of comments, number
 * of relevant and irrelevant comments wrt. decision knowledge, and the number
 * of commits. In ConDec, Jira issues are modeled as specific
 * {@link KnowledgeElement}s.
 */
public class CharacterizedJiraIssue extends KnowledgeElement {

	private List<Comment> comments;
	private int numberOfRelevantComments;
	private int numberOfIrrelevantComment;
	private int numberOfCommits;

	public CharacterizedJiraIssue(Issue jiraIssue) {
		super(jiraIssue);
		comments = ComponentAccessor.getCommentManager().getComments(jiraIssue);

		String projectKey = jiraIssue.getProjectObject().getKey();

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey)
				.getJiraIssueTextManager();
		for (Comment comment : comments) {
			List<PartOfJiraIssueText> elements = persistenceManager.getElementsInComment(comment.getId());
			if (elements.stream().noneMatch(element -> element.getType() != KnowledgeType.OTHER)) {
				numberOfIrrelevantComment++;
			} else {
				numberOfRelevantComments++;
			}
			// Commits on default branch are transcribed into Jira issue comments
			if (Origin.determineOrigin(comment) == Origin.COMMIT) {
				numberOfCommits++;
			}
		}
	}

	/**
	 * @return number of comments of the Jira issue.
	 */
	public int getNumberOfComments() {
		return comments.size();
	}

	/**
	 * @return number of irrelevant comments of the Jira issue, i.e. number of
	 *         comments without decision knowledge documented in the comment text.
	 */
	public int getNumberOfIrrelevantComments() {
		return numberOfIrrelevantComment;
	}

	/**
	 * @return number of relevant comments of the Jira issue, i.e. number of
	 *         comments with decision knowledge documented in the comment text.
	 */
	public int getNumberOfRelevantComments() {
		return numberOfRelevantComments;
	}

	/**
	 * @return number of commits linked to the Jira issue by mentioning the Jira
	 *         issue key in the commit message. ConDec transcribes/posts the commit
	 *         messages into Jira issue comments. This method counts the Jira issue
	 *         comments that originate from commits in git.
	 * @see CommitMessageToCommentTranscriber
	 */
	public int getNumberOfCommits() {
		return numberOfCommits;
	}
}
