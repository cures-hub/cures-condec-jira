/*
 This view provides a table with release notes.
 
 Requires
 * conDecAPI
 * conDecObservable
 * conDecContextMenu
 * conDecTreant
 * conDecTreeViewer

 Is referenced in HTML by
 * decisionKnowledgePage.vm
 */
(function(global) {
	/* private vars */
	var conDecObservable = null;
	var conDecAPI = null;
	var conDecDialog = null;


	var ConDecReleaseNotePage = function ConDecReleaseNotePage() {
	};

	ConDecReleaseNotePage.prototype.init = function(_conDecAPI, _conDecObservable, _conDecDialog) {
		console.log("conDecReleaseNotePage init");

		if (isConDecAPIType(_conDecAPI) && isConDecObservableType(_conDecObservable)
				&& isConDecDialogType(_conDecDialog) ) {

			conDecAPI = _conDecAPI;
			conDecObservable = _conDecObservable;
			conDecDialog = _conDecDialog;

			// Register/subscribe this view as an observer
			conDecObservable.subscribe(this);

			return true;
		}
		return false;
	};

	ConDecReleaseNotePage.prototype.getReleaseNotes = function() {
		conDecAPI.getReleaseNotes(function(){
			console.log("get release notes");
		});

	};

	ConDecReleaseNotePage.prototype.addReleaseNote = function() {
		conDecDialog.showCreateReleaseNoteDialog();
	};



	/*
	 * Init Helpers
	 */
	function isConDecAPIType(conDecAPI) {
		if (!(conDecAPI !== undefined && conDecAPI.getDecisionKnowledgeElement !== undefined && typeof conDecAPI.getDecisionKnowledgeElement === 'function')) {
			console.warn("ConDecKnowledgePage: invalid ConDecAPI object received.");
			return false;
		}
		return true;
	}

	function isConDecObservableType(conDecObservable) {
		if (!(conDecObservable !== undefined && conDecObservable.notify !== undefined && typeof conDecObservable.notify === 'function')) {
			console.warn("ConDecKnowledgePage: invalid ConDecObservable object received.");
			return false;
		}
		return true;
	}

	function isConDecDialogType(conDecDialog) {
		if (!(conDecDialog !== undefined && conDecDialog.showCreateDialog !== undefined && typeof conDecDialog.showCreateDialog === 'function')) {
			console.warn("ConDecKnowledgePage: invalid conDecDialog object received.");
			return false;
		}
		return true;
	}


	// export ConDecKnowledgePage
	global.conDecReleaseNotePage = new ConDecReleaseNotePage();
})(window);