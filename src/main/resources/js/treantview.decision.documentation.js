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

                        var type_select = setContent();

                        var data = setData();
                        for (var index = 0; index < data.length; index++){
                            type_select[0].insertAdjacentHTML('beforeend','<option value="' + data[index].id + '">' + data[index].text + '</option>');
                        }

                        var submitButton = document.getElementById('form-input-submit');
                        setSubmitFunction(submitButton, type_select, projectKey, options.$trigger.context.id);

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