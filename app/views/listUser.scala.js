@(divID: String,userList: String)
var divid = '@divID';
var usrList = '@userList';
$(document).ready(function() {
        var select = document.getElementById(divid);
        var userFullNameArray = usrList.split('|');
        var arrayLength=userFullNameArray.length;
        for(var i = 0;i<arrayLength;i++) {
        var option = document.createElement('option');
        option.text = option.value = userFullNameArray[i];
        select.add(option, 0);
        }});