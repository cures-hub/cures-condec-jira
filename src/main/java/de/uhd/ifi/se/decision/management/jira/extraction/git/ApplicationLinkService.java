package de.uhd.ifi.se.decision.management.jira.extraction.git;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkRequest;
import com.atlassian.applinks.api.ApplicationLinkRequestFactory;
import com.atlassian.applinks.api.ApplicationLinkResponseHandler;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;

import de.uhd.ifi.se.decision.management.jira.ComponentGetter;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistenceManager;

public class ApplicationLinkService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationLinkService.class);

	public static String startRequest(String url) {
		String responseBody = "";
		String consumerKey = ConfigPersistenceManager.getConsumerKey();
		Iterable<ApplicationLink> applicationLinks = ComponentGetter.getApplicationLinkService().getApplicationLinks();
		for (ApplicationLink applicationLink : applicationLinks) {
			if (applicationLink.getName().equals(consumerKey)) {
				ApplicationLinkRequestFactory requestFactory = applicationLink.createAuthenticatedRequestFactory();
				try {
					ApplicationLinkRequest request = requestFactory.createRequest(Request.MethodType.GET, url);
					request.addHeader("Content-Type", "application/json");
					responseBody = request.executeAndReturn(new ApplicationLinkResponseHandler<String>() {
						public String credentialsRequired(final Response response) throws ResponseException {
							return response.getResponseBodyAsString();
						}

						public String handle(final Response response) throws ResponseException {
							return response.getResponseBodyAsString();
						}
					});
				} catch (ResponseException | CredentialsRequiredException e) {
					LOGGER.error("REST call to " + url + " failed.");
					e.printStackTrace();
				}
			}
		}
		System.out.println("Response from Git Integration plugin: " + responseBody);
		return responseBody;
	}
}
