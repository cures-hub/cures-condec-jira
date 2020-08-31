package de.uhd.ifi.se.decision.management.jira.rest;

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
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitDecXtract;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.DecisionTable;
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
public class ViewRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ViewRest.class);
	private GitClient gitClient;

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
		gitClient = GitClient.getOrCreate(projectKey);
		List<Ref> branches = gitClient.getAllRemoteBranches();
		for (Ref branch : branches) {
			Matcher branchMatcher = filterPattern.matcher(branch.getName());
			if (branchMatcher.find()) {
				transcriber.postComments(branch);
			}
		}

		gitClient.closeAll();
		return resp;
	}

	private Response getDiffViewerResponse(String projectKey, String filter) {
		gitClient = GitClient.getOrCreate(projectKey);
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
		gitClient.closeAll();
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

	/**
	 * Returns a jstree tree viewer that matches the {@link FilterSettings}. If a
	 * knowledge element is selected in the {@link FilterSettings}, the tree viewer
	 * comprises only one tree with the selected element as the root element. If no
	 * element is selected, the tree viewer contains a list of trees.
	 * 
	 * @param filterSettings
	 *            For example, the {@link FilterSettings} cover the selected element
	 *            and the knowledge types to be shown. The selected element can be
	 *            null.
	 */
	@Path("/getTreeViewer")
	@POST
	public Response getTreeViewer(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Invalid parameters given. Tree viewer not be created.")).build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		TreeViewer treeViewer = new TreeViewer(filterSettings);
		return Response.ok(treeViewer).build();
	}

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

	// TODO Rename to "getDecisionProblems"
	@Path("/getDecisionIssues")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDecisionIssues(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getSelectedElement() == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Decision problems cannot be shown since filter settings or element key are invalid."))
					.build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		DecisionTable decisionTable = new DecisionTable(projectKey);
		decisionTable.setIssues(filterSettings, user);

		return Response.ok(decisionTable.getIssues()).build();
	}

	// TODO Pass FilterSettings
	@Path("/getDecisionTable")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDecisionTable(@Context HttpServletRequest request, @QueryParam("elementId") long id,
			@QueryParam("location") String location, @QueryParam("elementKey") String elementKey) {
		if (elementKey == null || id == -1 || location == null) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "Decision Table cannot be shown due to missing or invalid parameters."))
					.build();
		}
		String projectKey = getProjectKey(elementKey);
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		DecisionTable decisionTable = new DecisionTable(projectKey);
		ApplicationUser user = AuthenticationManager.getUser(request);
		decisionTable.setDecisionTableForIssue(id, location, user);
		return Response.ok(decisionTable.getDecisionTableData()).build();
	}

	@Path("/getDecisionTableCriteria")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getDecisionTableCriteria(@Context HttpServletRequest request,
			@QueryParam("elementKey") String elementKey) {
		if (request == null || elementKey == null) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "Decision Table cannot be shown due to missing or invalid parameters."))
					.build();
		}
		String projectKey = getProjectKey(elementKey);
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		DecisionTable decisionTable = new DecisionTable(projectKey);
		ApplicationUser user = AuthenticationManager.getUser(request);
		return Response.ok(decisionTable.getDecisionTableCriteria(user)).build();
	}

	@Path("/getTreant")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getTreant(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null || filterSettings.getSelectedElement() == null
				|| filterSettings.getSelectedElement().getKey() == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Treant cannot be shown since request or element key is invalid."))
					.build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Treant treant = new Treant(filterSettings);
		return Response.ok(treant).build();
	}

	@Path("/getVis")
	@POST
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getVis(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (request == null || filterSettings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"The HttpServletRequest or the filter settings are null. Vis graph could not be created."))
					.build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		ApplicationUser user = AuthenticationManager.getUser(request);
		VisGraph visGraph = new VisGraph(user, filterSettings);
		return Response.ok(visGraph).build();
	}

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
		return Response.ok(new FilterSettings(projectKey, searchTerm, user)).build();
	}

	@Path("/getDecisionMatrix")
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	// TODO Pass filter settings
	public Response getDecisionMatrix(@Context HttpServletRequest request, FilterSettings filterSettings) {
		if (filterSettings == null || filterSettings.getProjectKey() == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Matrix cannot be shown since filter settings are invalid."))
					.build();
		}
		String projectKey = filterSettings.getProjectKey();
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Matrix matrix = new Matrix(filterSettings);
		return Response.ok(matrix).build();
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
