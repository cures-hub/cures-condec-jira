package de.uhd.ifi.se.decision.management.jira.extraction.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DdecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.DecisionKnowledgeInCommentEntity;
import de.uhd.ifi.se.decision.management.jira.extraction.persistence.LinkBetweenDifferentEntitiesEntity;
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
	
	
	@Test
	@NonTransactional
	public void TestSentenceSplitWithDifferentQuotes() {
		Comment comment = new Comment("{quote} this is a quote {quote} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());
		
		comment = new Comment("{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} ");
		assertEquals(3, comment.getSentences().size());

		comment = new Comment("{quote} this is a quote {quote} and this is a test Sentence. {quote} this is a second quote {quote} and a Sentence at the back");
		assertEquals(4, comment.getSentences().size());
		
		comment = new Comment("{quote} this is a quote {quote} {quote} this is a second quote right after the first one {quote} and a Sentence at the back");
		assertEquals(3, comment.getSentences().size());
		
		comment = new Comment("{quote} this is a quote {quote} {quote} this is a second quote right after the first one {quote} {quote} These are many quotes {quote}");
		assertEquals(3, comment.getSentences().size());
	}

	@Test
	@NonTransactional
	public void TestSentenceSplitWithNoformats() {
		Comment comment = new Comment("{noformat} this is a noformat {noformat} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());
		
		comment = new Comment("{noformat} this is a noformat {noformat} and this is a test Sentence. {noformat} this is a second noformat {noformat} ");
		assertEquals(3, comment.getSentences().size());

		comment = new Comment("{noformat} this is a noformat {noformat} and this is a test Sentence. {noformat} this is a second noformat {noformat} and a Sentence at the back");
		assertEquals(4, comment.getSentences().size());
		
		comment = new Comment("{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} and a Sentence at the back");
		assertEquals(3, comment.getSentences().size());
		
		comment = new Comment("{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} {noformat} These are many noformats {noformat}");
		assertEquals(3, comment.getSentences().size());
	}
	
	@Test
	@NonTransactional
	public void TestSentenceSplitWithNoformatsAndQuotes() {
		Comment comment = new Comment("{noformat} this is a noformat {noformat} {quote} and this is a test Sentence.{quote}");
		assertEquals(2, comment.getSentences().size());
		
		comment = new Comment("{noformat} this is a noformat {noformat} and this is a test Sentence. {quote} this is a also a quote {quote} ");
		assertEquals(3, comment.getSentences().size());
		
		comment = new Comment("{noformat} this is a noformat {noformat} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} ");
		assertEquals(4, comment.getSentences().size());
		
		comment = new Comment("{noformat} this is a noformat {noformat} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} {noformat} this is a noformat {noformat} and this is a test Sentence.");
		assertEquals(6, comment.getSentences().size());
		
		comment = new Comment("{noformat} this is a first noformat {noformat} and this is a second test Sentence. {quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} {noformat} this is a fifth noformat {noformat} and this is a sixth test Sentence.");
		assertEquals(6, comment.getSentences().size());
		
		comment = new Comment("{noformat} this is a noformat {noformat} and this is a test Sentence. {noformat} this is a second noformat {noformat} and a Sentence at the back");
		assertEquals(4, comment.getSentences().size());
	
		comment = new Comment("{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} and a Sentence at the back");
		assertEquals(3, comment.getSentences().size());
		
		comment = new Comment("{noformat} this is a noformat {noformat} {noformat} this is a second noformat right after the first one {noformat} {noformat} These are many noformats {noformat}");
		assertEquals(3, comment.getSentences().size());
	}
	
	@Test
	@NonTransactional
	public void TestSentenceOrder() {
		Comment comment = new Comment("{noformat} this is a first noformat {noformat} and this is a second test Sentence. {quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} {noformat} this is a fifth noformat {noformat} and this is a sixth test Sentence.");
		assertEquals(6, comment.getSentences().size());
		assertTrue(comment.getSentences().get(0).getBody().contains("first"));
		assertTrue(comment.getSentences().get(1).getBody().contains("second"));
		assertTrue(comment.getSentences().get(2).getBody().contains("third"));
		assertTrue(comment.getSentences().get(3).getBody().contains("fourth"));
		assertTrue(comment.getSentences().get(4).getBody().contains("fifth"));
		assertTrue(comment.getSentences().get(5).getBody().contains("sixth"));
	}
	
	
	@Test
	@NonTransactional
	public void TestSentenceSplitWithUnknownTag() {
		Comment comment = new Comment("{noformat} this is a noformat {noformat} {wuzl} and this is a test Sentence {wuzl}");
		assertEquals(2, comment.getSentences().size());
		assertTrue(comment.getSentences().get(0).getBody().contains("{noformat} this is a noformat {noformat}"));
	}
	
	@Test
	@NonTransactional
	public void TestSentenceSplitWithCodeTag() {
		Comment comment = new Comment("{code:Java} int i = 0 {code} and this is a test Sentence.");
		assertEquals(2, comment.getSentences().size());
		
		comment = new Comment("{code:java} this is a code {code} and this is a test Sentence. {quote} this is a also a quote {quote} ");
		assertEquals(3, comment.getSentences().size());
		
		comment = new Comment("{code:java} this is a code {code} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} ");
		assertEquals(4, comment.getSentences().size());
		
		comment = new Comment("{code:java} this is a code {code} and this is a test Sentence. {quote} this is a also a quote {quote}{quote} this is a also a quote {quote} {code:java} this is a code {code} and this is a test Sentence.");
		assertEquals(6, comment.getSentences().size());
		
		comment = new Comment("{code:java} this is a first code {code} and this is a second test Sentence. {quote} this is a also a third quote {quote}{quote} this is a also a fourth quote {quote} {code:java} this is a fifth code {code} and this is a sixth test Sentence.");
		assertEquals(6, comment.getSentences().size());
		
		comment = new Comment("{code:java} this is a code {code} and this is a test Sentence. {code:java} this is a second code {code} and a Sentence at the back");
		assertEquals(4, comment.getSentences().size());
	
		comment = new Comment("{code:java} this is a code {code} {code:java} this is a second code right after the first one {code} and a Sentence at the back");
		assertEquals(3, comment.getSentences().size());
		
		comment = new Comment("{code:java} this is a code {code} {code:java} this is a second code right after the first one {code} {code:java} These are many codes {code}");
		assertEquals(3, comment.getSentences().size());
	}
	
	
	@Test
	@NonTransactional
	public void TestTagReplacementToHTMLCode() {
		Comment comment = new Comment("{quote} a quote {quote}");
		assertTrue(comment.getTaggedBody(0).contains("<span id=\"comment0\">{quote} a quote {quote}</span>"));
	}
	
	
	
	
	public static final class AoSentenceTestDatabaseUpdater implements DatabaseUpdater {
        @SuppressWarnings("unchecked")
		@Override
        public void update(EntityManager entityManager) throws Exception
        {
            entityManager.migrate(DecisionKnowledgeInCommentEntity.class);
            entityManager.migrate(DdecisionKnowledgeInCommentEntity.class);
            entityManager.migrate(LinkBetweenDifferentEntitiesEntity.class);
        }
    }
}
