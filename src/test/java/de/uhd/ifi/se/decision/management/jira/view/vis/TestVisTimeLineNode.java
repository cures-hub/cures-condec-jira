package de.uhd.ifi.se.decision.management.jira.view.vis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.filtering.FilterSettings;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
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
		VisTimeLineNode node = new VisTimeLineNode(null, false, false, new FilterSettings());
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
	public void testGetClassNameWithQualityHighlighting() {
		assertEquals(element.getTypeAsString().toLowerCase() + " dodViolation", timeNode.getClassName());
	}

	@Test
	public void testGetClassNameWithoutQualityHighlighting() {
		FilterSettings filterSettings = new FilterSettings();
		filterSettings.highlightQualityProblems(false);
		timeNode = new VisTimeLineNode(element, true, true, filterSettings);
		assertEquals(element.getTypeAsString().toLowerCase(), timeNode.getClassName());
	}

	@Test
	public void testGetTitleWithQualityHighlighting() {
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
