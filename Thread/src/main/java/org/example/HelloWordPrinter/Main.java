package org.example.HelloWordPrinter;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World from main " + Thread.currentThread().getName());
        Thread t = new Thread(new HelloWordPrinter());
        t.start();
        System.out.println("Hi From Mian "+Thread.currentThread().getName());
    }
}
