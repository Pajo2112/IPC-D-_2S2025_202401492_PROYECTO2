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
        productsPanel = new ProductsPanel(controller);
        utilPanel = buildUtilPanel();

        tabs.addTab("Usuarios", usersPanel);
        tabs.addTab("Productos", productsPanel);
        tabs.addTab("Utilidades", utilPanel);

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildUtilPanel(){
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnImportCSV = new JButton("Importar CSV productos");
        JButton btnExportCSV = new JButton("Exportar CSV productos");
        JButton btnPDF = new JButton("Generar PDF productos");
        JLabel lblMsg = new JLabel(" ");

        btnImportCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                boolean ok = controller.importProductsFromCSV(f.getAbsolutePath());
                lblMsg.setText(ok ? "Importado OK" : "Error importando");
                productsPanel.refreshTable();
            }
        });
        btnExportCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                boolean ok = controller.exportProductsToCSV(f.getAbsolutePath());
                lblMsg.setText(ok ? "Exportado OK" : "Error exportando");
            }
        });
        btnPDF.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                boolean ok = controller.generateProductsPDF(f.getAbsolutePath());
                lblMsg.setText(ok ? "PDF creado" : "Error creando PDF (¿iText en classpath?)");
            }
        });

        p.add(btnImportCSV); p.add(btnExportCSV); p.add(btnPDF); p.add(lblMsg);
        return p;
    }

    public void refreshAll(){
        usersPanel.refreshTable();
        productsPanel.refreshTable();
    }
}
