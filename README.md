# cures-decdoc-jira

[![Build Status](https://travis-ci.org/cures-hub/cures-decdoc-jira.svg?branch=master)](https://travis-ci.org/cures-hub/cures-decdoc-jira)
[![Codecoverage](https://codecov.io/gh/cures-hub/cures-decdoc-jira/branch/master/graph/badge.svg)](https://codecov.io/gh/cures-hub/cures-decdoc-jira/branch/master)

The CURES DecDoc JIRA plugin enables the user to capture and explore decision knowledge in [JIRA](https://de.atlassian.com/software/jira).
Decision knowledge is composed of decisions and their decision components (e.g., arguments, alternatives, problem, context, ...).
Decisions and decision components can be linked to JIRA issues such as requirements, work items, bugs, and features.

## Prerequisites
The following prerequisites are necessary to compile the plugin from source code:
- Java 8 JDK
- [Atlassian SDK](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project)

## Compilation via terminal
The source code is compiled via terminal. 
Navigate into the cures-decdoc-jira folder and run the following command:
```
atlas-mvn package
```
The .jar file is created.

## Download of precompiled .jar-File
The precompiled .jar-File for the latest release can be found here: (https://github.com/cures-hub/cures-decdoc-jira/releases/latest)

## Installation in JIRA
The .jar file is installed by uploading it to your JIRA application:
- As a system administrator, navigate to JIRA's administration console and click the "Add-ons" tab.
- Select "Manage add-ons" on the sidebar.
- Click on "Upload add-on" and select the previously compiled .jar file.
- Click "Upload" for upload, installation and activation.

![Add-ons](https://github.com/cures-hub/cures-decdoc-jira/raw/master/doc/installed_plugin.png)

## Settings
![DecDoc settings](https://github.com/cures-hub/cures-decdoc-jira/raw/master/doc/config_plugin.png)

## Implementation Details

### User Interface

In order to understand which decisions were made during the project, JIRA DecDoc provides a *TreeViewer* that lists all documented decisions. 
The TreeViewer was implemented using the [jsTree jQuery plugin](https://www.jstree.com).
The decisions can be selected and their decision components can be viewed and selected as well. 

![JIRA DecDoc plug-in](https://github.com/cures-hub/cures-decdoc-jira/raw/master/doc/example_treant_radargrammetry.png)
*TreeViewer (left) and tree view of a single decision (right)*

Currently, the JIRA DecDoc plugin provides two views: an accordion editor and a tree view (different to the TreeViewer).

The accordion editor enables the user to refine decisions incrementally and collaboratively.
The accordion editor was implemented using the jQuery [accordion widget](https://jqueryui.com/accordion).
The *Tree* view enables the developer to see the decision components of a single decision. 
The tree view was implemented using the [Treant.js library](http://fperucic.github.io/treant-js).

![Accordion editor](https://github.com/cures-hub/cures-decdoc-jira/raw/master/doc/example_editor_radargrammetry.png)
*Accordion editor*

### Persistence Strategies
The JIRA DecDoc plug-in supports two strategies to implement the DDM: the *issue strategy* and the *active object strategy*. 

![Persistence strategies](https://github.com/cures-hub/cures-decdoc-jira/raw/master/doc/decision_storage_strategies.png)
*Persistence strategies*

The issue strategy represents the concepts of the DDM as JIRA issues.
JIRA issue links are used to link DDM elements to each other and to JIRA issues of other types such as feature tasks.
The advantage of this strategy is that all features available for JIRA issues can be used to manage decision knowledge, e.g., searching for a decision in the list of issues.
The disadvantage is that the dedicated issue type scheme needs to be assigned to the JIRA project.
To overcome this disadvantage, the active object strategy uses distinct model classes for decision, decision components, and links.
This strategy uses object-relational mapping to communicate with JIRA's internal database.

### REST API
The plug-in provides four REST services.

![REST services](https://github.com/cures-hub/cures-decdoc-jira/raw/master/doc/rest_services.png)
*REST services provided by the JIRA DecDoc plug-in*




