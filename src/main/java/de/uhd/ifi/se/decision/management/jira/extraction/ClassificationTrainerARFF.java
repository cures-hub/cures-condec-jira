package de.uhd.ifi.se.decision.management.jira.extraction;

import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class ClassificationTrainerARFF implements ClassificationTrainer {
    protected File directory;
    protected Instances instances;
    protected String projectKey;


    @Override
    public List<File> getTrainingFiles() {
        List<File> arffFilesOnServer = new ArrayList<File>();
        for (File file : directory.listFiles()) {
            if (file.getName().toLowerCase(Locale.ENGLISH).contains(".arff")) {
                arffFilesOnServer.add(file);
            }
        }
        return arffFilesOnServer;
    }

    @Override
    public List<String> getTrainingFileNames() {
        List<File> arffFilesOnServer = getTrainingFiles();
        List<String> arffFileNames = new ArrayList<String>();
        for (File file : arffFilesOnServer) {
            arffFileNames.add(file.getName());
        }
        return arffFileNames;
    }

    private Instances getInstancesFromArffFile(File arffFile) {
        if (!arffFile.exists()) {
            return null;
        }
        Instances instances = null;
        try {
            ConverterUtils.DataSource dataSource = new ConverterUtils.DataSource(arffFile.getPath());
            instances = dataSource.getDataSet();

            if (instances.classIndex() == -1) {
                // Reset index
                instances.setClassIndex(instances.numAttributes() - 1);
            }
        } catch (Exception e) {
            LOGGER.error("Problem to get the instances from ARFF file. Message:" + e.getMessage());
        }
        return instances;
    }

    public Instances getInstancesFromArffFile(String arffFileName) {
        File arffFile = new File(directory + File.separator + arffFileName);

        return getInstancesFromArffFile(arffFile);
    }


    @Override
    public void setTrainingFile(File file) {
        this.instances = getInstancesFromArffFile(file);
    }

    private String getArffFileName() {
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String prefix = "";
        if (projectKey != null) {
            prefix = projectKey;
        }
        return prefix + timestamp.getTime() + ".arff";
    }

    public Instances loadTrainingDataFromJiraIssueText(boolean useOnlyValidatedData) {
        JiraIssueTextPersistenceManager manager = new JiraIssueTextPersistenceManager(projectKey);
        List<DecisionKnowledgeElement> partsOfText = manager.getUserValidatedPartsOfText(projectKey);
        if (!useOnlyValidatedData) {
            partsOfText.addAll(manager.getUnvalidatedPartsOfText(projectKey));
        }
        Instances instances = buildDatasetForMeka(partsOfText);
        return instances;
    }

    @Override
    public File saveTrainingFile(boolean useOnlyValidatedData) {
        File arffFile = null;
        try {
            arffFile = new File(directory + File.separator + getArffFileName());
            arffFile.createNewFile();
            String arffString = createArffString(useOnlyValidatedData);
            PrintWriter writer = new PrintWriter(arffFile, "UTF-8");
            writer.println(arffString);
            writer.close();
        } catch (IOException e) {
            LOGGER.error("The ARFF file could not be saved. Message: " + e.getMessage());
        }
        return arffFile;
    }

    private String createArffString(boolean useOnlyValidatedData) {
        if (instances == null) {
            instances = loadTrainingDataFromJiraIssueText(useOnlyValidatedData);
        }
        return instances.toString();
    }

    @Override
    public void setTrainingData(List<DecisionKnowledgeElement> trainingElements) {
        this.instances = buildDatasetForMeka(trainingElements);
    }

    /**
     * Creates the training instances for the supervised text classifier. The
     * instance contains the knowledge type indicated by the value 1 (or 0 for type
     * OTHER) and the summary of the element.
     * <p>
     * Data appearance:
     *
     * @param trainingElements list of validated decision knowledge elements
     * @return training dataset for the supervised text classifier. The instances
     * that this method returns is the ARFF file that is needed to train the
     * classifier.
     * @relation 'sentences: -C 5'
     * @attribute isAlternative {0,1}
     * @attribute isPro {0,1}
     * @attribute isCon {0,1}
     * @attribute isDecision {0,1}
     * @attribute isIssue {0,1}
     * @attribute sentence string
     * @data 0, 0, 0, 1, 0 'I am a test sentence that is a decision.' 1,0,0,0,0 'I am an
     * alternative for the issue.' 0,0,0,0,1 'And I am the issue for the
     * decision and the alternative.'
     */
    public Instances buildDatasetForMeka(List<DecisionKnowledgeElement> trainingElements) {
        ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();

        // Declare Class value with {0,1} as possible values
        wekaAttributes.add(getAttribute("isAlternative"));
        wekaAttributes.add(getAttribute("isPro"));
        wekaAttributes.add(getAttribute("isCon"));
        wekaAttributes.add(getAttribute("isDecision"));
        wekaAttributes.add(getAttribute("isIssue"));

        // Declare text attribute to hold the message (free form text)
        Attribute attribute = new Attribute("sentence", (List<String>) null);

        // Declare the feature vector
        wekaAttributes.add(attribute);

        Instances instances = new Instances("sentences -C 5 ", wekaAttributes, 1000000);

        for (DecisionKnowledgeElement trainingElement : trainingElements) {
            instances.add(createTrainingInstance(trainingElement, attribute));
        }
        instances.setClassIndex(instances.numAttributes() - 1);
        return instances;
    }

    /**
     * Creates a Attribute which defines the binary Value
     *
     * @param name
     * @return Attribute
     */
    private static Attribute getAttribute(String name) {
        ArrayList<String> rationaleAttribute = new ArrayList<String>();
        rationaleAttribute.add("0");
        rationaleAttribute.add("1");
        return new Attribute(name, rationaleAttribute);
    }

    /**
     * Creates a training instance for the supervised text classifier. The instance
     * contains the knowledge type indicated by the value 1 (or 0 for type OTHER)
     * and the summary of the element.
     *
     * @param element   validated decision knowledge element.
     * @param attribute text attribute.
     * @return training instance for the supervised text classifier.
     */
    private DenseInstance createTrainingInstance(DecisionKnowledgeElement element, Attribute attribute) {
        DenseInstance instance = initInstance();
        switch (element.getType()) {
            case ALTERNATIVE:
                instance.setValue(0, 1);
                break;
            case PRO:
                instance.setValue(1, 1);
                break;
            case CON:
                instance.setValue(2, 1);
                break;
            case DECISION:
                instance.setValue(3, 1);
                break;
            case ISSUE:
                instance.setValue(4, 1);
                break;
            default:
                break;
        }
        instance.setValue(attribute, element.getSummary());
        return instance;
    }

    private DenseInstance initInstance() {
        DenseInstance instance = new DenseInstance(6);
        instance.setValue(0, 0);
        instance.setValue(1, 0);
        instance.setValue(2, 0);
        instance.setValue(3, 0);
        instance.setValue(4, 0);
        return instance;
    }

}
