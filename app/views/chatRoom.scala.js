@(username: String)

$(function() {
    console.log("Hello");
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
 var chatSocket = new WS("@routes.Chat.chat(username).webSocketURL(request)")

    var sendMessage = function() {
        chatSocket.send(JSON.stringify(
            {text: $("#talk").val()}
        ))
        $("#talk").val('')
    }

    var receiveEvent = function(event) {
        var data = JSON.parse(event.data)

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
        if(data.user != 'Robot'){
            var el = $('<div class="message"><span></span><p></p></div>')
            $("span", el).text(data.user)
            $("p", el).text(data.message)
            $(el).addClass(data.kind)
            $(el).id
            if(data.user == '@username') $(el).addClass('me')
            $('#messages').append(el)
        }





        if(data.message.match(/(^|\s)(#[a-z\d-]+)/ig) != null){
            var hashtag = data.message.match(/(^|\s)(#[a-z\d-]+)/ig)[0].substr(1)
            console.log('Matched Hashtag')
            var $object = $('#h' + hashtag);
            if(!$object.length) {
                var dl = $('<div class="panel panel-default"><div class="panel-heading" role="tab" >               <h4 class="panel-title"><a data-toggle="collapse" data-parent="#accordion" aria-expanded="true" >                           </a></h4></div><div                  class="panel-collapse collapse in" role="tabpanel" >                            <div class="panel-body"><ul class="list-group"></ul></div></div></div>')
                        //  <div class="panel panel-default"><div class="panel-heading" role="tab" id="headingOne"><h4 class="panel-title"><a data-toggle="collapse" data-parent="#accordion" aria-expanded="true" aria-controls="collapseOne"></a></h4></div><div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne"><div class="panel-body">                            </div></div></div>
                $('.panel-heading',dl).attr('id',"heading" + hashtag)
                $('a',dl).attr('href','#collapse' + hashtag)
                $('a',dl).attr('aria-controls','collapse' + hashtag)
                $('a', dl).text(hashtag)
                $('.panel-collapse',dl).attr('aria-labelledby',"heading" + hashtag)
                $('.panel-collapse',dl).attr('id',"collapse" + hashtag)
                $("ul",dl).attr('id',"ul" + hashtag)
                $(dl).attr('id',"h" + hashtag)
                $('#accordion').append(dl)
            }

            var li = document.createElement('li');
            $(li).addClass("list-group-item")
            $(li).text(data.message)
            $("#ul" + hashtag).append(li);
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
