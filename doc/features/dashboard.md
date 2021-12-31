# Knowledge Dashboard

ConDec supports decision knowledge sharing and collaborative decision making using a rationale backlog and in meeting agendas.

The dashboard shows metrics regarding the 
- intra-rationale completeness (e.g., *are there arguments for the decisions?*), 
- the rationale coverage of requirements and code (e.g., *how many decisions are documented for a requirement?*), 
- the rationale-to-artifact traceability (e.g., *is it possible to navigate to the requirements affected by a decision?*), and 
- the feature branch quality regarding the rationale documentation.

## Rationale Coverage

![Rationale coverage dashboard item](../screenshots/dashboard_rationale_coverage.png)

*Rationale coverage dashboard item*

## Intra-Rationale Completeness

![Rationale coverage dashboard item](../screenshots/dashboard_intra_rationale_completeness.png)

*Intra-rationale completeness dashboard item*

## General Metrics

![General metrics dashboard item](../screenshots/dashboard_general_metrics.png)

*General metrics dashboard item*

## Metrics about the Decision Knowledge in Git

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

The Java code for the dashboard can be found here:

- [Java code for the dashboard](../../src/main/java/de/uhd/ifi/se/decision/management/jira/view/dashboard)
- [Java REST API for dashboard](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/DashboardRest.java)

The UI code for the dashboard can be found here:

- [Velocity templates for the dashboard](../../src/main/resources/templates/dashboard)
- [JavaScript code for the dashboard](../../src/main/resources/js/dashboard)