@(username: String, eventId: Long)

$(function() {
    console.log("Hello");
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
 var chatSocket = new WS("@routes.Chat.chat(username, eventId).webSocketURL(request)")

    var sendMessage = function() {
        chatSocket.send(JSON.stringify(
            {text: $("#talk").val(), user: '@username', event: '@eventId'}
        ))
        $("#talk").val('')
    }

    var receiveEvent = function(event) {
        var data = JSON.parse(event.data)
        if(data.kind == 'Dummy'){
            console.log('Dummy message received to keep the websocket alive.')
        }
        else {


            // Handle errors
            if(data.error) {
                alert("helo");
                chatSocket.close()
                $("#onError span").text(data.error)
                $("#onError").show()
                return
            } else {
                $("#onChat").show()
            }


                var imgUrl = 'http://www.gravatar.com/avatar/' + CryptoJS.MD5(data.user == "Sparky"? 'sparky.learnlab@@gmail.com':data.user) + '?d=mm'
                var chatMsg = $('<li ><div class="avatar"><img /></div><div class="messages"><p></p><time></time></div></li>');
                $('img',chatMsg).attr('src',imgUrl)
                $('p',chatMsg).text(data.message)
                $('time',chatMsg).text(data.user)
                if(data.user == "Sparky"){
                    $(chatMsg).addClass('sparky')
                }
                else if(data.user == '@username'){
                    $(chatMsg).addClass('self')
                }
                else{
                    $(chatMsg).addClass('other')
                }
                $('#newchat').append(chatMsg)
                var chatScroll = $("#newchat");
                chatScroll.scrollTop(chatScroll.prop('scrollHeight'))



            //Pinned Post logic
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
        }




    }

    var handleReturnKey = function(e) {
        if(e.charCode == 13 || e.keyCode == 13) {
            e.preventDefault()
            sendMessage()
        }
    }

    $("#talk").keypress(handleReturnKey)

    $('#btn-chat').click(function(){sendMessage()})

    chatSocket.onmessage = receiveEvent

})
