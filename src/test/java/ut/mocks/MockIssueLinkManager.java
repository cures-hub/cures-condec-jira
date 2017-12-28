package ut.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.ofbiz.core.entity.GenericValue;

import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.LinkCollection;
import com.atlassian.jira.user.ApplicationUser;
/**
 * 
 * @author Tim Kuchenbuch
 * @description rudimentary implementation. Only for Test Use 
 *
 */
public class MockIssueLinkManager implements IssueLinkManager {

	@Override
	public void changeIssueLinkType(IssueLink arg0, IssueLinkType arg1, ApplicationUser arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createIssueLink(Long arg0, Long arg1, Long arg2, Long arg3, ApplicationUser arg4)
			throws CreateException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<IssueLink> getInwardLinks(Long arg0) {
		List<IssueLink> allInwardIssueLink = new ArrayList<>();
		return allInwardIssueLink;
	}

	@Override
	public IssueLink getIssueLink(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IssueLink getIssueLink(Long arg0, Long arg1, Long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<IssueLink> getIssueLinks(Long arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkCollection getLinkCollection(GenericValue arg0, ApplicationUser arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkCollection getLinkCollection(Issue arg0, ApplicationUser arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkCollection getLinkCollection(Issue arg0, ApplicationUser arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkCollection getLinkCollectionOverrideSecurity(Issue arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IssueLink> getOutwardLinks(Long arg0) {
		List<IssueLink> allOutwardIssueLink = new ArrayList<>();
		return allOutwardIssueLink;
	}

	@Override
	public boolean isLinkingEnabled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void moveIssueLink(List<IssueLink> arg0, Long arg1, Long arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeIssueLink(IssueLink arg0, ApplicationUser arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int removeIssueLinks(GenericValue arg0, ApplicationUser arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeIssueLinks(Issue arg0, ApplicationUser arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int removeIssueLinksNoChangeItems(Issue arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void resetSequences(List<IssueLink> arg0) {
		// TODO Auto-generated method stub
		
	}

}
