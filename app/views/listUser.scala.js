@(instructorUsername: String,userList: String)
var insname = '@instructorUsername';
var usrList = '@userList';
$(document).ready(function() {
        //var el="Hello there!!!"
        //$('#dummy').append(el)
        var select = document.getElementById("participant1");
        window.alert(insname);
        var userFullNameArray = usrList.split('|');
        var arrayLength=userFullNameArray.length;
        for(var i = 0;i<arrayLength;i++) {
        var option = document.createElement('option');
        option.text = option.value = userFullNameArray[i];
        select.add(option, 0);
        }});