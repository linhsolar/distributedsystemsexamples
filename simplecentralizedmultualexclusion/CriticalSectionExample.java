package at.ac.tuwien.dsg.dsexamples;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Random;

/* 
 * This simple class for showing the execution of a critical section with multiple threads
 *  A primitive illustrative example for distributed systems course
 *  No strong check on errors and no  rigorous software engineering requirements  (e.g., controling input parameters, exception handling, etc.)
 *  Written by Linh Truong
 */ 
public class CriticalSectionExample {

    private String id = null;

    public CriticalSectionExample(String id) {
        super();
        this.id = id;
    }
    private static Lock lock = new ReentrantLock();

    public void criticalSection() {
        System.out.println(id+ ": This is a critical section: access only with permission");
        System.out.println("======== I am " + id + " Waiting for the lock===========");
        lock.lock();
        System.out.println("I am " + id + " I got the lock now");
        System.out.println(id + " doing some work  ");
        try {
            Thread.sleep((new Random()).nextInt(5000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("======== I am " + id + " releasing the lock=============");
        lock.unlock();

    }

    public static void main(String[] args) {
        System.out.println("Several threads accessing the same critical section");
        int i = 0;
        for (i = 0; i < Integer.valueOf(args[0]); i++) {
            final CriticalSectionExample exs = new CriticalSectionExample(String.valueOf(i));
            new Thread(new Runnable() {
                public void run() {
                    exs.criticalSection();
                }
            }).start();
        }
    }
}
