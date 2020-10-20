package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource.ProjectCalculationMethodSubstring;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.knowledgeelement.TestKnowledgeElementJiraIssue;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestProjectSource extends TestSetUp {


	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testSource() {
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		projectSource.setName("TEST");

		List<Recommendation> recommendations = projectSource.getResults("How can we implement the feature");

		assertEquals(2, recommendations.size());
		assertEquals("TEST", recommendations.get(0).getKnowledgeSourceName());

		String nullString = null;

		recommendations = projectSource.getResults(nullString);
		assertEquals(0, recommendations.size());

		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setId(123);
		knowledgeElement.setSummary("How can we implement the feature");
		recommendations = projectSource.getResults(knowledgeElement);
		assertEquals(2, recommendations.size());

	}

	@Test
	public void testActivation() {
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST Source", false);
		List<Recommendation> recommendations = projectSource.getResults("How can we implement the feature");
		assertEquals(0, recommendations.size());
	}

	@Test
	public void testDefaultAlgorithm() {
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST Source", true);
		projectSource.setName("ProjectSource");
		projectSource.getCalculationMethod();
		assertEquals(ProjectCalculationMethodSubstring.class, projectSource.calculationMethod.getClass());
	}

	@Test
	public void testProjectSourceSubstringAlgorithm() {
		ProjectCalculationMethodSubstring method = new ProjectCalculationMethodSubstring("TEST", "TEST");
		List<Recommendation> recommendations = method.getResults("How can we implement the feature");
		assertEquals(2, recommendations.size());
		assertEquals("TEST", recommendations.get(0).getKnowledgeSourceName());


		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElement();

		recommendations = method.getResults(knowledgeElement);
		assertEquals(2, recommendations.size());




	}

	@Test
	public void testProjectSourceSubstringAlgorithmInvalidProject() {
		ProjectCalculationMethodSubstring algorithm = new ProjectCalculationMethodSubstring("INVALID PROEJCT", "INVALID_PROJECT");
		List<Recommendation> recommendations = algorithm.getResults("How can we implement the feature");
		assertEquals(0, recommendations.size());
	}


	@Test
	public void testScore() {
		ProjectCalculationMethodSubstring algorithm = new ProjectCalculationMethodSubstring("TEST", "TEST");
		assertEquals(2, algorithm.getResults("How can we implement the feature").size());
		assertEquals(94, algorithm.getResults("How can we implement the feature").get(0).getScore());
	}


}
