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

                // 1. Adquirir todos los semáforos para evitar inconsistencias al contar o mover
                Proyecto1SistemasOperativos.mutexCPU.acquire();
                Proyecto1SistemasOperativos.mutexReady.acquire();
                Proyecto1SistemasOperativos.mutexBlocked.acquire();
                Proyecto1SistemasOperativos.mutexSuspended.acquire();

                // 2. Contar cuántos procesos hay actualmente en la RAM (Listos + Bloqueados + CPU)
                int enMemoria = Proyecto1SistemasOperativos.readyQueue.getSize() +
                                Proyecto1SistemasOperativos.blockedQueue.getSize() +
                                (Proyecto1SistemasOperativos.runningProcess != null ? 1 : 0);

                // 3. LÓGICA DE SWAP OUT (De RAM a Disco)
                // Si superamos el MAX_MEMORY, debemos expulsar procesos.
                while (enMemoria > Proyecto1SistemasOperativos.MAX_MEMORY) {
                    // Intentar sacar de Listos primero (al que tenga el deadline más lejano)
                    PCB victimaLista = Proyecto1SistemasOperativos.readyQueue.removeFarthestDeadline();
                    
                    if (victimaLista != null) {
                        victimaLista.setStatus("Listo-Suspend");
                        Proyecto1SistemasOperativos.readySuspendedQueue.addLast(victimaLista);
                        System.out.println("<<< SWAP OUT: " + victimaLista.getNombre() + " movido a Listo-Suspendido");
                        enMemoria--;
                    } else {
                        // Si la cola de Listos está vacía, sacamos de Bloqueados
                        PCB victimaBloqueada = Proyecto1SistemasOperativos.blockedQueue.removeFarthestDeadline();
                        if (victimaBloqueada != null) {
                            victimaBloqueada.setStatus("Bloqueado-Suspend");
                            Proyecto1SistemasOperativos.blockedSuspendedQueue.addLast(victimaBloqueada);
                            System.out.println("<<< SWAP OUT: " + victimaBloqueada.getNombre() + " movido a Bloqueado-Suspendido");
                            enMemoria--;
                        } else {
                            // Prevención de bucle infinito (solo queda el del CPU)
                            break; 
                        }
                    }
                }

                // 4. LÓGICA DE SWAP IN (De Disco a RAM)
                // Si hay espacio en RAM y hay procesos suspendidos esperando
                while (enMemoria < Proyecto1SistemasOperativos.MAX_MEMORY) {
                    // Damos prioridad a traer de vuelta a los Listos-Suspendidos
                    if (!Proyecto1SistemasOperativos.readySuspendedQueue.isEmpty()) {
                        PCB recuperado = Proyecto1SistemasOperativos.readySuspendedQueue.removeFirst();
                        recuperado.setStatus("Listo");
                        insertarSegunAlgoritmo(recuperado);
                        System.out.println(">>> SWAP IN: " + recuperado.getNombre() + " devuelto a Listos (RAM)");
                        enMemoria++;
                    } 
                    // Si no hay Listos-Suspendidos, traemos a los Bloqueados-Suspendidos
                    else if (!Proyecto1SistemasOperativos.blockedSuspendedQueue.isEmpty()) {
                        PCB recuperadoBloq = Proyecto1SistemasOperativos.blockedSuspendedQueue.removeFirst();
                        recuperadoBloq.setStatus("Bloqueado");
                        Proyecto1SistemasOperativos.blockedQueue.addLast(recuperadoBloq);
                        System.out.println(">>> SWAP IN: " + recuperadoBloq.getNombre() + " devuelto a Bloqueados (RAM)");
                        enMemoria++;
                    } 
                    // Si no hay nadie en estado suspendido, no hacemos nada más
                    else {
                        break; 
                    }
                }

                // 5. Liberar los semáforos
                Proyecto1SistemasOperativos.mutexSuspended.release();
                Proyecto1SistemasOperativos.mutexBlocked.release();
                Proyecto1SistemasOperativos.mutexReady.release();
                Proyecto1SistemasOperativos.mutexCPU.release();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    // Método de ayuda para reinsertar un proceso en la cola de Listos respetando el algoritmo actual
    private void insertarSegunAlgoritmo(PCB proceso) {
        switch (Proyecto1SistemasOperativos.algoritmoActual) {
            case PRIORIDAD -> Proyecto1SistemasOperativos.readyQueue.insertByPriority(proceso);
            case SRT -> Proyecto1SistemasOperativos.readyQueue.insertBySRT(proceso);
            case EDF -> Proyecto1SistemasOperativos.readyQueue.insertByDeadline(proceso);
            default -> Proyecto1SistemasOperativos.readyQueue.addLast(proceso);
        }
    }
}