package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;

/**
 * purpose: extract decision knowledge stored in git repository
 * out-of-scope linking decision knowledge elements among each other
 */
public class GitDecXtract {

	private final GitClientImpl gitClient;
	private final String projecKey;

	public GitDecXtract(String projecKey) {
		this.projecKey = projecKey;
		gitClient = new GitClientImpl(projecKey);
	}

	public GitDecXtract(String projecKey, String uri) {
		this.projecKey = projecKey;
		gitClient = new GitClientImpl(uri, projecKey);
	}

	// TODO: below method signature will change as soon as the interface is specified.
	public void getElements(String featureBranchShortName) {

	}
}
