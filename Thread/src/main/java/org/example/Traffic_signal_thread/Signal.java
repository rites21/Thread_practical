package org.example.Traffic_signal_thread;

public class Signal implements Runnable{
    private Color currentcol;
    public Signal(){
        currentcol = Color.RED;
    }


    @Override
    public void run() {
        while (true){
            switch (currentcol){
                case RED:
                    System.out.println("Traffic signal is red : Stop!");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    currentcol = Color.GREEN;
                    break;
                case GREEN:
                    System.out.println("Traffic signal is Green : Go!");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    currentcol = Color.ORANGE;
                    break;
                case ORANGE:
                    System.out.println("Traffic signal is yellow : ready to stop!");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    currentcol = Color.RED;
                    break;
            }
        }
    }
}
