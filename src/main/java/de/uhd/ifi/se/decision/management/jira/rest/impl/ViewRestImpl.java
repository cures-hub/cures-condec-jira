package de.uhd.ifi.se.decision.management.jira.rest.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.jgit.lib.Ref;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.exception.PermissionException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.GitClientImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitDecXtract;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.impl.FilterSettingsImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.KnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.CodeClassKnowledgeElementPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.KnowledgePersistenceManagerImpl;
import de.uhd.ifi.se.decision.management.jira.rest.ViewRest;
import de.uhd.ifi.se.decision.management.jira.view.diffviewer.DiffViewer;
import de.uhd.ifi.se.decision.management.jira.view.matrix.Matrix;
import de.uhd.ifi.se.decision.management.jira.view.treant.Treant;
import de.uhd.ifi.se.decision.management.jira.view.treeviewer.TreeViewer;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisGraph;
import de.uhd.ifi.se.decision.management.jira.view.vis.VisTimeLine;

/**
 * REST resource for view
 */
@Path("/view")
public class ViewRestImpl implements ViewRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewRestImpl.class);
	private GitClient gitClient;

	@Override
	@Path("/elementsFromBranchesOfProject")
	@GET
	public Response getAllFeatureBranchesTree(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		// get all project branches
		return getDiffViewerResponse(projectKey, projectKey);
	}

	// FIXME: Unit test
	@Override
	@Path("/elementsFromBranchesOfJiraIssue")
	@GET
	public Response getFeatureBranchTree(@Context HttpServletRequest request, @QueryParam("issueKey") String issueKey)
			throws PermissionException {
		String normalizedIssueKey = normalizeIssueKey(issueKey); // ex: issueKey=ConDec-498
		Issue issue = getJiraIssue(normalizedIssueKey);
		if (issue == null) {
			return jiraIssueKeyIsInvalid();
		}
		String regexFilter = normalizedIssueKey.toUpperCase() + "\\.|" + normalizedIssueKey.toUpperCase() + "$|"
				+ normalizedIssueKey.toUpperCase() + "\\-";

		// get feature branches of an issue
		return getDiffViewerResponse(getProjectKey(normalizedIssueKey), regexFilter,
				ComponentAccessor.getIssueManager().getIssueByCurrentKey(normalizedIssueKey));
	}

	private Issue getJiraIssue(String issueKey) {
		Issue issue = null;
		if (issueKey == null || issueKey.isBlank())
			return null;
		issue = ComponentAccessor.getIssueManager().getIssueObject(issueKey);
		return issue;
	}

	private Response getDiffViewerResponse(String projectKey, String filter, Issue issue) throws PermissionException {

		Response resp = this.getDiffViewerResponse(projectKey, filter);

		Pattern filterPattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);

		CommitMessageToCommentTranscriber transcriber = new CommitMessageToCommentTranscriber(issue);
		// get current branch name
		// iterate over commits to get all messages and post each one as a comment
		// make sure to not post duplicates
		gitClient = new GitClientImpl(projectKey);
		for (String repoUri : gitClient.getRemoteUris()) {
			List<Ref> branches = gitClient.getRemoteBranches(repoUri);
			for (Ref branch : branches) {
				Matcher branchMatcher = filterPattern.matcher(branch.getName());
				if (branchMatcher.find()
						|| branch.getName().contains("/" + gitClient.getDefaultBranchFolderNames().get(repoUri))) {
					transcriber.postComments(branch);
				}
			}
		}
		gitClient.closeAll();
		return resp;
	}

	private Response getDiffViewerResponse(String projectKey, String filter) {
		gitClient = new GitClientImpl(projectKey);
		Response resp = null;
		List<Ref> branches = gitClient.getAllRemoteBranches();
		Pattern filterPattern = Pattern.compile(filter, Pattern.CASE_INSENSITIVE);
		if (branches.isEmpty()) {
			return Response.status(Status.INTERNAL_SERVER_ERROR).build();
		}
		Map<Ref, List<KnowledgeElement>> ratBranchList = new HashMap<>();
		GitDecXtract extractor = new GitDecXtract(projectKey);
		for (Ref branch : branches) {
			String branchName = branch.getName();
			Matcher branchMatcher = filterPattern.matcher(branchName);
			if (branchMatcher.find()) {
				ratBranchList.put(branch, extractor.getElements(branch));
			}
		}
		extractor.close();
		DiffViewer diffView = new DiffViewer(ratBranchList);
		try {
			Response.ResponseBuilder respBuilder = Response.ok(diffView);
			resp = respBuilder.build();
		} catch (Exception ex) {
			LOGGER.error(ex.getMessage());
		}
		return resp;
	}

	private String normalizeIssueKey(String issueKey) {
		return issueKey.toUpperCase();
	}

	@Override
	@Path("/getTreeViewer")
	@GET
	public Response getTreeViewer(@QueryParam("projectKey") String projectKey,
			@QueryParam("rootElementType") String rootElementTypeString) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		if ("codeClass".equals(rootElementTypeString)) {
			TreeViewer treeViewer = new TreeViewer(projectKey);
			return Response.ok(treeViewer).build();
		} else {
			KnowledgeType rootType = KnowledgeType.getKnowledgeType(rootElementTypeString);
			if (rootType == KnowledgeType.OTHER) {
				rootType = KnowledgeType.DECISION;
			}
			TreeViewer treeViewer = new TreeViewer(projectKey, rootType);
			return Response.ok(treeViewer).build();
		}
	}

	@Override
	@Path("/getTreeViewerForSingleElement")
	@POST
	public Response getTreeViewerForSingleElement(@Context HttpServletRequest request,
			@QueryParam("jiraIssueKey") String jiraIssueKey, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Invalid parameters given. Tree viewer not be created.")).build();
		}
		if (jiraIssueKey == null || !jiraIssueKey.contains("-")) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Jira issue key is not valid."))
					.build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}

		TreeViewer treeViewer = new TreeViewer(jiraIssueKey, filterSettings);
		return Response.ok(treeViewer).build();
	}

	@Override
	@Path("/getEvolutionData")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getEvolutionData(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "HttpServletRequest is null. Timeline could not be created."))
					.build();
		}
		if (filterSettings == null || filterSettings.getProjectKey() == null
				|| filterSettings.getProjectKey().isBlank()) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Project key is not valid."))
					.build();
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		VisTimeLine timeLine = new VisTimeLine(user, filterSettings);
		return Response.ok(timeLine).build();
	}

	@Override
	@Path("/getTreant")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTreant(@Context HttpServletRequest request, @QueryParam("elementKey") String elementKey,
			@QueryParam("depthOfTree") String depthOfTree, @QueryParam("searchTerm") String searchTerm) {

		if (elementKey == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Treant cannot be shown since element key is invalid.")).build();
		}
		String projectKey = getProjectKey(elementKey);
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		int depth = 4; // default value
		try {
			depth = Integer.parseInt(depthOfTree);
		} catch (NumberFormatException e) {
			LOGGER.error(
					"Depth of tree could not be parsed, the default value of 4 is used. Message: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Treant cannot be shown since depth of Tree is NaN")).build();
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		Treant treant = new Treant(projectKey, elementKey, depth, searchTerm, user);
		return Response.ok(treant).build();
	}

	@Override
	@Path("/getClassTreant")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getClassTreant(@Context HttpServletRequest request, @QueryParam("elementKey") String elementKey,
			@QueryParam("depthOfTree") String depthOfTree, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("checkboxflag") Boolean checkboxflag, @QueryParam("isIssueView") Boolean isIssueView) {
		if (elementKey == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Treant cannot be shown since element key is invalid.")).build();
		}
		String projectKey = getProjectKey(elementKey);
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		int depth = 4; // default value
		try {
			depth = Integer.parseInt(depthOfTree);
		} catch (NumberFormatException e) {
			LOGGER.error(
					"Depth of tree could not be parsed, the default value of 4 is used. Message: " + e.getMessage());
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Treant cannot be shown since depth of Tree is NaN")).build();
		}
		try {
			KnowledgeElement element = new KnowledgeElementImpl();
			if (!isIssueView) {
				CodeClassKnowledgeElementPersistenceManager ccManager = new CodeClassKnowledgeElementPersistenceManager(
						projectKey);
				element = ccManager.getDecisionKnowledgeElement(elementKey);
			} else {
				KnowledgePersistenceManager kpManager = new KnowledgePersistenceManagerImpl(projectKey);
				element = kpManager.getJiraIssueManager().getDecisionKnowledgeElement(elementKey);
			}
			Treant treant = new Treant(projectKey, element, depth, searchTerm, "treant-container-class", checkboxflag,
					isIssueView);
			return Response.ok(treant).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Treant cannot be shown"))
					.build();
		}

	}

	@Override
	@Path("/getVis")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getVis(@Context HttpServletRequest request, FilterSettings filterSettings,
			@QueryParam("elementKey") String rootElementKey) {
		if (checkIfElementIsValid(rootElementKey).getStatus() != Status.OK.getStatusCode()) {
			return checkIfElementIsValid(rootElementKey);
		}
		if (filterSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "The filter settings are null. Vis graph could not be created."))
					.build();
		}
		if (request == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "HttpServletRequest is null. Vis graph could not be created."))
					.build();
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		VisGraph visGraph = new VisGraph(user, filterSettings, rootElementKey);
		return Response.ok(visGraph).build();
	}

	@Override
	@Path("/getCompareVis")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getCompareVis(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Invalid parameters given. Vis graph could not be created."))
					.build();
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		VisGraph graph = new VisGraph(user, filterSettings);
		return Response.ok(graph).build();
	}

	@Override
	@Path("/getFilterSettings")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getFilterSettings(@Context HttpServletRequest request, @QueryParam("searchTerm") String searchTerm,
			@QueryParam("elementKey") String elementKey) {
		String projectKey;
		if (checkIfProjectKeyIsValid(elementKey).getStatus() == Status.OK.getStatusCode()) {
			projectKey = elementKey;
		} else if (checkIfElementIsValid(elementKey).getStatus() == Status.OK.getStatusCode()) {
			projectKey = getProjectKey(elementKey);
		} else {
			return checkIfElementIsValid(elementKey);
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		return Response.ok(new FilterSettingsImpl(projectKey, searchTerm, user)).build();
	}

	@Override
	@Path("/getDecisionMatrix")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDecisionMatrix(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		List<KnowledgeElement> decisions = getAllDecisions(projectKey);
		Matrix matrix = new Matrix(projectKey, decisions);
		return Response.ok(matrix).build();
	}

	// TODO Remove
	private List<KnowledgeElement> getAllDecisions(String projectKey) {
		return KnowledgeGraph.getOrCreate(projectKey).getElements(KnowledgeType.DECISION);
	}

	@Override
	@Path("/getDecisionGraph")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDecisionGraph(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (filterSettings == null) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "The filter settings are null. Knowledge graph could not be accessed."))
					.build();
		}
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(filterSettings.getProjectKey());
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		VisGraph graph = new VisGraph(user, filterSettings);
		return Response.ok(graph).build();
	}

	private String getProjectKey(String elementKey) {
		return elementKey.split("-")[0];
	}

	private Response checkIfProjectKeyIsValid(String projectKey) {
		if (projectKey == null) {
			return projectKeyIsInvalid();
		}
		ProjectManager projectManager = ComponentAccessor.getProjectManager();
		Project project = projectManager.getProjectByCurrentKey(projectKey);
		if (project == null) {
			return projectKeyIsInvalid();
		}
		return Response.status(Status.OK).build();
	}

	private Response checkIfElementIsValid(String elementKey) {
		if (elementKey == null) {
			return jiraIssueKeyIsInvalid();
		}
		String projectKey = getProjectKey(elementKey);
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		return Response.status(Status.OK).build();
	}

	private Response projectKeyIsInvalid() {
		String message = "Decision knowledge elements cannot be shown since the project key is invalid.";
		LOGGER.error(message);
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", message)).build();
	}

	private Response jiraIssueKeyIsInvalid() {
		String message = "Decision knowledge elements cannot be shown" + " since the Jira issue key is invalid.";
		LOGGER.error(message);
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", message)).build();
	}
}
