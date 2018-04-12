package de.uhd.ifi.se.decision.documentation.jira.mocks;

import java.sql.Timestamp;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.issue.link.IssueLinkType;

public class MockIssueLinkType implements IssueLinkType{
	private Long id;
	private String name;

	public MockIssueLinkType(Long id) {
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
	public int compareTo(IssueLinkType arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getInward() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		name = "Test";
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	@Override
	public String getOutward() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStyle() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSubTaskLinkType() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSystemLinkType() {
		// TODO Auto-generated method stub
		return false;
	}

}
