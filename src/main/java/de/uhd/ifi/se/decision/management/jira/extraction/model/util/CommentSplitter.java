package de.uhd.ifi.se.decision.management.jira.extraction.model.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.gzipfilter.org.apache.commons.lang.ArrayUtils;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProjectImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;

public class CommentSplitter {

	private List<Integer> startSubstringCount;

	private List<Integer> endSubstringCount;

	public static final String[] excludedTagList = new String[] { "{code}", "{quote}", "{noformat}", "{panel}" };

	/**List with all knowledgeTypes as tags. Sequence matters! */
	public static final String[] manualRationaleTagList = new String[] { "{issue}", "{alternative}", "{decision}",
			"{pro}", "{con}" };
	
	/**List with all knowledgeTypes as icons. Sequence matters! */
	public static final String[] manualRationalIconList = new String[] { "(!)", "(?)", "(/)", "(y)", "(n)" };

	public static final String[] allExcluded = (String[]) ArrayUtils
			.addAll(ArrayUtils.addAll(excludedTagList, manualRationaleTagList), manualRationalIconList);

	public CommentSplitter() {
		this.setStartSubstringCount(new ArrayList<Integer>());
		this.setEndSubstringCount(new ArrayList<Integer>());
	}

	public List<String> splitSentence(String body) {
		return sliceCommentRecursionCommander(body, "");
	}

	public List<String> sliceCommentRecursionCommander(String body, String projectKey) {
		List<String> firstSplit = searchBetweenTagsRecursive(body, "{quote}", "{quote}", new ArrayList<String>());

		firstSplit = searchForFurtherTags(firstSplit, "{noformat}", "{noformat}");
		firstSplit = searchForFurtherTags(firstSplit, "{panel:", "{panel}");
		firstSplit = searchForFurtherTags(firstSplit, "{code:", "{code}");
		for (int i = 0; i < manualRationaleTagList.length; i++) {
			String tag = manualRationaleTagList[i];
			firstSplit = searchForFurtherTags(firstSplit, tag, tag);
		}
		if (ConfigPersistence.isIconParsing(projectKey)) {
			for (int i = 0; i < manualRationalIconList.length; i++) {
				firstSplit = searchForFurtherTags(firstSplit, manualRationalIconList[i],
						System.getProperty("line.separator"));
			}
		}
		return firstSplit;
	}

	private List<String> searchForFurtherTags(List<String> firstSplit, String openTag, String closeTag) {
		HashMap<Integer, ArrayList<String>> newSlices = new HashMap<Integer, ArrayList<String>>();
		for (String slice : firstSplit) {
			ArrayList<String> slicesOfSentence = searchBetweenTagsRecursive(slice.toLowerCase(), openTag.toLowerCase(),
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

	private ArrayList<String> searchBetweenTagsRecursive(String toSearch, String openTag, String closeTag,
			ArrayList<String> slices) {
		if(checkIncorrectTagMix(toSearch,openTag,closeTag)) {
			slices.add(toSearch);
			return slices;
		}
		// Icon is used to identify a sentence or a closing tag is forgotten
		if (toSearch.contains(openTag) && !toSearch.contains(closeTag) ) {
			return slices;
		} // Open and close tags are existent
		if (toSearch.startsWith(openTag) && toSearch.contains(closeTag)) {
			String part = StringUtils.substringBetween(toSearch, openTag, closeTag);
			part = openTag + part + closeTag;
			slices.add(part);
			toSearch = toSearch.substring(toSearch.indexOf(openTag) + part.length());
			slices = searchBetweenTagsRecursive(toSearch, openTag, closeTag, slices);
		} else {// currently plain text
			if (toSearch.contains(openTag)) {// comment block has special text later
				slices.add(toSearch.substring(0, toSearch.indexOf(openTag)));
				slices = searchBetweenTagsRecursive(toSearch.substring(toSearch.indexOf(openTag)), openTag, closeTag,
						slices);
			} else {// comment block has no more special text
				slices.add(toSearch);
			}
		}
		return slices;
	}

	private boolean checkIncorrectTagMix(String toSearch, String openTag, String closeTag) {
		if(openTag.equals(closeTag) && !isKnowledgeTypeTagTwiceExistant(toSearch,openTag)) {
			return true;
		}
		//all fine
		return false;
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
	public static String getKnowledgeTypeFromManuallIssueTag(String body, String projectKey, boolean lookOutForIcons) {
		boolean checkIcons = lookOutForIcons && ConfigPersistence.isIconParsing(projectKey);
		if (body.toLowerCase().contains(manualRationaleTagList[0]) || (checkIcons && body.contains(manualRationalIconList[0]))) {
			return KnowledgeType.ISSUE.toString();
		}
		if (body.toLowerCase().contains(manualRationaleTagList[1]) || (checkIcons && body.contains(manualRationalIconList[1]))) {
			return KnowledgeType.ALTERNATIVE.toString();
		}
		if (body.toLowerCase().contains(manualRationaleTagList[2]) || (checkIcons && body.contains(manualRationalIconList[2]))) {
			return KnowledgeType.DECISION.toString();
		}
		if (body.toLowerCase().contains(manualRationaleTagList[3]) || (checkIcons && body.contains(manualRationalIconList[3]))) {
			return "pro";
		}
		if (body.toLowerCase().contains(manualRationaleTagList[4]) || (checkIcons && body.contains(manualRationalIconList[4]))) {
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
			if (isKnowledgeTypeTagTwiceExistant(body, tag)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isKnowledgeTypeTagTwiceExistant(String body, String knowledgeType) {
		return StringUtils.countMatches(body.toLowerCase(), knowledgeType.toLowerCase()) >= 2;
	}

	public static String[] getAllTagsUsedInProject(String projectKey) {
		Set<KnowledgeType> projectKnowledgeTypes = new DecisionKnowledgeProjectImpl(projectKey).getKnowledgeTypes();
		ArrayList<String> projectList = new ArrayList<String>();
		for (int i = 0; i < projectKnowledgeTypes.size(); i++) {
			projectList.add("[" + projectKnowledgeTypes.toArray()[i].toString().toLowerCase() + "]");
		}
		for (int i = 0; i < manualRationaleTagList.length; i++) {
			projectList.add(manualRationaleTagList[i].toLowerCase());
		}
		return projectList.toArray(new String[0]);
	}

	public static boolean isCommentIconTagged(String text) {
		return StringUtils.indexOfAny(text, CommentSplitter.manualRationalIconList) > 0;
	}
}
