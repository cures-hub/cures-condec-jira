# Knowledge Management

To maintain a consistent knowledge graph, 
developers need to ensure that the knowledge elements and links are correctly documented. 
ConDec supports manual linking between all knowledge elements in the knowledge graph (i.e. software artifacts). 
Besides, ConDec offers techniques to **automatically link knowledge elements** within the knowledge graph, 
e.g., decisions to decision problems and [code to requirements via commits](knowledge-in-git-presentation.md). 
The [link recommendations](link-recommendation.md) feature recommends new links to the developers.
Developers can change and delete knowledge elements as well as links between them. 

## Wrong Link Marking
Trace links between Jira issues and code can be wrong (see example 
in [Extraction and Presentation of Knowledge in Git](knowledge-in-git-presentation.md)).
**Wrong links hinder the exploitation** of the knowledge graph e.g. during [change impact analysis](change-impact-analysis.md) 
or [transitive link creation](knowledge-visualization.md).
ConDec enables developers to **manually mark links as wrong** as follows:
If a developer deletes a link that involves a code file, the type of the link is set to *wrong*.
Per default, wrong links are filtered-out, i.e. not shown in the [views on the knowledge graph](knowledge-visualization.md).

## Design Details
ConDec uses both Jira issue links and custom links, e.g., to link a requirement and the decision knowledge elements in its comments. 
The custom links are stored via object-relational mapping.