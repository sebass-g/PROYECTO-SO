/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1.sistemasoperativos;

import DataStructures.PCB;

public class InterruptGenerator extends Thread {
    private boolean active = true;

    @Override
    public void run() {
        while (active) {
            try {
                // Esperar entre 10 y 25 segundos para el siguiente evento
                int waitTime = 10000 + (int) (Math.random() * 15001);
                Thread.sleep(waitTime);

                triggerEmergency();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void triggerEmergency() {
        // Bloqueamos los recursos necesarios con los semáforos estáticos
        Proyecto1SistemasOperativos.mutexCPU.acquire();
        Proyecto1SistemasOperativos.mutexReady.acquire();

        PCB running = Proyecto1SistemasOperativos.runningProcess;
        if (running != null) {
            System.out.println("\n[ALERTA] Evento asíncrono detectado: Micro-meteorito.");
            running.setStatus("Listo");
            
            // Usamos addFirst para que sea el primero en volver al despejarse la emergencia
            Proyecto1SistemasOperativos.readyQueue.addFirst(running);
            Proyecto1SistemasOperativos.runningProcess = null;
        }

        Proyecto1SistemasOperativos.mutexReady.release();
        Proyecto1SistemasOperativos.mutexCPU.release();
    }
}