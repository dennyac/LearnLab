@(instructorUsername: String)

$(document).ready(function() {
        var el="Hello there!!!"
        $('#dummy').append(el)
        var select = document.getElementById("participant1");
        for(var i = 1990; i <= 2011; i++){
        var option = document.createElement('option');
        option.text = option.value = i;
        select.add(option, 0);
        }
        });