package com.mycompany.proyecto1.sistemasoperativos;

import DataStructures.List;
import DataStructures.PCB;
import DataStructures.Clock;
import DataStructures.Semaphore;

/**
 *
 * @author Luigi
 */
public class Proyecto1SistemasOperativos {

    // --- SEMÁFOROS ---
    public static Semaphore mutexReady = new Semaphore(1);
    public static Semaphore mutexBlocked = new Semaphore(1);
    public static Semaphore mutexSuspended = new Semaphore(1);
    public static Semaphore mutexCPU = new Semaphore(1); 
    public static Semaphore mutexClock = new Semaphore(1); 
    
    // --- VARIABLES GLOBALES PARA ALGORITMOS ---
    // 1. Enumeración (Debe ser pública)
    public enum Algoritmo {
        FCFS, ROUND_ROBIN, SRT, PRIORIDAD, EDF
    }
    
    // 2. Variables de control (Públicas y estáticas para que Scheduler las vea)
    public static Algoritmo algoritmoActual = Algoritmo.FCFS; 
    public static int quantum = 2; 

    // 3. REFERENCIA GLOBAL AL SCHEDULER (Esto faltaba para el Dashboard)
    public static Scheduler scheduler;

    // --- VARIABLES DEL SISTEMA ---
    public static int globalClock = 0;
    public static PCB runningProcess = null;
    
    // --- COLAS ---
    public static List readyQueue = new List();
    public static List blockedQueue = new List();
    public static List readySuspendedQueue = new List();
    public static List blockedSuspendedQueue = new List();
    public static List finishedQueue = new List();

    public static void main(String[] args) {
        System.out.println("--- Iniciando UNIMET-Sat RTOS ---");
        
        // Inicializar procesos
        inicializarProcesos();
        
        // Iniciar el Reloj
        Clock mainClock = new Clock(1000); 
        mainClock.start();
        
        // Iniciar el Planificador (Usando la variable estática)
        scheduler = new Scheduler(); // <--- OJO: No pongas 'Scheduler scheduler = ...'
        scheduler.start();
        
        // Iniciar Interrupciones
        InterruptGenerator interruptSystem = new InterruptGenerator();
        interruptSystem.start();
        
        // Iniciar Interfaz Gráfica
        java.awt.EventQueue.invokeLater(() -> {
            new Dashboard().setVisible(true);
        });
        
        System.out.println("Sistemas iniciados correctamente.");
    }

    public static void inicializarProcesos() {
        for (int i = 1; i <= 20; i++) {
            int inst = 10 + (int)(Math.random() * 21);
            int prio = 1 + (int)(Math.random() * 3);
            int dline = 50 + (int)(Math.random() * 51);
            
            PCB nuevo = new PCB("P" + i, "Mision_" + i, inst, prio, dline, 5, 3);
            
            mutexReady.acquire();
            readyQueue.addLast(nuevo);
            mutexReady.release();
        }
        System.out.println("20 procesos aleatorios creados y protegidos por semáforo.");
    }
}