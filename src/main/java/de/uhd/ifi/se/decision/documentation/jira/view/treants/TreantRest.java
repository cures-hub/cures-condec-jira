package de.uhd.ifi.se.decision.documentation.jira.view.treants;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLink;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.documentation.jira.decisionknowledge.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.documentation.jira.persistence.IPersistenceStrategy;
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
	private IPersistenceStrategy strategy;
	
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

	//TODO Implementing the Function
	private Node createNode(DecisionKnowledgeElement decisionKnowledgeElement, int depth, int currentDepth) {
		Node node = new Node();
		Map<String, String> nodeContent = ImmutableMap.of("name", decisionKnowledgeElement.getSummary(), "title",
				decisionKnowledgeElement.getType(), "desc", decisionKnowledgeElement.getKey());
		node.setNodeContent(nodeContent);

		String htmlClass;
		String issueType = decisionKnowledgeElement.getType().toLowerCase();
		if (issueType.equals("constraint") || issueType.equals("assumption") || issueType.equals("implication")
				|| issueType.equals("context")) {
			htmlClass = "context";
		} else if (issueType.equals("problem") || issueType.equals("issue") || issueType.equals("goal")) {
			htmlClass = "problem";
		} else if (issueType.equals("solution") || issueType.equals("claim") || issueType.equals("alternative")) {
			htmlClass = "solution";
		} else {
			htmlClass = "rationale";
		}
		node.setHtmlClass(htmlClass);

		long htmlId = decisionKnowledgeElement.getId();
		node.setHtmlId(htmlId);

		if (currentDepth + 1 < depth) {
			List<Node> children = new ArrayList<Node>();
			List<DecisionKnowledgeElement> toBeAddedToChildren = new ArrayList<DecisionKnowledgeElement>();
			//TODO Change from ComponentAccessor to IssueStrategy getDecisionKowledgeElement
			List<IssueLink> allOutwardIssueLink = ComponentAccessor.getIssueLinkManager()
					.getOutwardLinks(decisionKnowledgeElement.getId());
			if (allOutwardIssueLink != null) {
				// this.children = new ArrayList<Node>();
				for (int i = 0; i < allOutwardIssueLink.size(); ++i) {
					DecisionKnowledgeElement linkeDecisionKnowledgeElement = strategy.getDecisionKnowledgeElement(allOutwardIssueLink.get(i).getDestinationObject().getKey());
					/*
					 * Erstelle Parent-Child Beziehung und pruefe ob diese bereits in der
					 * KeyValuePair-Liste vorhanden ist. Wenn nein, fuege diesem Knoten Kinder hinzu
					 */
					if (decisionKnowledgeElement != null & linkeDecisionKnowledgeElement != null) {
						Pair<String, String> newKVP = new Pair<String, String>(decisionKnowledgeElement.getKey(),
								linkeDecisionKnowledgeElement.getKey());
						Pair<String, String> newKVPReverse = new Pair<String, String>(linkeDecisionKnowledgeElement.getKey(),
								decisionKnowledgeElement.getKey());
						boolean boolvar = false;
						for (int counter = 0; counter < KeyValuePairList.keyValuePairList.size(); ++counter) {
							Pair<String, String> globalInst = KeyValuePairList.keyValuePairList.get(counter);
							if (newKVP.equals(globalInst) || newKVPReverse.equals(globalInst)) {
								boolvar = true;
							}
						}
						if (!boolvar) {
							KeyValuePairList.keyValuePairList.add(newKVP);
							KeyValuePairList.keyValuePairList.add(newKVPReverse);
							toBeAddedToChildren.add(linkeDecisionKnowledgeElement);
						}
					}
				}
			}

			//TODO Change from ComponentAccessor to IssueStrategy getDecisionKowledgeElement
			List<IssueLink> allInwardIssueLink = ComponentAccessor.getIssueLinkManager().getInwardLinks(decisionKnowledgeElement.getId());
			if (allInwardIssueLink != null) {
				for (int i = 0; i < allInwardIssueLink.size(); ++i) {
					DecisionKnowledgeElement linkeDecisionKnowledgeElement = strategy.getDecisionKnowledgeElement(allOutwardIssueLink.get(i).getDestinationObject().getKey());
					/*
					 * Erstelle Parent-Child Beziehung und pruefe ob diese bereits in der
					 * KeyValuePair-Liste vorhanden ist. Wenn nein, fuege diesem Knoten Kinder hinzu
					 */
					if (decisionKnowledgeElement != null & linkeDecisionKnowledgeElement != null) {
						Pair<String, String> newKVP = new Pair<String, String>(decisionKnowledgeElement.getKey(),
								linkeDecisionKnowledgeElement.getKey());
						Pair<String, String> newKVPReverse = new Pair<String, String>(linkeDecisionKnowledgeElement.getKey(),
								decisionKnowledgeElement.getKey());
						boolean boolvar = false;
						for (int counter = 0; counter < KeyValuePairList.keyValuePairList.size(); ++counter) {
							Pair<String, String> globalInst = KeyValuePairList.keyValuePairList.get(counter);
							if (newKVP.equals(globalInst) || newKVPReverse.equals(globalInst)) {
								boolvar = true;
							}
						}
						if (!boolvar) {
							KeyValuePairList.keyValuePairList.add(newKVP);
							KeyValuePairList.keyValuePairList.add(newKVPReverse);
							toBeAddedToChildren.add(linkeDecisionKnowledgeElement);
						}
					}
				}
			}
			for (int index = 0; index < toBeAddedToChildren.size(); ++index) {
				children.add(createNode(toBeAddedToChildren.get(index), depth, currentDepth + 1));
			}
			node.setChildren(children);
		}
		return node;
	}

	private Node createNodeStructure(DecisionKnowledgeElement decisionKnowledgeElement, int depth){
		Node node = new Node();
		Map<String, String> nodeContent = ImmutableMap.of("name", decisionKnowledgeElement.getSummary(), "title",
				decisionKnowledgeElement.getText(), "desc", decisionKnowledgeElement.getKey());
		node.setNodeContent(nodeContent);

		String htmlClass;
		String issueType = decisionKnowledgeElement.getType().toLowerCase();
		if (issueType.equals("constraint") || issueType.equals("assumption") || issueType.equals("implication")
				|| issueType.equals("context")) {
			htmlClass = "context";
		} else if (issueType.equals("problem") || issueType.equals("issue") || issueType.equals("goal")) {
			htmlClass = "problem";
		} else if (issueType.equals("solution") || issueType.equals("claim") || issueType.equals("alternative")) {
			htmlClass = "solution";
		} else {
			htmlClass = "rationale";
		}
		node.setHtmlClass(htmlClass);
		long htmlId = decisionKnowledgeElement.getId();
		node.setHtmlId(htmlId);

		List<Node> children = new ArrayList<Node>();
		List<IssueLink> allOutwardIssueLink = ComponentAccessor.getIssueLinkManager().getOutwardLinks(decisionKnowledgeElement.getId());
		KeyValuePairList.keyValuePairList = new ArrayList<Pair<String, String>>();
		if (allOutwardIssueLink != null) {
			if (allOutwardIssueLink.size() > 0) {
				for (int i = 0; i < allOutwardIssueLink.size(); i++) {
					DecisionKnowledgeElement linkeDecisionKnowledgeElement = strategy.getDecisionKnowledgeElement(allOutwardIssueLink.get(i).getDestinationObject().getKey());
					if (decisionKnowledgeElement!= null & linkeDecisionKnowledgeElement != null) {
						KeyValuePairList.keyValuePairList
								.add(new Pair<String, String>(decisionKnowledgeElement.getKey(), linkeDecisionKnowledgeElement.getKey()));
						KeyValuePairList.keyValuePairList
								.add(new Pair<String, String>(linkeDecisionKnowledgeElement.getKey(), decisionKnowledgeElement.getKey()));
						children.add(createNode(linkeDecisionKnowledgeElement, depth, 0));
					}
				}
			}
		}
		List<IssueLink> allInwardIssueLink = ComponentAccessor.getIssueLinkManager().getInwardLinks(decisionKnowledgeElement.getId());
		if (allInwardIssueLink != null) {
			if (allInwardIssueLink.size() > 0) {
				for (int i = 0; i < allInwardIssueLink.size(); i++) {
					DecisionKnowledgeElement linkeDecisionKnowledgeElement = strategy.getDecisionKnowledgeElement(allOutwardIssueLink.get(i).getDestinationObject().getKey());
					if (decisionKnowledgeElement != null & linkeDecisionKnowledgeElement != null) {
						Pair<String, String> kvp = new Pair<String, String>(decisionKnowledgeElement.getKey(),
								linkeDecisionKnowledgeElement.getKey());
						Pair<String, String> kvp2 = new Pair<String, String>(linkeDecisionKnowledgeElement.getKey(),
								decisionKnowledgeElement.getKey());
						KeyValuePairList.keyValuePairList.add(kvp);
						KeyValuePairList.keyValuePairList.add(kvp2);
						children.add(createNode(linkeDecisionKnowledgeElement, depth, 0));
					}
				}
			}
		}
		node.setChildren(children);
		return node;
	}

}