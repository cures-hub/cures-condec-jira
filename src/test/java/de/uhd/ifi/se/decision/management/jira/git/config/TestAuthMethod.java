package de.uhd.ifi.se.decision.management.jira.git.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestAuthMethod {

	@Test
	public void testDefaultMethod() {
		assertEquals(AuthMethod.NONE, AuthMethod.getAuthMethodByName(null));
		assertEquals(AuthMethod.NONE, AuthMethod.getAuthMethodByName(""));
		assertEquals(AuthMethod.NONE, AuthMethod.getAuthMethodByName("gitolite unknown git"));
	}

	@Test
	public void testValidMethods() {
		assertEquals(AuthMethod.HTTP, AuthMethod.getAuthMethodByName("http"));
		assertEquals(AuthMethod.GITHUB, AuthMethod.getAuthMethodByName("GITHUB"));
		assertEquals(AuthMethod.GITLAB, AuthMethod.getAuthMethodByName("GitLab"));
	}

}
