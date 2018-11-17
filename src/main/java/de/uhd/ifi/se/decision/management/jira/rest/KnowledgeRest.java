package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.text.WordUtils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.extraction.DecXtractEventListener;
import de.uhd.ifi.se.decision.management.jira.extraction.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.model.util.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Graph;
import de.uhd.ifi.se.decision.management.jira.model.GraphImpl;
import de.uhd.ifi.se.decision.management.jira.model.GraphImplFiltered;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;
import de.uhd.ifi.se.decision.management.jira.view.GraphFiltering;

/**
 * REST resource: Enables creation, editing, and deletion of decision knowledge
 * elements and their links
 */
@Path("/decisions")
public class KnowledgeRest {

	@Path("/getDecisionKnowledgeElement")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDecisionKnowledgeElement(@QueryParam("id") long id,
			@QueryParam("projectKey") String projectKey) {
		if (projectKey != null) {
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			DecisionKnowledgeElement decisionKnowledgeElement = strategy.getDecisionKnowledgeElement(id);
			if (decisionKnowledgeElement != null) {
				return Response.status(Status.OK).entity(decisionKnowledgeElement).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Decision knowledge element was not found for the given id."))
					.build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Decision knowledge element could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
	}

	@Path("/getLinkedElements")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getLinkedElements(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey) {
		if (projectKey != null) {
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			List<DecisionKnowledgeElement> linkedDecisionKnowledgeElements = strategy.getLinkedElements(id);
			return Response.ok(linkedDecisionKnowledgeElements).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Linked decision knowledge elements could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
	}

	@Path("/getUnlinkedElements")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUnlinkedElements(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey) {
		if (projectKey != null) {
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			List<DecisionKnowledgeElement> unlinkedDecisionKnowledgeElements = strategy.getUnlinkedElements(id);
			return Response.ok(unlinkedDecisionKnowledgeElements).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Unlinked decision knowledge elements could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
	}

	@Path("/createDecisionKnowledgeElement")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createDecisionKnowledgeElement(@Context HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement != null && request != null) {
			String projectKey = decisionKnowledgeElement.getProject().getProjectKey();
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			ApplicationUser user = AuthenticationManager.getUser(request);
			DecisionKnowledgeElement decisionKnowledgeElementWithId = strategy
					.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
			if (decisionKnowledgeElementWithId != null) {
				return Response.status(Status.OK).entity(decisionKnowledgeElementWithId).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of decision knowledge element failed.")).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Creation of decision knowledge element failed.")).build();
		}
	}
	
	@Path("/createIssueFromSentence")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createIssueFromSentence(@Context HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement != null && request != null) {
			
			ApplicationUser user = AuthenticationManager.getUser(request);
			 Issue issue = ActiveObjectsManager.createJIRAIssueFromSentenceObject(decisionKnowledgeElement.getId(), user);
				
			if (issue != null) {
				return Response.status(Status.OK).entity(issue).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Creation of decision knowledge element failed.")).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Creation of decision knowledge element failed.")).build();
		}
	}

	@Path("/updateDecisionKnowledgeElement")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response updateDecisionKnowledgeElement(@Context HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement != null && request != null) {
			String projectKey = decisionKnowledgeElement.getProject().getProjectKey();
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			ApplicationUser user = AuthenticationManager.getUser(request);
			if (strategy.updateDecisionKnowledgeElement(decisionKnowledgeElement, user)) {
				return Response.status(Status.OK).entity(decisionKnowledgeElement).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build();
		}
	}

	@Path("/deleteDecisionKnowledgeElement")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteDecisionKnowledgeElement(@Context HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement != null && request != null) {
			String projectKey = decisionKnowledgeElement.getProject().getProjectKey();
			ApplicationUser user = AuthenticationManager.getUser(request);
			boolean isDeleted = false;
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			DecisionKnowledgeElement elementToBeDeletedWithLinks = strategy
					.getDecisionKnowledgeElement(decisionKnowledgeElement.getId());
			isDeleted = strategy.deleteDecisionKnowledgeElement(elementToBeDeletedWithLinks, user);
			if (isDeleted) {
				return Response.status(Status.OK).entity(true).build();
			}
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();

		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Deletion of decision knowledge element failed.")).build();
		}
	}

	@Path("/createLink")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createLink(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request,
			Link link) {
		if (projectKey != null && request != null && link != null) {
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			ApplicationUser user = AuthenticationManager.getUser(request);
			long linkId = strategy.insertLink(link, user);
			if (linkId == 0) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
			}
			return Response.status(Status.OK).entity(ImmutableMap.of("id", linkId)).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Creation of link failed."))
					.build();
		}
	}

	@Path("/changeKnowledgeTypeOfSentence")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response changeKnowledgeTypeOfSentence(@QueryParam("projectKey") String projectKey,
			@Context HttpServletRequest request, DecisionKnowledgeElement newElement,
			@QueryParam("argument") String argument) {
		if (projectKey != null && request != null && newElement != null) {
			DecXtractEventListener.editCommentLock=true;
			Boolean result = ActiveObjectsManager.updateKnowledgeTypeOfSentence(newElement.getId(),
					newElement.getType(), argument);
			DecXtractEventListener.editCommentLock = false;
			if (!result) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Update of element failed.")).build();
			}
			return Response.status(Status.OK).entity(ImmutableMap.of("id", newElement.getId())).build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Update of element failed."))
				.build();
	}

	@Path("/setSentenceIrrelevant")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response setSentenceIrrelevant(@Context HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement) {
		if (request != null && decisionKnowledgeElement != null && decisionKnowledgeElement.getId() > 0) {
			boolean isDeleted = ActiveObjectsManager.setSentenceIrrelevant(decisionKnowledgeElement.getId(), false);
			if (isDeleted) {
				return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
			} else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Deletion of link failed.")).build();
			}
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of link failed.")).build();
	}

	@Path("/editSentenceBody")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response editSentenceBody(@Context HttpServletRequest request,
			DecisionKnowledgeElement decisionKnowledgeElement, @QueryParam("argument") String argument) {
		if (decisionKnowledgeElement != null && request != null) {

			// Get corresponding element from ao database
			DecisionKnowledgeInCommentEntity databaseEntity = ActiveObjectsManager
					.getElementFromAO(decisionKnowledgeElement.getId());
			int newSentenceEnd = databaseEntity.getEndSubstringCount();
			int newSentenceStart = databaseEntity.getStartSubstringCount();
			String newSentenceBody = decisionKnowledgeElement.getDescription();
			
			if ((newSentenceEnd - newSentenceStart) != newSentenceBody.length()) {
				// Get JIRA Comment instance - Casting fails in unittesting with Mock
				CommentManager cm = ComponentAccessor.getCommentManager();
				MutableComment mc = (MutableComment) cm.getCommentById(databaseEntity.getCommentId());

				if (mc.getBody().length() >= databaseEntity.getEndSubstringCount()) {
					String oldSentenceInComment = mc.getBody().substring(newSentenceStart, newSentenceEnd);
					int indexOfOldSentence = mc.getBody().indexOf(oldSentenceInComment);

					String newType = decisionKnowledgeElement.getType().toString();
					if(newType.equals(KnowledgeType.OTHER.toString()) && argument.length() >0) {
						newType = argument; 
					}
					String tag = "";
					// Allow changing of manual tags, but no tags for icons
					if (databaseEntity.isTaggedManually() && !CommentSplitter.isCommentIconTagged(oldSentenceInComment)) {
						tag = "{" + WordUtils.capitalize(newType) + "}";
					} else if (CommentSplitter.isCommentIconTagged(oldSentenceInComment)) {
						indexOfOldSentence = indexOfOldSentence + 3; // add icon to text.
					}
					String first = mc.getBody().substring(0, indexOfOldSentence);
					String second = tag + newSentenceBody + tag;
					String third = mc.getBody().substring(indexOfOldSentence + oldSentenceInComment.length());
					DecXtractEventListener.editCommentLock = true;
					mc.setBody(first + second + third);
					cm.update(mc, true);
					ActiveObjectsManager.updateSentenceBodyWhenCommentChanged(databaseEntity.getCommentId(),
							decisionKnowledgeElement.getId(), second);

					DecXtractEventListener.editCommentLock = false;
				}
			}
			ActiveObjectsManager.updateKnowledgeTypeOfSentence(decisionKnowledgeElement.getId(),
					decisionKnowledgeElement.getType(), argument);
			Response r = Response.status(Status.OK).entity(ImmutableMap.of("id", decisionKnowledgeElement.getId()))
					.build();
			return r;
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build();
		}
	}
	
	@Path("/getSentenceElement")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getSentenceElement(@QueryParam("id") long id) {

		DecisionKnowledgeInCommentEntity dbObject = ActiveObjectsManager.getElementFromAO(id);
		
		if (dbObject != null) {
			Sentence sentence = new SentenceImpl(dbObject);
			//TODO: Reweork this after merging with ConDec-378
			return Response.status(Status.OK).entity(ImmutableMap.of("id", sentence.getId(),"description",sentence.getDescription(),"type",sentence.getKnowledgeTypeString())).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Unlinked decision knowledge elements could not be received due to a bad request (element id or project key was missing)."))
					.build();
		}
	}


	@Path("/deleteLink")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteLink(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request,
			Link link) {
		if (projectKey != null && request != null && link != null) {
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			ApplicationUser user = AuthenticationManager.getUser(request);
			boolean isDeleted = strategy.deleteLink(link, user);
			if (isDeleted) {
				return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
			} else {
				Link inverseLink = new LinkImpl(link.getDestinationElement(), link.getSourceElement());
				isDeleted = strategy.deleteLink(inverseLink, user);
				if (isDeleted) {
					return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
				} else {
					return deleteGenericLink(projectKey, request, new LinkImpl(link.getIdOfSourceElementWithPrefix(),
							link.getIdOfDestinationElementWithPrefix()));

				}
			}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of link failed."))
					.build();
		}
	}

	@Path("/deleteGenericLink")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteGenericLink(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request,
			Link link) {
		if (projectKey != null && request != null && link != null) {
			boolean isDeleted = GenericLinkManager.deleteGenericLink(link);
			if (isDeleted) {
				return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
			} else {
				Link inverseLink = new LinkImpl(link.getIdOfDestinationElementWithPrefix(),
						link.getIdOfSourceElementWithPrefix());
				isDeleted = GenericLinkManager.deleteGenericLink(inverseLink);
				if (isDeleted) {
					return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
				} else {
					return Response.status(Status.INTERNAL_SERVER_ERROR)
							.entity(ImmutableMap.of("error", "Deletion of link failed.")).build();
				}
			}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of link failed."))
					.build();
		}
	}

	@Path("/createGenericLink")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response createGenericLink(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request,
			Link link) {
		if (projectKey != null && request != null && link != null) {
			ApplicationUser user = AuthenticationManager.getUser(request);
			long linkId = GenericLinkManager.insertLink(link, user);
			if (linkId == 0) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
			}
			return Response.status(Status.OK).entity(ImmutableMap.of("id", linkId)).build();
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Creation of link failed."))
					.build();
		}
	}

	@Path("getAllElementsMatchingQuery")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllElementsMatchingQuery(@QueryParam("projectKey") String projectKey,
			@QueryParam("query") String query, @Context HttpServletRequest request) {
		if (projectKey != null && query != null && request != null) {
			ApplicationUser user = AuthenticationManager.getUser(request);
			GraphFiltering filter = new GraphFiltering(projectKey, query, user);
			filter.produceResultsFromQuery();
			List<DecisionKnowledgeElement> queryResult = filter.getAllElementsMatchingQuery();
			if (queryResult == null) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Getting elements matching the query failed.")).build();
			}
			return Response.ok(queryResult).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Getting elements matching the query failed.")).build();
		}
	}

	@Path("/getAllElementsLinkedToElement")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getAllElementsLinkedToElement(@QueryParam("elementKey") String elementKey,
			@QueryParam("URISearch") String uriSearch, @Context HttpServletRequest request) {
		String projectKey = getProjectKey(elementKey);
		ApplicationUser user = AuthenticationManager.getUser(request);

		Graph graph;
		if ((uriSearch.matches("\\?jql=(.)+")) || (uriSearch.matches("\\?filter=(.)+"))) {
			GraphFiltering filter = new GraphFiltering(projectKey, uriSearch, user);
			filter = new GraphFiltering(projectKey, uriSearch, user);
			filter.produceResultsFromQuery();
			graph = new GraphImplFiltered(projectKey, elementKey, filter);
		} else {
			graph = new GraphImpl(projectKey, elementKey);
		}
		List<DecisionKnowledgeElement> filteredElements = graph.getAllElements();

		return Response.ok(filteredElements).build();
	}

	private String getProjectKey(String elementKey) {
		return elementKey.split("-")[0];
	}
}