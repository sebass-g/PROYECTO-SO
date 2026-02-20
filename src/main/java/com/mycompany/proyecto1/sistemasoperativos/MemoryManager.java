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
public class MemoryManager extends Thread {
    private boolean active = true;

    @Override
    public void run() {
        while (active) {
            try {
                // El vigilante de memoria revisa cada 500ms
                Thread.sleep(500); 

                // Bloquear los semáforos para contar de forma segura
                Proyecto1SistemasOperativos.mutexReady.acquire();
                Proyecto1SistemasOperativos.mutexBlocked.acquire();
                Proyecto1SistemasOperativos.mutexSuspended.acquire();
                Proyecto1SistemasOperativos.mutexCPU.acquire();

                // 1. Contar cuántos hay en la RAM (Listos + Bloqueados + CPU)
                int enMemoria = Proyecto1SistemasOperativos.readyQueue.getSize() +
                                Proyecto1SistemasOperativos.blockedQueue.getSize() +
                                (Proyecto1SistemasOperativos.runningProcess != null ? 1 : 0);

                // 2. LÓGICA DE SWAP OUT (Si sobrepasamos el límite)
                while (enMemoria > Proyecto1SistemasOperativos.MAX_MEMORY) {
                    // Sacamos al menos urgente de la cola de listos
                    PCB victima = Proyecto1SistemasOperativos.readyQueue.removeFarthestDeadline();
                    
                    if (victima != null) {
                        victima.setStatus("Listo-Suspend");
                        Proyecto1SistemasOperativos.readySuspendedQueue.addLast(victima);
                        System.out.println("<<< SWAP OUT: " + victima.getNombre() + " a Suspendidos");
                        enMemoria--;
                    } else {
                        break; // Proteccion infinita
                    }
                }

                // 3. LÓGICA DE SWAP IN (Si hay espacio en RAM y gente en Suspendidos)
                while (enMemoria < Proyecto1SistemasOperativos.MAX_MEMORY && 
                       (!Proyecto1SistemasOperativos.readySuspendedQueue.isEmpty() || 
                        !Proyecto1SistemasOperativos.blockedSuspendedQueue.isEmpty())) {
                    
                    // Prioridad a regresar los listos
                    if (!Proyecto1SistemasOperativos.readySuspendedQueue.isEmpty()) {
                        PCB recuperado = Proyecto1SistemasOperativos.readySuspendedQueue.removeFirst();
                        recuperado.setStatus("Listo");
                        
                        // Lo insertamos inteligentemente según el algoritmo actual
                        switch (Proyecto1SistemasOperativos.algoritmoActual) {
                            case PRIORIDAD -> Proyecto1SistemasOperativos.readyQueue.insertByPriority(recuperado);
                            case SRT -> Proyecto1SistemasOperativos.readyQueue.insertBySRT(recuperado);
                            case EDF -> Proyecto1SistemasOperativos.readyQueue.insertByDeadline(recuperado);
                            default -> Proyecto1SistemasOperativos.readyQueue.addLast(recuperado);
                        }
                        
                        System.out.println(">>> SWAP IN: " + recuperado.getNombre() + " a RAM (Listos)");
                        enMemoria++;
                    } 
                }

                // Liberar los semáforos
                Proyecto1SistemasOperativos.mutexCPU.release();
                Proyecto1SistemasOperativos.mutexSuspended.release();
                Proyecto1SistemasOperativos.mutexBlocked.release();
                Proyecto1SistemasOperativos.mutexReady.release();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
