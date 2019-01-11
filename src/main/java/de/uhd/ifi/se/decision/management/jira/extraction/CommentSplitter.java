package de.uhd.ifi.se.decision.management.jira.extraction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class CommentSplitter {

	private List<Integer> startSubstringCount;

	private List<Integer> endSubstringCount;

	public static final String[] EXCLUDED_TAGS = new String[] { "{code}", "{quote}", "{noformat}", "{panel}" };

	/** List of all knowledge types as tags. Sequence matters! */
	public static final String[] RATIONALE_TAGS = new String[] { "{issue}", "{alternative}", "{decision}", "{pro}",
			"{con}" };

	/** List of all knowledge types as icons. Sequence matters! */
	public static final String[] RATIONALE_ICONS = new String[] { "(!)", "(?)", "(/)", "(y)", "(n)" };

	public static final String[] EXCLUDED_STRINGS = (String[]) ArrayUtils
			.addAll(ArrayUtils.addAll(EXCLUDED_TAGS, RATIONALE_TAGS), RATIONALE_ICONS);

	public CommentSplitter() {
		this.setStartSubstringCount(new ArrayList<Integer>());
		this.setEndSubstringCount(new ArrayList<Integer>());
	}

	public List<String> splitSentence(String body) {
		return sliceCommentRecursionCommander(body, "");
	}

	public static List<String> sliceCommentRecursionCommander(String body, String projectKey) {
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
			commentPart = commentPart.substring(commentPart.indexOf(openTag) + part.length());
			slices = searchForTagsRecursively(commentPart, openTag, closeTag, slices);
		} else {// currently plain text
			if (commentPart.contains(openTag)) {// comment block has special text later
				slices.add(commentPart.substring(0, commentPart.indexOf(openTag)));
				slices = searchForTagsRecursively(commentPart.substring(commentPart.indexOf(openTag)), openTag,
						closeTag, slices);
			} else {// comment block has no more special text
				slices.add(commentPart);
			}
		}
		return slices;
	}

	private static boolean isIncorrectlyTagged(String toSearch, String openTag, String closeTag) {
		return openTag.equals(closeTag) && !knowledgeTypeTagExistsTwice(toSearch, openTag);
	}

	private static boolean knowledgeTypeTagExistsTwice(String body, String knowledgeType) {
		return StringUtils.countMatches(body.toLowerCase(), knowledgeType.toLowerCase()) >= 2;
	}

	public List<Integer> getStartSubstringCount() {
		return startSubstringCount;
	}

	public void setStartSubstringCount(List<Integer> startSubstringCount) {
		this.startSubstringCount = startSubstringCount;
	}

	public List<Integer> getEndSubstringCount() {
		return endSubstringCount;
	}

	public void setEndSubstringCount(List<Integer> endSubstringCount) {
		this.endSubstringCount = endSubstringCount;
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
	 * @return The manual tagged knowledge type of a given string
	 */
	public static String getKnowledgeTypeFromManualIssueTag(String body, String projectKey, boolean lookOutForIcons) {
		boolean checkIcons = lookOutForIcons && ConfigPersistenceManager.isIconParsing(projectKey);
		if (body.toLowerCase().contains(RATIONALE_TAGS[0]) || (checkIcons && body.contains(RATIONALE_ICONS[0]))) {
			return KnowledgeType.ISSUE.toString();
		}
		if (body.toLowerCase().contains(RATIONALE_TAGS[1]) || (checkIcons && body.contains(RATIONALE_ICONS[1]))) {
			return KnowledgeType.ALTERNATIVE.toString();
		}
		if (body.toLowerCase().contains(RATIONALE_TAGS[2]) || (checkIcons && body.contains(RATIONALE_ICONS[2]))) {
			return KnowledgeType.DECISION.toString();
		}
		if (body.toLowerCase().contains(RATIONALE_TAGS[3]) || (checkIcons && body.contains(RATIONALE_ICONS[3]))) {
			return "pro";
		}
		if (body.toLowerCase().contains(RATIONALE_TAGS[4]) || (checkIcons && body.contains(RATIONALE_ICONS[4]))) {
			return "con";
		}
		return matchSelectableKnowledgeTypes(body, projectKey);
	}

	private static String matchSelectableKnowledgeTypes(String body, String projectKey) {
		DecisionKnowledgeProject dkp = new DecisionKnowledgeProjectImpl(projectKey);
		for (KnowledgeType type : dkp.getKnowledgeTypes()) {
			if (body.toLowerCase().contains("[" + type.toString().toLowerCase() + "]")) {
				return type.toString();
			}
		}
		return KnowledgeType.OTHER.toString();
	}

	public static boolean containsOpenAndCloseTags(String body, String projectKey) {
		for (int i = 0; i < getAllTagsUsedInProject(projectKey).length; i++) {
			String tag = getAllTagsUsedInProject(projectKey)[i].toLowerCase();
			if (body.toLowerCase().contains(tag) && body.toLowerCase().contains(tag.replace("[", "[/"))) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAnyKnowledgeTypeTwiceExisintg(String body, String projectKey) {
		for (int i = 0; i < getAllTagsUsedInProject(projectKey).length; i++) {
			String tag = getAllTagsUsedInProject(projectKey)[i].toLowerCase().replace("[", "{").replace("]", "}");
			if (knowledgeTypeTagExistsTwice(body, tag)) {
				return true;
			}
		}
		return false;
	}

	public static String[] getAllTagsUsedInProject(String projectKey) {
		Set<KnowledgeType> projectKnowledgeTypes = new DecisionKnowledgeProjectImpl(projectKey).getKnowledgeTypes();
		ArrayList<String> projectList = new ArrayList<String>();
		for (int i = 0; i < projectKnowledgeTypes.size(); i++) {
			projectList.add("[" + projectKnowledgeTypes.toArray()[i].toString().toLowerCase() + "]");
		}
		for (int i = 0; i < RATIONALE_TAGS.length; i++) {
			projectList.add(RATIONALE_TAGS[i].toLowerCase());
		}
		return projectList.toArray(new String[0]);
	}

	public static boolean isCommentIconTagged(String text) {
		return StringUtils.indexOfAny(text, CommentSplitter.RATIONALE_ICONS) > 0;
	}
}
