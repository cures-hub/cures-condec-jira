/*
 This module provides the notification method for the ConDec views. This module is the subject/observable 
 according to the observer design pattern. The views/observers subscribe to this observable. 
 The views need to implement an updateView function. 
 The updateView functions of the subscribed views are called in the notify function.
 
 Registered/subscribed views/observers can be
 * conDecIssueModule
 * conDecKnowledgePage
 * conDecTabPanel
    
 Is required by
 * view.*  
 * 
 */
(function(global) {

	var ConDecObservable = function ConDecObservable() {
		// TODO add observers
	};

	ConDecObservable.prototype.notify = function notify() {
		if (global.conDecIssueModule !== undefined) {
			global.conDecIssueModule.updateView();
		} else if (global.conDecKnowledgePage !== undefined) {
			global.conDecKnowledgePage.updateView();
		}
	};

	// export ConDecObservable
	global.conDecObservable = new ConDecObservable();
})(window);