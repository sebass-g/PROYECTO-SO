/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1.sistemasoperativos;

import DataStructures.PCB;

/**
 *
 * @author Luigi
 */
public class Scheduler extends Thread {
    private boolean active = true;

    @Override
    public void run() {
        while (active) {
            try {
                // El planificador revisa la CPU constantemente
                Thread.sleep(100); // Revisa cada 100ms para no saturar el procesador

                if (Proyecto1SistemasOperativos.runningProcess == null) {
                    despacharProceso();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void despacharProceso() {
        // 1. Bloqueamos las colas necesarias
        Proyecto1SistemasOperativos.mutexReady.acquire();
        Proyecto1SistemasOperativos.mutexCPU.acquire();

        // 2. Intentamos sacar el siguiente proceso de la cola
        PCB proximo = Proyecto1SistemasOperativos.readyQueue.removeFirst();

        if (proximo != null) {
            proximo.setStatus("Ejecución");
            Proyecto1SistemasOperativos.runningProcess = proximo;
            System.out.println("[SCHEDULER] Despachando: " + proximo.getNombre() + " a la CPU.");
        }

        // 3. Liberamos
        Proyecto1SistemasOperativos.mutexCPU.release();
        Proyecto1SistemasOperativos.mutexReady.release();
    }
}
