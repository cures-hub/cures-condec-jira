package de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInput;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInputKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSourceInputString;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender.RecommenderType;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestProjectSource extends TestSetUp {


	@Before
	public void setUp() {
		init();
	}

	@Test
	public void testSource() {
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		projectSource.setName("TEST");

		projectSource.setRecommenderType(RecommenderType.KEYWORD);
		List<Recommendation> recommendations = projectSource.getResults("How can we implement the feature");

		assertEquals(2, recommendations.size());
		assertEquals("TEST", recommendations.get(0).getKnowledgeSourceName());

		String nullString = null;
	}

	@Test
	public void testInvalidProject() {
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), null, true);
		assertEquals(null, projectSource.getKnowledgePersistenceManager());
	}

	@Test
	public void testStringInput() {
		ProjectSourceInputString input = new ProjectSourceInputString();
		input.setData("TEST", "TEST", null);
		List<Recommendation> recommendations = input.getResults("How can we implement the feature");
		assertEquals(2, recommendations.size());
	}

	@Test
	public void testStringKnowledgeElement() {
		ProjectSourceInputKnowledgeElement input = new ProjectSourceInputKnowledgeElement();
		input.setData("TEST", "TEST", null);
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setId(123);
		knowledgeElement.setSummary("How can we implement the feature");
		List<Recommendation> recommendations = input.getResults(KnowledgeElements.getTestKnowledgeElement());
		assertEquals(2, recommendations.size());
	}

	@Test
	public void testSetData() {
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		projectSource.setData();
		ProjectSourceInput inputMethod = new ProjectSourceInputString();
		projectSource.setInputMethod(inputMethod);
		projectSource.setData();
		assertNotNull(inputMethod.getResults("TEST"));
	}

	@Test
	public void testGetInputMethod() {
		ProjectSource projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		projectSource.setRecommenderType(RecommenderType.KEYWORD);
		assertEquals(ProjectSourceInputString.class, projectSource.getInputMethod().getClass());
		projectSource.setRecommenderType(RecommenderType.ISSUE);
		assertEquals(ProjectSourceInputKnowledgeElement.class, projectSource.getInputMethod().getClass());
	}

	@Test
	public void testKnowledgeSource() {
		KnowledgeSource knowledgeSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true);
		assertEquals("TEST", knowledgeSource.getName());
		assertEquals("TEST", knowledgeSource.getProjectKey());
		assertEquals(true, knowledgeSource.isActivated());
		knowledgeSource.setActivated(false);
		assertEquals(false, knowledgeSource.isActivated());
	}
}
