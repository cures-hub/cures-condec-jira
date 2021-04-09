package de.uhd.ifi.se.decision.management.jira.view.decisiontable;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DocumentationLocation;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;

public class TestCriterion extends TestSetUp {

	private Criterion criterion1;
	private Criterion criterion2;

	final private String summary1 = "NFR: Usability";
	final private String criterionUrl = "null/browse/null";
	final private DocumentationLocation documentationLocation = DocumentationLocation.JIRAISSUE;

	@Before
	public void setUp() {
		init();
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setSummary(summary1);
		knowledgeElement.setType(KnowledgeType.OTHER);
		knowledgeElement.setDocumentationLocation(documentationLocation);
		this.criterion1 = new Criterion(knowledgeElement);

		knowledgeElement = new KnowledgeElement();
		knowledgeElement.setId(1);
		this.criterion2 = new Criterion(knowledgeElement);
	}

	@Test
	public void getCriterionProperties() {
		assertEquals(this.criterion1.getId(), 0);
		assertEquals(this.criterion1.getSummary(), summary1);
		assertEquals(this.criterion1.getDocumentationLocation(), documentationLocation);
		assertEquals(this.criterion1.getUrl(), criterionUrl);
	}

	@Test
	public void compareCriteria() {
		Criterion tmpCriterion = null;
		assertEquals(this.criterion1.equals(criterion1), true);
		assertEquals(this.criterion1.equals(this.criterion2), false);
		assertEquals(this.criterion1.equals(tmpCriterion), false);
		assertEquals(this.criterion2.getUrl(), criterionUrl);
	}

}
