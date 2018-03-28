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
                                'label' : 'Add Decision Component',
                                'action' : function(node){
                                    var selector  = node.reference.prevObject.selector;
                                    var tree_node = $('#evts').jstree(true).get_node(selector).data;

                                    //set header
                                    var closeX = document.getElementById('modal-close-x');
                                    //closeX.insertAdjacentHTML('beforeBegin', 'Add Decision Component');

                                    //set content
                                    var content = document.getElementById('modal-content');
                                    content.insertAdjacentHTML('afterBegin',
                                        '<p><label for="form-input-name" style="display:block;width:45%;float:left;">Summary:</label><input id="form-input-name" type="text" name="summary" placeholder="Summary of decision component" style="width:50%;"/></p>' +
                                        '<p><label for="form-select-type" style="display:block;width:45%;float:left;">Type:</label><select name="form-select-type" style="width:50%;"/></p>' +
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
                                                        createDecisionComponent(summary, type, function (data) {
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
                                                        createDecisionComponent(summary, type, function (data) {
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
                                            createDecisionComponent(summary, type, function (data) {
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
                                    //closeX.insertAdjacentHTML('beforeBegin', 'Edit Decision Component');

                                    var content = document.getElementById('modal-content');
                                    content.insertAdjacentHTML('afterBegin',
                                        '<p><label for="form-input-name" style="display:block;width:45%;float:left;">Summary:</label><input id="form-input-name" type="text" name="summary" value="' + tree_node.summary + '" style="width:50%;" readonly/></p>' +
                                        '<p><label for="form-input-description" style="display:block;width:45%;float:left;">Description:</label><input id="form-input-description" type="text" name="type" placeholder="Type in description" style="width:50%;"/></p>' +
                                        '<p><input id="form-input-submit" type="submit" value="Edit Decision Component" style="float:right;"/></p>'
                                    );

                                    var submitButton = document.getElementById('form-input-submit');
                                    submitButton.onclick = function () {
                                        var summary = document.getElementById('form-input-name').value;
                                        var description = document.getElementById('form-input-description').value;
                                        editDecisionComponent(tree_node.id, summary, description, function () {
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
                                'label' : 'Delete Decision Component',
                                'action' : function(node){
                                    var selector  = node.reference.prevObject.selector;
                                    var tree_node = $('#evts').jstree(true).get_node(selector).data;

                                    //set header
                                    var closeX = document.getElementById('modal-close-x');
                                    closeX.insertAdjacentHTML('beforeBegin', 'Delete Decision Component');

                                    var content = document.getElementById('modal-content');
                                    content.insertAdjacentHTML('afterBegin',
                                        '<p><input id="abort-submit" type="submit" value="Abort Action" style="float:right;"/><input id="form-input-submit" type="submit" value="Delete this element" style="float:right;"/></p>'
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