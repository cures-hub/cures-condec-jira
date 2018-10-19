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
public class TestConfigPersistence extends TestSetUpWithIssues {
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
		assertFalse(ConfigPersistence.isIssueStrategy(null));
	}

	@Test
	public void testIsIssueStrategyOk() {
		assertTrue(ConfigPersistence.isIssueStrategy("TEST"));
	}

	// SetIssueStrategy
	@Test
	public void testSetIssueStrategyNullFalse() {
		ConfigPersistence.setIssueStrategy(null, false);
	}

	@Test
	public void testSetIssueStrategyNullTrue() {
		ConfigPersistence.setIssueStrategy(null, true);
	}

	@Test
	public void testSetIssueStrategyValid() {
		ConfigPersistence.setIssueStrategy("TEST", true);
	}

	// IsActivated
	@Ignore
	public void testIsActivatedInvalid() {
		assertFalse(ConfigPersistence.isActivated("InvalidKey"));
	}

	@Test
	public void testIsActivatedOk() {
		assertTrue(ConfigPersistence.isActivated("TEST"));
	}

	// SetActivated
	@Test
	public void testSetActivatedNullFalse() {
		ConfigPersistence.setActivated(null, false);
	}

	@Test
	public void testSetActivateNullTrue() {
		ConfigPersistence.setActivated(null, true);
	}

	@Test
	public void testSetActivatedValid() {
		ConfigPersistence.setActivated("TEST", true);
	}

	// IsKnowledgeExtractedFromGit
	@Test
	public void testIsKnowledgeExtractedNull() {
		assertFalse(ConfigPersistence.isKnowledgeExtractedFromGit(null));
	}

	@Test
	public void testIsKnowledgeExtractedFilled() {
		assertTrue(ConfigPersistence.isKnowledgeExtractedFromGit("TEST"));
	}

	// SetKnowledgeExtractedFromGit
	@Test
	public void testSetKnowledgeExtractedNullFalse() {
		ConfigPersistence.setKnowledgeExtractedFromGit(null, false);
	}

	@Test
	public void testSetKnowledgeExtractedNullTrue() {
		ConfigPersistence.setKnowledgeExtractedFromGit(null, true);
	}

	@Test
	public void testSetKnowledgeExtractedInvalidFalse() {
		ConfigPersistence.setKnowledgeExtractedFromGit("NotTEST", false);
	}

	@Test
	public void testSetKnowledgeExtractedInvalidTrue() {
		ConfigPersistence.setKnowledgeExtractedFromGit("NotTEST", true);
	}

	@Test
	public void testSetKnowledgeExtractedFilledFalse() {
		ConfigPersistence.setKnowledgeExtractedFromGit("TEST", false);
	}

	@Test
	public void testSetKnowledgeExtractedFilledTrue() {
		ConfigPersistence.setKnowledgeExtractedFromGit("TEST", true);
	}

	// IsKnowledgeExtractedFromIssues
	@Test
	public void testIsKnowledgeExtractedIssuesKeyNull() {
		assertFalse(ConfigPersistence.isKnowledgeExtractedFromIssues(null));
	}

	@Test
	public void testIsKnowledgeExtractedIssuesKeyFilled() {
		assertTrue(ConfigPersistence.isKnowledgeExtractedFromIssues("TEST"));
	}

	// isKnowledgeTypeEnabled
	@Test
	public void testIsKnowledgeTypeEnabledKeyNullTypeFilled() {
		assertFalse(ConfigPersistence.isKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString()));
	}

	@Test
	public void testIsKnowledgeTypeEnabledKeyFilledTypeFilled() {
		assertTrue(ConfigPersistence.isKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString()));
	}

	// setKnowledgeExtractedFromIssues
	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyNullIssueTrue() {
		ConfigPersistence.setKnowledgeExtractedFromIssues(null, true);
	}

	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyNullIssueFalse() {
		ConfigPersistence.setKnowledgeExtractedFromIssues(null, false);
	}

	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyFilledIssueTrue() {
		ConfigPersistence.setKnowledgeExtractedFromIssues("TEST", true);
	}

	@Test
	public void testSetKnowledgeExtractedFromIssuesKeyFilledIssueFalse() {
		ConfigPersistence.setKnowledgeExtractedFromIssues("TEST", false);
	}

	// setKnowledgeTypeEnabled
	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeNullEnabledFalse() {
		ConfigPersistence.setKnowledgeTypeEnabled(null, null, false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeNullEnabledTrue() {
		ConfigPersistence.setKnowledgeTypeEnabled(null, null, true);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeFilledEnabledFalse() {
		ConfigPersistence.setKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString(), false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyNullTypeFilledEnabledTrue() {
		ConfigPersistence.setKnowledgeTypeEnabled(null, KnowledgeType.SOLUTION.toString(), true);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeNullEnabledFalse() {
		ConfigPersistence.setKnowledgeTypeEnabled("TEST", null, false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeNullEnabledTrue() {
		ConfigPersistence.setKnowledgeTypeEnabled("TEST", null, true);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeFilledEnabledFalse() {
		ConfigPersistence.setKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString(), false);
	}

	@Test
	public void testSetKnowledgeTypeEnabledKeyFilledTypeFilledEnabledTrue() {
		ConfigPersistence.setKnowledgeTypeEnabled("TEST", KnowledgeType.SOLUTION.toString(), true);
	}

	@Test
	public void testSetIconParsingTrue() {
		ConfigPersistence.setIconParsing("TEST", true);
	}

	@Test
	public void testSetIconParsingFalse() {
		ConfigPersistence.setIconParsing("TEST", false);
	}

	@Test
	public void testIsIconParsingTrue() {
		assertNotNull(ConfigPersistence.isIconParsing("TEST"));
	}

	@Test
	public void testSetAccessToken() {
		ConfigPersistence.setAccessToken("new Token");
	}

	@Test
	public void testGetAccessToken() {
		assertNotNull(ConfigPersistence.getAccessToken());
	}

	@Test
	public void testSetRequestToken() {
		ConfigPersistence.setRequestToken("new Token");
	}

	@Test
	public void testGetRequestToken() {
		assertNotNull(ConfigPersistence.getRequestToken());
	}

	@Test
	public void testSetOauthJiraHome() {
		ConfigPersistence.setOauthJiraHome("new Token");
	}

	@Test
	public void testGetOauthJiraHome() {
		assertNotNull(ConfigPersistence.getOauthJiraHome());
	}

	@Test
	public void testSetPrivateKey() {
		ConfigPersistence.setPrivateKey("new Token");
	}

	@Test
	public void testGetPrivateKey() {
		assertNotNull(ConfigPersistence.getPrivateKey());
	}

	@Test
	public void testSetConsumerKey() {
		ConfigPersistence.setConsumerKey("new Token");
	}

	@Test
	public void testGetConsumerKey() {
		assertNotNull(ConfigPersistence.getConsumerKey());
	}

	@Test
	public void testSetSecretForOAuth() {
		ConfigPersistence.setSecretForOAuth("new Token");
	}

	@Test
	public void testGetSecretForOAuth() {
		assertNotNull(ConfigPersistence.getSecretForOAuth());
	}

}
