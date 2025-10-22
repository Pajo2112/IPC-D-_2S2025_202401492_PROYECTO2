package com.proyecto2.vista;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import com.proyecto2.controlador.MainController;
import com.proyecto2.modelo.Product;

public class ProductsPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private ProductsTableModel model;
    private JTextField tfCodigo, tfNombre, tfCategoria, tfAtributo, tfStock;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh;

    public ProductsPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new ProductsTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2,6,4,4));
        tfCodigo = new JTextField(); tfNombre = new JTextField(); tfCategoria = new JTextField();
        tfAtributo = new JTextField(); tfStock = new JTextField();

        form.add(new JLabel("Código")); form.add(new JLabel("Nombre")); form.add(new JLabel("Categoría"));
        form.add(new JLabel("Atributo")); form.add(new JLabel("Stock")); form.add(new JLabel());
        form.add(tfCodigo); form.add(tfNombre); form.add(tfCategoria); form.add(tfAtributo); form.add(tfStock);

        add(form, BorderLayout.NORTH);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Agregar"); btnUpdate = new JButton("Actualizar"); btnDelete = new JButton("Eliminar"); btnRefresh = new JButton("Refrescar");
        south.add(btnAdd); south.add(btnUpdate); south.add(btnDelete); south.add(btnRefresh);
        add(south, BorderLayout.SOUTH);

        btnAdd.addActionListener(e-> {
            try {
                String c = tfCodigo.getText().trim();
                String n = tfNombre.getText().trim();
                String cat = tfCategoria.getText().trim();
                String att = tfAtributo.getText().trim();
                int s = Integer.parseInt(tfStock.getText().trim());
                boolean ok = controller.addProduct(new Product(c,n,cat,att,s));
                JOptionPane.showMessageDialog(this, ok ? "Agregado" : "Error (duplicado?)");
                refreshTable();
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Error en datos"); }
        });

        btnUpdate.addActionListener(e-> {
            try {
                String c = tfCodigo.getText().trim();
                String n = tfNombre.getText().trim();
                String cat = tfCategoria.getText().trim();
                String att = tfAtributo.getText().trim();
                int s = Integer.parseInt(tfStock.getText().trim());
                boolean ok = controller.updateProduct(c,n,cat,att,s);
                JOptionPane.showMessageDialog(this, ok ? "Actualizado" : "No existe producto");
                refreshTable();
            } catch(Exception ex){ JOptionPane.showMessageDialog(this, "Error en datos"); }
        });

        btnDelete.addActionListener(e-> {
            int r = table.getSelectedRow();
            if (r>=0){
                String code = (String) model.getValueAt(r,0);
                boolean ok = controller.deleteProduct(code);
                JOptionPane.showMessageDialog(this, ok ? "Eliminado" : "No eliminado");
                refreshTable();
            }
        });

        btnRefresh.addActionListener(e-> refreshTable());
    }

    public void refreshTable(){
        Product[] list = controller.listProducts();
        model.setProducts(list);
    }

    class ProductsTableModel extends AbstractTableModel {
        private String[] cols = {"Código","Nombre","Categoría","Atributo","Stock"};
        private Product[] prods = new Product[0];
        public void setProducts(Product[] p){ prods = p == null ? new Product[0] : p; fireTableDataChanged(); }
        public int getRowCount(){ return prods.length; }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int i){ return cols[i]; }
        public Object getValueAt(int r,int c){
            Product p = prods[r];
            switch(c){ case 0: return p.getCodigo(); case 1: return p.getNombre(); case 2: return p.getCategoria(); case 3: return p.getAtributo(); case 4: return p.getStock(); default: return ""; }
        }
    }
}

