# cures-decdoc-jira

[![Build Status](https://travis-ci.org/cures-hub/cures-decdoc-jira.svg?branch=master)](https://travis-ci.org/cures-hub/cures-decdoc-jira)

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

##Download of precompiled .jar-File
The precompiled .jar-File for the latest release can be found here: (https://github.com/cures-hub/cures-decdoc-jira/releases/latest)

## Installation in JIRA
The .jar file is installed by uploading it to your JIRA application:
- As a system administrator, navigate to JIRA's administration console and click the "Add-ons" tab.
- Select "Manage add-ons" on the sidebar.
- Click on "Upload add-on" and select the previously compiled .jar file.
- Click "Upload" for upload, installation and activation.