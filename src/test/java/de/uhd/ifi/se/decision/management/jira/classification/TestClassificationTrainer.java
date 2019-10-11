package de.uhd.ifi.se.decision.management.jira.classification;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineClassificationTrainerImpl;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Before;
import org.junit.Test;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;

/**
 * TODO: TESTS WITH useOnlyValidatedData FLAG
 */
public class TestClassificationTrainer extends TestSetUp {


    @Before
    public void setUp() {
        init();
    }

    private DecisionKnowledgeElement createElement(KnowledgeType type, String summary) {
        DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
        element.setType(type);
        element.setSummary(summary);
        return element;
    }

    private List<DecisionKnowledgeElement> getTrainingData() {
        List<DecisionKnowledgeElement> trainingElements = new ArrayList<DecisionKnowledgeElement>();
        trainingElements.add(createElement(KnowledgeType.ISSUE, "Issue"));
        trainingElements.add(createElement(KnowledgeType.DECISION, "Decision"));
        trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "Alternative"));
        trainingElements.add(createElement(KnowledgeType.PRO, "Pro"));
        trainingElements.add(createElement(KnowledgeType.CON, "Con"));
        trainingElements.add(createElement(KnowledgeType.OTHER, "Pizza"));
        trainingElements.add(createElement(KnowledgeType.ISSUE, "How to"));
        trainingElements.add(createElement(KnowledgeType.DECISION, "We decided"));
        trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "An option would be"));
        trainingElements.add(createElement(KnowledgeType.PRO, "+1"));
        trainingElements.add(createElement(KnowledgeType.CON, "-1"));
        trainingElements.add(createElement(KnowledgeType.OTHER, "Lunch"));
        trainingElements.add(createElement(KnowledgeType.ISSUE, "I don't know how we can"));
        trainingElements.add(createElement(KnowledgeType.DECISION, "We will do"));
        trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "A possible solution could be"));
        trainingElements.add(createElement(KnowledgeType.PRO, "Very good."));
        trainingElements.add(createElement(KnowledgeType.CON, "I don't agree"));
        trainingElements.add(createElement(KnowledgeType.OTHER, "Party tonight"));
        trainingElements.add(createElement(KnowledgeType.ISSUE, "The question is"));
        trainingElements.add(createElement(KnowledgeType.DECISION, "I implemented"));
        trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "We could have done"));
        trainingElements.add(createElement(KnowledgeType.PRO, "Great"));
        trainingElements.add(createElement(KnowledgeType.CON, "No"));
        trainingElements.add(createElement(KnowledgeType.OTHER, "Hello"));
        return trainingElements;
    }

    @Test
    @NonTransactional
    public void testClassificationTrainerSetTrainingData() {
        List<DecisionKnowledgeElement> trainingElements = getTrainingData();
        ClassificationTrainer trainer = new OnlineClassificationTrainerImpl("TEST");
        trainer.setTrainingData(trainingElements);
        //assertNotNull(trainer.getInstances());
        assertTrue(trainer.train());
    }

    @Test
    @NonTransactional
    public void testClassificationTrainerFromArffFile() {
        List<DecisionKnowledgeElement> trainingElements = getTrainingData();
        ClassificationTrainerARFF trainer = new OnlineClassificationTrainerImpl("TEST", trainingElements);
        File file = trainer.saveTrainingFile(true);
        trainer.setTrainingFile(file);
        assertNotNull(trainer.getInstances());
        trainer = new OnlineClassificationTrainerImpl("TEST", file.getName());
        assertNotNull(trainer.getInstances());
        assertTrue(trainer.train());
        file.delete();
    }

    @Test
    @NonTransactional
    public void testSaveArffFile() {
        ClassificationTrainer trainer = new OnlineClassificationTrainerImpl("TEST");
        File file = trainer.saveTrainingFile(false);
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    @NonTransactional
    public void testMockingOfClassifierDirectoryWorks() {
        assertEquals(DecisionKnowledgeClassifier.DEFAULT_DIR, System.getProperty("user.home") + File.separator + "data"
                + File.separator + "condec-plugin" + File.separator + "classifier" + File.separator);
    }

    @Test
    @NonTransactional
    public void testDefaultArffFile() {
        ClassificationTrainer trainer = new OnlineClassificationTrainerImpl();
        File luceneArffFile = getTrimmedDefaultArffFile();
        assertTrue(luceneArffFile.exists());
        trainer.setTrainingFile(luceneArffFile);
        //assertNotNull(trainer.getInstances());
        // assertTrue(trainer.train());
        //
        // DecisionKnowledgeClassifier classifier = trainer.getClassifier();
        // List<String> stringsToBeClassified = Arrays.asList("-1", "Issue", "Decision",
        // "Alternative", "Party tonight",
        // "+1", "Very good.");
        // // List<Boolean> expectedRelevance = Arrays.asList(true, true, false);
        // List<Boolean> predictedRelevance =
        // classifier.makeBinaryPredictions(stringsToBeClassified);
        // // assertEquals(expectedRelevance, predictedRelevance);
        // System.out.println(predictedRelevance);
        //
        // List<KnowledgeType> types =
        // classifier.makeFineGrainedPredictions(stringsToBeClassified);
        // System.out.println(types);
    }

    @Test
    @NonTransactional
    public void testCopyDefaultTrainingDataToFile() {
        assertTrue(ClassificationTrainer.copyDefaultTrainingDataToFile().exists());
    }

    @Test
    @NonTransactional
    public void testTrainDefaultClassifier() {
        File trainingFile = getTrimmedDefaultArffFile();
        assertTrue(ClassificationTrainer.trainClassifier(trainingFile));
    }

    private File getTrimmedDefaultArffFile() {
        File fullDefaultFile = new File("src/main/resources/classifier/defaultTrainingData.arff");
        File trimmedDefaultFile = new File("defaultTrainingData.arff");

        int numberOfLines = 25;

        BufferedWriter writer = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fullDefaultFile));
            writer = new BufferedWriter(new FileWriter(trimmedDefaultFile));

            String currentLine;
            int counter = 0;
            while ((currentLine = reader.readLine()) != null && counter < numberOfLines) {
                writer.write(currentLine + System.getProperty("line.separator"));
                counter++;
            }
            writer.close();
            reader.close();
            return trimmedDefaultFile;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fullDefaultFile;

    }


    @Test
    @NonTransactional
    public void testGetArffFiles() {
        ClassificationTrainerARFF trainer = new OnlineClassificationTrainerImpl();
        assertEquals(ArrayList.class, trainer.getTrainingFileNames().getClass());
    }

}
