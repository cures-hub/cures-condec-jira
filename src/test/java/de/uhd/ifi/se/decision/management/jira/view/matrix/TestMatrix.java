package de.uhd.ifi.se.decision.management.jira.view.matrix;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestMatrix extends TestSetUp {
	private Matrix matrix;

	@Before
	public void setUp() {
		init();
		List<DecisionKnowledgeElement> decisions = new ArrayList<>();

		DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
		element.setId(1);
		element.setKey("Test-1");
		element.setType("Decision");
		element.setProject("TEST");
		element.setSummary("TESTfwf");
		element.setDocumentationLocation("i");
		decisions.add(element);

		matrix = new Matrix("Test", decisions);

	}

	@Test
	public void testGetMatrixHeaderRow() {
		assertTrue(this.matrix.getHeaders().containsKey((long) 1));
		assertTrue(this.matrix.getHeaders().containsValue("TESTfwf"));
	}

	@Test
	public void testGetMatrixData() {
		assertTrue(this.matrix.getData().containsKey((long) 1));
		assertEquals(1, this.matrix.getData().size());
	}

	@Test
	public void testGetMatrixDataEntries() {
		assertTrue(this.matrix.getData().get((long) 1).contains("LightGray"));
	}

}
