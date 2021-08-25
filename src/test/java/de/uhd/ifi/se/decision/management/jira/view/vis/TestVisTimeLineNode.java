package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.quality.completeness.DefinitionOfDone;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestVisTimeLineNode extends TestSetUp {

	private KnowledgeElement element;
	private VisTimeLineNode timeNode;

	@Before
	public void setUp() {
		init();
		element = KnowledgeElements.getTestKnowledgeElement();
		timeNode = new VisTimeLineNode(element, true, true, new FilterSettings());
	}

	@Test
	public void testConstructorNull() {
		VisTimeLineNode node = new VisTimeLineNode(null, false, false, null);
		assertEquals(0, node.getId());
	}

	@Test
	public void testConstructorFilledCreationAndUpdatingDateConsidered() {
		VisTimeLineNode node = new VisTimeLineNode(element, true, true, new FilterSettings());
		assertEquals(element.getId(), node.getId());
	}

	@Test
	public void testConstructorWithGroup() {
		VisTimeLineNode node = new VisTimeLineNode(element, 123, false, true, new FilterSettings());
		assertEquals(123, node.getGroup());
	}

	@Test
	public void testGetId() {
		assertEquals(element.getId(), timeNode.getId());
	}

	@Test
	public void testGetContent() {
		assertTrue(timeNode.getContent().startsWith("<img src="));
		assertTrue(timeNode.getContent().contains(element.getSummary()));
	}

	@Test
	public void testGetStart() {
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(element.getCreationDate()), timeNode.getStart());
	}

	@Test
	public void testGetEnd() {
		assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(element.getUpdatingDate()), timeNode.getEnd());
	}

	@Test
	public void testGetClassNameWithQualityHighlightingDoDViolated() {
		assertEquals(element.getTypeAsString().toLowerCase() + "unresolved dodViolation", timeNode.getClassName());
	}

	@Test
	public void testGetClassNameWithQualityHighlightingDoDFulfilled() {
		DefinitionOfDone definitionOfDone = new DefinitionOfDone();
		definitionOfDone.setMinimumDecisionsWithinLinkDistance(0);
		FilterSettings filterSettings = new FilterSettings();
		filterSettings.setDefinitionOfDone(definitionOfDone);
		filterSettings.setLinkDistance(1);
		KnowledgeElement element = new KnowledgeElement();
		element.setProject("TEST");
		element.setDocumentationLocation(DocumentationLocation.JIRAISSUETEXT);
		timeNode = new VisTimeLineNode(element, true, true, filterSettings);
		assertEquals(element.getSummary(), timeNode.getTitle());
		assertEquals("other undefined", timeNode.getClassName());
	}

	@Test
	public void testGetClassNameWithoutQualityHighlighting() {
		FilterSettings filterSettings = new FilterSettings();
		filterSettings.highlightQualityProblems(false);
		timeNode = new VisTimeLineNode(element, true, false, filterSettings);
		assertEquals(element.getTypeAsString().toLowerCase() + " " + element.getStatusAsString(),
				timeNode.getClassName());
	}

	@Test
	public void testGetTitleWithQualityHighlightingDoDViolated() {
		assertTrue(timeNode.getTitle().contains("decision coverage"));
	}

	@Test
	public void testGetTitleWithoutQualityHighlighting() {
		FilterSettings filterSettings = new FilterSettings();
		filterSettings.highlightQualityProblems(false);
		timeNode = new VisTimeLineNode(element, true, true, filterSettings);
		assertEquals(element.getSummary(), timeNode.getTitle());
	}
}
