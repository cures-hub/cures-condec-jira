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