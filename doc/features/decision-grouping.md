# Decision Grouping

The ConDec Jira plugin enables to **group decisions and related knowledge elements**.
Developers can group decisions according to predefined **levels (high, medium, and realization)**.
They can also define **custom groups (e.g. UI, process, design, requirements, architecture, testing, ...)**.

Levels and custom groups can be assigned using the context menu on a specific knowledge element in a knowledge graph view.

![Context menu available in all views on the knowledge graph](../screenshots/decision_grouping_context_menu.png)

*Context menu available in all [views on the knowledge graph](knowledge-visualization.md)*

![Dialog to assign a level and custom groups to a decision](../screenshots/decision_grouping_assign.png)

*Dialog to assign a level and custom groups to a decision*

Developers (and other stakeholders) can **filter for decision levels and groups**, for example, if they only want to see decisions for the UI or process decisions.

![View elements of a specific decision group (here git)](../screenshots/decision_grouping_filter.png)

*View elements of a specific decision group (here UI)*

## Decision Grouping as a Definition of Done Criterion
The rationale manager can enforce that developers assign decision problems and solution options to decision levels and groups by 
making the **assignment of decision levels and groups a [definition of done (DoD)](quality-checking.md) criterion**.
If a decision problem or solution option is not assigned to a decision level and decision, the quality check fails and 
the respective element is colored in red in the knowledge graph views as means of [nudging](nudging.md).

![Decision problem that fullfills the DoD since a decision level (Medium) and decision group (UI) is assigned to it.](../screenshots/decision_grouping_dod_fulfilled.png)

*Decision problem that fullfills the DoD since a decision level (Medium) and decision group (UI) is assigned to it.*

![Decision problem that violates the DoD since no decision level and no custom decision group is assigned to it.](../screenshots/decision_grouping_dod_violated.png)

*Decision problem that violates the DoD since no decision level and no custom decision group is assigned to it.*

![Decision problem that violates the DoD since only a decision level but no custom decision group is assigned to it.](../screenshots/decision_grouping_dod_violated_level_only.png)

*Decision problem that violates the DoD since only a decision level but no custom decision group is assigned to it.*

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

## Important Decisions