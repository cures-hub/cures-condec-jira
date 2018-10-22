package de.uhd.ifi.se.decision.management.jira.oauth;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST resource for OAuth configuration, e.g., to access git integration for
 * Jira plugin
 */
public class OAuthManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuthManager.class);
	private JiraOAuthClient jiraOAuthClient;
	private PropertiesClient propertiesClient;
	private boolean invalidCredentials;

	// Constructor without arguments needs to be here to create a rest interface.
	// Otherwise a bean exception is thrown.
	public OAuthManager() {
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
}
