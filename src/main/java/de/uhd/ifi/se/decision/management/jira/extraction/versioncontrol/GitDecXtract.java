package de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.parser.RationaleFromCodeCommentParser;
import de.uhd.ifi.se.decision.management.jira.extraction.parser.RationaleFromCommitMessageParser;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.git.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.model.git.CodeComment;
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
		gitClient = GitClient.getInstance(projectKey);
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

	private Comparator<KnowledgeElement> comparatorForKnowledgeElementsByLocationInCode = new Comparator<KnowledgeElement>() {
		public int compare(KnowledgeElement e1, KnowledgeElement e2) {
			/*
			 * The description of a knowledge element from code is structured as
			 * 
			 * "nameOfCodeFile someInteger changeType(some numbers, probably related to the
			 * git commits) startLine:endLine:inCommentCursor gitCommitHash",
			 * 
			 * e.g.: "GodClass.java 0 INSERT(0-0,0-24) 1:1:5 70297039"
			 * 
			 * We want to extract the startLine:endLine:inCommentCursor part and use it to
			 * sort the elements by their position in the code
			 * 
			 */
			try {
				// extract string containing start line, end line and "inCommentCursor"
				// (whatever that is)
				String d1 = e1.getDescription().substring(0, e1.getDescription().lastIndexOf(' '));
				String d2 = e2.getDescription().substring(0, e2.getDescription().lastIndexOf(' '));
				d1 = d1.substring(d1.lastIndexOf(' ') + 1);
				d2 = d2.substring(d2.lastIndexOf(' ') + 1);

				// get a list of integers from that string
				List<String> sl1 = Arrays.asList(d1.split(":"));
				List<String> sl2 = Arrays.asList(d2.split(":"));

				// now sort elements by the numbers in both lists
				for (int i = 0; i < sl1.size(); i++) {
					if (Integer.valueOf(sl1.get(i)) < Integer.valueOf(sl2.get(i))) {
						return -1;
					}
					if (Integer.valueOf(sl1.get(i)) > Integer.valueOf(sl2.get(i))) {
						return 1;
					}
				}
				return 0;
			} catch (Exception e) {
				// something went wrong – perhaps the description structure of a knowledge
				// element was not as expected
				return 0;
			}
		}
	};

	public List<KnowledgeElement> getElementsFromCode(Diff diff) {
		List<KnowledgeElement> elementsFromCode = new ArrayList<>();
		for (ChangedFile codeFile : diff.getChangedFiles()) {
			elementsFromCode.addAll(getElementsFromCode(codeFile));
		}
		return elementsFromCode;
	}

	public List<KnowledgeElement> getElementsFromCode(ChangedFile codeFile) {
		List<KnowledgeElement> elementsFromCode = new ArrayList<>();
		for (CodeComment codeComment : codeFile.getCodeComments()) {
			RationaleFromCodeCommentParser rationaleFromCodeComment = new RationaleFromCodeCommentParser();
			elementsFromCode.addAll(rationaleFromCodeComment.getElements(codeComment));
		}

		List<KnowledgeElement> knowledgeElements = elementsFromCode.stream().map(element -> {
			element.setProject(projecKey);
			element.setDescription(updateKeyForCodeExtractedElementWithInformationHash(element));
			element.setDescription(codeFile.getName() + element.getDescription());
			element.setDocumentationLocation(DocumentationLocation.CODE);
			return element;
		}).collect(Collectors.toList());
		knowledgeElements.sort(comparatorForKnowledgeElementsByLocationInCode);
		return knowledgeElements;
	}

	public List<KnowledgeElement> getElementsFromMessage(RevCommit commit) {
		RationaleFromCommitMessageParser extractorFromMessage = new RationaleFromCommitMessageParser(commit.getFullMessage());
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
		return key.replace(RationaleFromCommitMessageParser.COMMIT_PLACEHOLDER,
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
