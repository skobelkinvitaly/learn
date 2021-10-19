package com.javarush.task.task30.task3008;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;

/*
Класс соединения между клиентом и сервером.
Класс Connection будет выполнять роль обертки над классом java.net.Socket,
которая должна будет уметь сериализовать и десериализовать объекты типа Message в сокет.
Методы этого класса должны быть готовы к вызову из разных потоков.
 */
public class Connection implements Closeable {
    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;
//        try {
            out = new ObjectOutputStream(socket.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
            this.in = new ObjectInputStream(socket.getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /*
    Он должен записывать (сериализовать) сообщение message в ObjectOutputStream.
    Этот метод будет вызываться из нескольких потоков.
    Позаботься, чтобы запись в объект ObjectOutputStream была возможна только одним потоком в определенный момент времени,
    остальные желающие ждали завершения записи.
    При этом другие методы класса Connection не должны быть заблокированы.
     */
    public void send(Message message) throws IOException {
        synchronized (out) {
            out.writeObject(message);
        }
    }

    /*
    Он должен читать (десериализовать) данные из ObjectInputStream.
    Сделай так, чтобы операция чтения не могла быть одновременно вызвана несколькими потоками,
    при этом вызов других методы класса Connection не блокировать.
     */
    public Message receive() throws IOException, ClassNotFoundException {

        Message mes;
        synchronized (in) {
            mes = (Message) in.readObject();
        }
        return mes;
//        return null;
//        return new Message();
    }

    //возвращающий удаленный адрес сокетного соединения
    public SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

    //должен закрывать все ресурсы класса
    public void close() throws IOException {
        out.close();
        in.close();
        socket.close();
    }
}
