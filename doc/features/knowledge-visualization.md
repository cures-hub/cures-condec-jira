# Knowledge Visualization

Requirements engineers and developers use the integrated knowledge visualization 
to understand the requirements of a software system along with decisions related to 
their elicitation or implementation.
All the views use the same knowledge graph data structure underneath, which enables to easily add new views.

ConDec enables the developers to **exploit transitive links between knowledge elements**. 
For example, the developers developers can examine all decisions made in the context of a requirement. 
The decisions can be documented in work items, commit messages, and code files reachable from the requirement.

## Design Details
The following class diagrams give an overview of relevant backend classes for knowledge visualization and filtering.

![Overview of classes in view package](doc/diagrams/class_diagram_view_overview.png)
*Overview of classes in view package*

![Filtering classes and associations](doc/diagrams/class_diagram_filtering_detailed.png)
*Filtering classes and associations (UML class diagram)*

## Publication
Kleebaum, A., Paech, B., Johanssen, J. O., & Bruegge, B. (2021). Continuous Rationale Visualization. 
In 2021 Working Conference on Software Visualization (VISSOFT) (pp. 33-43). 
Luxembourg: IEEE. https://doi.org/10.1109/VISSOFT52517.2021.00013