package com.proyecto2.vista;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import com.proyecto2.controlador.MainController;
import com.proyecto2.modelo.*;

public class ClientePanel extends JPanel {
    private MainController controller;
    private JTabbedPane tabs;
    private ProductosClientePanel productosPanel;
    private CarritoPanel carritoPanel;
    private HistorialComprasPanel historialPanel;

    public ClientePanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        
        tabs = new JTabbedPane();
        productosPanel = new ProductosClientePanel(controller, this); // ✅ Pasar referencia
        carritoPanel = new CarritoPanel(controller);
        historialPanel = new HistorialComprasPanel(controller);

        tabs.addTab("Productos", productosPanel);
        tabs.addTab("Carrito Compra", carritoPanel);
        tabs.addTab("Historial Compras", historialPanel);

        // ✅ Agregar listener para refrescar carrito al cambiar de pestaña
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedComponent() == carritoPanel) {
                carritoPanel.refreshCarrito();
            } else if (tabs.getSelectedComponent() == historialPanel) {
                historialPanel.refreshTable();
            }
        });

        add(tabs, BorderLayout.CENTER);
    }

    // ✅ Método público para refrescar el carrito desde otros paneles
    public void refrescarCarrito() {
        carritoPanel.refreshCarrito();
    }

    public void refreshAll(){
        productosPanel.refreshTable();
        carritoPanel.refreshCarrito();
        historialPanel.refreshTable();
    }
}

// Subpanel de Productos para Cliente - MODIFICADO
class ProductosClientePanel extends JPanel {
    private MainController controller;
    private ClientePanel clientePanel; // ✅ Referencia al panel padre
    private JTable table;
    private ProductsTableModel model;
    private JTextField tfCantidad;
    private JButton btnAgregarCarrito;

    public ProductosClientePanel(MainController controller, ClientePanel clientePanel){
        this.controller = controller;
        this.clientePanel = clientePanel; // ✅ Recibir referencia
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new ProductsTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        south.add(new JLabel("Cantidad:"));
        tfCantidad = new JTextField(5);
        btnAgregarCarrito = new JButton("Agregar al Carrito");
        
        south.add(tfCantidad);
        south.add(btnAgregarCarrito);
        add(south, BorderLayout.SOUTH);

        btnAgregarCarrito.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    String codigo = (String) model.getValueAt(row, 0);
                    int cantidad = Integer.parseInt(tfCantidad.getText().trim());
                    
                    Product p = controller.findProductByCode(codigo);
                    if (p != null) {
                        if (p.getStock() >= cantidad) {
                            controller.agregarAlCarrito(p, cantidad);
                            JOptionPane.showMessageDialog(this, "Producto agregado al carrito");
                            tfCantidad.setText("");
                            
                            // ✅ Refrescar el carrito inmediatamente
                            if (clientePanel != null) {
                                clientePanel.refrescarCarrito();
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "Stock insuficiente");
                        }
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla");
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

// Subpanel de Carrito para Cliente - MEJORADO
class CarritoPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private CarritoTableModel model;
    private JButton btnEliminar, btnActualizar, btnRealizarPedido;
    private JTextField tfNuevaCantidad;
    private JLabel lblTotal;

    public CarritoPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new CarritoTableModel();
        table = new JTable(model);
        
        // ✅ Configurar mejor la tabla
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                // Mostrar la cantidad actual en el campo de texto
                int row = table.getSelectedRow();
                int cantidad = (Integer) model.getValueAt(row, 2);
                tfNuevaCantidad.setText(String.valueOf(cantidad));
            }
        });
        
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel de controles
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Nueva Cantidad:"));
        tfNuevaCantidad = new JTextField(5);
        btnEliminar = new JButton("Eliminar");
        btnActualizar = new JButton("Actualizar Cantidad");
        btnRealizarPedido = new JButton("Realizar Pedido");
        
        controls.add(tfNuevaCantidad);
        controls.add(btnEliminar);
        controls.add(btnActualizar);
        controls.add(btnRealizarPedido);

        // Panel total
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Total: Q0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        totalPanel.add(lblTotal);

        JPanel south = new JPanel(new BorderLayout());
        south.add(controls, BorderLayout.WEST);
        south.add(totalPanel, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);

        // Listeners
        btnEliminar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                String codigo = (String) model.getValueAt(row, 0);
                controller.eliminarDelCarrito(codigo);
                refreshCarrito();
                tfNuevaCantidad.setText(""); // Limpiar campo
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar");
            }
        });

        btnActualizar.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                try {
                    String codigo = (String) model.getValueAt(row, 0);
                    int nuevaCantidad = Integer.parseInt(tfNuevaCantidad.getText().trim());
                    
                    if (nuevaCantidad > 0) {
                        Product p = controller.findProductByCode(codigo);
                        if (p != null && p.getStock() >= nuevaCantidad) {
                            controller.actualizarCantidadCarrito(codigo, nuevaCantidad);
                            refreshCarrito();
                            tfNuevaCantidad.setText(""); // Limpiar campo
                        } else {
                            JOptionPane.showMessageDialog(this, "Stock insuficiente");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor a 0");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un producto para actualizar");
            }
        });

        btnRealizarPedido.addActionListener(e -> {
            if (controller.getCarritoItemCount() > 0) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar pedido?\nTotal: " + String.format("Q%.2f", controller.getTotalCarrito()),
                    "Realizar Pedido",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = controller.realizarPedido();
                    if (ok) {
                        JOptionPane.showMessageDialog(this, "Pedido realizado exitosamente. Espera confirmación del vendedor.");
                        refreshCarrito();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error al realizar el pedido");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "El carrito está vacío");
            }
        });

        refreshCarrito();
    }

    public void refreshCarrito(){
        if (model != null) {
            model.setCarrito(controller.getCarrito());
            lblTotal.setText("Total: " + String.format("Q%.2f", controller.getTotalCarrito()));
            
            // ✅ Forzar repintado de la tabla
            if (table != null) {
                table.repaint();
            }
        }
    }

    class CarritoTableModel extends AbstractTableModel {
        private String[] cols = {"Código", "Nombre", "Cantidad", "Precio", "Subtotal"};
        private Product[] productos = new Product[0];
        private int[] cantidades = new int[0];
        
        public void setCarrito(Carrito carrito) {
            if (carrito != null && carrito.getItemCount() > 0) {
                // Filtrar productos que no sean null
                java.util.List<Product> productosList = new java.util.ArrayList<>();
                java.util.List<Integer> cantidadesList = new java.util.ArrayList<>();
                
                for (int i = 0; i < carrito.getItemCount(); i++) {
                    if (carrito.getProductos()[i] != null) {
                        productosList.add(carrito.getProductos()[i]);
                        cantidadesList.add(carrito.getCantidades()[i]);
                    }
                }
                
                productos = productosList.toArray(new Product[0]);
                cantidades = cantidadesList.stream().mapToInt(Integer::intValue).toArray();
            } else {
                productos = new Product[0];
                cantidades = new int[0];
            }
            fireTableDataChanged(); // ✅ Notificar cambios a la tabla
        }
        
        public int getRowCount(){ 
            return productos != null ? productos.length : 0; 
        }
        
        public int getColumnCount(){ return cols.length; }
        
        public String getColumnName(int i){ return cols[i]; }
        
        public Object getValueAt(int r, int c){
            // ✅ Verificar que el producto no sea null
            if (r >= productos.length || productos[r] == null) {
                return "";
            }
            
            Product p = productos[r];
            int cantidad = cantidades[r];
            
            switch(c){ 
                case 0: return p.getCodigo(); 
                case 1: return p.getNombre(); 
                case 2: return cantidad; 
                case 3: return p.getPrecioFormateado();
                case 4: return String.format("Q%.2f", p.getPrecio() * cantidad); 
                default: return ""; 
            }
        }
        
        @Override
        public boolean isCellEditable(int row, int column) {
            return false; // ✅ Hacer la tabla no editable
        }
    }
}

// Subpanel de Historial para Cliente (sin cambios)
class HistorialComprasPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private HistorialTableModel model;

    public HistorialComprasPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new HistorialTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshTable();
    }

    public void refreshTable(){
        Pedido[] pedidosConfirmados = controller.getPedidosConfirmados();
        model.setPedidos(pedidosConfirmados);
    }

    class HistorialTableModel extends AbstractTableModel {
        private String[] cols = {"Código", "Fecha Confirmación", "Total"};
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
                case 2: return p.getTotalFormateado(); 
                default: return ""; 
            }
        }
    }
}