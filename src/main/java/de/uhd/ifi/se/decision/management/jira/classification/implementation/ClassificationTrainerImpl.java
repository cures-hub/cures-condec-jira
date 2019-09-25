package de.uhd.ifi.se.decision.management.jira.classification.implementation;

import de.uhd.ifi.se.decision.management.jira.classification.ClassificationTrainerARFF;
import de.uhd.ifi.se.decision.management.jira.classification.DecisionKnowledgeClassifier;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import meka.classifiers.multilabel.LC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * Class responsible to train the supervised text classifier. For this purpose,
 * the project admin needs to create and select an ARFF file.
 */
public class ClassificationTrainerImpl extends ClassificationTrainerARFF {

    private DecisionKnowledgeClassifier classifier;

    protected static final Logger LOGGER = LoggerFactory.getLogger(ClassificationTrainerImpl.class);

    public ClassificationTrainerImpl() {
        super.directory = new File(DecisionKnowledgeClassifier.DEFAULT_DIR);
        directory.mkdirs();
    }

    public ClassificationTrainerImpl(String projectKey) {
        this();
        this.projectKey = projectKey;
    }

    public ClassificationTrainerImpl(String projectKey, String fileName) {
        this(projectKey);
        if (fileName == null || fileName.isEmpty()) {
            // TODO Use default file
            return;
        }
        this.instances = super.getInstancesFromArffFile(fileName);
    }

    public ClassificationTrainerImpl(String projectKey, List<DecisionKnowledgeElement> trainingElement) {
        this(projectKey);
        setTrainingData(trainingElement);
    }


    @Override
    public boolean train() {
        boolean isTrained = false;
        try {
            LC fineGrainedClassifier = new LC();
            FilteredClassifier binaryClassifier = new FilteredClassifier();
            StringToWordVector stringToWordVector = DecisionKnowledgeClassifier.getStringToWordVector();
            binaryClassifier.setFilter(stringToWordVector);
            binaryClassifier.setClassifier(new NaiveBayesMultinomial());
            fineGrainedClassifier.setClassifier(binaryClassifier);

            evaluateTraining(fineGrainedClassifier);

            fineGrainedClassifier.buildClassifier(instances);
            SerializationHelper.write(directory + File.separator + "binaryClassifier.model", binaryClassifier);
            SerializationHelper.write(directory + File.separator + "fineGrainedClassifier.model",
                    fineGrainedClassifier);

            //classifier = new DecisionKnowledgeClassifierImpl(binaryClassifier, fineGrainedClassifier);
            //classifier.setBinaryClassifier(binaryClassifier);
            //classifier.setFineGrainedClassifier(fineGrainedClassifier);

            isTrained = true;
        } catch (Exception e) {
            LOGGER.error("The classifier could not be trained. Message:" + e.getMessage());
        }
        return isTrained;
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


    @Override
    public DecisionKnowledgeClassifier getClassifier() {
        return classifier;
    }

    public Instances getInstances() {
        return instances;
    }
}
