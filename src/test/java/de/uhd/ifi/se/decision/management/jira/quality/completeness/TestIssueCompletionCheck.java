package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.IssueCompletionCheck;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestIssueCompletionCheck extends TestSetUp {

	private List<KnowledgeElement> elements;
	private KnowledgeElement issueElement;

	@Before
	public void setUp() {
		init();
		elements = KnowledgeElements.getTestKnowledgeElements();
		issueElement = elements.get(3);
	}

	@Test
	@NonTransactional
	public void testIsLinkedToDecision() {
		assertEquals(issueElement.getType(), KnowledgeType.ISSUE);
		assertEquals(issueElement.getId(), 2);
		KnowledgeElement decision = elements.get(6);
		assertEquals(decision.getType(), KnowledgeType.DECISION);
		assertEquals(decision.getId(), 4);
		assertTrue(issueElement.getLink(decision));
		assertTrue(new IssueCompletionCheck().execute(issueElement));
	}

	@Test
	@NonTransactional
	public void testIsLinkedToAlternative() {
		KnowledgeElement alternative = elements.get(5);
		assertEquals(alternative.getType(), KnowledgeType.ALTERNATIVE);
		assertEquals(alternative.getId(), 3);
		assertTrue(issueElement.getLink(alternative));
		assertTrue(new IssueCompletionCheck().execute(issueElement));
	}

	//TODO write test to check when issue is not linked to an decision or alternative
}
