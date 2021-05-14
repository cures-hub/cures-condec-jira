package de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.Argument;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.recommendation.RecommendationScore;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.recommendation.decisionguidance.rdfsource.RDFSource;

public class TestElementRecommendation extends TestSetUp {

	private KnowledgeSource knowledgeSource;

	@Before
	public void setUp() {
		init();
		knowledgeSource = new ProjectSource("TEST", true);
	}

	@Test
	public void testRecommendation() {
		ElementRecommendation recommendation = new ElementRecommendation(knowledgeSource, "MySQL", "TEST URL");
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
		ElementRecommendation recommendation = new ElementRecommendation();
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

		ElementRecommendation recommendation = new ElementRecommendation();
		recommendation.addArguments(arguments);
		assertEquals(1, recommendation.getArguments().size());

		recommendation.setArguments(arguments);
		assertEquals(1, recommendation.getArguments().size());

		ElementRecommendation emptyArguments = new ElementRecommendation();
		emptyArguments.addArgument(argument);
		assertEquals(1, emptyArguments.getArguments().size());
	}

	@Test
	public void testHashCode() {
		ElementRecommendation recommendation = new ElementRecommendation(knowledgeSource, "TEST", "TESTURL");
		assertEquals(Objects.hash("TEST", "TEST"), recommendation.hashCode());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		KnowledgeSource sourceA = new ProjectSource("TEST", true);
		ElementRecommendation recommendationA = new ElementRecommendation(sourceA, "Recommendation", "TESTURL");

		ElementRecommendation recommendationB = new ElementRecommendation(sourceA, "Recommendation", "TESTURL");
		assertTrue(recommendationA.equals(recommendationB));

		KnowledgeSource sourceB = new RDFSource();
		recommendationB = new ElementRecommendation(sourceB, "Recommendation", "TESTURL");
		assertFalse(recommendationA.equals(recommendationB));

		recommendationB = null;
		assertFalse(recommendationA.equals(recommendationB));

		recommendationB = new ElementRecommendation(sourceA, "Recommendation with other summary", "TESTURL");
		assertFalse(recommendationA.equals(recommendationB));

		assertFalse(recommendationA.equals(new RDFSource()));
	}
}
