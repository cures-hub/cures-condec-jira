package de.uhd.ifi.se.decision.management.jira.rest.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.rest.KnowledgeRest;

/**
 * REST resource: Enables creation, editing, and deletion of decision knowledge
 * elements and their links
 */
@Path("/knowledge")
public class KnowledgeRestImpl implements KnowledgeRest {

	@Override
	@Path("/getDecisionKnowledgeElement")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getDecisionKnowledgeElement(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey,
												@QueryParam("documentationLocation") String documentationLocationIdentifier) {
		if (projectKey == null || id <= 0) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Decision knowledge element could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
		KnowledgeElement decisionKnowledgeElement = persistenceManager.getKnowledgeElement(id,
				documentationLocationIdentifier);
		if (decisionKnowledgeElement != null) {
			return Response.status(Status.OK).entity(decisionKnowledgeElement).build();
		}
		return Response.status(Status.NOT_FOUND)
				.entity(ImmutableMap.of("error", "Decision knowledge element was not found for the given id.")).build();
	}

	@Override
	@Path("/getUnlinkedElements")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getUnlinkedElements(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey,
										@QueryParam("documentationLocation") String documentationLocation) {
		if (projectKey == null || id <= 0) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Unlinked decision knowledge elements could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
		KnowledgeElement element = persistenceManager.getKnowledgeElement(id, documentationLocation);
		List<KnowledgeElement> unlinkedDecisionKnowledgeElements = KnowledgeGraph.getOrCreate(projectKey)
				.getUnlinkedElements(element);
		return Response.ok(unlinkedDecisionKnowledgeElements).build();
	}

	@Override
	@Path("/createDecisionKnowledgeElement")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response createDecisionKnowledgeElement(@Context HttpServletRequest request, KnowledgeElement element,
												   @QueryParam("idOfExistingElement") long idOfExistingElement,
												   @QueryParam("documentationLocationOfExistingElement") String documentationLocationOfExistingElement,
												   @QueryParam("keyOfExistingElement") String keyOfExistingElement) {
		if (element == null || request == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Creation of decision knowledge element failed due to a bad request (element or request is null)."))
					.build();
		}

		if (idOfExistingElement == 0 && keyOfExistingElement != null && !keyOfExistingElement.isBlank()) {
			IssueManager issueManager = ComponentAccessor.getIssueManager();
			Issue issue = issueManager.getIssueByCurrentKey(keyOfExistingElement);
			idOfExistingElement = issue.getId();
		}

		String projectKey = element.getProject().getProjectKey();
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);

		ApplicationUser user = AuthenticationManager.getUser(request);

		KnowledgeElement existingElement = persistenceManager.getKnowledgeElement(idOfExistingElement,
				documentationLocationOfExistingElement);
		KnowledgeElement newElementWithId = persistenceManager.insertKnowledgeElement(element, user,
				existingElement);

		if (newElementWithId == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of decision knowledge element failed.")).build();
		}

		if (idOfExistingElement == 0 || existingElement == null) {
			return Response.status(Status.OK).entity(newElementWithId).build();
		}
		Link link = Link.instantiateDirectedLink(existingElement, newElementWithId);
		if (link.getSource() == null || link.getTarget() == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
		}
		persistenceManager.updateIssueStatus(existingElement, newElementWithId, user);
		long linkId = persistenceManager.insertLink(link, user);
		if (linkId == 0) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
		}

		return Response.status(Status.OK).entity(newElementWithId).build();
	}

	@Override
	@Path("/updateDecisionKnowledgeElement")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response updateDecisionKnowledgeElement(@Context HttpServletRequest request, KnowledgeElement element,
												   @QueryParam("idOfParentElement") long idOfParentElement,
												   @QueryParam("documentationLocationOfParentElement") String documentationLocationOfParentElement) {
		if (request == null || element == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Element could not be updated due to a bad request.")).build();
		}
		ApplicationUser user = AuthenticationManager.getUser(request);

		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(element.getProject());

		KnowledgeElement formerElement = persistenceManager.getKnowledgeElement(element.getId(),
				element.getDocumentationLocation());
		if (formerElement == null || formerElement.getId() <= 0) {
			return Response.status(Status.NOT_FOUND)
					.entity(ImmutableMap.of("error", "Decision knowledge element could not be found in database."))
					.build();
		}

		boolean isUpdated = persistenceManager.updateKnowledgeElement(element, user);

		if (!isUpdated) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Element could not be updated due to an internal server error."))
					.build();
		}

		if (idOfParentElement == 0) {
			return Response.status(Status.OK).build();
		}
		KnowledgeElement updatedElement = persistenceManager.getKnowledgeElement(element.getId(),
				element.getDocumentationLocation());
		long linkId = persistenceManager.updateLink(updatedElement, formerElement.getType(), idOfParentElement,
				documentationLocationOfParentElement, user);
		if (linkId == 0) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Link could not be updated.")).build();
		}
		KnowledgeElement parentElement = persistenceManager
				.getManagerForSingleLocation(documentationLocationOfParentElement)
				.getKnowledgeElement(idOfParentElement);
		persistenceManager.updateIssueStatus(parentElement, updatedElement, user);
		return Response.status(Status.OK).build();
	}

	@Override
	@Path("/deleteDecisionKnowledgeElement")
	@DELETE
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteDecisionKnowledgeElement(@Context HttpServletRequest request,
												   KnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null || request == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();
		}
		String projectKey = decisionKnowledgeElement.getProject().getProjectKey();
		ApplicationUser user = AuthenticationManager.getUser(request);

		boolean isDeleted = KnowledgePersistenceManager.getOrCreate(projectKey)
				.deleteKnowledgeElement(decisionKnowledgeElement, user);
		if (isDeleted) {
			return Response.status(Status.OK).entity(true).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();
	}

	@Override
	@Path("/assignDecisionGroup")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response assignDecisionGroup(@Context HttpServletRequest request, @QueryParam("sourceId") long sourceId,
										@QueryParam("documentationLocation") String location, @QueryParam("level") String level,
										@QueryParam("existingGroups") String existingGroups, @QueryParam("addGroup") String addGroup,
										@QueryParam("projectKey") String projectKey) {
		List<String> groupsToAssign = new ArrayList<String>();
		groupsToAssign.add(level);
		if (!"".equals(existingGroups)) {
			String[] groupSplitArray = existingGroups.replace(" ", "").split(",");
			for (String group : groupSplitArray) {
				if (!groupsToAssign.contains(group)) {
					groupsToAssign.add(group);
				}
			}
		}
		if (!"".equals(addGroup)) {
			String[] groupSplitArray = addGroup.replace(" ", "").split(",");
			for (String group : groupSplitArray) {
				if (!groupsToAssign.contains(group)) {
					groupsToAssign.add(group);
				}
			}
		}
		KnowledgeElement element = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getKnowledgeElement(sourceId, location);
		DecisionGroupManager.setGroupAssignment(groupsToAssign, element);
		inheritGroupAssignent(groupsToAssign, element);

		return Response.status(Status.OK).build();
	}

	private void inheritGroupAssignent(List<String> groupsToAssign, KnowledgeElement element) {
		if (!element.getDocumentationLocation().getIdentifier().equals("c")) {
			List<KnowledgeElement> linkedElements = new ArrayList<KnowledgeElement>();
			for (Link link : element.getLinks()) {
				KnowledgeElement linkedElement = link.getOppositeElement(element);
				if (linkedElement != null && linkedElement.getDocumentationLocation().getIdentifier().equals("c")) {
					if (!linkedElement.getDecisionGroups().contains("Realization_Level")) {
						DecisionGroupManager.insertGroup("Realization_Level", linkedElement);
					}
					for (String group : groupsToAssign) {
						if (!("High_Level").equals(group) && !("Medium_Level").equals(group) && !("Realization_Level").equals(group)) {
							DecisionGroupManager.insertGroup(group, linkedElement);
						}

					}
				} else if (linkedElement != null) {
					linkedElements.add(linkedElement);
					if ((linkedElement.getTypeAsString().equals("Decision")
							|| linkedElement.getTypeAsString().equals("Alternative")
							|| linkedElement.getTypeAsString().equals("Issue")) && linkedElement.getLinks() != null) {
						List<Link> deeperLinks = linkedElement.getLinks();
						for (Link deeperLink : deeperLinks) {
							if (deeperLink != null && deeperLink.getTarget() != null && deeperLink.getSource() != null) {
								KnowledgeElement deeperElement = deeperLink.getOppositeElement(linkedElement);
								if (deeperElement != null && (deeperElement.getTypeAsString().equals("Pro") || deeperElement.getTypeAsString().equals("Con")
										|| deeperElement.getTypeAsString().equals("Decision")
										|| deeperElement.getTypeAsString().equals("Alternative"))) {
									linkedElements.add(deeperElement);
								}
							}
						}
					}
				}
			}
			for (KnowledgeElement ele : linkedElements) {
				DecisionGroupManager.setGroupAssignment(groupsToAssign, ele);
			}
		}
	}

	@Override
	@Path("/createLink")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response createLink(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
							   @QueryParam("knowledgeTypeOfChild") String knowledgeTypeOfChild, @QueryParam("idOfParent") long idOfParent,
							   @QueryParam("documentationLocationOfParent") String documentationLocationOfParent,
							   @QueryParam("idOfChild") long idOfChild,
							   @QueryParam("documentationLocationOfChild") String documentationLocationOfChild,
							   @QueryParam("linkTypeName") String linkTypeName) {
		if (request == null || projectKey == null || idOfChild <= 0 || idOfParent <= 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Link could not be created due to a bad request.")).build();
		}
		ApplicationUser user = AuthenticationManager.getUser(request);

		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);

		KnowledgeElement parentElement = persistenceManager.getKnowledgeElement(idOfParent,
				documentationLocationOfParent);
		KnowledgeElement childElement = persistenceManager.getKnowledgeElement(idOfChild,
				documentationLocationOfChild);

		if (parentElement == null || childElement == null) {
			return Response.status(Status.NOT_FOUND).build();
		}

		persistenceManager.updateIssueStatus(parentElement, childElement, user);

		Link link;
		if (linkTypeName == null) {
			childElement.setType(knowledgeTypeOfChild);
			link = Link.instantiateDirectedLink(parentElement, childElement);
		} else {
			LinkType linkType = LinkType.getLinkType(linkTypeName);
			link = Link.instantiateDirectedLink(parentElement, childElement, linkType);
		}
		long linkId = persistenceManager.insertLink(link, user);
		if (linkId == 0) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
		}
		return Response.status(Status.OK).entity(ImmutableMap.of("id", linkId)).build();
	}

	@Override
	@Path("/deleteLink")
	@DELETE
	@Produces({MediaType.APPLICATION_JSON})
	public Response deleteLink(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
							   Link link) {
		if (projectKey == null || request == null || link == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of link failed."))
					.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);
		boolean isDeleted = KnowledgePersistenceManager.getOrCreate(projectKey).deleteLink(link, user);

		if (isDeleted) {
			return Response.status(Status.OK).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "Deletion of link failed.")).build();
	}

	@Override
	@Path("/getElements")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getElements(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
								@DefaultValue("") @QueryParam("query") String query) {
		if (request == null || projectKey == null || projectKey.isBlank() || query == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Getting elements failed due to a bad request. You need to provide a project key."))
					.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);
		FilteringManager filteringManager = new FilteringManager(projectKey, user, query);
		List<KnowledgeElement> elementsMatchingQuery = filteringManager.getAllElementsMatchingFilterSettings();
		return Response.ok(elementsMatchingQuery).build();
	}

	@Override
	@Path("/createIssueFromSentence")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response createIssueFromSentence(@Context HttpServletRequest request,
											KnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null || request == null) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "The documentation location could not be changed due to a bad request."))
					.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);

		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager
				.getOrCreate(decisionKnowledgeElement.getProject().getProjectKey()).getJiraIssueTextManager();
		Issue issue = persistenceManager.createJiraIssueFromSentenceObject(decisionKnowledgeElement.getId(), user);

		if (issue != null) {
			return Response.status(Status.OK).entity(issue).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "The documentation location could not be changed.")).build();
	}

	@Override
	@Path("/setSentenceIrrelevant")
	@POST
	@Produces({MediaType.APPLICATION_JSON})
	public Response setSentenceIrrelevant(@Context HttpServletRequest request,
										  KnowledgeElement decisionKnowledgeElement) {
		if (request == null || decisionKnowledgeElement == null || decisionKnowledgeElement.getId() <= 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Setting element irrelevant failed due to a bad request."))
					.build();
		}
		String projectKey = decisionKnowledgeElement.getProject().getProjectKey();
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);

		if (decisionKnowledgeElement.getDocumentationLocation() != DocumentationLocation.JIRAISSUETEXT) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error", "Only sentence elements can be set to irrelevant.")).build();
		}
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) persistenceManager
				.getKnowledgeElement(decisionKnowledgeElement.getId(), DocumentationLocation.JIRAISSUETEXT);
		if (sentence == null) {
			return Response.status(Status.NOT_FOUND)
					.entity(ImmutableMap.of("error", "Element could not be found in database.")).build();
		}

		sentence.setRelevant(false);
		sentence.setType(KnowledgeType.OTHER);
		sentence.setSummary(null);
		boolean isUpdated = persistenceManager.updateKnowledgeElement(sentence, null);
		if (isUpdated) {
			GenericLinkManager.deleteLinksForElement(sentence.getId(), DocumentationLocation.JIRAISSUETEXT);
			persistenceManager.getJiraIssueTextManager().createLinksForNonLinkedElements(sentence.getJiraIssueId());
			return Response.status(Status.OK).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "Setting element irrelevant failed.")).build();
	}

	@Override
	@Path("/getSummarizedCode")
	@GET
	@Produces({MediaType.APPLICATION_JSON})
	public Response getSummarizedCode(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey,
									  @QueryParam("documentationLocation") String documentationLocation,
									  @QueryParam("probability") int probability) {
		if (projectKey == null || id <= 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Getting summarized code failed due to a bad request.")).build();
		}

		IssueManager issueManager = ComponentAccessor.getIssueManager();
		Issue jiraIssue = issueManager.getIssueObject(id);

		if (jiraIssue == null) {
			jiraIssue = KnowledgePersistenceManager.getOrCreate(projectKey).getJiraIssueTextManager().getJiraIssue(id);
		}

		if (!ConfigPersistenceManager.isKnowledgeExtractedFromGit(projectKey)) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error",
							"Getting summarized code failed since git extraction is disabled for this project."))
					.build();
		}

		String summary = new CodeSummarizer(projectKey).createSummary(jiraIssue, probability);
		if (summary == null || summary.isEmpty()) {
			summary = "This JIRA issue does not have any code committed.";
		}
		return Response.ok(summary).build();
	}
}
