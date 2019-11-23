package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.genericlinkmanager;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.impl.AutomaticLinkCreator;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;
import net.java.ao.test.jdbc.NonTransactional;

public class TestGetMostRecentElement extends TestSetUp {

	public Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLinks().get(0);
	}

	@Test
	@NonTransactional
	public void testMostRecentElementEqualCreationDate() {
		DecisionKnowledgeElement source = link.getSource();
		DecisionKnowledgeElement target = link.getTarget();
		assertEquals(target, AutomaticLinkCreator.getMostRecentElement(source, target));
	}

	@Test
	@NonTransactional
	public void testMostRecentElementFirstNull() {
		DecisionKnowledgeElement target = link.getTarget();
		assertEquals(target, AutomaticLinkCreator.getMostRecentElement(null, target));
	}

	@Test
	@NonTransactional
	public void testMostRecentElementSecondNull() {
		DecisionKnowledgeElement source = link.getSource();
		assertEquals(source, AutomaticLinkCreator.getMostRecentElement(source, null));
	}

	@Test
	@NonTransactional
	public void testMostRecentElementUnequalCreationDate() {
		DecisionKnowledgeElement source = link.getSource();
		DecisionKnowledgeElement target = link.getTarget();
		source.setCreated(new Date());
		assertEquals(source, AutomaticLinkCreator.getMostRecentElement(source, target));
	}
}
