package de.uhd.ifi.se.decision.management.jira.rest.oauth;

import static de.uhd.ifi.se.decision.management.jira.rest.oauth.PropertiesClient.JIRA_HOME;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth.OAuthAuthorizeTemporaryTokenUrl;
import com.google.api.client.auth.oauth.OAuthCredentialsResponse;
import com.google.api.client.auth.oauth.OAuthParameters;

public class JiraOAuthClient {

	public final String jiraBaseUrl;
	private final JiraOAuthTokenFactory oAuthGetAccessTokenFactory;
	private final String authorizationUrl;

	private static final Logger LOGGER = LoggerFactory.getLogger(JiraOAuthClient.class);

	public JiraOAuthClient(PropertiesClient propertiesClient) throws Exception {
		jiraBaseUrl = propertiesClient.getPropertiesOrDefaults().get(JIRA_HOME);
		this.oAuthGetAccessTokenFactory = new JiraOAuthTokenFactory(this.jiraBaseUrl);
		authorizationUrl = jiraBaseUrl + "/plugins/servlet/oauth/authorize";
	}

	/**
	 * Gets temporary request token and creates url to authorize it
	 *
	 * @param consumerKey
	 *            consumer key
	 * @param privateKey
	 *            private key in PKCS8 format
	 * @return request token value
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public String getAndAuthorizeTemporaryToken(String consumerKey, String privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		JiraOAuthGetTemporaryToken temporaryToken = oAuthGetAccessTokenFactory.getTemporaryToken(consumerKey,
				privateKey);
		OAuthCredentialsResponse response = temporaryToken.execute();

		LOGGER.debug("Token:\t\t\t" + response.token);
		LOGGER.debug("Token secret:\t" + response.tokenSecret);

		OAuthAuthorizeTemporaryTokenUrl authorizationURL = new OAuthAuthorizeTemporaryTokenUrl(authorizationUrl);
		authorizationURL.temporaryToken = response.token;
		LOGGER.debug("Retrieve request token. Go to " + authorizationURL.toString() + " to authorize it.");

		return response.token;
	}

	/**
	 * Gets acces token from JIRA
	 *
	 * @param tmpToken
	 *            temporary request token
	 * @param secret
	 *            secret (verification code provided by JIRA after request token
	 *            authorization)
	 * @param consumerKey
	 *            consumer ey
	 * @param privateKey
	 *            private key in PKCS8 format
	 * @return access token value
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws IOException
	 */
	public String getAccessToken(String tmpToken, String secret, String consumerKey, String privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
		JiraOAuthGetAccessToken oAuthAccessToken = oAuthGetAccessTokenFactory.getJiraOAuthGetAccessToken(tmpToken,
				secret, consumerKey, privateKey);
		OAuthCredentialsResponse response = oAuthAccessToken.execute();

		LOGGER.debug("Access token:\t\t\t" + response.token);
		return response.token;
	}

	/**
	 * Creates OAuthParameters used to make authorized request to JIRA
	 *
	 * @param tmpToken
	 * @param secret
	 * @param consumerKey
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public OAuthParameters getParameters(String tmpToken, String secret, String consumerKey, String privateKey)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		JiraOAuthGetAccessToken oAuthAccessToken = oAuthGetAccessTokenFactory.getJiraOAuthGetAccessToken(tmpToken,
				secret, consumerKey, privateKey);
		if (oAuthAccessToken != null) {
			oAuthAccessToken.verifier = secret;
			return oAuthAccessToken.createParameters();
		}
		return null;
	}
}
