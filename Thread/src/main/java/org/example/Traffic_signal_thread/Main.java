package org.example.Traffic_signal_thread;

public class Main {
    public static void main(String[] args) {
        Thread t1 = new Thread(new Signal());
        t1.start();
    }
}
