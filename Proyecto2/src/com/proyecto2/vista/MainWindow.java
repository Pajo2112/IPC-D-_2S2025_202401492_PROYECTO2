package com.proyecto2.vista;

import javax.swing.*;
import java.awt.*;
import com.proyecto2.controlador.MainController;
import com.proyecto2.utiles.Bitacora;
import com.proyecto2.modelo.User;

public class MainWindow extends JFrame {
    private MainController controller;
    private Bitacora bitacora;

    private LoginPanel loginPanel;
    private AdminPanel adminPanel;
    private VendedorPanel vendedorPanel;
    private ClientePanel clientePanel;

    public MainWindow(MainController controller, Bitacora bitacora) {
        super("Sancarlista Shop - Proyecto 2");
        this.controller = controller;
        this.bitacora = bitacora;
        init();
    }

    private void init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        loginPanel = new LoginPanel(controller, this);
        adminPanel = new AdminPanel(controller, this);
        vendedorPanel = new VendedorPanel(controller);
        clientePanel = new ClientePanel(controller);

        add(loginPanel, BorderLayout.CENTER);
    }

    public void showAdmin(User u){
        getContentPane().removeAll();
        JPanel top = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel("Usuario: " + u.getNombre() + " (" + u.getRol() + ")");
        JButton btnLogout = new JButton("Cerrar sesión");
        top.add(lbl, BorderLayout.WEST);
        top.add(btnLogout, BorderLayout.EAST);
        
        btnLogout.addActionListener(e -> {
            getContentPane().removeAll();
            add(loginPanel, BorderLayout.CENTER);
            revalidate(); repaint();
        });

        add(top, BorderLayout.NORTH);
        
        // Configurar usuario actual en el controller
        controller.setUsuarioActual(u);
        
        String rol = u.getRol().toUpperCase();
        switch(rol) {
            case "ADMIN":
                add(adminPanel, BorderLayout.CENTER);
                adminPanel.refreshAll();
                break;
            case "VENDEDOR":
                add(vendedorPanel, BorderLayout.CENTER);
                vendedorPanel.refreshAll();
                break;
            case "CLIENTE":
                add(clientePanel, BorderLayout.CENTER);
                clientePanel.refreshAll();
                break;
            default:
                add(clientePanel, BorderLayout.CENTER);
                clientePanel.refreshAll();
        }
        
        revalidate(); repaint();
    }
}