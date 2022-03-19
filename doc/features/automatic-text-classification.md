# Automatic Text Classification/Rationale Identification

The ConDec Jira plug-in offers a feature that **automatically classifies text** either as relevant decision knowledge elements or as irrelevant.
The text classifier consists of a **binary** and a **fine-grained classifier**.

## Ground Truth Data
Ground truth data is needed to train and evaluate the text classifier.
ConDec installs two default training files: [one rather small one](../../src/main/resources/classifier/defaultTrainingData.csv) and one with the data used for the NLP4RE'21 paper.

To reproduce the results from the [**NLP4RE'21 paper**](http://ceur-ws.org/Vol-2857/nlp4re1.pdf) do the following steps:
- Install the [version 2.3.2](https://github.com/cures-hub/cures-condec-jira/releases/tag/v2.3.2) of the ConDec Jira plug-in and activate the plug-in for a Jira project.
- Navigate to the text classification settings page (see section below).
- Choose the training file [CONDEC-NLP4RE2021.csv](../../src/main/resources/classifier/CONDEC-NLP4RE2021.csv).
- Set the machine-learning algorithm to Logistic Regression for both the binary and fine-grained classifiers.
- Run 10-fold cross-validation (you need to set k to 10).
- ConDec writes evaluation results to a text file. 
The output file will be similar to [evaluation-results-CONDEC-NLP4RE2021-LR-10fold](evaluation-results-CONDEC-NLP4RE2021-LR-10fold.txt). 
The results might differ a little bit because of the random undersampling that we did to balance the training data.

Basic descriptive statistics on ground truth files can be calculated using the R file [training-data-analysis.r](training-data-analysis.r).

## Usage During Development
The classifier predicts whether the textual parts in the Jira issue description, comments, or commit messages are relevant decision knowledge elements and
-if yes-it annotates the parts accordingly.
ConDec performs the automatic annotation directly in the text of the Jira issue description and comments when developers save textual changes.
Besides, ConDec offers a dedicated view for text classification.
In the text classification view, developers manually approve the classification result, i.e., 
developers decide whether the annotations are correct or not.
Developers can trigger the automatic text classification.

![Text classification view with three sentences not yet manually approved/validated](../screenshots/text_classification_view.png)

*Text classification view with three sentences not yet manually approved/validated*

## Activation and Configuration
The text classifier can be trained and evaluated directly in Jira.

![Configuration view for the automatic text classifier](../screenshots/config_automatic_text_classification.png)
*Configuration view for the automatic text classifier*

## Design Details
The following class diagram gives an overview of relevant backend classes for this feature.
The class *TextClassifier* is responsible for processing the text of the description and comments of a Jira issue. 
It uses a classifier type, ground-truth data, as well as some configuration information in order to classify the text. 
The *ClassificationManagerForJiraIssueText* calls the TextClassifier to classify the text. 
The *JiraTextExtractionEventListener* is used for online learning, so that the classifier can learn while in use. 
Developers manually approve classifications by setting them as "validated". 
Any time a validated element is added, the classifier learns.

![Overview class diagram](../diagrams/class_diagram_classification.png)

*Overview class diagram for the automatic text classification*

The Java code for the automatic text classification can be found here:

- [Java code for automatic text classification](../../src/main/java/de/uhd/ifi/se/decision/management/jira/classification)
- [Java REST API for decision guidance](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/TextClassificationRest.java)

The UI code for the automatic text classification can be found here:

- [Velocity templates for configuration and evaluation](../../src/main/resources/templates/settings/classification)
- [Velocity template for text classification view](../../src/main/resources/templates/tabs/textClassification.vm)
- [JavaScript code for text classification](../../src/main/resources/js/classification)

## Publication
Kleebaum, A., Paech, B., Johanssen, J. O., & Bruegge, B. (2021). 
Continuous Rationale Identification in Issue Tracking and Version Control Systems. 
In REFSQ-2021 Workshops, OpenRE, Posters and Tools Track, and Doctoral Symposium (p. 9). 
Essen/Virtual: CEUR-WS.org. https://doi.org/10.11588/heidok.00029966