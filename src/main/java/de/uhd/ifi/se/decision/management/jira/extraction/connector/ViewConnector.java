package de.uhd.ifi.se.decision.management.jira.extraction.connector;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;

import de.uhd.ifi.se.decision.management.jira.extraction.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.extraction.classification.ClassificationManagerForJiraIssueComments;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class ViewConnector {

	private Issue currentIssue;

	private List<Sentence> sentences;

	private CommentManager commentManager;

	private static final Logger LOGGER = LoggerFactory.getLogger(ViewConnector.class);

	private static List<String> connectorInUseLock = new ArrayList<String>();

	public ViewConnector(Issue issue) {
		if (issue != null) {
			this.setCurrentIssue(issue);
			commentManager = ComponentAccessor.getCommentManager();
			this.sentences = new ArrayList<Sentence>();
		}
	}

	public static List<Comment> getComments(Issue issue) {
		CommentManager commentManager = ComponentAccessor.getCommentManager();
		if (issue != null && commentManager.getComments(issue) != null) {
			return commentManager.getComments(issue);
		}
		return new ArrayList<Comment>();
	}

	public ViewConnector(Issue issue, boolean doNotClassify) {
		this(issue);
		if (!connectorInUseLock.contains(issue.getKey())) {
			connectorInUseLock.add(issue.getKey());
			if (issue != null && commentManager.getComments(issue) != null) {
				for (Comment comment : commentManager.getComments(issue)) {
					List<Sentence> sentencesOfComment = new CommentSplitter().getSentences(comment);
					sentences.addAll(sentencesOfComment);
				}
			}
			if (!doNotClassify
					&& ConfigPersistenceManager.isUseClassiferForIssueComments(issue.getProjectObject().getKey())) {
				this.startClassification();
			}
			connectorInUseLock.remove(issue.getKey());
		} else {
			LOGGER.debug("Could not run ViewConnector. It's currently in use for issue: " + issue.getKey());
		}
	}

	private void startClassification() {
		ClassificationManagerForJiraIssueComments classifier = new ClassificationManagerForJiraIssueComments();
		this.sentences = classifier.classifySentencesBinary(sentences);
		this.sentences = classifier.classifySentencesFineGrained(sentences);
	}

	public Issue getCurrentIssue() {
		return currentIssue;
	}

	public void setCurrentIssue(Issue currentIssue) {
		this.currentIssue = currentIssue;
	}
}