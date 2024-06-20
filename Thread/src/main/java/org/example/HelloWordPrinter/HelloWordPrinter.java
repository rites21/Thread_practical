package org.example.HelloWordPrinter;

public class HelloWordPrinter implements Runnable{

    private void doSomething(){
        System.out.println("Hi From HWP "+ Thread.currentThread().getName());
    }
    @Override
    public void run() {
        System.out.println("Hello Word From HWP "+Thread.currentThread().getName());
        doSomething();
    }
}
