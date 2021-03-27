package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;

public class TestIssueBasedRecommender extends TestSetUp {

	private ProjectSource projectSource;
	private RDFSource rdfSource;

	@Before
	public void setUp() {
		init();
		projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true); // search for solutions
		// in the same
		// project
		rdfSource = new RDFSource(JiraProjects.getTestProject().getKey());
	}

	@Test
	public void testIssueBasedRecommender() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		KnowledgeElement knowledgeElement = new KnowledgeElement();

		BaseRecommender<KnowledgeElement> recommender = new IssueBasedRecommender(knowledgeElement);
		recommender.addKnowledgeSource(knowledgeSources);
		List<Recommendation> recommendations = recommender.getRecommendation();

		assertNotEquals(null, recommendations);
	}

	@Test
	public void testIssueBasedRecommenderConstuctor() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		KnowledgeElement knowledgeElement = new KnowledgeElement();

		BaseRecommender<KnowledgeElement> recommender = new IssueBasedRecommender(knowledgeElement, knowledgeSources);
		assertEquals(2, recommender.getKnowledgeSources().size());
	}

}
