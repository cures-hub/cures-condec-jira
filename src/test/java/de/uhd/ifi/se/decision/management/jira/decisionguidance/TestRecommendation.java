package de.uhd.ifi.se.decision.management.jira.decisionguidance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.projectsource.ProjectSource;
import de.uhd.ifi.se.decision.management.jira.decisionguidance.rdfsource.RDFSource;
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
		assertEquals(0, recommendation.getLinkedArguments().size());
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
		assertEquals(1, recommendation.getLinkedArguments().size());

		recommendation.setArguments(arguments);
		assertEquals(1, recommendation.getLinkedArguments().size());

		Recommendation emptyArguments = new Recommendation();
		emptyArguments.addArgument(argument);
		assertEquals(1, emptyArguments.getLinkedArguments().size());
	}

	@Test
	public void testHashCode() {
		Recommendation recommendation = new Recommendation(knowledgeSource, "TEST", "TESTURL");
		assertEquals(Objects.hash("TEST", "TEST"), recommendation.hashCode());
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void testEquals() {
		KnowledgeSource sourceA = new ProjectSource("TEST", "Source", true);
		Recommendation recommendationA = new Recommendation(sourceA, "Recommendation", "TESTURL");

		Recommendation recommendationB = new Recommendation(sourceA, "Recommendation", "TESTURL");
		assertTrue(recommendationA.equals(recommendationB));

		KnowledgeSource sourceB = new ProjectSource("TEST", "SourceB", true);
		recommendationB = new Recommendation(sourceB, "Recommendation", "TESTURL");
		assertFalse(recommendationA.equals(recommendationB));

		recommendationB = null;
		assertFalse(recommendationA.equals(recommendationB));

		recommendationB = new Recommendation(sourceA, "Recommendation with other summary", "TESTURL");
		assertFalse(recommendationA.equals(recommendationB));

		assertFalse(recommendationA.equals(new RDFSource()));
	}
}
