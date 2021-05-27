package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.user.ApplicationUser;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.extraction.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.extraction.GitClient;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.extraction.versioncontrol.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.JiraIssuePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.view.diffviewer.DiffViewer;

/**
 * REST resource for configuration and usage of the git connection.
 */
@Path("/git")
public class GitRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(GitRest.class);

	@Path("/setKnowledgeExtractedFromGit")
	@POST
	public Response setKnowledgeExtractedFromGit(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeExtractedFromGit") boolean isKnowledgeExtractedFromGit) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration(projectKey);
		gitConfig.setActivated(isKnowledgeExtractedFromGit);
		ConfigPersistenceManager.saveGitConfiguration(projectKey, gitConfig);
		ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, "Code", isKnowledgeExtractedFromGit);

		// deactivate other git extraction if false
		if (!isKnowledgeExtractedFromGit) {
			GitClient.instances.remove(projectKey);
		} else {
			// clone or fetch the git repositories
			if (GitClient.getInstance(projectKey) == null) {
				gitConfig.setActivated(false);
				ConfigPersistenceManager.saveGitConfiguration(projectKey, gitConfig);
				return Response.status(Status.INTERNAL_SERVER_ERROR)
						.entity(ImmutableMap.of("error", "Unable to clone git repository")).build();
			}
		}
		return Response.ok().build();
	}

	@Path("/setPostFeatureBranchCommits")
	@POST
	public Response setPostFeatureBranchCommits(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isPostFeatureBranchCommits") boolean isPostFeatureBranchCommits) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration(projectKey);
		gitConfig.setPostFeatureBranchCommitsActivated(isPostFeatureBranchCommits);
		ConfigPersistenceManager.saveGitConfiguration(projectKey, gitConfig);
		return Response.ok().build();
	}

	@Path("/setPostDefaultBranchCommits")
	@POST
	public Response setPostDefaultBranchCommits(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isPostDefaultBranchCommits") boolean isPostDefaultBranchCommits) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration(projectKey);
		gitConfig.setPostDefaultBranchCommitsActivated(isPostDefaultBranchCommits);
		ConfigPersistenceManager.saveGitConfiguration(projectKey, gitConfig);
		if (isPostDefaultBranchCommits) {
			ApplicationUser user = AuthenticationManager.getUser(request);
			List<Issue> jiraIssues = JiraIssuePersistenceManager.getAllJiraIssuesForProject(user, projectKey);
			jiraIssues
					.forEach(jiraIssue -> new CommitMessageToCommentTranscriber(jiraIssue).postDefaultBranchCommits());
		}
		return Response.ok().build();
	}

	@Path("/setGitRepositoryConfigurations")
	@POST
	public Response setGitRepositoryConfigurations(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey, List<GitRepositoryConfiguration> gitRepositoryConfigurations) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (gitRepositoryConfigurations == null
				|| !GitRepositoryConfiguration.areAllGitRepositoryConfigurationsValid(gitRepositoryConfigurations)) {
			return Response.status(Status.BAD_REQUEST).entity(
					ImmutableMap.of("error", "Git repository configurations could not be set because they are null."))
					.build();
		}
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration(projectKey);
		gitConfig.setGitRepoConfigurations(gitRepositoryConfigurations);
		ConfigPersistenceManager.saveGitConfiguration(projectKey, gitConfig);
		return Response.ok().build();
	}

	@Path("/setCodeFileEndings")
	@POST
	public Response setCodeFileEndings(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			Map<String, String> codeFileEndings) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (codeFileEndings == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Code file endings could not be set because they are null."))
					.build();
		}
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration(projectKey);
		gitConfig.setCodeFileEndings(codeFileEndings);
		ConfigPersistenceManager.saveGitConfiguration(projectKey, gitConfig);
		return Response.ok().build();
	}

	@Path("/deleteGitRepos")
	@POST
	public Response deleteGitRepos(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		GitClient gitClient = GitClient.getInstance(projectKey);
		if (!gitClient.deleteRepositories()) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(ImmutableMap.of("error", "Git repositories could not be deleted.")).build();
		}
		new CodeClassPersistenceManager(projectKey).deleteKnowledgeElements();
		return Response.ok().build();
	}

	@Path("/elementsFromBranchesOfProject")
	@GET
	public Response getElementsFromAllBranchesOfProject(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		if (!ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error", "Git extraction is disabled in project settings.")).build();
		}

		LOGGER.info("Feature branch dashboard opened for project:" + projectKey);
		return Response.ok(new DiffViewer(projectKey)).build();
	}

	@Path("/elementsFromBranchesOfJiraIssue")
	@GET
	public Response getElementsOfFeatureBranchForJiraIssue(@Context HttpServletRequest request,
			@QueryParam("issueKey") String issueKey) {
		if (request == null || issueKey == null || issueKey.isBlank()) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Invalid parameters given. Knowledge from feature branch cannot be shown.")).build();
		}
		String projectKey = getProjectKey(issueKey);
		Issue issue = ComponentAccessor.getIssueManager().getIssueObject(issueKey);
		if (issue == null) {
			return jiraIssueKeyIsInvalid();
		}
		if (!ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error", "Git extraction is disabled in project settings.")).build();
		}
		new CommitMessageToCommentTranscriber(issue).postCommitsIntoJiraIssueComments();

		LOGGER.info("Feature branch dashboard opened for Jira issue:" + issueKey);
		return Response.ok(new DiffViewer(projectKey, issueKey)).build();
	}

	// TODO Change to POST and pass FilterSettings object
	@Path("/getSummarizedCode")
	@GET
	public Response getSummarizedCode(@QueryParam("id") long id, @QueryParam("projectKey") String projectKey,
			@QueryParam("documentationLocation") String documentationLocation,
			@QueryParam("probability") int probability) {
		if (projectKey == null || id <= 0) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Getting summarized code failed due to a bad request.")).build();
		}

		if (!ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error",
							"Getting summarized code failed since git extraction is disabled for this project."))
					.build();
		}

		KnowledgeElement element = KnowledgePersistenceManager.getOrCreate(projectKey).getKnowledgeElement(id,
				documentationLocation);
		Issue jiraIssue = element.getJiraIssue();

		String summary = new CodeSummarizer(projectKey).createSummary(jiraIssue, probability);
		if (summary == null || summary.isEmpty()) {
			summary = "This Jira issue does not have any code committed.";
		}
		return Response.ok(summary).build();
	}

	private String getProjectKey(String elementKey) {
		return elementKey.split("-")[0].toUpperCase(Locale.ENGLISH);
	}

	private Response jiraIssueKeyIsInvalid() {
		String message = "Decision knowledge elements cannot be shown since the Jira issue key is invalid.";
		LOGGER.error(message);
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", message)).build();
	}

}