package ut.de.uhd.ifi.se.decdoc.jira.mocks;

import java.sql.Timestamp;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.issuetype.MockIssueType;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.mock.issue.MockIssue;

public class MockIssueLink implements IssueLink{
	private Long id;
	private Long sequenz;
	public MockIssueLink(Long id) {
		this.id=id;
	}

	@Override
	public GenericValue getGenericValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getLong(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getString(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getTimestamp(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void store() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long getDestinationId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Issue getDestinationObject() {
		IssueType issueType = new MockIssueType(12, "Argument");
		Issue issue = new MockIssue(200, "TEST-200");
		((MockIssue) issue).setIssueType(issueType);
		((MockIssue) issue).setDescription("Test");
		((MockIssue) issue).setSummary("Test");
		return issue;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public IssueLinkType getIssueLinkType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getLinkTypeId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getSequence() {
		return sequenz;
	}
	
	public void setSequence(Long seq) {
		sequenz=seq;
	}

	@Override
	public Long getSourceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Issue getSourceObject() {
		IssueType issueType = new MockIssueType(12, "Argument");
		Issue issue = new MockIssue(300, "TEST-300");
		((MockIssue) issue).setIssueType(issueType);
		((MockIssue) issue).setDescription("Test");
		((MockIssue) issue).setSummary("Test");
		return issue;
	}

	@Override
	public boolean isSystemLink() {
		// TODO Auto-generated method stub
		return false;
	}

}
