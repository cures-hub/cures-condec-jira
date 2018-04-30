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
		return null;
	}

	@Override
	public Long getLong(String arg0) {
		return null;
	}

	@Override
	public String getString(String arg0) {
		return null;
	}

	@Override
	public Timestamp getTimestamp(String arg0) {
		return null;
	}

	@Override
	public void store() {
		
	}

	@Override
	public int compareTo(IssueLinkType arg0) {
		return 0;
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public String getInward() {
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
		return null;
	}

	@Override
	public String getStyle() {
		return null;
	}

	@Override
	public boolean isSubTaskLinkType() {
		return false;
	}

	@Override
	public boolean isSystemLinkType() {
		return false;
	}

}
