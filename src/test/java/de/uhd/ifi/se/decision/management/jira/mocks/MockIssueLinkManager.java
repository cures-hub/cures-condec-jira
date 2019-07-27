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

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;

public class MockIssueLinkManager implements IssueLinkManager {

	@Override
	public void changeIssueLinkType(IssueLink arg0, IssueLinkType arg1, ApplicationUser arg2) {
		// method empty since not used for testing
	}

	@Override
	public void createIssueLink(Long arg0, Long arg1, Long arg2, Long arg3, ApplicationUser user)
			throws CreateException {
		if (user == null || user == JiraUsers.BLACK_HEAD.getApplicationUser()) {
			throw new CreateException();
		}
	}

	@Override
	public List<IssueLink> getInwardLinks(Long issueId) {
		List<IssueLink> allInwardIssueLink = new ArrayList<>();
		if (issueId == 20) {
			IssueLink link = new MockIssueLink(3);
			allInwardIssueLink.add(link);
		}
		if (issueId == 30) {
			for (int i = 0; i < 10; i++) {
				IssueLink link = new MockIssueLink(3);
				((MockIssueLink) link).setSequence(i);
				if (i == 9) {
					((MockIssueLink) link).setSequence(1);
				}
				allInwardIssueLink.add(link);
			}
		}
		return allInwardIssueLink;
	}

	@Override
	public IssueLink getIssueLink(Long issueId) {
		return new MockIssueLink(issueId);
	}

	@Override
	public IssueLink getIssueLink(Long arg0, Long arg1, Long arg2) {
		if (arg1 == 1) {
			return new MockIssueLink(arg0);
		}
		return null;
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
		List<IssueLink> allOutwardIssueLink = new ArrayList<>();
		if (issueId.intValue() == 5000) {
			for (long i = 2; i <= KnowledgeType.values().length; i++) {
				MockIssueLink link = new MockIssueLink(i);
				allOutwardIssueLink.add(link);
			}
		}
		if (issueId == 20) {
			IssueLink link = new MockIssueLink(4);
			allOutwardIssueLink.add(link);
		}
		if (issueId == 30) {
			for (int i = 0; i < 10; i++) {
				IssueLink link = new MockIssueLink(3);
				((MockIssueLink) link).setSequence((long) i + 10);
				if (i == 9) {
					((MockIssueLink) link).setSequence(1);
				}
				allOutwardIssueLink.add(link);
			}
		}
		if (issueId == 12) {
			MockIssueLink link = new MockIssueLink(1);
			allOutwardIssueLink.add(link);
		}
		return allOutwardIssueLink;
	}

	@Override
	public boolean isLinkingEnabled() {
		return false;
	}

	@Override
	public void moveIssueLink(List<IssueLink> arg0, Long arg1, Long arg2) {
		// method empty since not used for testing
	}

	@Override
	public void removeIssueLink(IssueLink arg0, ApplicationUser arg1) {
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
