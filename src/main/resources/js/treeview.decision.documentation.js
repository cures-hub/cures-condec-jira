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

                                    var type_select = setContent();

                                    var data = setData();
                                    for (var index = 0; index < data.length; index++){
                                        type_select[0].insertAdjacentHTML('beforeend','<option value="' + data[index].id + '">' + data[index].text + '</option>');
                                    }

                                    var submitButton = document.getElementById('form-input-submit');
                                   setSubmitFunction(submitButton, type_select, projectKey, tree_node.id);

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