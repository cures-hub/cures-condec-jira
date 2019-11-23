package de.uhd.ifi.se.decision.management.jira.model.link;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeProject;
import de.uhd.ifi.se.decision.management.jira.model.Link;
import de.uhd.ifi.se.decision.management.jira.testdata.Links;

public class TestIsInterProjectLink extends TestSetUp {

	public Link link;

	@Before
	public void setUp() {
		init();
		link = Links.getTestLinks().get(0);
	}

	@Test
	public void testFalseValidLink() {
		assertFalse(link.isInterProjectLink());
	}

	@Test
	public void testFalseInvalidLink() {
		link.getTarget().setProject((DecisionKnowledgeProject) null);
		assertFalse(link.isInterProjectLink());
		link.getTarget().setProject("TEST");
	}

	@Test
	public void testTrue() {
		link.getTarget().setProject("CONDEC");
		assertTrue(link.isInterProjectLink());
		link.getTarget().setProject("TEST");
	}
}