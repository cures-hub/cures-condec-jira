package de.uhd.ifi.se.decision.management.jira.extraction.connector;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;

import de.uhd.ifi.se.decision.management.jira.extraction.classification.ClassificationManagerForCommentSentences;
import de.uhd.ifi.se.decision.management.jira.model.JiraIssueComment;
import de.uhd.ifi.se.decision.management.jira.model.impl.JiraIssueCommentImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class ViewConnector {

	private Issue currentIssue;

	private List<JiraIssueComment> commentsList;

	private CommentManager commentManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(ViewConnector.class);

	private static List<String> connectorInUseLock = new ArrayList<String>();

	public ViewConnector(Issue issue) {
		if (issue != null) {
			this.setCurrentIssue(issue);
			commentManager = ComponentAccessor.getCommentManager();
			this.commentsList = new ArrayList<JiraIssueComment>();
		}
	}

	public ViewConnector(Issue issue, boolean doNotClassify) {
		this(issue);
		if (!connectorInUseLock.contains(issue.getKey())) {
			connectorInUseLock.add(issue.getKey());
			if (issue != null && commentManager.getComments(issue) != null) {
				for (Comment comment : commentManager.getComments(issue)) {
					JiraIssueComment comment2 = new JiraIssueCommentImpl(comment);
					commentsList.add(comment2);
				}
			}
			if (!doNotClassify && ConfigPersistenceManager.isUseClassiferForIssueComments(issue.getProjectObject().getKey())) {
				this.startClassification();
			}
			connectorInUseLock.remove(issue.getKey());
		} else {
			LOGGER.debug("Could not run ViewConnector. It's currently in use for issue: " + issue.getKey());
		}
	}

	private void startClassification() {
		ClassificationManagerForCommentSentences classifier = new ClassificationManagerForCommentSentences();
		this.commentsList = classifier.classifySentenceBinary(commentsList);
		this.commentsList = classifier.classifySentenceFineGrained(commentsList);
	}

	public Issue getCurrentIssue() {
		return currentIssue;
	}

	public void setCurrentIssue(Issue currentIssue) {
		this.currentIssue = currentIssue;
	}

	public List<String> getAllCommentsAuthorNames() {
		List<String> authorNames = new ArrayList<String>();
		for (JiraIssueComment comment : commentsList) {
			authorNames.add(comment.getAuthorFullName());
		}
		return authorNames;
	}

	public List<String> getAllCommentsDates() {
		List<String> commentDates = new ArrayList<String>();
		for (JiraIssueComment comment : commentsList) {
			commentDates.add(comment.getCreated().toString().replace("CEST", ""));
		}
		return commentDates;
	}

	public String getSentenceStyles() {
		String style = "<style>";
		style += ".Issue {" + "    background-color: #F2F5A9;} ";
		style += ".Alternative {" + "    background-color: #f1ccf9;} ";
		style += ".Decision {" + "    background-color: #c5f2f9;} ";
		style += ".Pro {" + "    background-color: #b9f7c0;} ";
		style += ".Con {" + "    background-color: #ffdeb5;} ";
		style += ".tag {" + "    background-color: #ffffff;} ";
		style += "</style>";

		return style;
	}
}