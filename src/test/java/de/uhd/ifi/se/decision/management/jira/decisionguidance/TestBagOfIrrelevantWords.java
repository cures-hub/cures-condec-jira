package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.BagOfIrrelevantWords;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestBagOfIrrelevantWords {

	private BagOfIrrelevantWords bagOfIrrelevantWords;
	private final static String WORDS = "WHICH;WHAT;COULD;SHOULD";

	@Before
	public void setUp() {
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
		List<String> tokens = Arrays.asList("Which database could we choose?".split(" "));
		assertEquals("database we choose?", bagOfIrrelevantWords.cleanSentence(tokens).trim());

	}

}
