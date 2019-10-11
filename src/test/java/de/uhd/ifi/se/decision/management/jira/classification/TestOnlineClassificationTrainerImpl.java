package de.uhd.ifi.se.decision.management.jira.classification;

import de.uhd.ifi.se.decision.management.jira.TestSetUp;
import de.uhd.ifi.se.decision.management.jira.classification.implementation.OnlineClassificationTrainerImpl;
import de.uhd.ifi.se.decision.management.jira.model.*;
import de.uhd.ifi.se.decision.management.jira.model.impl.DecisionKnowledgeElementImpl;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.model.text.impl.PartOfJiraIssueTextImpl;
import net.java.ao.test.jdbc.NonTransactional;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestOnlineClassificationTrainerImpl extends TestSetUp {

    private OnlineClassificationTrainerImpl trainer;

    @Before
    public void setUp() {
        init();
        trainer = new OnlineClassificationTrainerImpl("TEST");
        trainer.setTrainingData(getTrainingData());
    }


    private DecisionKnowledgeElement createElement(KnowledgeType type, String summary) {
        DecisionKnowledgeElement element = new DecisionKnowledgeElementImpl();
        element.setType(type);
        element.setSummary(summary);
        return element;
    }

    private List<DecisionKnowledgeElement> getTrainingData() {
        List<DecisionKnowledgeElement> trainingElements = new ArrayList<DecisionKnowledgeElement>();
        trainingElements.add(createElement(KnowledgeType.ISSUE, "I have an issue"));
        trainingElements.add(createElement(KnowledgeType.DECISION, "Thats is a Decision"));
        trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "This is an Alternative"));
        trainingElements.add(createElement(KnowledgeType.PRO, "Pro"));
        trainingElements.add(createElement(KnowledgeType.CON, "Con"));
        trainingElements.add(createElement(KnowledgeType.OTHER, "Pizza is preferred"));
        trainingElements.add(createElement(KnowledgeType.ISSUE, "How to do that"));
        trainingElements.add(createElement(KnowledgeType.DECISION, "We decided on option A."));
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
        trainingElements.add(createElement(KnowledgeType.DECISION, "I implemented the feature."));
        trainingElements.add(createElement(KnowledgeType.ALTERNATIVE, "We could have done option A."));
        trainingElements.add(createElement(KnowledgeType.PRO, "Great work guys!"));
        trainingElements.add(createElement(KnowledgeType.CON, "No that is not ok"));
        trainingElements.add(createElement(KnowledgeType.OTHER, "Hello"));
        return trainingElements;
    }

    @Test
    @NonTransactional
    public void testOnlineClassificationTrainerSetTrainingData() {
        List<DecisionKnowledgeElement> trainingElements = getTrainingData();
        trainer.setTrainingData(trainingElements);
        Assert.assertTrue(trainer.train());
    }

    @Test
    @NonTransactional
    public void testOnlineClassificationTrainerFromArffFile() {
        File file = trainer.saveTrainingFile(true);
        trainer.setTrainingFile(file);
        assertNotNull(trainer.getInstances());
        trainer = new OnlineClassificationTrainerImpl("TEST", file.getName());
        //assertNotNull(trainer.getInstances());
        assertTrue(trainer.train());
        file.delete();
    }

    @Test
    @NonTransactional
    public void testSaveArffFile() {
        File file = trainer.saveTrainingFile(false);
        Assert.assertTrue(file.exists());
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
        File file = getTrimmedDefaultArffFile();
        Assert.assertTrue(file.exists());
        trainer.setTrainingFile(file);
    }

    @Test
    @NonTransactional
    public void testCopyDefaultTrainingDataToFile() {
        assertTrue(ClassificationTrainer.copyDefaultTrainingDataToFile().exists());
    }

    @Test
    @NonTransactional
    public void testGetArffFiles() {
        assertEquals(ArrayList.class, trainer.getTrainingFileNames().getClass());
    }

    @Test
    @NonTransactional
    public void testEvaluateClassifier(){
        trainer.train();
        boolean executionSuccessful = true;
        try{
            trainer.evaluateClassifier();
        }catch (Exception e){
            executionSuccessful = false;
        }
        assertTrue(executionSuccessful);
    }


    @Test
    @NonTransactional
    public void testUpdate() {
        trainer.train();

        PartOfJiraIssueText sentence = new PartOfJiraIssueTextImpl();
        sentence.setDescription("In my opinion the query would be better!");
        sentence.setRelevant(true);
        sentence.setType(KnowledgeType.ALTERNATIVE);
        sentence.setValidated(true);

        assertTrue(trainer.update(sentence));
    }

    @Test
    @NonTransactional
    public void testGetClassifier() {
        assertNotNull(this.trainer.getClassifier());
    }

    @Test
    @NonTransactional
    public void testGetInstances() {
        assertNotNull(this.trainer.getInstances());
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

}
