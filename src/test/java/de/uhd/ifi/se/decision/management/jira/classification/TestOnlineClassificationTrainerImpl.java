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

import java.io.File;
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
    public void testOnlineClassificationTrainerSetTrainingData() {
        List<DecisionKnowledgeElement> trainingElements = getTrainingData();
        trainer.setTrainingData(trainingElements);
        Assert.assertTrue(trainer.train());
    }

    @Test
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
    public void testSaveArffFile() {
        File file = trainer.saveTrainingFile(false);
        Assert.assertTrue(file.exists());
        file.delete();
    }

    @Test
    public void testMockingOfClassifierDirectoryWorks() {
        assertEquals(DecisionKnowledgeClassifier.DEFAULT_DIR, System.getProperty("user.home") + File.separator + "data"
                + File.separator + "condec-plugin" + File.separator + "classifier" + File.separator);
    }

    @Test
    public void testDefaultArffFile() {
        File luceneArffFile = getDefaultArffFile();
        Assert.assertTrue(luceneArffFile.exists());
        trainer.setTrainingFile(luceneArffFile);
    }

    @Test
    public void testCopyDefaultTrainingDataToFile() {
        assertFalse(ClassificationTrainer.copyDefaultTrainingDataToFile(new File("")).exists());
        File luceneArffFile = getDefaultArffFile();
        Assert.assertTrue(ClassificationTrainer.copyDefaultTrainingDataToFile(luceneArffFile).exists());
    }

    @Test
    public void testTrainDefaultClassifier() {
        // File luceneArffFile = getDefaultArffFile();
        // assertTrue(OnlineClassificationTrainer.trainClassifier(luceneArffFile));
        assertFalse(ClassificationTrainer.trainDefaultClassifier());
    }

    private File getDefaultArffFile() {
        File luceneArffFile = new File("src" + File.separator + "main" + File.separator + "resources" + File.separator
                + "classifier" + File.separator + "lucene.arff");
        return luceneArffFile;
    }

    @Test
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
    public void testGetClassifier() {
        assertNotNull(this.trainer.getClassifier());
    }

    @Test
    public void testGetInstances() {
        assertNotNull(this.trainer.getInstances());
    }


}
