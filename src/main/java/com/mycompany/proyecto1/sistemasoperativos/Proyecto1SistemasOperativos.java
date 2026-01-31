/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyecto1.sistemasoperativos;

// Importaciones de clases
import DataStructures.List;
import DataStructures.PCB;
/**
 *
 * @author Luigi
 */
public class Proyecto1SistemasOperativos {

    // Colas requeridas para el modelo de 7 estados [cite: 15, 47]
    public static List readyQueue = new List();
    public static List blockedQueue = new List();
    public static List readySuspendedQueue = new List();
    public static List blockedSuspendedQueue = new List();
    public static List finishedQueue = new List();
    public static PCB runningProcess = null;

    public static void main(String[] args) {
        System.out.println("Iniciando Simulador RTOS UNIMET-Sat...");
        
        // Aquí se debe inicializar la GUI (Requisito indispensable) [cite: 97, 99]
        // Y lanzar el hilo del Reloj Global [cite: 29, 63]
    }
}
