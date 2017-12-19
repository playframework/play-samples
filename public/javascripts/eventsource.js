if (!!window.EventSource) {
    var stringSource = new EventSource(jsRoutes.controllers.JavaEventSourceController.streamClock().url);
    stringSource.addEventListener('message', function(e) {
        $('#clock').html(e.data.replace(/(\d)/g, '<span>$1</span>'))
    });
} else {
    $("#clock").html("Sorry. This browser doesn't seem to support Server sent event. Check <a href='http://html5test.com/compare/feature/communication-eventSource.html'>html5test</a> for browser compatibility.");
}
