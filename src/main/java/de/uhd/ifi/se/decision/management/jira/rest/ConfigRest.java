package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 * REST resource for plug-in configuration
 */
public interface ConfigRest {

	Response setActivated(HttpServletRequest request, String projectKey, String isActivated);

	Response isIssueStrategy(String projectKey);

	Response setIssueStrategy(HttpServletRequest request, String projectKey, String isIssueStrategy);

	Response isKnowledgeExtractedFromGit(String projectKey);

	Response setKnowledgeExtractedFromGit(HttpServletRequest request, String projectKey,
			String isKnowledgeExtractedFromGit);

	Response isKnowledgeExtractedFromIssues(String projectKey);

	Response setKnowledgeExtractedFromIssues(HttpServletRequest request, String projectKey,
			String isKnowledgeExtractedFromIssues);

	Response isKnowledgeTypeEnabled(String projectKey, String knowledgeType);

	Response setKnowledgeTypeEnabled(HttpServletRequest request, String projectKey, String isKnowledgeTypeEnabled,
			String knowledgeType);

	Response getKnowledgeTypes(String projectKey);

	Response setWebhookEnabled(HttpServletRequest request, String projectKey, String isActivated);

	Response setWebhookData(HttpServletRequest request, String projectKey, String webhookUrl, String webhookSecret);

	Response setWebhookType(HttpServletRequest request, String projectKey, String webhookType,
			boolean isWebhookTypeEnabled);

	Response clearSentenceDatabase(HttpServletRequest request, String projectKey);

	Response classifyWholeProject(HttpServletRequest request, String projectKey);

	Response setIconParsing(HttpServletRequest request, String projectKey, String isActivatedString);

	Response setUseClassiferForIssueComments(HttpServletRequest request, String projectKey, String isActivatedString);

	Response getRequestToken(String projectKey, String baseURL, String privateKey, String consumerKey);

	Response getAccessToken(String projectKey, String baseURL, String privateKey, String consumerKey,
			String requestToken, String secret);
}