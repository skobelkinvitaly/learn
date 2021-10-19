package com.javarush.task.task30.task3008;
//отвечает за тип сообщений пересылаемых между клиентом и сервером
public enum MessageType {
    NAME_REQUEST,
    NAME_ACCEPTED,
    USER_NAME,
    TEXT,
    USER_ADDED,
    USER_REMOVED;
}
