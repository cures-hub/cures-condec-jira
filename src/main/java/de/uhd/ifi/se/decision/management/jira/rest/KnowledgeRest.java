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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.comments.MutableComment;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.extraction.model.GenericLink;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.CommentImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.GenericLinkImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.model.util.CommentSplitter;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.AbstractPersistenceStrategy;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.persistence.StrategyProvider;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

/**
 * REST resource: Enables creation, editing and deletion of decision knowledge
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
			ApplicationUser user = getCurrentUser(request);
			decisionKnowledgeElement = strategy.insertDecisionKnowledgeElement(decisionKnowledgeElement, user);
			if (decisionKnowledgeElement != null) {
				if (ConfigPersistence.isWebhookEnabled(projectKey)) {
					WebhookConnector connector = new WebhookConnector(projectKey);
					connector.sendElementChanges(decisionKnowledgeElement);
				}
				return Response.status(Status.OK).entity(decisionKnowledgeElement).build();
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
			ApplicationUser user = getCurrentUser(request);
			if (strategy.updateDecisionKnowledgeElement(decisionKnowledgeElement, user)) {
				if (ConfigPersistence.isWebhookEnabled(projectKey)) {
					WebhookConnector connector = new WebhookConnector(projectKey);
					connector.sendElementChanges(decisionKnowledgeElement);
				}
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
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			boolean isDeleted = strategy.deleteDecisionKnowledgeElement(decisionKnowledgeElement, user);
			if (isDeleted) {
				if (ConfigPersistence.isWebhookEnabled(projectKey)) {
					WebhookConnector connector = new WebhookConnector(projectKey);
					connector.sendElementChanges(decisionKnowledgeElement, isDeleted);
				}
				return Response.status(Status.OK).entity(isDeleted).build();
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
			ApplicationUser user = getCurrentUser(request);
			long linkId = strategy.insertLink(link, user);
			if (linkId == 0) {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Creation of link failed.")).build();
			}
			DecisionKnowledgeElement element = strategy.getDecisionKnowledgeElement(link.getSourceElement().getId());
			if (ConfigPersistence.isWebhookEnabled(projectKey)) {
				WebhookConnector connector = new WebhookConnector(projectKey);
				connector.sendElementChanges(element);
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
			@Context HttpServletRequest request, DecisionKnowledgeElement newElement, @QueryParam("argument") String argument) {
		if (projectKey != null && request != null && newElement != null) {
			Boolean result = ActiveObjectsManager.updateKnowledgeTypeOfSentence(newElement.getId(),
					newElement.getType(),argument);
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
		if (decisionKnowledgeElement.getId() > 0) {
			boolean isDeleted = ActiveObjectsManager.setSentenceIrrelevant(decisionKnowledgeElement.getId(), true);
			if (isDeleted) {
				return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
			} else {
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Deletion of link failed.")).build();

			}
		} else {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Deletion of link failed."))
					.build();
		}
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
			if ((databaseEntity.getEndSubstringCount()
					- databaseEntity.getStartSubstringCount()) != decisionKnowledgeElement.getDescription().length()) {
				// Get JIRA Comment instance
				CommentManager cm = ComponentAccessor.getCommentManager();
				MutableComment mc = (MutableComment) cm.getCommentById(databaseEntity.getCommentId());
				// Generate sentence data generated for classification
				String sentenceToSearch = CommentImpl.textRule(mc.getBody())
						.substring(databaseEntity.getStartSubstringCount(), databaseEntity.getEndSubstringCount());
				int index = mc.getBody().indexOf(sentenceToSearch);
				
				String tag = "";
				if(databaseEntity.isTaggedManually()) {
					tag ="["+WordUtils.capitalize(CommentSplitter.getKnowledgeTypeFromManuallIssueTag(sentenceToSearch,databaseEntity.getProjectKey()))+"]";
				}
				if(tag.length()<=3) {
					tag="";
				}
				String first = mc.getBody().substring(0, index);
				String second = tag + decisionKnowledgeElement.getDescription()+ tag.replace("[","[/");
				String third = mc.getBody().substring(index + sentenceToSearch.length());

				mc.setBody(first + second + third);
				cm.update(mc, true);
				ActiveObjectsManager.updateSentenceBodyWhenCommentChanged(databaseEntity.getCommentId(), decisionKnowledgeElement.getId(),
						second);
			}

			ActiveObjectsManager.updateKnowledgeTypeOfSentence(decisionKnowledgeElement.getId(),
					decisionKnowledgeElement.getType(),argument);
			

			Response r = Response.status(Status.OK).entity(ImmutableMap.of("id", decisionKnowledgeElement.getId()))
					.build();
			return r;
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Update of decision knowledge element failed.")).build();
		}
	}

	@Path("/deleteLink")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteLink(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request,
			Link link) {
		if (projectKey != null && request != null && link != null) {
			AbstractPersistenceStrategy strategy = StrategyProvider.getPersistenceStrategy(projectKey);
			ApplicationUser user = getCurrentUser(request);
			boolean isDeleted = strategy.deleteLink(link, user);
			if (isDeleted) {
				DecisionKnowledgeElement element = strategy
						.getDecisionKnowledgeElement(link.getSourceElement().getId());
				if (ConfigPersistence.isWebhookEnabled(projectKey)) {
					WebhookConnector connector = new WebhookConnector(projectKey);
					connector.sendElementChanges(element);
				}
				return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
			} else {
				Link inverseLink = new LinkImpl(link.getDestinationElement(), link.getSourceElement());
				isDeleted = strategy.deleteLink(inverseLink, user);
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

	@Path("/deleteGenericLink")
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteGenericLink(@QueryParam("projectKey") String projectKey, @Context HttpServletRequest request,
			GenericLinkImpl link) {
		System.out.println(link.toString());
		if (projectKey != null && request != null && link != null) {
			boolean isDeleted = ActiveObjectsManager.deleteGenericLink(link);
			if (isDeleted) {
				return Response.status(Status.OK).entity(ImmutableMap.of("id", isDeleted)).build();
			} else {
				GenericLink inverseLink = new GenericLinkImpl(link.getIdOfSourceElement(),
						link.getIdOfDestinationElement());
				isDeleted = ActiveObjectsManager.deleteGenericLink(inverseLink);
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
			GenericLink link) {
		if (projectKey != null && request != null && link != null) {
			ApplicationUser user = getCurrentUser(request);
			long linkId = ActiveObjectsManager.insertGenericLink(link, user);
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

	private ApplicationUser getCurrentUser(HttpServletRequest request) {
		com.atlassian.jira.user.util.UserManager jiraUserManager = ComponentAccessor.getUserManager();
		UserManager userManager = ComponentGetter.getUserManager();
		String userName = userManager.getRemoteUsername(request);
		return jiraUserManager.getUserByName(userName);
	}
}