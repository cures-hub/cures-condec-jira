# Extraction and Presentation of Knowledge in Git

The ConDec Jira plug-in enables to explicitly document decision knowledge in commit messages and code comments.
It integrates code files as well as decision knowledge from commit messages and code comments into the knowledge graph.
Developers can filter the knowledge graph views, e.g. to only see the knowledge documented in git.
Trace link creation and maintenance between code files and Jira issues works as follows: 
1. Initial trace link creation during git clone. 
2. Manual link improvement and maintenance by developers. Developers can manually change links. 
3. Automatic trace link maintenance during git fetch based on recent changes.

ConDec offers dedicated views for the knowledge in git.
It indicates quality problems in the decision knowledge documentation and uses [nudging mechanisms](./nudging.md) 
to motivate the developers to improve the quality (e.g. coloring of menu items).

![View on knowledge from git for a specific Jira issue](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/screenshots/git_knowledge_work_item.png)

*View on knowledge from git for a specific Jira issue with highlighting of quality problems (to nudge the developers to improve the quality)*

## Activation and Configuration
The extraction and presentation of knowledge from git offers various configuration possibilities.

![Configuration view for the automatic text classifier](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/screenshots/config_git.png)

*Configuration view for the extraction and presentation of knowledge from git*

## Design Details
The following class diagram gives an overview of relevant backend classes for this feature.

![Overview class diagram](https://github.com/cures-hub/cures-condec-jira/raw/master/doc/diagrams/class_diagram_git.png)

*Overview class diagram for the extraction and presentation of knowledge in git*