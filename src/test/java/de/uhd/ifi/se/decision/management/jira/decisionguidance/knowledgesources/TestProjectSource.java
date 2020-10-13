package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource.ProjectCalculationMethodSubstring;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.calculationmethods.projectsource.ProjectCalculationMethodTokenize;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
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
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey());
		projectSource.setName("ProjectSource");

		List<Recommendation> recommendations = projectSource.getResults("feature");

		assertEquals(2, recommendations.size());
		assertEquals("ProjectSource", recommendations.get(0).getKnowledgeSourceName());
	}

	@Test
	public void testDefaultAlgorithm() { //default is SUBSTRING
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey());
		projectSource.setName("ProjectSource");
		projectSource.setCalculationMethodTypeType(null);
		List<Recommendation> recommendations = projectSource.getResults("feature");

		assertEquals(2, recommendations.size());
		assertEquals("ProjectSource", recommendations.get(0).getKnowledgeSourceName());
	}

	@Test
	public void testProjectSourceSubstringAlgorithm() {
		ProjectCalculationMethodSubstring algorithm = new ProjectCalculationMethodSubstring("TEST", "ProjectSource");
		List<Recommendation> recommendations = algorithm.getResults("feature");
		assertEquals(2, recommendations.size());
		assertEquals("ProjectSource", recommendations.get(0).getKnowledgeSourceName());
	}

	@Test
	public void testProjectSourceSubstringAlgorithmInvalidProject() {
		ProjectCalculationMethodSubstring algorithm = new ProjectCalculationMethodSubstring("INVALID PROEJCT", "ProjectSource");
		List<Recommendation> recommendations = algorithm.getResults("feature");
		assertEquals(0, recommendations.size());
	}

	@Test
	public void testTokenizedAlgorithm() {
		ProjectCalculationMethodTokenize algorithm = new ProjectCalculationMethodTokenize("TEST", "ProjectSource");
		List<Recommendation> recommendations = algorithm.getResults("How can we implement the feature?");
		assertEquals(4, recommendations.size());
		assertEquals("ProjectSource", recommendations.get(0).getKnowledgeSourceName());
	}

	@Test
	public void testTokenizedAlgorithmInvalidProject() {
		ProjectCalculationMethodTokenize algorithm = new ProjectCalculationMethodTokenize("INVALIDPROJECT", "ProjectSource");
		List<Recommendation> recommendations = algorithm.getResults("\"How can we implement the feature?\"");
		assertEquals(0, recommendations.size());
	}

	@Test
	public void testScore() {
		ProjectCalculationMethodSubstring algorithm = new ProjectCalculationMethodSubstring("TEST", "Test Source");
		assertEquals(2, algorithm.getResults("feature").size());
		assertEquals(100, algorithm.getResults("feature").get(0).getScore());
	}


}
