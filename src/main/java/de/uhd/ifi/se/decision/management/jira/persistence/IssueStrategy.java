package de.uhd.ifi.se.decision.management.jira.persistence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAutoDetect;
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

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;

/**
 * Extends the abstract class AbstractPersistenceStrategy. Uses JIRA issues to
 * store decision knowledge.
 */
@JsonAutoDetect
public class IssueStrategy extends AbstractPersistenceStrategy {
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
				LOGGER.error("Insertion of decision knowledge element into database failed. " + entry.getKey() + ": "
						+ entry.getValue());
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
				LOGGER.error("Updating decision knowledge element in database failed. " + entry.getKey() + ": "
						+ entry.getValue());
			}
			return false;
		}
		issueService.update(user, result);
		return true;
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(DecisionKnowledgeElement decisionElement, ApplicationUser user) {
		IssueService issueService = ComponentAccessor.getIssueService();
		IssueService.IssueResult issue = issueService.getIssue(user, decisionElement.getId());
		if (issue.isValid()) {
			IssueService.DeleteValidationResult result = issueService.validateDelete(user, issue.getIssue().getId());
			if (result.getErrorCollection().hasAnyErrors()) {
				for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
					LOGGER.error("Delition of decision knowledge element in database failed. " + entry.getKey() + ": "
							+ entry.getValue());
				}
				return false;
			} else {
				ErrorCollection errorCollection = issueService.delete(user, result);
				return !errorCollection.hasAnyErrors();
			}
		}
		return false;
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements(String projectKey) {
		List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<DecisionKnowledgeElement>();
		if (projectKey == null) {
			return decisionKnowledgeElements;
		}
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(projectKey);
		Collection<Long> issueIds;
		try {
			issueIds = issueManager.getIssueIdsForProject(project.getId());
		} catch (GenericEntityException e) {
			issueIds = new ArrayList<Long>();
		} catch (NullPointerException e) {
			issueIds = new ArrayList<Long>();
		}

		for (Long issueId : issueIds) {
			Issue issue = issueManager.getIssueObject(issueId);

			KnowledgeType type = KnowledgeType.getKnowledgeType(issue.getIssueType().getName());
			if (type != KnowledgeType.OTHER) {
				decisionKnowledgeElements.add(new DecisionKnowledgeElementImpl(issue));
			}
		}
		return decisionKnowledgeElements;
	}

	@Override
	public DecisionKnowledgeElementImpl getDecisionKnowledgeElement(String key) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueByCurrentKey(key);
		return new DecisionKnowledgeElementImpl(issue);
	}

	@Override
	public DecisionKnowledgeElementImpl getDecisionKnowledgeElement(long id) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueObject(id);
		if (issue == null) {
			return null;
		}
		return new DecisionKnowledgeElementImpl(issue);
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> elementsLinkedWithOutwardLinks = new ArrayList<DecisionKnowledgeElement>();
		List<IssueLink> outwardIssueLinks = getOutwardIssueLinks(decisionKnowledgeElement);
		for (IssueLink issueLink : outwardIssueLinks) {
			Issue outwardIssue = issueLink.getDestinationObject();
			if (outwardIssue != null) {
				DecisionKnowledgeElementImpl outwardElement = new DecisionKnowledgeElementImpl(outwardIssue);
				elementsLinkedWithOutwardLinks.add(outwardElement);
			}
		}
		return elementsLinkedWithOutwardLinks;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> elementsLinkedWithInwardLinks = new ArrayList<DecisionKnowledgeElement>();
		List<IssueLink> inwardIssueLinks = getInwardIssueLinks(decisionKnowledgeElement);
		for (IssueLink issueLink : inwardIssueLinks) {
			Issue inwardIssue = issueLink.getSourceObject();
			if (inwardIssue != null) {
				DecisionKnowledgeElementImpl inwardElement = new DecisionKnowledgeElementImpl(inwardIssue);
				elementsLinkedWithInwardLinks.add(inwardElement);
			}
		}
		return elementsLinkedWithInwardLinks;
	}

	@Override
	public long insertLink(Link link, ApplicationUser user) {
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		long linkTypeId = getLinkTypeId(link.getLinkType());
		try {
			issueLinkManager.createIssueLink(link.getOutgoingId(), link.getIngoingId(), linkTypeId, (long) 0, user);
			IssueLink issueLink = issueLinkManager.getIssueLink(link.getOutgoingId(), link.getIngoingId(), linkTypeId);
			return issueLink.getId();
		} catch (CreateException | NullPointerException e) {
			LOGGER.error("Insertion of link into database failed.");
		}
		return (long) 0;
	}

	private long getLinkTypeId(String linkTypeName) {
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypeCollection = issueLinkTypeManager.getIssueLinkTypesByName(linkTypeName);
		Iterator<IssueLinkType> issueLinkTypeIterator = issueLinkTypeCollection.iterator();
		long typeId = 0;
		while (issueLinkTypeIterator.hasNext()) {
			IssueLinkType issueLinkType = issueLinkTypeIterator.next();
			typeId = issueLinkType.getId();
		}
		return typeId;
	}

	// TODO If the Update functions are also used in the IssueStrategy this function
	// needs to be Implemented
	@Override
	public boolean editLink(Link link, ApplicationUser user) {
		return false;
	}

	@Override
	public boolean deleteLink(Link link, ApplicationUser user) {
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypes = issueLinkTypeManager.getIssueLinkTypes();
		for (IssueLinkType linkType : issueLinkTypes) {
			long typeId = linkType.getId();
			IssueLink issueLink = issueLinkManager.getIssueLink(link.getOutgoingId(), link.getIngoingId(), typeId);
			if (issueLink != null) {
				issueLinkManager.removeIssueLink(issueLink, user);
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<IssueLink> inwardIssueLinks = getInwardIssueLinks(decisionKnowledgeElement);
		List<Link> inwardLinks = new ArrayList<Link>();
		for (IssueLink inwardIssueLink : inwardIssueLinks) {
			inwardLinks.add(new LinkImpl(inwardIssueLink));
		}
		return inwardLinks;
	}

	public List<IssueLink> getInwardIssueLinks(DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null) {
			return new ArrayList<IssueLink>();
		}
		return ComponentAccessor.getIssueLinkManager().getInwardLinks(decisionKnowledgeElement.getId());
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement decisionKnowledgeElement) {
		List<IssueLink> outwardIssueLinks = getOutwardIssueLinks(decisionKnowledgeElement);
		List<Link> outwardLinks = new ArrayList<Link>();
		for (IssueLink outwardIssueLink : outwardIssueLinks) {
			outwardLinks.add(new LinkImpl(outwardIssueLink));
		}
		return outwardLinks;
	}

	public List<IssueLink> getOutwardIssueLinks(DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null) {
			return new ArrayList<IssueLink>();
		}
		return ComponentAccessor.getIssueLinkManager().getOutwardLinks(decisionKnowledgeElement.getId());
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