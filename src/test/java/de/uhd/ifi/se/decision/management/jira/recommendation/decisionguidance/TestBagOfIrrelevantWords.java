package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.preprocessing.Preprocessor;

public class TestBagOfIrrelevantWords extends TestSetUp {

	private BagOfIrrelevantWords bagOfIrrelevantWords;
	private final static String WORDS = "WHICH;WHAT;COULD;SHOULD";

	@Before
	public void setUp() {
		init();
		bagOfIrrelevantWords = new BagOfIrrelevantWords(WORDS);
	}

	@Test
	public void testConstructor() {
		assertEquals(4, bagOfIrrelevantWords.getIrrelevantWords().size());
	}

	@Test
	public void testCheckWords() {
		assertEquals(false, bagOfIrrelevantWords.checkIfWordIsRelevant("WHICH"));
		assertEquals(false, bagOfIrrelevantWords.checkIfWordIsRelevant("which"));
		assertEquals(false, bagOfIrrelevantWords.checkIfWordIsRelevant("WhICh"));
		assertEquals(false, bagOfIrrelevantWords.checkIfWordIsRelevant("    WhICh "));
		assertEquals(true, bagOfIrrelevantWords.checkIfWordIsRelevant("database"));
	}

	@Test
	public void testCleanSentence() {
		String[] tokens = Preprocessor.getInstance().tokenize("Which database could we choose?");
		assertEquals("database we choose ?", bagOfIrrelevantWords.cleanSentence(tokens).trim());
	}
}