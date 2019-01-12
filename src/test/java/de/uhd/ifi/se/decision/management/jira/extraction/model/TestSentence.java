package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.classification.DecisionKnowledgeClassifierImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Sentence;
import de.uhd.ifi.se.decision.management.jira.model.impl.SentenceImpl;
import de.uhd.ifi.se.decision.management.jira.persistence.tables.DecisionKnowledgeInCommentEntity;
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
				new MockUserManager());
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
		sentence.setType(DecisionKnowledgeClassifierImpl.getType(classification));
		assertEquals(KnowledgeType.ALTERNATIVE, sentence.getType());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeDoubleArgumentPro() {
		Sentence sentence = new SentenceImpl();
		double[] classification = { .0, 1.0, 0.0, 0.0, 0.0 };
		sentence.setType(DecisionKnowledgeClassifierImpl.getType(classification));
		assertEquals(KnowledgeType.PRO, sentence.getType());
		assertEquals("pro", sentence.getTypeAsString().toLowerCase());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeDoubleArgumentCon() {
		Sentence sentence = new SentenceImpl();
		double[] classification = { .0, .0, 1.0, 0.0, 0.0 };
		sentence.setType(DecisionKnowledgeClassifierImpl.getType(classification));
		assertEquals(KnowledgeType.CON, sentence.getType());
		assertEquals("con", sentence.getTypeAsString().toLowerCase());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeDoubleDecision() {
		Sentence sentence = new SentenceImpl();
		double[] classification = { .0, 0.0, 0.0, 1.0, 0.0 };
		sentence.setType(DecisionKnowledgeClassifierImpl.getType(classification));
		assertEquals(KnowledgeType.DECISION, sentence.getType());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeDoubleIssue() {
		Sentence sentence = new SentenceImpl();
		double[] classification = { .0, 0.0, 0.0, .0, 1.0 };
		sentence.setType(DecisionKnowledgeClassifierImpl.getType(classification));
		assertEquals(KnowledgeType.ISSUE, sentence.getType());
	}

	@Test
	@NonTransactional
	public void testSetKnowledgeTypeString() {
		Sentence sentence = new SentenceImpl();
		sentence.setType(KnowledgeType.ALTERNATIVE.toString());
		assertEquals(KnowledgeType.ALTERNATIVE.toString(), sentence.getTypeAsString());
		sentence.setType("pro");
		assertEquals("Pro", sentence.getTypeAsString());
		assertEquals("pro", sentence.getTypeAsString().toLowerCase());
		sentence.setType("con");
		assertEquals("Con", sentence.getTypeAsString());
		assertEquals("con", sentence.getTypeAsString().toLowerCase());
	}

	@Test
	@NonTransactional
	public void testSetRelevantWithDouble() {
		Sentence sentence = new SentenceImpl();
		sentence.setRelevant(DecisionKnowledgeClassifierImpl.isRelevant(1.0));
		assertTrue(sentence.isRelevant());
		sentence.setRelevant(DecisionKnowledgeClassifierImpl.isRelevant(0.));
		assertFalse(sentence.isRelevant());
		sentence.setRelevant(DecisionKnowledgeClassifierImpl.isRelevant(0.4));
		assertFalse(sentence.isRelevant());
	}

	@Test
	@NonTransactional
	public void testToString() {
		Sentence sentence = new SentenceImpl();
		assertNotNull(sentence.toString());
	}

	@Test
	@NonTransactional
	public void testGetKnowledgeTypeString() {
		Sentence sentence = new SentenceImpl();
		sentence.setType("");
		assertEquals("Other", sentence.getTypeAsString());
	}

	@Test
	@NonTransactional
	public void testGetCreated() {
		Sentence sentence = new SentenceImpl();
		sentence.setCreated(new Date());
		assertNotNull(sentence.getCreated());
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
