function initializeSite() {
    var pathname = window.location.pathname;
    var stringArray = pathname.split("/");
    var projectKey = stringArray[stringArray.length - 1];
    buildTreeViewer(projectKey);

    /*ClickHandler for accordion elements*/
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

function setContent() {

    var content = document.getElementById('modal-content');
    content.insertAdjacentHTML('afterBegin',
        '<p><label for="form-input-name" style="display:block;width:45%;float:left;">Name</label><input id="form-input-name" type="text" name="summary" placeholder="Name of decision component" style="width:50%;"/></p>' +
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
    return type_select;
}

function setData() {
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
    return data
}

function setSubmitFunction(submitButton, type_select, projectKey, id) {
    submitButton.onclick = function () {
        var summary = document.getElementById('form-input-name').value;
        var type = type_select.val();
        if (type === "Argument") {
            var argumentCheckBoxGroup = document.getElementsByName("type-of-argument");
            for (var i = 0; i < argumentCheckBoxGroup.length; i++) {
                if (argumentCheckBoxGroup[i].checked === true) {
                    var selectedNatureOfArgument = argumentCheckBoxGroup[i].value;
                    if (selectedNatureOfArgument === "pro") {
                        createDecisionComponent(summary, type, function (data) {
                            AJS.flag({
                                type: 'success',
                                close: 'auto',
                                title: 'Success',
                                body: type + ' has been created.'
                            });
                            var idOfNewObject = data.id;
                            createLink(id, idOfNewObject, "support", function () {
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
                        createDecisionComponent(summary, type, function (data) {
                            AJS.flag({
                                type: 'success',
                                close: 'auto',
                                title: 'Success',
                                body: type + ' has been created.'
                            });
                            var idOfNewObject = data.id;
                            createLink(id, idOfNewObject, "attack", function () {
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
                        createDecisionComponent(summary, type, function (data) {
                            AJS.flag({
                                type: 'success',
                                close: 'auto',
                                title: 'Success',
                                body: type + ' has been created.'
                            });
                            var idOfNewObject = data.id;
                            createLink(id, idOfNewObject, "comment", function () {
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
            createDecisionComponent(summary, type, function (data) {
                AJS.flag({
                    type: 'success',
                    close: 'auto',
                    title: 'Success',
                    body: type + ' has been created.'
                });
                var idOfNewObject = data.id;
                createLink(id, idOfNewObject, "contain", function () {
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
}