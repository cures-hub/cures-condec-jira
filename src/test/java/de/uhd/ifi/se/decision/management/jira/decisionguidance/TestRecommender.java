package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraIssues;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import net.java.ao.test.jdbc.NonTransactional;

public class TestRecommender extends TestSetUp {

	private ProjectSource projectSource;
	private RDFSource rdfSource;

	@Before
	public void setUp() {
		init();
		projectSource = new ProjectSource(JiraProjects.getTestProject().getKey(), "TEST", true); // search for solutions
		// in the same
		// project
		rdfSource = new RDFSource();
	}

	@Test
	@NonTransactional
	public void testAddToKnowledgeGraph() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		KnowledgeElement knowledgeElement = KnowledgeElements.getTestKnowledgeElements().get(4);

		List<Recommendation> recommendations = new ArrayList<>();
		Recommender.getAllRecommendations("TEST", knowledgeSources, knowledgeElement, "");

		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getOrCreate("TEST");
		assertEquals(JiraIssues.getTestJiraIssueCount(), manager.getKnowledgeElements().size());

		Recommender.addToKnowledgeGraph(KnowledgeElements.getTestKnowledgeElement(),
				JiraUsers.SYS_ADMIN.getApplicationUser(), "TEST", recommendations);

		assertEquals(18, manager.getKnowledgeElements().size());
	}
}
