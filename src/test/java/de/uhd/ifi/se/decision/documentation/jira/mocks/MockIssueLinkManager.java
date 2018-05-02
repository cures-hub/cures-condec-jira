package de.uhd.ifi.se.decision.documentation.jira.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.documentation.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;
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
		
	}

	@Override
	public void createIssueLink(Long arg0, Long arg1, Long arg2, Long arg3, ApplicationUser arg4)
			throws CreateException {
		if(arg0==4) {
			return;
		}
		if(arg4==null) {
			throw new NullPointerException();
		}
		if(arg4.getUsername().equals("CreateExecption")) {
			throw new CreateException();
		}
	}

	@Override
	public List<IssueLink> getInwardLinks(Long arg0) {
		List<IssueLink> allInwardIssueLink = new ArrayList<>();
		if(arg0==20) {
			IssueLink link = new MockIssueLink((long)3);
			allInwardIssueLink.add(link);
		}
		if(arg0==30) {
			for(int i=0 ;i<10;i++) {
				IssueLink link = new MockIssueLink((long)3);
				((MockIssueLink)link).setSequence((long)i);
				if(i==9) {
					((MockIssueLink)link).setSequence((long)1);
				}
				allInwardIssueLink.add(link);
			}
		}
		return allInwardIssueLink;
	}

	@Override
	public IssueLink getIssueLink(Long arg0) {
		IssueLink issueLink = new MockIssueLink(arg0);
		return issueLink;
	}

	@Override
	public IssueLink getIssueLink(Long arg0, Long arg1, Long arg2) {
		if(arg1 == 1) {
			IssueLink issueLink = new MockIssueLink(arg0);
			return issueLink;
		}
		return null;
	}

	@Override
	public Collection<IssueLink> getIssueLinks(Long arg0) {
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
	public List<IssueLink> getOutwardLinks(Long arg0) {
		List<IssueLink> allOutwardIssueLink = new ArrayList<>();
		if(arg0.intValue() == 5000){
			long i = 2;
			for(KnowledgeType type : KnowledgeType.values()){
				MockIssueLink link = new MockIssueLink(i);
				allOutwardIssueLink.add(link);
				i++;
			}
		}
		if(arg0 == 20) {
			IssueLink link = new MockIssueLink((long)4);
			allOutwardIssueLink.add(link);
		}
		if(arg0 == 30) {
			for(int i=0 ;i<10;i++) {
				IssueLink link = new MockIssueLink((long)3);
				((MockIssueLink)link).setSequence((long)i+10);
				if(i==9) {
					((MockIssueLink)link).setSequence((long)1);
				}
				allOutwardIssueLink.add(link);
			}
		}
		if(arg0 == 12) {
			MockIssueLink link = new MockIssueLink((long)1);
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
		
	}

	@Override
	public void removeIssueLink(IssueLink arg0, ApplicationUser arg1) {
		
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
		
	}

}
