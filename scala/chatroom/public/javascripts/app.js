$( document ).ready(function() {
	if ("WebSocket" in window) {
       console.log("WebSocket is supported by your Browser!");
    } else {
    	console.log("WebSocket NOT supported by your Browser!");
    	return;
    }	
	var getScriptParamUrl = function() {
	    var scripts = document.getElementsByTagName('script');
	    var lastScript = scripts[scripts.length-1];
	    return lastScript.getAttribute('data-url');
	};

	var send = function() {
		var text = $message.val();
		$message.val("");
		connection.send(text);
	};

	var $messages = $("#messages"), $send = $("#send"), $message = $("#message"); 
	
	var url = getScriptParamUrl();
	var connection = new WebSocket(url);

	$send.prop("disabled", true);
		
	connection.onopen = function() {
		$send.prop("disabled", false);
		$messages
				.prepend($("<li class='bg-info' style='font-size: 1.5em'>Connected</li>"));
		$send.on('click', send);
		$message.keypress(function(event) {
			var keycode = (event.keyCode ? event.keyCode : event.which);
			if (keycode == '13') {
				send();
			}
		});
	};
	connection.onerror = function(error) {
		console.log('WebSocket Error ', error);
	};
	connection.onmessage = function(event) {
		$messages.append($("<li style='font-size: 1.5em'>" + event.data + "</li>"))
	}

	console.log( "chat app is running!" );	
});