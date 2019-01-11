package de.uhd.ifi.se.decision.management.jira.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public interface Comment {

	public List<Sentence> getSentences();

	public void setSentences(ArrayList<Sentence> sentences);

	public String getBody();

	public void setBody(String body);

	public long getJiraCommentId();

	public void setJiraCommentId(long id);

	public String getAuthorFullName();

	public void setAuthorFullName(String authorApplicationUser);

	public Date getCreated();

	public void setCreated(Date created);

	public long getAuthorId();

	public void setAuthorId(long authorId);

	public List<Integer> getStartSubstringCount();

	public List<Integer> getEndSubstringCount();

	public String getProjectKey();

	public void setProjectKey(String projectKey);

	public Long getIssueId();

	public void setIssueId(Long issueId);
	
	public void reloadSentencesFromAo();

}
