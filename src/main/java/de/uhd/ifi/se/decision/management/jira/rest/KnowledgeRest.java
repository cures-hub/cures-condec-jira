package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.IssueManager;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssueTextPersistenceManager;

/**
 * REST resource: Enables creation, editing, and deletion of decision knowledge
 * elements and their links
 */
@Path("/knowledge")
public class KnowledgeRest {

	private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgeRest.class);

	/**
	 * @param id
	 *            of the {@link KnowledgeElement} in database and knowledge graph.
	 * @param projectKey
	 *            of the Jira project.
	 * @param documentationLocationIdentifier
	 *            identifier of the {@link DocumentationLocation} of the element,
	 *            e.g., "i" for Jira issue.
	 * @return {@link KnowledgeElement} (e.g. decision problem, decision,
	 *         requirement, or code file) if it was found.
	 */
	@Path("/knowledgeElement")
	@GET
	public Response getKnowledgeElement(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey,
			@QueryParam("documentationLocation") String documentationLocationIdentifier) {
		if (projectKey == null || id == 0) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Knowledge element could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
		KnowledgeElement knowledgeElement;
		if (id < 0) {
			// element documented in code comment
			KnowledgeGraph graph = KnowledgeGraph.getInstance(projectKey);
			knowledgeElement = graph.getElementById(id);
		} else {
			KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey);
			knowledgeElement = persistenceManager.getKnowledgeElement(id, documentationLocationIdentifier);
		}
		if (knowledgeElement != null) {
			LOGGER.info(knowledgeElement.getKey() + " was retrieved.");
			return Response.ok(knowledgeElement).build();
		}
		return Response.status(Status.BAD_REQUEST)
				.entity(ImmutableMap.of("error", "Knowledge element was not found for the given id.")).build();
	}

	/**
	 * @param id
	 *            of the {@link KnowledgeElement} in database.
	 * @param documentationLocation
	 *            of the {@link KnowledgeElement}, e.g. "i" for Jira issue, see
	 *            {@link DocumentationLocation}.
	 * @param projectKey
	 *            of the Jira project, see {@link DecisionKnowledgeProject}.
	 * @return unsorted list of unlinked elements of the knowledge element for a
	 *         project.
	 * 
	 * @see LinkRecommendationRest#getLinkRecommendations(HttpServletRequest,
	 *      FilterSettings) to sort the elements according to their similarity and
	 *      their likelihood that they should be linked.
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
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey);
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
	@Path("/element/{idOfExistingElement}/{documentationLocationOfExistingElement}")
	@POST
	public Response createDecisionKnowledgeElement(@Context HttpServletRequest request, KnowledgeElement element,
			@PathParam("idOfExistingElement") long idOfExistingElement,
			@PathParam("documentationLocationOfExistingElement") String documentationLocationOfExistingElement,
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
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey);

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
			return Response.ok(newElementWithId).build();
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
		return Response.ok(newElementWithId).build();
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

		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(element.getProject());

		KnowledgeElement formerElement = persistenceManager.getKnowledgeElement(element.getId(),
				element.getDocumentationLocation());
		if (formerElement == null || formerElement.getId() <= 0) {
			return Response.status(Status.BAD_REQUEST)
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
			return Response.ok().build();
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
		return Response.ok().build();
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

		boolean isDeleted = KnowledgePersistenceManager.getInstance(projectKey).deleteKnowledgeElement(knowledgeElement,
				user);
		if (isDeleted) {
			return Response.ok().build();
		}
		LOGGER.info(knowledgeElement.getKey() + " was deleted.");
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project. Both, link source and target need to be within
	 *            the same project.
	 * @param idOfParent
	 *            id of the source/parent {@link KnowledgeElement}.
	 * @param documentationLocationOfParent
	 *            {@link DocumentationLocation} of the source/parent
	 *            {@link KnowledgeElement}.
	 * @param idOfChild
	 *            id of the target/child {@link KnowledgeElement}.
	 * @param documentationLocationOfChild
	 *            {@link DocumentationLocation} of the target/child
	 *            {@link KnowledgeElement}.
	 * @param linkTypeName
	 *            {@link LinkType#name()}.
	 * @return ok if the new {@link Link} was successfully created, saved in
	 *         database, and added to the {@link KnowledgeGraph}.
	 * 
	 * @see KnowledgePersistenceManager#insertLink(Link, ApplicationUser)
	 */
	@Path("/link/{projectKey}")
	@POST
	public Response createLink(@Context HttpServletRequest request, @PathParam("projectKey") String projectKey,
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

		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey);

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
			if (linkType == LinkType.RECOMMENDED) {
				linkType = LinkType.getLinkTypeForKnowledgeType(childElement.getType());
				LOGGER.info("Link recommendation was accepted between: " + parentElement + " and " + childElement);
			}
			link = Link.instantiateDirectedLink(parentElement, childElement, linkType);
		}
		long linkId = persistenceManager.insertLink(link, user);
		if (linkId == 0) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
		}
		LOGGER.info("Link " + link + " was created in project " + projectKey);
		return Response.ok(ImmutableMap.of("id", linkId)).build();
	}

	/**
	 * Deletes a {@link Link} in database and in the {@link KnowledgeGraph}. If the
	 * deleted link involved a code file, a new link of type "wrong" is created.
	 * 
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project. Both, link source and target need to be within
	 *            the same project.
	 * @param link
	 *            {@link Link} to be deleted.
	 * @return ok if the {@link Link} was successfully deleted.
	 * 
	 * @see KnowledgePersistenceManager#deleteLink(de.uhd.ifi.se.decision.management.jira.model.Link,
	 *      ApplicationUser)
	 */
	@Path("/link/{projectKey}")
	@DELETE
	public Response deleteLink(@Context HttpServletRequest request, @PathParam("projectKey") String projectKey,
			Link link) {
		if (projectKey == null || request == null || link == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of link failed."))
					.build();
		}

		ApplicationUser user = AuthenticationManager.getUser(request);
		KnowledgePersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey);
		// to fill knowledge types for status update
		link.setSourceElement(persistenceManager.getKnowledgeElement(link.getSource()));
		link.setDestinationElement(persistenceManager.getKnowledgeElement(link.getTarget()));

		boolean isDeleted = persistenceManager.deleteLink(link, user);
		if (!isDeleted && link.getSource() != null && link.getSource().isTransitivelyLinkedTo(link.getTarget(), 7)) {
			link.setType(LinkType.TRANSITIVE);
		}
		if (isDeleted || link.getType() == LinkType.TRANSITIVE) {
			LOGGER.info("Link " + link + " was deleted in project " + projectKey);
			// Create new link of type "wrong" if deleted link involved code file or was a
			// transitive link
			if (link.getType() == LinkType.TRANSITIVE || link.getBothElements().stream()
					.anyMatch(element -> element.getDocumentationLocation() == DocumentationLocation.CODE)) {
				link.setType(LinkType.WRONG);
				KnowledgePersistenceManager.getInstance(projectKey).insertLink(link, user);
			}
			return Response.ok().build();
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
		LOGGER.info("Knowledge elements were retrieved for filter settings: " + filterSettings);
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
				.getInstance(decisionKnowledgeElement.getProject().getProjectKey()).getJiraIssueTextManager();
		Issue issue = persistenceManager.createJiraIssueFromSentenceObject(decisionKnowledgeElement.getId(), user);

		if (issue != null) {
			return Response.ok(issue).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR)
				.entity(ImmutableMap.of("error", "The documentation location could not be changed.")).build();
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
		JiraIssueTextPersistenceManager persistenceManager = KnowledgePersistenceManager.getInstance(projectKey)
				.getJiraIssueTextManager();

		persistenceManager.deleteElementsInJiraIssue(jiraIssue);
		persistenceManager.updateElementsOfDescriptionInDatabase(jiraIssue);
		List<Comment> comments = ComponentAccessor.getCommentManager().getComments(jiraIssue);
		comments.forEach(comment -> persistenceManager.updateElementsOfCommentInDatabase(comment));

		List<KnowledgeElement> elements = persistenceManager.getElementsInJiraIssue(jiraIssue.getId());
		return Response.ok(elements.size()).build();
	}
}
