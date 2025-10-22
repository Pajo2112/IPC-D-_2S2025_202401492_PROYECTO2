package com.proyecto2.vista;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import com.proyecto2.controlador.MainController;
import com.proyecto2.modelo.User;

public class UsersPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private UsersTableModel model;
    private JTextField tfCode, tfName, tfPass;
    private JComboBox<String> cbRol;
    private JButton btnAdd, btnDelete;

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
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(btnDelete);
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
    }

    public void refreshTable(){
        User[] list = controller.listUsers();
        model.setUsers(list);
    }

    // Modelo tabla
    class UsersTableModel extends AbstractTableModel {
        private String[] cols = {"Código","Nombre","Rol"};
        private User[] users = new User[0];

        public void setUsers(User[] u) { users = u == null ? new User[0] : u; fireTableDataChanged(); }
        public int getRowCount(){ return users.length; }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int i){ return cols[i]; }
        public Object getValueAt(int r,int c){
            User u = users[r];
            switch(c){ case 0: return u.getCodigo(); case 1: return u.getNombre(); case 2: return u.getRol(); default: return ""; }
        }
    }
}
