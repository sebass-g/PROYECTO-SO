package DataStructures;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */



/**
 *
 * @author Luigi
 */
public class PCB {
    // Atributos básicos requeridos
    private String id;
    private String nombre;
    private String status; // Nuevo, Listo, Ejecución, Bloqueado, etc. 
    private int pc; // Program Counter
    private int mar; // Memory Address Register 
    private int prioridad;
    
    // Atributos de tiempo real y ejecución
    private int instruccionesTotales;
    private int instruccionesEjecutadas;
    private int deadline; // Tiempo límite de finalización
    private int tiempoLlegada;
    
    // Para manejo de E/S [cite: 49]
    private int ciclosParaExcepcion; 
    private int ciclosParaSatisfacer;
    
    private int ciclosParaGenerarExcepcion;
    private int ciclosParaSatisfacerExcepcion;

    public PCB(String id, String nombre, int instrucciones, int prioridad, int deadline, int ciclosEx, int ciclosSat) {
        this.id = id;
        this.nombre = nombre;
        this.instruccionesTotales = instrucciones;
        this.prioridad = prioridad;
        this.deadline = deadline;
        this.ciclosParaGenerarExcepcion = ciclosEx;
        this.ciclosParaSatisfacerExcepcion = ciclosSat;
        
        // Inicialización por defecto
        this.status = "Nuevo";
        this.pc = 0;
        this.mar = 0;
        this.instruccionesEjecutadas = 0;
    }    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int pc) {
        this.pc = pc;
    }

    public int getMar() {
        return mar;
    }

    public void setMar(int mar) {
        this.mar = mar;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getInstruccionesTotales() {
        return instruccionesTotales;
    }

    public void setInstruccionesTotales(int instruccionesTotales) {
        this.instruccionesTotales = instruccionesTotales;
    }

    public int getInstruccionesEjecutadas() {
        return instruccionesEjecutadas;
    }

    public void setInstruccionesEjecutadas(int instruccionesEjecutadas) {
        this.instruccionesEjecutadas = instruccionesEjecutadas;
    }

    public int getDeadline() {
        return deadline;
    }

    public void setDeadline(int deadline) {
        this.deadline = deadline;
    }

    public int getTiempoLlegada() {
        return tiempoLlegada;
    }

    public void setTiempoLlegada(int tiempoLlegada) {
        this.tiempoLlegada = tiempoLlegada;
    }

    public int getCiclosParaExcepcion() {
        return ciclosParaExcepcion;
    }

    public void setCiclosParaExcepcion(int ciclosParaExcepcion) {
        this.ciclosParaExcepcion = ciclosParaExcepcion;
    }

    public int getCiclosParaSatisfacer() {
        return ciclosParaSatisfacer;
    }

    public void setCiclosParaSatisfacer(int ciclosParaSatisfacer) {
        this.ciclosParaSatisfacer = ciclosParaSatisfacer;
    }
    
    public int getCiclosParaGenerarExcepcion() {
        return ciclosParaGenerarExcepcion;
    }

    public void setCiclosParaGenerarExcepcion(int ciclosParaGenerarExcepcion) {
        this.ciclosParaGenerarExcepcion = ciclosParaGenerarExcepcion;
    }
    
    public int getCiclosParaSatisfacerExcepcion() {
        return ciclosParaSatisfacerExcepcion;
    }

    public void setCiclosParaSatisfacerExcepcion(int ciclosParaSatisfacerExcepcion) {
        this.ciclosParaSatisfacerExcepcion = ciclosParaSatisfacerExcepcion;
    }
    @Override
    public String toString() {
        // Devuelve: "Nombre (ID) - Prioridad: X"
        return this.nombre + " (ID:" + this.id + ") - P:" + this.prioridad;
    }
    
    // --- VARIABLES DE TIEMPO PARA LA GRÁFICA ---
    private int tiempoFinalizacion;

    // --- MÉTODOS PARA LA GRÁFICA ---
    public int getTiempoFinalizacion() {
        return tiempoFinalizacion;
    }

    public void setTiempoFinalizacion(int tiempoFinalizacion) {
        this.tiempoFinalizacion = tiempoFinalizacion;
    }

    // Calcula cuánto tiempo pasó el proceso esperando en colas
    public int getTiempoEspera() {
        // Tiempo total que existió = Finalización - Llegada
        int tiempoEnSistema = this.tiempoFinalizacion - this.getTiempoLlegada();
        
        // Calculamos la espera
        int espera = tiempoEnSistema - this.getInstruccionesTotales();
        
        // Retornamos el VALOR ABSOLUTO para eliminar el signo negativo 
        // y conservar el número exacto que necesitas.
        return Math.abs(espera);
    }
}
