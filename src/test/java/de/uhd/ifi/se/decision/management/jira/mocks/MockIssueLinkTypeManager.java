package de.uhd.ifi.se.decision.management.jira.mocks;

import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class MockIssueLinkTypeManager implements IssueLinkTypeManager {
	private boolean notInit;

	public MockIssueLinkTypeManager() {
		super();
	}

	public MockIssueLinkTypeManager(boolean notInit) {
		if (notInit) {
			this.notInit = true;
		}
	}

	@Override
	public void createIssueLinkType(String arg0, String arg1, String arg2, String arg3) {
		// method empty since not used for testing
	}

	@Override
	public IssueLinkType getIssueLinkType(Long arg0) {
		return null;
	}

	@Override
	public IssueLinkType getIssueLinkType(Long arg0, boolean arg1) {
		return null;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypes() {
        Collection<IssueLinkType> linkTypes = new ArrayList<>();
        IssueLinkType linkType = new MockIssueLinkType((long)1);
        linkTypes.add(linkType);
		return linkTypes;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypes(boolean arg0) {
		if (notInit) {
			return new HashSet<>();
		}
		Collection<IssueLinkType> issueLinkTypes = new HashSet<>();
		issueLinkTypes.add(new MockIssueLinkType((long) 1, "contain", "contains", "is contained by"));
		issueLinkTypes.add(new MockIssueLinkType((long) 2, "attack", "attacks", "is attacked by"));
		issueLinkTypes.add(new MockIssueLinkType((long) 3, "support", "supports", "is supported by"));
		issueLinkTypes.add(new MockIssueLinkType((long) 4, "comment", "comments", "is commented by"));
		return issueLinkTypes;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypesByInwardDescription(String arg0) {
		return null;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypesByName(String arg0) {
		Collection<IssueLinkType> issueLinkTypeCollection = new HashSet<>();
		IssueLinkType linkType = new MockIssueLinkType((long) 1);
		issueLinkTypeCollection.add(linkType);
		return issueLinkTypeCollection;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypesByOutwardDescription(String arg0) {
		return null;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypesByStyle(String arg0) {
		return null;
	}

	@Override
	public void removeIssueLinkType(Long arg0) {
		// method empty since not used for testing
	}

	@Override
	public void updateIssueLinkType(IssueLinkType arg0, String arg1, String arg2, String arg3) {
		// method empty since not used for testing
	}
}