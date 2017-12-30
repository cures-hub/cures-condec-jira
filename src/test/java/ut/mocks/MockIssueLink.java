package ut.mocks;

import java.sql.Timestamp;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkType;

public class MockIssueLink implements IssueLink{
	private Long id;
	
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
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getSourceId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Issue getSourceObject() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSystemLink() {
		// TODO Auto-generated method stub
		return false;
	}

}
