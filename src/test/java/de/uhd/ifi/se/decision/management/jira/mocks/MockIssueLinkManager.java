package de.uhd.ifi.se.decision.management.jira.mocks;

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

import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class MockIssueLinkManager implements IssueLinkManager {

	@Override
	public void changeIssueLinkType(IssueLink arg0, IssueLinkType arg1, ApplicationUser arg2) {
		// method empty since not used for testing
	}

	@Override
	public void createIssueLink(Long sourceIssueId, Long destinationIssueId, Long issueLinkTypeId, Long sequence,
			ApplicationUser user) throws CreateException {
		if (user == null || user == JiraUsers.BLACK_HEAD.getApplicationUser()) {
			throw new CreateException();
		}
	}

	@Override
	public List<IssueLink> getInwardLinks(Long issueId) {
		List<IssueLink> inwardIssueLinks = new ArrayList<>();
		if (issueId == 0) {
			return inwardIssueLinks;
		}
		long childId = issueId - 1;
		if (childId > 0) {
			MockIssueLink link = new MockIssueLink(childId, issueId);
			inwardIssueLinks.add(link);
		}
		return inwardIssueLinks;
	}

	@Override
	public IssueLink getIssueLink(Long issueId) {
		return new MockIssueLink(1, 2);
	}

	@Override
	public IssueLink getIssueLink(Long sourceIssueId, Long destinationIssueId, Long issueLinkTypeId) {
		return new MockIssueLink(sourceIssueId, destinationIssueId);
	}

	@Override
	public Collection<IssueLink> getIssueLinks(Long issueId) {
		return null;
	}

	@Override
	public LinkCollection getLinkCollection(GenericValue arg0, ApplicationUser arg1) {
		return null;
	}

	@Override
	public LinkCollection getLinkCollection(Issue arg0, ApplicationUser arg1) {
		return null;
	}

	@Override
	public LinkCollection getLinkCollection(Issue arg0, ApplicationUser arg1, boolean arg2) {
		return null;
	}

	@Override
	public LinkCollection getLinkCollectionOverrideSecurity(Issue arg0) {
		return null;
	}

	@Override
	public List<IssueLink> getOutwardLinks(Long issueId) {
		List<IssueLink> outwardIssueLinks = new ArrayList<IssueLink>();
		if (issueId == 0) {
			return outwardIssueLinks;
		}
		long parentId = issueId - 1;
		if (parentId > 0) {
			MockIssueLink link = new MockIssueLink(issueId, parentId);
			outwardIssueLinks.add(link);
		}
		return outwardIssueLinks;
	}

	@Override
	public boolean isLinkingEnabled() {
		return false;
	}

	@Override
	public void moveIssueLink(List<IssueLink> issueLinks, Long currentSequence, Long sequence) {
		// method empty since not used for testing
	}

	@Override
	public void removeIssueLink(IssueLink issueLink, ApplicationUser user) {
		// method empty since not used for testing
	}

	@Override
	public int removeIssueLinks(GenericValue arg0, ApplicationUser arg1) {
		return 0;
	}

	@Override
	public int removeIssueLinks(Issue arg0, ApplicationUser arg1) {
		return 0;
	}

	@Override
	public int removeIssueLinksNoChangeItems(Issue arg0) {
		return 0;
	}

	@Override
	public void resetSequences(List<IssueLink> arg0) {
		// method empty since not used for testing
	}

}
