package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.mocks.MockDefaultUserManager;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.DatabaseUpdater;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestComment.AoSentenceTestDatabaseUpdater.class) 
public class TestComment extends TestSetUp{


	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockDefaultUserManager());
	}

	@Test
	public void testConstructor() {
		assertNotNull(new Comment());
	}

	@Test
	public void testSentencesAreNotNull() {
		assertNotNull(new Comment().getSentences());
	}

	@Test
	@NonTransactional
	public void testCommentIsCreated() {
		assertNotNull(new Comment("this is a test Sentence. With two sentences"));
	}
	
	
	@Test
	@NonTransactional
	public void testCommentWithOneQuote() {
		Comment comment = new Comment("{quote} this is a quote {quote} and this is a test Sentence.");
		assertNotNull(comment);
		assertEquals(2, comment.getSentences().size());
	}
	
	@Test
	@NonTransactional
	public void testCommentWithOneQuoteAtTheBack() {
		Comment comment = new Comment("and this is a test Sentence. {quote} this is a quote {quote} ");
		assertNotNull(comment);
		assertEquals(2, comment.getSentences().size());
	}
	
	@Test
	@NonTransactional
	public void testCommentWithTwoQuotes() {
		Comment comment = new Comment("{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertNotNull(comment);
		assertEquals(3,comment.getSentences().size());
	}
	
	
	@Test
	@NonTransactional
	public void testGetTaggedBody() {
		Comment comment = new Comment("{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertNotNull(comment.getTaggedBody(0));
		assertTrue(comment.getTaggedBody(0).contains("span"));
	}
	

	
	
	
	
	
	
	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater // (2)
    {
        @SuppressWarnings("unchecked")
		@Override
        public void update(EntityManager entityManager) throws Exception
        {
            entityManager.migrate(DecisionKnowledgeInCommentEntity.class);
        }
    }
}
