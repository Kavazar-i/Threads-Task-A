package com.example.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static volatile boolean flag = false;

    public static void main(String[] args) {
//        task1();
//        task2();
//        task3();
        task4();
//        task5();
    }

    public static void task1() {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Child thread: " + i);
            }
        });

        thread.start();

        for (int i = 0; i < 5; i++) {
            System.out.println("Main thread: " + i);
        }
    }

    public static void task2() {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("Child thread: " + i);
            }
        });

        thread.start();

        try {
            thread.join();   //TODO: глянуть внутрь, там Throws: InterruptedException
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 5; i++) {
            System.out.println("Main thread: " + i);
        }
    }

    public static void task3() {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        String[] messages = {"Message A", "Message B", "Message C", "Message D"};

        for (String message : messages) {
            executor.submit(() -> printMessages(message));
        }

        executor.shutdown();
    }

    private static void printMessages(String message) {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + message);
        }
    }

    public static void task4() { // FIXME: не работает
        int M = 100; // Задержка в миллисекундах
        int K = 1000; // Время обратного отсчета
        int countdownSteps = 10 ; // Количество шагов обратного отсчета

        // Поток Producer
        Thread producer = new Thread(() -> {
            try {
                while (true) {
                    flag = true; // Переключение в true
                    System.out.println("Producer: " + flag);
                    TimeUnit.MILLISECONDS.sleep(M); // Задержка M миллисекунд

                    flag = false; // Переключение в false
                    System.out.println("Producer: " + flag);
                    TimeUnit.MILLISECONDS.sleep(M); // Задержка M миллисекунд
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Поток Consumer
        Thread consumer = new Thread(() -> {
            int countdown = countdownSteps; // Инициализация обратного отсчета

            try {
                while (true) {
                    if (flag) { // Ожидаем, пока состояние станет true
                        for (int i = countdown; i >= 0; i--) {
                            System.out.println("Countdown: " + i);
                            TimeUnit.MILLISECONDS.sleep(M / 10); // Задержка M/10 миллисекунд

                            if (!flag) { // Если состояние снова стало false, сохраняем текущее значение и выходим
//                                countdown = i; // Сохранение оставшихся шагов
                                System.out.println("Consumer: paused");
                                break;
                            }

                            if (i == 0) { // Завершаем работу, когда отсчет достигает 0
                                System.out.println("Consumer: Countdown finished, exiting.");
                                return; // Завершаем поток Consumer
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }

    /**
     * InterruptedException возникает, когда поток, находящийся в состоянии ожидания или сна,
     * прерывается другим потоком через вызов метода interrupt().
     * В примере выше главный поток вызывает метод interrupt() для порожденного потока,
     * что вызывает выброс исключения и прерывает выполнение операции sleep() в потоке.
     */
    public static void task5() {
        Thread thread = new Thread(() -> {
            try {
                System.out.println("Thread started");
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                System.out.println("Thread was interrupted");
            }
        });

        thread.start();

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        thread.interrupt(); // Прерывание потока
    }
}
