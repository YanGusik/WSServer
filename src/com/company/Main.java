package com.company;

import javax.websocket.server.*;

public class Main {

    public static void main(String[] args) {
	    ChatServer server = new ChatServer(8080);
	    server.start();
    }
}
