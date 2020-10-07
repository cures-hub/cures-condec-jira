package de.uhd.ifi.se.decision.management.jira.persistence.knowledgepersistencemanager.automaticlinkcreator;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.persistence.AutomaticLinkCreator;
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
		KnowledgeElement source = link.getSource();
		KnowledgeElement target = link.getTarget();
		assertEquals(0, source.getUpdatingDate().compareTo(target.getUpdatingDate()));
		assertTrue(source.getId() < target.getId());
		assertEquals(target, AutomaticLinkCreator.getRecentlyUpdatedElement(link.getBothElements()));
	}

	@Test
	@NonTransactional
	public void testMostRecentElementUnequalCreationDate() {
		KnowledgeElement source = link.getSource();
		KnowledgeElement target = link.getTarget();
		Date oldCreationDate = target.getCreationDate();
		target.setCreationDate(new Date());
		List<KnowledgeElement> elements = new ArrayList<>();
		elements.add(source);
		elements.add(target);
		assertEquals(target, AutomaticLinkCreator.getRecentlyUpdatedElement(elements));
		target.setCreationDate(oldCreationDate);
	}
}
