package de.uhd.ifi.se.decision.management.jira.extraction.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.atlassian.activeobjects.test.TestActiveObjects;

import de.uhd.ifi.se.decision.management.jira.TestComponentGetter;
import de.uhd.ifi.se.decision.management.jira.TestSetUpWithIssues;
import de.uhd.ifi.se.decision.management.jira.extraction.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.extraction.TestTextSplitter;
import de.uhd.ifi.se.decision.management.jira.extraction.impl.DecisionKnowledgeClassifierImpl;
import de.uhd.ifi.se.decision.management.jira.mocks.MockTransactionTemplate;
import de.uhd.ifi.se.decision.management.jira.mocks.MockUserManager;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import meka.classifiers.multilabel.LC;
import net.java.ao.EntityManager;
import net.java.ao.test.jdbc.Data;
import net.java.ao.test.jdbc.NonTransactional;
import net.java.ao.test.junit.ActiveObjectsJUnitRunner;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.SerializationHelper;

@RunWith(ActiveObjectsJUnitRunner.class)
@Data(TestTextSplitter.AoSentenceTestDatabaseUpdater.class)
public class TestDecisionKnowledgeClassifier extends TestSetUpWithIssues {

	private EntityManager entityManager;

	@Before
	public void setUp() {
		initialization();
		TestComponentGetter.init(new TestActiveObjects(entityManager), new MockTransactionTemplate(),
				new MockUserManager());
	}

	@Test
	@NonTransactional
	public void testGetTypeAlternative() {
		double[] classification = { 1.0, 0.0, 0.0, 0.0, 0.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.ALTERNATIVE, type);
	}

	@Test
	@NonTransactional
	public void testGetTypePro() {
		double[] classification = { .0, 1.0, 0.0, 0.0, 0.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.PRO, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeCon() {
		double[] classification = { .0, .0, 1.0, 0.0, 0.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.CON, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeDecision() {
		double[] classification = { .0, 0.0, 0.0, 1.0, 0.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.DECISION, type);
	}

	@Test
	@NonTransactional
	public void testGetTypeIssue() {
		double[] classification = { .0, 0.0, 0.0, .0, 1.0 };
		KnowledgeType type = DecisionKnowledgeClassifierImpl.getType(classification);
		assertEquals(KnowledgeType.ISSUE, type);
	}

	@Test
	@NonTransactional
	public void testIsRelevant() {
		assertTrue(DecisionKnowledgeClassifierImpl.isRelevant(1.0));
		assertFalse(DecisionKnowledgeClassifierImpl.isRelevant(0.));
		assertFalse(DecisionKnowledgeClassifierImpl.isRelevant(0.4));
	}

	@Test
	public void testDefaultClassifier() throws Exception {
		FilteredClassifier binaryClassifier = (FilteredClassifier) SerializationHelper
				.read(System.getProperty("user.home") + File.separator + "data" + File.separator + "condec-plugin"
						+ File.separator + "classifier" + File.separator + "fc.model");
		LC fineGrainedClassifier = (LC) SerializationHelper
				.read(System.getProperty("user.home") + File.separator + "data" + File.separator + "condec-plugin"
						+ File.separator + "classifier" + File.separator + "br.model");
		DecisionKnowledgeClassifier classifier = new DecisionKnowledgeClassifierImpl(binaryClassifier,
				fineGrainedClassifier);
		List<String> stringsToBeClassified = Arrays.asList("+1", "Very good.", "Party tonight");
		List<Boolean> expectedRelevance = Arrays.asList(true, true, false);
		List<Boolean> predictedRelevance = classifier.makeBinaryPredictions(stringsToBeClassified);
		// assertEquals(expectedRelevance, predictedRelevance);
		System.out.println(predictedRelevance);

		List<KnowledgeType> types = classifier.makeFineGrainedPredictions(stringsToBeClassified);
		System.out.println(types);
	}
}
