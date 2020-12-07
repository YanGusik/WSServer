package com.company;

import com.google.gson.Gson;
import models.Frame;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import utils.WrapHelper;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Config.CMD_MSG;
import static utils.Config.CMD_REG_USER;

public class ChatServer extends WebSocketServer
{
    private final Map<String, WebSocket> clients = new ConcurrentHashMap<>();

    public ChatServer(int port)
    {
        super(new InetSocketAddress(port));
    }

    public ChatServer(InetSocketAddress address)
    {
        super(address);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String uniqueID = UUID.randomUUID().toString();
        clients.put(uniqueID, conn);

        conn.send(WrapHelper.regToJson(uniqueID));
        conn.send(WrapHelper.resToJson("Welcome to the server!"));


        broadcast((WrapHelper.resToJson("new connection: " + handshake.getResourceDescriptor()))); //This method sends a message to all clients connected
        System.out.println(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " entered the room!");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        broadcast(WrapHelper.resToJson(conn + " has left the room!"));
        System.out.println(conn + " has left the room!");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Gson gson = new Gson();
        final Frame frame = gson.fromJson(message, Frame.class);
        if (frame != null) {
            System.out.println("frame: " + frame);
            if (!clients.containsKey(frame.getUserData().getId()))
                conn.close();
            switch (frame.getCmd()) {
                case CMD_MSG:
                    broadcast(message);

                    break;

                case CMD_REG_USER:

                    break;
            }
        }
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer message) {
        broadcast(message.array());
        System.out.println(conn + ": " + message);
    }

    @Override
    public void onError(WebSocket conn, Exception ex)
    {
        ex.printStackTrace();
        if (conn != null) {
            // some errors like port binding failed may not be assignable to a specific websocket
        }
    }

    @Override
    public void onStart()
    {
        System.out.println("Server started!");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }
}