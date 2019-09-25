package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import de.uhd.ifi.se.decision.management.jira.classification.ClassificationTrainerARFF;
import de.uhd.ifi.se.decision.management.jira.classification.Classifier;
import de.uhd.ifi.se.decision.management.jira.classification.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import meka.classifiers.multilabel.LC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;

import java.io.File;
import java.util.*;

import static java.lang.Math.round;

/**
 * Class responsible to train the supervised text classifier. For this purpose,
 * the project admin needs to create and select an ARFF file.
 */
public class OnlineClassificationTrainerImpl extends ClassificationTrainerARFF {

    private DecisionKnowledgeClassifier classifier;

    //private static OnlineClassificationTrainerImpl instance;

    protected static final Logger LOGGER = LoggerFactory.getLogger(OnlineClassificationTrainerImpl.class);

    public OnlineClassificationTrainerImpl() {
        this.classifier = new DecisionKnowledgeClassifierImpl();
        super.directory = new File(DecisionKnowledgeClassifier.DEFAULT_DIR);
        directory.mkdirs();
    }

    public OnlineClassificationTrainerImpl(String projectKey) {
        this();
        this.projectKey = projectKey;
    }

    public OnlineClassificationTrainerImpl(String projectKey, String fileName) {
        this(projectKey);
        if ((fileName == null || fileName.isEmpty())) {
            return;
        }
        this.instances = super.getInstancesFromArffFile(fileName);
    }

    public OnlineClassificationTrainerImpl(String projectKey, List<DecisionKnowledgeElement> trainingElement) {
        this(projectKey);
        this.setTrainingData(trainingElement);
    }

    /*
        public static OnlineClassificationTrainerImpl getInstance() {
            if (instance == null) {
                instance = new OnlineClassificationTrainerImpl();
            }
            return instance;
        }

        public static OnlineClassificationTrainerImpl getInstance(String projectKey) {
            if (instance == null) {
                instance = new OnlineClassificationTrainerImpl(projectKey);
            }
            return instance;
        }

        public static OnlineClassificationTrainerImpl getInstance(String projectKey, List<DecisionKnowledgeElement> trainingElements) {
            if (instance == null) {
                instance = new OnlineClassificationTrainerImpl(projectKey, trainingElements);
            }
            instance.setTrainingData(trainingElements);
            return instance;
        }

        public static OnlineClassificationTrainerImpl getInstance(String projectKey, String trainingFileName) {
            if (instance == null) {
                instance = new OnlineClassificationTrainerImpl(projectKey, trainingFileName);
            }
            return instance;
        }


     */
    @Override
    //is called after setting trainings-file
    public boolean train() {
        boolean isTrained = false;
        try {
            Map<String, List> trainingData = this.extractTrainingData(super.instances);
            Map preprocessedIsRelevantSentences = this.classifier.preprocess(trainingData.get("sentences"),
                    trainingData.get("labelsIsRelevant"));
            this.classifier.trainBinaryClassifier((List<List<Double>>) preprocessedIsRelevantSentences.get("features"),
                    (List<Integer>) preprocessedIsRelevantSentences.get("labels"));
            Map preprocessedFineSentences = this.classifier.preprocess(trainingData.get("sentences"),
                    trainingData.get("labelsKnowledgeType"));
            this.classifier.trainFineGrainedClassifier((List<List<Double>>) preprocessedIsRelevantSentences.get("features"),
                    (List<Integer>) preprocessedIsRelevantSentences.get("labels"));

            //this.evaluateTraining();

            this.classifier.getBinaryClassifier().saveToFile();
            this.classifier.getFineGrainedClassifier().saveToFile();

            isTrained = true;
        } catch (Exception e) {
            LOGGER.error("The classifier could not be trained. Message:" + e.getMessage());
        }
        return isTrained;
    }


    public void update(PartOfJiraIssueText sentence) {
        List<Double> feature = this.classifier.preprocess(sentence.getText());
        // classifier needs numerical value
        Integer labelIsRelevant = sentence.isRelevant() ? 1 : 0;
        this.classifier.getBinaryClassifier().train(feature.toArray(Double[]::new), labelIsRelevant);

        KnowledgeType labelKnowledgeType = sentence.getType();
        this.classifier.getFineGrainedClassifier().train(feature.toArray(Double[]::new), labelKnowledgeType);
    }

    @Override
    public DecisionKnowledgeClassifier getClassifier() {
        return classifier;
    }

    public Instances getInstances() {
        return instances;
    }

    private Map<String, List> extractTrainingData(Instances trainingData) {
        Map extractedTrainingData = new HashMap();
        List sentences = new ArrayList();
        List labelsIsRelevant = new ArrayList();
        List labelsKnowledgeType = new ArrayList();
        //TODO: can we use the names instead of indices?
        //iterate over all instances
        for (int i = 0; i < trainingData.size(); i++) {
            Instance currInstance = trainingData.get(i);
            // last attribute is the sentence that needs to be classified
            sentences.add(currInstance.stringValue(currInstance.numAttributes() - 1));

            Integer isRelevant = 0;
            Integer fineGrainedLabel = -1;
            // iterate over the binary attributes for each possible class
            for (int j = 0; j < currInstance.numAttributes() - 1; j++) {
                if (round(currInstance.value(j)) == 1) {
                    isRelevant = 1;
                    fineGrainedLabel = j;
                }
            }
            labelsIsRelevant.add(isRelevant);
            labelsKnowledgeType.add(fineGrainedLabel);
        }

        extractedTrainingData.put("sentences", sentences);
        extractedTrainingData.put("labelsIsRelevant", labelsIsRelevant);
        extractedTrainingData.put("labelsKnowledgeType", labelsKnowledgeType);


        return extractedTrainingData;
    }


    private void evaluateTraining(LC binaryRelevance) throws Exception {
        Evaluation rate = new Evaluation(instances);
        Random seed = new Random(1);
        Instances datarandom = new Instances(instances);
        datarandom.randomize(seed);

        int folds = 10;
        datarandom.stratify(folds);
        rate.crossValidateModel(binaryRelevance, instances, folds, seed);

        LOGGER.info(rate.toSummaryString());
        LOGGER.info("Structure num classes: " + instances.numClasses());

        // for (int i = 0; i < instances.numClasses(); i++) {
        // System.out.println(rate.fMeasure(i));
        // }
    }

    private void evaluateTraining(Classifier classfier) throws Exception {
        Evaluation rate = new Evaluation(instances);
        Random seed = new Random(1);
        Instances datarandom = new Instances(instances);
        datarandom.randomize(seed);

        int folds = 10;
        datarandom.stratify(folds);
        //rate.crossValidateModel(binaryRelevance, instances, folds, seed);

        LOGGER.info(rate.toSummaryString());
        LOGGER.info("Structure num classes: " + instances.numClasses());

        // for (int i = 0; i < instances.numClasses(); i++) {
        // System.out.println(rate.fMeasure(i));
        // }
    }

}
