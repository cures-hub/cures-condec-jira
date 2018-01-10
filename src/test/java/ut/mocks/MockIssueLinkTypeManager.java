package ut.mocks;

import java.util.Collection;
import java.util.HashSet;

import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;

public class MockIssueLinkTypeManager implements IssueLinkTypeManager{

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
		// TODO Auto-generated method stub
		return null;
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
