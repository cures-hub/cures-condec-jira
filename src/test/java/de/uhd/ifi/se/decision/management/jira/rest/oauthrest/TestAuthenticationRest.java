package de.uhd.ifi.se.decision.management.jira.rest.oauthrest;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.Issue;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.persistence.ConfigPersistence;
import de.uhd.ifi.se.decision.management.jira.rest.AuthenticationRest;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestAuthenticationRest extends TestSetUpWithIssues {

	private AuthenticationRest authenticationRest;
	private EntityManager entityManager;
	private DecisionKnowledgeElementImpl decisionKnowledgeElement;

	private String privateKey = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMhmjkK9MsLveS9o0wzT/rLLefFuGz1pvmFikI9cBWRx8dawXSSnIItEtPO6yJjqK+ZiLKrd3WvMwSd45yggjeiNKe2jRhGia/QgJePDC/+09Z9iWOhwPA/Eci+E4cwD/JGtjS0Gg6U8qCQc3wlZX6/z5g/+3paEgHV+FOelQSztAgMBAAECgYEAsOkQR0x8xmffpIG2ZsmzPCWytfaMp491GMWJdnU28XBBnVQ+NcAwU6HI2K0Yrx1yucQLSJ/p+2NbVLw/3EW433NTgQPTxM/xrjIlvtZQDdgttEEmczfsVzD3tgvj9TvqDKngKQH0o9rUjDC4rI4f766gL7142Qb5elqMkJZrg0UCQQD9336IqPXV0WHxMR2el89MmclMtrek15LraDCnxbpb3rxajB4pGr5h9q8eOAU4ANPnIXCG6vYyU8x/l7lnydG3AkEAyhRfwK8UW58tOyoiJe2FuOXEFJYPeUPGM6JLalwfZFuDYpW3TQhg+mrpVPNyCAgaL4V97NzSoMH1LTzp1hnmewJBAOZBPlJUbCNxtJM9KNAegDXJhXm+fvFTVD2OUhLYkx2f9tVpIDHHv8S6KDoQNSuGFKsc+SJlGMasml1fDxnDQiECQQCdy5EFnfEwpjgklf76TOH5gnk9dfv5PiH72cQ39l2Q+SC8D5qFrYBEqs0ux7aIbQM9jmjJV5mlbC8uNv2FcM4XAkA1jdGDbtkKY0NKvjU3G0VhG1PTrCDSu7FRHb4/kMjfLPVlTaioKOp654ZiWPimVHnlTy0kqZ+ratK8qtuiucNs";

	private String consumerKey = "OauthKey";

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
		
		authenticationRest = new AuthenticationRest("TEST");

		Issue issue = ComponentAccessor.getIssueManager().getIssueByCurrentKey("3");
		decisionKnowledgeElement = new DecisionKnowledgeElementImpl(issue);
		decisionKnowledgeElement.setId(3);
		decisionKnowledgeElement.setProject("TEST");
		decisionKnowledgeElement.setType(KnowledgeType.ISSUE);
		
		
		
	}

	@Test
	public void testConfigPersistanceProperties() {
		ConfigPersistence.setAccessToken("TEST", "token");
		assertNotNull(ConfigPersistence.getAccessToken("TEST"));
		
		ConfigPersistence.setOauthJiraHome("TEST", "token");
		assertNotNull(ConfigPersistence.getOauthJiraHome("TEST"));
		
		ConfigPersistence.setConsumerKey("TEST", "token");
		assertNotNull(ConfigPersistence.getConsumerKey("TEST"));
		
		ConfigPersistence.setRequestToken("TEST", "token");
		assertNotNull(ConfigPersistence.getRequestToken("TEST"));
		
		ConfigPersistence.setSecretForOAuth("TEST", "token");
		assertNotNull(ConfigPersistence.getSecretForOAuth("TEST"));
	}
	
	@Test
	public void testAuthenticationInValidConsumerKey() {
		String response = this.authenticationRest.retrieveRequestToken("aasf", privateKey, "TEST");
		assertNotNull(response);
		assertTrue(response.length() == 0);
	}

	@Test
	@Ignore
	public void testAuthenticationValid() {
		String response = this.authenticationRest.retrieveRequestToken(consumerKey, privateKey, "TEST");
		assertNotNull(response);
		assertTrue(response.length() > 0);
	}

	@Test
	public void testGetAccessTokenInvalid() {
		String response = "1337";
		String result = this.authenticationRest.retrieveAccessToken(response, "T3f777", consumerKey, privateKey,
				"TEST");
		assertTrue(result.length() == 0);
	}

	@Test
	@Ignore // Needs user interaction in browser. May mock this.
	public void testGetAccessToken() {
		String response = "GGDQuyv3sFfFRr7FjJm1NYRKgodBIy0k";
		String result = this.authenticationRest.retrieveAccessToken(response, "T3f777", consumerKey, privateKey,
				"TEST");
		System.out.println(result); // s4vAMMhhV4FSagXm8cMjIWCiNWhOJhhW
		assertNotNull(result);
		assertTrue(result.length() > 0);
	}

	@Test
	@Ignore
	public void testStartRequestWithInvalidCall() {
		String result = this.authenticationRest.startRequest(
				"guugle.com", "TEST");
		assertNotNull(result);
	}
	

}
