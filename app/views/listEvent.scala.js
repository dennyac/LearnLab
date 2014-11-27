@(divID: String,eventList: String)
var divid = '@divID';
var eventList = '@eventList';
$(document).ready(function() {
        var select = document.getElementById(divid);
        var eventNameArray = eventList.split('|');
        var arrayLength=eventNameArray.length;
        for(var i = 0;i<arrayLength;i++) {
        var option = document.createElement('option');
        option.text = option.value = eventNameArray[i];
        select.add(option, 0);
        }});