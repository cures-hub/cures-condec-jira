package de.uhd.ifi.se.decision.documentation.jira.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.ofbiz.core.entity.GenericEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.bc.issue.IssueService;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueInputParameters;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.MutableIssue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.IDecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.documentation.jira.model.Link;

/**
 * @description Extends the abstract class PersistenceStrategy. Uses JIRA
 *              issues to store decision knowledge.
 */
public class IssueStrategy extends PersistenceStrategy {
	private static final Logger LOGGER = LoggerFactory.getLogger(IssueStrategy.class);

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement decisionElement,
			ApplicationUser user) {
		IssueInputParameters issueInputParameters = ComponentAccessor.getIssueService().newIssueInputParameters();

		issueInputParameters.setSummary(decisionElement.getSummary());
		issueInputParameters.setDescription(decisionElement.getDescription());
		issueInputParameters.setAssigneeId(user.getName());
		issueInputParameters.setReporterId(user.getName());

		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(decisionElement.getProjectKey());
		issueInputParameters.setProjectId(project.getId());
		String issueTypeId = getIssueTypeId(decisionElement.getType());
		issueInputParameters.setIssueTypeId(issueTypeId);
		IssueService issueService = ComponentAccessor.getIssueService();

		IssueService.CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);
		if (result.getErrorCollection().hasAnyErrors()) {
			for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
				LOGGER.error(entry.getKey() + ": " + entry.getValue());
			}
			return null;
		} else {
			IssueResult issueResult = issueService.create(user, result);
			Issue issue = issueResult.getIssue();
			decisionElement.setId(issue.getId());
			decisionElement.setKey(issue.getKey());
			return decisionElement;
		}
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement decisionElement, ApplicationUser user) {
		IssueService issueService = ComponentAccessor.getIssueService();

		IssueResult issueResult = issueService.getIssue(user, decisionElement.getId());
		MutableIssue issueToBeUpdated = issueResult.getIssue();
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		issueInputParameters.setSummary(decisionElement.getSummary());
		issueInputParameters.setDescription(decisionElement.getDescription());
		String issueTypeId = getIssueTypeId(decisionElement.getType());
		issueInputParameters.setIssueTypeId(issueTypeId);
		IssueService.UpdateValidationResult result = issueService.validateUpdate(user, issueToBeUpdated.getId(),
				issueInputParameters);
		if (result.getErrorCollection().hasAnyErrors()) {
			for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
				LOGGER.error(entry.getKey() + ": " + entry.getValue());
			}
			return false;
		} else {
			issueResult = issueService.update(user, result);
			return true;
		}
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement decisionElement, ApplicationUser user) {
		IssueService issueService = ComponentAccessor.getIssueService();
		IssueService.IssueResult issue = issueService.getIssue(user, decisionElement.getId());
		if (issue.isValid()) {
			IssueService.DeleteValidationResult result = issueService.validateDelete(user, issue.getIssue().getId());
			if (result.getErrorCollection().hasAnyErrors()) {
				for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
					LOGGER.error(entry.getKey() + ": " + entry.getValue());
				}
				return false;
			} else {
				ErrorCollection errorCollection = issueService.delete(user, result);
				if (!errorCollection.hasAnyErrors()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey) {
		if (projectKey == null) {
			return null;
		}
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		Collection<Long> issueIds;
		try {
			issueIds = issueManager.getIssueIdsForProject(project.getId());
		} catch (GenericEntityException e) {
			issueIds = new ArrayList<Long>();
		}
		List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<DecisionKnowledgeElement>();
		for (Long issueId : issueIds) {
			Issue issue = issueManager.getIssueObject(issueId);

			KnowledgeType type = KnowledgeType.getKnowledgeType(issue.getIssueType().getName());
			if (type != KnowledgeType.OTHER) {
				decisionKnowledgeElements.add(new DecisionKnowledgeElement(issue));
			}
		}
		return decisionKnowledgeElements;
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(String key) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueByCurrentKey(key);
		return new DecisionKnowledgeElement(issue);
	}

	@Override
	public DecisionKnowledgeElement getDecisionKnowledgeElement(long id) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueObject(id);
		return new DecisionKnowledgeElement(issue);
	}

	@Override
	public List<IDecisionKnowledgeElement> getChildren(IDecisionKnowledgeElement decisionKnowledgeElement) {
		List<IssueLink> outwardIssueLinks = ComponentAccessor.getIssueLinkManager()
				.getOutwardLinks(decisionKnowledgeElement.getId());
		List<IDecisionKnowledgeElement> children = new ArrayList<IDecisionKnowledgeElement>();

		if(decisionKnowledgeElement.getType() != KnowledgeType.ARGUMENT) {
			for (IssueLink issueLink : outwardIssueLinks) {
				Issue outwardIssue = issueLink.getDestinationObject();
				if (outwardIssue != null) {
					DecisionKnowledgeElement outwardElement = new DecisionKnowledgeElement(outwardIssue);
					if (outwardElement.getType() != KnowledgeType.ARGUMENT) {
						children.add(outwardElement);
					}
				}
			}
		}

        List<IssueLink> inwardIssueLinks = ComponentAccessor.getIssueLinkManager()
                .getInwardLinks(decisionKnowledgeElement.getId());
        for (IssueLink issueLink : inwardIssueLinks) {
            Issue inwardIssue = issueLink.getSourceObject();
            if (inwardIssue != null) {
                DecisionKnowledgeElement inwardElement = new DecisionKnowledgeElement(inwardIssue);
                if(inwardElement.getType() == KnowledgeType.ARGUMENT) {
                    children.add(new DecisionKnowledgeElement(inwardIssue));
                }
            }
        }

		return children;
	}

	@Override
	public List<DecisionKnowledgeElement> getParents(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<IssueLink> inwardIssueLinks = ComponentAccessor.getIssueLinkManager()
				.getInwardLinks(decisionKnowledgeElement.getId());
		List<DecisionKnowledgeElement> parents = new ArrayList<DecisionKnowledgeElement>();
		for (IssueLink issueLink : inwardIssueLinks) {
			Issue inwardIssue = issueLink.getDestinationObject();
			if (inwardIssue != null) {
				parents.add(new DecisionKnowledgeElement(inwardIssue));
			}
		}
		return parents;
	}

	@Override
	public long insertLink(Link link, ApplicationUser user) {
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypeCollection = issueLinkTypeManager
				.getIssueLinkTypesByName(link.getLinkType());
		Iterator<IssueLinkType> issueLinkTypeIterator = issueLinkTypeCollection.iterator();
		long typeId = 0;
		while (issueLinkTypeIterator.hasNext()) {
			IssueLinkType issueLinkType = issueLinkTypeIterator.next();
			typeId = issueLinkType.getId();
		}
		long sequence = 0;
		List<IssueLink> inwardIssueLinkList = issueLinkManager.getInwardLinks(link.getIngoingId());
		List<IssueLink> outwardIssueLinkList = issueLinkManager.getOutwardLinks(link.getIngoingId());
		for (IssueLink issueLink : inwardIssueLinkList) {
			if (sequence <= issueLink.getSequence()) {
				sequence = issueLink.getSequence() + 1;
			}
		}
		for (IssueLink issueLink : outwardIssueLinkList) {
			if (sequence <= issueLink.getSequence()) {
				sequence = issueLink.getSequence() + 1;
			}
		}
		try {
			issueLinkManager.createIssueLink(link.getOutgoingId(), link.getIngoingId(), typeId, sequence, user);
		} catch (CreateException e) {
			LOGGER.error("CreateException");
			return (long) 0;
		} catch (NullPointerException e) {
			LOGGER.error("NullPointerException");
			return (long) 0;
		} finally {
			outwardIssueLinkList = issueLinkManager.getOutwardLinks(link.getIngoingId());
			issueLinkManager.resetSequences(outwardIssueLinkList);
			inwardIssueLinkList = issueLinkManager.getInwardLinks(link.getIngoingId());
			issueLinkManager.resetSequences(inwardIssueLinkList);
		}
		IssueLink issueLink = issueLinkManager.getIssueLink(link.getOutgoingId(), link.getIngoingId(), typeId);
		if (issueLink == null) {
			LOGGER.error("issueLink == null");
			return (long) 0;
		}
		return issueLink.getId();
	}

	@Override
	public boolean deleteLink(Link link, ApplicationUser user) {
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypeCollection = issueLinkTypeManager
				.getIssueLinkTypesByName(link.getLinkType());
		Iterator<IssueLinkType> issueLinkTypeIterator = issueLinkTypeCollection.iterator();
		long typeId = 0;
		while (issueLinkTypeIterator.hasNext()) {
			IssueLinkType issueLinkType = issueLinkTypeIterator.next();
			typeId = issueLinkType.getId();
		}
		IssueLink issueLink = issueLinkManager.getIssueLink(link.getOutgoingId(), link.getIngoingId(), typeId);
		if(issueLink !=null){
			issueLinkManager.removeIssueLink(issueLink,user);
			return true;
		}

		return false;
	}

	@Override
	public List<Link> getInwardLinks(IDecisionKnowledgeElement decisionKnowledgeElement) {
		List<IssueLink> inwardIssueLinks = ComponentAccessor.getIssueLinkManager()
				.getInwardLinks(decisionKnowledgeElement.getId());
		List<Link> inwardLinks = new ArrayList<Link>();
		for(IssueLink inwardIssueLink : inwardIssueLinks) {
			inwardLinks.add(new Link(inwardIssueLink));
		}
		return inwardLinks;
	}

	@Override
	public List<Link> getOutwardLinks(IDecisionKnowledgeElement decisionKnowledgeElement) {
		List<IssueLink> outwardIssueLinks = ComponentAccessor.getIssueLinkManager()
				.getInwardLinks(decisionKnowledgeElement.getId());
		List<Link> outwardLinks = new ArrayList<Link>();
		for(IssueLink inwardIssueLink : outwardIssueLinks) {
			outwardLinks.add(new Link(inwardIssueLink));
		}
		return outwardLinks;
	}

	private String getIssueTypeId(KnowledgeType type) {
		ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
		Collection<IssueType> listOfIssueTypes = constantsManager.getAllIssueTypeObjects();
		for (IssueType issueType : listOfIssueTypes) {
			if (issueType.getName().equalsIgnoreCase(type.toString())) {
				return issueType.getId();
			}
		}
		return "";
	}
}