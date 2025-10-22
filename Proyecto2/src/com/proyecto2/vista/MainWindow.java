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

    public MainWindow(MainController controller, Bitacora bitacora) {
        super("Proyecto2 - Demo MVC");
        this.controller = controller;
        this.bitacora = bitacora;
        init();
    }

    private void init(){
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        loginPanel = new LoginPanel(controller, this);
        adminPanel = new AdminPanel(controller, this);

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
        add(adminPanel, BorderLayout.CENTER);
        adminPanel.refreshAll();
        revalidate(); repaint();
    }
}
