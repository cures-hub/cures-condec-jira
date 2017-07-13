# cures-decdoc-jira
JIRA plugin to capture and explore decision knowledge

[![Build Status](https://travis-ci.org/cures-hub/cures-decdoc-jira.svg?branch=master)](https://travis-ci.org/cures-hub/cures-decdoc-jira)

## prerequisites:

-Java 8 JDK

-[Atlassian SDK](https://developer.atlassian.com/docs/getting-started/set-up-the-atlassian-plugin-sdk-and-build-a-project)

## compilation in the commandline to create .jar:

navigate into the cures-decdoc-jira-folder and run the following command
```
atlas-mvn package
```

## installation in JIRA:

Open up "Administration" and proceed to the tab "Add-ons". On the sidebar, select on "Manage add-ons". 
Click on "Upload add-on" and select the jar the previously compiled jar. 
Click "Upload" for upload, installation and activation.