package de.uhd.ifi.se.decision.management.jira.decisionguidance.recommender;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.rdfsource.RDFSource;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.KnowledgePersistenceManager;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraProjects;
import de.uhd.ifi.se.decision.management.jira.testdata.JiraUsers;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;
import de.uhd.ifi.se.decision.management.jira.view.decisionguidance.Recommendation;
import net.java.ao.test.jdbc.NonTransactional;

public class TestIBaseRecommender extends TestSetUp {

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
	@NonTransactional
	public void testAddToKnowledgeGraph() {
		List<KnowledgeSource> knowledgeSources = new ArrayList<>();
		knowledgeSources.add(projectSource);
		knowledgeSources.add(rdfSource);

		KnowledgeElement knowledgeElement = new KnowledgeElement();

		BaseRecommender recommender = new IssueBasedRecommender(knowledgeElement);
		recommender.addKnowledgeSource(knowledgeSources);

		recommender.recommendations.add(new Recommendation());
		recommender.recommendations.add(new Recommendation());

		KnowledgePersistenceManager manager = KnowledgePersistenceManager.getOrCreate("TEST");
		assertEquals(18, manager.getKnowledgeElements().size());

		recommender.addToKnowledgeGraph(KnowledgeElements.getTestKnowledgeElement(),
				JiraUsers.SYS_ADMIN.getApplicationUser(), "TEST");

		assertEquals(19, manager.getKnowledgeElements().size());
	}

	@Test
	public void testRecommenderDefaultType() {
		assertEquals(RecommenderType.KEYWORD, RecommenderType.getDefault());
	}

}
