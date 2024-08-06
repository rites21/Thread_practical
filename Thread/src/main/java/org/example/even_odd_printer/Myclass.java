package org.example.even_odd_printer;

public class Myclass {
    static int N;
    int count = 0;

    public void printEven(){
        synchronized (this) {
            while (count < N) {
                while (count % 2 == 0) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(count + " "+Thread.currentThread().getName() );
                count++;
                notify();
            }
        }
    }

    public void oddPrinter(){

        synchronized (this) {
            while (count < N) {
                while (count % 2 == 1) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                System.out.println(count + " "+Thread.currentThread().getName());
                count++;
                notify();
            }
        }
    }

    public static void main(String[] args) {
        N  = 10;
        Myclass myclass = new Myclass();
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                myclass.printEven();
            }
        });

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                myclass.oddPrinter();
            }
        });

        t1.start();
        t2.start();
    }
}
