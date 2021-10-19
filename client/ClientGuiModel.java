package com.javarush.task.task30.task3008.client;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
Консольный клиент мы уже реализовали, чат бота тоже сделали, почему бы не сделать клиента с графическим интерфейсом?
Он будет так же работать с нашим сервером, но иметь графическое окно, кнопки и т.д.
Итак, приступим. При написании графического клиента будет очень уместно воспользоваться паттерном MVC (Model-View-Controller).
Ты уже должен был с ним сталкиваться, если необходимо, освежи свои знания про MVC с помощью Интернет.
В нашей задаче самая простая реализация будет у класса, отвечающего за модель (Model).
 */

public class ClientGuiModel {
    private String newMessage = null;// котором будет храниться новое сообщение, которое получил клиент.

    private final Set<String> allUserNames = new HashSet<>();//В нем будет храниться список всех участников чата

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    //геттер для allUserNames, запретив модифицировать возвращенное множество
    public Set<String> getAllUserNames() {
        return Collections.unmodifiableSet(allUserNames);
    }

    //должен добавлять имя участника во множество, хранящее всех участников.
    public void addUser(String newUserName) {
        allUserNames.add(newUserName);
    }

    //будет удалять имя участника из множества
    public void deleteUser(String userName) {
        allUserNames.remove(userName);
    }
}
