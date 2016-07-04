if (!Element.prototype.matches) {
    Element.prototype.matches = 
        Element.prototype.matchesSelector || 
        Element.prototype.mozMatchesSelector ||
        Element.prototype.msMatchesSelector || 
        Element.prototype.oMatchesSelector || 
        Element.prototype.webkitMatchesSelector ||
        function(s) {
            var matches = (this.document || this.ownerDocument).querySelectorAll(s),
                i = matches.length;
            while (--i >= 0 && matches.item(i) !== this) {}
            return i > -1;            
        };
}


var isFunction = function(obj) {
	 return !!(obj && obj.constructor && obj.call && obj.apply);
};

window.marathon_create_session = function() {
	var details = {
		"driver" : "Marathon Recorder",
		"driver.version" : "1.0",
		"platform" : "browser",
		"platform.version" : "1.0",
		"os" : "windows",
		"os.version" : "10.0",
		"os.arch" : "x64"
	};

	window.marathon_ws_read = function(data) {
		data = JSON.parse(data);
		var sessionID = JSON.parse(data.data).sessionID;
		window.marathon_base = "/session/" + sessionID;
		window.get_json('object_map_configuration')
		window.marathon_ws_read = function(response) {
			var config = JSON.parse(JSON.parse(response).data);
			var lists = [ 'namingProperties', 'recognitionProperties',
					'containerNamingProperties',
					'containerRecognitionProperties' ];

			lists.forEach(function(list) {
				var nplist = [] ;
				config.value[list].forEach(function(np) {
					np.propertyLists.map(function(propertyList) {
						propertyList.className = np.className;
						nplist.push(propertyList);
					});
				});
				nplist = nplist.sort(function(a, b) {
					return b.priority - a.priority;
				});
				window[list] = nplist;
			});
			window.marathon_ws_read = window.marathon_record_reply;
		}
	}
	window.post_json("session", JSON.stringify(details));
}

window.marathon_hook = function() {
	if ("WebSocket" in window) {

		window.marathon_base = "";

		document.addEventListener('change', function(evt) {
			var target = evt.target;
			var matched = window.namingProperties.find(function(np) {
				if(target.matches(np.className)) {
					var rb = true ;
					np.properties.forEach(function(prop) {
						if(rb && (target.getAttribute(prop) === null && !target[prop] && !isFunction(target[prop]))) {
							rb = false ;
						}
					});
					return rb;
				}
				return false;
			});
			console.log(matched);
			if(matched) {
				var name = matched.properties.map(function(prop) {
					if(isFunction(target[prop]))
						return target[prop].call(target);
					return target.getAttribute(prop);
				}).join(":");
				console.log(name);
			}
		});
		
		var ws = new WebSocket("ws://localhost:marathon_recorder_port/");

		ws.onopen = function() {
			window.marathon_create_session();
		};

		ws.onmessage = function(evt) {
			window.marathon_ws_read(evt.data);
		};

		ws.onclose = function() {
		};

		window.post_json = function(path, data) {
			var pb = {
				method : 'post',
				path : window.marathon_base + "/" + path,
				data : data
			};
			ws.send(JSON.stringify(pb));
		};

		window.get_json = function(path) {
			var pb = {
				method : 'get',
				path : window.marathon_base + "/" + path
			};
			ws.send(JSON.stringify(pb));
		};

		window.marathon_record_reply = function(data) {
		}
	} else {
		// The browser doesn't support WebSocket
		alert("WebSocket NOT supported by your Browser!");
	}
}
window.marathon_hook();
