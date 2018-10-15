package de.uhd.ifi.se.decision.management.jira.rest.oauth;

import com.google.api.client.auth.oauth.OAuthGetTemporaryToken;

public class JiraOAuthGetTemporaryToken extends OAuthGetTemporaryToken {

    /**
     * @param authorizationServerUrl encoded authorization server URL
     */
    public JiraOAuthGetTemporaryToken(String authorizationServerUrl) {
        super(authorizationServerUrl);
        this.usePost = true;
    }

}
