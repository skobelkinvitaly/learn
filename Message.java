package com.javarush.task.task30.task3008;

import java.io.Serializable;

//класс, отвечающий за пересылаемые сообщения
public class Message implements Serializable {
    private final MessageType type; //будет содержать тип сообщения
    private final String data;      //будет содержать данные сообщения


    public MessageType getType() {
        return type;
    }

    public String getData() {
        return data;
    }


    public Message(MessageType messageType, String data) {
        this.type = messageType;
        this.data = data;
    }

    public Message(MessageType messageType) {
        this.type = messageType;
        this.data = null;
    }



}
