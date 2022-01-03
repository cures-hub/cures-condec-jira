package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

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

	public Map<Integer, List<KnowledgeElement>> getNumberOfCommentsPerIssue() {
		LOGGER.info("CommentMetricCalculator numberOfCommentsPerIssue");
		Map<Integer, List<KnowledgeElement>> numberOfCommentsPerJiraIssue = new HashMap<>();
		for (CharacterizedJiraIssue jiraIssue : characterizedJiraIssues) {
			int numberOfComments = jiraIssue.getNumberOfComments();
			if (!numberOfCommentsPerJiraIssue.containsKey(numberOfComments)) {
				numberOfCommentsPerJiraIssue.put(numberOfComments, new ArrayList<>());
			}
			numberOfCommentsPerJiraIssue.get(numberOfComments).add(jiraIssue);
		}
		return numberOfCommentsPerJiraIssue;
	}

	public Map<String, Integer> getNumberOfRelevantComments() {
		LOGGER.info("CommentMetricCalculator getNumberOfRelevantComments");
		Map<String, Integer> commentRelevanceMap = new LinkedHashMap<>();
		commentRelevanceMap.put("Relevant Comment", numberOfRelevantComments);
		commentRelevanceMap.put("Irrelevant Comment", numberOfIrrelevantComments);
		return commentRelevanceMap;
	}

	public Map<Integer, List<KnowledgeElement>> getNumberOfCommitsPerIssue() {
		LOGGER.info("CommentMetricCalculator numberOfCommitsPerIssue");
		Map<Integer, List<KnowledgeElement>> numberOfCommitsPerJiraIssue = new HashMap<>();
		for (CharacterizedJiraIssue jiraIssue : characterizedJiraIssues) {
			int numberOfComments = jiraIssue.getNumberOfCommits();
			if (!numberOfCommitsPerJiraIssue.containsKey(numberOfComments)) {
				numberOfCommitsPerJiraIssue.put(numberOfComments, new ArrayList<>());
			}
			numberOfCommitsPerJiraIssue.get(numberOfComments).add(jiraIssue);
		}
		return numberOfCommitsPerJiraIssue;
	}
}
