package com.javarush.task.task30.task3008;
//основной класс сервера

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/*
Сервер должен поддерживать множество соединений с разными клиентами одновременно.
Это можно реализовать с помощью следующего алгоритма:

- Сервер создает серверное сокетное соединение.
- В цикле ожидает, когда какой-то клиент подключится к сокету.
- Создает новый поток обработчик Handler, в котором будет происходить обмен сообщениями с клиентом.
- Ожидает следующее соединение.
 */
public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    /*
    Класс Handler должен реализовывать протокол общения с клиентом.
Выделим из протокола отдельные этапы и реализуем их с помощью отдельных методов:

Этап первый - это этап рукопожатия (знакомства сервера с клиентом).
Реализуем его с помощью приватного метода String serverHandshake(Connection connection) throws IOException, ClassNotFoundException .
Метод в качестве параметра принимает соединение connection, а возвращает имя нового клиента.

Реализация метода должна:
1) Сформировать и отправить команду запроса имени пользователя
2) Получить ответ клиента
3) Проверить, что получена команда с именем пользователя
4) Достать из ответа имя, проверить, что оно не пустое и пользователь с таким именем еще не подключен (используй connectionMap)
5) Добавить нового пользователя и соединение с ним в connectionMap
6) Отправить клиенту команду информирующую, что его имя принято
7) Если какая-то проверка не прошла, заново запросить имя клиента
8) Вернуть принятое имя в качестве возвращаемого значения
     */
    private static class Handler extends Thread {

        Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            String client = "";
            Message mes = null;
            while (true) {
                connection.send(new Message(MessageType.NAME_REQUEST));
                mes = connection.receive();
                String name = mes.getData();
                if (mes.getType() == MessageType.USER_NAME) {
                    if (!name.isEmpty() & !connectionMap.containsKey(name)) {
                        connectionMap.put(name, connection);
                        client = name;
                        connection.send(new Message(MessageType.NAME_ACCEPTED));
                        break;
                    }
                }
            }
            return client;
        }//Закрываем метод serverHandShake

        //Отправка клиенту (новому участнику) информации об остальных клиентах (участниках) чата
        private void notifyUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()) {
                if (!entry.getKey().equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, entry.getKey()));
                }
            }
        }//Закрываем метод notifyUsers

        //Главный цикл обработки сообщений сервером
        /*
        Он должен:
1. Принимать сообщение клиента
2. Если принятое сообщение - это текст (тип TEXT), то формировать новое текстовое сообщение путем конкатенации: имени клиента, двоеточия, пробела и текста сообщения.
Например, если мы получили сообщение с текстом "привет чат" от пользователя "Боб", то нужно сформировать сообщение "Боб: привет чат".
3. Отправлять сформированное сообщение всем клиентам с помощью метода sendBroadcastMessage().
4. Если принятое сообщение не является текстом, вывести сообщение об ошибке
5. Организовать бесконечный цикл, внутрь которого перенести функционал пунктов 10.1-10.4.
         */
        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message mes = connection.receive();
                if (mes.getType() == MessageType.TEXT) {
                    String text = userName + ":" + " " + mes.getData();
                    sendBroadcastMessage(new Message(MessageType.TEXT, text));
                } else {
                    ConsoleHelper.writeMessage("Error");
                }
            }
        }//Закрываем serverMainLoop

        //Т.к. сервер может одновременно работать с несколькими клиентами, нам понадобится метод для отправки сообщения сразу всем.
        //Главный метод класса Handler, который будет вызывать все вспомогательные методы
        public void run() {
            String username = null;
            String address = socket.getRemoteSocketAddress().toString();
            /*
            Вывод сообщения, что установлено новое соединение с удаленным адресом,
            который можно получить с помощью метода getRemoteSocketAddress().
             */
            ConsoleHelper.writeMessage(address);
            try (Connection connection = new Connection(socket)) {
                //Вызов метода, реализующего рукопожатие с клиентом, сохраняя имя нового клиента.
                username = serverHandshake(connection);
                //Рассылка всем участникам чата информацию об имени присоединившегося участника (сообщение с типом USER_ADDED).
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, username));
                //Сообщение новому участнику о существующих участниках.
                notifyUsers(connection, username);
                //Запуск главного цикла обработки сообщений сервером.
                serverMainLoop(connection, username);
            } catch (IOException | ClassNotFoundException exception) {
                ConsoleHelper.writeMessage("Ошибка");
            }
            /*
            После того как все исключения обработаны, если п.11.3 отработал и возвратил нам имя, мы должны удалить
            запись для этого имени из connectionMap и разослать всем остальным участникам сообщение с типом USER_REMOVED
            и сохраненным именем.
             */
            for (Map.Entry<String, Connection> entry : connectionMap.entrySet()
            ) {
                if (username != null && entry.getKey().equals(username)) {
                    connectionMap.remove(username);
                    try {
                        sendBroadcastMessage(new Message(MessageType.USER_REMOVED, username));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //Последнее, что нужно сделать в методе run() - вывести сообщение, информирующее что соединение с
                //удаленным адресом закрыто. - Не делал.
            }
//        }
    }//Закрываем run

}// Закрываем класс Handler

    public static void sendBroadcastMessage(Message message) throws IOException {
        for (String name : connectionMap.keySet()) {
            try {
                connectionMap.get(name).send(message);
            } catch (IOException e) {
                ConsoleHelper.writeMessage(String.format("Can't send the message to %s", name));
            }
        }
    }//Закрываем метод SendBroadCastMessage

    public static void main(String[] args) throws IOException {
        int port = ConsoleHelper.readInt();
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("cервер запущен");
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Handler handler = new Handler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            serverSocket.close();
            System.out.println("ошибка");

        }
    }//Закрываем Main
}//Закрываем класс Server
