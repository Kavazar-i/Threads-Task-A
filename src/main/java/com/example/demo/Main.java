package com.example.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    private static volatile boolean flag = false;

    public static void main(String[] args) {
        System.out.println("Hello, World!");
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

    public static void task4() {
        Thread producer = new Thread(() -> {
            try {
                while (true) {
                    flag = !flag;
                    System.out.println("Producer: " + flag);
                    TimeUnit.MILLISECONDS.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    if (flag) {
                        for (int i = 10; i >= 0; i--) {
                            System.out.println("Countdown: " + i);
                            TimeUnit.MILLISECONDS.sleep(10);
                            if (!flag) break;
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
