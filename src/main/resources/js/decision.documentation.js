function getJSON(url, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open("GET", url, true);
    xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
    xhr.responseType = "json";
    xhr.onload = function () {
        var status = xhr.status;
        if (status === 200) {
            callback(null, xhr.response);
        } else {
            callback(status);
        }
    };
    xhr.send();
}
function postJSON(url, data, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
    xhr.setRequestHeader("Accept", "application/json");
    xhr.responseType = "json";
    xhr.onload = function () {
        var status = xhr.status;
        if (status === 200) {
            callback(null, xhr.response);
        } else {
            callback(status);
        }
    };
    xhr.send(JSON.stringify(data));
}
function putJSON(url, data, callback) {
    var xhr = new XMLHttpRequest();
    xhr.open("PUT", url, true);
    xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
    xhr.setRequestHeader("Accept", "application/json");
    xhr.responseType = "json";
    xhr.onload = function () {
        var status = xhr.status;
        if (status === 200) {
            callback(null, xhr.response);
        } else {
            callback(status);
        }
    };
    xhr.send(JSON.stringify(data));
}
function deleteJSON(url, data, callback){
    var xhr = new XMLHttpRequest();
    xhr.open("DELETE",url, true);
    xhr.setRequestHeader("Content-type", "application/json; charset=utf-8");
    xhr.setRequestHeader("Accept","application/json");
    xhr.responseType="json";
    xhr.onload = function () {
        var status = xhr.status;
        if(status==200){
            callback(null, xhr.response);
        } else {
            callback(status);
        }
    };
    xhr.send(JSON.stringify(data));
}

function createDecisionComponent(summary, type, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    if (summary !== "") {
        var jsondata = {
            "projectKey": projectKey,
            "name": summary,
            "type": type
        };
        postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?actionType=create", jsondata, function (err, data) {
            if (err !== null) {
                AJS.flag({
                    type: 'error',
                    close: 'auto',
                    title: 'Error',
                    body: type + ' has not been created. Error Code: ' + err
                });
            } else {
                callback(data);
            }
        });
    } else {
        //summary is empty
    }
}
function editDecisionComponent(issueId, summary, description, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    var jsondata = {
        "id": issueId,
        "name": summary,
        "projectKey": projectKey,
        "description": description
    };
    postJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?actionType=edit", jsondata, function (err, data) {
        if (err !== null) {
            AJS.flag({
                type: 'error',
                close: 'auto',
                title: 'Error',
                body: 'Decision Component has not been updated. Error Code: ' + err
            });
        } else {
            callback(data);
        }
    });
}
function deleteDecisionComponent(issueId, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    var jsondata = {
        "id": issueId,
        "projectKey": projectKey
    };
    deleteJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?actionType=delete", jsondata, function (err, data) {
        if (err !== null) {
            AJS.flag({
                type: 'error',
                close: 'auto',
                title: 'Error',
                body: 'Decision Component has not been deleted. Error Code: ' + err
            });
        } else {
            callback(data);
        }
    });
}
function createLink(parentId, childId, linkType, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    var jsondata = {
        "linkType": linkType,
        "ingoingId": childId,
        "outgoingId": parentId
    };
    putJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?projectKey=" + projectKey + "&actionType=create", jsondata, function (err, data) {
        if (err !== null) {
            AJS.flag({
                type: 'error',
                close: 'auto',
                title: 'Error',
                body: 'IssueLink could not be created'
            });
        } else {
            callback(data);
        }
    });
}
//TODO check if still needed (Not used at the moment)
function deleteLink(parentId, childId, linkType, callback) {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    var jsondata = {
        "linkType": linkType,
        "ingoingId": childId,
        "outgoingId": parentId
    };
    putJSON(AJS.contextPath() + "/rest/decisions/latest/decisions.json?projectKey=" + projectKey + "&actionType=delete", jsondata, function (err, data) {
        if (err !== null) {
            AJS.flag({
                type: 'error',
                close: 'auto',
                title: 'Error',
                body: 'IssueLink could not be deleted'
            });
        } else {
            callback(data);
        }
    });
}

function createContextMenuForTreeNodes(projectKey) {
    $(function () {
        $.contextMenu({
            selector: '.rationale, .context, .problem, .solution',
            //TODO icons
            items: {
                "add": {
                    name: "Add Decision Component",
                    callback: function (key, options) {
                        //set header
                        var closeX = document.getElementById('modal-close-x');
                        closeX.insertAdjacentHTML('beforeBegin', 'Add Decision Component');

                        //set content
                        var content = document.getElementById('modal-content');
                        content.insertAdjacentHTML('afterBegin',
                            '<p><label for="form-input-name" style="display:block;width:45%;float:left;">Name</label><input id="form-input-name" type="text" name="name" placeholder="Name of decisioncomponent" style="width:50%;"/></p>' +
                            '<p><label for="form-select-type" style="display:block;width:45%;float:left;">Componenttype</label><select name="form-select-type" style="width:50%;"/></p>' +
                            '<p><input id="form-input-submit" type="submit" value="Add Decision Component" style="float:right;"/></p>'
                        );

                        var type_select = $('select[name="form-select-type"]');
                        type_select.on('change', function () {
                            var type = type_select.val();
                            if (type === 'Argument') {
                                type_select.insertAdjacentHTML('afterEnd', '<p id="type-of-argument-para"><label for="type-of-argument" style="display:block;width:45%;float:left;">Type of Argument</label><input type="radio" name="type-of-argument" value="pro" checked="checked">Pro<input type="radio" name="type-of-argument" value="contra">Contra<input type="radio" name="type-of-argument" value="comment">Comment</p>');
                            } else {
                                var para = document.getElementById("type-of-argument-para");
                                if (para) {
                                    clearInner(para);
                                    para.parentNode.removeChild(para);
                                }
                            }
                        });

                        var data = [
                            {
                                id: "Alternative",
                                text: "Alternative"
                            },
                            {
                                id: "Argument",
                                text: "Argument"
                            },
                            {
                                id: "Assessment",
                                text: "Assessment"
                            },
                            {
                                id: "Assumption",
                                text: "Assumption"
                            },
                            {
                                id: "Claim",
                                text: "Claim"
                            },
                            {
                                id: "Constraint",
                                text: "Constraint"
                            },
                            {
                                id: "Context",
                                text: "Context"
                            },
                            {
                                id: "Goal",
                                text: "Goal"
                            },
                            {
                                id: "Implication",
                                text: "Implication"
                            },
                            {
                                id: "Issue",
                                text: "Issue"
                            },
                            {
                                id: "Problem",
                                text: "Problem"
                            },
                            {
                                id: "Solution",
                                text: "Solution"
                            }
                        ];
                        for (var index = 0; index < data.length; index++){
                            type_select[0].insertAdjacentHTML('beforeend','<option value="' + data[index].id + '">' + data[index].text + '</option>');
                        }

                        var submitButton = document.getElementById('form-input-submit');
                        submitButton.onclick = function () {
                            var name = document.getElementById('form-input-name').value;
                            var type = type_select.val();
                            if (type === "Argument") {
                                var argumentCheckBoxGroup = document.getElementsByName("type-of-argument");
                                for (var i = 0; i < argumentCheckBoxGroup.length; i++) {
                                    if (argumentCheckBoxGroup[i].checked === true) {
                                        var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
                                        if (selectedNatureOfArgument === "pro") {
                                            createDecisionComponent(name, type, function (data) {
                                                AJS.flag({
                                                    type: 'success',
                                                    close: 'auto',
                                                    title: 'Success',
                                                    body: type + ' has been created.'
                                                });
                                                var idOfNewObject = data.id;
                                                createLink(options.$trigger.context.id, idOfNewObject, "support", function () {
                                                    AJS.flag({
                                                        type: 'success',
                                                        close: 'auto',
                                                        title: 'Success',
                                                        body: 'IssueLink has been created.'
                                                    });
                                                    buildTreeViewer(projectKey, idOfNewObject);
                                                });
                                            });
                                        } else if (selectedNatureOfArgument === "contra") {
                                            createDecisionComponent(name, type, function (data) {
                                                AJS.flag({
                                                    type: 'success',
                                                    close: 'auto',
                                                    title: 'Success',
                                                    body: type + ' has been created.'
                                                });
                                                var idOfNewObject = data.id;
                                                createLink(options.$trigger.context.id, idOfNewObject, "attack", function () {
                                                    AJS.flag({
                                                        type: 'success',
                                                        close: 'auto',
                                                        title: 'Success',
                                                        body: 'IssueLink has been created.'
                                                    });
                                                    buildTreeViewer(projectKey, idOfNewObject);
                                                });
                                            });
                                        } else if (selectedNatureOfArgument === "comment") {
                                            createDecisionComponent(name, type, function (data) {
                                                AJS.flag({
                                                    type: 'success',
                                                    close: 'auto',
                                                    title: 'Success',
                                                    body: type + ' has been created.'
                                                });
                                                var idOfNewObject = data.id;
                                                createLink(options.$trigger.context.id, idOfNewObject, "comment", function () {
                                                    AJS.flag({
                                                        type: 'success',
                                                        close: 'auto',
                                                        title: 'Success',
                                                        body: 'IssueLink has been created.'
                                                    });
                                                    buildTreeViewer(projectKey, idOfNewObject);
                                                });
                                            });
                                        }
                                    }
                                }
                            } else {
                                createDecisionComponent(name, type, function (data) {
                                    AJS.flag({
                                        type: 'success',
                                        close: 'auto',
                                        title: 'Success',
                                        body: type + ' has been created.'
                                    });
                                    var idOfNewObject = data.id;
                                    createLink(options.$trigger.context.id, idOfNewObject, "contain", function () {
                                        AJS.flag({
                                            type: 'success',
                                            close: 'auto',
                                            title: 'Success',
                                            body: 'IssueLink has been created.'
                                        });
                                        buildTreeViewer(projectKey, idOfNewObject);
                                    });
                                });
                            }
                            closeModal();
                        };

                        var modal = document.getElementById('ContextMenuModal');
                        modal.style.display = "block";
                    }
                },
                "edit": {
                    name: "Edit Decision Component",
                    callback: function (key, options) {
                        //set header
                        var closeX = document.getElementById('modal-close-x');
                        closeX.insertAdjacentHTML('beforeBegin', 'Edit Decision Component');

                        var context = options.$trigger.context;
                        var content = document.getElementById('modal-content');
                        content.insertAdjacentHTML('afterBegin',
                            '<p><label for="form-input-name" style="display:block;width:45%;float:left;">Name</label><input id="form-input-name" type="text" name="name" value="" style="width:50%;" readonly/></p>' +
                            '<p><label for="form-input-description" style="display:block;width:45%;float:left;">Description</label><input id="form-input-description" type="text" name="type" placeholder="Type in description" style="width:50%;"/></p>' +
                            '<p><input id="form-input-submit" type="submit" value="Edit Decision Component" style="float:right;"/></p>'
                        );

                        var children = context.childNodes;
                        for (var index = 0; index < children.length; index++) {
                            if (children.hasOwnProperty(index)) {
                                if (index === 0) {
                                    document.getElementById('form-input-name').value = children[index].innerText;
                                } else if (index === 1) {
                                    //title, not needed right now
                                } else if (index === 2) {
                                    //description, not needed right now
                                } else {
                                    //not implemented, not needed right now
                                }
                            }
                        }

                        var submitButton = document.getElementById('form-input-submit');
                        submitButton.onclick = function () {
                            var name = document.getElementById('form-input-name').value;
                            var description = document.getElementById('form-input-description').value;
                            editDecisionComponent(context.id, name, description, function () {
                                AJS.flag({
                                    type: 'success',
                                    close: 'auto',
                                    title: 'Success',
                                    body: 'Decision component has been updated.'
                                });
                                var nodeId = $.jstree.reference('#evts').get_selected()[0];
                                buildTreeViewer(projectKey, nodeId);
                            });
                            closeModal();
                        };

                        // Get the modal window
                        var modal = document.getElementById('ContextMenuModal');
                        modal.style.display = "block";
                    }
                },
                "delete": {
                    name: "Delete Decision Component",
                    callback: function (key, options) {
                        //set header
                        var closeX = document.getElementById('modal-close-x');
                        closeX.insertAdjacentHTML('beforeBegin', 'Delete Decision Component');

                        var context = options.$trigger.context;
                        var content = document.getElementById('modal-content');
                        content.insertAdjacentHTML('afterBegin',
                            '<p><input id="abort-submit" type="submit" value="Abort Action" style="float:right;"/><input id="form-input-submit" type="submit" value="Delete Decision Component" style="float:right;"/></p>'
                        );

                        var abortButton = document.getElementById('abort-submit');
                        abortButton.onclick = function () {
                            closeModal();
                        };
                        var submitButton = document.getElementById('form-input-submit');
                        submitButton.onclick = function () {
                            deleteDecisionComponent(context.id, function () {
                                AJS.flag({
                                    type: 'success',
                                    close: 'auto',
                                    title: 'Success',
                                    body: 'Decisioncomponent has been deleted'
                                });
                                var nodeId = $.jstree.reference('#evts').get_selected()[0];
                                buildTreeViewer(projectKey, nodeId);
                            });
                            closeModal();
                        };

                        // Get the modal window
                        var modal = document.getElementById('ContextMenuModal');
                        modal.style.display = "block";
                    }
                }
            }
        });
    });
}
function buildTreant(projectKey, node) {
    var depthOfTree = document.getElementById("depthOfTreeInput").value;
    var treantUrl = AJS.contextPath() + "/rest/treantsrest/latest/treant.json?projectKey=" + projectKey + "&issueKey=" + node.key + "&depthOfTree=" + depthOfTree;
    getJSON(treantUrl, function (err, data) {
        if (err !== null) {
            document.getElementById("treant-container").innerHTML = "Fehler beim Abfragen der Daten. Error-Code: " + err;
        } else {
            document.getElementById("treant-container").innerHTML = "";
            new Treant(data);
            var modal = document.getElementById('ContextMenuModal');
            //add click-handler for elements in modal to close modal window
            var elementsWithCloseFunction = document.getElementsByClassName("modal-close");
            for (var counter = 0; counter < elementsWithCloseFunction.length; counter++) {
                elementsWithCloseFunction[counter].onclick = function () {
                    closeModal();
                }
            }
            //close modal window if user clicks anywhere outside of the modal
            window.onclick = function (event) {
                if (event.target === modal) {
                    closeModal();
                }
            };
            createContextMenuForTreeNodes(projectKey);
        }
    });
}

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
            var createDecisionComponentButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
            var inputField = document.getElementById("inputField" + type);
            createDecisionComponentButton.addEventListener('click', function () {
                var tempString = inputField.value;
                inputField.value = "";
                createDecisionComponent(tempString, type, function (data) {
                    AJS.flag({
                        type: 'success',
                        close: 'auto',
                        title: 'Success',
                        body: type + ' has been created.'
                    });
                    createLink(parentNode.id, data.id, "contain", function () {
                        AJS.flag({
                            type: 'success',
                            close: 'auto',
                            title: 'Success',
                            body: 'IssueLink has been created.'
                        });
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
        var createDecisionComponentButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
        var inputField = document.getElementById("inputField" + type);
        createDecisionComponentButton.addEventListener('click', function () {
            var tempString = inputField.value;
            inputField.value = "";
            var argumentCheckBoxGroup = document.getElementsByName("natureOfArgument");
            for (var i = 0; i < argumentCheckBoxGroup.length; i++) {
                if (argumentCheckBoxGroup[i].checked === true) {
                    var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
                    if (selectedNatureOfArgument === "pro") {
                        createDecisionComponent(tempString, type, function (data) {
                            AJS.flag({
                                type: 'success',
                                close: 'auto',
                                title: 'Success',
                                body: type + ' has been created.'
                            });
                            createLink(parentNode.id, data.id, "support", function () {
                                AJS.flag({
                                    type: 'success',
                                    close: 'auto',
                                    title: 'Success',
                                    body: 'IssueLink has been created.'
                                });
                            });
                            var tree = $('#evts').jstree(true);
                            var nodeId = tree.create_node('' + parentNode.id, data, 'last', tree.redraw(true), true);
                            tree.deselect_all();
                            tree.select_node(nodeId);
                        });
                    } else if (selectedNatureOfArgument === "contra") {
                        createDecisionComponent(tempString, type, function (data) {
                            AJS.flag({
                                type: 'success',
                                close: 'auto',
                                title: 'Success',
                                body: type + ' has been created.'
                            });
                            createLink(parentNode.id, data.id, "attack", function () {
                                AJS.flag({
                                    type: 'success',
                                    close: 'auto',
                                    title: 'Success',
                                    body: 'IssueLink has been created.'
                                });
                            });
                            var tree = $('#evts').jstree(true);
                            var nodeId = tree.create_node('' + parentNode.id, data, 'last', tree.redraw(true), true);
                            tree.deselect_all();
                            tree.select_node(nodeId);
                        });
                    } else if (selectedNatureOfArgument === "comment") {
                        createDecisionComponent(tempString, type, function (data) {
                            AJS.flag({
                                type: 'success',
                                close: 'auto',
                                title: 'Success',
                                body: type + ' has been created.'
                            });
                            createLink(parentNode.id, data.id, "comment", function () {
                                AJS.flag({
                                    type: 'success',
                                    close: 'auto',
                                    title: 'Success',
                                    body: 'IssueLink has been created.'
                                });
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
        var createDecisionComponentButton = document.getElementById("CreateAndLinkDecisionComponent" + type);
        createDecisionComponentButton.addEventListener('click', function () {
            var inputField = document.getElementById("inputField" + type);
            var tempString = inputField.value;
            inputField.value = "";
            createDecisionComponent(tempString, type, function (data) {
                AJS.flag({
                    type: 'success',
                    close: 'auto',
                    title: 'Success',
                    body: type + ' has been created.'
                });
                console.log(parentNode);
                console.log(data);
                createLink(parentNode.id, data.id, "contain", function () {
                    AJS.flag({
                        type: 'success',
                        close: 'auto',
                        title: 'Success',
                        body: 'IssueLink has been created.'
                    });
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
            AJS.flag({
                type: 'success',
                close: 'auto',
                title: 'Success',
                body: 'Decision component has been updated.'
            });
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
                    AJS.flag({
                        type: 'success',
                        close: 'auto',
                        title: 'Success',
                        body: 'Link has been created.'
                    });
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
            if (array.indexOf(type) !== -1) {
                document.getElementById(type).insertAdjacentHTML('beforeend', '<div class="issuelinkbox"><p>' + child.data.type +
                    ' / ' + child.data.summary + '</p>' + '<p>Description: ' + child.data.description + '</p></div>'
                );
                /*
                document.getElementById(isstypensertAdjacentHTML('beforeend', '<div class="issuelinkbox"><p><a href="' +
                    AJS.contextPath() + '/browse/' + child.data.key + '">' + child.data.key +
                    ' / ' + child.data.summary + '</a></p>' + '<p>Description: ' + child.data.description + '</p></div>'
                );
                */
                document.getElementById(child.data.type).style.display = "block";
            }
        }
        addOptionsToAllDecisionComponents(data.node.data);
    } else {
        addOptionsToAllDecisionComponents(data.node.data);
    }
}
function buildTreeViewer(projectKey, nodeId) {
    var treeJSONUrl = AJS.contextPath() + "/rest/treeviewerrest/latest/treeviewer.json?projectKey=" + projectKey;
    getJSON(treeJSONUrl, function (err, data) {
        if (err !== null) {
            displayGetJsonError(err);
        } else {
            if ($('#evts').jstree(true)) {
                var tree = $('#evts').jstree(true);
                tree.destroy();
            }
            $('#evts')
                .on("select_node.jstree", function (e, data) {
                    setBack("");
                    var node = data.node.data;
                    fillAccordion(data, projectKey, node);
                    buildTreant(projectKey, node);
                })
                .on('ready.jstree', function () {
                    if (nodeId) {
                        var tree = $('#evts').jstree(true);
                        if(tree){
                            tree.select_node("" + nodeId)
                            console.log("select_node");
                            //no need in any scenario
                        } else {
                            console.log("set_back");
                            //setBack("No Element has been selected");
                        }
                    }
                })
                .jstree({
                    'core' : data,
                    //TODO: add drag n drop
                    'plugins' : [
                        'contextmenu',
                        'wholerow',
                        'sort',
                        'search',
                        ''
                    ],
                    'search' : {
                        'show_only_matches': true
                    },
                    'contextmenu' : {
                        'items' : {
                            'create' : {
                                'label' : 'Create Decision Component',
                                'action' : function(node){
                                    var selector  = node.reference.prevObject.selector;
                                    var tree_node = $('#evts').jstree(true).get_node(selector).data;

                                    //set header
                                    var closeX = document.getElementById('modal-close-x');
                                    closeX.insertAdjacentHTML('beforeBegin', 'Add Decision Component');

                                    //set content
                                    var content = document.getElementById('modal-content');
                                    content.insertAdjacentHTML('afterBegin',
                                        '<p><label for="form-input-name" style="display:block;width:45%;float:left;">Name</label><input id="form-input-name" type="text" name="name" placeholder="Name of decisioncomponent" style="width:50%;"/></p>' +
                                        '<p><label for="form-select-type" style="display:block;width:45%;float:left;">Componenttype</label><select name="form-select-type" style="width:50%;"/></p>' +
                                        '<p><input id="form-input-submit" type="submit" value="Add Decision Component" style="float:right;"/></p>'
                                    );

                                    var type_select = $('select[name="form-select-type"]');
                                    type_select.on('change', function () {
                                        var type = type_select.val();
                                        if (type === 'Argument') {
                                            type_select.insertAdjacentHTML('afterEnd', '<p id="type-of-argument-para"><label for="type-of-argument" style="display:block;width:45%;float:left;">Type of Argument</label><input type="radio" name="type-of-argument" value="pro" checked="checked">Pro<input type="radio" name="type-of-argument" value="contra">Contra<input type="radio" name="type-of-argument" value="comment">Comment</p>');
                                        } else {
                                            var para = document.getElementById("type-of-argument-para");
                                            if (para) {
                                                clearInner(para);
                                                para.parentNode.removeChild(para);
                                            }
                                        }
                                    });

                                    var data = [
                                        {
                                            id: "Alternative",
                                            text: "Alternative"
                                        },
                                        {
                                            id: "Argument",
                                            text: "Argument"
                                        },
                                        {
                                            id: "Assessment",
                                            text: "Assessment"
                                        },
                                        {
                                            id: "Assumption",
                                            text: "Assumption"
                                        },
                                        {
                                            id: "Claim",
                                            text: "Claim"
                                        },
                                        {
                                            id: "Constraint",
                                            text: "Constraint"
                                        },
                                        {
                                            id: "Context",
                                            text: "Context"
                                        },
                                        {
                                            id: "Goal",
                                            text: "Goal"
                                        },
                                        {
                                            id: "Implication",
                                            text: "Implication"
                                        },
                                        {
                                            id: "Issue",
                                            text: "Issue"
                                        },
                                        {
                                            id: "Problem",
                                            text: "Problem"
                                        },
                                        {
                                            id: "Solution",
                                            text: "Solution"
                                        }
                                    ];
                                    for (var index = 0; index < data.length; index++){
                                        type_select[0].insertAdjacentHTML('beforeend','<option value="' + data[index].id + '">' + data[index].text + '</option>');
                                    }

                                    var submitButton = document.getElementById('form-input-submit');
                                    submitButton.onclick = function () {
                                        var name = document.getElementById('form-input-name').value;
                                        var type = type_select.val();
                                        if (type === "Argument") {
                                            var argumentCheckBoxGroup = document.getElementsByName("type-of-argument");
                                            for (var i = 0; i < argumentCheckBoxGroup.length; i++) {
                                                if (argumentCheckBoxGroup[i].checked === true) {
                                                    var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
                                                    if (selectedNatureOfArgument === "pro") {
                                                        createDecisionComponent(name, type, function (data) {
                                                            AJS.flag({
                                                                type: 'success',
                                                                close: 'auto',
                                                                title: 'Success',
                                                                body: type + ' has been created.'
                                                            });
                                                            var idOfNewObject = data.id;
                                                            createLink(tree_node.id, idOfNewObject, "support", function () {
                                                                AJS.flag({
                                                                    type: 'success',
                                                                    close: 'auto',
                                                                    title: 'Success',
                                                                    body: 'IssueLink has been created.'
                                                                });
                                                                buildTreeViewer(projectKey, idOfNewObject);
                                                            });
                                                        });
                                                    } else if (selectedNatureOfArgument === "contra") {
                                                        createDecisionComponent(name, type, function (data) {
                                                            AJS.flag({
                                                                type: 'success',
                                                                close: 'auto',
                                                                title: 'Success',
                                                                body: type + ' has been created.'
                                                            });
                                                            var idOfNewObject = data.id;
                                                            createLink(tree_node.id, idOfNewObject, "attack", function () {
                                                                AJS.flag({
                                                                    type: 'success',
                                                                    close: 'auto',
                                                                    title: 'Success',
                                                                    body: 'IssueLink has been created.'
                                                                });
                                                                buildTreeViewer(projectKey, idOfNewObject);
                                                            });
                                                        });
                                                    } else if (selectedNatureOfArgument === "comment") {
                                                        createDecisionComponent(name, type, function (data) {
                                                            AJS.flag({
                                                                type: 'success',
                                                                close: 'auto',
                                                                title: 'Success',
                                                                body: type + ' has been created.'
                                                            });
                                                            var idOfNewObject = data.id;
                                                            createLink(tree_node.id, idOfNewObject, "comment", function () {
                                                                AJS.flag({
                                                                    type: 'success',
                                                                    close: 'auto',
                                                                    title: 'Success',
                                                                    body: 'IssueLink has been created.'
                                                                });
                                                                buildTreeViewer(projectKey, idOfNewObject);
                                                            });
                                                        });
                                                    }
                                                }
                                            }
                                        } else {
                                            createDecisionComponent(name, type, function (data) {
                                                AJS.flag({
                                                    type: 'success',
                                                    close: 'auto',
                                                    title: 'Success',
                                                    body: type + ' has been created.'
                                                });
                                                var idOfNewObject = data.id;
                                                createLink(tree_node.id, idOfNewObject, "contain", function () {
                                                    AJS.flag({
                                                        type: 'success',
                                                        close: 'auto',
                                                        title: 'Success',
                                                        body: 'IssueLink has been created.'
                                                    });
                                                    buildTreeViewer(projectKey, idOfNewObject);
                                                });
                                            });
                                        }
                                        closeModal();
                                    };

                                    var modal = document.getElementById('ContextMenuModal');
                                    modal.style.display = "block";
                                }
                            },
                            'edit' : {
                                'label' : 'Edit DecisionComponent',
                                'action' : function(node){
                                    var selector  = node.reference.prevObject.selector;
                                    var tree_node = $('#evts').jstree(true).get_node(selector).data;

                                    //set header
                                    var closeX = document.getElementById('modal-close-x');
                                    closeX.insertAdjacentHTML('beforeBegin', 'Edit Decision Component');

                                    var content = document.getElementById('modal-content');
                                    content.insertAdjacentHTML('afterBegin',
                                        '<p><label for="form-input-name" style="display:block;width:45%;float:left;">Name</label><input id="form-input-name" type="text" name="name" value="' + tree_node.summary + '" style="width:50%;" readonly/></p>' +
                                        '<p><label for="form-input-description" style="display:block;width:45%;float:left;">Description</label><input id="form-input-description" type="text" name="type" placeholder="Type in description" style="width:50%;"/></p>' +
                                        '<p><input id="form-input-submit" type="submit" value="Edit Decision Component" style="float:right;"/></p>'
                                    );

                                    var submitButton = document.getElementById('form-input-submit');
                                    submitButton.onclick = function () {
                                        var name = document.getElementById('form-input-name').value;
                                        var description = document.getElementById('form-input-description').value;
                                        editDecisionComponent(tree_node.id, name, description, function () {
                                            AJS.flag({
                                                type: 'success',
                                                close: 'auto',
                                                title: 'Success',
                                                body: 'Decision component has been updated.'
                                            });
                                            buildTreeViewer(projectKey, tree_node.id);
                                        });
                                        closeModal();
                                    };

                                    // Get the modal window
                                    var modal = document.getElementById('ContextMenuModal');
                                    modal.style.display = "block";
                                }
                            },
                            'delete' : {
                                'label' : 'Delete DecisionComponent',
                                'action' : function(node){
                                    var selector  = node.reference.prevObject.selector;
                                    var tree_node = $('#evts').jstree(true).get_node(selector).data;

                                    //set header
                                    var closeX = document.getElementById('modal-close-x');
                                    closeX.insertAdjacentHTML('beforeBegin', 'Delete Decision Component');

                                    var content = document.getElementById('modal-content');
                                    content.insertAdjacentHTML('afterBegin',
                                        '<p><input id="abort-submit" type="submit" value="Abort Action" style="float:right;"/><input id="form-input-submit" type="submit" value="Delete Decision Component" style="float:right;"/></p>'
                                    );

                                    var abortButton = document.getElementById('abort-submit');
                                    abortButton.onclick = function () {
                                        closeModal();
                                    };

                                    var submitButton = document.getElementById('form-input-submit');
                                    submitButton.onclick = function () {
                                        deleteDecisionComponent(tree_node.id, function () {
                                            AJS.flag({
                                                type: 'success',
                                                close: 'auto',
                                                title: 'Success',
                                                body: 'Decisioncomponent has been deleted'
                                            });
                                            buildTreeViewer(projectKey, tree_node.id);
                                        });
                                        closeModal();
                                    };

                                    // Get the modal window
                                    var modal = document.getElementById('ContextMenuModal');
                                    modal.style.display = "block";
                                }
                            }
                        }
                    }
                });
            document.getElementById("Details").style.display = "block";
            $(".search-input").keyup(function () {
                var searchString = $(this).val();
                $('#evts').jstree(true).search(searchString);
            });
        }
    });
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

function initializeSite() {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    buildTreeViewer(projectKey);

    /*ClickHandler for accordionelements*/
    $(document).ready(function () {
        $("dt").click(function () {
            $(this).next("dd").slideToggle("fast");
        });
    });
    /*ClickHandler for the creation of decisions*/
    var createDecisionButton = document.getElementById("CreateDecision");
    var DecisionInputField = document.getElementById("DecisionInputField");
    createDecisionButton.addEventListener('click', function () {
        var tempDecString = DecisionInputField.value;
        DecisionInputField.value = "";
        createDecisionComponent(tempDecString, "Decision", function (data) {
            AJS.flag({
                type: 'success',
                close: 'auto',
                title: 'Success',
                body: 'Decision has been created'
            });
            var tree = $('#evts').jstree(true);
            var nodeId = tree.create_node('#', data, 'last', tree.redraw(true), true);
            tree.deselect_all();
            tree.select_node(nodeId);
        });
    });
    /*ClickHandler for the Editor Button*/
    var viewEditorButton = document.getElementById("view-editor");
    viewEditorButton.addEventListener('click', function () {
        var editorContainer = document.getElementById("container");
        var treantContainer = document.getElementById("treant-container");
        editorContainer.style.display = "block";
        treantContainer.style.visibility = "hidden";
    });
    /*ClickHandler for the Tree Button*/
    var viewTreeButton = document.getElementById("view-tree");
    viewTreeButton.addEventListener('click', function () {
        var editorContainer = document.getElementById("container");
        var treantContainer = document.getElementById("treant-container");
        treantContainer.style.visibility = "visible";
        editorContainer.style.display = "none";
    });
    var DepthOfTreeInput = document.getElementById("depthOfTreeInput");
    DepthOfTreeInput.addEventListener('input', function () {
        var DepthOfTreeWarningLabel = document.getElementById("DepthOfTreeWarning");
        if (this.value > 0) {
            DepthOfTreeWarningLabel.style.visibility = "hidden";
        } else {
            DepthOfTreeWarningLabel.style.visibility = "visible";
        }
    });
    window.onkeydown = function( event ) {
        if ( event.keyCode == 27 ) {
            var modal = document.getElementById('ContextMenuModal');
            if (modal.style.display === "block"){
                closeModal();
            }
        }
    };
}

function closeModal() {
    // Get the modal window
    var modal = document.getElementById('ContextMenuModal');
    modal.style.display = "none";
    var modalHeader = document.getElementById('modal-header');
    if (modalHeader.hasChildNodes()) {
        var childNodes = modalHeader.childNodes;
        for (var index = 0; index < childNodes.length; ++index) {
            var child = childNodes[index];
            if (child.nodeType === 3) {
                child.parentNode.removeChild(child);
            }
        }
    }
    var modalContent = document.getElementById('modal-content');
    if (modalContent) {
        clearInner(modalContent);
    }
}

/*
Source: https://stackoverflow.com/users/2234742/maximillian-laumeister
Maximillian Laumeister
Software Developer at Tanzle
*/
function clearInner(node) {
    while (node.hasChildNodes()) {
        clear(node.firstChild);
    }
}

function clear(node) {
    while (node.hasChildNodes()) {
        clear(node.firstChild);
    }
    node.parentNode.removeChild(node);
}