package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.BinaryClassifier;
import net.java.ao.test.jdbc.NonTransactional;

public class TestBinaryClassifier extends TestSetUp {

	@Before
	public void setUp() {
		init();
	}

	@Test
	@NonTransactional
	public void testIsRelevant() {
		assertTrue(BinaryClassifier.isRelevant(new double[] { 0.2, 0.8 }));
		assertFalse(BinaryClassifier.isRelevant(new double[] { 0.8, 0.2 }));
	}
}
