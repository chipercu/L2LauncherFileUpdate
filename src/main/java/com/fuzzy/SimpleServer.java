package com.fuzzy;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SimpleServer {
    public static void main(String[] args) {
        int port = 2106; // Порт для сервера

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер запущен. Ожидание подключения...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept()) {
                    System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
                    InputStream inputStream = clientSocket.getInputStream();
                    byte[] buffer = new byte[65536];
                    int bytesRead = inputStream.read(buffer);

                    if (bytesRead != -1) {
                        String message = new String(buffer, 0, bytesRead);
                        System.out.println("Получено сообщение: " + message);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}