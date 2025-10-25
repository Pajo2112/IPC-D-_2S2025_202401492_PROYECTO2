package com.proyecto2.vista;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import com.proyecto2.controlador.MainController;
import com.proyecto2.modelo.Product;
import java.io.BufferedReader;
import java.io.FileReader;

public class ProductsPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private ProductsTableModel model;
    private JTextField tfCodigo, tfNombre, tfCategoria, tfPrecio, tfStock, tfAtributo;
    private JButton btnAdd, btnUpdate, btnDelete, btnRefresh, btnCargarCSV;

    public ProductsPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new ProductsTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 7, 4, 4));
        tfCodigo = new JTextField(); 
        tfNombre = new JTextField(); 
        tfCategoria = new JTextField();
        tfPrecio = new JTextField(); 
        tfStock = new JTextField();
        tfAtributo = new JTextField();

        form.add(new JLabel("Código*")); 
        form.add(new JLabel("Nombre*")); 
        form.add(new JLabel("Categoría*"));
        form.add(new JLabel("Precio*")); 
        form.add(new JLabel("Stock*"));
        form.add(new JLabel("Atributo*"));
        form.add(new JLabel());
        
        form.add(tfCodigo); 
        form.add(tfNombre); 
        form.add(tfCategoria); 
        form.add(tfPrecio); 
        form.add(tfStock);
        form.add(tfAtributo);

        add(form, BorderLayout.NORTH);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnAdd = new JButton("Agregar"); 
        btnUpdate = new JButton("Actualizar"); 
        btnDelete = new JButton("Eliminar"); 
        btnRefresh = new JButton("Refrescar");
        btnCargarCSV = new JButton("Cargar CSV");
        south.add(btnAdd); 
        south.add(btnUpdate); 
        south.add(btnDelete); 
        south.add(btnRefresh);
        south.add(btnCargarCSV);
        add(south, BorderLayout.SOUTH);

        btnAdd.addActionListener(e-> {
            try {
                String c = tfCodigo.getText().trim();
                String n = tfNombre.getText().trim();
                String cat = tfCategoria.getText().trim();
                String precioText = tfPrecio.getText().trim().replace("Q", "").replace("q", "").trim();
                double precio = Double.parseDouble(precioText);
                int s = Integer.parseInt(tfStock.getText().trim());
                String atributo = tfAtributo.getText().trim();
                
                // Validaciones
                if (c.isEmpty() || n.isEmpty() || cat.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios (*)");
                    return;
                }
                
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0");
                    return;
                }
                
                if (s < 0) {
                    JOptionPane.showMessageDialog(this, "El stock no puede ser negativo");
                    return;
                }
                
                // Validar atributo según categoría
                String errorAtributo = validarAtributoPorCategoria(cat, atributo);
                if (errorAtributo != null) {
                    JOptionPane.showMessageDialog(this, errorAtributo);
                    return;
                }
                
                boolean ok = controller.addProduct(new Product(c, n, cat, precio, s, atributo));
                JOptionPane.showMessageDialog(this, ok ? "Producto agregado exitosamente" : "Error: Código duplicado");
                refreshTable();
                clearForm();
            } catch(NumberFormatException ex){ 
                JOptionPane.showMessageDialog(this, "Error: Precio y Stock deben ser números válidos");
            } catch(Exception ex){ 
                JOptionPane.showMessageDialog(this, "Error en datos: " + ex.getMessage()); 
            }
        });

        btnUpdate.addActionListener(e-> {
            try {
                String c = tfCodigo.getText().trim();
                String n = tfNombre.getText().trim();
                String cat = tfCategoria.getText().trim();
                String precioText = tfPrecio.getText().trim().replace("Q", "").replace("q", "").trim();
                double precio = Double.parseDouble(precioText);
                int s = Integer.parseInt(tfStock.getText().trim());
                String atributo = tfAtributo.getText().trim();
                
                // Validaciones
                if (c.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Ingrese el código del producto a actualizar");
                    return;
                }
                
                if (n.isEmpty() || cat.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nombre y categoría no pueden estar vacíos");
                    return;
                }
                
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(this, "El precio debe ser mayor a 0");
                    return;
                }
                
                if (s < 0) {
                    JOptionPane.showMessageDialog(this, "El stock no puede ser negativo");
                    return;
                }
                
                // Validar atributo según categoría
                String errorAtributo = validarAtributoPorCategoria(cat, atributo);
                if (errorAtributo != null) {
                    JOptionPane.showMessageDialog(this, errorAtributo);
                    return;
                }
                
                boolean ok = controller.updateProduct(c, n, cat, precio, s, atributo);
                JOptionPane.showMessageDialog(this, ok ? "Producto actualizado exitosamente" : "Producto no encontrado");
                refreshTable();
            } catch(NumberFormatException ex){ 
                JOptionPane.showMessageDialog(this, "Error: Precio y Stock deben ser números válidos");
            } catch(Exception ex){ 
                JOptionPane.showMessageDialog(this, "Error en datos: " + ex.getMessage()); 
            }
        });

        btnDelete.addActionListener(e-> {
            int r = table.getSelectedRow();
            if (r>=0){
                String code = (String) model.getValueAt(r,0);
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de eliminar el producto " + code + "?",
                    "Confirmar Eliminación",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean ok = controller.deleteProduct(code);
                    JOptionPane.showMessageDialog(this, ok ? "Producto eliminado exitosamente" : "Error al eliminar");
                    refreshTable();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla");
            }
        });

        btnRefresh.addActionListener(e-> refreshTable());
        
        btnCargarCSV.addActionListener(e-> cargarProductosCSV());
        
        tfPrecio.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                formatPrecioField();
            }
        });
        
        // Listener para selección de tabla
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    tfCodigo.setText((String) model.getValueAt(row, 0));
                    tfNombre.setText((String) model.getValueAt(row, 1));
                    tfCategoria.setText((String) model.getValueAt(row, 2));
                    
                    String precio = (String) model.getValueAt(row, 3);
                    tfPrecio.setText(precio.replace("Q", ""));
                    
                    tfStock.setText(model.getValueAt(row, 4).toString());
                    
                    // Obtener el atributo específico del producto
                    Product p = controller.findProductByCode((String) model.getValueAt(row, 0));
                    if (p != null) {
                        tfAtributo.setText(p.getAtributoEspecifico());
                    }
                }
            }
        });
    }
    
    private String validarAtributoPorCategoria(String categoria, String atributo) {
        if (atributo == null || atributo.trim().isEmpty()) {
            return "El atributo específico es obligatorio";
        }
        
        categoria = categoria.toUpperCase();
        switch(categoria) {
            case "TECNOLOGIA":
                try {
                    int meses = Integer.parseInt(atributo.trim());
                    if (meses <= 0) {
                        return "Los meses de garantía deben ser mayores a 0";
                    }
                } catch (NumberFormatException e) {
                    return "Para tecnología, el atributo debe ser número de meses de garantía (ej: 12)";
                }
                break;
                
            case "ALIMENTO":
                // Validar formato de fecha dd/mm/aaaa
                if (!atributo.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    return "Para alimento, el atributo debe ser fecha de caducidad (formato: dd/mm/aaaa)";
                }
                break;
                
            case "GENERAL":
                if (atributo.trim().length() < 2) {
                    return "Para categoría general, el atributo debe describir el material (ej: plástico, madera)";
                }
                break;
                
            default:
                return "Categoría no reconocida. Use: TECNOLOGIA, ALIMENTO o GENERAL";
        }
        
        return null; // Sin error
    }
    
    private void cargarProductosCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filename = fc.getSelectedFile().getAbsolutePath();
            
            try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
                String line;
                int lineNumber = 0;
                int successCount = 0;
                int errorCount = 0;
                
                // Leer y validar encabezado
                String header = br.readLine();
                if (header == null || !header.toLowerCase().contains("codigo")) {
                    JOptionPane.showMessageDialog(this, 
                        "Formato CSV inválido. Encabezado debe contener: codigo,nombre,categoria,atributo_especifico,precio,stock");
                    return;
                }
                
                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        String codigo = parts[0].trim();
                        String nombre = parts[1].trim();
                        String categoria = parts[2].trim().toUpperCase();
                        String atributo = parts[3].trim();
                        double precio = 0.0;
                        int stock = 0;
                        
                        try {
                            precio = Double.parseDouble(parts[4].trim());
                            stock = Integer.parseInt(parts[5].trim());
                        } catch (NumberFormatException e) {
                            System.err.println("Línea " + lineNumber + ": Precio o stock inválido");
                            errorCount++;
                            continue;
                        }
                        
                        // Validaciones
                        if (codigo.isEmpty()) {
                            System.err.println("Línea " + lineNumber + ": Código vacío");
                            errorCount++;
                            continue;
                        }
                        
                        if (precio <= 0) {
                            System.err.println("Línea " + lineNumber + ": Precio debe ser mayor a 0");
                            errorCount++;
                            continue;
                        }
                        
                        if (stock < 0) {
                            System.err.println("Línea " + lineNumber + ": Stock no puede ser negativo");
                            errorCount++;
                            continue;
                        }
                        
                        // Validar atributo según categoría
                        String errorAtributo = validarAtributoPorCategoria(categoria, atributo);
                        if (errorAtributo != null) {
                            System.err.println("Línea " + lineNumber + ": " + errorAtributo);
                            errorCount++;
                            continue;
                        }
                        
                        // Verificar si ya existe
                        Product existente = controller.findProductByCode(codigo);
                        if (existente != null) {
                            System.err.println("Línea " + lineNumber + ": Código duplicado - " + codigo);
                            errorCount++;
                            continue;
                        }
                        
                        // Crear producto
                        Product nuevoProducto = new Product(codigo, nombre, categoria, precio, stock, atributo);
                        boolean ok = controller.addProduct(nuevoProducto);
                        
                        if (ok) {
                            successCount++;
                        } else {
                            errorCount++;
                        }
                    } else {
                        System.err.println("Línea " + lineNumber + ": Formato inválido - " + line);
                        errorCount++;
                    }
                }
                
                // Mostrar resultados
                JOptionPane.showMessageDialog(this,
                    "Carga CSV completada:\n" +
                    "• Productos creados: " + successCount + "\n" +
                    "• Errores: " + errorCount,
                    "Resultado Carga CSV",
                    JOptionPane.INFORMATION_MESSAGE);
                
                refreshTable();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error al leer archivo CSV: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    private void formatPrecioField() {
        String text = tfPrecio.getText();
        if (!text.startsWith("Q") && !text.isEmpty() && !text.equals("0")) {
            tfPrecio.setText("Q" + text.replace("Q", ""));
        }
    }
    
    private void clearForm() {
        tfCodigo.setText("");
        tfNombre.setText("");
        tfCategoria.setText("");
        tfPrecio.setText("");
        tfStock.setText("");
        tfAtributo.setText("");
    }

    public void refreshTable(){
        Product[] list = controller.listProducts();
        model.setProducts(list);
    }

    class ProductsTableModel extends AbstractTableModel {
        private String[] cols = {"Código","Nombre","Categoría","Precio","Stock"};
        private Product[] prods = new Product[0];
        public void setProducts(Product[] p){ 
            prods = p == null ? new Product[0] : p; 
            fireTableDataChanged(); 
        }
        public int getRowCount(){ return prods.length; }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int i){ return cols[i]; }
        public Object getValueAt(int r,int c){
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
        
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 3 || column == 4;
        }
        
        @Override
        public void setValueAt(Object value, int row, int column) {
            Product p = prods[row];
            
            if (column == 3) {
                try {
                    String precioText = value.toString().replace("Q", "").replace("q", "").trim();
                    double nuevoPrecio = Double.parseDouble(precioText);
                    if (nuevoPrecio <= 0) {
                        JOptionPane.showMessageDialog(ProductsPanel.this, "El precio debe ser mayor a 0");
                        return;
                    }
                    controller.updateProduct(p.getCodigo(), p.getNombre(), p.getCategoria(), nuevoPrecio, p.getStock(), p.getAtributoEspecifico());
                    refreshTable();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(ProductsPanel.this, "Precio debe ser un número (ej: 25.50)");
                }
            }
            else if (column == 4) {
                try {
                    int nuevoStock = Integer.parseInt(value.toString());
                    if (nuevoStock < 0) {
                        JOptionPane.showMessageDialog(ProductsPanel.this, "El stock no puede ser negativo");
                        return;
                    }
                    controller.updateProduct(p.getCodigo(), p.getNombre(), p.getCategoria(), p.getPrecio(), nuevoStock, p.getAtributoEspecifico());
                    refreshTable();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(ProductsPanel.this, "Stock debe ser un número entero");
                }
            }
        }
    }
}