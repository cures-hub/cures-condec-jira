package de.uhd.ifi.se.decision.management.jira.filtering.filteringmanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.filtering.FilteringManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.DecisionGroupPersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestIsElementMatchingDecisionGroupFilter extends TestSetUp {

	private FilteringManager filteringManager;
	private KnowledgeElement element;

	@Before
	public void setUp() {
		init();
		FilterSettings settings = new FilterSettings("TEST", "");
		filteringManager = new FilteringManager(settings);
		element = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	@NonTransactional
	public void testElementWithoutGroupAndNoGroupsInFilterSettings() {
		assertTrue(filteringManager.isElementMatchingDecisionGroupFilter(element));
		assertTrue(filteringManager.isElementMatchingFilterSettings(element));
	}

	@Test
	@NonTransactional
	public void testElementWithoutGroupAndGroupsInFilterSettings() {
		List<String> decGroups = List.of("Medium_Level", "UI", "process");
		filteringManager.getFilterSettings().setDecisionGroups(decGroups);
		assertFalse(filteringManager.isElementMatchingDecisionGroupFilter(element));
		assertFalse(filteringManager.isElementMatchingFilterSettings(element));
	}

	@Test
	@NonTransactional
	public void testElementWithGroupsAndGroupsInFilterSettings() {
		List<String> decGroups = List.of("Medium_Level", "UI", "process");
		filteringManager.getFilterSettings().setDecisionGroups(decGroups);
		DecisionGroupPersistenceManager.insertGroups(decGroups, element);
		assertTrue(filteringManager.isElementMatchingDecisionGroupFilter(element));
	}

	@Test
	@NonTransactional
	public void testElementWithGroupButGroupNotInFilterSettings() {
		List<String> decGroups = List.of("Medium_Level", "UI", "process");
		filteringManager.getFilterSettings().setDecisionGroups(decGroups);
		DecisionGroupPersistenceManager.insertGroup("link recommendation", element);
		assertFalse(filteringManager.isElementMatchingDecisionGroupFilter(element));
	}
}