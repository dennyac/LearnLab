@(username: String)

$(function() {
    var temp = 0
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var chatSocket = new WS("@routes.Chat.instructor(username).webSocketURL(request)")

    var sendMessage = function() {
        chatSocket.send(JSON.stringify(
            {text: $("#talk").val()}
        ))
        $("#talk").val('')
    }

    var receiveEvent = function(event) {
        console.log("Received event")

        var data = JSON.parse(event.data)
        console.log(data.text)

        // Handle errors
        if(data.error) {
            chatSocket.close()
            $("#onError span").text(data.error)
            $("#onError").show()
            return
        } else {
            $("#onChat").show()
        }

        // Create the message element
        if(data.username != '@username'){
            var el = $('<div class="message"><span></span><p></p></div>')
            $("span", el).text(data.username)
            $("p", el).text(data.text)
            $(el).addClass("talk")
            if(data.user == '@username') $(el).addClass('me')
            $('#messages').append(el)
        }



        // Update the members list
//        $("#members").html('')
//        $(data.members).each(function() {
//            var li = document.createElement('li');
//            li.textContent = this;
//            $("#members").append(li);
//        })


    }

    var handleReturnKey = function(e) {
        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault()
            sendMessage()
        }
    }

    $("#talk").keypress(handleReturnKey)

    chatSocket.onmessage = receiveEvent

})
