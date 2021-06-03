package de.uhd.ifi.se.decision.management.jira.rest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConfiguration;
import de.uhd.ifi.se.decision.management.jira.webhook.WebhookConnector;

/**
 * REST resource for the configuration and testing of the webhook (see
 * {@link WebhookConfiguration}.
 */
@Path("/webhook")
public class WebhookRest {
	private static final Logger LOGGER = LoggerFactory.getLogger(WebhookRest.class);

	@Path("/setWebhookEnabled")
	@POST
	public Response setWebhookEnabled(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("isActivated") boolean isActivated) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		LOGGER.info("Webhook activation was set to " + isActivated + " for project " + projectKey);
		WebhookConfiguration webhookConfig = ConfigPersistenceManager.getWebhookConfiguration(projectKey);
		webhookConfig.setActivated(isActivated);
		ConfigPersistenceManager.saveWebhookConfiguration(projectKey, webhookConfig);
		return Response.ok().build();
	}

	@Path("/setWebhookData")
	@POST
	public Response setWebhookData(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("webhookUrl") String webhookUrl, @QueryParam("webhookSecret") String webhookSecret) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		if (webhookUrl == null || webhookSecret == null) {
			return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "webhook Data = null")).build();
		}
		WebhookConfiguration webhookConfig = ConfigPersistenceManager.getWebhookConfiguration(projectKey);
		webhookConfig.setWebhookUrl(webhookUrl);
		webhookConfig.setWebhookSecret(webhookSecret);
		ConfigPersistenceManager.saveWebhookConfiguration(projectKey, webhookConfig);
		return Response.ok().build();
	}

	@Path("/setWebhookType")
	@POST
	public Response setWebhookType(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey,
			@QueryParam("webhookType") String webhookType,
			@QueryParam("isWebhookTypeEnabled") boolean isWebhookTypeEnabled) {
		Response isValidDataResponse = RestParameterChecker.checkIfDataIsValid(request, projectKey);
		if (isValidDataResponse.getStatus() != Status.OK.getStatusCode()) {
			return isValidDataResponse;
		}
		WebhookConfiguration webhookConfig = ConfigPersistenceManager.getWebhookConfiguration(projectKey);
		webhookConfig.setWebhookType(webhookType, isWebhookTypeEnabled);
		ConfigPersistenceManager.saveWebhookConfiguration(projectKey, webhookConfig);
		return Response.ok().build();
	}

	@Path("/sendTestPost")
	@POST
	public Response sendTestPost(@Context HttpServletRequest request, @QueryParam("projectKey") String projectKey) {
		WebhookConnector connector = new WebhookConnector(projectKey);
		if (connector.sendTestPost()) {
			return Response.ok().build();
		}
		return Response.status(Status.BAD_REQUEST).entity(ImmutableMap.of("error", "Test webhook post failed."))
				.build();
	}
}