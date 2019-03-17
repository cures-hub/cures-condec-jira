package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.extraction.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.macros.AbstractKnowledgeClassificationMacro;

public class CommentSplitterImpl implements CommentSplitter {

	private List<Integer> startSubstringCount;
	private List<Integer> endSubstringCount;

	public CommentSplitterImpl() {
		this.startSubstringCount = new ArrayList<Integer>();
		this.endSubstringCount = new ArrayList<Integer>();
	}

	@Override
	public List<Sentence> getSentences(Comment comment) {
		List<Sentence> sentences = new ArrayList<Sentence>();
		String projectKey = comment.getIssue().getProjectObject().getKey();

		List<String> strings = CommentSplitterImpl.getRawSentences(comment.getBody(), projectKey);
		runBreakIterator(strings, comment.getBody());

		// Create AO entries
		for (int i = 0; i < this.startSubstringCount.size(); i++) {
			int startIndex = this.startSubstringCount.get(i);
			int endIndex = this.endSubstringCount.get(i);
			if (!startAndEndIndexRules(startIndex, endIndex, comment.getBody())) {
				continue;
			}
			Sentence sentence = new SentenceImpl();
			sentence.setCommentId(comment.getId());
			sentence.setEndSubstringCount(endIndex);
			sentence.setStartSubstringCount(startIndex);
			sentence.setJiraIssueId(comment.getIssue().getId());
			sentence.setProject(projectKey);
			String body = comment.getBody().substring(startIndex, endIndex).toLowerCase();
			KnowledgeType type = getKnowledgeTypeFromTag(body, projectKey);
			sentence.setType(type);
			if (type != KnowledgeType.OTHER) {
				sentence.setRelevant(true);
				sentence.setValidated(true);
			}
			long sentenceId = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(sentence, null);
			sentence = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(sentenceId);
			JiraIssueCommentPersistenceManager.createSmartLinkForSentence(sentence);
			sentence.setCreated(comment.getCreated());
			sentences.add(sentence);
		}
		return sentences;
	}

	private static List<String> getRawSentences(String body, String projectKey) {
		List<String> firstSplit = searchForTagsRecursively(body, "{quote}", "{quote}", new ArrayList<String>());

		firstSplit = searchForTags(firstSplit, "{noformat}", "{noformat}");
		firstSplit = searchForTags(firstSplit, "{panel:", "{panel}");
		firstSplit = searchForTags(firstSplit, "{code:", "{code}");
		for (String tag : RATIONALE_TAGS) {
			firstSplit = searchForTags(firstSplit, tag, tag);
		}
		if (ConfigPersistenceManager.isIconParsing(projectKey)) {
			for (String icon : RATIONALE_ICONS) {
				firstSplit = searchForTags(firstSplit, icon, System.getProperty("line.separator"));
			}
		}
		return firstSplit;
	}

	private static ArrayList<String> searchForTagsRecursively(String commentPart, String openTag, String closeTag,
			ArrayList<String> slices) {
		if (isIncorrectlyTagged(commentPart, openTag, closeTag)) {
			slices.add(commentPart);
			return slices;
		}
		// Icon is used to identify a sentence or a closing tag is forgotten
		if (commentPart.contains(openTag) && !commentPart.contains(closeTag)) {
			return slices;
		} // Open and close tags are existent
		if (commentPart.startsWith(openTag) && commentPart.contains(closeTag)) {
			String part = StringUtils.substringBetween(commentPart, openTag, closeTag);
			part = openTag + part + closeTag;
			slices.add(part);
			String commentPartSubstring = commentPart.substring(commentPart.indexOf(openTag) + part.length());
			return searchForTagsRecursively(commentPartSubstring, openTag, closeTag, slices);
		} else {// currently plain text
			if (commentPart.contains(openTag)) {// comment block has special text later
				slices.add(commentPart.substring(0, commentPart.indexOf(openTag)));
				return searchForTagsRecursively(commentPart.substring(commentPart.indexOf(openTag)), openTag, closeTag,
						slices);
			} else {// comment block has no more special text
				slices.add(commentPart);
			}
		}
		return slices;
	}

	private static List<String> searchForTags(List<String> firstSplit, String openTag, String closeTag) {
		HashMap<Integer, ArrayList<String>> newSlices = new HashMap<Integer, ArrayList<String>>();
		for (String slice : firstSplit) {
			ArrayList<String> slicesOfSentence = searchForTagsRecursively(slice.toLowerCase(), openTag.toLowerCase(),
					closeTag.toLowerCase(), new ArrayList<String>());
			if (slicesOfSentence.size() > 1) {
				newSlices.put(firstSplit.indexOf(slice), slicesOfSentence);
			}
		}
		for (int i = newSlices.keySet().toArray().length - 1; i >= 0; i--) {
			int remove = (int) newSlices.keySet().toArray()[i];
			firstSplit.remove(remove);
			firstSplit.addAll(remove, newSlices.get(remove));
		}

		return firstSplit;
	}

	/**
	 * Checks: Start Index >=0, End Index >= 0, End Index - Start Index > 0, Body
	 * not only whitespaces
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	private boolean startAndEndIndexRules(int startIndex, int endIndex, String body) {
		return (startIndex >= 0 && endIndex >= 0 && (endIndex - startIndex) > 0
				&& body.substring(startIndex, endIndex).replaceAll("\r\n", "").trim().length() > 1);
	}

	private void runBreakIterator(List<String> rawSentences, String body) {
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

		for (String currentSentence : rawSentences) {
			if (StringUtils.indexOfAny(currentSentence, CommentSplitter.EXCLUDED_STRINGS) == -1) {
				iterator.setText(currentSentence);
				int start = iterator.first();
				for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
					if (end - start > 1 && currentSentence.substring(start, end).trim().length() > 0) {
						int startOfSentence = body.toLowerCase()
								.indexOf(currentSentence.toLowerCase().substring(start, end));
						int endOfSentence = currentSentence.substring(start, end).length() + startOfSentence;
						this.addSentenceIndex(startOfSentence, endOfSentence);
					}
				}
			} else {
				int start1 = body.toLowerCase().indexOf(currentSentence.toLowerCase());
				int end1 = currentSentence.length() + start1;
				this.addSentenceIndex(start1, end1);
			}
		}
	}

	private static boolean isIncorrectlyTagged(String toSearch, String openTag, String closeTag) {
		return openTag.equals(closeTag) && !knowledgeTypeTagExistsTwice(toSearch, openTag);
	}

	private static boolean knowledgeTypeTagExistsTwice(String body, String knowledgeType) {
		return StringUtils.countMatches(body.toLowerCase(), knowledgeType.toLowerCase()) >= 2;
	}

	public void addSentenceIndex(int startIndex, int endIndex) {
		this.startSubstringCount.add(startIndex);
		this.endSubstringCount.add(endIndex);
	}

	/**
	 * 
	 * @param body
	 * @param projectKey
	 * @param lookOutForIcons
	 *            search also for icons
	 * @return tagged knowledge type of a given string
	 */
	public static KnowledgeType getKnowledgeTypeFromTag(String body, String projectKey) {
		boolean checkIcons = ConfigPersistenceManager.isIconParsing(projectKey);
		for (KnowledgeType type : KNOWLEDGE_TYPES) {
			if (body.toLowerCase().contains(AbstractKnowledgeClassificationMacro.getTag(type))
					|| (checkIcons && body.contains(type.getIconString()))) {
				return type;
			}
		}
		return KnowledgeType.OTHER;
	}

	public static boolean isAnyKnowledgeTypeTwiceExisting(String body, String projectKey) {
		Set<String> knowledgeTypeTags = getAllTagsUsedInProject(projectKey);
		for (String tag : knowledgeTypeTags) {
			if (knowledgeTypeTagExistsTwice(body, tag)) {
				return true;
			}
		}
		return false;
	}

	public static Set<String> getAllTagsUsedInProject(String projectKey) {
		Set<KnowledgeType> projectKnowledgeTypes = new DecisionKnowledgeProjectImpl(projectKey).getKnowledgeTypes();
		projectKnowledgeTypes.add(KnowledgeType.PRO);
		projectKnowledgeTypes.add(KnowledgeType.CON);
		Set<String> knowledgeTypeTags = new HashSet<String>();
		for (KnowledgeType type : projectKnowledgeTypes) {
			knowledgeTypeTags.add(AbstractKnowledgeClassificationMacro.getTag(type));
		}
		return knowledgeTypeTags;
	}

	public static boolean isCommentIconTagged(String text) {
		// TODO WHY >=
		return StringUtils.indexOfAny(text, CommentSplitter.RATIONALE_ICONS) >= 0;
	}
}
