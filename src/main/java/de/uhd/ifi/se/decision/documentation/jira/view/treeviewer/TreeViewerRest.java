package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

import com.atlassian.jira.component.ComponentAccessor;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Ewald Rode
 * @description Rest resource for TreeViewer list
 */
@Path("/treeviewer")
public class TreeViewerRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TreeViewerRest.class);
	private IPersistenceStrategy strategy;

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@QueryParam("projectKey") final String projectKey) throws GenericEntityException {
		if (projectKey != null) {
			LOGGER.error("getMessage ProjectKey is NULL");
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project == null) {
				LOGGER.error("getMessage no project with this ProjectKey found");
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
						ImmutableMap.of("error", "Cannot find project for the given query parameter 'projectKey'"))
						.build();
			} else {
				StrategyProvider strategyProvider = new StrategyProvider();
				strategy = strategyProvider.getStrategy(projectKey);
				Core core = createCore(projectKey, strategy);
				return Response.ok(core).build();
			}
		} else {
			// projectKey is not provided as a query parameter
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Query parameter 'projectKey' is not provided, please add a valid projectKey")).build();
		}
	}

	private Core createCore(String projectKey, IPersistenceStrategy strategy) {
		Core core = new Core();
		core.setMultiple(false);
		core.setCheckCallback(true);
		core.setThemes(ImmutableMap.of("icons", false));
		HashSet<Data> dataSet = new HashSet<Data>();
		List<DecisionKnowledgeElement> decisions = strategy.getDecisions(projectKey);
		if (decisions == null) {
			return null;
		}
		for (int index = 0; index < decisions.size(); ++index) {
			KeyValuePairList.keyValuePairList = new ArrayList<Pair<String, String>>();
			Pair<String, String> kvp = new Pair<String, String>("root", decisions.get(index).getKey());
			KeyValuePairList.keyValuePairList.add(kvp);
			dataSet.add(createData(decisions.get(index)));
		}
		core.setData(dataSet);
		return core;
	}

	private Data createData(DecisionKnowledgeElement decisionKnowledgeElement) {
		if (decisionKnowledgeElement == null) {
			LOGGER.error("createData decisionKnowledgeElement is NULL");
			return new Data();
		}
		Data data = new Data();

		data.setText(decisionKnowledgeElement.getType() + " / " + decisionKnowledgeElement.getName());
		data.setId(String.valueOf(decisionKnowledgeElement.getId()));

		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setId(Long.toString(decisionKnowledgeElement.getId()));
		nodeInfo.setKey(decisionKnowledgeElement.getKey());
		nodeInfo.setType(decisionKnowledgeElement.getType());
		nodeInfo.setDescription(decisionKnowledgeElement.getDescription());
		nodeInfo.setSummary(decisionKnowledgeElement.getName());
		data.setNodeInfo(nodeInfo);

		List<DecisionKnowledgeElement> children = strategy.getChildren(decisionKnowledgeElement);

		List<Data> childrenToData = new ArrayList<Data>();
		for (DecisionKnowledgeElement child : children) {
			childrenToData.add(createData(child));
		}
		data.setChildren(childrenToData);

		return data;
	}
}