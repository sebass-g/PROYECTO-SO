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

    // Bloquea si no hay recursos (Mantiene tu lógica original)
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

    // Libera un recurso (Mantiene tu lógica original)
    public synchronized void release() {
        value++;
        notify();
    }
    
    // Intenta tomar el recurso. Si está ocupado, retorna false inmediatamente (NO BLOQUEA)
    public synchronized boolean tryAcquire() {
        if (value > 0) {
            value--;
            return true;
        }
        return false;
    }
}