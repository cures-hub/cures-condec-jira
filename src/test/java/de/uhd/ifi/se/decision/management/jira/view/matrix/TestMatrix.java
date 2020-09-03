package de.uhd.ifi.se.decision.management.jira.view.matrix;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;

public class TestMatrix extends TestSetUp {
	private Matrix matrix;

	@Before
	public void setUp() {
		init();
		Set<KnowledgeElement> decisions = new LinkedHashSet<>();

		KnowledgeElement element_1 = new KnowledgeElement(1, "TESTfwf", "", "Decision", "TEST", "Test-1", "i", "");
		decisions.add(element_1);

		KnowledgeElement element_2 = new KnowledgeElement(2, "TESTfwfw", "", "Decision", "TEST", "Test-1", "i", "");
		decisions.add(element_2);

		matrix = new Matrix("TEST", decisions);
	}

	@Test
	public void testGetHeaderElements() {
		assertEquals(2, this.matrix.getHeaderElements().size());
	}

	@Test
	public void testGetColoredRows() {
		assertTrue(this.matrix.getColoredRows().get(1).contains("LightGray"));
		assertTrue(this.matrix.getColoredRows().get(1).contains("White"));
	}

}