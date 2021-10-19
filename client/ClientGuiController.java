package com.javarush.task.task30.task3008.client;

/*
Осталось написать компонент контроллер (Controller):
1. Создай класс ClientGuiController унаследованный от Client.
2. Создай и инициализируй поле, отвечающее за модель ClientGuiModel model.
3. Создай и инициализируй поле, отвечающее за представление ClientGuiView view.
Подумай, что нужно передать в конструктор при инициализации объекта.
4. Добавь внутренний класс GuiSocketThread унаследованный от SocketThread.
Класс GuiSocketThread должен быть публичным.
В нем переопредели следующие методы:
а) void processIncomingMessage(String message) - должен устанавливать новое сообщение у модели и вызывать обновление вывода сообщений у представления.
б) void informAboutAddingNewUser(String userName) - должен добавлять нового пользователя в модель и вызывать обновление вывода пользователей у отображения.
в) void informAboutDeletingNewUser(String userName) - должен удалять пользователя из модели и вызывать обновление вывода пользователей у отображения.
г) void notifyConnectionStatusChanged(boolean clientConnected) - должен вызывать аналогичный метод у представления.
5. Переопредели методы в классе ClientGuiController:
а) SocketThread getSocketThread() - должен создавать и возвращать объект типа GuiSocketThread.
б) void run() - должен получать объект SocketThread через метод getSocketThread() и вызывать у него метод run().
Разберись, почему нет необходимости вызывать метод run() в отдельном потоке, как мы это делали для консольного клиента.
в) getServerAddress(), getServerPort(), getUserName().
Они должны вызывать одноименные методы из представления (view).
6. Реализуй метод ClientGuiModel getModel(), который должен возвращать модель.
7. Реализуй метод main(), который должен создавать новый объект ClientGuiController и вызывать у него метод run().
Запусти клиента с графическим окном, нескольких консольных клиентов и убедись, что все работает корректно.
 */

//компонент контроллер (Controller):
public class ClientGuiController extends Client {

   private ClientGuiModel model = new ClientGuiModel();//поле, отвечающее за модель

    private ClientGuiView view = new ClientGuiView(this);//поле, отвечающее за представление. Почему this????

    public class GuiSocketThread extends SocketThread {
        //должен устанавливать новое сообщение у модели и вызывать обновление вывода сообщений у представления.
        @Override
        protected void processIncomingMessage(String message) {
            model.setNewMessage(message);
            view.refreshMessages();
        }

        //должен добавлять нового пользователя в модель и вызывать обновление вывода пользователей у отображения.
        @Override
        protected void informAboutAddingNewUser(String userName) {
            model.addUser(userName);
            view.refreshUsers();
        }

        //должен удалять пользователя из модели и вызывать обновление вывода пользователей у отображения.
        @Override
        protected void informAboutDeletingNewUser(String userName) {
            model.deleteUser(userName);
            view.refreshUsers();
        }

        //должен вызывать аналогичный метод у представления.
        @Override
        protected void notifyConnectionStatusChanged(boolean clientConnected) {
//            super.notifyConnectionStatusChanged(clientConnected);
            view.notifyConnectionStatusChanged(clientConnected);
        }
    }//Закрываем class GuiSocketThread

    //должен создавать и возвращать объект типа GuiSocketThread
    @Override
    protected SocketThread getSocketThread() {
        return new GuiSocketThread();
    }

    //должен получать объект SocketThread через метод getSocketThread() и вызывать у него метод run().
    //Разберись, почему нет необходимости вызывать метод run() в отдельном потоке, как мы это делали для консольного
    // клиента.
    @Override
    public void run() {
        getSocketThread().run();
    }

    //должен вызывать одноименные методы из представления (view).
    @Override
    protected String getServerAddress() {
//        return super.getServerAddress();
        return view.getServerAddress();
    }

    //должен вызывать одноименные методы из представления (view).
    @Override
    protected int getServerPort() {
//        return super.getServerPort();
        return view.getServerPort();
    }

    //должен вызывать одноименные методы из представления (view).
    @Override
    protected String getUserName() {
//        return super.getUserName();
        return view.getUserName();
    }

    // должен возвращать модель
    public ClientGuiModel getModel() {
        return model;
    }

    // должен создавать новый объект ClientGuiController и вызывать у него метод run()
    public static void main(String[] args) {
        ClientGuiController clientGuiController = new ClientGuiController();
        clientGuiController.run();
    }
}
