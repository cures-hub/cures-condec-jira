package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

public class CharacterizedJiraIssue {

	private List<Comment> comments;
	private Issue jiraIssue;
	private int numberOfRelevantComments;
	private int numberOfIrrelevantComment;
	private int numberOfCommits;

	public CharacterizedJiraIssue(Issue jiraIssue) {
		this.jiraIssue = jiraIssue;
		comments = ComponentAccessor.getCommentManager().getComments(jiraIssue);

		String projectKey = jiraIssue.getProjectObject().getKey();

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
		for (Comment comment : comments) {
			List<PartOfJiraIssueText> elements = persistenceManager.getElementsInComment(comment.getId());
			if (elements.isEmpty()) {
				numberOfIrrelevantComment++;
			} else {
				numberOfRelevantComments++;
			}
		}

		if (ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			numberOfCommits = GitClient.getOrCreate(projectKey).getNumberOfCommits(jiraIssue);
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

	public int getNumberOfCommits() {
		return numberOfCommits;
	}
}
