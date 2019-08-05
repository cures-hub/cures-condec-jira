package de.uhd.ifi.se.decision.management.jira.extraction;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
This class provides statistics for ConDec report page.
It was born as result of first simplification actions done in the ../jira/view/DecisionKnowledgeReport.java
 */
public class GitExtractor {

	private GitClient gitClient;

	/*
	TODO: make sure commit caches get eventually updated,
		decide upon a strategy (ondemand,expiration?)
	*/
	public GitExtractor(String projectKey) {
		/* TODO: check git client creation result */
		gitClient = new GitClientImpl(projectKey);
	}

	/*
	TODO: make sure commit cache gets eventually updated, below approach is just
		for development/debugging
	*/
	public List<RevCommit> getListOfCommitsForJiraIssue(Issue issue) {
		Map<String, List<RevCommit>> jiraIssueKeyedCommits = new HashMap<>();
		if (!jiraIssueKeyedCommits.containsKey(issue.getKey())) {
			List<RevCommit> commits = gitClient.getCommits(issue);
			jiraIssueKeyedCommits.put(issue.getKey(),commits);
		}
		return jiraIssueKeyedCommits.get(issue.getKey());
	}
}
