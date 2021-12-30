# Decision Grouping

The ConDec Jira plugin enables to group decisions and related knowledge elements.
Developers can group decisions according to predefined levels (high, medium, and realization).
They can also define custom groups (e.g. UI, process, design, requirements).
Levels and custom groups can be assigned using the context menu on a specific knowledge element in a knowledge graph view.

![Dialog to assign a level and custom groups to a decision](../screenshots/decision_grouping_assign.png)

*Dialog to assign a level and custom groups to a decision*

Developers and other stakeholders can filter for decision levels and groups, for example, if they only want to see decisions for a software component (here git).
![View elements of a specific decision group (here git)](../screenshots/decision_grouping_filter.png)

*View elements of a specific decision group (here git)*

## Design Details
The following class diagram gives an overview of relevant backend classes for this feature.

![Overview class diagram](../diagrams/class_diagram_decision_grouping.png)

*Overview class diagram for the decision grouping feature*

The Java code for decision grouping can be found here:

- [Java code for decision group persistence](../../src/main/java/de/uhd/ifi/se/decision/management/jira/persistence/DecisionGroupPersistenceManager.java)
- [Java REST API for decision grouping](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/DecisionGroupingRest.java)

The UI code for decision grouping can be found here:

- [Velocity template for decision group view](../../src/main/resources/templates/tabs/decisionGroups.vm)
- [JavaScript code for decision grouping](../../src/main/resources/js/grouping)