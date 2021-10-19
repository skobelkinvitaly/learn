package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static javafx.scene.input.KeyCode.X;

public class BotClient extends Client {
    //бота, который будет представлять собой клиента, который автоматически будет отвечать на некоторые команды.
    //Проще всего реализовать бота, который сможет отправлять текущее время или дату, когда его кто-то об этом попросит.
    //Внутренний класс
    public class BotSocketThread extends SocketThread {
        //Сейчас будем реализовывать класс BotSocketThread, вернее переопределять некоторые его методы, весь основной
        // функционал он уже унаследовал от SocketThread.
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        /*
        Переопредели метод processIncomingMessage(String message).
        Он должен следующим образом обрабатывать входящие сообщения:
        а) Вывести в консоль текст полученного сообщения message.
        б) Получить из message имя отправителя и текст сообщения. Они разделены ": ".
        в) Отправить ответ в зависимости от текста принятого сообщения.
        Если текст сообщения:
        "дата" - отправить сообщение содержащее текущую дату в формате "d.MM.YYYY";
        "день" - в формате "d";
        "месяц" - "MMMM";
        "год" - "YYYY";
        "время" - "H:mm:ss";
        "час" - "H";
        "минуты" - "m";
        "секунды" - "s".
        Указанный выше формат используй для создания объекта SimpleDateFormat. Для получения текущей даты необходимо
        использовать класс Calendar и метод getTime(). Ответ должен содержать имя клиента, который прислал запрос и
        ожидает ответ, например, если Боб отправил запрос "время", мы должны отправить ответ "Информация для Боб: 12:30:47".
         */
        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            if (!message.isEmpty() && message.contains(": ")) {
                String senderName = message.substring(0, message.trim().indexOf(":"));
                senderName = senderName.trim();
                String senderText = message.substring(message.trim().indexOf(":") + 1, message.length());
                senderText = senderText.trim();
                String arr[] = senderText.split(" ");
                String dateInformation = null;
                String formattedMessageText;
                SimpleDateFormat simpleDateFormat = null;
                Date date = Calendar.getInstance().getTime();
                switch (senderText) {
                    case "время":
                        simpleDateFormat = new SimpleDateFormat("H:mm:ss", Locale.ENGLISH);
                        dateInformation = simpleDateFormat.format(date);
                        formattedMessageText = "Информация для " + senderName + ": " + dateInformation;
                        BotClient.this.sendTextMessage(formattedMessageText);
                        break;
                    case "дата":
                        simpleDateFormat = new SimpleDateFormat("d.MM.YYYY", Locale.ENGLISH);
                        dateInformation = simpleDateFormat.format(date);
                        formattedMessageText = "Информация для " + senderName + ": " + dateInformation;
                        BotClient.this.sendTextMessage(formattedMessageText);
                        break;
                    case "день":
                        simpleDateFormat = new SimpleDateFormat("d", Locale.ENGLISH);
                        dateInformation = simpleDateFormat.format(date);
                        formattedMessageText = "Информация для " + senderName + ": " + dateInformation;
                        BotClient.this.sendTextMessage(formattedMessageText);
                        break;
                    case "месяц":
                        simpleDateFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
                        dateInformation = simpleDateFormat.format(date);
                        formattedMessageText = "Информация для " + senderName + ": " + dateInformation;
                        BotClient.this.sendTextMessage(formattedMessageText);
                        break;
                    case "час":
                        simpleDateFormat = new SimpleDateFormat("H", Locale.ENGLISH);
                        dateInformation = simpleDateFormat.format(date);
                        formattedMessageText = "Информация для " + senderName + ": " + dateInformation;
                        BotClient.this.sendTextMessage(formattedMessageText);
                        break;
                    case "минуты":
                        simpleDateFormat = new SimpleDateFormat("m", Locale.ENGLISH);
                        dateInformation = simpleDateFormat.format(date);
                        formattedMessageText = "Информация для " + senderName + ": " + dateInformation;
                        BotClient.this.sendTextMessage(formattedMessageText);
                        break;
                    case "секунды":
                        simpleDateFormat = new SimpleDateFormat("s");
                        dateInformation = simpleDateFormat.format(date);
                        formattedMessageText = "Информация для " + senderName + ": " + dateInformation;
                        BotClient.this.sendTextMessage(formattedMessageText);
                        break;
                    case "год":
                        simpleDateFormat = new SimpleDateFormat("YYYY");
                        dateInformation = simpleDateFormat.format(date);
                        formattedMessageText = "Информация для " + senderName + ": " + dateInformation;
                        BotClient.this.sendTextMessage(formattedMessageText);
                        break;
                    default:
                        return;
                }//Закрываем switch
            }//Закрываем if
        }//Закрываем метод
    }//Закрываем класс

    //Он должен создавать и возвращать объект класса BotSocketThread.
    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    //Он должен всегда возвращать false.
    //Мы не хотим, чтобы бот отправлял текст введенный в консоль.
    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    //метод должен генерировать новое имя бота
    @Override
    protected String getUserName() {
        int X = (int) (Math.random() * 100);
        return "date_bot_" + X;
    }

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
