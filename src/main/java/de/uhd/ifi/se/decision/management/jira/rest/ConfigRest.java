package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
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

	Response setKnowledgeExtractedFromIssues(HttpServletRequest request, String projectKey,
			String isKnowledgeExtractedFromIssues);

	Response setKnowledgeTypeEnabled(@Context HttpServletRequest request, String projectKey,
			String isKnowledgeTypeEnabledString, String knowledgeType);

	Response setWebhookEnabled(HttpServletRequest request, String projectKey, String isActivatedString);

	Response setWebhookData(HttpServletRequest request, String projectKey, String webhookUrl, String webhookSecret);

	Response setWebhookType(HttpServletRequest request, String projectKey, String webhookType,
			boolean isWebhookTypeEnabled);

	Response setReleaseNoteMapping(HttpServletRequest request, String projectKey, ReleaseNoteCategory category,
			List<String> selectedIssueNames);

	Response cleanDatabases(HttpServletRequest request, String projectKey);

	Response classifyWholeProject(HttpServletRequest request, String projectKey);

	Response trainClassifier(HttpServletRequest request, String projectKey, String arffFileName);

	Response evaluateModel(HttpServletRequest request, String projectKey);

	Response saveArffFile(HttpServletRequest request, String projectKey, boolean useOnlyValidatedData);

	Response setIconParsing(HttpServletRequest request, String projectKey, String isActivatedString);

	Response setUseClassifierForIssueComments(HttpServletRequest request, String projectKey, String isActivatedString);

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