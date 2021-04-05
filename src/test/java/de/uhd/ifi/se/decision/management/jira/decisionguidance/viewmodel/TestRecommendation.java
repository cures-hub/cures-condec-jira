package de.uhd.ifi.se.decision.management.jira.decisionguidance.viewmodel;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.Recommendation;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.KnowledgeSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.knowledgesources.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.score.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.view.decisiontable.Argument;

public class TestRecommendation extends TestSetUp {

	private KnowledgeSource knowledgeSource;

	@Before
	public void setUp() {
		init();
		knowledgeSource = new ProjectSource("TEST", "TEST", true);
	}

	@Test
	public void testRecommendation() {
		Recommendation recommendation = new Recommendation(knowledgeSource, "MySQL", "TEST URL");
		recommendation.setUrl("TEST URL");
		recommendation.setScore(new RecommendationScore(123, ""));
		assertEquals("TEST", recommendation.getKnowledgeSource().getName());
		assertEquals("MySQL", recommendation.getSummary());
		assertEquals("TEST URL", recommendation.getUrl());
		assertEquals(123, recommendation.getScore().getValue(), 0.0);
		assertEquals(0, recommendation.getArguments().size());
	}

	@Test
	public void testSetAndGetKnowledgeSource() {
		Recommendation recommendation = new Recommendation();
		recommendation.setKnowledgeSource(knowledgeSource);
		assertEquals("TEST", recommendation.getKnowledgeSource().getName());
	}

	@Test
	public void testAddArgument() {
		KnowledgeElement knowledgeElement = new KnowledgeElement();
		knowledgeElement.setDescription("Test Argument");
		knowledgeElement.setSummary("Test Argument");
		knowledgeElement.setDocumentationLocation("i");
		knowledgeElement.setType(KnowledgeType.CON);
		List<Argument> arguments = new ArrayList<>();
		Argument argument = new Argument(knowledgeElement);
		arguments.add(argument);

		Recommendation recommendation = new Recommendation();
		recommendation.addArguments(arguments);
		assertEquals(1, recommendation.getArguments().size());

		recommendation.setArguments(arguments);
		assertEquals(1, recommendation.getArguments().size());

		Recommendation emptyArguments = new Recommendation();
		emptyArguments.addArgument(argument);
		assertEquals(1, emptyArguments.getArguments().size());
	}

	@Test
	public void testHashCode() {
		Recommendation recommendation = new Recommendation(knowledgeSource, "TEST", "TESTURL");
		assertEquals(Objects.hash("TEST", "TEST"), recommendation.hashCode());
	}

	@Test
	public void testEquals() {
		KnowledgeSource sourceA = new ProjectSource("TEST", "SourceA", true);
		Recommendation recommendationA = new Recommendation(sourceA, "Recommendation", "TESTURL");

		KnowledgeSource sourceB = new ProjectSource("TEST", "SourceA", true);
		Recommendation recommendationB = new Recommendation(sourceB, "Recommendation", "TESTURL");

		assertEquals(recommendationA, recommendationB);
	}
}
