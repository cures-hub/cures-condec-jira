package de.uhd.ifi.se.decision.management.jira.persistence;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

	// @issue: The settings are always true, even if they were set to false before. Why?
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
	}

	@Test
	public void testSetKnowledgeExtractedNullTrue() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit(null, true);
	}

	@Test
	public void testSetKnowledgeExtractedInvalidFalse() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("NotTEST", false);
		assertTrue(ConfigPersistenceManager.isKnowledgeExtractedFromGit("NotTEST"));
	}

	@Test
	public void testSetKnowledgeExtractedInvalidTrue() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("NotTEST", true);
	}

	@Test
	public void testSetKnowledgeExtractedFilledFalse() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", false);
	}

	@Test
	public void testSetKnowledgeExtractedFilledTrue() {
		ConfigPersistenceManager.setKnowledgeExtractedFromGit("TEST", true);
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
	}

	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyNullIssueFalse() {
		ConfigPersistenceManager.setKnowledgeExtractedFromIssues(null, false);
	}

	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyFilledIssueTrue() {
		ConfigPersistenceManager.setKnowledgeExtractedFromIssues("TEST", true);
	}

	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyFilledIssueFalse() {
		ConfigPersistenceManager.setKnowledgeExtractedFromIssues("TEST", false);
	}

	// setKnowledgeTypeEnabled
	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeNullEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, null, false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeNullEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, null, true);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeFilledEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString(), false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeFilledEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString(), true);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeNullEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", null, false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeNullEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", null, true);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeFilledEnabledFalse() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString(), false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeFilledEnabledTrue() {
		ConfigPersistenceManager.setKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString(), true);
	}

	@Test
	public void testSetIconParsingTrue() {
		ConfigPersistenceManager.setIconParsing("TEST", true);
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
	}

	@Test
	public void testGetAccessToken() {
		assertNotNull(ConfigPersistenceManager.getAccessToken());
	}

	@Test
	public void testSetRequestToken() {
		ConfigPersistenceManager.setRequestToken("new Token");
	}

	@Test
	public void testGetRequestToken() {
		assertNotNull(ConfigPersistenceManager.getRequestToken());
	}

	@Test
	public void testSetOauthJiraHome() {
		ConfigPersistenceManager.setOauthJiraHome("new Token");
	}

	@Test
	public void testGetOauthJiraHome() {
		assertNotNull(ConfigPersistenceManager.getOauthJiraHome());
	}

	@Test
	public void testSetPrivateKey() {
		ConfigPersistenceManager.setPrivateKey("new Token");
	}

	@Test
	public void testGetPrivateKey() {
		assertNotNull(ConfigPersistenceManager.getPrivateKey());
	}

	@Test
	public void testSetConsumerKey() {
		ConfigPersistenceManager.setConsumerKey("new Token");
	}

	@Test
	public void testGetConsumerKey() {
		assertNotNull(ConfigPersistenceManager.getConsumerKey());
	}

	@Test
	public void testSetSecretForOAuth() {
		ConfigPersistenceManager.setSecretForOAuth("new Token");
	}

	@Test
	public void testGetSecretForOAuth() {
		assertNotNull(ConfigPersistenceManager.getSecretForOAuth());
	}
}
