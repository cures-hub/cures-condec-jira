# Knowledge Dashboard

ConDec supports decision knowledge sharing and collaborative decision making using a rationale backlog and in meeting agendas.

The dashboard shows metrics regarding the 
- intra-rationale completeness (e.g., *are there arguments for the decisions?*), 
- the rationale coverage of requirements and code (e.g., *how many decisions are documented for a requirement?*), 
- the rationale-to-artifact traceability (e.g., *is it possible to navigate to the requirements affected by a decision?*), and 
- the feature branch quality regarding the rationale documentation.

## Intra-Rationale Completeness

## Rationale Coverage

## General Metrics

## Metrics about the Decision Knowledge in Git

## Navigation

## Design Details
The following class diagram gives an overview of relevant backend classes for the dashboard.

![Overview class diagram](../diagrams/class_diagram_dashboard.png)

*Overview class diagram for the dashboard*

You find the explanation for the class diagramm in the Javadoc in the code:

- [Java code for the dashboard](../../src/main/java/de/uhd/ifi/se/decision/management/jira/view/dashboard)
- [Java REST API for dashboard](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/DashboardRest.java)

The UI code for the dashboard can be found here:

- [Velocity templates for the dashboard](../../src/main/resources/templates/dashboard)
- [JavaScript code for the dashboard](../../src/main/resources/js/dashboard)