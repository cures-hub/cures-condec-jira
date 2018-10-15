function fillIssueModule() {
    console.log("view.issue.module fillIssueModule");
    updateView();
}

function updateView() {
    console.log("view.issue.module updateView");
    var issueKey = getIssueKey();
    var search = getURLsSearch();
    buildTreant(issueKey, true, search);
}

function setAsRootElement(id) {	
	getDecisionKnowledgeElement(id, function(decisionKnowledgeElement) {
		var baseUrl = AJS.params.baseURL;
		var key = decisionKnowledgeElement.key;
		window.open(baseUrl + "/browse/" + key, '_self');
	});	
}

var contextMenuActionsTreant = {
		"asRoot" : contextMenuSetAsRootAction,
		"create" : contextMenuCreateAction,
		"edit" : contextMenuEditAction,
		"link" : contextMenuLinkAction,
		"deleteLink" : contextMenuDeleteLinkAction,
		"delete" : contextMenuDeleteAction
	};

function downloadMyJsonAsTable(){
  console.log("downloadME");
    //get jql from url
    var userInputJql = window.location.search;
    var baseLink = window.location.origin + "/jira/browse/";
    console.log("projectId", getProjectKey());
    var elementsWithLinkArray = [];
    getElementsByQuery(userInputJql, function (response) {
        console.log("byQuery", response);
        if (response) {
            response.map(function (el) {
                el["link"] = baseLink + el["key"];
                elementsWithLinkArray.push(el);
            });
            download("jsonAsTable", JSON.stringify(elementsWithLinkArray));
        }
    });
}


function downloadJsonAsTree() {
    //get jql from url
    var userInputJql = window.location.search;
    var baseLink = window.location.origin + "/jira/browse/";
    console.log("projectId", getProjectKey());

    getElementsByQuery(userInputJql, function (response) {
        console.log("byQuery", response);
        let elementsWithLinkArray = [];
        if (response) {
            let myPromise = new Promise(function (resolve, reject) {
                response.map(function (topNode,i,arr) {
                    //make new request foreach

                    getTreant(topNode["key"], 4, function (myNode) {
                        console.log("myNode", myNode);

                        // var parentObject = handleParentObject(myNode.nodeStructure);

                        // if (myNode.nodeStructure.children.length > 0) {
                            // myNode.nodeStructure.children.map(function (child, i, arr) {
                            //     console.log("pushingTOCHildren", child);
                                // var tree = handleChildrenRecursive(child);
                                //
                                // console.log("recieve Tree OBject child", tree)
                                // //push children to parentObject
                                // console.log("parentBefore", parentObject)
                                //
                                // parentObject.children.push(tree);
                                // console.log("parentafter", parentObject)

                            // });
                        // }
                        elementsWithLinkArray.push(myNode);
                        if (arr.length - 1 === i) {
                            // last one
                            resolve();
                        }
                    });

                });

            });
            console.log("complete Child Tree", elementsWithLinkArray);
            myPromise.then(function () {
                download("jsonAsTree", JSON.stringify(elementsWithLinkArray));
            })
        }


    })
}

/*
*Returns ChilElement, which could also contain children
*
 */
// function handleChildrenRecursive(treeElement, existingParent) {
//     console.log("childRecieve", treeElement);
//     console.log("childRecievearray", existingParent);
//
//     //handle current element
//     var parentObject = handleParentObject(treeElement);
//     var children = treeElement.children;
//
//
//     if (children && children.length > 0) {
//         //should return array
//        var aMultipleChildren= handleMultipleChildren(children);
//         parentObject.children=(aMultipleChildren);
//         return parentObject;
//     } else {
//         if (existingParent) {
//             // existingParent["children"].push(parentObject);
//             console.log("returning Child", existingParent);
//             return existingParent;
//         } else {
//             return parentObject;
//         }
//     }
// }
// function handleMultipleChildren(aMultipleChildren,existingArray){
//     var aMultipleChildrenDone = [];
//     // if(existingArray){
//     //     aMultipleChildrenDone=existingArray;
//     // }
//     aMultipleChildren.map(function (child, index) {
//         var recursiveCall = handleChildrenRecursive(child,aMultipleChildrenDone);
//         aMultipleChildrenDone.push(recursiveCall);
//     });
//      return aMultipleChildrenDone;
// }

function handleParentObject(oParent) {
    parentObject = {};
    parentObject["subType"] = oParent.HTMLclass || "";
    parentObject["id"] = oParent.HTMLid || "";
    parentObject["link"] = oParent.link.href || "";
    parentObject["description"] = oParent.link.title || "";
    parentObject["key"] = oParent.text.desc || "";
    parentObject["type"] = oParent.text.name || "";
    parentObject["title"] = oParent.text.title || "";
    parentObject["children"] = [];
    return parentObject;
}

function download(filename, text) {
    console.log("filename", filename);

    const element = document.createElement('a');
    element.setAttribute('href', 'data:text/plain;charset=utf-8,' + encodeURIComponent(text));
    element.setAttribute('download', filename);

    element.style.display = 'none';
    document.body.appendChild(element);

    element.click();

    document.body.removeChild(element);


}
