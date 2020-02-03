package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.Diff;

/**
 * Extract decision knowledge elements stored in git repository. Out-of-scope:
 * linking decision knowledge elements among each other.
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

    public GitDecXtract(String projecKey, List<String> uri) {
	this.projecKey = projecKey;
	gitClient = new GitClientImpl(uri, projecKey);
    }

    /// TODO: can this be done better in JAVA?
    /// Release git client.
    public void close() {
	gitClient.closeAll();
    }

    // TODO: below method signature will further improve
    public List<KnowledgeElement> getElements(String featureBranchShortName) {
	List<KnowledgeElement> allGatheredElements = new ArrayList<>();
	Map<String, List<RevCommit>> allFeatureCommits = new HashMap<String, List<RevCommit>>();
	for (String uri : gitClient.getRemoteUris()) {
	    List<RevCommit> featureCommits = gitClient.getFeatureBranchCommits(featureBranchShortName, uri);
	    if (featureCommits != null && featureCommits.size() > 0) {
		allFeatureCommits.put(uri, featureCommits);
	    }
	}
	if (allFeatureCommits == null || allFeatureCommits.size() == 0) {
	    return allGatheredElements;
	} else {
	    for (String uri : allFeatureCommits.keySet()) {
		for (RevCommit commit : allFeatureCommits.get(uri)) {
		    allGatheredElements.addAll(getElementsFromMessage(commit));
		}
		RevCommit baseCommit = allFeatureCommits.get(uri).get(0);
		RevCommit lastFeatureBranchCommit = allFeatureCommits.get(uri)
			.get(allFeatureCommits.get(uri).size() - 1);
		allGatheredElements
			.addAll(getElementsFromCode(baseCommit, lastFeatureBranchCommit, featureBranchShortName, uri));
	    }

	}
	return allGatheredElements;
    }

    public List<KnowledgeElement> getElementsFromCode(RevCommit revCommitStart, RevCommit revCommitEnd,
	    String featureBranchShortName, String repoUri) {
	List<KnowledgeElement> elementsFromCode = new ArrayList<>();

	// git client which has access to correct version of files (revCommitEnd)
	GitClient endAnchoredGitClient = new GitClientImpl((GitClientImpl) gitClient);
	if (featureBranchShortName != null) {
	    endAnchoredGitClient.checkoutFeatureBranch(featureBranchShortName, repoUri);
	}

	GitClient startAnchoredGitClient = new GitClientImpl((GitClientImpl) gitClient);
	if (featureBranchShortName != null) {
	    startAnchoredGitClient.checkoutCommit(revCommitStart.getParent(0), repoUri);
	}

	Diff diff = gitClient.getDiff(revCommitStart, revCommitEnd, repoUri);
	GitDiffedCodeExtractionManager diffCodeManager = new GitDiffedCodeExtractionManager(diff, endAnchoredGitClient,
		startAnchoredGitClient);
	elementsFromCode = diffCodeManager.getNewDecisionKnowledgeElements();
	elementsFromCode.addAll(diffCodeManager.getOldDecisionKnowledgeElements());

	startAnchoredGitClient.closeAll();
	endAnchoredGitClient.closeAll();

	return elementsFromCode.stream().map(element -> {
	    element.setProject(projecKey);
	    element.setKey(updateKeyForCodeExtractedElementWithInformationHash(element));
	    return element;
	}).collect(Collectors.toList());
    }

    public List<KnowledgeElement> getElementsFromMessage(RevCommit commit) {
	GitCommitMessageExtractor extractorFromMessage = new GitCommitMessageExtractor(commit.getFullMessage());
	List<KnowledgeElement> elementsFromMessage = extractorFromMessage.getElements().stream().map(element -> { // need
														  // to
														  // update
														  // project
														  // and
														  // key
														  // attributes
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
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(rationaleText.getBytes());
	    byte[] digest = md.digest();
	    return DatatypeConverter.printHexBinary(digest).toUpperCase().substring(0, 8);
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	    return "";
	}
    }

    public List<KnowledgeElement> getElements(Ref branch) {
	if (branch == null) {
	    return getElements((String) null);
	}

	return getElements(generateBranchShortName(branch));
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
