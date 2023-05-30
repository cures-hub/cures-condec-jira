# cures-condec-jira

[![Continuous integration](https://github.com/cures-hub/cures-condec-jira/actions/workflows/maven.yml/badge.svg)](https://github.com/cures-hub/cures-condec-jira/actions/workflows/maven.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/e1b098d7a6b94aa199cfe0fd05dc263e)](https://www.codacy.com/gh/cures-hub/cures-condec-jira/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cures-hub/cures-condec-jira&amp;utm_campaign=Badge_Grade)
[![Codecoverage](https://codecov.io/gh/cures-hub/cures-condec-jira/branch/master/graph/badge.svg)](https://codecov.io/gh/cures-hub/cures-condec-jira/branch/master)
[![GitHub contributors](https://img.shields.io/github/contributors/cures-hub/cures-condec-jira.svg)](https://github.com/cures-hub/cures-condec-jira/graphs/contributors)

The ConDec Jira plug-in enables the user to capture and explore decision knowledge in [Jira](https://de.atlassian.com/software/jira).
Decision knowledge covers knowledge about decisions, the problems they address, solution proposals, their context, and justifications (rationale). 
The documented decision knowledge can be linked to Jira tickets such as features, tasks to implement a feature, or bug reports.
The plug-in supports four documentation locations for decision knowledge: entire Jira tickets with distinct types, comments and the description of Jira tickets, commit messages, and code comments.

## Installation

### Prerequisites
The plug-in works for Jira server and data center instances.
The following prerequisites are necessary to compile the plug-in from source code:
- Java 11 JDK
- [Atlassian SDK](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project)

### Compilation via Terminal
The source code is compiled via terminal.
Navigate into the cures-condec-jira folder and run the following command:
```
atlas-package
```
(Run `atlas-package -DskipTests=true` to skip unit tests to speed-up compilation.)
The .jar file is created.

Run the plug-in locally via:
```
atlas-run
```

### Download of Precompiled .jar-File
The precompiled .jar-File for the latest release can be found here: https://github.com/cures-hub/cures-condec-jira/releases/latest

### Installation in Jira
[The ConDec Jira plug-in is available via the Atlassian Marketplace.](
https://marketplace.atlassian.com/apps/1219690/decision-documentation-and-exploration)

Alternatively, the plug-in can be installed via uploading the .jar file to your Jira application:
- As a system administrator, navigate to Jira's administration console and click the "Add-ons" tab.
- Select "Manage add-ons" on the side-bar.
- Click on "Upload add-on" and select the previously compiled .jar file.
- Click "Upload" for upload and installation.
- [Activate the "Decision Documentation and Exploration" plug-in.](doc/screenshots/installed_plugin.png)
- Activate the plug-in for the specific project in the [setting page.](doc/screenshots/config_plugin.png)

## ConDec Views and Features
ConDec adds several views and features to the issue tracking system Jira.
The [user interface structure diagram](doc/diagrams/user_interface_structure.png) provides an overview of the views and features.

### Knowledge Overviews
The ConDec Jira plug-in adds knowledge overviews in a separate page.
The *indented outline* lists all documented knowledge elements.
The user can choose the type of the top level knowledge element, e.g., to understand which decisions were made or which issues (decision problems) were addressed in the project.
Decision knowledge elements can be selected and the related elements can be viewed and selected as well.

![ConDec Jira plug-in](doc/screenshots/example_radargrammetry.png)
*Knowledge overview page showing the indented outline (left) and a node-link tree diagram of a single decision (right)*

The *node-link tree diagram* enables the user to explore decision knowledge related to the selected decision knowledge element.

The user can [filter the decision knowledge](doc/screenshots/example_radargrammetry_filter.png) and manage it using drag and drop and a [context menu](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/screenshots/example_radargrammetry_context_menu.png).

### Jira Issue Detail View
The ConDec Jira plug-in provides a [Jira issue module that enables the user to explore decision knowledge related to a specific Jira ticket such as a requirement](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/screenshots/example_radargrammetry_issue_module.png).

### Configuration
The [project setting page](doc/screenshots/config_plugin.png) enables the user to:
- Activate the plug-in for the specific project.
- Activate whether decision knowledge can be stored in entire Jira tickets. If activated, the plug-in automatically adds decision knowledge types to the issue type scheme of the project.
- [Configure the rationale model.](doc/screenshots/config_rationale_model.png)
- Configure the features listed below.

### Features
ConDec offers the following features:
- [Decision knowledge documentation in various documentation locations](doc/features/documentation.md)
- [Knowledge management](doc/features/knowledge-management.md)
- [Extraction and presentation of knowledge in git](doc/features/knowledge-in-git-presentation.md)
- [Interactive knowledge visualization based on a knowledge graph containing requirements, decision knowledge, code files, and other software artifacts](doc/features/knowledge-visualization.md)
- [Change impact analysis](doc/features/change-impact-analysis.md)
- [Definition of done checking to support high quality of the knowledge documentation](doc/features/quality-checking.md)
- [Rationale backlog listing knowledge elements that violate the definition of done](doc/features/rationale-backlog.md)
- [Automatic text classification to identify decision knowledge in natural language text](doc/features/automatic-text-classification.md)
- [Recommendation of solution options from external knowledge sources](doc/features/decision-guidance.md)
- [Link recommendation and duplicate recognition](doc/features/link-recommendation.md)
- [Nudges for decision knowledge documentation and exploitation](doc/features/nudging.md)
- [Decision grouping](doc/features/decision-grouping.md)
- [Creation of release notes with explicit decision knowledge](doc/features/release-notes-creation.md)
- [Metrics presentation in a knowledge dashboard](doc/features/dashboard.md)
- [Knowledge export](doc/features/knowledge-export.md)
- [Webhook to inform a receiver system about changed knowledge](doc/features/webhook.md)

## Design and Implementation Details

### Overview and Model
The plug-in consists of a frontend and backend component.
[The backend is implemented in Java code organized into 15 packages.](src/main/java/de/uhd/ifi/se/decision/management/jira)
The following class diagram gives an overview of important classes (only ten packages are included).

![Overview class diagram](doc/diagrams/class_diagram_overview.png)
*Overview of important backend classes (UML class diagram)*

The [model classes](src/main/java/de/uhd/ifi/se/decision/management/jira/model) represent the data model of decision knowledge and other software artifacts in Jira. 
The class *KnowledgeGraph* contains *KnowledgeElement*s and *Link*s.
The class *KnowledgeElement* represents decision knowledge (e.g., decision problems, alternatives, decisions, pro and con arguments) and other software artifacts (e.g., requirements and code).
Each knowledge element has attributes to describe its location (*DocumentationLocation*), its knowledge status (*KnowledgeStatus*), and its type (*KnowledgeType*), whereby the possibilities for the knowledge status depend on the type of the knowledge element.
The *documentationLocation* attribute describes where an element is documented, for example, if it is documented as an entire Jira ticket, in the description or comments of a Jira ticket, or in code.
Each knowledge element also has an attribute origin. 
By default, the *documentationLocation* and *origin* of an element are the same, but for knowledge elements that originated in commit messages, the *documentationLocation* is the text in a comment of a Jira ticket, but the origin is a commit message.
This is due to the fact that ConDec parses the decision knowledge from commit messages and automatically posts them as comments on the related Jira tickets.

![Model](doc/diagrams/class_diagram_model_detailed.png)
*Model classes and associations (UML class diagram)*

The [classes in the rest package](src/main/java/de/uhd/ifi/se/decision/management/jira/rest) provide representational state transfer (REST) endpoints for communication between the frontend and backend. 
They provide methods that are called by the JavaScript code in the frontend to get the data from the backend for the respective feature (see next section). 

The [persistence classes](src/main/java/de/uhd/ifi/se/decision/management/jira/persistence) manage the storage of decision knowledge in relation to other software artifacts.
The *KnowledgePersistenceManager* is the central class responsible for knowledge storage.
The *JiraIssuePersistenceManager* manages the storage of decision knowledge elements as Jira tickets.
Jira issue links are used to link decision knowledge elements documented as entire tickets to each other and to Jira tickets of other types such as requirements.
The *JiraIssueTextPersistenceManager* manages the storage of decision knowledge elements in the description and comments of Jira tickets.
The *GenericLinkManager* manages linking of the decision knowledge elements in the description and comments and the *AutomaticLinkCreator* performs automatic linking.

![Overview of classes for knowledge persistence](doc/diagrams/class_diagram_persistence_overview.png)
*Overview of classes for knowledge persistence*

The [git classes](src/main/java/de/uhd/ifi/se/decision/management/jira/git) deal with the extraction and presentation of code changes and decision knowledge from [git](https://git-scm.com).

The [view classes](src/main/java/de/uhd/ifi/se/decision/management/jira/view) represent the views on the knowledge graph. 
The [filtering classes](src/main/java/de/uhd/ifi/se/decision/management/jira/filtering) provides ways to filter the knowledge graph.
The [config classes](src/main/java/de/uhd/ifi/se/decision/management/jira/config) store the configuration options and settings. 
The [classification classes](src/main/java/de/uhd/ifi/se/decision/management/jira/rest) deal with automatic text classification to extract decision knowledge from various knowledge sources.

The [quality classes](src/main/java/de/uhd/ifi/se/decision/management/jira/quality) check whether the knowledge documentation fulfills the definition of done.
The [metric classes](src/main/java/de/uhd/ifi/se/decision/management/jira/metric) calculate metrics on the knowledge graph data structure. 
The metrics are presented in the knowledge dashboard.

### REST API
This plug-in provides a [representational state transfer (REST) application programming interface (API)](src/main/java/de/uhd/ifi/se/decision/management/jira/rest), 
e.g., to retrieve, insert, update, and delete decision knowledge in Jira.
These services can be accessed via the following link:

**Jira base URL**/rest/condec/latest/**knowledge|config|view|dashboard|grouping|dodchecking|git|decision-guidance|linkrecommendation|nudging|classification|releasenotes|webhook**/**REST service**

The Jira ConDec plug-in uses the REST services in the [REST JavaScript client](src/main/resources/js/condec.api.js) from the user interface.

## Testing
This repository contains [unit tests](src/test) that are automatically executed when calling `atlas-package`.
The repository [*cures-condec-jira-system-tests*](https://github.com/cures-hub/cures-condec-jira-system-tests) contains automated system tests that require a running Jira instance with the ConDec Jira plug-in installed.
The static code quality is tested with various source code analyzers, such as PMD and ESLint.

## Logging and Monitoring
The backend (Java) code of the plug-in contains `LOGGER.info()` statements that can be used to monitor the plug-in usage, 
e.g. to evaluate which views the users prefer.
You need to configure the `log4j.properties` so that `LOGGER.info()` statements are logged.
More infos on usage data analysis can be found [here](doc/logging/).