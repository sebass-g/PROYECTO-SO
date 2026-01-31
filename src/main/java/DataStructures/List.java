package DataStructures;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Luigi
 */
public class List {
    private Node head;
    private int size;

    // Agregar al final (Útil para FCFS y colas generales)
    public void addLast(PCB pcb) {
        Node newNode = new Node(pcb);
        if (head == null) {
            head = newNode;
        } else {
            Node temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newNode;
        }
        size++;
    }

    public void addFirst(PCB pcb) {
        Node newNode = new Node(pcb);
        if (head == null) {
            head = newNode;
        } else {
            newNode.next = head;
            head = newNode;
        }
        size++;
    }

    // Asegúrate de tener este método también para el Scheduler
    public PCB removeFirst() {
        if (head == null) return null;
        PCB temp = head.pcb;
        head = head.next;
        size--;
        return temp;
    }

    // Inserción ordenada por Deadline (Para política EDF) [cite: 23, 35]
    public void insertByDeadline(PCB pcb) {
        Node newNode = new Node(pcb);
        if (head == null || pcb.getDeadline() < head.pcb.getDeadline()) {
            newNode.next = head;
            head = newNode;
        } else {
            Node current = head;
            while (current.next != null && current.next.pcb.getDeadline() < pcb.getDeadline()) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    public int getSize() { return size; }
    public boolean isEmpty() { return head == null; }
}