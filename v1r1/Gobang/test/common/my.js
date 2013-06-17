$(document).ready(function() {
/* define basic function on ajax */
(function() {
		var AjaxFactory = {};
		AjaxFactory.getLeaguerInfoProxy = function() {
			return new Proxy('leaguerInfo');
		};
		AjaxFactory.getLeaguerOperProxy = function() {
			return new Proxy('leagerOper');
		};
		AjaxFactory.getEventProxy = function() {
			return new Proxy('event');
		};
		AjaxFactory.getLogProxy = function() {
			return new Proxy('log');
		};
		function Proxy(url) {
			var serviceUrl = '/gobang/ajax/' + url;
			this.sendRequest = function(option) {
				$.ajax($.extend({
							url : serviceUrl,
							type : 'GET',
							async : false,
							data : '',
							dataFilter : function(data, type) {
							},
							success : function(event, xhr, options) {
							},
							error : function(jqXHR, textStatus, errorThrown) {
							},
							complete : function(jqXHR, textStatus) {

							}
						}, option));
			}
		}
		window.AjaxFactory = AjaxFactory;
	})();

/* event polling */
(function() {
		var EventUtil = {}, listenerList = [];
		EventUtil.registerListen = function(eventid, func) {
			if (!Array.isArray(listenerList[eventid])) {
				listenerList[eventid] = [];
			}
			var timeStamp = new Date().getTime();
			listenerList[eventid].push({
						listenid : timeStamp,
						callback : func
					});
			return timeStamp;
		}
		EventUtil.unregisterListen = function(listenid) {
			for (eventid in listenerList) {
				listeners = listenerList[eventid];
				if (listeners && listeners.length) {
					for (var listenIndex = 0; listenIndex < listeners.length; listenIndex++) {
						if (listeners[listenIndex].listenid === listenid) {
							listeners.splice(listenIndex, 1);
							break;
						}
					}
				}
			}
		}
		window.EventUtil = EventUtil;

		/* event center */
		var timeStamp = new Date().getTime();
		// register event polling
		AjaxFactory.getEventProxy().sendRequest({
					type : 'POST',
					data : {
						pageid : timeStamp
					}
				});
		// start event polling
		var eventPolling = function() {
			AjaxFactory.getEventProxy().sendRequest({
				data : {
					pageid : timeStamp
				},
				success : function(event, xhr, options) {
					var data = JSON.parse(options.responseText);
					if (data && data.eventList && data.eventList.length) {
						var eventList = data.eventList;
						for (var eventIndex = 0; eventIndex < eventList.length; eventIndex++) {
							var eventid = eventList[eventIndex].event.eventid;
							var data = eventList[eventIndex].event.data;
							if (listenerList && listenerList[eventid]) {
								for (var listenIndex = 0; listenIndex < listenerList[eventid].length; listenIndex++) {
									listenerList[eventid][listenIndex]
											.callback(data);
								}
							}
						}
					}
				},
				complete : function(jqXHR, textStatus) {
					window.setTimeout(eventPolling, 1 * 100);
				}
			});
		}
		eventPolling();
	})();

/* print log in server */
(function() {
		var LogUtil = {};
		LogUtil.debug = function(data) {
			log('debug', data);
		}
		LogUtil.warn = function(data) {
			log('warn', data);
		}
		LogUtil.error = function(data) {
			log('error', data);
		}
		function log(level, data) {
			AjaxFactory.getLogProxy().sendRequest({
						data : 'level=' + level + '&data=' + data,
						error : function(jqXHR, textStatus, errorThrown) {
							console.log(errorThrown);
						}
					});
		}
		window.LogUtil = LogUtil;
	})();
});
