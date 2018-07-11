package de.uhd.ifi.se.decision.management.jira.decXtract.connector;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;


public class ViewConnector {

	private Issue currentIssue;

	private List<Comment> commentsList;

	public ViewConnector(Issue issue) {
		this.setCurrentIssue(issue);
		CommentManager cm  = ComponentAccessor.getCommentManager();
		commentsList = cm.getComments(issue);
	}

	public Issue getCurrentIssue() {
		return currentIssue;
	}

	public void setCurrentIssue(Issue currentIssue) {
		this.currentIssue = currentIssue;
	}

	public List<String> getAllCommentsBody() {
	List<String> comments = new ArrayList<String>();
		for(Comment c: commentsList) {
		comments.add("<p>" +c.getBody() + "</p>");
	}
		return comments;
	}

	public List<Long> getAllCommentsIDs() {
	List<Long> comments = new ArrayList<Long>();
		for(Comment c: commentsList) {
		comments.add(c.getId());
	}
		return comments;
	}

	public List<Long> getAllAuthorIDs() {
	List<Long> authorIDs = new ArrayList<Long>();
		for(Comment c: commentsList) {
			authorIDs.add(c.getAuthorApplicationUser().getId());
	}
		return authorIDs;
	}

	public List<String> getAllCommentsAuthorNames() {
	List<String> authorName = new ArrayList<String>();
	for(Comment c: commentsList) {
		authorName.add(c.getAuthorFullName());
	}
	return authorName;
	}
	public List<String> getAllCommentsDates() {
	List<String> commentDate = new ArrayList<String>();
	for(Comment c: commentsList) {
		commentDate.add(c.getCreated().toString().replace("CEST", ""));
	}
	return commentDate;
	}

}
