package com.mycompany.proyecto1.sistemasoperativos;

import DataStructures.List;
import DataStructures.PCB;
import DataStructures.Clock;
import DataStructures.Semaphore;

public class Proyecto1SistemasOperativos {

    // 1. Semáforos independientes para cada recurso compartido
    public static Semaphore mutexReady = new Semaphore(1);
    public static Semaphore mutexBlocked = new Semaphore(1);
    public static Semaphore mutexSuspended = new Semaphore(1);
    public static Semaphore mutexCPU = new Semaphore(1); 
    public static Semaphore mutexClock = new Semaphore(1); 

    // Atributos del sistema
    public static int globalClock = 0;
    public static PCB runningProcess = null;
    
    // Colas del modelo de 7 estados
    public static List readyQueue = new List();
    public static List blockedQueue = new List();
    public static List readySuspendedQueue = new List();
    public static List blockedSuspendedQueue = new List();
    public static List finishedQueue = new List();

    public static void main(String[] args) {
        System.out.println("--- Iniciando UNIMET-Sat RTOS ---");
        
        // Inicializar procesos
        inicializarProcesos();
        
        // Iniciar el Reloj (pasa el semáforo de CPU y Clock)
        Clock mainClock = new Clock(1000); 
        mainClock.start();
        
        // Iniciar el Planificador
        Scheduler scheduler = new Scheduler();
        scheduler.start();
        
        // Iniciar el Generador de Interrupciones (Eventos asíncronos)
        InterruptGenerator interruptSystem = new InterruptGenerator();
        interruptSystem.start();
        
        System.out.println("Sistemas iniciados correctamente.");
    }

    public static void inicializarProcesos() {
        for (int i = 1; i <= 20; i++) {
            int inst = 10 + (int)(Math.random() * 21);
            int prio = 1 + (int)(Math.random() * 3);
            int dline = 50 + (int)(Math.random() * 51);
            
            PCB nuevo = new PCB("P" + i, "Mision_" + i, inst, prio, dline, 5, 3);
            
            // USO DE SEMÁFORO en lugar de synchronized
            mutexReady.acquire();
            readyQueue.addLast(nuevo);
            mutexReady.release();
        }
        System.out.println("20 procesos aleatorios creados y protegidos por semáforo.");
    }
}