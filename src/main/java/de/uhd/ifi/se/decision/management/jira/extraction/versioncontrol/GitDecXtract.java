package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

/**
 * purpose: extract decision knowledge elements stored in git repository
 * out-of-scope linking decision knowledge elements among each other
 */
public class GitDecXtract {

	public static final String RAT_KEY_COMPONENTS_SEPARATOR = " ";
	public static final String RAT_KEY_NOEDIT = "-";
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
		} else {
			for (RevCommit commit : featureCommits) {
				gatheredElements.addAll(getElementsFromMessage(commit));
			}
			RevCommit baseCommit = featureCommits.get(0);
			RevCommit lastFeatureBranchCommit = featureCommits.get(featureCommits.size() - 1);
			gatheredElements.addAll(getElementsFromCode(baseCommit, lastFeatureBranchCommit, featureBranchShortName));
		}
		return gatheredElements;
	}

	private List<DecisionKnowledgeElement> getElementsFromCode(RevCommit revCommitStart, RevCommit revCommitEnd,
			String featureBranchShortName) {
		List<DecisionKnowledgeElement> elementsFromCode = new ArrayList<>();

		// git client which has access to correct version of files (revCommitEnd)
		GitClient endAnchoredGitClient = new GitClientImpl((GitClientImpl) gitClient);
		if (featureBranchShortName != null) {
			endAnchoredGitClient.checkoutFeatureBranch(featureBranchShortName);
		}

		GitClient startAnchoredGitClient = new GitClientImpl((GitClientImpl) gitClient);
		if (featureBranchShortName != null) {
			startAnchoredGitClient.checkoutCommit(revCommitStart.getParent(0));
		}

		Diff diff = gitClient.getDiff(revCommitStart, revCommitEnd);
		GitDiffedCodeExtractionManager diffCodeManager = new GitDiffedCodeExtractionManager(diff, endAnchoredGitClient,
				startAnchoredGitClient);
		elementsFromCode = diffCodeManager.getNewDecisionKnowledgeElements();
		elementsFromCode.addAll(diffCodeManager.getOldDecisionKnowledgeElements());

		return elementsFromCode.stream().map(element -> {
			element.setProject(projecKey);
			element.setKey(updateKeyForCommentExtractedElement(element));
			return element;
		}).collect(Collectors.toList());
	}

	private List<DecisionKnowledgeElement> getElementsFromMessage(RevCommit commit) {
		GitCommitMessageExtractor extractorFromMessage = new GitCommitMessageExtractor(commit.getFullMessage());
		List<DecisionKnowledgeElement> elementsFromMessage = extractorFromMessage.getElements().stream()
				.map(element -> { // need to update project and key attributes
					element.setProject(projecKey);
					element.setKey(updateKeyForMessageExtractedElement(element, commit.getId()));
					return element;
				}).collect(Collectors.toList());
		return elementsFromMessage;
	}

	/*
	 * Appends rationale text hash to the DecisionKnowledgeElement key.
	 */
	private String updateKeyForCommentExtractedElement(DecisionKnowledgeElement elementWithoutTextHash) {
		String key = elementWithoutTextHash.getKey();
		String rationaleText = elementWithoutTextHash.getSummary() + elementWithoutTextHash.getDescription();

		key += RAT_KEY_COMPONENTS_SEPARATOR + calculateRationaleTextHash(rationaleText);

		return key;
	}

	/*
	 * Appends rationale text hash to the DecisionKnowledgeElement key. Replaces
	 * commit hash placeholder in the key with the actual commit hash.
	 */
	private String updateKeyForMessageExtractedElement(DecisionKnowledgeElement elementWithoutCommitishAndHash,
			ObjectId id) {
		String key = elementWithoutCommitishAndHash.getKey();

		// 1st: append rationale text hash
		String rationaleText = elementWithoutCommitishAndHash.getSummary()
				+ elementWithoutCommitishAndHash.getDescription();
		key += RAT_KEY_COMPONENTS_SEPARATOR + calculateRationaleTextHash(rationaleText);

		// 2nd: replace placeholder with commit's hash (40 hex chars)
		return key.replace(GitCommitMessageExtractor.COMMIT_PLACEHOLDER,
				String.valueOf(id).split(" ")[1] + RAT_KEY_COMPONENTS_SEPARATOR);
	}

	private String calculateRationaleTextHash(String rationaleText) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(rationaleText.getBytes());
			byte[] digest = md.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase().substring(0, 8);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
	}

	public List<DecisionKnowledgeElement> getElements(Ref branch) {
		if (branch == null) {
			return getElements((String) null);
		}
		String[] branchNameComponents = branch.getName().split("/");
		String shortName = branchNameComponents[branchNameComponents.length - 1];
		return getElements(shortName);
	}
}
