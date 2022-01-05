package de.uhd.ifi.se.decision.management.jira.metric;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.atlassian.jira.issue.Issue;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

/**
 * Calculates metrics on the comments of Jira issues.
 * 
 * @see CharacterizedJiraIssue
 */
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

	/**
	 * @return number of relevant comments of all the Jira issues, i.e. number of
	 *         comments with decision knowledge documented in the comment text.
	 */
	public int getNumberOfRelevantComments() {
		return numberOfRelevantComments;
	}

	/**
	 * @return number of irrelevant comments of all the Jira issues, i.e. number of
	 *         comments without decision knowledge documented in the comment text.
	 */
	public int getNumberOfIrrelevantComments() {
		return numberOfIrrelevantComments;
	}

	/**
	 * @return map with number of comments as keys and elements (Jira issues) that
	 *         have the respective number of comments as map values.
	 */
	public Map<Integer, List<KnowledgeElement>> getNumberOfCommentsPerJiraIssueMap() {
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

	/**
	 * @return map with number of commits as keys and elements (Jira issues) that
	 *         have the respective number of commits linked as map values.
	 */
	public Map<Integer, List<KnowledgeElement>> getNumberOfCommitsPerJiraIssueMap() {
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
