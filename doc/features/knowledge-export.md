# Knowledge Export

ConDec enables to export decision knowledge and related knowledge elements, such as requirements, code, and work items.

## Design Details

The Java code for knowledge export can be found here:

- [Java REST API for export of list of knowledge elements used for Word and JSON export](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/KnowledgeRest.java)
- [Java REST API for markdown export](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/ViewRest.java)

The UI code for knowledge export can be found here:

- [Velocity template for export dialog](../../src/main/resources/templates/dialogs/exportDialog.vm)
- [JavaScript code for knowledge export](../../src/main/resources/js/condec.export.js)

The following knowledge is exported via the knowledge export feature for the system function (SF) Export knowledge from Jira:

- SF: Export knowledge from Jira ([CONDEC-484](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-484))
	- ![Issue](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/issue.png) How can we get the depth of an element in the markdown tree?
	- ![Issue](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/issue.png) Which word files should be supported?
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) Word Docs should be supported, as it was well documented!
	- ![Issue](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/issue.png) How should classified decision knowledge be edited? ([CONDEC-223](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-223))
	- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) We add a possibility to export decision knowledge to the context menu! ([CONDEC-480](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-480))
	- ![Issue](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/issue.png) How to export and present decision knowledge from Jira into Confluence? ([CONDEC-271](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-271))
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) Create a Confluence plugin with a new macro to import decision knowledge from Jira into Confluence! ([CONDEC-298](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-298))
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) We try to use the same context menu in every view! ([CONDEC-220](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-220))
	- ![Issue](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/issue.png) Which format do we support for exporting the knowledge documentation?
		- ![Alternative](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/alternative.png) CSV
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) Use Word to export the decision knowledge documentation!
		- ![Alternative](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/alternative.png) PDF
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) Use Markdown format to enable the export of the knowledge subgraph!
	- ![Issue](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/issue.png) How can we include icon images into the release notes?
	- ![Issue](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/issue.png) How should disconnected sub-graphs be handled in the getElements REST API method?
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) rejected: The knowledge-elements REST API method used to return a list of lists of elements belonging to a connected graph!
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) The knowledge-elements REST API method returns only one list of all elements matching the query!
	- ![Issue](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/issue.png) How do we deal with irrelevant sentences/parts of text when exporting knowledge elements from Jira?
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) The user is offered the same filter settings as in the knowledge graph views during export! Thus, the user can decide whether irrelevant text wrt. decision knowledge can be exported!
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) rejected: Irrelevant sentences are not included in the exported list of decision knowledge elements!
	- ![Issue](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/issue.png) Which machine readable format should be implemented?
		- ![Decision](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/decision.png) JSON should be used as machine readable format to export the knowledge documentation! ([CONDEC-487](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-487))
			- ![Pro](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/argument_pro.png) web standard, easily parsable, many libraries that can handle the JSON format
		- ![Alternative](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/alternative.png) CSV
comma separated values
			- ![Con](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/argument_con.png) hard to parse, not very flexible own parser has to be build, no data hierarchies
			- ![Pro](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/argument_pro.png) Also readable by humans with excel
lightwight
		- ![Alternative](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/alternative.png) XML
Extensive markup language
			- ![Con](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/argument_con.png) Created for document markup, not for data exchange
			- ![Pro](https://raw.githubusercontent.com/cures-hub/cures-condec-jira/master/src/main/resources/images/argument_pro.png) Full Data hierarchies, old standard for Documents
