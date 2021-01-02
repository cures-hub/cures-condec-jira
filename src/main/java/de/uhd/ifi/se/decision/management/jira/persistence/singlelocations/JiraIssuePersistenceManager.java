package de.uhd.ifi.se.decision.management.jira.persistence.singlelocations;

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
import com.atlassian.jira.bc.issue.IssueService.TransitionValidationResult;
import com.atlassian.jira.bc.issue.search.SearchService;
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
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.issue.status.Status;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.permission.ProjectPermissions;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.util.ErrorCollection;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.workflow.JiraWorkflow;
import com.atlassian.jira.workflow.WorkflowManager;
import com.atlassian.query.Query;
import com.opensymphony.workflow.loader.ActionDescriptor;
import com.opensymphony.workflow.loader.StepDescriptor;

import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeStatus;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;

/**
 * Extends the abstract class
 * {@link AbstractPersistenceManagerForSingleLocation}. Uses Jira issues to
 * store decision knowledge elements and links.
 *
 * @see AbstractPersistenceManagerForSingleLocation
 */
@JsonAutoDetect
public class JiraIssuePersistenceManager extends AbstractPersistenceManagerForSingleLocation {
	private static final Logger LOGGER = LoggerFactory.getLogger(JiraIssuePersistenceManager.class);

	public JiraIssuePersistenceManager(String projectKey) {
		this.projectKey = projectKey;
		this.documentationLocation = DocumentationLocation.JIRAISSUE;
	}

	/**
	 * Deletes an existing Jira {@link IssueLink} in database. The link needs to be
	 * between two Jira issues in the {@link KnowledgeGraph}, i.e., the link is a
	 * Jira {@link IssueLink}.
	 *
	 * @param link
	 *            link (=edge) between a source and a destination decision knowledge
	 *            element as a {@link Link} object. Needs to be a Jira
	 *            {@link IssueLink}.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return true if deletion was successful.
	 */
	public boolean deleteLink(Link link, ApplicationUser user) {
		if (link == null || user == null) {
			return false;
		}
		Project jiraProject = link.getSource().getProject().getJiraProject();
		PermissionManager permissionManager = ComponentAccessor.getComponent(PermissionManager.class);
		if (!permissionManager.hasPermission(ProjectPermissions.LINK_ISSUES, jiraProject, user)) {
			return false;
		}
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		IssueLinkTypeManager issueLinkTypeManager = ComponentAccessor.getComponent(IssueLinkTypeManager.class);
		Collection<IssueLinkType> issueLinkTypes = issueLinkTypeManager.getIssueLinkTypes();
		for (IssueLinkType linkType : issueLinkTypes) {
			long typeId = linkType.getId();
			IssueLink issueLink = issueLinkManager.getIssueLink(link.getTarget().getId(), link.getSource().getId(),
					typeId);
			if (issueLink != null) {
				issueLinkManager.removeIssueLink(issueLink, user);
				KnowledgeGraph.getOrCreate(projectKey).removeEdge(link);
				return true;
			}
		}

		LOGGER.error("Deletion of link in database failed.");
		return false;
	}

	public static List<IssueLink> getInwardIssueLinks(KnowledgeElement element) {
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

	public static List<IssueLink> getOutwardIssueLinks(KnowledgeElement element) {
		if (element == null) {
			return new ArrayList<IssueLink>();
		}
		List<IssueLink> links = ComponentAccessor.getIssueLinkManager().getOutwardLinks(element.getId());
		return links;
	}

	/**
	 * Inserts a new link into database. The link can be between any kinds of nodes
	 * in the {@link KnowledgeGraph}. The link needs to be between two Jira issues
	 * in the {@link KnowledgeGraph}, i.e., the link is a Jira {@link IssueLink}.
	 *
	 * @param link
	 *            link (=edge) between a source and a destination decision knowledge
	 *            element as a {@link Link} object. Needs to be a Jira
	 *            {@link IssueLink}.
	 * @param user
	 *            authenticated Jira {@link ApplicationUser}.
	 * @return internal database id of inserted link, zero if insertion failed.
	 */
	public long insertLink(Link link, ApplicationUser user) {
		PermissionManager permissionManager = ComponentAccessor.getComponent(PermissionManager.class);
		if (user == null || !permissionManager.hasPermission(ProjectPermissions.LINK_ISSUES,
				link.getSource().getProject().getJiraProject(), user)) {
			return 0;
		}
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		long linkTypeId = getLinkTypeId(link.getType().getName());
		try {
			issueLinkManager.createIssueLink(link.getSource().getId(), link.getTarget().getId(), linkTypeId, (long) 0,
					user);
			IssueLink issueLink = issueLinkManager.getIssueLink(link.getSource().getId(), link.getTarget().getId(),
					linkTypeId);
			return issueLink.getId();
		} catch (CreateException e) {
			LOGGER.error("Insertion of link into database failed. Message: " + e.getMessage());
		}
		return 0;
	}

	/**
	 * @param link
	 *            {@link Link} object.
	 * @return database id of a link object if it is a Jira issue link. Returns a
	 *         value <= 0 if the link is not existing in the database.
	 * @see IssueLink
	 */
	public static long getLinkId(Link link) {
		IssueLinkManager issueLinkManager = ComponentAccessor.getIssueLinkManager();
		try {
			long linkTypeId = getLinkTypeId(link.getTypeAsString());
			IssueLink issueLink = issueLinkManager.getIssueLink(link.getSource().getId(), link.getTarget().getId(),
					linkTypeId);
			return issueLink.getId();
		} catch (NullPointerException e) {
			LOGGER.error("Id of link in database could not be retrieved. Message: " + e.getMessage());
		}
		return 0;
	}

	private static void setParameters(KnowledgeElement element, IssueInputParameters issueInputParameters) {
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
	public boolean deleteKnowledgeElement(long id, ApplicationUser user) {
		IssueService issueService = ComponentAccessor.getIssueService();
		IssueService.IssueResult issue = issueService.getIssue(user, id);
		if (issue.isValid() && issue.getIssue() != null) {
			IssueService.DeleteValidationResult result = issueService.validateDelete(user, issue.getIssue().getId());
			if (result.getErrorCollection().hasAnyErrors()) {
				for (Map.Entry<String, String> entry : result.getErrorCollection().getErrors().entrySet()) {
					LOGGER.error("Deletion of decision knowledge element in database failed. " + entry.getKey() + ": "
							+ entry.getValue());
				}
				return false;
			}
			ErrorCollection errorCollection = issueService.delete(user, result);
			if (!errorCollection.hasAnyErrors()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public KnowledgeElement getKnowledgeElement(long id) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueObject(id);
		if (issue == null) {
			return null;
		}
		return new KnowledgeElement(issue);
	}

	@Override
	public KnowledgeElement getKnowledgeElement(String key) {
		Issue issue = getJiraIssue(key);
		return new KnowledgeElement(issue);
	}

	public static Issue getJiraIssue(String key) {
		if (key == null || key.isBlank()) {
			return null;
		}
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		return issueManager.getIssueByCurrentKey(key);
	}

	@Override
	public List<KnowledgeElement> getKnowledgeElements() {
		List<KnowledgeElement> knowledgeElements = new ArrayList<KnowledgeElement>();
		if (this.projectKey == null) {
			return knowledgeElements;
		}
		for (Issue issue : getIssueIdCollection()) {
			knowledgeElements.add(new KnowledgeElement(issue));
		}
		return knowledgeElements;
	}

	@Override
	public List<Link> getInwardLinks(KnowledgeElement element) {
		List<IssueLink> inwardIssueLinks = getInwardIssueLinks(element);
		List<Link> inwardLinks = new ArrayList<Link>();
		for (IssueLink inwardIssueLink : inwardIssueLinks) {
			Link link = new Link(inwardIssueLink);
			if (link.isValid()) {
				inwardLinks.add(link);
			}
		}
		return inwardLinks;
	}

	@Override
	public List<Link> getOutwardLinks(KnowledgeElement element) {
		List<IssueLink> outwardIssueLinks = getOutwardIssueLinks(element);
		List<Link> outwardLinks = new ArrayList<Link>();
		for (IssueLink outwardIssueLink : outwardIssueLinks) {
			Link link = new Link(outwardIssueLink);
			if (link.isValid()) {
				outwardLinks.add(link);
			}
		}
		return outwardLinks;
	}

	@Override
	public KnowledgeElement insertKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
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
	public boolean updateKnowledgeElement(KnowledgeElement element, ApplicationUser user) {
		IssueService issueService = ComponentAccessor.getIssueService();
		IssueResult issueResult = issueService.getIssue(user, element.getId());
		MutableIssue issueToBeUpdated = issueResult.getIssue();
		if (issueToBeUpdated == null) {
			return false;
		}
		KnowledgeElement formerElement = new KnowledgeElement(issueToBeUpdated);
		// for decision -> alternative stays decision
		formerElement.setType(element.getType());
		element.setType(KnowledgeType.OTHER);
		element.setType(formerElement.getType());
		element.setStatus(formerElement.getStatus());

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
		return updateStatus(element.getStatus(), issueToBeUpdated, user, issueService);
	}

	@Override
	public ApplicationUser getCreator(KnowledgeElement element) {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue issue = issueManager.getIssueByCurrentKey(element.getKey());
		if (issue == null) {
			return ComponentAccessor.getUserManager().getUserByNameEvenWhenUnknown("Unknown User");
		}
		return issue.getReporterUser();
	}

	public static void updateJiraIssue(Issue jiraIssue, ApplicationUser user) {
		ComponentAccessor.getIssueManager().updateIssue(user, (MutableIssue) jiraIssue,
				EventDispatchOption.ISSUE_UPDATED, true);
	}

	public static void updateDescription(Issue jiraIssue, String descriptionText, ApplicationUser user) {
		((MutableIssue) jiraIssue).setDescription(descriptionText);
		updateJiraIssue(jiraIssue, user);
	}

	private List<Issue> getIssueIdCollection() {
		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Project project = ComponentAccessor.getProjectManager().getProjectByCurrentKey(this.projectKey);
		Collection<Long> issueIds;
		try {
			issueIds = issueManager.getIssueIdsForProject(project.getId());
		} catch (GenericEntityException | NullPointerException e) {
			issueIds = new ArrayList<Long>();
			LOGGER.error("Get decisionknowledgeelemtns failed. Message: " + e.getMessage());
		}
		List<Issue> issueList = new ArrayList<>();
		for (long issueId : issueIds) {
			Issue issue = issueManager.getIssueObject(issueId);
			issueList.add(issue);
		}
		return issueList;
	}

	private boolean updateStatus(KnowledgeStatus newStatus, MutableIssue issueToBeUpdated, ApplicationUser user,
			IssueService issueService) {
		boolean isStatusUpdated = true;
		WorkflowManager workflowManager = ComponentAccessor.getComponent(WorkflowManager.class);
		JiraWorkflow workFlow = workflowManager.getWorkflow(issueToBeUpdated);
		Status status = issueToBeUpdated.getStatus();
		try {
			StepDescriptor currentStep = workFlow.getLinkedStep(status);
			@SuppressWarnings("unchecked")
			List<ActionDescriptor> possibleActionsList = currentStep.getActions();

			int actionId = 0;
			for (ActionDescriptor actionDescriptor : possibleActionsList) {
				if (actionDescriptor.getName().equalsIgnoreCase("set " + newStatus.toString())) {
					actionId = actionDescriptor.getId();
				}
			}

			TransitionValidationResult transitionValidationResult = issueService.validateTransition(user,
					issueToBeUpdated.getId(), actionId, issueService.newIssueInputParameters());
			if (transitionValidationResult.isValid()) {
				issueService.transition(user, transitionValidationResult);
			}
		} catch (Exception e) {
			LOGGER.error("Updating decision knowledge element in database failed. " + e.getLocalizedMessage());
			isStatusUpdated = false;
		}
		return isStatusUpdated;
	}

	public static List<Issue> getAllJiraIssuesForProject(ApplicationUser user, String projectKey) {
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		Query query = jqlClauseBuilder.project(projectKey).buildQuery();
		return getIssuesMatchingQuery(user, query);
	}

	public static List<Issue> getAllJiraIssuesForProjectAndType(ApplicationUser user, String projectKey,
			IssueType jiraIssueType) {
		JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
		Query query = jqlClauseBuilder.project(projectKey).and().issueType(jiraIssueType.getName()).buildQuery();
		return getIssuesMatchingQuery(user, query);
	}

	private static List<Issue> getIssuesMatchingQuery(ApplicationUser user, Query query) {
		SearchResults<Issue> searchResult = null;
		try {
			SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);
			searchResult = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
		} catch (SearchException e) {
			LOGGER.error("Getting all Jira issues for this project failed. Message: " + e.getMessage());
		}
		return searchResult.getResults();
	}
}