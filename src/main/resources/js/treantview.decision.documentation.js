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
                                            createDecisionComponent(summary, type, function (data) {
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
                                            createDecisionComponent(summary, type, function (data) {
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
                                createDecisionComponent(summary, type, function (data) {
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
                            '<p><label for="form-input-name" style="display:block;width:45%;float:left;">Name</label><input id="form-input-name" type="text" name="summary" value="" style="width:50%;" readonly/></p>' +
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
                            var summary = document.getElementById('form-input-name').value;
                            var description = document.getElementById('form-input-description').value;
                            editDecisionComponent(context.id, summary, description, function () {
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