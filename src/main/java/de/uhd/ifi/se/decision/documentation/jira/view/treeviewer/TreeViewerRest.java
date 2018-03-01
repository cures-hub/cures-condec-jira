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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author Ewald Rode
 * @description Rest resource for TreeViewer list
 */
@Path("/treeviewer")
public class TreeViewerRest {

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response getMessage(@QueryParam("projectKey") final String projectKey) throws GenericEntityException {
		if (projectKey != null) {
			ProjectManager projectManager = ComponentAccessor.getProjectManager();
			Project project = projectManager.getProjectObjByKey(projectKey);
			if (project == null) {
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(
						ImmutableMap.of("error", "Cannot find project for the given query parameter 'projectKey'"))
						.build();
			} else {
				StrategyProvider strategyProvider = new StrategyProvider();
				IPersistenceStrategy strategy = strategyProvider.getStrategy(projectKey);
				Core core = createCore(projectKey, strategy);
				return Response.ok(core).build();
			}
		} else {
			// projectKey is not provided as a query parameter
			return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error",
					"Query parameter 'projectKey' is not provided, please add a valid projectKey")).build();
		}
	}

	private Core createCore(String projectKey, IPersistenceStrategy strategy){
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
			dataSet.add(strategy.createData(decisions.get(index)));

		}
		core.setData(dataSet);
		return core;
	}
}