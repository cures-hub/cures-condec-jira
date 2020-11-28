package de.uhd.ifi.se.decision.management.jira.quality.commentmetrics;

import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

public class CharacterizedJiraIssue {

	private List<Comment> comments;
	private Issue jiraIssue;
	private int numberOfRelevantComments;
	private int numberOfIrrelevantComment;

	public CharacterizedJiraIssue(Issue jiraIssue) {
		this.jiraIssue = jiraIssue;
		comments = ComponentAccessor.getCommentManager().getComments(jiraIssue);
		numberOfRelevantComments = 0;
		numberOfIrrelevantComment = 0;

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(jiraIssue.getProjectObject().getKey()).getJiraIssueTextManager();
		for (Comment comment : comments) {
			List<PartOfJiraIssueText> elements = persistenceManager.getElementsInComment(comment.getId());
			if (elements.isEmpty()) {
				numberOfIrrelevantComment++;
			} else {
				numberOfRelevantComments++;
			}
		}
	}

	public Integer getNumberOfComments() {
		return comments.size();
	}

	public String getKey() {
		return jiraIssue.getKey();
	}

	public int getNumberOfIrrelevantComments() {
		return numberOfIrrelevantComment;
	}

	public int getNumberOfRelevantComments() {
		return numberOfRelevantComments;
	}
}
