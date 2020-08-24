package de.uhd.ifi.se.decision.management.jira.rationalebacklog;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.rationale.backlog.AlternativeCompletionCheck;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestAlternativeCompletionCheck {

	@Test
	public void testChildElementIssue() {
		KnowledgeElement element = new KnowledgeElement();
		element.setType(KnowledgeType.ISSUE);
		assertTrue(new AlternativeCompletionCheck().execute(element));
	}

	@Test
	public void testChildElementOther() {
		KnowledgeElement element = new KnowledgeElement();
		assertFalse(new AlternativeCompletionCheck().execute(element));
	}

}
