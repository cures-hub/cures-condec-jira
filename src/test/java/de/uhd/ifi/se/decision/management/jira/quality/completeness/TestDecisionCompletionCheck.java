package de.uhd.ifi.se.decision.management.jira.quality.completeness;


import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DecisionCompletionCheck;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestDecisionCompletionCheck extends TestSetUp {
	private List<KnowledgeElement> elements;
	private KnowledgeElement decisionElement;

	@Before
	public void setUp() {
		init();
		elements = KnowledgeElements.getTestKnowledgeElements();
		decisionElement = elements.get(6);
	}

	@Test
	@NonTransactional
	public void testIsLinkedToIssue() {
		assertEquals(decisionElement.getType(), KnowledgeType.DECISION);
		assertEquals(decisionElement.getId(), 4);
		KnowledgeElement issue = elements.get(3);
		assertEquals(issue.getType(), KnowledgeType.ISSUE);
		assertEquals(issue.getId(), 2);
		assertTrue(decisionElement.getLink(issue));
		assertTrue(new DecisionCompletionCheck().execute(decisionElement));
	}

	//TODO write test to check when decision is not linked to an issue
}
