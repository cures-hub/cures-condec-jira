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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

/**
 * Extract decision knowledge elements stored in git repository. Out-of-scope:
 * linking decision knowledge elements among each other.
 */
public class GitDecXtract {
	private static final Logger LOGGER = LoggerFactory.getLogger(GitDecXtract.class);

	public static final String RAT_KEY_COMPONENTS_SEPARATOR = " ";
	public static final String RAT_KEY_NOEDIT = "-";
	private final GitClient gitClient;
	private final String projecKey;

	public GitDecXtract(String projectKey) {
		this.projecKey = projectKey;
		gitClient = GitClient.getOrCreate(projectKey);
	}

	// TODO: below method signature will further improve
	public List<KnowledgeElement> getElements(Ref branch) {
		List<KnowledgeElement> elements = new ArrayList<>();
		List<RevCommit> featureBranchCommits = gitClient.getFeatureBranchCommits(branch);
		if (featureBranchCommits == null || featureBranchCommits.isEmpty()) {
			return elements;
		}
		for (RevCommit commit : featureBranchCommits) {
			elements.addAll(getElementsFromMessage(commit));
		}

		RevCommit baseCommit = featureBranchCommits.get(0);
		RevCommit lastFeatureBranchCommit = featureBranchCommits.get(featureBranchCommits.size() - 1);
		elements.addAll(getElementsFromCode(baseCommit, lastFeatureBranchCommit));
		return elements;
	}

	public List<KnowledgeElement> getElementsFromCode(RevCommit revCommitStart, RevCommit revCommitEnd) {
		Diff diff = gitClient.getDiff(revCommitStart, revCommitEnd);
		return getElementsFromCode(diff);
	}

	public List<KnowledgeElement> getElementsFromCode(Diff diff) {
		List<KnowledgeElement> elementsFromCode = new ArrayList<>();
		GitDiffedCodeExtractionManager diffCodeManager = new GitDiffedCodeExtractionManager(diff);
		elementsFromCode = diffCodeManager.getNewDecisionKnowledgeElements();
		elementsFromCode.addAll(diffCodeManager.getOldDecisionKnowledgeElements());

		return elementsFromCode.stream().map(element -> {
			element.setProject(projecKey);
			element.setDescription(updateKeyForCodeExtractedElementWithInformationHash(element));
			element.setDocumentationLocation(DocumentationLocation.CODE);
			return element;
		}).collect(Collectors.toList());
	}

	public List<KnowledgeElement> getElementsFromMessage(RevCommit commit) {
		GitCommitMessageExtractor extractorFromMessage = new GitCommitMessageExtractor(commit.getFullMessage());
		List<KnowledgeElement> elementsFromMessage = extractorFromMessage.getElements().stream().map(element -> {
			element.setProject(projecKey);
			element.setKey(updateKeyForMessageExtractedElement(element, commit.getId()));
			return element;
		}).collect(Collectors.toList());
		return elementsFromMessage;
	}

	/*
	 * Appends rationale text hash to the DecisionKnowledgeElement key.
	 */
	private String updateKeyForCodeExtractedElementWithInformationHash(KnowledgeElement elementWithoutTextHash) {
		String key = elementWithoutTextHash.getKey();
		String rationaleText = elementWithoutTextHash.getSummary() + elementWithoutTextHash.getDescription();

		key += RAT_KEY_COMPONENTS_SEPARATOR + calculateRationaleTextHash(rationaleText);

		return key;
	}

	/*
	 * Appends rationale text hash to the DecisionKnowledgeElement key. Replaces
	 * commit hash placeholder in the key with the actual commit hash.
	 */
	private String updateKeyForMessageExtractedElement(KnowledgeElement elementWithoutCommitishAndHash, ObjectId id) {
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
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(rationaleText.getBytes());
			byte[] digest = messageDigest.digest();
			return DatatypeConverter.printHexBinary(digest).toUpperCase().substring(0, 8);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.error(e.getMessage());
			return "";
		}
	}

	public static String generateBranchShortName(Ref branch) {
		String[] branchNameComponents = branch.getName().split("/");
		return branchNameComponents[branchNameComponents.length - 1];
	}

	public static String generateRegexToFindAllTags(String tag) {
		return generateRegexForOpenTag(tag) + "|" + generateRegexForCloseTag(tag);
	}

	public static String generateRegexForOpenTag(String tag) {
		return "(?i)(\\[(" + tag + ")\\])";
	}

	public static String generateRegexForCloseTag(String tag) {
		return "(?i)(\\[\\/(" + tag + ")\\])";
	}
}
