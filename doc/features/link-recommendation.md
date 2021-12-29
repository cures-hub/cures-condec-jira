# Link Recommendation and Duplicate Recognition

The ConDec Jira plug-in offers a feature that recommends new links between knowledge elements and 
that tries to identify duplicated knowledge elements.

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

You find more explanation for the class diagramm in the Javadoc in the code:

- [Java code for link recommendation](../../src/main/java/de/uhd/ifi/se/decision/management/jira/recommendation/linkrecommendation)
- [Java REST API for link recommendation](../../src/main/java/de/uhd/ifi/se/decision/management/jira/rest/LinkRecommendationRest.java)

The UI code for link recommendation can be found here:

- [Velocity templates for configuration and evaluation](../../src/main/resources/templates/settings/linkrecommendation)
- [Velocity templates for usage during development](../../src/main/resources/templates/tabs/recommendation)
- [JavaScript code for link recommendation](../../src/main/resources/js/recommendation)