package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * purpose: extract decision knowledge elements stored in git repository
 * out-of-scope linking decision knowledge elements among each other
 */
public class GitDecXtract {

	private static final String COMMIT_POSITION_SEPARATOR = "_";
	private final GitClient gitClient;
	private final String projecKey;

	public GitDecXtract(String projecKey) {
		this.projecKey = projecKey;
		gitClient = new GitClientImpl(projecKey);
	}

	public GitDecXtract(String projecKey, String uri) {
		this.projecKey = projecKey;
		gitClient = new GitClientImpl(uri, projecKey);
	}

	// TODO: below method signature will further improve
	public List<DecisionKnowledgeElement> getElements(String featureBranchShortName) {
		List<DecisionKnowledgeElement> gatheredElements = new ArrayList<>();
		List<RevCommit> featureCommits = gitClient.getFeatureBranchCommits(featureBranchShortName);
		if (featureCommits == null || featureCommits.size() == 0) {
			return gatheredElements;
		}
		for (RevCommit commit : featureCommits) {
			gatheredElements.addAll(getElementsFromMessage(commit));
		}
		return gatheredElements;
	}

	private List<DecisionKnowledgeElement> getElementsFromMessage(RevCommit commit) {
		GitCommitMessageDecXtract extractorFromMessage = new GitCommitMessageDecXtract(commit.getFullMessage());
		List<DecisionKnowledgeElement> elementsFromMessage = extractorFromMessage.getElements()
				.stream().map(element -> { // need to update project and key attributes
					element.setProject(projecKey);
					element.setKey(updateKeyFroMessageExtractedElement(element.getKey(), commit.getId()));
					return element;
				}).collect(Collectors.toList());
		return elementsFromMessage;
	}

	private String updateKeyFroMessageExtractedElement(String keyWithoutCommitish, ObjectId id) {
		return keyWithoutCommitish.replace(GitCommitMessageDecXtract.COMMIT_PLACEHOLDER,
				String.valueOf(id) + COMMIT_POSITION_SEPARATOR);
	}
}
