# Link Recommendation and Duplicate Recognition

The ConDec Jira plug-in offers a feature that **recommends new links between knowledge elements** and 
that tries to **identify duplicated knowledge elements**.

This feature tries to identify related knowledge elements using the **context information** of knowledge elements.
The context information is calculated from the various **context information providers, i.e. link recommendation rules** listed below.

Every context information provider calculates a `ruleValue<sub>i</sub>`.
For example, the textual similarity context information provider calculates a higher rule value for knowledge elements that are textual similar.
Besides, every context information provider is assigned a `ruleWeight<sub>i</sub>` to determine its importance for recommendation creation.

For every knowledge element that might be related to the selected element, a <var>recommendationScore</var> is calculated as follows:

```
recommendationScore = (&sum;<sup>N</sup> ruleValue<sub>i</sub> * ruleWeight<sub>i</sub>) / maxAchievableScore
```

where `N` is the number of enabled context information providers 
and `maxAchievableScore` is the hypothetical best score to normalize the recommendation score between 0 and 1.

The `ruleWeight<sub>i</sub>` can also be negative to reverse the effect of the rule.
For instance, for the timely coupling context information provider (*recommend elements that are timely coupled to the source element*),
a negative rule weight means that elements that are not timely coupled are assigned a higher recommendation score.

The link recommendations are sorted by their `recommendationScore`.

A link to another knowledge element is only **recommended if the `recommendationScore >= threshold`** and 
if the link recommendation is under the **top-k recommendations**.

![Link recommendation view showing a potential duplicate](../screenshots/link_recommendation_duplicate_tooltip.png)

*Link recommendation view as part of the Jira issue view showing a potential duplicated alternative*

Developers and the rationale manager can inspect the potential duplicates in the [knowledge graph views](knowledge-visualization.md), 
e.g. in the [node-link diagram](../screenshots/link_recommendation_duplicates_node-link_diagram.png) or in the [indented outline](../screenshots/link_recommendation_duplicates_indented_outline.png).

![Node-link diagram showing the context of the duplicated alternative](../screenshots/link_recommendation_duplicates_node-link_diagram.png)

*Node-link diagram showing the context of a user story.
Directly and transitively linked decision problems (issues) and solution options (alternatives) are shown that match the "export" filter string. 
The alternatives are duplicated.*

## Design Details
The following class diagram gives an overview of relevant backend classes for this feature.
The class *LinkRecommendationRest* provides the interface to the frontend. 
It uses the *LinkRecommendationConfiguration* to get the configuration information for a project when a request comes. 
The interface *ContextInformationProvider* is implemented by five other classes: 
the class *ContextInformation* and four different *ContextInformationProvider*s that assess different types of context 
to find similarities between elements and ultimately suggest new links for elements. 
Each of these assigns a score based on the similarity or dissimilarity it has calculated. 
The *TextualSimilarityContextInformationProvider* assesses the textual similarity between two knowledge elements; 
the more textually similar the elements, the higher the score. 
Similarly, *TimeContextInformationProvider* assesses how close in time two elements were created. 
The score given here is higher if the elements were created in a shorter period of time and lower if they were created further apart. 
The *TracingContextInformationProvider* looks at how close in the knowledge graph two elements are to each other. 
If they have a lower link distance, the score is higher. 
Finally, the *UserContextInformationProvider* gives a positive score if the assignee or reporter of the issue 
where the first knowledge element is contained is the same as that of the second. 
If the users are not the same, the score is zero.
The class *ContextInformation* registers these four providers, and uses the information from them to create elements of the class *LinkRecommendation*.
*LinkRecommendation* elements each have a *RecommendationScore* and inherit from the class Link, 
which means they have source and target elements, as well as a *LinkType* (not shown in the diagram).

![Overview class diagram](../diagrams/class_diagram_link_recommendation.png)

*Overview class diagram for the link recommendation feature*

The Java code for link recommendation can be found here:

- [Java code for link recommendation](../../src/main/java/de/uhd/ifi/se/decision/management/jira/recommendation/linkrecommendation)
- [Java REST API for link recommendation](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/LinkRecommendationRest.java)

The UI code for link recommendation can be found here:

- [Velocity templates for configuration and evaluation](../../src/main/resources/templates/settings/linkrecommendation)
- [Velocity templates for usage during development](../../src/main/resources/templates/tabs/recommendation)
- [JavaScript code for link recommendation](../../src/main/resources/js/recommendation)