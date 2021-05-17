package de.uhd.ifi.se.decision.management.jira.recommendation.linkrecommendation.contextinformation;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.testdata.KnowledgeElements;

public class TestTracingContextInformationProvider extends TestSetUp {

	private ContextInformationProvider tracingContextInformationProvider;

	@Before
	public void setUp() {
		init();
		tracingContextInformationProvider = new TracingContextInformationProvider();
	}

	@Test
	public void testSameElement() {
		assertEquals(0.5,
				tracingContextInformationProvider
						.assessRelation(KnowledgeElements.getDecision(), KnowledgeElements.getDecision()).getValue(),
				0);
	}

	@Test
	public void testDirectlyLinked() {
		assertEquals(0.3,
				tracingContextInformationProvider
						.assessRelation(KnowledgeElements.getSolvedDecisionProblem(), KnowledgeElements.getDecision())
						.getValue(),
				0.1);
	}

	@Test
	public void testNotLinked() {
		assertEquals(1, tracingContextInformationProvider
				.assessRelation(KnowledgeElements.getOtherWorkItem(), KnowledgeElements.getProArgument()).getValue(),
				0.1);
	}
}
