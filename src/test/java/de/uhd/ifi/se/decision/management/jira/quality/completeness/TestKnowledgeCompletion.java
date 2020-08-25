package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.KnowledgeCompletion;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestKnowledgeCompletion extends TestSetUp {
	private KnowledgeElement issueElement;
	private KnowledgeElement decisionElement;
	private KnowledgeElement alternativeElement;
	private KnowledgeElement proElement;

	@Before
	public void setUp() {
		init();
		List<KnowledgeElement> elements = KnowledgeElements.getTestKnowledgeElements();
		issueElement = elements.get(3);
		decisionElement = elements.get(6);
		alternativeElement = elements.get(5);
		proElement = elements.get(7);
	}

	@Test
	@NonTransactional
	public void testCompleteIssue() {
		assertTrue(KnowledgeCompletion.isElementComplete(issueElement));
	}

	@Test
	@NonTransactional
	public void testCompleteDecision() {
		assertTrue(KnowledgeCompletion.isElementComplete(decisionElement));
	}

	@Test
	@NonTransactional
	public void testCompleteAlternative() {
		assertTrue(KnowledgeCompletion.isElementComplete(alternativeElement));
	}

	@Test
	@NonTransactional
	public void testArgument() {
		assertFalse(KnowledgeCompletion.isElementComplete(proElement));
	}

}
