/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package DataStructures;

import com.mycompany.proyecto1.sistemasoperativos.Proyecto1SistemasOperativos;

/**
 *
 * @author Luigi
 */

public class Clock extends Thread {
    private int duration;
    private boolean running;
    private final Object lock;

    public Clock(int duration, Object lock) {
        this.duration = duration;
        this.lock = lock;
        this.running = true;
    }
    
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(duration);
                synchronized (lock) {
                    Proyecto1SistemasOperativos.globalClock++;
                    executeCycle();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void executeCycle() {
        PCB p = Proyecto1SistemasOperativos.runningProcess;
        if (p != null) {
            p.setPc(p.getPc() + 1);
            p.setMar(p.getMar() + 1);
            p.setInstruccionesEjecutadas(p.getInstruccionesEjecutadas() + 1);

            if (p.getDeadline() > 0) {
                p.setDeadline(p.getDeadline() - 1);
            }
            
            if (p.getInstruccionesEjecutadas() >= p.getInstruccionesTotales()) {
                p.setStatus("Terminado");
            }
        }
    }

    public void setDuration(int duration) { this.duration = duration; }
}