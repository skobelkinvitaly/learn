package com.javarush.task.task30.task3008;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//вспомогательный класс, для чтения или записи в консоль
public class ConsoleHelper {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    //должен выводить сообщение message в консоль
    public static void writeMessage(String message) {
        System.out.println(message);
    }

    //должен считывать строку с консоли
    public static String readString() {
      String s = "";
      while (true){
          try {
              s = bufferedReader.readLine();
              break;
          } catch (IOException e) {
              System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
          }
      }
        return s;
    }

    //должен возвращать введенное число
    public static int readInt(){
        int num = 0;
        try {
            num = Integer.parseInt(readString());
        } catch (NumberFormatException e) {
            System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            num = Integer.parseInt(readString());
        }
        return num;
    }
}
