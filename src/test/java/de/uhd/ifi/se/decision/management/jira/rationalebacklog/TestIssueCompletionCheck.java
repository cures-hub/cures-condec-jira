package de.uhd.ifi.se.decision.management.jira.rationalebacklog;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rationale.backlog.IssueCompletionCheck;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestIssueCompletionCheck {

	@Test
	public void testChildElementDecision() {
		KnowledgeElement element = new KnowledgeElement();
		element.setType(KnowledgeType.DECISION);
		assertTrue(new IssueCompletionCheck().execute(element));
	}

	@Test
	public void testChildElementAlternative() {
		KnowledgeElement element = new KnowledgeElement();
		element.setType(KnowledgeType.ALTERNATIVE);
		assertTrue(new IssueCompletionCheck().execute(element));
	}

	@Test
	public void testChildElementOther() {
		KnowledgeElement element = new KnowledgeElement();
		assertFalse(new IssueCompletionCheck().execute(element));
	}

}
