/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataStructures;

/**
 *
 * @author Luigi
 */
public class Semaphore {
    private int value;

    public Semaphore(int initialValue) {
        this.value = initialValue;
    }

    public synchronized void acquire() {
        while (value <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        value--;
    }

    public synchronized void release() {
        value++;
        notify();
    }
}
