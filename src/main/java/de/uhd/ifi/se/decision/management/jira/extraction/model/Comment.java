package de.uhd.ifi.se.decision.management.jira.extraction.model;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.uhd.ifi.se.decision.management.jira.extraction.persistance.ActiveObjectsManager;

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

	public static ArrayList<Comment> getCommentsFromStringList(ArrayList<String> strings) {
		ArrayList<Comment> comments = new ArrayList<Comment>();
		for (String body : strings) {
			comments.add(new Comment(body));
		}
		return comments;
	}

	private void splitCommentIntoSentences() {
		this.sentences = new ArrayList<Sentence>();
		// Delete breaklines,
		this.body = this.body.replace("<br>", " ").replace("\n", " ").replace("\r", " ").replaceAll("\\<.*?>", "")
				.toString();
		// Using break Iterator from https://stackoverflow.com/questions/2687012/split-string-into-sentences
		//to split sentences in pieces
		long aoId =0;
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		iterator.setText(this.body);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			//Sync sentence objects with AO database
			aoId = ActiveObjectsManager.addElement(this.jiraCommentId, false, end, start, this.authorId);
			this.sentences.add(new Sentence(this.body.substring(start, end),aoId));
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
		for(Sentence sentence: this.sentences) {
			if(!sentence.isTagged()) {
				unlabeled.add(sentence);
			}
		}
		return unlabeled;
	}

	public String getTaggedBody() {
		String result = "";
		for (Sentence sentence : this.sentences) {
			if (sentence.isRelevant()) {
				result = result   + Rationale.getOpeningTag(sentence.getClassification())+ Rationale.getOpeningTag("isRelevant")+ sentence.getBody()
				+ Rationale.getClosingTag("isRelevant")+ Rationale.getClosingTag(sentence.getClassification()) ;
			} else {
				result = result+ sentence.getBody();// + sentence.getBody(); if commented, irrelevant sentences will not be shown
			}
		}
		return result;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getId() {
		return jiraCommentId;
	}

	public void setId(long id) {
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
