package de.uhd.ifi.se.decision.management.jira.extraction.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import de.uhd.ifi.se.decision.management.jira.extraction.ClassificationTrainer;
import de.uhd.ifi.se.decision.management.jira.model.DecisionKnowledgeElement;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import meka.classifiers.multilabel.LC;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ConverterUtils;
import weka.core.converters.ConverterUtils.DataSource;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Class responsible to train the supervised text classifier. For this purpose,
 * the project admin needs to create and select an ARFF file.
 */
public class ClassificationTrainerImpl implements ClassificationTrainer {

	private String projectKey;
	private File directory;
	private Instances instances;

	public ClassificationTrainerImpl(String projectKey) {
		this.projectKey = projectKey;
		this.directory = new File(DEFAULT_DIR + File.separator + projectKey);
		directory.mkdirs();
	}

	public ClassificationTrainerImpl(String projectKey, String arffFileName) {
		this(projectKey);
		if (arffFileName == null || arffFileName.isEmpty()) {
			// TODO Use default ARFF file
			return;
		}
		this.instances = getInstancesFromArffFile(arffFileName);
	}

	public ClassificationTrainerImpl(String projectKey, List<DecisionKnowledgeElement> trainingElement) {
		this(projectKey);
		setTrainingData(trainingElement);
	}

	private Instances getInstancesFromArffFile(String arffFileName) {
		Instances instances = null;
		try {
			DataSource dataSource = new ConverterUtils.DataSource(directory + File.separator + arffFileName);
			instances = dataSource.getDataSet();

			if (instances.classIndex() == -1) {
				// Reset index
				instances.setClassIndex(instances.numAttributes() - 1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return instances;
	}

	public void setArffFile(File arffFile) {
		this.instances = getInstancesFromArffFile(arffFile.getName());
	}

	@Override
	public boolean train() {
		boolean isTrained = false;
		try {
			LC binaryRelevance = new LC();
			FilteredClassifier filteredClassifier = new FilteredClassifier();
			filteredClassifier.setFilter(getStringToWordVector());
			filteredClassifier.setClassifier(new NaiveBayesMultinomial());
			binaryRelevance.setClassifier(filteredClassifier);

			evaluateTraining(binaryRelevance);

			binaryRelevance.buildClassifier(instances);
			SerializationHelper.write(directory + File.separator + "newBr.model", binaryRelevance);

			isTrained = true;
		} catch (Exception e) {
			e.printStackTrace();
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

		System.out.println(rate.toSummaryString());
		System.out.println("Structure num classes: " + instances.numClasses());

		// for (int i = 0; i < instances.numClasses(); i++) {
		// System.out.println(rate.fMeasure(i));
		// }
	}

	@Override
	public File saveArffFile() {
		File arffFile = null;
		try {
			arffFile = new File(directory + File.separator + getArffFileName());
			arffFile.createNewFile();
			String arffString = createArffString();
			PrintWriter writer = new PrintWriter(arffFile, "UTF-8");
			writer.println(arffString);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return arffFile;
	}

	private String getArffFileName() {
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		return "arffFile" + timestamp.getTime() + ".arff";
	}

	private String createArffString() {
		if (instances == null) {
			instances = loadTrainingDataFromJiraIssueText();
		}
		return instances.toString();
	}

	public Instances loadTrainingDataFromJiraIssueText() {
		JiraIssueTextPersistenceManager manager = new JiraIssueTextPersistenceManager(projectKey);
		List<DecisionKnowledgeElement> validatedPartsOfText = manager.getUserValidatedPartsOfText(projectKey);
		Instances instances = buildDatasetForMeka(validatedPartsOfText);
		return instances;
	}

	@Override
	public void setTrainingData(List<DecisionKnowledgeElement> trainingElements) {
		this.instances = buildDatasetForMeka(trainingElements);
	}

	/**
	 * Creates the training instances for the supervised text classifier. The
	 * instance contains the knowledge type indicated by the value 1 (or 0 for type
	 * OTHER) and the summary of the element.
	 * 
	 * Data appearance:
	 * 
	 * @relation 'sentences: -C 5'
	 * @attribute isAlternative {0,1}
	 * @attribute isPro {0,1}
	 * @attribute isCon {0,1}
	 * @attribute isDecision {0,1}
	 * @attribute isIssue {0,1}
	 * @attribute sentence string
	 *
	 * @data 0,0,0,1,0 'I am a test sentence that is a decision.' 1,0,0,0,0 'I am an
	 *       alternative for the issue.' 0,0,0,0,1 'And I am the issue for the
	 *       decision and the alternative.'
	 * 
	 * @param trainingElements
	 *            list of validated decision knowledge elements
	 * @return training dataset for the supervised text classifier. The instances
	 *         that this method returns is the ARFF file that is needed to train the
	 *         classifier.
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
	 * Creates a training instance for the supervised text classifier. The instance
	 * contains the knowledge type indicated by the value 1 (or 0 for type OTHER)
	 * and the summary of the element.
	 * 
	 * @param element
	 *            validated decision knowledge element.
	 * @param attribute
	 *            text attribute.
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

	@Override
	public List<File> getArffFiles() {
		List<File> arffFilesOnServer = new ArrayList<File>();
		for (File file : directory.listFiles()) {
			arffFilesOnServer.add(file);
		}
		return arffFilesOnServer;
	}

	@Override
	public List<String> getArffFileNames() {
		List<File> arffFilesOnServer = getArffFiles();
		List<String> arffFileNames = new ArrayList<String>();
		for (File file : arffFilesOnServer) {
			arffFileNames.add(file.getName());
		}
		return arffFileNames;
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
	 * Creates the Tokenizer and sets the Values and Options for the String to Word
	 * Vector
	 * 
	 * @return Tokenizer
	 * @throws Exception
	 */
	private static Tokenizer getTokenizer() throws Exception {
		Tokenizer tokenizer = new NGramTokenizer();
		String[] options = weka.core.Utils.splitOptions(
				"weka.core.tokenizers.NGramTokenizer -max 3 -min 1 -delimiters \" \\r\\n\\t.,;:\\'\\\"()?!\"");
		tokenizer.setOptions(options);
		return tokenizer;
	}

	/**
	 * Creates a String to Word Vector for the Classifier All Elements are Lowercase
	 * Tokens
	 * 
	 * @return StringToWordVector
	 * @throws Exception
	 */
	private static StringToWordVector getStringToWordVector() throws Exception {
		StringToWordVector stringToWordVector = new StringToWordVector();
		stringToWordVector.setLowerCaseTokens(true);
		stringToWordVector.setIDFTransform(true);
		stringToWordVector.setTFTransform(true);
		stringToWordVector.setTokenizer(getTokenizer());
		stringToWordVector.setWordsToKeep(1000000);
		return stringToWordVector;
	}

	public String getArffFileString(String fileName) {
		BufferedReader bufferedReader;
		String returnString = "";
		for (File file : directory.listFiles()) {
			if (file.getName().equalsIgnoreCase(fileName)) {
				try {
					FileReader fileReader = new FileReader(file);
					bufferedReader = new BufferedReader(fileReader);
					String line;
					while ((line = bufferedReader.readLine()) != null) {
						returnString += line + System.lineSeparator();
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return "There was an Error Reading the File. Try again";
				} catch (IOException e) {
					e.printStackTrace();
					return "There was an Error Reading the File. Try again";
				}
			}
		}
		return returnString;
	}
}
