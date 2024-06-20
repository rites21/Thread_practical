package org.example.NumberprinterThreadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
//        ExecutorService executorService1 = Executors.newCachedThreadPool(11);
        for(int i = 1; i <=10000; i++){
            if(i == 800){
                System.out.println("Ritesh");
            }
            NumberPrinter np = new NumberPrinter(i);
//            Thread t = new Thread(np);
//            t.start();

            executorService.execute(np);
        }
    }
}
