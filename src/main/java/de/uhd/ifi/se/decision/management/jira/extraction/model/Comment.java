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

	public Comment() {
		this.setSentences(new ArrayList<Sentence>());
	}

	public Comment(String comment) {
		this.setBody(comment);
		splitCommentIntoSentences();
	}

	public Comment(com.atlassian.jira.issue.comments.Comment comment) {
		this.body = comment.getBody();
		this.created = comment.getCreated();
		this.authorFullName = comment.getAuthorFullName();
		this.jiraCommentId = comment.getId();
		this.authorId = comment.getAuthorApplicationUser().getId();
		splitCommentIntoSentences();
	}

	public static String textRule(String text) {
		return text.replace("<br>", " ").replaceAll("\\{quote\\}[^<]*\\{quote\\}", "").toString();
	}

	public static ArrayList<Comment> getCommentsFromStringList(ArrayList<String> strings) {
		ArrayList<Comment> comments = new ArrayList<Comment>();
		for (String body : strings) {
			comments.add(new Comment(body));
		}
		return comments;
	}

	private void splitCommentIntoSentences() {
		this.sentences = new ArrayList<Sentence>();
		this.body = textRule(this.body);
		ActiveObjectsManager.checkIfCommentBodyHasChangedOutsideOfPlugin(this);
		// Using break Iterator to split sentences in pieces from
		// https://stackoverflow.com/questions/2687012/split-string-into-sentences
		long aoId = 0;
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(this.body);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			// Sync sentence objects with AO database if sentence is larger than one
			// character
			if (end - start > 1) {
				aoId = ActiveObjectsManager.addElement(this.jiraCommentId, false, end, start, this.authorId);
				this.sentences.add(new Sentence(this.body.substring(start, end), aoId, jiraCommentId));
			}
		}
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences;
	}

	public ArrayList<Sentence> getUnTaggedSentences() {
		ArrayList<Sentence> unlabeled = new ArrayList<Sentence>();
		for (Sentence sentence : this.sentences) {
			if (!sentence.isTagged()) {
				unlabeled.add(sentence);
			}
		}
		return unlabeled;
	}

	public String getTaggedBody(int index) {
		String result = "<span id=\"comment" + index + "\">";
		for (Sentence sentence : this.sentences) {
			if (sentence.isRelevant()) {
				result = result + "<span class=\"sentence " + sentence.classificationToString() + " " + // weg
						sentence.getKnowledgeTypeString() + // done
						"\"  id  = ui" + sentence.getActiveObjectId() + ">" + sentence.getOpeningTagSpan()
						+ "<span class = sentenceBody>" + sentence.getBody() + "</span>" + sentence.getClosingTagSpan()
						+ "</span>";
			} else {
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

}
