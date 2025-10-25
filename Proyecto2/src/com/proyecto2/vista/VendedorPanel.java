package com.proyecto2.vista;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import com.proyecto2.controlador.MainController;
import com.proyecto2.modelo.*;

public class VendedorPanel extends JPanel {
    private MainController controller;
    private JTabbedPane tabs;
    private ProductsVendedorPanel productsPanel;
    private ClientesPanel clientesPanel;
    private PedidosVendedorPanel pedidosPanel;

    public VendedorPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        
        tabs = new JTabbedPane();
        productsPanel = new ProductsVendedorPanel(controller);
        clientesPanel = new ClientesPanel(controller);
        pedidosPanel = new PedidosVendedorPanel(controller);

        tabs.addTab("Productos", productsPanel);
        tabs.addTab("Clientes", clientesPanel);
        tabs.addTab("Pedidos", pedidosPanel);

        add(tabs, BorderLayout.CENTER);
    }

    public void refreshAll(){
        productsPanel.refreshTable();
        clientesPanel.refreshTable();
        pedidosPanel.refreshTable();
    }
}

// Subpanel de Productos para Vendedor
class ProductsVendedorPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private ProductsTableModel model;
    private JTextField tfCodigo, tfCantidad;
    private JButton btnAgregarStock, btnCargarCSV, btnVerDetalle;

    public ProductsVendedorPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new ProductsTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel de formulario para agregar stock
        JPanel form = new JPanel(new GridLayout(2, 4, 4, 4));
        tfCodigo = new JTextField();
        tfCantidad = new JTextField();
        btnAgregarStock = new JButton("Agregar Stock");
        btnCargarCSV = new JButton("Cargar CSV Stock");
        btnVerDetalle = new JButton("Ver Detalle");

        form.add(new JLabel("Código Producto"));
        form.add(new JLabel("Cantidad"));
        form.add(new JLabel(""));
        form.add(new JLabel(""));
        form.add(tfCodigo);
        form.add(tfCantidad);
        form.add(btnAgregarStock);
        form.add(btnVerDetalle);

        add(form, BorderLayout.NORTH);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(btnCargarCSV);
        add(south, BorderLayout.SOUTH);

        // Listeners
        btnAgregarStock.addActionListener(e -> {
            try {
                String codigo = tfCodigo.getText().trim();
                int cantidad = Integer.parseInt(tfCantidad.getText().trim());
                
                Product p = controller.findProductByCode(codigo);
                if (p != null) {
                    p.setStock(p.getStock() + cantidad);
                    controller.saveAll();
                    refreshTable();
                    JOptionPane.showMessageDialog(this, "Stock actualizado exitosamente");
                } else {
                    JOptionPane.showMessageDialog(this, "Producto no encontrado");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cantidad debe ser un número entero");
            }
        });

        btnVerDetalle.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String codigo = (String) model.getValueAt(row, 0);
                Product p = controller.findProductByCode(codigo);
                if (p != null) {
                    JOptionPane.showMessageDialog(this, 
                        "Detalle del Producto:\n" + p.getDetalleCategoria(),
                        "Detalle Producto", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla");
            }
        });

        btnCargarCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filename = fc.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(this, "Funcionalidad de cargar CSV stock en desarrollo");
            }
        });

        refreshTable();
    }

    public void refreshTable(){
        Product[] list = controller.listProducts();
        model.setProducts(list);
    }

    class ProductsTableModel extends AbstractTableModel {
        private String[] cols = {"Código", "Nombre", "Categoría", "Precio", "Stock"};
        private Product[] prods = new Product[0];
        
        public void setProducts(Product[] p){ 
            prods = p == null ? new Product[0] : p; 
            fireTableDataChanged(); 
        }
        public int getRowCount(){ return prods.length; }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int i){ return cols[i]; }
        public Object getValueAt(int r, int c){
            Product p = prods[r];
            switch(c){ 
                case 0: return p.getCodigo(); 
                case 1: return p.getNombre(); 
                case 2: return p.getCategoria(); 
                case 3: return p.getPrecioFormateado();
                case 4: return p.getStock(); 
                default: return ""; 
            }
        }
    }
}

// Subpanel de Clientes para Vendedor
class ClientesPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private ClientesTableModel model;
    private JTextField tfCodigo, tfNombre, tfGenero, tfCumpleanos, tfPassword;
    private JButton btnCrear, btnActualizar, btnEliminar, btnCargarCSV;

    public ClientesPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new ClientesTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel de formulario
        JPanel form = new JPanel(new GridLayout(3, 5, 4, 4));
        tfCodigo = new JTextField();
        tfNombre = new JTextField();
        tfGenero = new JTextField();
        tfCumpleanos = new JTextField();
        tfPassword = new JTextField();
        
        btnCrear = new JButton("Crear");
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnCargarCSV = new JButton("Cargar CSV");

        form.add(new JLabel("Código"));
        form.add(new JLabel("Nombre"));
        form.add(new JLabel("Género (M/F)"));
        form.add(new JLabel("Cumpleaños (dd/mm/aaaa)"));
        form.add(new JLabel("Contraseña"));
        form.add(tfCodigo);
        form.add(tfNombre);
        form.add(tfGenero);
        form.add(tfCumpleanos);
        form.add(tfPassword);
        form.add(btnCrear);
        form.add(btnActualizar);
        form.add(btnEliminar);
        form.add(btnCargarCSV);

        add(form, BorderLayout.NORTH);

        // Listeners
        btnCrear.addActionListener(e -> {
            String codigo = tfCodigo.getText().trim();
            String nombre = tfNombre.getText().trim();
            String genero = tfGenero.getText().trim();
            String cumpleanos = tfCumpleanos.getText().trim();
            String password = tfPassword.getText().trim();
            
            if (codigo.isEmpty() || nombre.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios");
                return;
            }
            
            boolean ok = controller.addUser(new User(codigo, nombre, password, "CLIENTE"));
            JOptionPane.showMessageDialog(this, ok ? "Cliente creado" : "Error (código duplicado?)");
            refreshTable();
            limpiarFormulario();
        });

        btnActualizar.addActionListener(e -> {
    String codigo = tfCodigo.getText().trim();
    if (codigo.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ingrese código del cliente a actualizar");
        return;
    }
    
    User cliente = controller.findUserByCode(codigo);
    if (cliente != null && "CLIENTE".equals(cliente.getRol())) {
        cliente.setNombre(tfNombre.getText().trim());
        cliente.setPassword(tfPassword.getText().trim());
        controller.saveAll(); // ✅ GUARDAR INMEDIATAMENTE
        JOptionPane.showMessageDialog(this, "Cliente actualizado");
        refreshTable();
    } else {
        JOptionPane.showMessageDialog(this, "Cliente no encontrado");
    }
});

        btnEliminar.addActionListener(e -> {
            String codigo = tfCodigo.getText().trim();
            if (!codigo.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Está seguro de eliminar al cliente " + codigo + "?",
                    "Confirmar Eliminación", 
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = controller.deleteUser(codigo);
                    JOptionPane.showMessageDialog(this, ok ? "Cliente eliminado" : "No se pudo eliminar");
                    refreshTable();
                    limpiarFormulario();
                }
            }
        });

        btnCargarCSV.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                String filename = fc.getSelectedFile().getAbsolutePath();
                JOptionPane.showMessageDialog(this, "Funcionalidad de cargar CSV clientes en desarrollo");
            }
        });

        refreshTable();
    }

    private void limpiarFormulario() {
        tfCodigo.setText("");
        tfNombre.setText("");
        tfGenero.setText("");
        tfCumpleanos.setText("");
        tfPassword.setText("");
    }

    public void refreshTable(){
        User[] allUsers = controller.listUsers();
        java.util.List<User> clientes = new java.util.ArrayList<>();
        for (User u : allUsers) {
            if ("CLIENTE".equals(u.getRol())) {
                clientes.add(u);
            }
        }
        model.setUsers(clientes.toArray(new User[0]));
    }

    class ClientesTableModel extends AbstractTableModel {
        private String[] cols = {"Código", "Nombre", "Género"};
        private User[] users = new User[0];

        public void setUsers(User[] u) { 
            users = u == null ? new User[0] : u; 
            fireTableDataChanged(); 
        }
        public int getRowCount(){ return users.length; }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int i){ return cols[i]; }
        public Object getValueAt(int r, int c){
            User u = users[r];
            switch(c){ 
                case 0: return u.getCodigo(); 
                case 1: return u.getNombre(); 
                case 2: return "M"; // Temporal - deberías agregar género a User
                default: return ""; 
            }
        }
    }
}

// Subpanel de Pedidos para Vendedor
class PedidosVendedorPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private PedidosTableModel model;
    private JButton btnConfirmar;

    public PedidosVendedorPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new PedidosTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnConfirmar = new JButton("Confirmar Pedido");
        south.add(btnConfirmar);
        add(south, BorderLayout.SOUTH);

        btnConfirmar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String codigoPedido = (String) model.getValueAt(row, 0);
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar pedido " + codigoPedido + "?",
                    "Confirmar Pedido",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = controller.confirmarPedido(codigoPedido);
                    if (ok) {
                        JOptionPane.showMessageDialog(this, "Pedido confirmado exitosamente");
                        refreshTable();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al confirmar pedido");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un pedido de la tabla");
            }
        });

        refreshTable();
    }

    public void refreshTable(){
        Pedido[] pedidosPendientes = controller.getPedidosPendientes();
        model.setPedidos(pedidosPendientes);
    }

    class PedidosTableModel extends AbstractTableModel {
        private String[] cols = {"Código", "Fecha", "Cliente", "Total", "Estado"};
        private Pedido[] pedidos = new Pedido[0];

        public void setPedidos(Pedido[] p) { 
            pedidos = p == null ? new Pedido[0] : p; 
            fireTableDataChanged(); 
        }
        public int getRowCount(){ return pedidos.length; }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int i){ return cols[i]; }
        public Object getValueAt(int r, int c){
            Pedido p = pedidos[r];
            switch(c){ 
                case 0: return p.getCodigo(); 
                case 1: return p.getFechaFormateada(); 
                case 2: return p.getNombreCliente(); 
                case 3: return p.getTotalFormateado(); 
                case 4: return p.isConfirmado() ? "Confirmado" : "Pendiente"; 
                default: return ""; 
            }
        }
    }
}
