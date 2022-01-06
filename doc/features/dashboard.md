# Knowledge Dashboard

ConDec presents **metrics calculated on the knowledge graph data structure** in a knowledge dashboard.
The dashboard comprises four dashboard items that show the following metrics:

1. the **rationale coverage** of requirements and code, e.g., *how many decisions are documented for a requirement?*, 
2. **intra-rationale completeness**, e.g., *are there arguments for the decisions?*, 
3. **general metrics**, e.g., *how many rationale elements are documented per origin/documentation location?*, and
4. **metrics on the knowledge in git**, e.g., 
*do the rationale elements documented in code comments and commit messages of a branch fulfill the [definition of done](quality-checking.md)?*

The four dashboard items are shown in the following sections.

## Rationale Coverage
This dashboard item shows the coverage of requirements, code, and other software artifacts (=knowledge elements) 
with a specific decision knowledge type, e.g. decsion problem or decision. 
For example, this dashboard item shows how many decisions are reachable from a requirement or 
how many decisions are reachable from a code file **within a certain link distance in the knowledge graph**.
The filter settings used to filter the knowledge graph.
For example, the status in the can be used to specify whether the decision problems should be *resolved* or *unresolved*. 
Per default, both resolved and unresolved decision problems (issues) are included.
Similarly, the filter settings can be used to specify that the coverage of decisions should only include decisions 
with status *decided* and no decisions with status *rejected* or *challenged*.

![Rationale coverage dashboard item](../screenshots/dashboard_rationale_coverage.png)

*Rationale coverage dashboard item*

## Intra-Rationale Completeness

![Rationale coverage dashboard item](../screenshots/dashboard_intra_rationale_completeness.png)

*Intra-rationale completeness dashboard item*

## General Metrics

Offers the following **metrics on the knowledge graph data structure** after it was filtered with the given filter settings: 
- Number of comments per Jira issue
- Number of commits per Jira issue
- Number of code files and requirements in the project
- Number of rationale elements per origin/documentation location
- Number of comments with and without decision knowledge
- Number of decision knowledge elements per decision knowledge type
- Number of knowledge elements fulfilling and violating the definition of done (DoD)

![General metrics dashboard item](../screenshots/dashboard_general_metrics.png)

*General metrics dashboard item*

## Metrics about the Decision Knowledge in Git
the feature branch quality regarding the rationale documentation.

![Dashboard item showing metrics about the knowledge in git](../screenshots/dashboard_git.png)

*Dashboard item showing metrics about the knowledge in git*

## Filtering

![Filter settings for rationale coverage dashboard item](../screenshots/dashboard_filter_settings.png)

*Filter settings for rationale coverage dashboard item*

## Navigation

![Navigation dialog with elements violating the DoD](../screenshots/dashboard_navigation.png)

*Navigation dialog with elements violating the DoD*

## Design Details
The following class diagram gives an overview of relevant backend classes for the dashboard.

![Overview class diagram](../diagrams/class_diagram_dashboard.png)

*Overview class diagram for the dashboard*

The Java code for metric calculation and the dashboard creation can be found here:

- [Java code for the metric calculation](../../src/main/java/de/uhd/ifi/se/decision/management/jira/metric)
- [Java code for the dashboard creation](../../src/main/java/de/uhd/ifi/se/decision/management/jira/view/dashboard)
- [Java REST API for dashboard](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/DashboardRest.java)

The UI code for the dashboard can be found here:

- [Velocity templates for the dashboard](../../src/main/resources/templates/dashboard)
- [JavaScript code for the dashboard](../../src/main/resources/js/dashboard)