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

    // Constructor simplificado: solo recibe la duración
    public Clock(int duration) {
        this.duration = duration;
        this.running = true;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(duration);
                
                // Usamos los semáforos estáticos de la clase principal
                Proyecto1SistemasOperativos.mutexClock.acquire();
                Proyecto1SistemasOperativos.globalClock++;
                Proyecto1SistemasOperativos.mutexClock.release();

                executeCycle();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void executeCycle() {
        Proyecto1SistemasOperativos.mutexCPU.acquire();
        PCB p = Proyecto1SistemasOperativos.runningProcess;
        if (p != null) {
            p.setPc(p.getPc() + 1);
            p.setMar(p.getMar() + 1);
            p.setInstruccionesEjecutadas(p.getInstruccionesEjecutadas() + 1);

            if (p.getDeadline() > 0) {
                p.setDeadline(p.getDeadline() - 1);
            }
        }
        Proyecto1SistemasOperativos.mutexCPU.release();
    }
}