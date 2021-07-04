package de.uhd.ifi.se.decision.management.jira.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;
import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * REST resource: Enables creation, editing, and deletion of decision knowledge
 * elements and their links
 */
@Path("/knowledge")
public class KnowledgeRest {

	private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeRest.class);

	@Path("/getDecisionKnowledgeElement")
	@GET
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
			LOGGER.info(decisionKnowledgeElement.getKey() + " was retrieved.");
			return Response.ok().entity(decisionKnowledgeElement).build();
		}
		return Response.status(Status.NOT_FOUND)
				.entity(ImmutableMap.of("error", "Decision knowledge element was not found for the given id.")).build();
	}

	/**
	 * TODO Sorting according to the likelihood that they should be linked.
	 *
	 * @param id
	 *            of the {@link KnowledgeElement} in database.
	 * @param documentationLocation
	 *            of the {@link KnowledgeElement}, e.g. "i" for Jira issue, see
	 *            {@link DocumentationLocation}.
	 * @param projectKey
	 *            of the Jira project, see {@link DecisionKnowledgeProject}.
	 * @return list of unlinked elements of the knowledge element for a project.
	 *         Sorts the elements according to their similarity and their likelihood
	 *         that they should be linked.
	 * @issue How can the sorting be implemented?
	 */
	@Path("/getUnlinkedElements")
	@GET
	public Response getUnlinkedElements(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey,
			@QueryParam("documentationLocation") String documentationLocation) {
		if (projectKey == null || id <= 0) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Unlinked decision knowledge elements could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);
		KnowledgeElement element = persistenceManager.getKnowledgeElement(id, documentationLocation);
		if (element == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Unlinked decision knowledge elements could not be received since the knowledge element was not found."))
					.build();
		}
		List<KnowledgeElement> unlinkedDecisionKnowledgeElements = KnowledgeGraph.getInstance(projectKey)
				.getUnlinkedElements(element);
		LOGGER.info("Unlinked elements for " + element.getKey() + " were retrieved.");
		return Response.ok(unlinkedDecisionKnowledgeElements).build();
	}

	/**
	 * Creates a new {@link KnowledgeElement}. The decision knowledge element can
	 * either be documented as a separate Jira issue (documentation location "i") or
	 * in the description/a comment of an existing Jira issue (documentation
	 * location "s").
	 *
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param element
	 *            {@link KnowledgeElement} object with attributes, such as summary,
	 *            description (optional), {@link DocumentationLocation}, and
	 *            {@link KnowledgeType}.
	 * @param idOfExistingElement
	 *            optional parameter. Identifier of a parent element that the new
	 *            element should be linked with. Either the id or the key needs to
	 *            be passed, not both.
	 * @param documentationLocationOfExistingElement
	 *            optional parameter. Documentation location of a parent element
	 *            that the new element should be linked with.
	 * @param keyOfExistingElement
	 *            optional parameter. Key of a parent element that the new element
	 *            should be linked with. Either the id or the key needs to be
	 *            passed, not both.
	 * @return new {@link KnowledgeElement} with its internal database id set.
	 */
	@Path("/createDecisionKnowledgeElement")
	@POST
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
		KnowledgeElement newElementWithId = persistenceManager.insertKnowledgeElement(element, user, existingElement);

		if (newElementWithId == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error",
							"Creation of decision knowledge element failed. "
									+ "A reason might be that Jira issue persistence in project settings is disabled."))
					.build();
		}

		if (idOfExistingElement == 0 || existingElement == null) {
			return Response.status(Status.OK).entity(newElementWithId).build();
		}
		Link link = Link.instantiateDirectedLink(existingElement, newElementWithId);
		if (link.getSource() == null || link.getTarget() == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
		}
		long linkId = persistenceManager.insertLink(link, user);
		if (linkId == 0) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
		}
		LOGGER.info(newElementWithId.getKey() + " was created.");
		return Response.status(Status.OK).entity(newElementWithId).build();
	}

	@Path("/updateDecisionKnowledgeElement")
	@POST
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
		KnowledgeElement parentElement = persistenceManager.getKnowledgeElement(idOfParentElement,
				documentationLocationOfParentElement);
		long linkId = persistenceManager.updateLink(updatedElement, formerElement.getType(), parentElement, user);
		if (linkId == 0) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Link could not be updated.")).build();
		}
		LOGGER.info(updatedElement.getKey() + " was updated.");
		return Response.status(Status.OK).build();
	}

	@Path("/deleteDecisionKnowledgeElement")
	@DELETE
	public Response deleteDecisionKnowledgeElement(@Context HttpServletRequest request,
			KnowledgeElement knowledgeElement) {
		if (knowledgeElement == null || request == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();
		}
		String projectKey = knowledgeElement.getProject().getProjectKey();
		ApplicationUser user = AuthenticationManager.getUser(request);

		boolean isDeleted = KnowledgePersistenceManager.getOrCreate(projectKey).deleteKnowledgeElement(knowledgeElement,
				user);
		if (isDeleted) {
			return Response.status(Status.OK).entity(true).build();
		}
		LOGGER.info(knowledgeElement.getKey() + " was deleted.");
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();
	}

	@Path("/assignDecisionGroup")
	@POST
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
		KnowledgeElement element = KnowledgePersistenceManager.getOrCreate(projectKey).getKnowledgeElement(sourceId,
				location);
		DecisionGroupManager.setGroupAssignment(groupsToAssign, element);
		inheritGroupAssignment(groupsToAssign, element);

		return Response.status(Status.OK).build();
	}

	// TODO Simplify, this method is way too long and complex!
	private void inheritGroupAssignment(List<String> groupsToAssign, KnowledgeElement element) {
		if (element.getDocumentationLocation() != DocumentationLocation.CODE) {
			List<KnowledgeElement> linkedElements = new ArrayList<KnowledgeElement>();
			for (Link link : element.getLinks()) {
				KnowledgeElement linkedElement = link.getOppositeElement(element);
				if (linkedElement != null && linkedElement.getDocumentationLocation() == DocumentationLocation.CODE) {
					if (!linkedElement.getDecisionGroups().contains("Realization_Level")) {
						DecisionGroupManager.insertGroup("Realization_Level", linkedElement);
					}
					for (String group : groupsToAssign) {
						if (!("High_Level").equals(group) && !("Medium_Level").equals(group)
								&& !("Realization_Level").equals(group)) {
							DecisionGroupManager.insertGroup(group, linkedElement);
						}

					}
				} else if (linkedElement != null) {
					linkedElements.add(linkedElement);
					if ((linkedElement.getTypeAsString().equals("Decision")
							|| linkedElement.getTypeAsString().equals("Alternative")
							|| linkedElement.getTypeAsString().equals("Issue")) && linkedElement.getLinks() != null) {
						Set<Link> deeperLinks = linkedElement.getLinks();
						for (Link deeperLink : deeperLinks) {
							if (deeperLink != null && deeperLink.getTarget() != null
									&& deeperLink.getSource() != null) {
								KnowledgeElement deeperElement = deeperLink.getOppositeElement(linkedElement);
								if (deeperElement != null && (deeperElement.getTypeAsString().equals("Pro")
										|| deeperElement.getTypeAsString().equals("Con")
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

	@Path("/createLink")
	@POST
	public Response createLink(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("idOfParent") long idOfParent,
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
		KnowledgeElement childElement = persistenceManager.getKnowledgeElement(idOfChild, documentationLocationOfChild);

		if (parentElement == null || childElement == null) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Link could not be created since the elements to be linked could not be found.")).build();
		}

		Link existingLink = parentElement.getLink(childElement);
		if (existingLink != null) {
			persistenceManager.deleteLink(existingLink, user);
		}

		if (parentElement.equals(childElement)) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Link could not be created because of identical elements (self links forbidden).")).build();
		}

		Link link;
		if (linkTypeName == null || linkTypeName.equals("null")) {
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
		LOGGER.info("Link " + link + " was created.");
		return Response.status(Status.OK).entity(ImmutableMap.of("id", linkId)).build();
	}

	@Path("/deleteLink")
	@DELETE
	public Response deleteLink(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			Link link) {
		if (projectKey == null || request == null || link == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of link failed."))
					.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey);

		// to fill knowledge types
		link.setSourceElement(persistenceManager.getKnowledgeElement(link.getSource()));
		link.setDestinationElement(persistenceManager.getKnowledgeElement(link.getTarget()));

		boolean isDeleted = persistenceManager.deleteLink(link, user);

		if (isDeleted) {
			LOGGER.info("Link " + link + " was deleted.");
			return Response.status(Status.OK).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "Deletion of link failed.")).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param filterSettings
	 *            object of the {@link FilterSettings} class.
	 * @return list of all {@link KnowledgeElement}s that match the
	 *         {@link FilterSettings}.
	 */
	@Path("/knowledgeElements")
	@POST
	public Response getKnowledgeElements(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null || filterSettings.getProjectKey().isBlank()) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Getting elements failed due to a bad request. You need to provide the filter settings."))
					.build();
		}
		FilteringManager filteringManager = new FilteringManager(filterSettings);
		Set<KnowledgeElement> elementsMatchingQuery = filteringManager.getElementsMatchingFilterSettings();
		LOGGER.info("Knowledge elements were retrieved for filter settings: " + filterSettings.toString());
		return Response.ok(elementsMatchingQuery).build();
	}

	@Path("/createJiraIssueFromSentence")
	@POST
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

	@Path("/setSentenceIrrelevant")
	@POST
	public Response setSentenceIrrelevant(@Context HttpServletRequest request,
			KnowledgeElement decisionKnowledgeElement) {
		if (request == null || decisionKnowledgeElement == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Setting element irrelevant failed due to a bad request."))
					.build();
		}
		if (decisionKnowledgeElement.getDocumentationLocation() != DocumentationLocation.JIRAISSUETEXT) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error", "Only decision knowledge elements documented in the description "
							+ "or comments of a Jira issue can be set to irrelevant."))
					.build();
		}

		String projectKey = decisionKnowledgeElement.getProject().getProjectKey();
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
				.getJiraIssueTextManager();

		PartOfJiraIssueText sentence = (PartOfJiraIssueText) persistenceManager
				.getKnowledgeElement(decisionKnowledgeElement);
		if (sentence == null) {
			return Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Element could not be found in database.")).build();
		}

		sentence.setRelevant(false);
		sentence.setType(KnowledgeType.OTHER);
		persistenceManager.updateKnowledgeElement(sentence, AuthenticationManager.getUser(request));
		persistenceManager.createLinksForNonLinkedElements(sentence.getJiraIssue());
		return Response.status(Status.OK).build();
	}

	/**
	 * @param request                  HttpServletRequest with an authorized Jira
	 * @param decisionKnowledgeElement JSON object containing at least the id, documentation location
	 * @return {@link Status.OK} if setting the sentence validated was successful
	 * @issue How should setting a single element "validated" be handled?
	 * @alternative Change the API of updateDecisionKnowledgeElement to allow this attribute!
	 * @con This could be a breaking change
	 * @con This would make the code confusing
	 * @decision Make a new REST endpoint "setSentenceValidated"!
	 * @pro This would be backwards compatible
	 * @pro The code stays cleaner this way
	 * @con It might be confusing that this is documented as part of the SF: Change decision knowledge element, but not inside the function of the same name
	 */
	@Path("/setSentenceValidated")
	@POST
	public Response setSentenceValidated(@Context HttpServletRequest request,
										 KnowledgeElement decisionKnowledgeElement) {
		if (request == null || decisionKnowledgeElement == null) {
			return Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Setting element validated failed due to a bad request."))
				.build();
		}
		if (decisionKnowledgeElement.getDocumentationLocation() != DocumentationLocation.JIRAISSUETEXT) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
				.entity(ImmutableMap.of("error", "Only decision knowledge elements documented in the description "
					+ "or comments of a Jira issue can be set to validated."))
				.build();
		}

		String projectKey = decisionKnowledgeElement.getProject().getProjectKey();
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
			.getJiraIssueTextManager();
		PartOfJiraIssueText sentence = (PartOfJiraIssueText) persistenceManager
			.getKnowledgeElement(decisionKnowledgeElement);
		if (sentence == null) {
			return Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Element could not be found in database.")).build();
		}

		sentence.setValidated(true);
		persistenceManager.updateKnowledgeElement(sentence, AuthenticationManager.getUser(request));
		persistenceManager.createLinksForNonLinkedElements(sentence.getJiraIssue());
		return Response.status(Status.OK).build();
	}

	/**
	 * Rereads all decision knowledge elements documented within the description and
	 * comments of a Jira issue. For example, this might be useful if linkage
	 * between knowledge elements was destroyed.
	 *
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param jiraIssueId
	 *            of the {@link Issue} with decision knowledge elements documented
	 *            within its description and comments (e.g. a user story,
	 *            development task, ...).
	 * @return {@link Status.OK} if rereading was successful.
	 */
	@Path("/resetDecisionKnowledgeFromText")
	@POST
	public Response resetDecisionKnowledgeFromText(@Context HttpServletRequest request, Long jiraIssueId) {
		if (request == null || jiraIssueId == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
				"Resetting decision knowledge documented in the description and comments of a Jira issue failed due to a bad request."))
				.build();
		}
		Issue jiraIssue = ComponentAccessor.getIssueManager().getIssueObject(jiraIssueId);
		if (jiraIssue == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
				"Resetting decision knowledge documented in the description and comments of a Jira issue failed "
					+ "because the Jira issue could not be found."))
				.build();
		}
		String projectKey = jiraIssue.getProjectObject().getKey();
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getOrCreate(projectKey)
			.getJiraIssueTextManager();

		persistenceManager.deleteElementsInJiraIssue(jiraIssue);
		persistenceManager.updateElementsOfDescriptionInDatabase(jiraIssue);
		List<Comment> comments = ComponentAccessor.getCommentManager().getComments(jiraIssue);
		comments.forEach(comment -> persistenceManager.updateElementsOfCommentInDatabase(comment));

		List<KnowledgeElement> elements = persistenceManager.getElementsInJiraIssue(jiraIssue.getId());
		return Response.status(Status.OK).entity(elements.size()).build();
	}
}
