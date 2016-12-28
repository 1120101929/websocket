package cn.lym.controller;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuyimin on 2016/12/27.
 */
@ServerEndpoint(value = "/websocket/chat")
public class ChatEntpoint {
    private static final String GUEST_PREFIX = "访客";
    private static final AtomicInteger connectionIds = new AtomicInteger(0);
    /**
     * 定义一个集合，用于保存所有接入的WebSocket客户端
     */
    private static final Set<ChatEntpoint> clientSet = new CopyOnWriteArraySet<>();
    /**
     * 定义一个成员变量，记录WebSocket客户端的聊天昵称
     */
    private final String nickname;
    /**
     * 定义一个成员变量，记录与WebSocket之间的会话
     */
    private Session session;

    public ChatEntpoint() {
        this.nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }

    /**
     * 实现广播消息的工具方法
     *
     * @param msg
     */
    private static void broadcast(String msg) {
        for (ChatEntpoint client : clientSet) {
            try {
                synchronized (client) {
                    //发送消息
                    client.session.getBasicRemote().sendText(msg);
                }
            } catch (Exception e) {
                System.out.println("聊天错误，向客户端 " + client + " 发送消息出现错误。");
                clientSet.remove(client);
                try {
                    client.session.close();
                } catch (IOException e1) {
                }
                String message = String.format("【%s %s】", client.nickname, "已经被断开了连接！");
                broadcast(message);
            }
        }
    }

    /**
     * 定义一个工具方法，用于对字符串中的 HTML 字符标签进行转义
     *
     * @param message
     * @return
     */
    private static String filter(String message) {
        if (message == null) {
            return null;
        }

        char[] content = new char[message.length()];
        message.getChars(0, message.length(), content, 0);
        StringBuilder result = new StringBuilder(content.length + 50);
        for (int i = 0; i < content.length; i++) {
            //控制对尖括号等特殊字符进行转义
            switch (content[i]) {
                case '<':
                    result.append("&lt;");
                    break;
                case '>':
                    result.append("&gt;");
                    break;
                case '&':
                    result.append("&amp;");
                    break;
                case '"':
                    result.append("&quot;");
                    break;
                default:
                    result.append(content[i]);
            }
        }
        return result.toString();
    }

    /**
     * 当客户端连接进来时，自动激发该方法
     *
     * @param session
     */
    @OnOpen
    public void start(Session session) {
        this.session = session;
        clientSet.add(this);
        String message = String.format("【%s %s】", this.nickname, "加入了聊天室！");
        broadcast(message);
    }

    /**
     * 当客户端断开连接时，自动激发该方法
     */
    @OnClose
    public void close() {
        clientSet.remove(this);
        String message = String.format("【%s %s】", this.nickname, "离开了聊天室！");
        broadcast(message);
    }

    /**
     * 每当收到客户端消息时，自动激发该方法
     *
     * @param message
     */
    @OnMessage
    public void incoming(String message) {
        String filteredMessage = String.format("%s：%s", this.nickname, filter(message));
        broadcast(filteredMessage);
    }

    /**
     * 当客户端通信出现错误时，激发该方法
     *
     * @throws Throwable
     */
    @OnError
    public void onError(Throwable t) throws Throwable {
        System.out.println("WebSocket 服务端错误：" + t);
    }
}
