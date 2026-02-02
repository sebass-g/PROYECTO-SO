package com.mycompany.proyecto1.sistemasoperativos;

import DataStructures.List;
import DataStructures.PCB;

/**
 *
 * @author Luigi
 */
public class Scheduler extends Thread {
    private boolean active = true;
    private int contadorRR = 0; 

    @Override
    public void run() {
        while (active) {
            try {
                Thread.sleep(100); 

                // LÓGICA DE ROUND ROBIN
                // Nota el uso de "Proyecto1SistemasOperativos." antes de las variables
                if (Proyecto1SistemasOperativos.algoritmoActual == Proyecto1SistemasOperativos.Algoritmo.ROUND_ROBIN) {
                    
                    Proyecto1SistemasOperativos.mutexCPU.acquire();
                    if (Proyecto1SistemasOperativos.runningProcess != null) {
                        contadorRR++;
                        if (contadorRR >= Proyecto1SistemasOperativos.quantum) {
                            System.out.println("--- Fin de Quantum RR ---");
                            
                            PCB procesoSaliente = Proyecto1SistemasOperativos.runningProcess;
                            procesoSaliente.setStatus("Listo");
                            
                            Proyecto1SistemasOperativos.mutexReady.acquire();
                            Proyecto1SistemasOperativos.readyQueue.addLast(procesoSaliente);
                            Proyecto1SistemasOperativos.mutexReady.release();
                            
                            Proyecto1SistemasOperativos.runningProcess = null;
                            contadorRR = 0; 
                        }
                    }
                    Proyecto1SistemasOperativos.mutexCPU.release();
                }

                // SI EL CPU ESTÁ LIBRE O HAY QUE EXPROPIAR
                if (Proyecto1SistemasOperativos.runningProcess == null) {
                    despacharProceso();
                } else {
                    verificarPreemcion(); 
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void despacharProceso() {
        Proyecto1SistemasOperativos.mutexReady.acquire();
        Proyecto1SistemasOperativos.mutexCPU.acquire();

        PCB proximo = Proyecto1SistemasOperativos.readyQueue.removeFirst();

        if (proximo != null) {
            proximo.setStatus("Ejecución");
            Proyecto1SistemasOperativos.runningProcess = proximo;
            contadorRR = 0;
            System.out.println("[SCHEDULER] Ejecutando: " + proximo.getNombre());
        }

        Proyecto1SistemasOperativos.mutexCPU.release();
        Proyecto1SistemasOperativos.mutexReady.release();
    }
    
    // Método público para que el Dashboard lo llame
    public void cambiarAlgoritmo(Proyecto1SistemasOperativos.Algoritmo nuevoAlgoritmo) {
        Proyecto1SistemasOperativos.mutexReady.acquire();
        
        Proyecto1SistemasOperativos.algoritmoActual = nuevoAlgoritmo;
        System.out.println(">>> CAMBIO DE ALGORITMO A: " + nuevoAlgoritmo + " <<<");
        
        List tempQueue = new List();
        while (!Proyecto1SistemasOperativos.readyQueue.isEmpty()) {
            PCB p = Proyecto1SistemasOperativos.readyQueue.removeFirst();
            
            switch (nuevoAlgoritmo) {
                case FCFS: 
                case ROUND_ROBIN:
                    tempQueue.addLast(p); 
                    break;
                case SRT:
                    tempQueue.insertBySRT(p);
                    break;
                case PRIORIDAD:
                    tempQueue.insertByPriority(p);
                    break;
                case EDF:
                    tempQueue.insertByDeadline(p);
                    break;
            }
        }
        Proyecto1SistemasOperativos.readyQueue = tempQueue;
        
        Proyecto1SistemasOperativos.mutexReady.release();
    }

    private void verificarPreemcion() {
        Proyecto1SistemasOperativos.mutexReady.acquire();
        Proyecto1SistemasOperativos.mutexCPU.acquire();
        
        PCB running = Proyecto1SistemasOperativos.runningProcess;
        PCB candidato = Proyecto1SistemasOperativos.readyQueue.peek(); 

        if (running != null && candidato != null) {
            boolean debeCambiar = false;

            // Usamos la referencia completa al ENUM
            switch (Proyecto1SistemasOperativos.algoritmoActual) {
                case PRIORIDAD:
                    if (candidato.getPrioridad() < running.getPrioridad()) {
                        debeCambiar = true;
                    }
                    break;
                case SRT:
                    int restanteCandidato = candidato.getInstruccionesTotales() - candidato.getInstruccionesEjecutadas();
                    int restanteRunning = running.getInstruccionesTotales() - running.getInstruccionesEjecutadas();
                    if (restanteCandidato < restanteRunning) {
                        debeCambiar = true;
                    }
                    break;
                case EDF:
                    if (candidato.getDeadline() < running.getDeadline()) {
                        debeCambiar = true;
                    }
                    break;
                default:
                    break;
            }

            if (debeCambiar) {
                System.out.println(">>> PREEMCIÓN: " + candidato.getNombre() + " desplaza a " + running.getNombre());
                
                running.setStatus("Listo");
                PCB procesoSaliente = running;
                Proyecto1SistemasOperativos.runningProcess = null; 
                
                // Reinsertamos según el algoritmo actual
                switch (Proyecto1SistemasOperativos.algoritmoActual) {
                    case PRIORIDAD:
                        Proyecto1SistemasOperativos.readyQueue.insertByPriority(procesoSaliente);
                        break;
                    case SRT:
                        Proyecto1SistemasOperativos.readyQueue.insertBySRT(procesoSaliente);
                        break;
                    case EDF:
                        Proyecto1SistemasOperativos.readyQueue.insertByDeadline(procesoSaliente);
                        break;
                    default:
                        Proyecto1SistemasOperativos.readyQueue.addLast(procesoSaliente);
                }
            }
        }

        Proyecto1SistemasOperativos.mutexCPU.release();
        Proyecto1SistemasOperativos.mutexReady.release();
    }
}