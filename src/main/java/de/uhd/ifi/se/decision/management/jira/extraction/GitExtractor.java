package de.uhd.ifi.se.decision.management.jira.extraction;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GitExtractor {

	private GitClient gitClient;

	/*
	TODO: make sure commit caches get eventually updated,
			decide upon a strategy (ondemand,expiration?)
	*/
	private Map<String, List<String>> jiraIssueKeyedBranchCommits;
	private Map<String, List<RevCommit>> jiraIssueKeyedCommits;
	private Map<String, List<RevCommit>> branchKeyedCommits;

	public GitExtractor(String projectKey) {
		jiraIssueKeyedBranchCommits = new HashMap<>();
		jiraIssueKeyedCommits = new HashMap<>();
		branchKeyedCommits = new HashMap<>();
		/* TODO: check git client creation result */
		gitClient = new GitClientImpl(projectKey);
	}

	/*
	TODO: make sure commit cache gets eventually updated, below approach is just
		for development/debugging
	*/
	public List<RevCommit> GetListOfCommitsForJiraIssue(Issue issue) {
		if (!jiraIssueKeyedCommits.containsKey(issue.getKey())) {
			List<RevCommit> commits = gitClient.getCommits(issue);
			jiraIssueKeyedCommits.put(issue.getKey(),commits);
		}
		return jiraIssueKeyedCommits.get(issue.getKey());
	}
}
