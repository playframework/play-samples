// Called for each Comet message
var clockChanged = function(time) {
    $('#clock').html(time.replace(/(\d)/g, '<span>$1</span>'))
}
