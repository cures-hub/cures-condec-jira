# Knowledge Export

ConDec enables to **export decision knowledge and related knowledge elements**, such as requirements, code, and work items.
Three export formats are supported: Word, JSON, and Markdown.
The user can filter the knowledge to be exported using the **same filter criteria 
as in the [knowledge graph views](knowledge-visualization.md)**, e.g. by knowledge type, status, [decision groups](decision-grouping.md), and many other criteria.

The user can export knowledge either via the **context menu in a [knowledge graph view](knowledge-visualization.md)** or
via the **More menu in the Jira issue view**.

![Context menu item to export the knowledge subgraph](../screenshots/export_context_menu.png)

*Context menu item to export the knowledge subgraph*

![Jira issue *More* menu item to export the knowledge subgraph](../screenshots/export_more_menu.png)

*Jira issue *More* menu item to export the knowledge subgraph*

The user customizes the export e.g. by choosing the export format in the **export dialog**.

![Dialog to export the knowledge subgraph](../screenshots/export_dialog.png)

*Dialog to export the knowledge subgraph offering the same filter criteria as in the views on the knowledge graph*

Meeting managers can use the JSON export to create a 
**[meeting agenda using the ConDec Confluence plug-in](https://github.com/cures-hub/cures-condec-confluence)**.

## Design Details
The Java code for the knowledge export feature can be found here:

- [Java REST API for export of list of knowledge elements used for Word and JSON export](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/KnowledgeRest.java)
- [Java REST API for markdown export](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/ViewRest.java)

The UI code for the knowledge export feature can be found here:

- [Velocity template for export dialog](../../src/main/resources/templates/dialogs/exportDialog.vm)
- [JavaScript code for knowledge export](../../src/main/resources/js/condec.export.js)

## Important Decisions
The knowledge elements listed below were exported via the knowledge export feature for the system function *SF: Export knowledge from Jira*, which specifies the export feature.
In particular, the following filter criteria were used for the export:

1) Selected element: system function *SF: Export knowledge from Jira* (CONDEC-484), which is a requirement for ConDec
2) Link distance: 5 from the selected element
3) [Decision group](decision-grouping.md): export
4) [Transitive links](knowledge-visualization.md) should be established. 
For example, the code file *MarkdownCreator.java* is only indirectly linked to the system function via a work item. 
The work item is not part of the *export* decision group, but the code file *MarkdownCreator.java* is part of this decision group.
The code file is included in the filtered knowledge graph because of transitive linking.
The code file contains decision knowledge documented in its code comments.
5) All other filter criteria were the default filter criteria used in ConDec.
For example, this means that the **exported decision knowledge is taken from different [documentation locations](documentation.md): entire Jira issues, 
Jira issue description and comments, commit messages, and code comments**.

- SF: Export knowledge from Jira ([CONDEC-484](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-484))
	- ![Issue](../../src/main/resources/images/issue.png) Which format do we support for exporting the knowledge documentation?
		- ![Decision](../../src/main/resources/images/decision.png) Use Word to export the decision knowledge documentation!
			- ![Pro](../../src/main/resources/images/argument_pro.png) Word files can be edited.
		- ![Alternative](../../src/main/resources/images/alternative.png) PDF
			- ![Con](../../src/main/resources/images/argument_con.png) PDF files cannot be edited.
		- ![Decision](../../src/main/resources/images/decision.png) Use Markdown format to enable the export of the knowledge subgraph!
			- ![Pro](../../src/main/resources/images/argument_pro.png) Markdown text can be imported into github release notes and .md files for feature documentation.
			- ![Pro](../../src/main/resources/images/argument_pro.png) Is also used for release notes creation with explicit decision knowledge, thus, the code can be reused for general export.
		- ![Alternative](../../src/main/resources/images/alternative.png) CSV
	- ![Issue](../../src/main/resources/images/issue.png) Which word files should be supported?
		- ![Decision](../../src/main/resources/images/decision.png) Word Docs should be supported, as it was well documented!
	- ![Issue](../../src/main/resources/images/issue.png) How do we deal with irrelevant sentences/parts of text when exporting knowledge elements from Jira?
		- ![Decision](../../src/main/resources/images/decision.png) The user is offered the same filter settings as in the knowledge graph views during export! Thus, the user can decide whether irrelevant text wrt. decision knowledge can be exported!
			- ![Pro](../../src/main/resources/images/argument_pro.png) Flexible solution because of same filtering possibilities in all views
		- ![Decision](../../src/main/resources/images/decision.png) rejected: Irrelevant sentences are not included in the exported list of decision knowledge elements!
	- ![Issue](../../src/main/resources/images/issue.png) How should disconnected sub-graphs be handled in the getElements REST API method?
		- ![Decision](../../src/main/resources/images/decision.png) rejected: The knowledge-elements REST API method used to return a list of lists of elements belonging to a connected graph!
			- ![Con](../../src/main/resources/images/argument_con.png) This is hard to understand/low abstraction level.
		- ![Decision](../../src/main/resources/images/decision.png) The knowledge-elements REST API method returns only one list of all elements matching the query!
	- ![Issue](../../src/main/resources/images/issue.png) How to export and present decision knowledge from Jira into Confluence? ([CONDEC-271](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-271))
		- ![Decision](../../src/main/resources/images/decision.png) Create a Confluence plugin with a new macro to import decision knowledge from Jira into Confluence! ([CONDEC-298](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-298))
		- ![Decision](../../src/main/resources/images/decision.png) We add a possibility to export decision knowledge to the context menu! ([CONDEC-480](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-480))
	- ![Issue](../../src/main/resources/images/issue.png) Which machine readable format should be implemented?
		- ![Decision](../../src/main/resources/images/decision.png) JSON should be used as machine readable format to export the knowledge documentation! ([CONDEC-487](https://jira-se.ifi.uni-heidelberg.de/browse/CONDEC-487))
			- ![Pro](../../src/main/resources/images/argument_pro.png) web standard, easily parsable, many libraries that can handle the JSON format
		- ![Alternative](../../src/main/resources/images/alternative.png) CSV comma separated values
			- ![Con](../../src/main/resources/images/argument_con.png) hard to parse, not very flexible own parser has to be build, no data hierarchies
			- ![Pro](../../src/main/resources/images/argument_pro.png) Also readable by humans with excel, lightweight
		- ![Alternative](../../src/main/resources/images/alternative.png) XML Extensive markup language
			- ![Con](../../src/main/resources/images/argument_con.png) Created for document markup, not for data exchange
			- ![Pro](../../src/main/resources/images/argument_pro.png) Full Data hierarchies, old standard for Documents
	- ![Code](../../src/main/resources/images/code.png) MarkdownCreator.java
		- ![Issue](../../src/main/resources/images/issue.png) How can we include icon images into the markdown used for release notes and general knowledge export?
			- ![Decision](../../src/main/resources/images/decision.png) We use the icon URL of github to include icon images into the markdown used for release notes and general knowledge export!
				- ![Pro](../../src/main/resources/images/argument_pro.png) The icons can be seen also by non Jira users. Thus, the markdown text could be excluded in external systems such as release page on github.
			- ![Alternative](../../src/main/resources/images/alternative.png) We could use the icon URL on the Jira server.
				- ![Con](../../src/main/resources/images/argument_con.png) The icons could not be seen by non Jira users.
		- ![Issue](../../src/main/resources/images/issue.png) How can we get the depth of an element in the markdown tree?
			- ![Decision](../../src/main/resources/images/decision.png) We use the BreadthFirstIterator::getDepth method to get the depth of an element!
				- ![Con](../../src/main/resources/images/argument_con.png) We build the markdown with a DepthFirstIterator which does not offer a method getDepth. We currently traverse the graph twice: first, with a breadth first and second, with a depth first iterator, which is not very efficient.
			- ![Alternative](../../src/main/resources/images/alternative.png) We could use a shortest path algorithm (e.g. Dijkstra) to determine the link distance.
				- ![Con](../../src/main/resources/images/argument_con.png) Might also not be very efficient.
	- ![Issue](../../src/main/resources/images/issue.png) How to sort the exported knowledge elements?
		- ![Decision](../../src/main/resources/images/decision.png) Sort elements by key when exporting them to Word and JSON!
			- ![Pro](../../src/main/resources/images/argument_pro.png) Keeps the order in Jira issue text
			- ![Con](../../src/main/resources/images/argument_con.png) Can be different to for example the order in the indented outline.
		- ![Alternative](../../src/main/resources/images/alternative.png) We could change the REST API to return an ordered list created with e.g. a depth first iterator.
			- ![Con](../../src/main/resources/images/argument_con.png) Would take more computation effort