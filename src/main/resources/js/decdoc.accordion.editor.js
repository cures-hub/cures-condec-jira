function addOptionsToAllDecisionComponents(parentNode) {
    var types = ["Problem", "Issue", "Goal", "Solution", "Alternative", "Claim", "Context", "Assumption", "Constraint", "Implication", "Assessment", "Argument"];
    for (var counter = 0; counter < types.length; ++counter) {
        addOptionToDecisionComponent(types[counter], parentNode);
    }
}
function addOptionToDecisionComponent(type, parentNode) {
    if (type === "Solution") {
        if (document.getElementById(type).innerHTML === "") {
            document.getElementById(type).insertAdjacentHTML('beforeend', '<p>Do you want to add an additional ' + type + '? <input type="text" id="inputField' + type + '" placeholder="Name of ' + type + '"><input type="button" name="CreateAndLinkDecisionComponent' + type + '" id="CreateAndLinkDecisionComponent' + type + '" value="Add ' + type + '"/></p>');
            var createDecisionKnowledgeElementButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
            var inputField = document.getElementById("inputField" + type);
            createDecisionKnowledgeElementButton.addEventListener('click', function () {
                var tempString = inputField.value;
                inputField.value = "";
                var description = "TODO";
                createDecisionKnowledgeElement(tempString, description, type, function (newId) {
                    createLink(parentNode.id, newId, "contain", function () {
                    });
                    var tree = $('#evts').jstree(true);
                    var nodeId = tree.create_node('' + parentNode.id, data, 'last', tree.redraw(true), true);
                    tree.deselect_all();
                    tree.select_node(nodeId);
                });
            });
        }
    } else if (type === "Argument") {
        document.getElementById(type).insertAdjacentHTML('beforeend', '<p>Do you want to add an additional ' + type + '? <input type="radio" name="natureOfArgument" value="pro" checked="checked">Pro<input type="radio" name="natureOfArgument" value="contra">Contra<input type="radio" name="natureOfArgument" value="comment">Comment<input type="text" id="inputField' + type + '" placeholder="Name of ' + type + '"><input type="button" name="CreateAndLinkDecisionComponent' + type + '" id="CreateAndLinkDecisionComponent' + type + '" value="Add ' + type + '"/></p>');
        var createDecisionKnowledgeElementButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
        var inputField = document.getElementById("inputField" + type);
        createDecisionKnowledgeElementButton.addEventListener('click', function () {
            var tempString = inputField.value;
            var description = "TODO";
            inputField.value = "";
            var argumentCheckBoxGroup = document.getElementsByName("natureOfArgument");
            for (var i = 0; i < argumentCheckBoxGroup.length; i++) {
                if (argumentCheckBoxGroup[i].checked === true) {
                    var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
                    if (selectedNatureOfArgument === "pro") {
                        createDecisionKnowledgeElement(tempString, description, type, function (newId) {
                            createLink(parentNode.id, newId, "support", function () {
                            });
                            var tree = $('#evts').jstree(true);
                            var nodeId = tree.create_node('' + parentNode.id, data, 'last', tree.redraw(true), true);
                            tree.deselect_all();
                            tree.select_node(nodeId);
                        });
                    } else if (selectedNatureOfArgument === "contra") {
                        createDecisionKnowledgeElement(tempString, description, type, function (newId) {
                            createLink(parentNode.id, newId, "attack", function () {
                            });
                            var tree = $('#evts').jstree(true);
                            var nodeId = tree.create_node('' + parentNode.id, data, 'last', tree.redraw(true), true);
                            tree.deselect_all();
                            tree.select_node(nodeId);
                        });
                    } else if (selectedNatureOfArgument === "comment") {
                        createDecisionKnowledgeElement(tempString, description, type, function (newId) {
                            createLink(parentNode.id, newId, "comment", function () {
                            });
                            var tree = $('#evts').jstree(true);
                            var nodeId = tree.create_node('' + parentNode.id, data, 'last', tree.redraw(true), true);
                            tree.deselect_all();
                            tree.select_node(nodeId);
                        });
                    }
                }
            }
        });
    } else {
        document.getElementById(type).insertAdjacentHTML('beforeend', '<p>Do you want to add an additional ' + type + '?<input type="text" id="inputField' + type + '" placeholder="Name of ' + type + '"><input type="button" name="CreateAndLinkDecisionComponent' + type + '" id="CreateAndLinkDecisionComponent' + type + '" value="Add ' + type + '"/></p>');
        var createDecisionKnowledgeElementButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
        createDecisionKnowledgeElementButton.addEventListener('click', function () {
            var inputField = document.getElementById("inputField" + type);
            var tempString = inputField.value;
            var description = "TODO";
            inputField.value = "";
            createDecisionKnowledgeElement(tempString, description, type, function (newId) {
                console.log(parentNode);
                console.log(newId);
                createLink(parentNode.id, newId, "contain", function () {
                });
                var tree = $('#evts').jstree(true);
                var nodeId = tree.create_node('' + parentNode.id, data, 'last', tree.redraw(true), true);
                tree.deselect_all();
                tree.select_node(nodeId);
            });
        });
    }
}

function fillAccordion(data, projectKey, node) {
    var detailsElement = document.getElementById("Details");
    detailsElement.insertAdjacentHTML('beforeend', '<p>' + node.type + ' / ' + node.summary + ' <input type="button" name="updateIssue" id="updateIssue" value="Update"/></p>' +
        '<p><textarea id="IssueDescription" style="width:99%; height:auto;border: 1px solid rgba(204,204,204,1); ">' +
        node.description + '</textarea></p>'
    );
    detailsElement.style.display = "block";
    var updateButton = document.getElementById("updateIssue");
    updateButton.addEventListener('click', function () {
        editDecisionComponent(node.id, node.summary, document.getElementById("IssueDescription").value, function () {
            buildTreeViewer(projectKey, node.id);
        });
    });

    getJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?projectKey=" + projectKey + '&issueId=' + node.id, function (err, data) {
        if (err !== null) {
            displayGetJsonError(err);
        } else {
            var insertString = '<select name="linkExistingIssueSearchField">';
            for (var index = 0; index < data.length; index++){
                insertString+= '<option value="' + data[index].id + '">' + data[index].text + '</option>';
            }
            insertString+= '</select><input type="button" name="linkExistingIssueButton" id="linkExistingIssueButton" value="Create Link"/>';
            document.getElementById("Details").insertAdjacentHTML('beforeend', insertString);
            var linkButton = document.getElementById("linkExistingIssueButton");
            linkButton.addEventListener('click', function () {

                createLink(node.id, $('select[name="linkExistingIssueSearchField"] option:selected').val() /*$("#linkExistingIssueSearchField")[0].value*/, "contain", function () {
                    buildTreeViewer(projectKey, node.key);
                });
                singleSelect.value = '';
                window.location.reload();
            });
        }
    });
    if (data.node.children.length > 0) {
        for (var counter = 0; counter < data.node.children.length; ++counter) {
            var child = $('#evts').jstree(true).get_node(data.node.children[counter]);
            var type = child.data.type;
            var array = ["Problem", "Issue", "Goal", "Solution", "Alternative", "Claim", "Context", "Assumption", "Constraint", "Implication", "Assessment", "Argument"];
            for(var i = 0; i< array.length; i++){
                if(array[i].toLocaleLowerCase()===type.toLocaleLowerCase()) {
                    document.getElementById(array[i]).insertAdjacentHTML('beforeend', '<div class="issuelinkbox"><p>' + child.data.type +
                        ' / ' + child.data.summary + '</p>' + '<p>Description: ' + child.data.description + '</p></div>'
                    );
                    document.getElementById(array[i]).style.display = "block";
                }
            }
        }
        addOptionsToAllDecisionComponents(data.node.data);
    } else {
        addOptionsToAllDecisionComponents(data.node.data);
    }
}

/* Displays Error Message in Accordion */
function displayGetJsonError(errorCode) {
    document.getElementById("Details").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Problem").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Issue").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Goal").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Solution").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Alternative").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Claim").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Context").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Assumption").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Constraint").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Implication").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Assessment").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
    document.getElementById("Argument").innerHTML = "Error occured while retrieving data. Error-Code: " + errorCode;
}
/* Deletes all content from Accordion */
function setBack(text) {
    var details = document.getElementById("Details");
    clearInner(details);
    details.innerHTML = text;
    var problem = document.getElementById("Problem");
    clearInner(problem);
    problem.innerHTML = text;
    document.getElementById("Problem").style.display = "none";
    document.getElementById("Issue").innerHTML = text;
    document.getElementById("Issue").style.display = "none";
    document.getElementById("Goal").innerHTML = text;
    document.getElementById("Goal").style.display = "none";
    document.getElementById("Solution").innerHTML = text;
    document.getElementById("Solution").style.display = "none";
    document.getElementById("Alternative").innerHTML = text;
    document.getElementById("Alternative").style.display = "none";
    document.getElementById("Claim").innerHTML = text;
    document.getElementById("Claim").style.display = "none";
    document.getElementById("Context").innerHTML = text;
    document.getElementById("Context").style.display = "none";
    document.getElementById("Assumption").innerHTML = text;
    document.getElementById("Assumption").style.display = "none";
    document.getElementById("Constraint").innerHTML = text;
    document.getElementById("Constraint").style.display = "none";
    document.getElementById("Implication").innerHTML = text;
    document.getElementById("Implication").style.display = "none";
    document.getElementById("Assessment").innerHTML = text;
    document.getElementById("Assessment").style.display = "none";
    document.getElementById("Argument").innerHTML = text;
    document.getElementById("Argument").style.display = "none";
    document.getElementById("treant-container").innerHTML = text;
}