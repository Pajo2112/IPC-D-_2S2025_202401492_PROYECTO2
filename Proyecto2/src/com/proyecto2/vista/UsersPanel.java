package com.proyecto2.vista;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import com.proyecto2.controlador.MainController;
import com.proyecto2.modelo.User;
import java.util.ArrayList;
import java.util.List;

public class UsersPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private UsersTableModel model;
    private JTextField tfCode, tfName, tfPass, tfSearch;
    private JComboBox<String> cbRol;
    private JButton btnAdd, btnDelete, btnSearch, btnClearSearch;
    private User[] allUsers;

    public UsersPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new UsersTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2,5,4,4));
        tfCode = new JTextField();
        tfName = new JTextField();
        tfPass = new JTextField();
        cbRol = new JComboBox<>(new String[]{"ADMIN","VENDEDOR","CLIENTE"});
        btnAdd = new JButton("Agregar");
        btnDelete = new JButton("Eliminar");

        form.add(new JLabel("Código")); form.add(new JLabel("Nombre")); form.add(new JLabel("Contraseña")); form.add(new JLabel("Rol")); form.add(new JLabel());
        form.add(tfCode); form.add(tfName); form.add(tfPass); form.add(cbRol); form.add(btnAdd);

        add(form, BorderLayout.NORTH);
        
        JPanel south = new JPanel(new BorderLayout());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(btnDelete);
        
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Buscar:"));
        tfSearch = new JTextField(15);
        btnSearch = new JButton("Buscar");
        btnClearSearch = new JButton("Limpiar");
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);
        
        south.add(buttonPanel, BorderLayout.WEST);
        south.add(searchPanel, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);

        btnAdd.addActionListener(e-> {
            String code = tfCode.getText().trim();
            String name = tfName.getText().trim();
            String pass = tfPass.getText().trim();
            String rol = (String) cbRol.getSelectedItem();
            if (code.isEmpty() || name.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete los datos");
                return;
            }
            boolean ok = controller.addUser(new User(code,name,pass,rol));
            JOptionPane.showMessageDialog(this, ok ? "Usuario agregado" : "Error (duplicado?)");
            refreshTable();
            clearForm();
        });

        btnDelete.addActionListener(e-> {
            int r = table.getSelectedRow();
            if (r>=0){
                String code = (String) model.getValueAt(r,0);
                boolean ok = controller.deleteUser(code);
                JOptionPane.showMessageDialog(this, ok ? "Eliminado" : "No eliminado");
                refreshTable();
            }
        });

        btnSearch.addActionListener(e -> {
            String searchText = tfSearch.getText().trim().toLowerCase();
            applyFilter(searchText);
        });

        btnClearSearch.addActionListener(e -> {
            tfSearch.setText("");
            applyFilter("");
        });

        tfSearch.addActionListener(e -> {
            btnSearch.doClick();
        });

        refreshTable();
    }

    private void applyFilter(String searchText) {
        if (allUsers == null) return;
        
        if (searchText.isEmpty()) {
            model.setUsers(allUsers);
            return;
        }
        
        List<User> filtered = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getCodigo().toLowerCase().contains(searchText) ||
                user.getNombre().toLowerCase().contains(searchText) ||
                user.getRol().toLowerCase().contains(searchText)) {
                filtered.add(user);
            }
        }
        model.setUsers(filtered.toArray(new User[0]));
    }

    private void clearForm() {
        tfCode.setText("");
        tfName.setText("");
        tfPass.setText("");
        cbRol.setSelectedIndex(0);
    }

    public void refreshTable(){
        allUsers = controller.listUsers();
        applyFilter(tfSearch.getText().trim().toLowerCase());
    }

    class UsersTableModel extends AbstractTableModel {
        private String[] cols = {"Código","Nombre","Rol"};
        private User[] users = new User[0];

        public void setUsers(User[] u) { 
            users = u == null ? new User[0] : u; 
            fireTableDataChanged(); 
        }
        public int getRowCount(){ return users.length; }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int i){ return cols[i]; }
        public Object getValueAt(int r,int c){
            User u = users[r];
            switch(c){ 
                case 0: return u.getCodigo(); 
                case 1: return u.getNombre(); 
                case 2: return u.getRol(); 
                default: return ""; 
            }
        }
    }
}