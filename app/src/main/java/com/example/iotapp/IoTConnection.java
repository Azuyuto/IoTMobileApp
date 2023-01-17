package com.example.iotapp;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class IoTConnection {
    private String ip;
    private int port;
    private Socket socket;
    private PrintWriter out;

    public IoTConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void connect() throws IOException {
        InetAddress serverAddr = InetAddress.getByName(ip);
        socket = new Socket(serverAddr, port);
        out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
    }

    public void sendData(String data) {
        out.println(data);
    }

    public void close() throws IOException {
        out.close();
        socket.close();
    }
}