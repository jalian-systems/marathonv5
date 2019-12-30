console.log("Loading marathon.js...");

if(window.$marathon && window.$marathon.wsClosed !== true)
	console.warn("marathon.js already loaded... ignoring the load.")
else
	window.$marathon = new Marathon(arguments[0], arguments[1], arguments[2]);
