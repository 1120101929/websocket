<%--
  Created by IntelliJ IDEA.
  User: liuyimin
  Date: 2016/12/27
  Time: 21:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>使用 WebSocket 通信</title>
</head>
<body>
<div>
    <input type="text" size="80" id="msg" name="msg" placeholder="输入聊天内容"/>
    <input type="button" value="发送" id="sendBn" name="sendBn"/>
</div>
<div style="width: 600px; height: 240px; overflow-y: auto; border: 1px solid #333;" id="show">
</div>

<script src="jquery-3.1.1.js"></script>
<script type="text/javascript">
    //    //创建 WebSocket 对象
    //    var webSocket = new WebSocket("ws://127.0.0.1:8080/websocket/chat");
    //    var sendMsg = function () {
    //        var inputElement = document.getElementById("msg");
    //        //发送消息
    //        webSocket.send(inputElement.value);
    //        //清空单行文本框
    //        inputElement.value = "";
    //    };
    //
    //    var send = function (event) {
    //        if (event.keyCode == 13) {
    //            sendMsg();
    //        }
    //    };
    //
    //    webSocket.onopen = function () {
    //        console.log("websocket open");
    //    };
    //
    //    webSocket.onmessage = function (event) {
    //        console.log("message come");
    //        var show = document.getElementById("show");
    //        //接收并显示消息
    //        show.innerHTML += event.data + "<br/>"
    //        show.scrollTop = show.scrollHeight;
    //    };
    //
    //    document.getElementById("msg").onkeydown = function (event) {
    //        console.log("key down");
    //        if (event.keyCode == 13) {
    //            sendMsg();
    //        }
    //    };
    //    document.getElementById("sendBn").onclick = function () {
    //        console.log("btn click");
    //        var inputElement = document.getElementById("msg");
    //        //发送消息
    //        webSocket.send(inputElement.value);
    //        //清空单行文本框
    //        inputElement.value = "";
    //    };
    //
    //    webSocket.onclose = function () {
    //        //document.getElementById("msg").onkeydown = null;
    //        //document.getElementById("sendBn").onclick = null;
    //        console.log("WebSocket 已经被关闭。");
    //    };

    $(function () {
        var webSocket = new WebSocket("ws://127.0.0.1:8080/websocket/chat");
        webSocket.onopen = function () {
            console.log("on open");
        };

        webSocket.onmessage = function (event) {
            console.log("on message");
            $("#show").append("<div>" + event.data + "</div>");
            $("#show").scrollTop($("#show")[0].scrollHeight)
        };

        webSocket.onerror = function () {
            console.log("on error");
        }

        $("#msg").keydown(function (event) {
            console.log("on keydown");
            if (event.keyCode == 13) {
                sendMsg();
            }
        });

        $("#sendBn").click(function () {
            console.log("on click")
            sendMsg();
        });

        function sendMsg() {
            console.log("send message");
            var message = $("#msg").val();
            webSocket.send(message);
            $("#msg").val("");
        }
    });
</script>
</body>
</html>
