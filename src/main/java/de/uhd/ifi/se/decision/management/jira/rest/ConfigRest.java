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

	Response classifyWholeProject(HttpServletRequest request, String projectKey);

	Response cleanDatabases(HttpServletRequest request, String projectKey);

	Response evaluateModel(HttpServletRequest request, String projectKey);

	Response getKnowledgeTypes(String projectKey);

	Response getLinkTypes(String projectKey);

	Response getReleaseNoteMapping(String projectKey);

	Response isIssueStrategy(String projectKey);

	Response isKnowledgeTypeEnabled(String projectKey, String knowledgeType);

	Response saveArffFile(HttpServletRequest request, String projectKey, boolean useOnlyValidatedData);

	Response setActivated(HttpServletRequest request, String projectKey, String isActivatedString);

	Response setGitUri(HttpServletRequest request, String projectKey, String gitUri);

	Response setIconParsing(HttpServletRequest request, String projectKey, String isActivatedString);

	Response setIssueStrategy(HttpServletRequest request, String projectKey, String isIssueStrategyString);

	Response setKnowledgeExtractedFromGit(HttpServletRequest request, String projectKey,
			String isKnowledgeExtractedFromGit);

	Response setKnowledgeExtractedFromIssues(HttpServletRequest request, String projectKey,
			String isKnowledgeExtractedFromIssues);

	Response setKnowledgeTypeEnabled(@Context HttpServletRequest request, String projectKey,
			String isKnowledgeTypeEnabledString, String knowledgeType);

	Response setReleaseNoteMapping(HttpServletRequest request, String projectKey, ReleaseNoteCategory category,
			List<String> selectedIssueNames);

	Response setUseClassifierForIssueComments(HttpServletRequest request, String projectKey, String isActivatedString);

	Response setWebhookData(HttpServletRequest request, String projectKey, String webhookUrl, String webhookSecret);

	Response setWebhookEnabled(HttpServletRequest request, String projectKey, String isActivatedString);

	Response setWebhookType(HttpServletRequest request, String projectKey, String webhookType,
			boolean isWebhookTypeEnabled);

	Response trainClassifier(HttpServletRequest request, String projectKey, String arffFileName);

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