package de.uhd.ifi.se.decision.documentation.jira.view.treeviewer;

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
import org.ofbiz.core.entity.GenericEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @description REST resource for TreeViewer list
 */
@Path("/treeviewer")
public class TreeViewerRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(TreeViewerRest.class);
	private PersistenceStrategy strategy;

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@QueryParam("projectKey") final String projectKey) throws GenericEntityException {
		if (projectKey != null) {
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
			LOGGER.error("getMessage ProjectKey is NULL");
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Query parameter 'projectKey' is not provided, please add a valid projectKey")).build();
		}
	}

	private Core createCore(String projectKey, PersistenceStrategy strategy) {
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

		data.setText(decisionKnowledgeElement.getType() + " / " + decisionKnowledgeElement.getSummary());
		data.setId(String.valueOf(decisionKnowledgeElement.getId()));

		NodeInfo nodeInfo = new NodeInfo();
		nodeInfo.setId(Long.toString(decisionKnowledgeElement.getId()));
		nodeInfo.setKey(decisionKnowledgeElement.getKey());
		nodeInfo.setType(decisionKnowledgeElement.getType().toString().toLowerCase());
		nodeInfo.setDescription(decisionKnowledgeElement.getDescription());
		nodeInfo.setSummary(decisionKnowledgeElement.getSummary());
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