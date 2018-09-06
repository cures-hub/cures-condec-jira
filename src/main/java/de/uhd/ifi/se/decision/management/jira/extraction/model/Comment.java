package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;

public class Comment {

	private List<Sentence> sentences;

	private String body = "";

	private long jiraCommentId;

	private String authorFullName;

	private long authorId;

	private Date created;

	private List<Integer> startSubstringCount;

	private List<Integer> endSubstringCount;

	public Comment() {
		this.sentences = new ArrayList<Sentence>();
		this.startSubstringCount = new ArrayList<Integer>();
		this.endSubstringCount = new ArrayList<Integer>();
		this.sentences = new ArrayList<Sentence>();
		this.created = new Date();
		this.authorFullName = "";
		this.jiraCommentId = 0;
		this.authorId = 0;
	}

	public Comment(String comment) {
		this();
		this.body = Comment.textRule(comment);
		splitCommentIntoSentences(true);
	}

	public Comment(com.atlassian.jira.issue.comments.Comment comment) {
		this();
		this.body = Comment.textRule(comment.getBody());
		this.created = comment.getCreated();
		this.authorFullName = comment.getAuthorFullName();
		this.jiraCommentId = comment.getId();
		this.authorId = comment.getAuthorApplicationUser().getId();
		splitCommentIntoSentences(true);
	}

	public static String textRule(String text) {
		return text.replace("<br>", " ").toString();
		// .replaceAll("\\{quote\\}[^<]*\\{quote\\}", "").toString();
	}

	private void splitCommentIntoSentences(boolean addSentencesToAo) {
		List<String> rawSentences = sliceCommentRecursionCommander();
		runBreakIterator(rawSentences);
		ActiveObjectsManager.checkIfCommentBodyHasChangedOutsideOfPlugin(this);
		// Create AO entries
		for (int i = 0; i < this.startSubstringCount.size(); i++) {
			long aoId = ActiveObjectsManager.addElement(this.jiraCommentId, false, this.endSubstringCount.get(i),
					this.startSubstringCount.get(i), this.authorId);
			this.sentences.add(
					new Sentence(this.body.substring(this.startSubstringCount.get(i), this.endSubstringCount.get(i)),
							aoId, jiraCommentId));
		}
	}

	private List<String> sliceCommentRecursionCommander() {
		List<String> firstSplit = searchBetweenTagsRecursive(this.body, "{quote}", "{quote}", new ArrayList<String>());

		firstSplit = searchForFurtherTags(firstSplit, "{noformat}", "{noformat}");
		firstSplit = searchForFurtherTags(firstSplit, "{code:", "{code}");

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

	private void runBreakIterator(List<String> rawSentences) {
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

		for (String currentSentence : rawSentences) {
			if (!currentSentence.contains("{quote}") && !currentSentence.contains("{code}")) {
				iterator.setText(currentSentence);
				int start = iterator.first();
				for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
					if (end - start > 1) {
						int startOfSentence = this.body.indexOf(currentSentence.substring(start, end));
						int endOfSentence = currentSentence.substring(start, end).length() + startOfSentence;
						addSentenceIndex(startOfSentence, endOfSentence);
					}
				}
			} else {
				int start1 = this.body.indexOf(currentSentence);
				int end1 = currentSentence.length() + start1;
				addSentenceIndex(start1, end1);
			}
		}
	}

	private void addSentenceIndex(int startIndex, int endIndex) {
		this.startSubstringCount.add(startIndex);
		this.endSubstringCount.add(endIndex);
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences; 
	}

	public String getTaggedBody(int index) {
		String result = "<span id=\"comment" + index + "\">";
		for (Sentence sentence : this.sentences) {
			if (sentence.isRelevant() && !sentence.getBody().contains("{code}")
					&& !sentence.getBody().contains("{noformat}")) {
				result = result + "<span class=\"sentence " + sentence.getKnowledgeTypeString() + // done
						"\"  id  = ui" + sentence.getActiveObjectId() + ">" + sentence.getOpeningTagSpan()
						+ "<span class = sentenceBody>" + sentence.getBody() + "</span>" + sentence.getClosingTagSpan()
						+ "</span>";
			} else if (!sentence.getBody().contains("{code}") && !sentence.getBody().contains("{noformat}")) {
				result = result + "<span class=\"sentence \"  id  = ui" + sentence.getActiveObjectId() + ">"
						+ sentence.getOpeningTagSpan() + "<span class = sentenceBody>" + sentence.getBody() + "</span>"
						+ sentence.getClosingTagSpan() + "</span>";
			}
		}
		return result + "</span>";
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getJiraCommentId() {
		return jiraCommentId;
	}

	public void setJiraCommentId(long id) {
		this.jiraCommentId = id;
	}

	public String getAuthorFullName() {
		return authorFullName;
	}

	public void setAuthorFullName(String authorApplicationUser) {
		this.authorFullName = authorApplicationUser;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public long getAuthorId() {
		return authorId;
	}

	public void setAuthorId(long authorId) {
		this.authorId = authorId;
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

}
