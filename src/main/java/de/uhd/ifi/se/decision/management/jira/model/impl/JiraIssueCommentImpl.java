package de.uhd.ifi.se.decision.management.jira.model.impl;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.atlassian.jira.issue.comments.Comment;

import de.uhd.ifi.se.decision.management.jira.extraction.model.util.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.model.JiraIssueComment;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueCommentPersistenceManager;

public class JiraIssueCommentImpl implements JiraIssueComment  {

	private List<Sentence> sentences;

	private String body = "";

	private long jiraCommentId;

	private String authorFullName;

	private long authorId;

	private Date created;

	private String projectKey;

	private CommentSplitter splitter;

	private Long issueId;

	public JiraIssueCommentImpl() {
		this.sentences = new ArrayList<Sentence>();
		this.created = new Date();
		this.authorFullName = " ";
		this.jiraCommentId = 0;
		this.authorId = 0;
		this.splitter = new CommentSplitter();
		this.projectKey = " ";
		this.issueId = 0L;
	}

	public JiraIssueCommentImpl(Comment comment) {
		this();
		this.projectKey = comment.getIssue().getProjectObject().getKey();
		this.body = comment.getBody();
		this.created = comment.getCreated();
		this.authorFullName = comment.getAuthorFullName();
		this.jiraCommentId = comment.getId();
		this.authorId = comment.getAuthorApplicationUser().getId();
		this.setIssueId(comment.getIssue().getId());
		splitCommentIntoParts();
	}

	// TODO das sollte statische Methode in CommentSplitter sein
	private void splitCommentIntoParts() {
		List<String> rawSentences = this.splitter.sliceCommentRecursionCommander(this.body, this.projectKey);
		runBreakIterator(rawSentences);
		// Create AO entries
		for (int i = 0; i < this.splitter.getStartSubstringCount().size(); i++) {
			int startIndex = this.splitter.getStartSubstringCount().get(i);
			int endIndex = this.splitter.getEndSubstringCount().get(i);
			if (startAndEndIndexRules(startIndex, endIndex)) {
				Sentence sentence = new SentenceImpl();
				sentence.setCommentId(this.jiraCommentId);
				sentence.setEndSubstringCount(endIndex);
				sentence.setStartSubstringCount(startIndex);
				sentence.setIssueId(this.issueId);
				sentence.setProject(this.projectKey);
				long aoId2 = JiraIssueCommentPersistenceManager.insertDecisionKnowledgeElement(sentence, null);
				sentence = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(aoId2);
				JiraIssueCommentPersistenceManager.createSmartLinkForSentence(sentence);
				sentence.setCreated(this.created);
				this.sentences.add(sentence);
			}
		}
	}

	/**
	 * Checks: Start Index >=0, End Index >= 0, End Index - Start Index > 0, Body
	 * not only whitespaces
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	private boolean startAndEndIndexRules(int startIndex, int endIndex) {
		return (startIndex >= 0 && endIndex >= 0 && (endIndex - startIndex) > 0
				&& this.body.substring(startIndex, endIndex).replaceAll("\r\n", "").trim().length() > 1);
	}

	private void runBreakIterator(List<String> rawSentences) {
		BreakIterator iterator = BreakIterator.getSentenceInstance(Locale.US);

		for (String currentSentence : rawSentences) {
			if (StringUtils.indexOfAny(currentSentence, CommentSplitter.allExcluded) == -1) {
				iterator.setText(currentSentence);
				int start = iterator.first();
				for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
					if (end - start > 1 && currentSentence.substring(start, end).trim().length() > 0) {
						int startOfSentence = this.body.toLowerCase()
								.indexOf(currentSentence.toLowerCase().substring(start, end));
						int endOfSentence = currentSentence.substring(start, end).length() + startOfSentence;
						this.splitter.addSentenceIndex(startOfSentence, endOfSentence);
					}
				}
			} else {
				int start1 = this.body.toLowerCase().indexOf(currentSentence.toLowerCase());
				int end1 = currentSentence.length() + start1;
				this.splitter.addSentenceIndex(start1, end1);
			}
		}
	}

	public List<Sentence> getSentences() {
		return sentences;
	}

	public void setSentences(ArrayList<Sentence> sentences) {
		this.sentences = sentences;
	}

	public void reloadSentencesFromAo() {
		List<Sentence> newSentences = new ArrayList<>();
		for (Sentence sentence : this.sentences) {
			Sentence aoSentence = (Sentence) new JiraIssueCommentPersistenceManager("").getDecisionKnowledgeElement(sentence.getId());
			newSentences.add(aoSentence);
		}
		this.sentences = newSentences;
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
		return this.splitter.getStartSubstringCount();
	}

	public List<Integer> getEndSubstringCount() {
		return this.splitter.getEndSubstringCount();
	}

	public String getProjectKey() {
		return projectKey;
	}

	public void setProjectKey(String projectKey) {
		this.projectKey = projectKey;
	}

	public Long getIssueId() {
		return issueId;
	}

	public void setIssueId(Long issueId) {
		this.issueId = issueId;
	}
}
