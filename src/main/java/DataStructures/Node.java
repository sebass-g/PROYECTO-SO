package DataStructures;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Luigi
 */
public class Node {
    PCB pcb;
    Node next;

    public Node(PCB pcb) {
        this.pcb = pcb;
        this.next = null;
    }
}