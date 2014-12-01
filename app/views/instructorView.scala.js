@(username: String, graphInput: String, hashesList: List[String])
var uname = '@username';
var graphJsonData = '@graphInput';
var barData = jQuery.parseJSON(graphJsonData);
var scriptHashTags = [];
@for(hash <- hashesList){
    scriptHashTags.push('@hash');
}
hashTagMap = {};
for(i=0;i<scriptHashTags.length;i++){
    hashTagMap[scriptHashTags[i]] = 0;
}
//var barData = [{
//    'x': "Informal Messages",
//    'y': 0
//}, {
//    'x': "Hashtag Messages",
//    'y': 0
//},{
//      'x': "#explain",
//      'y': 0
//  },{
//          'x': "#justify",
//          'y': 0
//      }
//];
console.log(scriptHashTags);
var message_count =  0;
var hash_message_count = 0;
$(function() {
    var temp = 0
    var WS = window['MozWebSocket'] ? MozWebSocket : WebSocket
    var chatSocket = new WS("@routes.Chat.instructor(username).webSocketURL(request)")

    var sendMessage = function() {
        var e = document.getElementById("eventSelect");
        var strUser = e.options[e.selectedIndex].value;
        chatSocket.send(JSON.stringify(
            {text: $("#talk").val(), eventid: strUser}
        ))
        $("#talk").val('')
    }

    var receiveEvent = function(event) {
        console.log("Received event")
        var data = JSON.parse(event.data)
        if(data.text == 'Dummy'){
            console.log('Dummy message received to keep the websocket alive.')
        }
        else{
            //username
            //eventid
            //text
            console.log("data.text-" +data.text+"-data.username-"+data.username+"data.eventid" +data.eventId )
            // Handle errors
            if(data.error) {
                chatSocket.close()
                $("#onError span").text(data.error)
                $("#onError").show()
                return
            } else {
                $("#onChat").show()
            }

            if(data.text.match(/(^|\s)(#[a-z\d-]+)/ig) != null){
                var hashtag = data.text.match(/(^|\s)(#[a-z\d-]+)/ig)[0].substr(1);
                console.log('Matched Hashtag')
                var $object = $('#h' +data.eventId+ hashtag);
                if(!$object.length) {
                    var dl = $('<div class="panel panel-default"><div class="panel-heading" role="tab" >               <h4 class="panel-title"><a data-toggle="collapse" data-parent="#accordion" aria-expanded="true" >                           </a></h4></div><div                  class="panel-collapse collapse in" role="tabpanel" >                            <div class="panel-body"><ul class="list-group"></ul></div></div></div>')
                    //  <div class="panel panel-default"><div class="panel-heading" role="tab" id="headingOne"><h4 class="panel-title"><a data-toggle="collapse" data-parent="#accordion" aria-expanded="true" aria-controls="collapseOne"></a></h4></div><div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne"><div class="panel-body">                            </div></div></div>
                    $('.panel-heading',dl).attr('id',"heading" + hashtag)
                    $('a',dl).attr('href','#collapse' + hashtag)
                    $('a',dl).attr('aria-controls','collapse' + hashtag)
                    $('a', dl).text(hashtag)
                    $('.panel-collapse',dl).attr('aria-labelledby',"heading" + hashtag)
                    $('.panel-collapse',dl).attr('id',"collapse" + hashtag)
                    $("ul",dl).attr('id',"ul" + hashtag+ data.eventId)
                    $(dl).attr('id',"h" +data.eventId + hashtag)
                    $('#accordion' + data.eventId).append(dl)
                }

                var li = document.createElement('li');
                $(li).addClass("list-group-item")
                $(li).text(data.text)
                $("#ul" + hashtag + data.eventId).append(li);
            }
//
//        // Create the message element
//        if(data.username != 'Robot'){
//            var el = $('<div class="message"><span></span><p></p></div>')
//            $("span", el).text(data.username)
//            $("p", el).text(data.text)
//            $(el).addClass("talk")
//            if(data.user == '@username') $(el).addClass('me')
//            $('#messages').prepend(el)
//        }

            console.log(event);

            // Update the members list
//        $("#members").html('')
//        $(data.members).each(function() {
//            var li = document.createElement('li');
//            li.textContent = this;
//            $("#members").append(li);
//        })
//        alert("receiving");
//        barData.shift();
//        barData.push(next());

        if(data.text.match(/(^|\s)(#[a-z\d-]+)/ig) != null){
            var t = data.text.match(/(^|\s)(#[a-z\d-]+)/ig);
            var v = t[0].substring(1);
            console.log(v);
            if(v in hashTagMap){
                hashTagMap[v] = hashTagMap[v] + 1;
            }
            hash_message_count = hash_message_count + 1;
            hashTagMap["Formal"] = hashTagMap["Formal"] + 1;
        }else{
            message_count = message_count + 1;
            hashTagMap["Informal"] = hashTagMap["Informal"] + 1;
        }

//        message_count = message_count + 1;
//        hash_message_count = hash_message_count + 1;
        var newData = [];
//        var temp1 = {};
//        temp1["x"]="Informal Messages";
//        temp1["y"]=message_count;
//        newData.push(temp1);
//        var temp2 = {};
//        temp2["x"]="Hashtag Messages";
//        temp2["y"]=hash_1message_count;
//        newData.push(temp2);
//        var temp2 = {};
//        temp2["x"]="Hashtag Messages";
//        temp2["y"]=hash_1message_count;
//        newData.push(temp2);


        for(i=0;i<scriptHashTags.length;i++){
            var temp = {};
            temp["x"] = scriptHashTags[i];
            temp["y"] = hashTagMap[scriptHashTags[i]];
            newData.push(temp);
        }

        console.log(newData);
        redraw(newData);
        }
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

//function drawBarGraph(){

    var color = d3.scale.ordinal()
        .range(["#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"]);
    var vis = d3.select('#visualisation1'),
        WIDTH = 500,
        HEIGHT = 300,
        MARGINS = {
            top: 20,
            right: 20,
            bottom: 20,
            left: 50
        },
        xRange = d3.scale.ordinal().rangeRoundBands([MARGINS.left, WIDTH - MARGINS.right], 0.1).domain(barData.map(function (d) {
            return d.x;
        })),


//        yRange = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([0,
//            d3.max(barData, function (d) {
//                return d.y;
//            })
//        ]),

        yRange = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([0,40]),


        xAxis = d3.svg.axis()
            .scale(xRange)
            .tickSize(5)
            .tickSubdivide(true),

        yAxis = d3.svg.axis()
            .scale(yRange)
            .tickSize(5)
            .orient("left")
            .tickSubdivide(true);


    vis.append('svg:g')
        .attr('class', 'x axis')
        .attr('transform', 'translate(0,' + (HEIGHT - MARGINS.bottom) + ')')
        .call(xAxis);

    vis.append('svg:g')
        .attr('class', 'y axis')
        .attr('transform', 'translate(' + (MARGINS.left) + ',0)')
        .call(yAxis);

    vis.selectAll('rect')
        .data(barData)
        .enter()
        .append('rect')
        .attr('x', function (d) {
            return xRange(d.x);
        })
        .attr('y', function (d) {
            return yRange(d.y);
        })
        .attr('width', xRange.rangeBand())
        .attr('height', function (d) {
            return ((HEIGHT - MARGINS.bottom) - yRange(d.y));
        })
//                    .attr('fill', 'grey')
        .style("fill", function(d) { return color(d.name); })
        .on('mouseover',function(d){
            d3.select(this)
                .attr('fill','blue');
        })
        .on('mouseout',function(d){
            d3.select(this)
                .attr('fill','grey');
        });

    function next()
    {
        message_count = message_count + 1;
//        alert("redrawing");
        return {
            x: "phase1",
//            y: Math.floor((Math.random() * 10) + 1)
            y:message_count
        };
    }


//    setInterval(function() {
//        barData.shift();
//        barData.push(next());
//        redraw();
//    }, 1500);

    function redraw(newData)
    {
        vis.selectAll("rect")
            .data(newData)
            .transition()
            .duration(1000)
//            .attr('width', xRange.rangeBand())
//            .attr("x", function(d,i){ return xRange(i)})
            .attr("y", function(d) { return yRange(d.y); })
            .attr("height", function(d) { return ((HEIGHT - MARGINS.bottom) - yRange(d.y)); });
    }

//}
