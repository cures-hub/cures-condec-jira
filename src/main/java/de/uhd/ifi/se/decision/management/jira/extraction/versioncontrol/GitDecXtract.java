package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;

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
		gitClient = new GitClientImpl(uri,projecKey);
	}

	// TODO: below method signature will further improve
	public List<DecisionKnowledgeElement> getElements(String featureBranchShortName) {
		List<DecisionKnowledgeElement> gatheredElements = new ArrayList<>();
		List<RevCommit> featureCommits = gitClient.getFeatureBranchCommits(featureBranchShortName);
		if (featureCommits == null || featureCommits.size() == 0) {
			return gatheredElements;
		}
		return gatheredElements;
	}
}
