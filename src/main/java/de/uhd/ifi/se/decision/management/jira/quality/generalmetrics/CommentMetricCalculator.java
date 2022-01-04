package de.uhd.ifi.se.decision.management.jira.quality.generalmetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class CommentMetricCalculator {

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

	public int getNumberOfRelevantComments() {
		return numberOfRelevantComments;
	}

	public int getNumberOfIrrelevantComments() {
		return numberOfIrrelevantComments;
	}

	public Map<Integer, List<KnowledgeElement>> getNumberOfCommentsPerIssueMap() {
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

	public Map<Integer, List<KnowledgeElement>> getNumberOfCommitsPerIssueMap() {
		Map<Integer, List<KnowledgeElement>> numberOfCommitsPerJiraIssue = new HashMap<>();
		for (CharacterizedJiraIssue jiraIssue : characterizedJiraIssues) {
			int numberOfCommits = jiraIssue.getNumberOfCommits();
			if (!numberOfCommitsPerJiraIssue.containsKey(numberOfCommits)) {
				numberOfCommitsPerJiraIssue.put(numberOfCommits, new ArrayList<>());
			}
			numberOfCommitsPerJiraIssue.get(numberOfCommits).add(jiraIssue);
		}
		return numberOfCommitsPerJiraIssue;
	}
}
