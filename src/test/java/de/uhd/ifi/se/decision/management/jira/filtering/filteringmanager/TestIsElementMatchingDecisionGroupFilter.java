package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestIsElementMatchingDecisionGroupFilter extends TestSetUp {

	private FilteringManager filteringManager;

	@Before
	public void setUp() {
		init();
		FilterSettings settings = new FilterSettings("TEST", "");
		filteringManager = new FilteringManager(settings);
	}

	@Test
	@NonTransactional
	public void testElementWithoutGroupAndNoGroupsInFilterSettings() {
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertTrue(filteringManager.isElementMatchingDecisionGroupFilter(element));
	}

	@Test
	@NonTransactional
	public void testElementWithoutGroupAndGroupsInFilterSettings() {
		List<String> decGroups = List.of("Low", "Medium", "High");
		filteringManager.getFilterSettings().setDecisionGroups(decGroups);
		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		assertFalse(filteringManager.isElementMatchingDecisionGroupFilter(element));
	}

	@Test
	public void testElementWithGroupsAndGroupsInFilterSettings() {
		List<String> decGroups = List.of("Low", "Medium", "High");
		filteringManager.getFilterSettings().setDecisionGroups(decGroups);

		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		element.addDecisionGroup("Low");
		element.addDecisionGroup("Medium");
		element.addDecisionGroup("High");
		assertTrue(filteringManager.isElementMatchingDecisionGroupFilter(element));
	}

	@Test
	public void testElementWithGroupButGroupNotInFilterSettings() {
		List<String> decGroups = List.of("Low", "Medium", "High");
		filteringManager.getFilterSettings().setDecisionGroups(decGroups);

		KnowledgeElement element = KnowledgeElements.getTestKnowledgeElement();
		element.addDecisionGroup("link recommendation");
		assertFalse(filteringManager.isElementMatchingDecisionGroupFilter(element));
	}
}