package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;

public class CommentMetricCalculator {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommentMetricCalculator.class);
	private List<CharacterizedJiraIssue> characterizedJiraIssues;
	private int numberOfRelevantComments;
	private int numberOfIrrelevantComments;

	public CommentMetricCalculator(List<Issue> jiraIssues) {
		characterizedJiraIssues = new ArrayList<>();
		numberOfRelevantComments = 0;
		numberOfIrrelevantComments = 0;

		for (Issue jiraIssue : jiraIssues) {
			CharacterizedJiraIssue characterizedJiraIssue = new CharacterizedJiraIssue(jiraIssue);
			characterizedJiraIssues.add(characterizedJiraIssue);
			numberOfRelevantComments += characterizedJiraIssue.getNumberOfRelevantComments();
			numberOfIrrelevantComments += characterizedJiraIssue.getNumberOfIrrelevantComments();
		}
	}

	public Map<String, Integer> getNumberOfCommentsPerIssue() {
		LOGGER.info("CommentMetricCalculator numberOfCommentsPerIssue");
		Map<String, Integer> numberOfCommentsPerJiraIssue = new HashMap<String, Integer>();
		characterizedJiraIssues.forEach(
				jiraIssue -> numberOfCommentsPerJiraIssue.put(jiraIssue.getKey(), jiraIssue.getNumberOfComments()));
		return numberOfCommentsPerJiraIssue;
	}

	public Map<String, Integer> getNumberOfRelevantComments() {
		LOGGER.info("CommentMetricCalculator getNumberOfRelevantComments");
		Map<String, Integer> commentRelevanceMap = new LinkedHashMap<String, Integer>();
		commentRelevanceMap.put("Relevant Comment", numberOfRelevantComments);
		commentRelevanceMap.put("Irrelevant Comment", numberOfIrrelevantComments);
		return commentRelevanceMap;
	}

	public Map<String, Integer> getNumberOfCommitsPerIssue() {
		LOGGER.info("CommentMetricCalculator numberOfCommitsPerIssue");
		Map<String, Integer> numberOfCommitsPerJiraIssue = new HashMap<String, Integer>();
		characterizedJiraIssues.forEach(
				jiraIssue -> numberOfCommitsPerJiraIssue.put(jiraIssue.getKey(), jiraIssue.getNumberOfCommits()));
		return numberOfCommitsPerJiraIssue;
	}
}
