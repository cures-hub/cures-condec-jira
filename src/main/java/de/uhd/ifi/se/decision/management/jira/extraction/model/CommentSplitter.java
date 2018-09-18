package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class CommentSplitter {

	private List<Integer> startSubstringCount;

	private List<Integer> endSubstringCount;
	
	public static String[] excludedTagList = new String[] { "{code}", "{quote}", "{noformat}","[issue]" };

	public CommentSplitter() {
		this.setStartSubstringCount(new ArrayList<Integer>());
		this.setEndSubstringCount(new ArrayList<Integer>());
	}

	public List<String> sliceCommentRecursionCommander(String body) {
		List<String> firstSplit = searchBetweenTagsRecursive(body, "{quote}", "{quote}", new ArrayList<String>());

		firstSplit = searchForFurtherTags(firstSplit, "{noformat}", "{noformat}");
		firstSplit = searchForFurtherTags(firstSplit, "{code:", "{code}");
		firstSplit = searchForFurtherTags(firstSplit, "[issue]", "[/issue]");
		

		return firstSplit;
	}

	private List<String> searchForFurtherTags(List<String> firstSplit, String openTag, String closeTag) {
		HashMap<Integer, ArrayList<String>> newSlices = new HashMap<Integer, ArrayList<String>>();
		for (String slice : firstSplit) {
			ArrayList<String> slicesOfSentence = searchBetweenTagsRecursive(slice, openTag, closeTag,
					new ArrayList<String>());
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
		if (toSearch.startsWith(openTag)) {
			String part = StringUtils.substringBetween(toSearch, openTag, closeTag);
			part = openTag + part + closeTag;
			slices.add(part);
			toSearch = toSearch.substring(toSearch.indexOf(openTag) + part.length());
			slices = searchBetweenTagsRecursive(toSearch, openTag, closeTag, slices);
		} else {// Comment block has now plain text
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

}
