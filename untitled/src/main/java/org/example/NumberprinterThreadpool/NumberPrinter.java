package org.example.NumberprinterThreadpool;

public class NumberPrinter implements Runnable {
    int numberToPrint;
    public NumberPrinter(int numberToPrint){
        this.numberToPrint = numberToPrint;
    }
    @Override
    public void run() {
        System.out.println("Printing " + numberToPrint + " from "+ Thread.currentThread().getName());
    }
}
