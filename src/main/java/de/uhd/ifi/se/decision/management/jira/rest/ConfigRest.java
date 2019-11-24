package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import de.uhd.ifi.se.decision.management.jira.config.PluginInitializer;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.releasenotes.ReleaseNoteCategory;

/**
 * REST resource for plug-in configuration
 */
public interface ConfigRest {

	Response isIssueStrategy(String projectKey);

	Response isKnowledgeTypeEnabled(String projectKey, String knowledgeType);

	Response getKnowledgeTypes(String projectKey);

	Response getLinkTypes(String projectKey);

	Response getReleaseNoteMapping(String projectKey);

	Response setActivated(HttpServletRequest request, String projectKey, String isActivatedString);

	Response setIssueStrategy(HttpServletRequest request, String projectKey, String isIssueStrategyString);

	Response setKnowledgeExtractedFromGit(HttpServletRequest request, String projectKey,
			String isKnowledgeExtractedFromGit);

	Response setGitUri(HttpServletRequest request, String projectKey, String gitUri);

	@Path("/setKnowledgeExtractedFromIssues")
	@POST
	Response setKnowledgeExtractedFromIssues(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeExtractedFromIssues") String isKnowledgeExtractedFromIssues);

	@Path("/setKnowledgeTypeEnabled")
	@POST
	Response setKnowledgeTypeEnabled(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeTypeEnabled") String isKnowledgeTypeEnabledString,
			@QueryParam("knowledgeType") String knowledgeType);

	@Path("/setWebhookEnabled")
	@POST
	Response setWebhookEnabled(@Context HttpServletRequest request, @QueryParam("projectKey") final String projectKey,
			@QueryParam("isActivated") final String isActivatedString);

	@Path("/setWebhookData")
	@POST
	Response setWebhookData(@Context HttpServletRequest request, @QueryParam("projectKey") final String projectKey,
			@QueryParam("webhookUrl") final String webhookUrl, @QueryParam("webhookSecret") final String webhookSecret);

	@Path("/setWebhookType")
	@POST
	Response setWebhookType(@Context HttpServletRequest request, @QueryParam("projectKey") final String projectKey,
			@QueryParam("webhookType") final String webhookType,
			@QueryParam("isWebhookTypeEnabled") final boolean isWebhookTypeEnabled);

	@Path("/setReleaseNoteMapping")
	@POST
	Response setReleaseNoteMapping(@Context HttpServletRequest request,
			@QueryParam("projectKey") final String projectKey,
			@QueryParam("releaseNoteCategory") final ReleaseNoteCategory category, List<String> selectedIssueNames);

	@Path("/cleanDatabases")
	@POST
	Response cleanDatabases(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey);

	@Path("/classifyWholeProject")
	@POST
	Response classifyWholeProject(@Context HttpServletRequest request,
			@QueryParam("projectKey") final String projectKey);

	@Path("/trainClassifier")
	@POST
	Response trainClassifier(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("arffFileName") String arffFileName);

	@Path("/evaluateModel")
	@POST
	Response evaluateModel(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey);

	@Path("/saveArffFile")
	@POST
	Response saveArffFile(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("useOnlyValidatedData") boolean useOnlyValidatedData);

	@Path("/setIconParsing")
	@POST
	Response setIconParsing(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivatedString") String isActivatedString);

	@Path("/setUseClassifierForIssueComments")
	@POST
	Response setUseClassifierForIssueComments(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isClassifierUsedForIssues") String isActivatedString);

	static void manageDefaultIssueTypes(String projectKey, boolean isIssueStrategy) {
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaultTypes();
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			if (isIssueStrategy) {
				ConfigPersistenceManager.setKnowledgeTypeEnabled(projectKey, knowledgeType.toString(), true);
				PluginInitializer.createIssueType(knowledgeType.toString());
				PluginInitializer.addIssueTypeToScheme(knowledgeType.toString(), projectKey);
			} else {
				PluginInitializer.removeIssueTypeFromScheme(knowledgeType.toString(), projectKey);
			}
		}
	}
}