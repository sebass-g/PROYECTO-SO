/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1.sistemasoperativos;

import DataStructures.PCB;

/**
 *
 * @author Luigi Lauricella & Sebastian Gonzalez
 */
public class ManejadorES extends Thread {
    private PCB proceso;

    public ManejadorES(PCB proceso) {
        this.proceso = proceso;
    }

    @Override
    public void run() {
        try {
            // Simulamos el tiempo de bloqueo (Ciclos * 200ms de velocidad del ciclo actual)
            int tiempoEspera = proceso.getCiclosParaSatisfacerExcepcion() * 200; 
            Thread.sleep(tiempoEspera);

            // Una vez terminada la E/S, lo sacamos de la cola de bloqueados
            Proyecto1SistemasOperativos.mutexBlocked.acquire();
            Proyecto1SistemasOperativos.blockedQueue.remove(proceso); 
            Proyecto1SistemasOperativos.mutexBlocked.release();

            // Cambiamos su estado
            proceso.setStatus("Listo");
            System.out.println("<<< EXCEPCIÓN SATISFECHA: " + proceso.getNombre() + " vuelve a Listos");

            // Lo regresamos inteligentemente a la cola de listos según el algoritmo actual
            Proyecto1SistemasOperativos.mutexReady.acquire();
            switch (Proyecto1SistemasOperativos.algoritmoActual) {
                case PRIORIDAD -> Proyecto1SistemasOperativos.readyQueue.insertByPriority(proceso);
                case SRT -> Proyecto1SistemasOperativos.readyQueue.insertBySRT(proceso);
                case EDF -> Proyecto1SistemasOperativos.readyQueue.insertByDeadline(proceso);
                default -> Proyecto1SistemasOperativos.readyQueue.addLast(proceso);
            }
            Proyecto1SistemasOperativos.mutexReady.release();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
