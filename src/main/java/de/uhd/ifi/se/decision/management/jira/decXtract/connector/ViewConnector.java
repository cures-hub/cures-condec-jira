package de.uhd.ifi.se.decision.management.jira.decXtract.connector;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;

import de.uhd.ifi.se.decision.management.jira.decXtract.classification.WekaInitializer;
import de.uhd.ifi.se.decision.management.jira.decXtract.model.Comment;

public class ViewConnector {

	private Issue currentIssue;

	private List<Comment> commentsList;

	public ViewConnector(Issue issue) {
		this.setCurrentIssue(issue);
		CommentManager cm = ComponentAccessor.getCommentManager();
		this.commentsList = new ArrayList<Comment>();

		for (com.atlassian.jira.issue.comments.Comment comment : cm.getComments(issue)) {
			commentsList.add(new Comment(comment));
		}
		this.startClassification();
	}

	public void startClassification() {
		try {
			WekaInitializer.init(commentsList);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.commentsList = WekaInitializer.predict(commentsList);
	}

	public Issue getCurrentIssue() {
		return currentIssue;
	}

	public void setCurrentIssue(Issue currentIssue) {
		this.currentIssue = currentIssue;
	}

	public List<String> getAllTaggedComments() {
		List<String> comments = new ArrayList<String>();
		for (Comment c : commentsList) {
			comments.add("<p>" + c.getTaggedBody() + "</p>");
		}
		return comments;
	}

	public List<String> getAllCommentsBody() {
		List<String> comments = new ArrayList<String>();
		for (Comment c : commentsList) {
			comments.add("<p>" + c.getBody() + "</p>");
		}
		return comments;
	}

	public List<Long> getAllCommentsIDs() {
		List<Long> comments = new ArrayList<Long>();
		for (Comment c : commentsList) {
			comments.add(c.getId());
		}
		return comments;
	}

	public List<String> getAllCommentsAuthorNames() {
		List<String> authorName = new ArrayList<String>();
		for (Comment c : commentsList) {
			authorName.add(c.getAuthorFullName());
		}
		return authorName;
	}

	public List<String> getAllCommentsDates() {
		List<String> commentDate = new ArrayList<String>();
		for (Comment c : commentsList) {
			commentDate.add(c.getCreated().toString().replace("CEST", ""));
		}
		return commentDate;
	}

}
