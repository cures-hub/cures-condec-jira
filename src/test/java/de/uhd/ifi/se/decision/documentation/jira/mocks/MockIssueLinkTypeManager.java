package de.uhd.ifi.se.decision.documentation.jira.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;

public class MockIssueLinkTypeManager implements IssueLinkTypeManager{
	private boolean notInit;

	public MockIssueLinkTypeManager() {
		super();
	}
	public MockIssueLinkTypeManager(boolean notInit) {
		if(notInit==true) {
			this.notInit=true;
		}
	}

	@Override
	public void createIssueLinkType(String arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public IssueLinkType getIssueLinkType(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueLinkType getIssueLinkType(Long arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypes(boolean arg0) {
		if(notInit==true) {
			Collection<IssueLinkType> isseLTypes = new HashSet<>();
			return isseLTypes;
		}
		Collection<IssueLinkType> isseLTypes = new HashSet<>();
		ArrayList<String> types= new ArrayList<>();

		types.add("contain");
		types.add("attack");
		types.add("support");
		types.add("comment");
		for(String type: types) {
			IssueLinkType lt = new MockIssueLinkType((long) 1);
			((MockIssueLinkType)lt).setName(type);
			isseLTypes.add(lt);
		}
		return isseLTypes;

	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypesByInwardDescription(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypesByName(String arg0) {
		Collection<IssueLinkType> issueLinkTypeCollection = new HashSet<>();
		IssueLinkType linkType = new MockIssueLinkType((long) 1);
		issueLinkTypeCollection.add(linkType);
		if(arg0.equals("Ok")) {

		}
		return issueLinkTypeCollection;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypesByOutwardDescription(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueLinkType> getIssueLinkTypesByStyle(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeIssueLinkType(Long arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateIssueLinkType(IssueLinkType arg0, String arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub

	}

}
