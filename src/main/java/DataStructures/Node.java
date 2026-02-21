package DataStructures;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Luigi Lauricella & Sebastian Gonzalez
 */
public class Node {
    public PCB pcb;
    public Node next;

    public Node(PCB pcb) {
        this.pcb = pcb;
        this.next = null;
    }
}