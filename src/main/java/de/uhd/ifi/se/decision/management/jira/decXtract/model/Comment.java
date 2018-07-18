package de.uhd.ifi.se.decision.management.jira.decXtract.model;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Comment {

	private ArrayList<Sentence> sentences;

	private String body = "";

	private Long Id;

	private String authorFullName;

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

		// Using break Iterator from
		// https://stackoverflow.com/questions/2687012/split-string-into-sentences
		//Instead of:  this.body.split("\\. ");
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);
		String source = this.body;
		iterator.setText(source);
		int start = iterator.first();
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			this.sentences.add(new Sentence(source.substring(start, end)));
		}
	}

	public ArrayList<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences;
	}

	public String getTaggedBody() {
		String result = "";
		for (Sentence sentence : this.sentences) {
			if (sentence.isRelevant()) {
				result = result + Rationale.getOpeningTag("isRelevant")  + Rationale.getOpeningTag(sentence.getClassification())+ sentence.getBody()
				+ Rationale.getClosingTag(sentence.getClassification()) + Rationale.getClosingTag("isRelevant");
			} else {
				result = result + sentence.getBody();
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

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
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

}
