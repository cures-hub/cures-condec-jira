# Knowledge Export

ConDec enables to export decision knowledge and related knowledge elements, such as requirements, code, and work items.

## Design Details

The Java code for knowledge export can be found here:

- [Java REST API for export of list of knowledge elements](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/KnowledgeRest.java)
- [Java REST API for markdown export](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/ViewRest.java)

The UI code for knowledge export can be found here:

- [Velocity template for export dialog](../../src/main/resources/templates/dialogs/exportDialog.vm)
- [JavaScript code for knowledge export](../../src/main/resources/js/condec.export.js)