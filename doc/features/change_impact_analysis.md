# Change Impact Analysis

Developers can exploit the knowledge documentation during changes, to estimate change impacts.
The [node-link diagram, tree, list, and matrix views](knowledge-visualization.md) can be used for change impact analysis (CIA).
ConDec colors the knowledge elements in these views according to the likelihood that they are affected by a change in the selected element.
The [knowledge graph visualization](knowledge-visualization.md) and change impact analysis should support the developers to make new decisions consistent with the requirements and former decisions.

![Node-link diagram with change impact highlighting](../screenshots/change_impact_analysis_user_story_ise2020_graph.png)

*Node-link diagram with change impact highlighting*

During change impact analysis, each knowledge element (i.e. node/vertex) in the knowledge graph is given an impact value. 
High impact values indicate that the element is highly affected by the change and needs to be changed as well. The impact value of an element (elementImpact) is calculated using the following equation:

```
elementImpact = parentImpact * (1 - decayValue) * linkTypeWeight * ruleBasedValue
```

where `parentImpact` is the element impact of the ancestor node in the knowledge graph, 
`decayValue` is the decay per iteration step, `linkTypeWeight` is a link type specific decay value between 0 and 1 of the traversed edge between the parent/ancestor element and the current element, 
and `ruleBasedValue` is calculated based on rules. For example, rules are:

1. Stop at elements with the same type as the selected element (e.g. at requirements with same type)
2. Outward links only
3. Boost when element is textual similar to the selected element

The element is included in the impact graph if `elementImpact >= threshold`.