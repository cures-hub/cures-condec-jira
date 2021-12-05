# Decision Knowledge Documentation

ConDec aims to support developers to capture decision knowledge within their current development context.
In particular, ConDec enables the decision knowledge documentation in the issue tracking system (ITS) and version control system (VCS).
Developers document decision knowledge when they 
1) capture it, i.e., write it down, 
2) annotate it, and 
3) link it to other knowledge elements in the knowledge graph.

ConDec supports four documentation locations.
These documentation locations enable decisions to be traced to requirements in the ITS and code in the VCS 
and can be used interchangeably.
We assume that there are trace links between ITS tickets and code and that ITS tickets are linked.
With these assumptions, it is only a minor difference whether a decision is documented within the ITS or VCS. 
In either location, the decision can be accessed from the requirement.

## Documentation in Jira
ConDec enables developers to document decision knowledge in the ITS Jira in two different ways:
1) as entire ITS tickets, similar to e.g. requirements and development tasks (work items) in the ITS, and
2) in the description and comments of existing ITS tickets, e.g., in requirements or development tasks.

## Documentation in Git
ConDec enables developers to document decision knowledge in the VCS git in two different ways:
1) documentation in commit messages, and
2) documentation in code comments.