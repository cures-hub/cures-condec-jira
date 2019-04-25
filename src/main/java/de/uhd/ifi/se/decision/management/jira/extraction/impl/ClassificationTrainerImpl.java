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
import de.uhd.ifi.se.decision.management.jira.model.KnowledgeType;
import de.uhd.ifi.se.decision.management.jira.model.text.PartOfJiraIssueText;
import de.uhd.ifi.se.decision.management.jira.persistence.JiraIssueTextPersistenceManager;
import meka.classifiers.multilabel.LC;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import weka.core.tokenizers.NGramTokenizer;
import weka.core.tokenizers.Tokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 * Class responsible to train the classifier with the ARFF file selected by the
 * project admin.
 */
public class ClassificationTrainerImpl implements ClassificationTrainer {

	private String projectKey;
	private Instances structure;
	private List<PartOfJiraIssueText> mekaTrainData;

	/**
	 * In the Constructor the Sentences from Database will be uses for the
	 * classification that is validated from the user.
	 * 
	 * @param projectKey
	 */
	public ClassificationTrainerImpl(String projectKey) {
		this.projectKey = projectKey;
		JiraIssueTextPersistenceManager manager = new JiraIssueTextPersistenceManager(projectKey);
		mekaTrainData = manager.getUserValidatedPartsOfText(projectKey);
	}

	public ClassificationTrainerImpl(String projectKey, String arffFileName) {
		this(projectKey);

		if (arffFileName.isEmpty() || arffFileName == null) {
			// TODO Use default ARFF file
			return;
		}

		try {
			ConverterUtils.DataSource source = new ConverterUtils.DataSource(
					DEFAULT_DIR + File.separator + projectKey + File.separator + arffFileName);
			structure = source.getDataSet();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void train() {
		try {
			LC binaryRelevance = new LC();
			FilteredClassifier fc = new FilteredClassifier();
			fc.setFilter(getSTWV());
			fc.setClassifier(new NaiveBayesMultinomial());
			binaryRelevance.setClassifier(fc);

			evaluateTraining(binaryRelevance);

			binaryRelevance.buildClassifier(structure);
			File directory = new File(DEFAULT_DIR + File.separator + projectKey);
			directory.mkdirs();
			weka.core.SerializationHelper
					.write(DEFAULT_DIR + File.separator + projectKey + File.separator + "newBr.model", binaryRelevance);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param trainSentences
	 * @return The training dataset. The Instance that this function returns is the
	 *         ARFF File that is needed to train the Classifier. The Attributes are
	 *         Boolean and the Sentence is a String.
	 *
	 *         Data appearance:
	 * @relation 'sentences: -C 5'
	 * @attribute isAlternative {0,1}
	 * @attribute isPro {0,1}
	 * @attribute isCon {0,1}
	 * @attribute isDecision {0,1}
	 * @attribute isIssue {0,1}
	 * @attribute sentence string
	 *
	 * @data 0,0,0,1,0 'I am at Test sentence that is a Decision' 1,0,0,0,0 'I am a
	 *       Alternative for the Issue' 0,0,0,0,1 'And i am the Issue for the
	 *       Decision and the Alternative'
	 */
	public Instances buildDatasetForMeka(List<PartOfJiraIssueText> trainSentences) {

		ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();

		// Declare Class value with {0,1} as possible values
		wekaAttributes.add(getAttribute("isAlternative"));
		wekaAttributes.add(getAttribute("isPro"));
		wekaAttributes.add(getAttribute("isCon"));
		wekaAttributes.add(getAttribute("isDecision"));
		wekaAttributes.add(getAttribute("isIssue"));

		// Declare text attribute to hold the message (free form text)
		Attribute attributeText = new Attribute("sentence", (List<String>) null);

		// Declare the feature vector
		wekaAttributes.add(attributeText);

		Instances data = new Instances("sentences -C 5 ", wekaAttributes, 1000000);

		for (PartOfJiraIssueText trainSentence : trainSentences) {
			data.add(createTrainData(trainSentence, attributeText));
		}
		data.setClassIndex(data.numAttributes() - 1);
		return data;
	}

	public File saveArffFile() {
		File arffFile = null;
		try {
			File defdirectory = new File(DEFAULT_DIR);
			defdirectory.mkdirs();
			String pathToDirectory = DEFAULT_DIR + File.separator + projectKey;
			File directory = new File(pathToDirectory);
			directory.mkdirs();
			if (!directory.exists()) {
				return null;
			}
			Date date = new Date();
			Timestamp timestamp = new Timestamp(date.getTime());

			arffFile = new File(pathToDirectory + File.separator + "arffFile" + timestamp.getTime() + ".arff");
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

	public List<String> getArffFiles() {
		List<String> arffFilesOnServer = new ArrayList<String>();
		File directory = new File(DEFAULT_DIR + File.separator + projectKey + File.separator);
		if (directory.exists()) {
			for (File file : directory.listFiles()) {
				arffFilesOnServer.add(file.getName());
			}
		}
		return arffFilesOnServer;
	}

	public String getArffFileString(String fileName) {
		File directory = new File(DEFAULT_DIR + File.separator + projectKey);
		String returnString = "";
		if (directory.exists()) {
			File[] fileArray = directory.listFiles();
			for (File file : directory.listFiles()) {
				if (file.getName().equalsIgnoreCase(fileName)) {
					try {
						FileReader fileReader = new FileReader(file);
						BufferedReader bufferedReader = new BufferedReader(fileReader);
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
		}
		return returnString;
	}

	private String createArffString() {
		Instances data = this.buildDatasetForMeka(mekaTrainData);
		return data.toString();
	}

	/**
	 *
	 * @param sentence
	 * @param attributeText
	 * @return a Data entry for the training of the classifier. The Instance
	 *         contains the Knowledge Type as a 1 and the Sentence. The Knowledge
	 *         Types that are not wrong are set to 0.
	 */
	private DenseInstance createTrainData(PartOfJiraIssueText sentence, Attribute attributeText) {
		DenseInstance newInstance = new DenseInstance(6);
		newInstance.setValue(0, 0);
		newInstance.setValue(1, 0);
		newInstance.setValue(2, 0);
		newInstance.setValue(3, 0);
		newInstance.setValue(4, 0);
		if (sentence.getType() == KnowledgeType.ALTERNATIVE) {
			newInstance.setValue(0, 1);
		}
		if (sentence.getType() == KnowledgeType.PRO) {
			newInstance.setValue(1, 1);
		}
		if (sentence.getType() == KnowledgeType.CON) {
			newInstance.setValue(2, 1);
		}
		if (sentence.getType() == KnowledgeType.DECISION) {
			newInstance.setValue(3, 1);
		}
		if (sentence.getType() == KnowledgeType.ISSUE) {
			newInstance.setValue(4, 1);
		}
		newInstance.setValue(attributeText, sentence.getText());
		return newInstance;
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

	private void evaluateTraining(LC binaryRelevance) throws Exception {
		Evaluation rate = new Evaluation(structure);
		Random seed = new Random(1);
		Instances datarandom = new Instances(structure);
		datarandom.randomize(seed);

		int folds = 10;
		datarandom.stratify(folds);
		rate.crossValidateModel(binaryRelevance, structure, folds, seed);
		System.out.println("Structure num classes: " + structure.numClasses());
	}

	/**
	 * Creates the Tokenizer and sets the Values and Options for the String to Word
	 * Vector
	 * 
	 * @return Tokenizer
	 * @throws Exception
	 */
	private static Tokenizer getTokenizer() throws Exception {
		Tokenizer t = new NGramTokenizer();
		String[] options = weka.core.Utils.splitOptions(
				"weka.core.tokenizers.NGramTokenizer -max 3 -min 1 -delimiters \" \\r\\n\\t.,;:\\'\\\"()?!\"");
		t.setOptions(options);
		return t;
	}

	/**
	 * Creates a String to Word Vector for the Classifier All Elements are Lowercase
	 * Tokens
	 * 
	 * @return StringToWordVector
	 * @throws Exception
	 */
	private static StringToWordVector getSTWV() throws Exception {
		StringToWordVector stwv = new StringToWordVector();
		stwv.setLowerCaseTokens(true);
		stwv.setIDFTransform(true);
		stwv.setTFTransform(true);
		stwv.setTokenizer(getTokenizer());
		stwv.setWordsToKeep(1000000);
		return stwv;
	}
}
