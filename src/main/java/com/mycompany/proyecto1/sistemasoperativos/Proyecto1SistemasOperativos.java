package com.mycompany.proyecto1.sistemasoperativos;

import DataStructures.List;
import DataStructures.PCB;
import DataStructures.Clock;
import DataStructures.Semaphore;

/**
 *
 * @author Luigi Lauricella & Sebastian Gonzalez
 */
public class Proyecto1SistemasOperativos {
    // --- SEMÁFOROS ---
    public static Semaphore mutexReady = new Semaphore(1);
    public static Semaphore mutexBlocked = new Semaphore(1);
    public static Semaphore mutexSuspended = new Semaphore(1);
    public static Semaphore mutexCPU = new Semaphore(1); 
    public static Semaphore mutexClock = new Semaphore(1); 
    
    // --- VARIABLES GLOBALES PARA ALGORITMOS ---
    public enum Algoritmo {
        FCFS, ROUND_ROBIN, SRT, PRIORIDAD, EDF
    }
    
    public static Algoritmo algoritmoActual = Algoritmo.FCFS; 
    public static int quantum = 2; 

    // REFERENCIA GLOBAL AL SCHEDULER
    public static Scheduler scheduler;

    // --- VARIABLES DEL SISTEMA ---
    public static int globalClock = 0;
    public static int velocidadSimulacion = 1000;
    public static PCB runningProcess = null;
    
    // ---> LÍMITE DE MEMORIA PARA EL SWAP (Resuelve tu error) <---
    public static final int MAX_MEMORY = 10; 
    
    // --- COLAS ---
    public static List readyQueue = new List();
    public static List blockedQueue = new List();
    public static List readySuspendedQueue = new List(); // Cola Listo-Suspendido
    public static List blockedSuspendedQueue = new List(); // Cola Bloqueado-Suspendido
    public static List finishedQueue = new List();

    public static void main(String[] args) {
        System.out.println("--- Iniciando UNIMET-Sat RTOS ---");
        
        // Inicializar los 20 procesos iniciales
        inicializarProcesos();
        
        // 1. Iniciar el Reloj
        Clock mainClock = new Clock(1000); 
        mainClock.start();
        
        // 2. Iniciar el Planificador
        scheduler = new Scheduler(); 
        scheduler.start();
        
        // 3. Iniciar el generador de interrupciones de hardware
        InterruptGenerator interruptSystem = new InterruptGenerator();
        interruptSystem.start();

        // 4. Iniciar el Planificador de Mediano Plazo (Gestión de Memoria / Swap)
        MemoryManager memoryManager = new MemoryManager();
        memoryManager.start();
        
        // 5. Iniciar la Interfaz Gráfica y Ventana Gráficos
        java.awt.EventQueue.invokeLater(() -> {
            Dashboard ventanaPrincipal = new Dashboard();
            VentanaGraficas ventanaGraficas = new VentanaGraficas();
            
            // 1. ESTABLECER EL TAMAÑO (Ancho, Alto) en píxeles
            // Ajusta estos números según el tamaño de tu pantalla
            ventanaGraficas.setSize(700, 550); 
            
            // 2. ACOMODARLAS EN LA PANTALLA (Coordenadas X, Y)
            // Así evitas que aparezcan encimadas la una sobre la otra
            ventanaPrincipal.setLocation(100, 100);  // Más pegada a la izquierda
            ventanaGraficas.setLocation(750, 100);   // Más pegada a la derecha
            
            // 3. HACERLAS VISIBLES
            ventanaPrincipal.setVisible(true);
            ventanaGraficas.setVisible(true);
        });
        
        System.out.println("Sistemas iniciados correctamente.");
    }

    // MÉTODO GENERADOR DE PROCESOS
    public static void inicializarProcesos() {
        for (int i = 1; i <= 20; i++) {
            int inst = 10 + (int)(Math.random() * 21);
            int prio = 1 + (int)(Math.random() * 3);
            int dline = 50 + (int)(Math.random() * 51);
            
            // Genera un ID único para evitar conflictos en la lista
            int idAleatorio = (int)(Math.random() * 10000);
            
            PCB nuevo = new PCB("P" + idAleatorio, "Mision_" + i, inst, prio, dline, 5, 3);
            
            mutexReady.acquire();
            readyQueue.addLast(nuevo);
            mutexReady.release();
        }
        System.out.println("20 procesos generados y enviados a la cola de Listos.");
    }
}