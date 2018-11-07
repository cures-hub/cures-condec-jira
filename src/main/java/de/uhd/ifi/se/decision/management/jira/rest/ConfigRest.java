package de.uhd.ifi.se.decision.management.jira.rest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.config.IssueTypeManager;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.issuetype.IssueType;
import com.atlassian.jira.jql.builder.JqlClauseBuilder;
import com.atlassian.jira.jql.builder.JqlQueryBuilder;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.config.AuthenticationManager;
import de.uhd.ifi.se.decision.management.jira.config.PluginInitializer;
import de.uhd.ifi.se.decision.management.jira.extraction.connector.ViewConnector;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.ActiveObjectsManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.oauth.OAuthManager;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.persistence.GenericLinkManager;

/**
 * REST resource for plug-in configuration
 */
@Path("/config")
public class ConfigRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigRest.class);

	@Path("/setActivated")
	@POST
	public Response setActivated(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivated") String isActivatedString) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build();
		}
		try {
			boolean isActivated = Boolean.valueOf(isActivatedString);
			ConfigPersistence.setActivated(projectKey, isActivated);
			setDefaultKnowledgeTypesEnabled(projectKey, isActivated, ConfigPersistence.isIssueStrategy(projectKey));
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	public static void setDefaultKnowledgeTypesEnabled(String projectKey, boolean isActivated,
			boolean isIssueStrategy) {
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaulTypes();
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			ConfigPersistence.setKnowledgeTypeEnabled(projectKey, knowledgeType.toString(), isActivated);
		}
	}

	@Path("/isIssueStrategy")
	@GET
	public Response isIssueStrategy(@QueryParam("projectKey") final String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Boolean isIssueStrategy = ConfigPersistence.isIssueStrategy(projectKey);
		return Response.ok(isIssueStrategy).build();
	}

	@Path("/setIssueStrategy")
	@POST
	public Response setIssueStrategy(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isIssueStrategy") String isIssueStrategyString) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isIssueStrategyString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isIssueStrategy = null"))
					.build();
		}
		try {
			boolean isIssueStrategy = Boolean.valueOf(isIssueStrategyString);
			ConfigPersistence.setIssueStrategy(projectKey, isIssueStrategy);
			manageDefaultIssueTypes(projectKey, isIssueStrategy);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	public static void manageDefaultIssueTypes(String projectKey, boolean isIssueStrategy) {
		Set<KnowledgeType> defaultKnowledgeTypes = KnowledgeType.getDefaulTypes();
		for (KnowledgeType knowledgeType : defaultKnowledgeTypes) {
			if (isIssueStrategy) {
				ConfigPersistence.setKnowledgeTypeEnabled(projectKey, knowledgeType.toString(), true);
				PluginInitializer.createIssueType(knowledgeType.toString());
				PluginInitializer.addIssueTypeToScheme(knowledgeType.toString(), projectKey);
			} else {
				PluginInitializer.removeIssueTypeFromScheme(knowledgeType.toString(), projectKey);
			}
		}
	}

	@Path("/isKnowledgeExtractedFromGit")
	@GET
	public Response isKnowledgeExtractedFromGit(@QueryParam("projectKey") final String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Boolean isKnowledgeExtractedFromGit = ConfigPersistence.isKnowledgeExtractedFromGit(projectKey);
		return Response.ok(isKnowledgeExtractedFromGit).build();
	}

	@Path("/setKnowledgeExtractedFromGit")
	@POST
	public Response setKnowledgeExtractedFromGit(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeExtractedFromGit") String isKnowledgeExtractedFromGit) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isKnowledgeExtractedFromGit == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "isKnowledgeExtractedFromGit = null")).build();
		}
		try {
			ConfigPersistence.setKnowledgeExtractedFromGit(projectKey, Boolean.valueOf(isKnowledgeExtractedFromGit));
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/isKnowledgeExtractedFromIssues")
	@GET
	public Response isKnowledgeExtractedFromIssues(@QueryParam("projectKey") final String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Boolean isKnowledgeExtractedFromIssues = ConfigPersistence.isKnowledgeExtractedFromIssues(projectKey);
		return Response.ok(isKnowledgeExtractedFromIssues).build();
	}

	@Path("/setKnowledgeExtractedFromIssues")
	@POST
	public Response setKnowledgeExtractedFromIssues(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeExtractedFromIssues") String isKnowledgeExtractedFromIssues) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isKnowledgeExtractedFromIssues == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "isKnowledgeExtractedFromIssues = null")).build();
		}
		try {
			ConfigPersistence.setKnowledgeExtractedFromIssues(projectKey,
					Boolean.valueOf(isKnowledgeExtractedFromIssues));
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/isKnowledgeTypeEnabled")
	@GET
	public Response isKnowledgeTypeEnabled(@QueryParam("projectKey") final String projectKey,
			@QueryParam("knowledgeType") String knowledgeType) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		Boolean isKnowledgeTypeEnabled = ConfigPersistence.isKnowledgeTypeEnabled(projectKey, knowledgeType);
		return Response.ok(isKnowledgeTypeEnabled).build();
	}

	@Path("/setKnowledgeTypeEnabled")
	@POST
	public Response setKnowledgeTypeEnabled(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isKnowledgeTypeEnabled") String isKnowledgeTypeEnabledString,
			@QueryParam("knowledgeType") String knowledgeType) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isKnowledgeTypeEnabledString == null || knowledgeType == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isKnowledgeTypeEnabled = null"))
					.build();
		}
		try {
			boolean isKnowledgeTypeEnabled = Boolean.valueOf(isKnowledgeTypeEnabledString);
			ConfigPersistence.setKnowledgeTypeEnabled(projectKey, knowledgeType, isKnowledgeTypeEnabled);
			if (ConfigPersistence.isIssueStrategy(projectKey)) {
				if (isKnowledgeTypeEnabled) {
					PluginInitializer.createIssueType(knowledgeType);
					PluginInitializer.addIssueTypeToScheme(knowledgeType, projectKey);
				} else {
					PluginInitializer.removeIssueTypeFromScheme(knowledgeType, projectKey);
				}
			}
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/getKnowledgeTypes")
	@GET
	public Response getKnowledgeTypes(@QueryParam("projectKey") final String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		List<String> knowledgeTypes = new ArrayList<String>();
		for (KnowledgeType knowledgeType : KnowledgeType.values()) {
			boolean isEnabled = ConfigPersistence.isKnowledgeTypeEnabled(projectKey, knowledgeType);
			if (isEnabled) {
				knowledgeTypes.add(knowledgeType.toString());
			}
		}
		return Response.ok(knowledgeTypes).build();
	}

	@Path("/getDefaultKnowledgeTypes")
	@GET
	public Response getDefaultKnowledgeTypes(@QueryParam("projectKey") final String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		List<String> defaultKnowledgeTypes = new ArrayList<String>();
		for (KnowledgeType knowledgeType : KnowledgeType.getDefaulTypes()) {
			defaultKnowledgeTypes.add(knowledgeType.toString());
		}
		return Response.ok(defaultKnowledgeTypes).build();
	}

	@Path("/setWebhookEnabled")
	@POST
	public Response setWebhookEnabled(@Context HttpServletRequest request,
			@QueryParam("projectKey") final String projectKey,
			@QueryParam("isActivated") final String isActivatedString) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error", "Webhook Activation bookean = null")).build();
		}
		try {
			boolean isActivated = Boolean.valueOf(isActivatedString);
			ConfigPersistence.setWebhookEnabled(projectKey, isActivated);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/setWebhookData")
	@POST
	public Response setWebhookData(@Context HttpServletRequest request,
			@QueryParam("projectKey") final String projectKey, @QueryParam("webhookUrl") final String webhookUrl,
			@QueryParam("webhookSecret") final String webhookSecret) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (webhookUrl == null || webhookSecret == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "webhook Data = null")).build();
		}
		try {
			ConfigPersistence.setWebhookUrl(projectKey, webhookUrl);
			ConfigPersistence.setWebhookSecret(projectKey, webhookSecret);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/setWebhookType")
	@POST
	public Response setWebhookType(@Context HttpServletRequest request,
		@QueryParam("projectKey") final String projectKey, @QueryParam("webhookType") final String webhookType,
								   @QueryParam("isWebhookTypeEnabled") final boolean isWebhookTypeEnabled) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (webhookType == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "webhook Type = null")).build();
		}
		try {
			ConfigPersistence.setWebhookType(projectKey, webhookType, isWebhookTypeEnabled);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/clearSentenceDatabase")
	@POST
	public Response clearSentenceDatabase(@Context HttpServletRequest request,
			@QueryParam("projectKey") final String projectKey) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		try {
			// Use link validation over deletion. Deletion is useful during development
			// process
			// ActiveObjectsManager.clearSentenceDatabaseForProject(projectKey);
			GenericLinkManager.clearInvalidLinks();
			ActiveObjectsManager.cleanSentenceDatabaseForProject(projectKey);
			ActiveObjectsManager.createLinksForNonLinkedElementsForProject(projectKey);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/classifyWholeProject")
	@POST
	public Response classifyWholeProject(@Context HttpServletRequest request,
			@QueryParam("projectKey") final String projectKey) {
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		try {
			ApplicationUser user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
			JqlClauseBuilder jqlClauseBuilder = JqlQueryBuilder.newClauseBuilder();
			SearchService searchService = ComponentAccessor.getComponentOfType(SearchService.class);

			com.atlassian.query.Query query = jqlClauseBuilder.project(projectKey).buildQuery();
			com.atlassian.jira.issue.search.SearchResults searchResults = null;

			searchResults = searchService.search(user, query, PagerFilter.getUnlimitedFilter());
			for (Issue issue : searchResults.getIssues()) {
				new ViewConnector(issue, false);
			}

			return Response.ok(Status.ACCEPTED).entity(ImmutableMap.of("isSucceeded", true)).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).entity(ImmutableMap.of("isSucceeded", false)).build();
		}
	}

	@Path("/setIconParsing")
	@POST
	public Response setIconParsing(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivatedString") String isActivatedString) {
		System.out.println(isActivatedString);
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build();
		}
		try {
			boolean isActivated = Boolean.valueOf(isActivatedString);
			ConfigPersistence.setIconParsing(projectKey, isActivated);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/setUseClassiferForIssueComments")
	@POST
	public Response setUseClassiferForIssueComments(@Context HttpServletRequest request,
			@QueryParam("projectKey") String projectKey,
			@QueryParam("isClassifierUsedForIssues") String isActivatedString) {
		System.out.println(isActivatedString);
		Response isValidDataResponse = checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (isActivatedString == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "isActivated = null")).build();
		}
		try {
			boolean isActivated = Boolean.valueOf(isActivatedString);
			ConfigPersistence.setUseClassiferForIssueComments(projectKey, isActivated);
			return Response.ok(Status.ACCEPTED).build();
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return Response.status(Status.CONFLICT).build();
		}
	}

	@Path("/isIconParsing")
	@GET
	public Response isIconParsing(@QueryParam("projectKey") final String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		boolean isIconParsing = ConfigPersistence.isIconParsing(projectKey);
		return Response.ok(isIconParsing).build();
	}

	@Path("/getProjectIssueTypes")
	@GET
	public Response getProjectIssueTypes(@QueryParam("projectKey") final String projectKey) {
		Response checkIfProjectKeyIsValidResponse = checkIfProjectKeyIsValid(projectKey);
		if (checkIfProjectKeyIsValidResponse.getStatus() != Status.OK.getStatusCode()) {
			return checkIfProjectKeyIsValidResponse;
		}
		IssueTypeManager issueTypeManager = ComponentAccessor.getComponent(IssueTypeManager.class);
		Collection<IssueType> types = issueTypeManager.getIssueTypes();
		Collection<String> typeNames = new ArrayList<String>();
		for (IssueType type : types) {
			typeNames.add(type.getName());
		}
		return Response.ok(typeNames).build();
	}

	@Path("/getRequestToken")
	@GET
	public Response getRequestToken(@QueryParam("projectKey") String projectKey, @QueryParam("baseURL") String baseURL,
			@QueryParam("privateKey") String privateKey, @QueryParam("consumerKey") String consumerKey) {
		if (baseURL != null && privateKey != null && consumerKey != null) {
			privateKey = privateKey.replaceAll(" ", "+");
			ConfigPersistence.setOauthJiraHome(baseURL);
			ConfigPersistence.setPrivateKey(privateKey);
			ConfigPersistence.setConsumerKey(consumerKey);
			OAuthManager oAuthManager = new OAuthManager();
			String requestToken = oAuthManager.retrieveRequestToken(consumerKey, privateKey);

			ConfigPersistence.setRequestToken(requestToken);
			// TODO: Tim: why do we have to use a map here, Response.ok(requestToken).build() does not work, why?
			return Response.status(Status.OK).entity(ImmutableMap.of("result", requestToken)).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Request token could not be retrieved since the base URL, private key, and/or consumer key are missing."))
					.build();
		}
	}

	@Path("/getAccessToken")
	@GET
	public Response getAccessToken(@QueryParam("projectKey") String projectKey, @QueryParam("baseURL") String baseURL,
			@QueryParam("privateKey") String privateKey, @QueryParam("consumerKey") String consumerKey,
			@QueryParam("requestToken") String requestToken, @QueryParam("secret") String secret) {
		if (baseURL != null && privateKey != null && consumerKey != null) {

			privateKey = privateKey.replaceAll(" ", "+");

			ConfigPersistence.setOauthJiraHome(baseURL);
			ConfigPersistence.setRequestToken(requestToken);
			ConfigPersistence.setPrivateKey(privateKey);
			ConfigPersistence.setConsumerKey(consumerKey);
			ConfigPersistence.setSecretForOAuth(secret);

			OAuthManager oAuthManager = new OAuthManager();
			String accessToken = oAuthManager.retrieveAccessToken(requestToken, secret, consumerKey, privateKey);

			ConfigPersistence.setAccessToken(accessToken);

			// TODO: Tim: why do we have to use a map here, Response.ok(accessToken).build() does not work, why?
			return Response.status(Status.OK).entity(ImmutableMap.of("result", accessToken)).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Access token could not be retrieved since the base URL, private key, and/or consumer key are missing."))
					.build();
		}
	}

	private Response checkIfDataIsValid(HttpServletRequest request, String projectKey) {
		if (request == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "request = null")).build();
		}

		Response projectResponse = checkIfProjectKeyIsValid(projectKey);
		if (projectResponse.getStatus() != Status.OK.getStatusCode()) {
			return projectResponse;
		}

		Response userResponse = checkIfUserIsAuthorized(request, projectKey);
		if (userResponse.getStatus() != Status.OK.getStatusCode()) {
			return userResponse;
		}

		return Response.status(Status.OK).build();
	}

	private Response checkIfUserIsAuthorized(HttpServletRequest request, String projectKey) {
		String username = AuthenticationManager.getUsername(request);
		boolean isProjectAdmin = AuthenticationManager.isProjectAdmin(username, projectKey);
		if (isProjectAdmin) {
			return Response.status(Status.OK).build();
		}
		LOGGER.warn("Unauthorized user (name:{}) tried to change configuration.", username);
		return Response.status(Status.UNAUTHORIZED).entity(ImmutableMap.of("error", "Authorization failed.")).build();
	}

	private Response checkIfProjectKeyIsValid(String projectKey) {
		if (projectKey == null || projectKey.equals("")) {
			LOGGER.error("Project configuration could not be changed since the project key is invalid.");
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Project key is invalid."))
					.build();
		}
		return Response.status(Status.OK).build();
	}
}