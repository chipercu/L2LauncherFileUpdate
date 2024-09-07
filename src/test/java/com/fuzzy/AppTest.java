package com.fuzzy;

import javafx.concurrent.Task;
import junit.framework.TestCase;


import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.namednumber.ProtocolFamily;

import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {


    public void testApp() {
        final int numberOfDevices;
        try {
            List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
            numberOfDevices = allDevs.size();
            CountDownLatch latch = new CountDownLatch(numberOfDevices);

            for (PcapNetworkInterface device : allDevs) {
                if (device.getName().equalsIgnoreCase("\\Device\\NPF_Loopback")){
                    Runnable task = () -> {
                        System.out.println("start Handle device");
                        // Настройка устройства для захвата
                        PcapHandle handle = null;
                        try {
                            handle = device.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
                        } catch (PcapNativeException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            handle.setFilter("tcp port 2106", BpfProgram.BpfCompileMode.OPTIMIZE);
                        } catch (PcapNativeException | NotOpenException e) {
                            throw new RuntimeException(e);
                        }
                        try {
                            handle.loop(-1, (PacketListener) packet -> {
                                try {
                                    byte[] data = packet.getRawData();
                                    Packet.Header header = packet.getHeader();
//                                System.out.println("Hex Data: " + byteArrayToHex(data));
                                    System.out.println(device.getName() + " :: " + Arrays.toString(data));
                                    // Проверяем, что пакет достаточно длинный для заголовка IP и UDP
                                    if (data.length >= 28) { // 20 байт для IP заголовка и 8 для UDP
                                        // IP заголовок начинается с 0 байта
                                        int udpHeaderStart = 14; // Заголовок Ethernet - 14 байт

                                        // Порты в заголовке UDP располагаются на следующих позициях
                                        int sourcePort = (data[udpHeaderStart] & 0xFF) << 8 | (data[udpHeaderStart + 1] & 0xFF);
                                        int destinationPort = (data[udpHeaderStart + 2] & 0xFF) << 8 | (data[udpHeaderStart + 3] & 0xFF);

//                                    System.out.println("Source Port: " + sourcePort);
//                                    System.out.println("Destination Port: " + destinationPort);
                                    } else {
                                        System.out.println("Packet too short to analyze.");
                                    }
                                    //                DatagramPacket datagramPacket = new DatagramPacket(data, data.length, InetAddress.getByName(targetHost), targetPort);
                                    //                socket.send(datagramPacket);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        } catch (PcapNativeException | InterruptedException | NotOpenException e) {
                            throw new RuntimeException(e);
                        }
                        handle.close();
                        latch.countDown();
                    };

                    new Thread(task).start();
                    device.getAddresses().forEach(pcapAddress -> System.out.println(pcapAddress.getAddress()));
                    System.out.println(device.getName() + " - " + device.getDescription());
                }


            }
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSendPacket() {
        String host = "127.0.0.1"; // Локальный адрес
        int port = 2106; // Порт, на который будем отправлять сообщение

        try (Socket socket = new Socket(host, port)) {
            OutputStream outputStream = socket.getOutputStream();
            String message = "Hello, this is a test message!";

            // Отправляем данные на сервер
            outputStream.write(message.getBytes());
            outputStream.flush();

            System.out.println("Сообщение отправлено: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String byteArrayToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xFF);
            if (hex.length() == 1) {
                hexString.append('0'); // Добавьте ведущий ноль при необходимости
            }
            hexString.append(hex);
            hexString.append(' '); // Добавить пробел для разделения байтов
        }
        return hexString.toString().trim(); // Удалите лишний пробел в конце
    }

    public void test2() throws UnknownHostException, PcapNativeException, NotOpenException {
//
//        PcapNetworkInterface nif = Pcaps.getDevByAddress(InetAddress.getLocalHost());
//
//        // Установите фильтр
//        String filter = "tcp and src host 127.0.0.1 and port 2106";
//        PcapHandle pcapHandle = nif.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 10);
//
//        // Добавьте фильтр
//        pcapHandle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
//
//        // Захват пакетов
//        PacketListener listener = packet -> {
//            System.out.println("Captured packet: " + packet);
//        };
//
//        pcapHandle.dispatch()
//        // Запустите захват
//        nif.getPacketDispatcher().addPacketListener(listener);
//        nif.getPacketDispatcher().start();

//        try {
//            // Получите список доступных сетевых интерфейсов
//            PcapNetworkInterface nif = PcapNetworkInterface.getNetworkInterfaces()
//                    .nextElement(); // Здесь можно выбрать нужный интерфейс
//
//            // Откройте интерфейс для захвата
//            int snapLen = 65536; // Максимальная длина пакета
//            PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
//            long timeout = 10; // Тайм-аут в миллисекундах
//
//            // Открытие интерфейса
//            NetworkInterfaceAddress address = nif.getAddresses().get(0); // Получение первого адреса
//            if (address.getAddress().getHostAddress().equals("127.0.0.1")) {
//                // Открытие для захвата
//                PcapHandle handle = nif.openLive(snapLen, mode, timeout, "jacket");
//
//                // Установка фильтра
//                String filter = "tcp and port 2106"; // Фильтр для захвата на порту 2106
//                handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
//
//                // Захватываем пакеты
//                PacketListener listener = packet -> {
//                    System.out.println("Captured packet: " + packet);
//                };
//
//                // Запускаем захват
//                handle.loop(-1, listener); // Бесконечно захватываем пакеты
//
//                // Закрываем интерфейс при завершении
//                handle.close();
//            } else {
//                System.out.println("Необходима локальная адресация (127.0.0.1)");
//            }
//
//        } catch (PcapNativeException | NotOpenException | FilterNotSupportedException e) {
//            e.printStackTrace();
//        }
    }


}
