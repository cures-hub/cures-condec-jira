# Quality Checking

The ConDec Jira plugin offers the possibility to configure a **Definition of Done (DoD)** for decision knowledge.
The definition of done is checked and the status of the quality can be viewed continuously in the Quality Check tab.

ConDec supports the fulfillment of the definition of done with the help of a [knowledge dashboard](dashboard.md) and automated quality checks integrated into the development workflow, e.g., when finishing a requirement. 
For example, the developer can only finish the log-in requirement if all the decision problems that are documented in a certain link distance in the knowledge graph are solved.

## Design Details
The following class diagram gives an overview of relevant backend classes for this feature.
The *DefinitionOfDone* stores the rules that are configured for the decision knowledge project’s rationale documentation to be considered complete. 
The *DefinitionOfDone* is configured separately for each *DecisionKnowledgeProject*, and is checked using the *DefinitionOfDoneChecker*. 
The *DefinitionOfDoneChecker* checks if the *DefinitionOfDone* is fulfilled for a *KnowledgeElement*. 
To do this, it uses the *KnowledgeType* of the element to fetch the corresponding inheritor of the abstract *KnowledgeElementCheck* class. 
An interface to the frontend is provided through the REST endpoints in the class *DefinitionOfDoneCheckingRest*.

![Overview class diagram](../diagrams/class_diagram_quality_checking.png)

*Overview class diagram for the quality checking feature*

You find more explanation for the class diagramm in the Javadoc in the code:

- [Java code for quality checking](../../src/main/java/de/uhd/ifi/se/decision/management/jira/quality)
- [Java REST API for quality checking](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/DefinitionOfDoneCheckingRest.java)

The UI code for quality checking can be found here:

- [Velocity templates for configuration](../../src/main/resources/templates/settings/definitionofdone)
- [Velocity templates for usage during development](../../src/main/resources/templates/tabs/qualityCheck.vm)
- [JavaScript code for decision guidance](../../src/main/resources/js/definitionofdone)