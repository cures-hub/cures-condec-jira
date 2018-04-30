package de.uhd.ifi.se.decision.documentation.jira.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.model.IDecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.persistence.PersistenceStrategy;
import de.uhd.ifi.se.decision.documentation.jira.persistence.StrategyProvider;
import de.uhd.ifi.se.decision.documentation.jira.view.treants.Chart;
import de.uhd.ifi.se.decision.documentation.jira.view.treants.Node;
import de.uhd.ifi.se.decision.documentation.jira.view.treants.Treant;

/**
 * @description REST resource for Treants
 */
@Path("")
public class TreantRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TreeViewerRest.class);
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

	@Path("/getTreant")
	@GET
	public Response getTreant(@QueryParam("projectKey") final String projectKey,
			@QueryParam("elementKey") String decisionKnowledgeElementKey,
			@QueryParam("depthOfTree") String depthOfTree) {
		if (projectKey != null) {
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project == null) {
				LOGGER.error("getMessage no project with this ProjectKey found");
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
						ImmutableMap.of("error", "Cannot find project for the given query parameter 'projectKey'"))
						.build();
			} else if (decisionKnowledgeElementKey != null) {
				int depth;
				if (depthOfTree != null) {
					try {
						depth = Integer.parseInt(depthOfTree);
					} catch (NumberFormatException e) {
						depth = 4; // default value
					}
				} else {
					depth = 4;
				}
				Treant treant = this.createTreant(decisionKnowledgeElementKey, depth, projectKey);
				return Response.ok(treant).build();
			}
		} else {
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Query parameter 'projectKey' is not provided, please add a valid projectKey")).build();
		}
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
				ImmutableMap.of("error", "Query parameters 'projectKey' and 'issueKey' do not lead to a valid result"))
				.build();
	}

	private Node createNodeStructure(DecisionKnowledgeElement decisionKnowledgeElement, int depth) {
		Node node = setUpNode(decisionKnowledgeElement);
		List<Node> nodes = new ArrayList<Node>();

		List<IDecisionKnowledgeElement> children = strategy.getChildren(decisionKnowledgeElement);
		if (children != null && children.size() > 0) {
			for (IDecisionKnowledgeElement child : children) {
				if (child != null) {
					nodes.add(createNode(child, depth, 0));
				}
			}
		}
		node.setChildren(nodes);
		return node;
	}

	private Node createNode(IDecisionKnowledgeElement decisionKnowledgeElement, int depth, int currentDepth) {
		Node node = setUpNode(decisionKnowledgeElement);

		if (currentDepth + 1 < depth) {
			List<Node> nodes = new ArrayList<Node>();
			List<IDecisionKnowledgeElement> children = strategy.getChildren(decisionKnowledgeElement);

			for (int index = 0; index < children.size(); ++index) {
				nodes.add(createNode(children.get(index), depth, currentDepth + 1));
			}
			node.setChildren(nodes);
		}
		return node;
	}

	private Node setUpNode(IDecisionKnowledgeElement decisionKnowledgeElement) {
		Node node = new Node();
		Map<String, String> nodeContent = ImmutableMap.of("name", decisionKnowledgeElement.getType().toString(),
				"title", decisionKnowledgeElement.getSummary(), "desc", decisionKnowledgeElement.getKey());
		node.setNodeContent(nodeContent);

		String htmlClass = decisionKnowledgeElement.getSuperType().toString().toLowerCase();
		node.setHtmlClass(htmlClass);
		node.setHtmlId(decisionKnowledgeElement.getId());
		return node;
	}
}