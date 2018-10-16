package de.uhd.ifi.se.decision.management.jira.mocks;

import java.util.Date;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.security.roles.ProjectRole;
import com.atlassian.jira.user.ApplicationUser;

public class MockComment implements Comment{
	
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

	public MockComment(Issue issue2, ApplicationUser applicationUser, String s) {
		super();
		this.issue = issue2;
		this.user = applicationUser;
		this.body = s;
		this.issueID = issue2.getId();
	}

	@Override
	public String getAuthor() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		return issueID;
	}

	public void setIssueID(long issueID) {
		this.issueID = issueID;
	}

}
