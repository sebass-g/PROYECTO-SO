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
                // 1. Velocidad de la simulación
                Thread.sleep(200); // 0.2 segundos por ciclo

                
                // PASO A: SIMULACIÓN DE EJECUCIÓN 
                
                Proyecto1SistemasOperativos.mutexCPU.acquire();
                PCB procesoActual = Proyecto1SistemasOperativos.runningProcess;
                
                if (procesoActual != null) {
                    // Aumentar contador de instrucciones ejecutadas
                    int ejecutadas = procesoActual.getInstruccionesEjecutadas();
                    procesoActual.setInstruccionesEjecutadas(ejecutadas + 1);
                    
                    // Verificar si el proceso YA TERMINÓ
                    if (procesoActual.getInstruccionesEjecutadas() >= procesoActual.getInstruccionesTotales()) {
                        System.out.println(">>> PROCESO TERMINADO: " + procesoActual.getNombre());
                        
                        procesoActual.setStatus("Terminado");
                        
                        // Guardar en la cola de terminados
                        Proyecto1SistemasOperativos.finishedQueue.addLast(procesoActual);
                        
                        // Liberar el CPU
                        Proyecto1SistemasOperativos.runningProcess = null;
                        contadorRR = 0; // Reiniciar contador RR
                    }
                }
                Proyecto1SistemasOperativos.mutexCPU.release();
                


               
                // PASO B: LÓGICA DE ROUND ROBIN 
                
                if (Proyecto1SistemasOperativos.algoritmoActual == Proyecto1SistemasOperativos.Algoritmo.ROUND_ROBIN) {
                    
                    Proyecto1SistemasOperativos.mutexCPU.acquire();
                    if (Proyecto1SistemasOperativos.runningProcess != null) {
                        contadorRR++;
                        // Si se acabó su tiempo (Quantum)
                        if (contadorRR >= Proyecto1SistemasOperativos.quantum) {
                            System.out.println("--- Fin de Quantum RR para " + Proyecto1SistemasOperativos.runningProcess.getNombre() + " ---");
                            
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

               
                // PASO C: DESPACHAR O EXPROPIAR 
          
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
        // Solo intentamos despachar si hay gente en la cola de listos
        if (Proyecto1SistemasOperativos.readyQueue.isEmpty()) {
            return;
        }

        Proyecto1SistemasOperativos.mutexReady.acquire();
        Proyecto1SistemasOperativos.mutexCPU.acquire();

        // Verificar de nuevo por seguridad
        if (!Proyecto1SistemasOperativos.readyQueue.isEmpty()) {
            PCB proximo = Proyecto1SistemasOperativos.readyQueue.removeFirst();
            if (proximo != null) {
                proximo.setStatus("Ejecución");
                Proyecto1SistemasOperativos.runningProcess = proximo;
                contadorRR = 0;
                System.out.println("[SCHEDULER] Ejecutando: " + proximo.getNombre());
            }
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
        // Esta función puede ser costosa si se llama muy rápido, 
        // verifica primero si readyQueue tiene algo antes de bloquear mutexes
        if (Proyecto1SistemasOperativos.readyQueue.isEmpty()) return;

        Proyecto1SistemasOperativos.mutexReady.acquire();
        Proyecto1SistemasOperativos.mutexCPU.acquire();
        
        PCB running = Proyecto1SistemasOperativos.runningProcess;
        PCB candidato = Proyecto1SistemasOperativos.readyQueue.peek(); 

        if (running != null && candidato != null) {
            boolean debeCambiar = false;

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