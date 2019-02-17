package de.uhd.ifi.se.decision.management.jira.persistence;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

import static org.junit.Assert.*;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestConfigPersistenceManager extends TestSetUpWithIssues {
	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
	}

	// IsIssueStrategy
	@Test
	public void testIsIssueStrategyInvalid() {
		assertFalse(ConfigPersistenceManager.isIssueStrategy(null));
	}

	@Test
	public void testIsIssueStrategyOk() {
		assertTrue(ConfigPersistenceManager.isIssueStrategy("TEST"));
	}

	// SetIssueStrategy
	@Test
	public void testSetIssueStrategyNullFalse() {
		ConfigPersistenceManager.setIssueStrategy(null, false);
		assertTrue(ConfigPersistenceManager.isIssueStrategy("TEST"));
	}

	@Test
	public void testSetIssueStrategyNullTrue() {
		ConfigPersistenceManager.setIssueStrategy(null, true);
		assertTrue(ConfigPersistenceManager.isIssueStrategy("TEST"));
	}

	// @issue: The settings are always true, even if they were set to false before.
	// Why?
	@Test
	public void testSetIssueStrategyValidTrue() {
		ConfigPersistenceManager.setIssueStrategy("TEST", true);
		assertTrue(ConfigPersistenceManager.isIssueStrategy("TEST"));
	}

	// IsActivated
	@Ignore
	public void testIsActivatedInvalid() {
		assertFalse(ConfigPersistenceManager.isActivated("InvalidKey"));
	}

	@Test
	public void testIsActivatedOk() {
		assertTrue(ConfigPersistenceManager.isActivated("TEST"));
	}

	// SetActivated
	@Test
	public void testSetActivatedNullFalse() {
		ConfigPersistenceManager.setActivated(null, false);
		assertTrue(ConfigPersistenceManager.isActivated("TEST"));
	}

	@Test
	public void testSetActivateNullTrue() {
		ConfigPersistenceManager.setActivated(null, true);
		assertTrue(ConfigPersistenceManager.isActivated("TEST"));
	}

	@Test
	public void testSetActivatedValid() {
		ConfigPersistenceManager.setActivated("TEST", true);
		assertTrue(ConfigPersistenceManager.isActivated("TEST"));
	}

	// IsKnowledgeExtractedFromGit
	@Test
	public void testIsKnowledgeExtractedNull() {
		assertFalse(ConfigPersistenceManager.isKnowledgeExtractedFromGit(null));
	}

	@Test
	public void testIsKnowledgeExtractedFilled() {
		assertTrue(ConfigPersistenceManager.isKnowledgeExtractedFromGit("TEST"));
	}

	// SetKnowledgeExtractedFromGit
	@Test
	public void testSetKnowledgeExtractedNullFalse() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit(null, false);
		assertFalse(ConfigPersistenceManager.isKnowledgeExtractedFromGit(null));
	}

	@Test
	@Ignore
	// TODO Mock ApplicationLinkService
	public void testSetKnowledgeExtractedNullTrue() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit(null, true);
		assertFalse(ConfigPersistenceManager.isKnowledgeExtractedFromGit(null));
	}

	@Test
	public void testSetKnowledgeExtractedInvalidFalse() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("NotTEST", false);
		assertTrue(ConfigPersistenceManager.isKnowledgeExtractedFromGit("NotTEST"));
	}

	@Test
	@Ignore
	// TODO Mock ApplicationLinkService
	public void testSetKnowledgeExtractedInvalidTrue() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("NotTEST", true);
		assertTrue(ConfigPersistenceManager.isKnowledgeExtractedFromGit("NotTEST"));
	}

	@Test
	public void testSetKnowledgeExtractedFilledFalse() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", false);
	}

	@Test
	@Ignore
	// TODO Mock ApplicationLinkService
	public void testSetKnowledgeExtractedFilledTrue() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", true);
		assertTrue(ConfigPersistenceManager.isKnowledgeExtractedFromGit("TEST"));
	}

	// IsKnowledgeExtractedFromIssues
	@Test
	public void testIsKnowledgeExtractedIssuesKeyNull() {
		assertFalse(ConfigPersistenceManager.isKnowledgeExtractedFromIssues(null));
	}

	@Test
	public void testIsKnowledgeExtractedIssuesKeyFilled() {
		assertTrue(ConfigPersistenceManager.isKnowledgeExtractedFromIssues("TEST"));
	}

	// isKnowledgeTypeEnabled
	@Test
	public void testIsKnowledgeTypeEnabledKeyNullTypeFilled() {
		assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString()));
	}

	@Test
	public void testIsKnowledgeTypeEnabledKeyFilledTypeFilled() {
		assertTrue(ConfigPersistenceManager.isKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString()));
	}

	// setKnowledgeExtractedFromIssues
	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyNullIssueTrue() {
		ConfigPersistenceManager.setKnowledgeExtractedFromIssues(null, true);
		assertFalse(ConfigPersistenceManager.isKnowledgeExtractedFromIssues(null));
	}

	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyNullIssueFalse() {
		ConfigPersistenceManager.setKnowledgeExtractedFromIssues(null, false);
		assertFalse(ConfigPersistenceManager.isKnowledgeExtractedFromIssues(null));
	}

	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyFilledIssueTrue() {
		ConfigPersistenceManager.setKnowledgeExtractedFromIssues("TEST", true);
		assertTrue(ConfigPersistenceManager.isKnowledgeExtractedFromIssues("TEST"));
	}

	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyFilledIssueFalse() {
		ConfigPersistenceManager.setKnowledgeExtractedFromIssues("TEST", false);
	}

	// setKnowledgeTypeEnabled
	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeNullEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, null, false);
		assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null,""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeNullEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, null, true);
        assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null,""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeFilledEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString(), false);
        assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null,""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeFilledEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString(), true);
        assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled(null,""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeNullEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", null, false);
        assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled("TEST",""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeNullEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", null, true);
        assertFalse(ConfigPersistenceManager.isKnowledgeTypeEnabled("TEST",""));
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeFilledEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString(), false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeFilledEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString(), true);
		assertTrue(ConfigPersistenceManager.isKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString()));
	}

	@Test
	public void testSetIconParsingTrue() {
		ConfigPersistenceManager.setIconParsing("TEST", true);
		assertTrue(ConfigPersistenceManager.isIconParsing("TEST"));
	}

	@Test
	public void testSetIconParsingFalse() {
		ConfigPersistenceManager.setIconParsing("TEST", false);
	}

	@Test
	public void testIsIconParsingTrue() {
		assertNotNull(ConfigPersistenceManager.isIconParsing("TEST"));
	}

	@Test
	public void testSetAccessToken() {
		ConfigPersistenceManager.setAccessToken("new Token");
		assertNotNull(ConfigPersistenceManager.getAccessToken());
	}

	@Test
	public void testGetAccessToken() {
		assertNotNull(ConfigPersistenceManager.getAccessToken());
	}

	@Test
	public void testSetRequestToken() {
		ConfigPersistenceManager.setRequestToken("new Token");
		assertNotNull(ConfigPersistenceManager.getRequestToken());
	}

	@Test
	public void testGetRequestToken() {
		assertNotNull(ConfigPersistenceManager.getRequestToken());
	}

	@Test
	public void testSetOauthJiraHome() {
		ConfigPersistenceManager.setOauthJiraHome("new Token");
		assertNotNull(ConfigPersistenceManager.getOauthJiraHome());
	}

	@Test
	public void testGetOauthJiraHome() {
		assertNotNull(ConfigPersistenceManager.getOauthJiraHome());
	}

	@Test
	public void testSetPrivateKey() {
		ConfigPersistenceManager.setPrivateKey("new Token");
		assertNotNull(ConfigPersistenceManager.getPrivateKey());
	}

	@Test
	public void testGetPrivateKey() {
		assertNotNull(ConfigPersistenceManager.getPrivateKey());
	}

	@Test
	public void testSetConsumerKey() {
		ConfigPersistenceManager.setConsumerKey("new Token");
		assertNotNull(ConfigPersistenceManager.getConsumerKey());
	}

	@Test
	public void testGetConsumerKey() {
		assertNotNull(ConfigPersistenceManager.getConsumerKey());
	}

	@Test
	public void testSetSecretForOAuth() {
		ConfigPersistenceManager.setSecretForOAuth("new Token");
		assertNotNull(ConfigPersistenceManager.getSecretForOAuth());
	}

	@Test
	public void testGetSecretForOAuth() {
		assertNotNull(ConfigPersistenceManager.getSecretForOAuth());
	}

	@Test
	public void testSetWebhookUrlNullNull() {
		ConfigPersistenceManager.setWebhookUrl(null, null);
		assertEquals("", ConfigPersistenceManager.getWebhookUrl(null));
	}

	@Test
	public void testSetWebhookUrlFilledNull() {
		ConfigPersistenceManager.setWebhookUrl("TEST", null);
	}

	@Test
	public void testSetWebhookUrlNullFilled() {
		ConfigPersistenceManager.setWebhookUrl(null, "http://true");
		assertEquals("", ConfigPersistenceManager.getWebhookUrl(null));
	}

	@Test
	public void testSetWebhookUrlFilledFilled() {
		ConfigPersistenceManager.setWebhookUrl("TEST", "http://true");
		assertEquals("true", ConfigPersistenceManager.getWebhookUrl("TEST"));
	}

	@Test
	public void testGetWebhookUrlNull() {
		assertEquals("", ConfigPersistenceManager.getWebhookUrl(null));
	}

	@Test
	public void testGetWebhookUrlFilled() {
		assertEquals("true", ConfigPersistenceManager.getWebhookUrl("TEST"));
	}

	@Test
	public void testSetWebhookSecretNullNull() {
		ConfigPersistenceManager.setWebhookSecret(null, null);
		assertEquals("", ConfigPersistenceManager.getWebhookSecret(null));
	}

	@Test
	public void testSetWebhookSecretFilledNull() {
		ConfigPersistenceManager.setWebhookSecret("TEST", null);
	}

	@Test
	public void testSetWebhookSecretNullFilled() {
		ConfigPersistenceManager.setWebhookSecret(null, "http://true");
		assertEquals("", ConfigPersistenceManager.getWebhookSecret(null));
	}

	@Test
	public void testSetWebhookSecretFilledFilled() {
		ConfigPersistenceManager.setWebhookSecret("TEST", "http://true");
		assertEquals("true", ConfigPersistenceManager.getWebhookSecret("TEST"));
	}

	@Test
	public void testGetWebhookSecretNull() {
		assertEquals("", ConfigPersistenceManager.getWebhookSecret(null));
	}

	@Test
	public void testGetWebhookSecretFilled() {
		assertEquals("true", ConfigPersistenceManager.getWebhookSecret("TEST"));
	}

	@Test
	public void testSetWebhookEnabledNullFalse() {
		ConfigPersistenceManager.setWebhookEnabled(null, false);
		assertFalse(ConfigPersistenceManager.isWebhookEnabled(null));
	}

	@Test
	public void testSetWebhookEnabledNullTrue() {
		ConfigPersistenceManager.setWebhookEnabled(null, true);
		assertFalse(ConfigPersistenceManager.isWebhookEnabled(null));
	}

	@Test
	public void testSetWebhookEnabledFilledFalse() {
		ConfigPersistenceManager.setWebhookEnabled("TEST", false);
	}

	@Test
	public void testSetWebhookEnabledFiledTrue() {
		ConfigPersistenceManager.setWebhookEnabled("TEST", true);
		assertTrue(ConfigPersistenceManager.isWebhookEnabled("TEST"));
	}

	@Test
	public void testIsWebhookEnabledNull() {
		assertFalse(ConfigPersistenceManager.isWebhookEnabled(null));
	}

	@Test
	public void testIsWebhookEnabledEmpty() {
		assertFalse(ConfigPersistenceManager.isWebhookEnabled(""));
	}

	@Test
	public void testIsWebhookEnabledFilled() {
		assertTrue(ConfigPersistenceManager.isWebhookEnabled("TEST"));
	}

	@Test
	public void testSetWebhookTypeNullNullFalse() {
		ConfigPersistenceManager.setWebhookType(null, null, false);
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null,null));
	}

	@Test
	public void testSetWebhookTypeNullNullTrue() {
		ConfigPersistenceManager.setWebhookType(null, null, true);
        assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null,null));
	}

	@Test
	public void testSetWebhookTypeNullEmptyFalse() {
		ConfigPersistenceManager.setWebhookType(null, "", false);
        assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null,""));
	}

	@Test
	public void testSetWebhookTypeNullEmptyTrue() {
		ConfigPersistenceManager.setWebhookType(null, "", true);
        assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, ""));
	}

	@Test
	public void testSetWebhookTypeNullFilledFalse() {
		ConfigPersistenceManager.setWebhookType(null, "Task", false);
        assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, "Task"));
	}

	@Test
	public void testSetWebhookTypeNullFilledTrue() {
		ConfigPersistenceManager.setWebhookType(null, "Task", true);
        assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, "Task"));
	}

	@Test
	public void testSetWebhookTypeFilledNullFalse() {
		ConfigPersistenceManager.setWebhookType("TEST", null, false);
	}

	@Test
	public void testSetWebhookTypeFilledNullTrue() {
		ConfigPersistenceManager.setWebhookType("TEST", null, true);
	}

	@Test
	public void testSetWebhookTypeFilledEmptyFalse() {
		ConfigPersistenceManager.setWebhookType("TEST", "", false);
	}

	@Test
	public void testSetWebhookTypeFilledEmptyTrue() {
		ConfigPersistenceManager.setWebhookType("TEST", "", true);
	}

	@Test
	public void testSetWebhookTypeFilledFilledFalse() {
		ConfigPersistenceManager.setWebhookType("TEST", "Task", false);
	}

	@Test
	public void testSetWebhookTypeFilledFilledTrue() {
		ConfigPersistenceManager.setWebhookType("TEST", "Task", true);
		assertNotNull(ConfigPersistenceManager.getEnabledWebhookTypes("TEST"));
	}

	@Test
	public void testIsWebhookTypeEnabledNullNull() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, null));
	}

	@Test
	public void testIsWebhookTypeEnabledNullEmpty() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, ""));
	}

	@Test
	public void testIsWebhookTypeEnabledEmptyNull() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled("", null));
	}

	@Test
	public void testIsWebhookTypeEnabledNullFilled() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled(null, "Task"));
	}

	@Test
	public void testIsWebhookTypeEnabledFilledNull() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled("TEST", null));
	}

	@Test
	public void testIsWebhookTypeEnabledEmptyFilled() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled("", "Task"));
	}

	@Test
	public void testIsWebhookTypeEnabledFilledEmpty() {
		assertFalse(ConfigPersistenceManager.isWebhookTypeEnabled("TEST", ""));
	}

	@Test
	public void testIsWebhookTypeEnabledFilledFilled() {
		assertTrue(ConfigPersistenceManager.isWebhookTypeEnabled("TEST", "Task"));
	}

	@Test
	public void testGetEnabledWebhookTypesNull() {
		assertEquals(0, ConfigPersistenceManager.getEnabledWebhookTypes(null).size(), 0.0);
	}

	@Test
	public void testGetEnabledWebhookTypesEmpty() {
		assertEquals(0, ConfigPersistenceManager.getEnabledWebhookTypes("").size(), 0.0);
	}

	@Test
	public void testGetEnabledWebhookTypesFilled() {
		assertEquals(13, ConfigPersistenceManager.getEnabledWebhookTypes("TEST").size(), 0.0);
	}
}
