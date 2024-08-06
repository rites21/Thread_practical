package org.example.NumberPrinter;

public class Main {
    public static void main(String[] args) {
        //with normal
        for(int i = 1; i <= 10000; i++){
//            if(i == 40) {
                System.out.println("Ritesh");
//            }
            Thread t = new Thread(new NumberPrinter(i));
            t.start();
        }

        //with lambda expression no need to create numberprinter class
//        for(int i = 1; i <= 10000; i++){
//            if(i == 40) {
//                System.out.println("Ritesh");
//            }
//            int number = i;
//            Thread t = new Thread(() -> System.out.println(number+" "+Thread.currentThread().getName()));
//            t.start();
//        }
    }
}
