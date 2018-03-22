package de.uhd.ifi.se.decision.documentation.jira.view.treants;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import de.uhd.ifi.se.decision.documentation.jira.util.KeyValuePairList;
import de.uhd.ifi.se.decision.documentation.jira.util.Pair;
import org.ofbiz.core.entity.GenericEntityException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Ewald Rode
 * @description Rest resource for Treants
 */
@Path("/treant")
public class TreantRest {
	private PersistenceStrategy strategy;

	public Treant createTreant(String key, int depth, String projectKey) {
		StrategyProvider strategyProvider = new StrategyProvider();
		strategy = strategyProvider.getStrategy(projectKey);
		DecisionKnowledgeElement decisionKnowledgeElement = strategy.getDecisionKnowledgeElement(key);

		Treant treant = new Treant();
		treant.setChart(new Chart());

		treant.setNodeStructure(createNodeStructure(decisionKnowledgeElement, depth));
		return treant;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@QueryParam("projectKey") final String projectKey,
			@QueryParam("issueKey") String issueKey, @QueryParam("depthOfTree") String depthOfTree)
			throws GenericEntityException {
		if (projectKey != null) {
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project == null) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
						ImmutableMap.of("error", "Cannot find project for the given query parameter 'projectKey'"))
						.build();
			} else if (issueKey != null) {
				int depth;
				if (depthOfTree != null) {
					try {
						depth = Integer.parseInt(depthOfTree);
					} catch (NumberFormatException e) {
						// default value
						depth = 4;
					}
				} else {
					depth = 4;
				}
				Treant treantRestModel = this.createTreant(issueKey, depth, projectKey);
				return Response.ok(treantRestModel).build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Query parameter 'projectKey' is not provided, please add a valid projectKey")).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
				ImmutableMap.of("error", "Query parameters 'projectKey' and 'issueKey' do not lead to a valid result"))
				.build();
	}

	//TODO
	private String checkDecisionType(String type) {
		if (type.equals("constraint") || type.equals("assumption") || type.equals("implication")
				|| type.equals("context")) {
			return "context";
		} else if (type.equals("problem") || type.equals("issue") || type.equals("goal")) {
			return "problem";
		} else if (type.equals("solution") || type.equals("claim") || type.equals("alternative")) {
			return "solution";
		} else {
			return "rationale";
		}
	}

	private Node createNode(DecisionKnowledgeElement decisionKnowledgeElement, int depth, int currentDepth) {
		Node node = new Node();
		Map<String, String> nodeContent = ImmutableMap.of("name", decisionKnowledgeElement.getType().toString().toLowerCase(), "title",
				decisionKnowledgeElement.getSummary(), "desc", decisionKnowledgeElement.getKey());
		node.setNodeContent(nodeContent);

		String htmlClass = checkDecisionType(decisionKnowledgeElement.getType().toString().toLowerCase());
		node.setHtmlClass(htmlClass);

		long htmlId = decisionKnowledgeElement.getId();
		node.setHtmlId(htmlId);

		if (currentDepth + 1 < depth) {
			List<Node> children = new ArrayList<Node>();
			List<DecisionKnowledgeElement> toBeAddedToChildren = strategy.getChildren(decisionKnowledgeElement);

			for (int index = 0; index < toBeAddedToChildren.size(); ++index) {
				children.add(createNode(toBeAddedToChildren.get(index), depth, currentDepth + 1));
			}
			node.setChildren(children);
		}
		return node;
	}

	private Node createNodeStructure(DecisionKnowledgeElement decisionKnowledgeElement, int depth) {
		Node node = new Node();
		Map<String, String> nodeContent = ImmutableMap.of("name", decisionKnowledgeElement.getType().toString().toLowerCase(), "title",
				decisionKnowledgeElement.getSummary(), "desc", decisionKnowledgeElement.getKey());
		node.setNodeContent(nodeContent);

		String htmlClass = checkDecisionType(decisionKnowledgeElement.getType().toString().toLowerCase());
		node.setHtmlClass(htmlClass);
		long htmlId = decisionKnowledgeElement.getId();
		node.setHtmlId(htmlId);

		List<Node> children = new ArrayList<Node>();

		List<DecisionKnowledgeElement> elementChildren = strategy.getChildren(decisionKnowledgeElement);
		KeyValuePairList.keyValuePairList = new ArrayList<Pair<String, String>>();
		if (elementChildren != null) {
			if (elementChildren.size() > 0) {
				for (DecisionKnowledgeElement linkeDecisionKnowledgeElement : elementChildren) {
					if (decisionKnowledgeElement != null & linkeDecisionKnowledgeElement != null) {
						KeyValuePairList.keyValuePairList.add(new Pair<String, String>(
								decisionKnowledgeElement.getKey(), linkeDecisionKnowledgeElement.getKey()));
						KeyValuePairList.keyValuePairList.add(new Pair<String, String>(
								linkeDecisionKnowledgeElement.getKey(), decisionKnowledgeElement.getKey()));
						children.add(createNode(linkeDecisionKnowledgeElement, depth, 0));
					}
				}
			}
		}
		node.setChildren(children);
		return node;
	}
}