# cures-condec-jira

[![Build Status](https://travis-ci.org/cures-hub/cures-condec-jira.svg?branch=master)](https://travis-ci.org/cures-hub/cures-condec-jira)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/bcbb5354da724d718c5b63c0416ee572)](https://www.codacy.com/app/anja.kleebaum/cures-condec-jira?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=cures-hub/cures-condec-jira&amp;utm_campaign=Badge_Grade)
[![Codecoverage](https://codecov.io/gh/cures-hub/cures-condec-jira/branch/master/graph/badge.svg)](https://codecov.io/gh/cures-hub/cures-condec-jira/branch/master)
[![GitHub contributors](https://img.shields.io/github/contributors/cures-hub/cures-condec-jira.svg)](https://github.com/cures-hub/cures-condec-jira/graphs/contributors)

The ConDec Jira plug-in enables the user to capture and explore decision knowledge in [Jira](https://de.atlassian.com/software/jira).
Decision knowledge covers knowledge about decisions, the problems they address, solution proposals, their context, and justifications (rationale). The documented decision knowledge can be linked to Jira issues such as features, tasks to implement a feature, or bug reports.
The plug-in supports four documentation locations for decision knowledge: entire Jira issues with distinct types, comments and the description of existing Jira issues, commit messages, and code comments.

## Installation

### Prerequisites
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
- [Activate the "Decision Documentation and Exploration" plug-in.](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/installed_plugin.png)
- Activate the plug-in for the specific project in the [setting page.](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/config_plugin.png)

## User Interface

### Decision Knowledge Page
Jira ConDec provides a *TreeViewer* that lists all documented decision knowledge elements.
The user can choose the type of the top level decision knowledge element, e.g., to understand which decisions were made or which issues were addressed during the project.
The TreeViewer was implemented using the [jsTree jQuery plug-in](https://www.jstree.com).
Decision knowledge elements can be selected and the related elements can be viewed and selected as well.

![Jira ConDec plug-in](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/example_radargrammetry.png)
*TreeViewer (left) and tree view of a single decision (right)*

The *Tree* view enables the user to explore decision knowledge related to the selected decision knowledge element.
The tree view was implemented using the [Treant.js library](http://fperucic.github.io/treant-js).

The user can [filter the decision knowledge](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/example_radargrammetry_filter.png) and manage it using drag and drop and a [context menu](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/example_radargrammetry_context_menu.png).

### Jira Issue Module
Jira ConDec provides a [Jira issue module that enables the user to explore decision knowledge related to Jira issues such as feature tasks](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/example_radargrammetry_issue_module.png).

### Configuration
The [project setting page](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/config_plugin.png) enables the user to:
- Activate the plug-in for the specific project.
- Choose the persistence strategy (either *issue strategy* or *active object strategy*). If you choose the issue strategy, you need to associate the project with the *decision knowledge issue type scheme*.

## Implementation Details

### Model
The [model interfaces and classes](https://github.com/cures-hub/cures-condec-jira/tree/master/src/main/java/de/uhd/ifi/se/decision/management/jira/model) are used to represent decision knowledge in Jira.

![Model](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/model.png)
*Model interfaces and classes*

### Persistence Strategies
The Jira ConDec plug-in supports two strategies to [persist decision knowledge in Jira](https://github.com/cures-hub/cures-condec-jira/tree/master/src/main/java/de/uhd/ifi/se/decision/management/jira/persistence): the *issue strategy* and the *active object strategy*.

![Persistence strategies](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/decision_storage_strategies.png)
*Persistence strategies*

The issue strategy represents decision knowledge elements as Jira issues.
Jira issue links are used to link decision knowledge elements to each other and to Jira issues of other types such as feature tasks.
The advantage of this strategy is that all features available for Jira issues can be used to manage decision knowledge, e.g., searching for a decision in the list of issues.
The disadvantage is that the dedicated issue type scheme needs to be assigned to the Jira project.
To overcome this disadvantage, the active object strategy uses distinct model classes for decision knowledge elements and their links.
This strategy uses object-relational mapping to communicate with Jira's internal database.

### REST API
This plug-in provides a [representational state transfer (REST) application programming interface (API)](https://github.com/cures-hub/cures-condec-jira/tree/master/src/main/java/de/uhd/ifi/se/decision/management/jira/rest) to retrieve, insert, update, and delete decision knowledge in Jira.
These services can be accessed via the following link:

**Jira base URL**/rest/condec/latest/**knowledge|config|view**/**REST service**

The Jira ConDec plug-in uses the REST services in the [REST Java Script client](https://github.com/cures-hub/cures-condec-jira/blob/master/src/main/resources/js/condec.api.js) from the user interface.

### Webhook
Jira ConDec provides a webhook sending decision knowledge to a receiver system via a HTTP post request. To activate the webhook, do the following steps:

- As a project administrator, navigate to Jira project settings.
- Select "Webhook" on the side-bar under "ConDec Decision Knowledge".
- Insert a receiver URL and a shared secret (for Slack, there is no need to set a shared secret).
- Select the types of elements, which trigger the webhook, if they are created or edited.
- Activate the webhook with the switch on the top of the page
- You can click the test button to send a test post to the given URL.