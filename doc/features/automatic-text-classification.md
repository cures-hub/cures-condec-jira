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
The classifier predicts whether the textual parts in the Jira issue description, comments, or commit messages are relevant decision knowledge elements and, 
if yes, it annotates the parts accordingly.
ConDec performs the **automatic annotation** directly **in the text of the Jira issue description and comments** when developers save textual changes.
Besides, ConDec offers a dedicated **view for text classification**.
In the text classification view, developers **manually approve the classification result**, i.e., 
developers decide whether the annotations are correct or not.
Developers can trigger the automatic text classification using the *Auto-Classify* button.

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

# Important Decisions
In the following, important decision knowledge regarding the automatic text classification feature is listed.
The knowledge was exported via [ConDec's knowledge export feature](knowledge-export.md) starting from the 
user sub-tasks *ST: Continuously document decision knowledge in Jira*, *ST: Set up rationale management process*, and 
*ST: Evaluate the performance of algorithms and "smart features"*.
Decision knowledge elements that belong to the decision group *text classification* and 
that are directly and indirectly (transitively) linked to the sub-tasks are shown.

- ST: Continuously document decision knowledge in Jira ([CONDEC-188](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-188))
	- ![Issue](../../src/main/resources/images/issue.png) How to manually classify text as an decision knowledge element?
		- ![Decision](../../src/main/resources/images/decision.png) Annotate/tag text with Jira macros or icons/emojis to classify it as an decision knowledge element! Automatically replace icons with macro tags, e.g. "\(!\) How to...?" is replaced with "\ How to...? \"!
	- ![Issue](../../src/main/resources/images/issue.png) How to enable the user to manually classify parts/sentences of Jira issue comments and description into the knowledge types activated for the project?
		- ![Decision](../../src/main/resources/images/decision.png) We enable the user to manually classify parts/sentences of Jira issue comments and description using annotations/tags!
	- ![Issue](../../src/main/resources/images/issue.png) How can we update knowledge elements from Jira issue text after the text was edited by the user?
		- ![Decision](../../src/main/resources/images/decision.png) We catch the comment edited and Jira issue updated events via an event listener to update knowledge elements from Jira issue text after the text was edited by the user!
	- ![Issue](../../src/main/resources/images/issue.png) How can we get the part of text from database while creating a colored decision knowledge macro in Jira issue text?
		- ![Decision](../../src/main/resources/images/decision.png) We get all parts of text of the current Jira issue, filter them for the current macro type, and select them by text body to get the part of text from database while creating a colored decision knowledge macro in Jira issue text!
			- ![Con](../../src/main/resources/images/argument_con.png) this approach is slow for larger comment sections
			- ![Con](../../src/main/resources/images/argument_con.png) If a text does not match, no context menu can be provided
			- ![Pro](../../src/main/resources/images/argument_pro.png) Enables to add the context menu on parts of text in Jira issue description/comments.
	- ![Issue](../../src/main/resources/images/issue.png) Which decision knowledge types do we enable for manual annotation in Jira issue text?
		- ![Decision](../../src/main/resources/images/decision.png) We enable only five decision knowledge types for manual annotation in Jira issue text: issue, decision, alternative, pro, and con!
			- ![Con](../../src/main/resources/images/argument_con.png) Only a subset of elements in the DDM
			- ![Pro](../../src/main/resources/images/argument_pro.png) Most common types
		- ![Alternative](../../src/main/resources/images/alternative.png) We could try to enable all activated decision knowledge types for manual annotation in Jira issue text.
			- ![Con](../../src/main/resources/images/argument_con.png) A lot of macro classes and entries in atlassian-plugin.xml would be needed.
	- ![Issue](../../src/main/resources/images/issue.png) How to identify invalid parts of text in database?
		- ![Decision](../../src/main/resources/images/decision.png) Set elements with an empty summary and decription as invalid!
	- ![Issue](../../src/main/resources/images/issue.png) How to deal with an updated Jira issue description or comment?
		- ![Alternative](../../src/main/resources/images/alternative.png) Update the existing elements in database if the number of total elements does not decrease!
			- ![Con](../../src/main/resources/images/argument_con.png) If a new element is added in between, e.g. a new argument, the links are wrong.
		- ![Decision](../../src/main/resources/images/decision.png) Update the existing elements in database if the number of total elements stays the same!
			- ![Pro](../../src/main/resources/images/argument_pro.png) Keeps the existing links
		- ![Decision](../../src/main/resources/images/decision.png) Reread decision knowledge elements from Jira issue description or comment if the user added a new element or deleted an existing element in the text!
	- ![Issue](../../src/main/resources/images/issue.png) How should we deal with Jira issue text annotated with decision knowledge tags that includes text formatting, inner macros, images and so on?
		- ![Alternative](../../src/main/resources/images/alternative.png) Render text formatting, inner macros, images and so on in knowledge classification macros!
			- ![Con](../../src/main/resources/images/argument_con.png) The content in the description or comment of a Jira issue looks different to the graph node.
			- ![Con](../../src/main/resources/images/argument_con.png) After rendering, it is not clear whether the inner macro is part of the knowledge element.
		- ![Decision](../../src/main/resources/images/decision.png) Do not render text formatting, inner macros, images and so on in knowledge classification macros, only render line breaks!
			- ![Pro](../../src/main/resources/images/argument_pro.png) A knowledge element should be formulated in a short easy way so that it is easy to understand without cluttering through images, macros and so on. All other information is context information, which a developer can read from the text.
			- ![Pro](../../src/main/resources/images/argument_pro.png) Is consistent with the graph/tree nodes because there is also nothing rendered.
			- ![Pro](../../src/main/resources/images/argument_pro.png) Popular Jira macros such as code also do not render inner macros.
	- ![Issue](../../src/main/resources/images/issue.png) Where do we show a tree viewer/indented outline with decision knowledge extracted from Jira issue text?
		- ![Alternative](../../src/main/resources/images/alternative.png) We could try to clone the comment section.
			- ![Pro](../../src/main/resources/images/argument_pro.png) It is not clear whether and how this would work.
		- ![Decision](../../src/main/resources/images/decision.png) We decided to include the tree viewer/indented outline in the Jira issue module!
			- ![Pro](../../src/main/resources/images/argument_pro.png) All the ConDec views are in the same place.
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We used to show the tree viewer/indented outline in a separate ConDec tab panel.
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We used to show the tree viewer/indented outline in a separate dialog.
	- ![Issue](../../src/main/resources/images/issue.png) How can we remove the icon's textual representation from Treant nodes?
		- ![Decision](../../src/main/resources/images/decision.png) We decided to automatically parse an icon into a knowledge type macro when the user saves the Jira issue comment/description!
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We used a regex to remove the icons' textual representation from nodes in the knowledge views!
	- ![Issue](../../src/main/resources/images/issue.png) Which libraries should we use for automatic text classification?
		- ![Decision](../../src/main/resources/images/decision.png) Remove weka and meka dependencies because it is not needed anymore, we do text classification with smile (Statistical Machine Intelligence and Learning Engine)!
	- ![Issue](../../src/main/resources/images/issue.png) How should we indicate that an automatic classified part of text needs manual approval?
		- ![Decision](../../src/main/resources/images/decision.png) I renamed the isTagged column to isValidated.
	- ![Issue](../../src/main/resources/images/issue.png) Should we integrate parts of Jira issue description and comments into one database and model class or separate them?
		- ![Decision](../../src/main/resources/images/decision.png) Only use one model class, database table, and persistence manager for both decision knowledge elements in Jira issue description and comments!
			- ![Issue](../../src/main/resources/images/issue.png) How can we distinguish between decision knowledge elements in the description of a Jira issue from those in comments in the database table?
			- ![Pro](../../src/main/resources/images/argument_pro.png) No redundant code
	- ![Issue](../../src/main/resources/images/issue.png) How can we avoid that the parenthesis of the knowledge type macro is escaped in text mode?
		- ![Decision](../../src/main/resources/images/decision.png) Add a line break in front of the parenthesis!
	- ![Issue](../../src/main/resources/images/issue.png) How do we determine whether a part of text needs to be classified as relevant or irrelevant wrt. decision knowledge?
		- ![Decision](../../src/main/resources/images/decision.png) We use the attribute "isRelevant" to determine hether a part of text needs to be classified as relevant or irrelevant wrt. decision knowledge!
		- ![Decision](../../src/main/resources/images/decision.png) rejected: The Sentence class (later PartOfJiraIssueText) gets an attribute "isPlainText". This allows to distinguish between plain text and other, like quotes, code, noformat (log files,..), etc. If isPlainText is set to false, these sentence instances will not be passed to the classification functions or labeled on the UI.
	- ![Issue](../../src/main/resources/images/issue.png) How can the system distinguish between a plain text comment and a special formatted comment?
		- ![Decision](../../src/main/resources/images/decision.png) We set special formatted text (e.g. code snippets) to isRelevant=false so that we know that they do not need to be classified!
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We add a boolean attribute isPlainText to distinguish between a plain text and a special formatted comment!
			- ![Con](../../src/main/resources/images/argument_con.png) Not really necessary because we have the "isRelevant" attribute.
	- ![Issue](../../src/main/resources/images/issue.png) What behavior do we expect when changing the type of decision knowledge in Jira issue text?
		- ![Decision](../../src/main/resources/images/decision.png) Changing the type of manually classified sentences in Jira issue text (comments and description) should change their tags as well!
	- ![Issue](../../src/main/resources/images/issue.png) How can we build a view for the extracted decision knowledge from Jira issue text (DecXtract)?
		- ![Decision](../../src/main/resources/images/decision.png) We removed the separate tab panel (for DecXtract) and integrated all ConDec views of a Jira issue into one Jira issue module!
			- ![Pro](../../src/main/resources/images/argument_pro.png) All ConDec views are in one place.
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We used to add a new tab panel in the Jira issue view for DecXtract! This tab panel was located next to the comment panel!
	- ![Issue](../../src/main/resources/images/issue.png) Should we do default options/opt-out nudging for the automatic text classifier?
		- ![Alternative](../../src/main/resources/images/alternative.png) The automatic text classifier should be trained and enabled by default when the plugin is activated for a project!
			- ![Con](../../src/main/resources/images/argument_con.png) The text classifier requires a lot of computational resources, to use it without a real need is not sustainable.
		- ![Decision](../../src/main/resources/images/decision.png) We decided not to do this now, as the classifier's results are not reliable enough to make it useful!
	- ![Issue](../../src/main/resources/images/issue.png) Where should the information about automated text classifications be displayed?
		- ![Alternative](../../src/main/resources/images/alternative.png) Display information about text classifications in a tab combined with information about other smart features!
			- ![Con](../../src/main/resources/images/argument_con.png) This would be hard to use.
		- ![Decision](../../src/main/resources/images/decision.png) Display information about the automated text classifications in a just-in-time prompt!
		- ![Decision](../../src/main/resources/images/decision.png) Display information about the automated text classifications in a new tab!
	- ![Issue](../../src/main/resources/images/issue.png) Should we add macro tags when automatically classifying issue comments?
		- ![Decision](../../src/main/resources/images/decision.png) Macro tags are added when automatically classifying issue comments!
			- ![Con](../../src/main/resources/images/argument_con.png) The TAGGED_MANUALLY cell is always set to true no matter whether the comment was classified automatically or manually.
		- ![Decision](../../src/main/resources/images/decision.png) Remove column TAGGED_MANUALLY from CONDEC_IN_COMMENT table!
			- ![Pro](../../src/main/resources/images/argument_pro.png) We already have the "is validated" attribute to store the information whether decision knowledge is manually approved or not.
	- ![Issue](../../src/main/resources/images/issue.png) Which information about the automated text classifications should be displayed?
		- ![Decision](../../src/main/resources/images/decision.png) Display only the elements that were classified and not yet validated!
		- ![Alternative](../../src/main/resources/images/alternative.png) Display all elements that were classified!
	- ![Issue](../../src/main/resources/images/issue.png) Which attributes should we store for a part of Jira issue text (which can be relevant wrt. decision knowledge or irrelevant)?
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We renamed START_SUBSTRING_COUNT to START_POSITION and END_SUBSTRING_COUNT to END_POSITION as attributes to store a part of Jira issue text!
		- ![Decision](../../src/main/resources/images/decision.png) rejected: I removed the TAGGED_MANUALLY column since it is currently always set to true because of the added macro tags!
		- ![Decision](../../src/main/resources/images/decision.png) rejected: I deleted the ARGUMENT and the USER_ID column (as former attributes to store a part of Jira issue text)!
		- ![Decision](../../src/main/resources/images/decision.png) I made isTaggedFineGrained a derived method that returns true if the knowledge type is not OTHER.
		- ![Decision](../../src/main/resources/images/decision.png) rejected: I renamed the column KNOWLEDGE_TYPE_AS_STRING into TYPE in order to use the same terminology as in the CONDEC_ELEMENT table (as attributes to store a part of Jira issue text)!
		- ![Decision](../../src/main/resources/images/decision.png) We store the following attributes for a part of Jira issue text: id, comment id (0 means documented in description), start and end position in the text, project key of the Jira project, whether it is validated (manually approved), knowledge type, and status!
	- ![Issue](../../src/main/resources/images/issue.png) How can new comments automatically be linked to other decision knowledge?
		- ![Decision](../../src/main/resources/images/decision.png) Link new comments automatically to decision knowledge by rules!
	- ![Issue](../../src/main/resources/images/issue.png) What should be presented in the Jira issue tab panel for decision knowledge? ([CONDEC-401](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-401))
		- ![Decision](../../src/main/resources/images/decision.png) We decided to remove the tab panel and add a tab in the ConDec Jira issue module instead!
			- ![Pro](../../src/main/resources/images/argument_pro.png) All the ConDec views are in one place.
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We used to show representations with tags, e.g. [issue] ... [/issue]!
			- ![Con](../../src/main/resources/images/argument_con.png) Used to be like this, but is now inconsistent with comments tab panel
		- ![Decision](../../src/main/resources/images/decision.png) We use icons and colors to indicate decision knowledge!
			- ![Pro](../../src/main/resources/images/argument_pro.png) Consistent with comments tab.
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We used to show a tree viewer in tab panel for decision knowledge!
		- ![Alternative](../../src/main/resources/images/alternative.png) discarded: We could show the same text as in the comment tab panel!
			- ![Pro](../../src/main/resources/images/argument_pro.png) Would enable to add smart functionality such as drag&drop, i.e. would be better extensible than normal comment tab panel.
			- ![Con](../../src/main/resources/images/argument_con.png) This would be redundant to the comment tab panel
	- ![Issue](../../src/main/resources/images/issue.png) How to deal with irrelevant parts of Jira issue text in knowledge graph? ([CONDEC-362](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-362))
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We used to include only relevant parts of text (=decision knowledge elements) into knowledge graph! ([CONDEC-365](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-365))
			- ![Pro](../../src/main/resources/images/argument_pro.png) Aligned with domain model that graph is a graph of decision knowledge (=only relevant sentences)
		- ![Alternative](../../src/main/resources/images/alternative.png) Include irrelevant parts of text into knowledge graph! ([CONDEC-363](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-363))
			- ![Con](../../src/main/resources/images/argument_con.png) Needs special treatment to only get relevant decision knowledge.
		- ![Alternative](../../src/main/resources/images/alternative.png) Add parameter to Graph constructor to specify whether irrelevant sentences should be included! ([CONDEC-367](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-367))
		- ![Decision](../../src/main/resources/images/decision.png) We decided to add irrelevant parts of text/sentences to the knowledge graph and filter them out per default!
	- ![Issue](../../src/main/resources/images/issue.png) How can we classify text as decision knowledge (or as irrelevant wrt. decision knowledge)?
		- ![Decision](../../src/main/resources/images/decision.png) We use and adopt the classification methods and training data by R. Alkadhi to classify text as decision knowledge (or as irrelevant wrt. decision knowledge)!
	- ![Issue](../../src/main/resources/images/issue.png) Which classifier algorithm to choose for automatic text classification?
		- ![Alternative](../../src/main/resources/images/alternative.png) We could use the binary relevance or Labor Powerset as base Classifier.
			- ![Con](../../src/main/resources/images/argument_con.png) model file is ~30 MB / 20 MB larger than with Labor Powerset
			- ![Pro](../../src/main/resources/images/argument_pro.png) only one class per sentence possible
			- ![Pro](../../src/main/resources/images/argument_pro.png) Better precision for new sentences
		- ![Decision](../../src/main/resources/images/decision.png) rejected: We used the Classifier Chains as meka base classifier!
		- ![Decision](../../src/main/resources/images/decision.png) We replaced WEKA/MEKA with the SMILE library and now enable the rationale manager to choose a classifier algorithm!

- ST: Set up rationale management process ([CONDEC-179](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-179))
	- ![Issue](../../src/main/resources/images/issue.png) When should the automatic text classifier be enabled?
		- ![Alternative](../../src/main/resources/images/alternative.png) The automatic text classifier should be enabled by default, as an opt-out nudge!
			- ![Con](../../src/main/resources/images/argument_con.png) This would use a lot of computing power, as all text in the project is classified when the classifier is activated
			- ![Pro](../../src/main/resources/images/argument_pro.png) The developers would not need to classify the text manually (even if the rationale manager did not actively activate the text classifier).
			- ![Con](../../src/main/resources/images/argument_con.png) The quality of the text classifications is not high enough for this to be useful.
		- ![Decision](../../src/main/resources/images/decision.png) The automatic text classifier should not be enabled by default!
			- ![Pro](../../src/main/resources/images/argument_pro.png) We could avoid unnecessarily using computing resources by leaving the automatic text classifier as an opt-in feature, would be better for sustainable development.
	- ![Issue](../../src/main/resources/images/issue.png) How can we enable the rationale manager to decide whether automatic classification should be enabled?
		- ![Decision](../../src/main/resources/images/decision.png) We added a toggle in the project settings to allow the rationale manager to turn automatic classification on and off!
	- ![Issue](../../src/main/resources/images/issue.png) How can the rationale manager decide s/he wants to use the extraction of decision knowledge elements from Jira issue description and comments (=DecXtract) or not?
		- ![Decision](../../src/main/resources/images/decision.png) rejected: There used to be a toggle on the project settings page to enable/disabled the extraction of decision knowledge elements from Jira issue description and comments (DecXtract)!
		- ![Decision](../../src/main/resources/images/decision.png) Make the extraction of decision knowledge elements from Jira issue description and comments (DecXtract) a default feature of ConDec! It cannot be disabled by the user!
	- ![Issue](../../src/main/resources/images/issue.png) Should we do default options/opt-out nudging for the automatic text classifier?
		- ![Alternative](../../src/main/resources/images/alternative.png) The automatic text classifier should be trained and enabled by default when the plugin is activated for a project!
			- ![Con](../../src/main/resources/images/argument_con.png) The text classifier requires a lot of computational resources, to use it without a real need is not sustainable.
		- ![Decision](../../src/main/resources/images/decision.png) We decided not to do this now, as the classifier's results are not reliable enough to make it useful!
	- ![Issue](../../src/main/resources/images/issue.png) Should only user-validated data be exported?
		- ![Decision](../../src/main/resources/images/decision.png) We enabled the rationale manager to export user-validated/manual approved parts of text (decision knowledge elements and irrelevant parts)!
			- ![Con](../../src/main/resources/images/argument_con.png) The main problem is that users dont seem to validate non-relevant text wich leads to a very imbalanced dataset.

- ST: Evaluate the performance of algorithms and "smart features" ([CONDEC-681](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-681))
	- ![Issue](../../src/main/resources/images/issue.png) How can the ground truth data for the text classifier be balanced so that it is not biased towards one (or more) classes?
		- ![Decision](../../src/main/resources/images/decision.png) Balance ground truth data for the binary and fine-grained classifiers using random undersampling, split lists of knowledge elements for k-fold cross-validation in such a way that the knowledge type is equally distributed!
		- ![Alternative](../../src/main/resources/images/alternative.png) Use synthetic minority oversampling technique to balance the ground truth data!
	- ![Issue](../../src/main/resources/images/issue.png) How can a researcher/data scientist evaluate the performance of the automatic text classification?
		- ![Decision](../../src/main/resources/images/decision.png) Add two evaluation sections in classifier settings: one for cross-project validation and one for k-fold cross-validation!
	- ![Issue](../../src/main/resources/images/issue.png) How can we make the evaluation results of the automatic text classification accessible?
		- ![Decision](../../src/main/resources/images/decision.png) Store last evaluation results of the automatic text classification for a project and show it in the configuration/settings view!
		- ![Decision](../../src/main/resources/images/decision.png) Enable to store evaluation result in file system as a text file (in JSON format)!

## Publication
Kleebaum, A., Paech, B., Johanssen, J. O., & Bruegge, B. (2021). 
Continuous Rationale Identification in Issue Tracking and Version Control Systems. 
In REFSQ-2021 Workshops, OpenRE, Posters and Tools Track, and Doctoral Symposium (p. 9). 
Essen/Virtual: CEUR-WS.org. https://doi.org/10.11588/heidok.00029966