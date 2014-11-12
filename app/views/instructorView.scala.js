@(username: String)
var barData = [{
    'x': "Informal Messages",
    'y': 5
}, {
    'x': "Hashtag Messages",
    'y': 20
}
];

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
            $('#messages').prepend(el)
        }



        // Update the members list
//        $("#members").html('')
//        $(data.members).each(function() {
//            var li = document.createElement('li');
//            li.textContent = this;
//            $("#members").append(li);
//        })
//        alert("receiving");
        barData.shift();
        barData.push(next());
        redraw();

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


        yRange = d3.scale.linear().range([HEIGHT - MARGINS.top, MARGINS.bottom]).domain([0,
            d3.max(barData, function (d) {
                return d.y;
            })
        ]),

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
//        alert("redrawing");
        return {
            x: "phase1",
            y: Math.floor((Math.random() * 10) + 1)
        };
    }


//    setInterval(function() {
//        barData.shift();
//        barData.push(next());
//        redraw();
//    }, 1500);

    function redraw()
    {
        vis.selectAll("rect")
            .data(barData)
            .transition()
            .duration(1000)
            //            .attr("x", function(d,i){ return xRange(i)})
            .attr("y", function(d) { return yRange(d.y); })
            .attr("height", function(d) { return ((HEIGHT - MARGINS.bottom) - yRange(d.y)); });
    }

//}
