package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.Date;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.user.ApplicationUser;

public class MockComment implements MutableComment {

	private String author;

	private String body;

	private Issue issue;

	private Long issueID;

	private ApplicationUser user;

	public MockComment(Issue issue) {
		super();
		this.body = "This is a comment for test purposes";
		this.author = "Marc";
		this.issue = issue;
	}

	public MockComment(Issue issue, ApplicationUser applicationUser, String commentBody) {
		super();
		this.issue = issue;
		this.user = applicationUser;
		this.body = commentBody;
		this.issueID = issue.getId();
	}

	@Override
	public String getAuthor() {
		return this.author;
	}

	@Override
	public ApplicationUser getAuthorApplicationUser() {
		return this.user;
	}

	@Override
	public String getAuthorFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAuthorKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getAuthorUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBody() {
		return this.body;
	}

	@Override
	public Date getCreated() {
		// TODO Auto-generated method stub
		return new Date();
	}

	@Override
	public String getGroupLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getId() {
		// TODO Auto-generated method stub
		return (long) 1337;
	}

	@Override
	public Issue getIssue() {
		return this.issue;
	}

	@Override
	public ProjectRole getRoleLevel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getRoleLevelId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateAuthor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getUpdateAuthorApplicationUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUpdateAuthorFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ApplicationUser getUpdateAuthorUser() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getUpdated() {
		// TODO Auto-generated method stub
		return null;
	}

	public long getIssueID() {
		return this.issueID;
	}

	public void setIssueID(long issueId) {
		this.issueID = issueId;
	}

	@Override
	public void setAuthor(ApplicationUser arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAuthor(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBody(String arg0) {
		this.body = arg0;

	}

	@Override
	public void setCreated(Date arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGroupLevel(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRoleLevelId(Long arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUpdateAuthor(ApplicationUser arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUpdateAuthor(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setUpdated(Date arg0) {
		// TODO Auto-generated method stub

	}

}
