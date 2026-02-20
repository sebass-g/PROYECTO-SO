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
    
    // Insertar ordenado por Prioridad (Asumiendo 1 = Mayor Prioridad)
    public void insertByPriority(PCB pcb) {
        Node newNode = new Node(pcb);
        // Si la lista está vacía o el nuevo tiene MEJOR prioridad (número menor) que el primero
        if (head == null || pcb.getPrioridad() < head.pcb.getPrioridad()) {
            newNode.next = head;
            head = newNode;
        } else {
            Node current = head;
            // Busca dónde insertar
            while (current.next != null && current.next.pcb.getPrioridad() <= pcb.getPrioridad()) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        size++;
    }

    // Insertar ordenado por Tiempo Restante (SRT)
    public void insertBySRT(PCB pcb) {
        Node newNode = new Node(pcb);
        // SRT: Menor tiempo restante va primero
        int restanteNuevo = pcb.getInstruccionesTotales() - pcb.getInstruccionesEjecutadas();
        
        // Lógica para cabeza
        if (head == null) {
            head = newNode;
        } else {
            int restanteHead = head.pcb.getInstruccionesTotales() - head.pcb.getInstruccionesEjecutadas();
            if (restanteNuevo < restanteHead) {
                newNode.next = head;
                head = newNode;
            } else {
                Node current = head;
                while (current.next != null) {
                    int restanteCurrentNext = current.next.pcb.getInstruccionesTotales() - current.next.pcb.getInstruccionesEjecutadas();
                    if (restanteNuevo < restanteCurrentNext) {
                        break;
                    }
                    current = current.next;
                }
                newNode.next = current.next;
                current.next = newNode;
            }
        }
        size++;
    }
    // Método para "espiar" el primer elemento sin sacarlo de la lista
    public PCB peek() {
        if (head == null) {
            return null;
        }
        return head.pcb;
    }

    public int getSize() { return size; }
    public boolean isEmpty() { return head == null; }
    
    // Elimina un PCB específico de la lista (Útil para la cola de bloqueados)
    public boolean remove(PCB pcb) {
        if (head == null) return false;

        // Si es el primero
        if (head.pcb.getId().equals(pcb.getId())) {
            head = head.next;
            size--;
            return true;
        }

        // Buscar en el resto de la lista
        Node current = head;
        while (current.next != null) {
            if (current.next.pcb.getId().equals(pcb.getId())) {
                current.next = current.next.next;
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    // Busca y extrae el proceso con el deadline más lejano. 
    // Se usa cuando la memoria está llena y hay que hacer Swap Out.
    public PCB removeFarthestDeadline() {
        if (head == null) return null;

        Node current = head;
        Node farthestNode = head;
        int maxDeadline = head.pcb.getDeadline();

        // Recorremos buscando el deadline más alto (el menos urgente)
        while (current != null) {
            if (current.pcb.getDeadline() > maxDeadline) {
                maxDeadline = current.pcb.getDeadline();
                farthestNode = current;
            }
            current = current.next;
        }

        // Reutilizamos el método remove() que creamos en el paso anterior
        PCB farthestPCB = farthestNode.pcb;
        remove(farthestPCB); 
        return farthestPCB;
    }
    
    @Override
    public String toString() {
        if (isEmpty()) {
            return " [Vacía] ";
        }
        StringBuilder sb = new StringBuilder();
        Node auxiliar = head; 
        while (auxiliar != null) {
            // Llama al toString() del PCB y agrega un salto de línea
            sb.append(auxiliar.pcb.toString()).append("\n"); 
            auxiliar = auxiliar.next;
        }
        return sb.toString();
    }
}