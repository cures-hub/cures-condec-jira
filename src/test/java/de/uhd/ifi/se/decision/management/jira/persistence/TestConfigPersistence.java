package de.uhd.ifi.se.decision.management.jira.persistence;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(ActiveObjectsJUnitRunner.class)
public class TestConfigPersistence extends TestSetUp {
	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	// IsIssueStrategy
	// Because the TransactionCallbacks are hardcoded on true in the Test the Tests
	// are only fore the right values
	@Ignore
	public void testIsIssueStrategyInvalid() {
		assertFalse(ConfigPersistence.isIssueStrategy("InvalidKey"));
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

	@Ignore
	public void testIsKnowledgeExtractedInvalid() {
		assertFalse(ConfigPersistence.isKnowledgeExtractedFromGit("NotTEST"));
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
}
