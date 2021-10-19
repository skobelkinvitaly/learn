package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

/*
Клиент, в начале своей работы, должен запросить у пользователя адрес и порт сервера, подсоединиться к указанному адресу,
получить запрос имени от сервера, спросить имя у пользователя, отправить имя пользователя серверу, дождаться принятия
имени сервером. После этого клиент может обмениваться текстовыми сообщениями с сервером.
Обмен сообщениями будет происходить в двух параллельно работающих потоках.
Один будет заниматься чтением из консоли и отправкой прочитанного серверу,
а второй поток будет получать данные от сервера и выводить их в консоль.
 */
public class Client {

    protected Connection connection;

    //В дальнейшем оно будет устанавливаться в true, если клиент подсоединен к серверу или в false в противном случае
    private volatile boolean clientConnected;

    //Он будет отвечать за поток, устанавливающий сокетное соединение и читающий сообщения сервера.
    //Класс должен иметь публичный модификатор доступа.
    public class SocketThread extends Thread {
        //
        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message;
                //В цикле получать сообщения, используя соединение connection.
                message = connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST) {
                    //Если тип полученного сообщения NAME_REQUEST (сервер запросил имя), запросить ввод имени
                    // пользователя с помощью метода getUserName(), создать новое сообщение с типом MessageType.USER_NAME
                    // и введенным именем, отправить сообщение серверу.
                    connection.send(new Message(MessageType.USER_NAME, getUserName()));
                } else if (message.getType() == MessageType.NAME_ACCEPTED) {
                    /*
                    Если тип полученного сообщения MessageType.NAME_ACCEPTED (сервер принял имя), значит сервер принял
                    имя клиента, нужно об этом сообщить главному потоку, он этого очень ждет.
                    Сделай это с помощью метода notifyConnectionStatusChanged(), передав в него true.
                    После этого выйди из метода.
                     */
                    notifyConnectionStatusChanged(true);
                    break;
                } else {
                    //Если пришло сообщение с каким-либо другим типом, кинь исключение IOException("Unexpected MessageType").
                    throw new IOException("Unexpected MessageType");
                }
            }
        }//Закрываем метод clientHandShake

        //Этот метод будет реализовывать главный цикл обработки сообщений сервера
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                //Получи сообщение от сервера, используя соединение connection.
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    //Если это текстовое сообщение (тип MessageType.TEXT), обработай его с помощью метода processIncomingMessage().
                    processIncomingMessage(message.getData());
                } else if (message.getType() == MessageType.USER_ADDED) {
                    //Если это сообщение с типом MessageType.USER_ADDED, обработай его с помощью метода informAboutAddingNewUser().
                    informAboutAddingNewUser(message.getData());
                    //сли это сообщение с типом MessageType.USER_REMOVED, обработай его с помощью метода informAboutDeletingNewUser().
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());
                } else {
                    //Если клиент получил сообщение какого-либо другого типа, брось исключение IOException("Unexpected MessageType").
                    throw new IOException("Unexpected MessageType");
                }
            }
        }//Закрываем метод clientMainLoop

        //должен выводить текст message в консоль
        protected void processIncomingMessage(String message) {
            System.out.println(message);
        }//Закрываем метод processIncomingMessage

        //должен выводить в консоль информацию о том, что участник с именем userName присоединился к чату.
        protected void informAboutAddingNewUser(String userName) {
            System.out.println(userName + " подключился к чату");
        }//Закрываем метод informAboutAddingNewUser

        //должен выводить в консоль, что участник с именем userName покинул чат.
        protected void informAboutDeletingNewUser(String userName) {
            System.out.println(userName + " покинул чат");
        }//Закрываем метод informAboutDeletingNewUser

        //этот метод должен:
        //а) Устанавливать значение поля clientConnected внешнего объекта Client в соответствии с переданным параметром.
        //б) Оповещать (пробуждать ожидающий) основной поток класса Client.
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }//Закрываем метод notifyConnectionStatusChanged

        public void run() {
            try {
                //новый объект класса java.net.Socket, используя данные, полученные getServerAddress(),getServerPort().
                Socket socket = new Socket(getServerAddress(), getServerPort());
                //новый объект класса Connection
//                Connection connection = new Connection(socket);
                connection = new Connection(socket);
                clientHandshake();//метод, реализующий "рукопожатие" клиента с сервером
                clientMainLoop();//метод, реализующий основной цикл обработки сообщений сервера
            } catch (IOException e) {
                notifyConnectionStatusChanged(false);//сообщение главному потоку о проблеме
            } catch (ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);//сообщение главному потоку о проблеме
            }
        }
    }//Закрываем класс SocketThread

    //олжен запросить ввод адреса сервера у пользователя и вернуть введенное значение.
    //Адрес может быть строкой, содержащей ip, если клиент и сервер запущен на разных машинах или 'localhost',
    // если клиент и сервер работают на одной машине.
    protected String getServerAddress() {
        return ConsoleHelper.readString();
    }

    //Должен запрашивать ввод порта сервера и возвращать его
    protected int getServerPort() {
        return ConsoleHelper.readInt();
    }

    //должен запрашивать и возвращать имя пользователя
    protected String getUserName() {
        return ConsoleHelper.readString();
    }

    //в данной реализации клиента всегда должен возвращать true (мы всегда отправляем текст введенный в консоль).
    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    //должен создавать и возвращать новый объект класса SocketThread.
    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    //создает новое текстовое сообщение, используя переданный текст и отправляет его серверу через соединение connection.
    //Если во время отправки произошло исключение IOException, то необходимо вывести информацию об этом пользователю и
    // присвоить false полю clientConnected.
    protected void sendTextMessage(String text) {
        try {
            Message message = new Message(MessageType.TEXT, text);
            connection.send(message);
        } catch (IOException e) {
            clientConnected = false;
            e.printStackTrace();
        }
    }//Закрываем метод sendTextMessage

    /*
    Он должен создавать вспомогательный поток SocketThread, ожидать пока тот установит соединение с сервером,
    а после этого в цикле считывать сообщения с консоли и отправлять их серверу.
    Условием выхода из цикла будет отключение клиента или ввод пользователем команды 'exit'.
    Для информирования главного потока, что соединение установлено во вспомогательном потоке, используй методы wait() и
    notify() объекта класса Client
     */
    public void run() {
        //Создавать новый сокетный поток с помощью метода getSocketThread()
        SocketThread socketThread = getSocketThread();
        //Помечать созданный поток как daemon, это нужно для того, чтобы при выходе из программы вспомогательный
        // поток прервался автоматически.
        socketThread.setDaemon(true);
        //Запустить вспомогательный поток.
        socketThread.start();
        // Заставить текущий поток ожидать, пока он не получит нотификацию из другого потока.
        //Подсказка: используй wait() и синхронизацию на уровне объекта.
        //Если во время ожидания возникнет исключение, сообщи об этом пользователю и выйди из программы.
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                ConsoleHelper.writeMessage("Error");
            }
        }//Закрываем synchronized блок
        // После того, как поток дождался нотификации, проверь значение clientConnected.
        //Если оно true - выведи "Соединение установлено.
        //Для выхода наберите команду 'exit'.".
        //Если оно false - выведи "Произошла ошибка во время работы клиента.".
        while (clientConnected == true) {
            String text = "";
            text = ConsoleHelper.readString();
            //Считывай сообщения с консоли пока клиент подключен.
            //Если будет введена команда 'exit', то выйди из цикла.
            if (text == "exit") break;
            else {
                //После каждого считывания, если метод shouldSendTextFromConsole() возвращает true, отправь считанный
                // текст с помощью метода sendTextMessage().
                if (shouldSendTextFromConsole() == true) sendTextMessage(text);
            }
        }
    }// Закрываем метод run класса Client

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}// Закрываем класс Client
