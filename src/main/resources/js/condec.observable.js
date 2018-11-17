/*
 This module provides the notification method for the ConDec views. This module is the subject/observable 
 according to the observer design pattern. The views/observers subscribe/register to this observable. 
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

	var observers = null;

	var ConDecObservable = function ConDecObservable() {
		this.observers = [];
	};

	ConDecObservable.prototype.notify = function notify() {
		this.observers.forEach(function(observer) {
			observer.updateView();
		});
	};

	ConDecObservable.prototype.subscribe = function subscribe(observer) {
		this.observers.push(observer);
	};

	// export ConDecObservable
	global.conDecObservable = new ConDecObservable();
})(window);