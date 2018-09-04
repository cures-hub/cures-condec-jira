package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
		//Splits comment into text and quotes
		List<String> rawSentences = setupCommentSplit();
		//Splits text into sentences
		runBreakIterator(rawSentences);
		//Check if sentence lengths have changed
		ActiveObjectsManager.checkIfCommentBodyHasChangedOutsideOfPlugin(this);
		//Create AO entries 
		for(int i = 0; i < this.startSubstringCount.size(); i++) {
			long aoId = ActiveObjectsManager.addElement(this.jiraCommentId, false, this.endSubstringCount.get(i), this.startSubstringCount.get(i), this.authorId);
			this.sentences.add(new Sentence(this.body.substring(this.startSubstringCount.get(i), this.endSubstringCount.get(i)), aoId, jiraCommentId));
		}
	}

	private List<String> setupCommentSplit() {
		String quoteString = "{quote}";
		String codeString = "{code:";
		List<String> slices = new ArrayList<String>();
		if(this.body.contains(quoteString)) { 
			slices = sliceQuotesAndCodeOutOfCommentText(quoteString,slices);
		}if(this.body.contains(codeString)){
			slices = sliceQuotesAndCodeOutOfCommentText(codeString,slices);
		}else if(!this.body.contains(quoteString)){
			slices.add(this.body);
		}
		return slices;
	}
	
	private List<String> sliceQuotesAndCodeOutOfCommentText(String quoteString, List<String> slices) {
		List<Integer> indexes = new ArrayList<Integer>();
		int i = this.body.indexOf(quoteString);
		while (i >= 0) {
			indexes.add(i);
			if(quoteString.equals("{code:")) {quoteString = "{code}";}
			else if(quoteString.contains("{code}")) {quoteString = "{code:";}
			i = this.body.indexOf(quoteString, i + 1);
		}
		for (int j = 0; j <= indexes.size(); j = j + 2) {
			if (indexes.get(0) > 0 && j == 0) {
				slices.add(this.body.substring(0, indexes.get(j)));
			}
			if (j < indexes.size() - 1) {
				slices.add(this.body.substring(indexes.get(j), indexes.get(j + 1) + quoteString.length()));
			}
			if (j + 2 < indexes.size()) {
				slices.add(this.body.substring(indexes.get(j + 1) + quoteString.length(), indexes.get(j + 2)));
			} else if (j + 2 == indexes.size()) {
				slices.add(this.body.substring(indexes.get(j + 1) + quoteString.length()));
			}
		}
		return slices;
	}

	private void runBreakIterator(List<String> rawSentences) {
		String quote = "{quote}";
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		for (String a : rawSentences) {
			if (!a.contains(quote) && !a.contains("{code}")) {
				iterator.setText(a);
				int start = iterator.first();
				for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
					if (end - start > 1) {
						int start1 = this.body.indexOf(a.substring(start, end));
						int end1 = a.substring(start, end).length() + start1;
						addSentenceIndex(start1,end1);
					}
				}
			} else {
				int start1 = this.body.indexOf(a);
				int end1 = a.length() + start1;
				addSentenceIndex(start1,end1);
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
			if (sentence.isRelevant() && !sentence.getBody().contains("{code}")) {
				result = result + "<span class=\"sentence " + sentence.getKnowledgeTypeString() + // done
						"\"  id  = ui" + sentence.getActiveObjectId() + ">" + sentence.getOpeningTagSpan()
						+ "<span class = sentenceBody>" + sentence.getBody() + "</span>" + sentence.getClosingTagSpan()
						+ "</span>";
			} else if(!sentence.getBody().contains("{code}")){
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
