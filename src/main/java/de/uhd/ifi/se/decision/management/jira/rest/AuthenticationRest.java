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
	private boolean invalidCredentials;

	// Constructor without arguments needs to be here to create a rest interface.
	// Otherwise Bean Exception
	public AuthenticationRest() {
		checkInit();
	}

	private void checkInit() {

		try {
			this.propertiesClient = new PropertiesClient();
			this.jiraOAuthClient = new JiraOAuthClient(propertiesClient);
		} catch (Exception e) {
		}

		if (this.propertiesClient.getPropertiesOrDefaults().get("access_token").trim().length() <= 1) {
			this.invalidCredentials = true;
		} else {
			this.invalidCredentials = false;
		}
	}

	public String retrieveRequestToken(String consumerKey, String privateKey) {
		try {
			checkInit();
			return this.jiraOAuthClient.getAndAuthorizeTemporaryToken(consumerKey, privateKey.trim());
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | NullPointerException e) {
			LOGGER.debug("Bad request");
			return "";
		}
	}

	public String retrieveAccessToken(String tmpToken, String secret, String consumerKey, String privateKey) {
		checkInit();
		try {
			return this.jiraOAuthClient.getAccessToken(tmpToken, secret, consumerKey, privateKey);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | IOException | NullPointerException e) {
			LOGGER.debug("Bad request during get Access Token");
			return "";
		}
	}

	public String startRequest(String urlToCall) {
		try {
			checkInit();
		} catch (Exception e) {
			LOGGER.debug("Credentials invalid");
			return "";
		}
		if (invalidCredentials) {
			LOGGER.debug("Credentials invalid");
			return "";
		}
		List<String> s2 = new ArrayList<String>();
		s2.add(urlToCall);
		OAuthClient oac = null;
		try {
			oac = new OAuthClient(propertiesClient, jiraOAuthClient);
			oac.execute(Command.fromString("request"), s2);
		} catch (Exception e) {
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
			ConfigPersistence.setOauthJiraHome(baseURL);
			ConfigPersistence.setPrivateKey(privateKey);
			ConfigPersistence.setConsumerKey(consumerKey);
			String result = this.retrieveRequestToken(consumerKey, privateKey);

			ConfigPersistence.setRequestToken(result);
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

			ConfigPersistence.setOauthJiraHome(baseURL);
			ConfigPersistence.setRequestToken(requestToken);
			ConfigPersistence.setPrivateKey(privateKey);
			ConfigPersistence.setConsumerKey(consumerKey);
			ConfigPersistence.setSecretForOAuth(secret);

			String result = this.retrieveAccessToken(requestToken, secret, consumerKey, privateKey);

			ConfigPersistence.setAccessToken(result);

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
