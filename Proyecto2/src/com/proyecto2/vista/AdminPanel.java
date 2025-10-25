package com.proyecto2.vista;

import javax.swing.*;
import java.awt.*;
import com.proyecto2.controlador.MainController;
import com.proyecto2.modelo.*;
import java.io.File;

public class AdminPanel extends JPanel {
    private MainController controller;
    private MainWindow mainWindow;

    // subpaneles
    private UsersPanel usersPanel;
    private VendedoresPanel vendedoresPanel;
    private ProductsPanel productsPanel;
    private JPanel utilPanel;

    public AdminPanel(MainController controller, MainWindow mainWindow){
        this.controller = controller;
        this.mainWindow = mainWindow;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        JTabbedPane tabs = new JTabbedPane();

        usersPanel = new UsersPanel(controller);
        vendedoresPanel = new VendedoresPanel(controller);
        productsPanel = new ProductsPanel(controller);
        utilPanel = buildUtilPanel();

        tabs.addTab("Usuarios", usersPanel);
        tabs.addTab("Vendedores", vendedoresPanel);
        tabs.addTab("Productos", productsPanel);
        tabs.addTab("Utilidades", utilPanel);

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildUtilPanel(){
        JPanel p = new JPanel(new GridLayout(3, 1, 10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Sección de CSV
        JPanel csvPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        csvPanel.setBorder(BorderFactory.createTitledBorder("Importar/Exportar CSV"));
        JButton btnImportCSV = new JButton("Importar CSV productos");
        JButton btnExportCSV = new JButton("Exportar CSV productos");
        JButton btnImportVendedoresCSV = new JButton("Importar CSV vendedores");
        
        csvPanel.add(btnImportCSV);
        csvPanel.add(btnExportCSV);
        csvPanel.add(btnImportVendedoresCSV);

        // Sección de Reportes PDF
        JPanel pdfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pdfPanel.setBorder(BorderFactory.createTitledBorder("Reportes PDF"));
        JButton btnPDFProductos = new JButton("Reporte Productos");
        JButton btnPDFMasVendidos = new JButton("Productos Más Vendidos");
        JButton btnPDFMenosVendidos = new JButton("Productos Menos Vendidos");
        JButton btnPDFInventario = new JButton("Estado Inventario");
        
        pdfPanel.add(btnPDFProductos);
        pdfPanel.add(btnPDFMasVendidos);
        pdfPanel.add(btnPDFMenosVendidos);
        pdfPanel.add(btnPDFInventario);

        // Sección de Reportes Avanzados
        JPanel reportesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        reportesPanel.setBorder(BorderFactory.createTitledBorder("Reportes Avanzados"));
        JButton btnPDFVentasVendedor = new JButton("Ventas por Vendedor");
        JButton btnPDFClientesActivos = new JButton("Clientes Activos");
        JButton btnPDFFinanciero = new JButton("Reporte Financiero");
        JButton btnPDFCaducar = new JButton("Productos por Caducar");
        
        reportesPanel.add(btnPDFVentasVendedor);
        reportesPanel.add(btnPDFClientesActivos);
        reportesPanel.add(btnPDFFinanciero);
        reportesPanel.add(btnPDFCaducar);

        JLabel lblMsg = new JLabel(" ");
        lblMsg.setHorizontalAlignment(SwingConstants.CENTER);

        // Listeners para CSV
        btnImportCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                boolean ok = controller.importProductsFromCSV(f.getAbsolutePath());
                lblMsg.setText(ok ? "✅ Productos importados exitosamente" : "❌ Error importando productos");
                productsPanel.refreshTable();
            }
        });
        
        btnImportVendedoresCSV.addActionListener(e -> {
            // Simular click en el botón de cargar CSV del panel de vendedores
            vendedoresPanel.refreshTable();
            lblMsg.setText("✅ Use el botón 'Cargar CSV' en la pestaña Vendedores");
        });

        btnExportCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String filename = f.getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".csv")) {
                    filename += ".csv";
                }
                boolean ok = controller.exportProductsToCSV(filename);
                lblMsg.setText(ok ? "✅ Productos exportados exitosamente" : "❌ Error exportando productos");
            }
        });

        // Listeners para Reportes PDF Básicos
        btnPDFProductos.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String filename = f.getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }
                boolean ok = controller.generateProductsPDF(filename);
                lblMsg.setText(ok ? "✅ PDF de productos creado" : "❌ Error creando PDF");
            }
        });

        btnPDFMasVendidos.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String filename = f.getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }
                boolean ok = controller.generateProductosMasVendidosPDF(filename);
                lblMsg.setText(ok ? "✅ PDF productos más vendidos creado" : "❌ Error creando PDF");
            }
        });

        btnPDFMenosVendidos.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String filename = f.getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }
                boolean ok = controller.generateProductosMenosVendidosPDF(filename);
                lblMsg.setText(ok ? "✅ PDF productos menos vendidos creado" : "❌ Error creando PDF");
            }
        });

        btnPDFInventario.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String filename = f.getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }
                boolean ok = controller.generateInventarioPDF(filename);
                lblMsg.setText(ok ? "✅ PDF inventario creado" : "❌ Error creando PDF");
            }
        });

        // Listeners para Reportes Avanzados
        btnPDFVentasVendedor.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String filename = f.getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }
                boolean ok = controller.generateVentasPorVendedorPDF(filename);
                lblMsg.setText(ok ? "✅ PDF ventas por vendedor creado" : "❌ Error creando PDF");
            }
        });

        btnPDFClientesActivos.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String filename = f.getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }
                boolean ok = controller.generateClientesActivosPDF(filename);
                lblMsg.setText(ok ? "✅ PDF clientes activos creado" : "❌ Error creando PDF");
            }
        });

        btnPDFFinanciero.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String filename = f.getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }
                boolean ok = controller.generateReporteFinancieroPDF(filename);
                lblMsg.setText(ok ? "✅ PDF financiero creado" : "❌ Error creando PDF");
            }
        });

        btnPDFCaducar.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                String filename = f.getAbsolutePath();
                if (!filename.toLowerCase().endsWith(".pdf")) {
                    filename += ".pdf";
                }
                boolean ok = controller.generateProductosPorCaducarPDF(filename);
                lblMsg.setText(ok ? "✅ PDF productos por caducar creado" : "❌ Error creando PDF");
            }
        });

        p.add(csvPanel);
        p.add(pdfPanel);
        p.add(reportesPanel);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(p, BorderLayout.CENTER);
        mainPanel.add(lblMsg, BorderLayout.SOUTH);
        
        return mainPanel;
    }

    public void refreshAll(){
        usersPanel.refreshTable();
        vendedoresPanel.refreshTable();
        productsPanel.refreshTable();
    }
}