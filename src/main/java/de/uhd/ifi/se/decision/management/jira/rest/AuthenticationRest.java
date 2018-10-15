package de.uhd.ifi.se.decision.management.jira.rest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.rest.oauth.Command;
import de.uhd.ifi.se.decision.management.jira.rest.oauth.JiraOAuthClient;
import de.uhd.ifi.se.decision.management.jira.rest.oauth.OAuthClient;
import de.uhd.ifi.se.decision.management.jira.rest.oauth.PropertiesClient;

@Path("/auth")
public class AuthenticationRest {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationRest.class);
	private JiraOAuthClient jiraOAuthClient;
	private PropertiesClient propertiesClient;

	// Constructor without arguments needs to be here to create a rest interface.
	// Otherwise Bean Exception
	public AuthenticationRest() {

	}

	public AuthenticationRest(String projectKey) {
		this();
		checkInit(projectKey);
	}

	private void checkInit(String projectKey) {
		try {
			this.propertiesClient = new PropertiesClient(projectKey);
			this.jiraOAuthClient = new JiraOAuthClient(propertiesClient);
		} catch (Exception e) {
		}
	}

	public String retrieveRequestToken(String consumerKey, String privateKey, String projectKey) {
		try {
			checkInit(projectKey);
			return this.jiraOAuthClient.getAndAuthorizeTemporaryToken(consumerKey, privateKey.trim());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | NullPointerException e) {
			LOGGER.debug("Bad request");
			return "";
		}
	}

	public String retrieveAccessToken(String tmpToken, String secret, String consumerKey, String privateKey,
			String projectKey) {
		checkInit(projectKey);
		try {
			return this.jiraOAuthClient.getAccessToken(tmpToken, secret, consumerKey, privateKey);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | NullPointerException e) {
			LOGGER.debug("Bad request during get Access Token");
			return "";
		}
	}

	public String startRequest(String urlToCall, String projectKey) {
		if (projectKey == null || projectKey.equals("")) {
			LOGGER.debug("Invalid Project key to start oauth request");
			return "";
		}
		try {
			checkInit(projectKey);
		} catch (Exception e) {
			LOGGER.debug("Credentials invalid");
			return "";
		}
		List<String> s2 = new ArrayList<String>();
		s2.add(urlToCall);
		OAuthClient oac = new OAuthClient(propertiesClient, jiraOAuthClient);
		try {
			oac.execute(Command.fromString("request"), s2);
		} catch (NullPointerException e) {
			LOGGER.debug("Bad request");
			return "";
		}
		return oac.getResult().toString();
	}

	@Path("/getRequestToken")
	@GET
	public Response getRequestToken(@QueryParam("projectKey") String projectKey, @QueryParam("baseURL") String baseURL,
			@QueryParam("privateKey") String privateKey, @QueryParam("consumerKey") String consumerKey) {
		if (baseURL != null && privateKey != null && consumerKey != null) {
			privateKey = privateKey.replaceAll(" ", "+");
			ConfigPersistence.setOauthJiraHome(projectKey, baseURL);
			ConfigPersistence.setPrivateKey(projectKey, privateKey);
			ConfigPersistence.setConsumerKey(projectKey, consumerKey);
			String result = this.retrieveRequestToken(consumerKey, privateKey, projectKey);

			ConfigPersistence.setRequestToken(projectKey, result);
			// TODO: Tim: why do we have to use a map here
			return Response.status(Status.OK).entity(ImmutableMap.of("result", result)).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Request could not be send due to a bad request (element id or project key was missing)."))
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

			ConfigPersistence.setOauthJiraHome(projectKey, baseURL);
			ConfigPersistence.setRequestToken(projectKey, requestToken);
			ConfigPersistence.setPrivateKey(projectKey, privateKey);
			ConfigPersistence.setConsumerKey(projectKey, consumerKey);
			ConfigPersistence.setSecretForOAuth(projectKey, secret);

			String result = this.retrieveAccessToken(requestToken, secret, consumerKey, privateKey, projectKey);

			ConfigPersistence.setAccessToken(projectKey, result);

			// TODO: Tim: why do we have to use a map here
			return Response.status(Status.OK).entity(ImmutableMap.of("result", result)).build();
		} else {
			return Response.status(Status.BAD_REQUEST)
					.entity(ImmutableMap.of("error",
							"Request could not be send due to a bad request (element id or project key was missing)."))
					.build();
		}
	}

}
