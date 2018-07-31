package de.uhd.ifi.se.decision.management.jira.extraction.connector;

import java.util.ArrayList;
import java.util.List;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.sal.api.transaction.TransactionCallback;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.classification.MekaInitializer;
import de.uhd.ifi.se.decision.management.jira.extraction.classification.WekaInitializer;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Comment;
import de.uhd.ifi.se.decision.management.jira.extraction.persistance.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.extraction.persistance.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.extraction.persistance.DecisionKnowledgeInCommentImpl;

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
			this.commentsList = WekaInitializer.classifySentencesBinary(commentsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			MekaInitializer.classifySentencesFineGrained(this.commentsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Issue getCurrentIssue() {
		return currentIssue;
	}

	public void setCurrentIssue(Issue currentIssue) {
		this.currentIssue = currentIssue;
	}

	public List<String> getAllTaggedComments() {
		List<String> comments1 = new ArrayList<String>();
		for (Comment c : commentsList) {
			comments1.add(c.getTaggedBody());
		}
		List<String> comments2 = new ArrayList<String>();
		for(String comment: comments1) {
			if(comment.length() > 0) {
				comments2.add(comment);
			}
		}
		comments1 = null;
		return comments2;
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
