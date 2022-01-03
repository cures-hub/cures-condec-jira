package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Origin;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

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
			if (elements.isEmpty()) {
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

	public Integer getNumberOfComments() {
		return comments.size();
	}

	public int getNumberOfIrrelevantComments() {
		return numberOfIrrelevantComment;
	}

	public int getNumberOfRelevantComments() {
		return numberOfRelevantComments;
	}

	public int getNumberOfCommits() {
		return numberOfCommits;
	}
}
