package de.uhd.ifi.se.decision.documentation.jira.mocks;

import java.net.URI;
import java.net.URISyntaxException;

import com.atlassian.sal.api.auth.LoginUriProvider;

public class MockLoginUriProvider implements LoginUriProvider{

	@Override
	public URI getLoginUri(URI arg0) {
		try {
			return new URI("TEST");
		} catch (URISyntaxException e) {
			return null;
		}
	}

}
