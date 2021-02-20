package de.uhd.ifi.se.decision.management.jira.model.cia;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;
import de.uhd.ifi.se.decision.management.jira.model.PassRule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IgnoreDecisionIncomingTest {

	@Test
	public void passTestDescription() {
		assertEquals("IgnoreDecisionIncoming", PassRule.IGNORE_DecisionIncoming.getTranslation());
		assertEquals("IgnoreDecisionIncoming", PassRule.IGNORE_DecisionIncoming.getDescription());
	}

	@Test
	public void passTestTrueBecauseSrcType() {
		KnowledgeElement src = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		src.setId(1);
		src.setType(KnowledgeType.DECISION);
		src.setDescription("Src Descr");
		target.setId(2);
		target.setType(KnowledgeType.DECISION);
		target.setDescription("Target Descr");
		Link link = new Link(src, target, LinkType.RELATE);
		assertTrue(PassRule.IGNORE_DecisionIncoming.getPredicate().pass(src, 0.75, target, 0.25, link));
	}

	@Test
	public void passTestFalseBecauseSrcTypeNotDecision() {
		KnowledgeElement src = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		src.setId(1);
		src.setType(KnowledgeType.PRO);
		src.setDescription("Src Descr");
		target.setId(2);
		target.setType(KnowledgeType.DECISION);
		target.setDescription("Target Descr");
		Link link = new Link(src, target, LinkType.RELATE);
		assertTrue(PassRule.IGNORE_DecisionIncoming.getPredicate().pass(src, 0.75, target, 0.25, link));
	}

	@Test
	public void notPassTestTrueBecauseRootLinkIsTarget() {
		KnowledgeElement src = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		src.setId(1);
		src.setType(KnowledgeType.DECISION);
		target.setId(2);
		target.setType(KnowledgeType.DECISION);
		Link link = new Link(target, src, LinkType.RELATE);
		assertFalse(PassRule.IGNORE_DecisionIncoming.getPredicate().pass(src, 0.75, target, 0.25, link));
	}
}
