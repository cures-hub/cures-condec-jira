package de.uhd.ifi.se.decision.management.jira.quality.completeness;

import static org.junit.Assert.assertEquals;

import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import org.junit.Before;
import org.junit.Test;
import net.java.ao.test.jdbc.NonTransactional;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;

import java.util.ArrayList;
import java.util.List;

public class TestDefinitionOfDoneCheck  extends TestSetUp {

	private FilterSettings filterSettings;
	private KnowledgeElement knowledgeElement;

	@Before
	public void setUp() {
		init();
		filterSettings = new FilterSettings("TEST", "");
		knowledgeElement = KnowledgeElements.getTestKnowledgeElement();
	}

	@Test
	@NonTransactional
	public void testDefinitionOfDoneCheck() {
		List<String> list = new ArrayList<>();
		list.add("hasIncompleteKnowledgeLinked");
		list.add("doesNotHaveMinimumCoverage");
		assertEquals(DefinitionOfDoneCheck.execute(knowledgeElement, filterSettings), list);
	}
}
