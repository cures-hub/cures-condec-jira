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
import com.atlassian.jira.bc.issue.IssueService.CreateValidationResult;
import com.atlassian.jira.bc.issue.IssueService.IssueResult;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.ConstantsManager;
import com.atlassian.jira.event.type.EventDispatchOption;
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
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.impl.LinkImpl;

/**
 * Extends the abstract class AbstractPersistenceStrategy. Uses JIRA issues to
 * store decision knowledge.
 *
 * @see AbstractPersistenceManager
 */
@JsonAutoDetect
public class JiraIssuePersistenceManager extends AbstractPersistenceManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssuePersistenceManager.class);

	public JiraIssuePersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.JIRAISSUE;
	}

	public static boolean deleteLink(Link link, ApplicationUser user) {
		if (link == null || user == null) {
			return false;
		}
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypes = issueLinkTypeManager.getIssueLinkTypes();
		for (IssueLinkType linkType : issueLinkTypes) {
			long typeId = linkType.getId();
			IssueLink issueLink = issueLinkManager.getIssueLink(link.getDestinationElement().getId(),
					link.getSourceElement().getId(), typeId);
			if (issueLink != null) {
				issueLinkManager.removeIssueLink(issueLink, user);
				return true;
			}
		}

		LOGGER.error("Deletion of link in database failed.");
		return false;
	}

	public static List<IssueLink> getInwardIssueLinks(DecisionKnowledgeElement element) {
		if (element == null) {
			return new ArrayList<IssueLink>();
		}
		return ComponentAccessor.getIssueLinkManager().getInwardLinks(element.getId());
	}

	public static String getIssueTypeId(KnowledgeType type) {
		ConstantsManager constantsManager = ComponentAccessor.getConstantsManager();
		Collection<IssueType> listOfIssueTypes = constantsManager.getAllIssueTypeObjects();
		for (IssueType issueType : listOfIssueTypes) {
			if (issueType.getName().equalsIgnoreCase(type.toString())) {
				return issueType.getId();
			}
		}
		return "";
	}

	public static long getLinkTypeId(String linkTypeName) {
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

	public static List<IssueLink> getOutwardIssueLinks(DecisionKnowledgeElement element) {
		if (element == null) {
			return new ArrayList<IssueLink>();
		}
		return ComponentAccessor.getIssueLinkManager().getOutwardLinks(element.getId());
	}

	public static long insertLink(Link link, ApplicationUser user) {
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		long linkTypeId = getLinkTypeId(link.getType());
		try {
			issueLinkManager.createIssueLink(link.getSourceElement().getId(), link.getDestinationElement().getId(),
					linkTypeId, (long) 0, user);
			IssueLink issueLink = issueLinkManager.getIssueLink(link.getSourceElement().getId(),
					link.getDestinationElement().getId(), linkTypeId);
			return issueLink.getId();
		} catch (CreateException | NullPointerException e) {
			LOGGER.error("Insertion of link into database failed.");
		}
		return 0;
	}

	private static void setParameters(DecisionKnowledgeElement element, IssueInputParameters issueInputParameters) {
		String summary = element.getSummary();
		if (summary != null) {
			if (summary.length() > 255) {
				summary = summary.substring(0, 254);
			}
			issueInputParameters.setSummary(summary);
		}
		String description = element.getDescription();
		if (description != null) {
			issueInputParameters.setDescription(description);
		}
		String issueTypeId = getIssueTypeId(element.getType().replaceProAndConWithArgument());
		issueInputParameters.setIssueTypeId(issueTypeId);
	}

	@Override
	public boolean deleteDecisionKnowledgeElement(long id, ApplicationUser user) {
		IssueService issueService = ComponentAccessor.getIssueService();
		IssueService.IssueResult issue = issueService.getIssue(user, id);
		if (issue.isValid()) {
			IssueService.DeleteValidationResult result = issueService.validateDelete(user, issue.getIssue().getId());
			if (result.getErrorCollection().hasAnyErrors()) {
				for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
					LOGGER.error("Deletion of decision knowledge element in database failed. " + entry.getKey() + ": "
							+ entry.getValue());
				}
				return false;
			}
			ErrorCollection errorCollection = issueService.delete(user, result);
			return !errorCollection.hasAnyErrors();
		}
		return false;
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
	public DecisionKnowledgeElementImpl getDecisionKnowledgeElement(String key) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueByCurrentKey(key);
		return new DecisionKnowledgeElementImpl(issue);
	}

	@Override
	public List<DecisionKnowledgeElement> getDecisionKnowledgeElements() {
		List<DecisionKnowledgeElement> decisionKnowledgeElements = new ArrayList<DecisionKnowledgeElement>();
		if (this.projectKey == null) {
			return decisionKnowledgeElements;
		}
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(this.projectKey);
		Collection<Long> issueIds;
		try {
			issueIds = issueManager.getIssueIdsForProject(project.getId());
		} catch (GenericEntityException | NullPointerException e) {
			issueIds = new ArrayList<Long>();
		}

		for (long issueId : issueIds) {
			Issue issue = issueManager.getIssueObject(issueId);

			KnowledgeType type = KnowledgeType.getKnowledgeType(issue.getIssueType().getName());
			if (type != KnowledgeType.OTHER) {
				decisionKnowledgeElements.add(new DecisionKnowledgeElementImpl(issue));
			}
		}
		return decisionKnowledgeElements;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithInwardLinks(DecisionKnowledgeElement element) {
		if (element == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> elementsLinkedWithInwardLinks = new ArrayList<DecisionKnowledgeElement>();
		List<IssueLink> inwardIssueLinks = getInwardIssueLinks(element);
		for (IssueLink issueLink : inwardIssueLinks) {
			Issue inwardIssue = issueLink.getSourceObject();
			if (inwardIssue != null) {
				DecisionKnowledgeElement inwardElement = new DecisionKnowledgeElementImpl(inwardIssue);
				elementsLinkedWithInwardLinks.add(inwardElement);
			}
		}
		return elementsLinkedWithInwardLinks;
	}

	@Override
	public List<DecisionKnowledgeElement> getElementsLinkedWithOutwardLinks(DecisionKnowledgeElement element) {
		if (element == null) {
			return new ArrayList<DecisionKnowledgeElement>();
		}
		List<DecisionKnowledgeElement> elementsLinkedWithOutwardLinks = new ArrayList<DecisionKnowledgeElement>();
		List<IssueLink> outwardIssueLinks = getOutwardIssueLinks(element);
		for (IssueLink issueLink : outwardIssueLinks) {
			Issue outwardIssue = issueLink.getDestinationObject();
			if (outwardIssue != null) {
				DecisionKnowledgeElement outwardElement = new DecisionKnowledgeElementImpl(outwardIssue);
				elementsLinkedWithOutwardLinks.add(outwardElement);
			}
		}
		return elementsLinkedWithOutwardLinks;
	}

	@Override
	public List<Link> getInwardLinks(DecisionKnowledgeElement element) {
		List<IssueLink> inwardIssueLinks = getInwardIssueLinks(element);
		List<Link> inwardLinks = new ArrayList<Link>();
		for (IssueLink inwardIssueLink : inwardIssueLinks) {
			Link link = new LinkImpl(inwardIssueLink);
			if (link.isValid()) {
				inwardLinks.add(link);
			}
		}
		return inwardLinks;
	}

	@Override
	public List<Link> getOutwardLinks(DecisionKnowledgeElement element) {
		List<IssueLink> outwardIssueLinks = getOutwardIssueLinks(element);
		List<Link> outwardLinks = new ArrayList<Link>();
		for (IssueLink outwardIssueLink : outwardIssueLinks) {
			outwardLinks.add(new LinkImpl(outwardIssueLink));
		}
		return outwardLinks;
	}

	@Override
	public DecisionKnowledgeElement insertDecisionKnowledgeElement(DecisionKnowledgeElement element,
			ApplicationUser user) {
		IssueInputParameters issueInputParameters = ComponentAccessor.getIssueService().newIssueInputParameters();

		setParameters(element, issueInputParameters);
		issueInputParameters.setReporterId(user.getName());
		Project project = ComponentAccessor.getProjectManager()
				.getProjectByCurrentKey(element.getProject().getProjectKey());
		issueInputParameters.setProjectId(project.getId());

		IssueService issueService = ComponentAccessor.getIssueService();
		CreateValidationResult result = issueService.validateCreate(user, issueInputParameters);
		if (result.getErrorCollection().hasAnyErrors()) {
			for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
				LOGGER.error("Insertion of decision knowledge element into database failed. " + entry.getKey() + ": "
						+ entry.getValue());
			}
			return null;
		}
		IssueResult issueResult = issueService.create(user, result);
		Issue issue = issueResult.getIssue();
		element.setId(issue.getId());
		element.setKey(issue.getKey());
		return element;
	}

	@Override
	public boolean updateDecisionKnowledgeElement(DecisionKnowledgeElement element, ApplicationUser user) {
		IssueService issueService = ComponentAccessor.getIssueService();
		IssueResult issueResult = issueService.getIssue(user, element.getId());
		MutableIssue issueToBeUpdated = issueResult.getIssue();
		IssueInputParameters issueInputParameters = issueService.newIssueInputParameters();
		setParameters(element, issueInputParameters);
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
	
	public static void updateJiraIssue(Issue jiraIssue, ApplicationUser user) {
		ComponentAccessor.getIssueManager().updateIssue(user, (MutableIssue) jiraIssue, EventDispatchOption.ISSUE_UPDATED, true);
	}
}