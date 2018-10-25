package de.uhd.ifi.se.decision.management.jira.oauth;

import static de.uhd.ifi.se.decision.management.jira.oauth.PropertiesClient.ACCESS_TOKEN;
import static de.uhd.ifi.se.decision.management.jira.oauth.PropertiesClient.CONSUMER_KEY;
import static de.uhd.ifi.se.decision.management.jira.oauth.PropertiesClient.PRIVATE_KEY;
import static de.uhd.ifi.se.decision.management.jira.oauth.PropertiesClient.REQUEST_TOKEN;
import static de.uhd.ifi.se.decision.management.jira.oauth.PropertiesClient.SECRET;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.auth.oauth.OAuthParameters;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.collect.ImmutableMap;

import de.uhd.ifi.se.decision.management.jira.extraction.view.DecisionKnowledgeReport;

public class OAuthClient {

	private final Map<Command, Function<List<String>, Optional<Exception>>> actionHandlers;

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuthClient.class);

	private final PropertiesClient propertiesClient;
	private final JiraOAuthClient jiraOAuthClient;
	private JSONObject result;

	public OAuthClient(PropertiesClient propertiesClient, JiraOAuthClient jiraOAuthClient) {
		this.propertiesClient = propertiesClient;
		this.jiraOAuthClient = jiraOAuthClient;
		this.setResult(new JSONObject());

		actionHandlers = ImmutableMap.<Command, Function<List<String>, Optional<Exception>>>builder()
				.put(Command.REQUEST_TOKEN, this::handleGetRequestTokenAction)
				.put(Command.ACCESS_TOKEN, this::handleGetAccessToken).put(Command.REQUEST, this::handleGetRequest)
				.build();
	}

	/**
	 * Executes action (if found) with given lists of arguments
	 *
	 * @param action
	 * @param arguments
	 */
	public void execute(Command action, List<String> arguments) {
		actionHandlers.getOrDefault(action, this::handleUnknownCommand).apply(arguments)
				.ifPresent(Throwable::printStackTrace);
	}

	private Optional<Exception> handleUnknownCommand(List<String> arguments) {
		LOGGER.error("Command not supported. Only " + Command.names() + " are supported.");
		return Optional.empty();
	}

	/**
	 * Gets request token and saves it to properties file
	 *
	 * @param arguments
	 *            list of arguments: no arguments are needed here
	 * @return
	 */
	private Optional<Exception> handleGetRequestTokenAction(List<String> arguments) {
		Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
		try {
			String requestToken = jiraOAuthClient.getAndAuthorizeTemporaryToken(properties.get(CONSUMER_KEY),
					properties.get(PRIVATE_KEY));
			properties.put(REQUEST_TOKEN, requestToken);
			propertiesClient.savePropertiesToFile(properties);
			return Optional.empty();
		} catch (Exception e) {
			return Optional.of(e);
		}
	}

	/**
	 * Gets access token and saves it to properties file
	 *
	 * @param arguments
	 *            list of arguments: first argument should be secert (verification
	 *            code provided by JIRA after request token authorization)
	 * @return
	 */
	private Optional<Exception> handleGetAccessToken(List<String> arguments) {
		Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
		String tmpToken = properties.get(REQUEST_TOKEN);
		String secret = arguments.get(0);

		try {
			String accessToken = jiraOAuthClient.getAccessToken(tmpToken, secret, properties.get(CONSUMER_KEY),
					properties.get(PRIVATE_KEY));
			properties.put(ACCESS_TOKEN, accessToken);
			properties.put(SECRET, secret);
			propertiesClient.savePropertiesToFile(properties);
			return Optional.empty();
		} catch (Exception e) {
			return Optional.of(e);
		}
	}

	/**
	 * Makes request to JIRA to provided url and prints response contect
	 *
	 * @param arguments
	 *            list of arguments: first argument should be request url
	 * @return
	 */
	private Optional<Exception> handleGetRequest(List<String> arguments) {
		Map<String, String> properties = propertiesClient.getPropertiesOrDefaults();
		String tmpToken = properties.get(ACCESS_TOKEN);
		String secret = properties.get(SECRET);
		String url = arguments.get(0);
		propertiesClient.savePropertiesToFile(properties);

		try {
			OAuthParameters parameters = jiraOAuthClient.getParameters(tmpToken, secret, properties.get(CONSUMER_KEY),
					properties.get(PRIVATE_KEY));
			HttpResponse response = getResponseFromUrl(parameters, new GenericUrl(url));
			parseResponse(response);
			return Optional.empty();
		} catch (Exception e) {
			return Optional.of(e);
		}
	}

	/**
	 * Prints response content if response content is valid JSON it prints it in
	 * 'pretty' format
	 *
	 * @param response
	 * @throws IOException
	 */
	private void parseResponse(HttpResponse response) throws IOException {

		try {
			Scanner scanner = new Scanner(response.getContent());
			Scanner scannerWithDelimiter = scanner.useDelimiter("\\A");
			String result = scannerWithDelimiter.hasNext() ? scannerWithDelimiter.next() : "";
			scanner.close();
			scannerWithDelimiter.close();
			response.getContent().close();
			JSONObject jsonObj = new JSONObject(result);
			DecisionKnowledgeReport.restResponse = jsonObj;
			this.result = jsonObj;
		} catch (Exception e) {
			LOGGER.debug("Error while reading response:\n" + e.getMessage());
		}
	}

	/**
	 * Authenticates to JIRA with given OAuthParameters and makes request to url
	 *
	 * @param parameters
	 * @param jiraUrl
	 * @return
	 * @throws IOException
	 */
	private static HttpResponse getResponseFromUrl(OAuthParameters parameters, GenericUrl jiraUrl) throws IOException {
		HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory(parameters);
		HttpRequest request = requestFactory.buildGetRequest(jiraUrl);

		HttpResponse r = null;
		try {
			r = request.execute();
		} catch (Exception e) {
			LOGGER.debug("Error while executing request:\n");
		}
		return r;
	}

	public JSONObject getResult() {
		return result;
	}

	public void setResult(JSONObject result) {
		this.result = result;
	}
}
