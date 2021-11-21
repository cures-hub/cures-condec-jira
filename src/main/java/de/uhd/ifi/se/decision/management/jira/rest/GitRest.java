package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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

import de.uhd.ifi.se.decision.management.jira.config.BasicConfiguration;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.git.CodeSummarizer;
import de.uhd.ifi.se.decision.management.jira.git.CommitMessageToCommentTranscriber;
import de.uhd.ifi.se.decision.management.jira.git.GitClient;
import de.uhd.ifi.se.decision.management.jira.git.GitClientForSingleRepository;
import de.uhd.ifi.se.decision.management.jira.git.config.GitConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.config.GitRepositoryConfiguration;
import de.uhd.ifi.se.decision.management.jira.git.model.ChangedFile;
import de.uhd.ifi.se.decision.management.jira.git.model.Diff;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeGraph;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.persistence.singlelocations.CodeClassPersistenceManager;

/**
 * REST resource for configuration and usage of the git connection.
 */
@Path("/git")
public class GitRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(GitRest.class);

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param isKnowledgeExtractedFromGit
	 *            true if {@link ChangedFile}s and decision knowledge is extracted
	 *            from git. The decision knowledge is both extracted from commit
	 *            messages and code comments.
	 * @return ok if the knowledge extraction from git was successfully activated.
	 */
	@Path("/activate/{projectKey}")
	@POST
	public Response setKnowledgeExtractedFromGit(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, boolean isKnowledgeExtractedFromGit) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration(projectKey);
		gitConfig.setActivated(isKnowledgeExtractedFromGit);
		ConfigPersistenceManager.saveGitConfiguration(projectKey, gitConfig);

		BasicConfiguration basicConfig = ConfigPersistenceManager.getBasicConfiguration(projectKey);
		basicConfig.setKnowledgeTypeEnabled(KnowledgeType.CODE, isKnowledgeExtractedFromGit);
		ConfigPersistenceManager.saveBasicConfiguration(projectKey, basicConfig);

		if (!isKnowledgeExtractedFromGit) {
			// destroy singleton object of GitClient
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

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param isPostFeatureBranchCommits
	 *            true if git commit messages of feature branch commits should be
	 *            posted as Jira issue comments. This enables to integrate decision
	 *            knowledge from commit messages into the {@link KnowledgeGraph}.
	 * @return ok if successfully activated.
	 */
	@Path("/configuration/{projectKey}/post-feature-branch-commits")
	@POST
	public Response setPostFeatureBranchCommits(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, boolean isPostFeatureBranchCommits) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration(projectKey);
		gitConfig.setPostFeatureBranchCommitsActivated(isPostFeatureBranchCommits);
		ConfigPersistenceManager.saveGitConfiguration(projectKey, gitConfig);
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param isPostDefaultBranchCommits
	 *            true if git commit messages of default branch commits (e.g.
	 *            squashed commits) should be posted as Jira issue comments. This
	 *            enables to integrate decision knowledge from commit messages into
	 *            the {@link KnowledgeGraph}.
	 * @return ok if successfully activated and if all messages from commits on the
	 *         default branch(es) were successfully posted to Jira issue comments.
	 */
	@Path("/configuration/{projectKey}/post-default-branch-commits")
	@POST
	public Response setPostDefaultBranchCommits(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, boolean isPostDefaultBranchCommits) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		GitConfiguration gitConfig = ConfigPersistenceManager.getGitConfiguration(projectKey);
		gitConfig.setPostDefaultBranchCommitsActivated(isPostDefaultBranchCommits);
		ConfigPersistenceManager.saveGitConfiguration(projectKey, gitConfig);
		if (isPostDefaultBranchCommits) {
			List<Issue> jiraIssues = KnowledgePersistenceManager.getInstance(projectKey).getJiraIssueManager()
					.getAllJiraIssuesForProject();
			jiraIssues
					.forEach(jiraIssue -> new CommitMessageToCommentTranscriber(jiraIssue).postDefaultBranchCommits());
		}
		return Response.ok().build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @param gitRepositoryConfigurations
	 *            list of configuration details for the git repositories connected
	 *            to the Jira project, i.e., for every
	 *            {@link GitClientForSingleRepository}.
	 * @return ok if the configuration for git repositories connected to the Jira
	 *         project was successfully saved.
	 */
	@Path("/configuration/{projectKey}/repositories")
	@POST
	public Response setGitRepositoryConfigurations(@Context HttpServletRequest request,
			@PathParam("projectKey") String projectKey, List<GitRepositoryConfiguration> gitRepositoryConfigurations) {
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

	/**
	 * HttpServletRequest with an authorized Jira {@link ApplicationUser}.
	 * 
	 * @param projectKey
	 *            of a Jira project.
	 * @param codeFileEndings
	 *            defines which code files are extracted from git and decision
	 *            knowledge from their code comments.
	 * @return ok if code file endings were successfully configured.
	 */
	@Path("/configuration/{projectKey}/file-endings")
	@POST
	public Response setCodeFileEndings(@Context HttpServletRequest request, @PathParam("projectKey") String projectKey,
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

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param projectKey
	 *            of a Jira project.
	 * @return true if all git repositories that were associated to the Jira project
	 *         and also all database entries were successfully deleted.
	 */
	@Path("/{projectKey}")
	@DELETE
	public Response deleteGitRepos(@Context HttpServletRequest request, @PathParam("projectKey") String projectKey) {
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

	/**
	 * @param projectKey
	 *            of a Jira project.
	 * @return {@link Diff} that contains all changes on the default branch and
	 *         feature branches for the entire project.
	 */
	@Path("/diff/project")
	@GET
	public Response getDiffForProject(@QueryParam("projectKey") String projectKey) {
		Response checkIfProjectKeyIsValidResponse = RestParameterChecker.checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		if (!ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error", "Git extraction is disabled in project settings.")).build();
		}

		LOGGER.info("Feature branch dashboard opened for project:" + projectKey);
		GitClient gitClient = GitClient.getInstance(projectKey);
		Diff branchesForProject = gitClient.getDiffForBranchWithName(projectKey);
		branchesForProject.addAll(gitClient.getDiffOfEntireDefaultBranchFromKnowledgeGraph());
		return Response.ok(branchesForProject).build();
	}

	/**
	 * @param request
	 *            HttpServletRequest with an authorized Jira
	 *            {@link ApplicationUser}.
	 * @param jiraIssueKey
	 *            of a Jira issue to show a {@link Diff} for.
	 * @return {@link Diff} that contains all changes on the default branch and
	 *         feature branches for the Jira issue. The commits on the default
	 *         branch need to have the key in their message and the feature
	 *         branch(es) need to have the key in the branch name.
	 */
	@Path("/diff/jira-issue")
	@GET
	public Response getDiffForJiraIssue(@Context HttpServletRequest request,
			@QueryParam("jiraIssueKey") String jiraIssueKey) {
		if (request == null || jiraIssueKey == null || jiraIssueKey.isBlank()) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error",
					"Invalid parameters given. Knowledge from feature branch cannot be shown.")).build();
		}
		Issue jiraIssue = ComponentAccessor.getIssueManager().getIssueObject(jiraIssueKey);
		if (jiraIssue == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Summary cannot be shown since the Jira issue key is invalid."))
					.build();
		}
		String projectKey = jiraIssue.getProjectObject().getKey();
		if (!ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error", "Git extraction is disabled in project settings.")).build();
		}
		new CommitMessageToCommentTranscriber(jiraIssue).postCommitsIntoJiraIssueComments();

		LOGGER.info("Feature branch dashboard opened for Jira issue:" + jiraIssueKey);
		Diff diffForJiraIssue = GitClient.getInstance(projectKey)
				.getDiffForJiraIssueOnDefaultBranchAndFeatureBranches(jiraIssue);
		return Response.ok(diffForJiraIssue).build();
	}

	/**
	 * @param filterSettings
	 *            object of {@link FilterSettings} class that contains the selected
	 *            element for which the summary should be shown.
	 * @param minProbabilityOfCorrectness
	 *            to filter out wrong linkes between the Jira issue and code files
	 *            resulting from tangled changes.
	 * @return summary of code changes for the selected element in the
	 *         {@link FilterSettings}.
	 */
	@Path("/summary")
	@POST
	public Response getSummarizedCode(FilterSettings filterSettings,
			@QueryParam("minProbabilityOfCorrectness") int minProbabilityOfCorrectness) {
		if (filterSettings == null || filterSettings.getSelectedElement() == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Getting summarized code failed due to a bad request.")).build();
		}
		String projectKey = filterSettings.getProjectKey();

		if (!ConfigPersistenceManager.getGitConfiguration(projectKey).isActivated()) {
			return Response.status(Status.SERVICE_UNAVAILABLE)
					.entity(ImmutableMap.of("error",
							"Getting summarized code failed since git extraction is disabled for this project."))
					.build();
		}

		KnowledgeElement element = filterSettings.getSelectedElement();
		Issue jiraIssue = element.getJiraIssue();

		String summary = new CodeSummarizer(projectKey).createSummary(jiraIssue, minProbabilityOfCorrectness);
		if (summary.isBlank()) {
			summary = "This Jira issue does not have any code committed.";
		}
		return Response.ok(summary).build();
	}
}