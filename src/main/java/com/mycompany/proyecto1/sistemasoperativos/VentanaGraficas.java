/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.proyecto1.sistemasoperativos;

/**
 *
 * @author Luigi Lauricella & Sebastian Gonzalez
 */
public class VentanaGraficas extends javax.swing.JFrame {
    
    // --- VARIABLES DE LA GRÁFICA ---
    private org.jfree.data.category.DefaultCategoryDataset dataset;
    private org.jfree.chart.JFreeChart barChart;

    /**
     * Creates new form VentanaGraficas
     */
    public VentanaGraficas() {
        initComponents();
        this.setTitle("Métricas del Sistema en Tiempo Real");
        this.setLocationRelativeTo(null);
        
        // Iniciamos la gráfica
        inicializarGrafica();
        iniciarActualizadorGrafico();
    }

    // --- 1. INICIALIZAR GRÁFICA ---
    private void inicializarGrafica() {
        dataset = new org.jfree.data.category.DefaultCategoryDataset();
        
        // Cambiamos la estructura para que cada barra tenga su propio color en la leyenda
        dataset.setValue(0.0, "Throughput (x100)", "");
        dataset.setValue(0.0, "Espera Promedio", "");
        dataset.setValue(0.0, "Tasa Éxito (%)", "");

        barChart = org.jfree.chart.ChartFactory.createBarChart(
                "Rendimiento del Sistema", 
                "",
                "Valor Numérico",
                dataset, 
                org.jfree.chart.plot.PlotOrientation.VERTICAL, 
                true,
                true, 
                false
        );

        // ================= ESTILIZADO DE LA GRÁFICA =================
        // Limpieza de tabla
        barChart.setBackgroundPaint(java.awt.Color.WHITE); 
        barChart.getTitle().setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));

        // Formateo de color de tabla
        org.jfree.chart.plot.CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(new java.awt.Color(248, 249, 250)); // Fondo gris súper clarito
        plot.setDomainGridlinePaint(java.awt.Color.WHITE);
        plot.setRangeGridlinePaint(java.awt.Color.WHITE);
        plot.setOutlineVisible(false);
        
        // Formato de letra
        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        rangeAxis.setLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        
        // Formato de diseño de barra
        org.jfree.chart.renderer.category.BarRenderer renderer = (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
        
        // Asignamos colores
        renderer.setSeriesPaint(0, new java.awt.Color(52, 152, 219));  
        renderer.setSeriesPaint(1, new java.awt.Color(243, 156, 18));  
        renderer.setSeriesPaint(2, new java.awt.Color(46, 204, 113));  
        
        renderer.setShadowVisible(false);
        renderer.setItemMargin(0.10);

        // Mostramos los números limitados
        renderer.setDefaultItemLabelGenerator(new org.jfree.chart.labels.StandardCategoryItemLabelGenerator(
                "{2}", new java.text.DecimalFormat("0.00")));
        renderer.setDefaultItemLabelsVisible(true);
        renderer.setDefaultItemLabelFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        // ==============================================================

        org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(barChart);
        
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 350));
        
        panelGrafica.setLayout(new java.awt.BorderLayout()); 
        panelGrafica.removeAll();
        panelGrafica.add(chartPanel, java.awt.BorderLayout.CENTER);
        panelGrafica.revalidate();
        panelGrafica.repaint();
    }
    
    // --- 2. CÁLCULO DE MÉTRICAS ---
    private double[] calcularMetricas() {
        int totalTerminados = Proyecto1SistemasOperativos.finishedQueue.getSize();
        if (totalTerminados == 0 || Proyecto1SistemasOperativos.globalClock == 0) {
            return new double[]{0.0, 0.0, 0.0};
        }

        double throughput = (double) totalTerminados / Proyecto1SistemasOperativos.globalClock;
        int sumaEspera = 0;
        int misionesExitosas = 0;
        
        DataStructures.Node current = Proyecto1SistemasOperativos.finishedQueue.getHead(); 
        while (current != null) {
            sumaEspera += current.pcb.getTiempoEspera(); 
            if (current.pcb.getTiempoFinalizacion() <= current.pcb.getDeadline()) {
                misionesExitosas++;
            }
            current = current.next;
        }

        double esperaPromedio = (double) sumaEspera / totalTerminados;
        double tasaExito = ((double) misionesExitosas / totalTerminados) * 100.0;
        return new double[]{throughput, esperaPromedio, tasaExito};
    }

    // --- 3. HILO ACTUALIZADOR ---
    private void iniciarActualizadorGrafico() {
        Thread actualizador = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000); 
                    double[] metricas = calcularMetricas();
                    
                    // En este caso se tuvo que aumentar el thorughput porque sino no se veia en el formato de gráfica
                    double throughputEscalado = Math.round((metricas[0] * 100) * 100.0) / 100.0; 
                    double esperaLimitada = Math.round(metricas[1] * 100.0) / 100.0;
                    double exitoLimitado = Math.round(metricas[2] * 100.0) / 100.0;

                    // Actualizamos la gráfica con los valores limitados y redondeados
                    dataset.setValue(throughputEscalado, "Throughput (x100)", "");
                    dataset.setValue(esperaLimitada, "Espera Promedio", "");
                    dataset.setValue(exitoLimitado, "Tasa Éxito (%)", "");
                    
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        actualizador.setDaemon(true); 
        actualizador.start();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        panelGrafica = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel5.setText("   GRÁFICA DEL SISTEMA");

        javax.swing.GroupLayout panelGraficaLayout = new javax.swing.GroupLayout(panelGrafica);
        panelGrafica.setLayout(panelGraficaLayout);
        panelGraficaLayout.setHorizontalGroup(
            panelGraficaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 483, Short.MAX_VALUE)
        );
        panelGraficaLayout.setVerticalGroup(
            panelGraficaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 489, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelGrafica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(panelGrafica, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(99, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel panelGrafica;
    // End of variables declaration//GEN-END:variables
}
