package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;
import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestSentence.AoSentenceTestDatabaseUpdater.class)
public class TestSentence extends TestSetUpWithIssues {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeEnum() {
		Sentence sentence = new SentenceImpl();
		assertNotNull(sentence);
		sentence.setType(KnowledgeType.ALTERNATIVE);
		assertEquals(KnowledgeType.ALTERNATIVE, sentence.getType());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeDoubleAlternative() {
		Sentence sentence = new SentenceImpl();
		double[] classification = { 1.0, 0.0, 0.0, 0.0, 0.0 };
		sentence.setKnowledgeType(classification);
		assertEquals(KnowledgeType.ALTERNATIVE, sentence.getType());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeDoubleArgumentPro() {
		Sentence sentence = new SentenceImpl();
		double[] classification = { .0, 1.0, 0.0, 0.0, 0.0 };
		sentence.setKnowledgeType(classification);
		assertEquals(KnowledgeType.ARGUMENT, sentence.getType());
		assertEquals("pro", sentence.getArgument().toLowerCase());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeDoubleArgumentCon() {
		Sentence sentence = new SentenceImpl();
		double[] classification = { .0, .0, 1.0, 0.0, 0.0 };
		sentence.setKnowledgeType(classification);
		assertEquals(KnowledgeType.ARGUMENT, sentence.getType());
		assertEquals("con", sentence.getArgument().toLowerCase());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeDoubleDecision() {
		Sentence sentence = new SentenceImpl();
		double[] classification = { .0, 0.0, 0.0, 1.0, 0.0 };
		sentence.setKnowledgeType(classification);
		assertEquals(KnowledgeType.DECISION, sentence.getType());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeDoubleIssue() {
		Sentence sentence = new SentenceImpl();
		double[] classification = { .0, 0.0, 0.0, .0, 1.0 };
		sentence.setKnowledgeType(classification);
		assertEquals(KnowledgeType.ISSUE, sentence.getType());
	}

	@Test
	@Ignore
	@NonTransactional // TODO: Findout how to initialize project impl
	public void testGetKnowledgeTypeToString() {
		Sentence sentence = new SentenceImpl();
		assertEquals("", sentence.getKnowledgeTypeString());
		sentence.setType(KnowledgeType.ALTERNATIVE);
		assertEquals("Alternative", sentence.getKnowledgeTypeString());
		sentence.setType(KnowledgeType.ARGUMENT);
		assertEquals("", sentence.getKnowledgeTypeString());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeString() {
		Sentence sentence = new SentenceImpl();
		sentence.setKnowledgeTypeString(KnowledgeType.ALTERNATIVE.toString());
		assertEquals(KnowledgeType.ALTERNATIVE.toString(), sentence.getKnowledgeTypeString());
		sentence.setKnowledgeTypeString("pro");
		assertEquals("Pro", sentence.getKnowledgeTypeString());
		assertEquals("pro", sentence.getArgument().toLowerCase());
		sentence.setKnowledgeTypeString("con");
		assertEquals("Con", sentence.getKnowledgeTypeString());
		assertEquals("con", sentence.getArgument().toLowerCase());
	}

	@Test
	@NonTransactional
	public void testSetRelevantWithDouble() {
		Sentence sentence = new SentenceImpl();
		sentence.setRelevant(1.0);
		assertTrue(sentence.isRelevant());
		sentence.setRelevant(.0);
		assertFalse(sentence.isRelevant());
	}

	@Test
	@NonTransactional
	public void testToString() {
		Sentence sentence = new SentenceImpl();
		assertNotNull(sentence.toString());
	}

	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater // (2)
	{
		@SuppressWarnings("unchecked")
		@Override
		public void update(EntityManager entityManager) throws Exception {
			entityManager.migrate(DecisionKnowledgeInCommentEntity.class);
		}
	}
}
