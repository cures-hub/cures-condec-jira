package de.uhd.ifi.se.decision.management.jira.quality;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.model.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

public class CommentMetricCalculator {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommentMetricCalculator.class);
	List<Issue> jiraIssues;
	List<Issue> comments;
	String projectKey;

	public CommentMetricCalculator(List<Issue> jiraIssues, String projectKey) {
		this.jiraIssues = jiraIssues;
		this.projectKey = projectKey;

	}

	public Map<String, Integer> numberOfCommentsPerIssue() {
		LOGGER.info("RequirementsDashboard numberOfCommentsPerIssue");
		Map<String, Integer> numberMap = new HashMap<String, Integer>();
		int numberOfComments;
		for (Issue jiraIssue : jiraIssues) {
			try {
				numberOfComments = ComponentAccessor.getCommentManager().getComments(jiraIssue).size();
			} catch (NullPointerException e) {
				LOGGER.error("Getting number of comments for Jira issues failed. Message: " + e.getMessage());
				numberOfComments = 0;
			}
			numberMap.put(jiraIssue.getKey(), numberOfComments);
		}
		return numberMap;
	}

	public Map<String, Integer> getNumberOfRelevantComments() {
		LOGGER.info("RequirementsDashboard getNumberOfRelevantComments");
		Map<String, Integer> numberOfRelevantSentences = new LinkedHashMap<String, Integer>();
		int isRelevant = 0;
		int isIrrelevant = 0;

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();
		for (Issue jiraIssue : jiraIssues) {
			// TODO only do this once
			List<Comment> comments = ComponentAccessor.getCommentManager().getComments(jiraIssue);
			for (Comment comment : comments) {
				List<PartOfJiraIssueText> elements = persistenceManager.getElementsInComment(comment.getId());
				if (elements.isEmpty()) {
					isIrrelevant++;
				} else {
					isRelevant++;
				}
			}
		}
		numberOfRelevantSentences.put("Relevant Comment", isRelevant);
		numberOfRelevantSentences.put("Irrelevant Comment", isIrrelevant);
		return numberOfRelevantSentences;
	}

}
