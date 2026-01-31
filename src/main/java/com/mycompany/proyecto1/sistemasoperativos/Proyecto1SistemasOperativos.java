package com.mycompany.proyecto1.sistemasoperativos;

// IMPORTANTE: Importar tus estructuras
import DataStructures.List;
import DataStructures.PCB;
import DataStructures.Clock;

public class Proyecto1SistemasOperativos {

    // 1. Atributos estáticos (deben ser static para usarse en el main)
    public static final Object syncLock = new Object();
    public static int globalClock = 0;
    public static PCB runningProcess = null;
    
    // Colas del modelo de 7 estados
    public static List readyQueue = new List();
    public static List blockedQueue = new List();
    public static List readySuspendedQueue = new List();
    public static List blockedSuspendedQueue = new List();
    public static List finishedQueue = new List();

    // 2. EL MÉTODO MAIN (Asegúrate de que tenga el String[] args)
    public static void main(String[] args) {
        System.out.println("--- Iniciando UNIMET-Sat RTOS ---");
        
        // Inicializar procesos
        inicializarProcesos();
        
        // Iniciar el Reloj
        Clock mainClock = new Clock(1000, syncLock);
        mainClock.start();
        
        System.out.println("Reloj en marcha. Ciclo actual: " + globalClock);
    }

    // 3. Método para cumplir con los 20 procesos iniciales
    public static void inicializarProcesos() {
        for (int i = 1; i <= 20; i++) {
            // Generar valores aleatorios usando Math.random()
            int inst = 10 + (int)(Math.random() * 21);
            int prio = 1 + (int)(Math.random() * 3);
            int dline = 50 + (int)(Math.random() * 51);
            
            PCB nuevo = new PCB("P" + i, "Mision_" + i, inst, prio, dline, 5, 3);
            
            synchronized(syncLock) {
                readyQueue.addLast(nuevo);
            }
        }
        System.out.println("20 procesos aleatorios creados.");
    }
}