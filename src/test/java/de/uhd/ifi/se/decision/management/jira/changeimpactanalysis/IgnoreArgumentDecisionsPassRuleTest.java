package de.uhd.ifi.se.decision.management.jira.changeimpactanalysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.model.LinkType;

public class IgnoreArgumentDecisionsPassRuleTest {

	@Test
	public void passTestDescription() {
		assertEquals("IgnoreArgumentsFromAlternatives", PassRule.IGNORE_ARGUMENTS.getTranslation());
		assertEquals("IgnoreArgumentsFromAlternatives", PassRule.IGNORE_ARGUMENTS.getDescription());
	}

	@Test
	public void passTestFalse() {
		KnowledgeElement src = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		src.setType(KnowledgeType.ARGUMENT); // PRO,CON
		target.setType(KnowledgeType.DECISION);
		Link link = new Link(src, target, LinkType.RELATE);
		assertFalse(PassRule.IGNORE_ARGUMENTS.getPredicate().pass(src, 0.5, target, 0.25, link));
	}

	@Test
	public void passTestTrueBecauseImpact() {
		KnowledgeElement src = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		src.setType(KnowledgeType.ARGUMENT);
		target.setType(KnowledgeType.DECISION);
		Link link = new Link(src, target, LinkType.RELATE);
		assertTrue(PassRule.IGNORE_ARGUMENTS.getPredicate().pass(src, 1.0, target, 0.25, link));
	}

	@Test
	public void passTestTrueBecauseSrcType() {
		KnowledgeElement src = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		src.setType(KnowledgeType.DECISION);
		target.setType(KnowledgeType.ALTERNATIVE);
		Link link = new Link(src, target, LinkType.RELATE);
		assertTrue(PassRule.IGNORE_ARGUMENTS.getPredicate().pass(src, 0.75, target, 0.25, link));
	}

	@Test
	public void passProTestTrueBecauseDestType() {
		KnowledgeElement src = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		src.setType(KnowledgeType.PRO);
		target.setType(KnowledgeType.ARGUMENT);
		Link link = new Link(src, target, LinkType.RELATE);
		assertTrue(PassRule.IGNORE_ARGUMENTS.getPredicate().pass(src, 0.75, target, 0.25, link));
	}

	@Test
	public void passConTestTrueBecauseDestType() {
		KnowledgeElement src = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		src.setType(KnowledgeType.CON);
		target.setType(KnowledgeType.ARGUMENT);
		Link link = new Link(src, target, LinkType.RELATE);
		assertTrue(PassRule.IGNORE_ARGUMENTS.getPredicate().pass(src, 0.75, target, 0.25, link));
	}

	@Test
	public void passTestTrueBecauseDestType() {
		KnowledgeElement src = new KnowledgeElement();
		KnowledgeElement target = new KnowledgeElement();
		src.setType(KnowledgeType.DECISION);
		target.setType(KnowledgeType.DECISION);
		Link link = new Link(src, target, LinkType.RELATE);
		assertTrue(PassRule.IGNORE_ARGUMENTS.getPredicate().pass(src, 0.75, target, 0.25, link));
	}

}
