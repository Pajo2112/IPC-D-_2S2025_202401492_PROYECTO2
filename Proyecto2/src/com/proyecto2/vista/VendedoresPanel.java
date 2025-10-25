package com.proyecto2.vista;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import com.proyecto2.controlador.MainController;
import com.proyecto2.modelo.User;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

public class VendedoresPanel extends JPanel {
    private MainController controller;
    private JTable table;
    private VendedoresTableModel model;
    private JTextField tfCodigo, tfNombre, tfPassword, tfSearch;
    private JComboBox<String> cbGenero;
    private JButton btnCrear, btnActualizar, btnEliminar, btnCargarCSV, btnSearch, btnClearSearch;
    private User[] allVendedores;

    public VendedoresPanel(MainController controller){
        this.controller = controller;
        init();
    }

    private void init(){
        setLayout(new BorderLayout());
        model = new VendedoresTableModel();
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel de formulario
        JPanel form = new JPanel(new GridLayout(2, 5, 4, 4));
        tfCodigo = new JTextField();
        tfNombre = new JTextField();
        tfPassword = new JTextField();
        cbGenero = new JComboBox<>(new String[]{"M", "F"});
        btnCrear = new JButton("Crear Vendedor");

        form.add(new JLabel("Código*"));
        form.add(new JLabel("Nombre*"));
        form.add(new JLabel("Contraseña*"));
        form.add(new JLabel("Género*"));
        form.add(new JLabel(""));
        form.add(tfCodigo);
        form.add(tfNombre);
        add(tfPassword);
        form.add(cbGenero);
        form.add(btnCrear);

        add(form, BorderLayout.NORTH);

        // Panel de controles inferiores
        JPanel south = new JPanel(new BorderLayout());
        
        // Panel de botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnActualizar = new JButton("Actualizar");
        btnEliminar = new JButton("Eliminar");
        btnCargarCSV = new JButton("Cargar CSV");
        
        actionPanel.add(btnActualizar);
        actionPanel.add(btnEliminar);
        actionPanel.add(btnCargarCSV);

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Buscar:"));
        tfSearch = new JTextField(15);
        btnSearch = new JButton("Buscar");
        btnClearSearch = new JButton("Limpiar");
        searchPanel.add(tfSearch);
        searchPanel.add(btnSearch);
        searchPanel.add(btnClearSearch);

        south.add(actionPanel, BorderLayout.WEST);
        south.add(searchPanel, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);

        // Listeners
        btnCrear.addActionListener(e -> crearVendedor());
        
        btnActualizar.addActionListener(e -> actualizarVendedor());

        btnEliminar.addActionListener(e -> eliminarVendedor());

        btnCargarCSV.addActionListener(e -> cargarVendedoresCSV());

        btnSearch.addActionListener(e -> {
            String searchText = tfSearch.getText().trim().toLowerCase();
            aplicarFiltro(searchText);
        });

        btnClearSearch.addActionListener(e -> {
            tfSearch.setText("");
            aplicarFiltro("");
        });

        // Listener para selección de tabla
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    cargarDatosVendedorSeleccionado(row);
                }
            }
        });

        refreshTable();
    }

    private void crearVendedor() {
        String codigo = tfCodigo.getText().trim();
        String nombre = tfNombre.getText().trim();
        String password = tfPassword.getText().trim();
        String genero = (String) cbGenero.getSelectedItem();

        if (codigo.isEmpty() || nombre.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Complete todos los campos obligatorios (*)");
            return;
        }

        // Validar formato de código (VE-XXX)
        if (!codigo.matches("VE-\\d+")) {
            JOptionPane.showMessageDialog(this, 
                "Formato de código inválido. Debe ser VE- seguido de números (ej: VE-001)");
            return;
        }

        // Validar que el código no exista
        User usuarioExistente = controller.findUserByCode(codigo);
        if (usuarioExistente != null) {
            JOptionPane.showMessageDialog(this, 
                "El código de vendedor ya existe en el sistema");
            return;
        }

        // Crear vendedor con rol VENDEDOR
        User nuevoVendedor = new User(codigo, nombre, password, "VENDEDOR", genero, "");
        boolean ok = controller.addUser(nuevoVendedor);
        
        if (ok) {
            JOptionPane.showMessageDialog(this, "Vendedor creado exitosamente");
            limpiarFormulario();
            refreshTable();
        } else {
            JOptionPane.showMessageDialog(this, "Error al crear el vendedor");
        }
    }

    private void actualizarVendedor() {
        String codigo = tfCodigo.getText().trim();
        
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código del vendedor a actualizar");
            return;
        }

        User vendedor = controller.findUserByCode(codigo);
        if (vendedor == null || !"VENDEDOR".equals(vendedor.getRol())) {
            JOptionPane.showMessageDialog(this, "Vendedor no encontrado");
            return;
        }

        // Actualizar solo nombre y contraseña (según requisitos)
        String nuevoNombre = tfNombre.getText().trim();
        String nuevaPassword = tfPassword.getText().trim();
        
        if (nuevoNombre.isEmpty() || nuevaPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y contraseña no pueden estar vacíos");
            return;
        }

        vendedor.setNombre(nuevoNombre);
        vendedor.setPassword(nuevaPassword);
        
        // Guardar cambios inmediatamente
        controller.saveAll();
        JOptionPane.showMessageDialog(this, "Vendedor actualizado exitosamente");
        refreshTable();
    }

    private void eliminarVendedor() {
        String codigo = tfCodigo.getText().trim();
        
        if (codigo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el código del vendedor a eliminar");
            return;
        }

        User vendedor = controller.findUserByCode(codigo);
        if (vendedor == null || !"VENDEDOR".equals(vendedor.getRol())) {
            JOptionPane.showMessageDialog(this, "Vendedor no encontrado");
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar al vendedor " + codigo + " - " + vendedor.getNombre() + "?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            boolean ok = controller.deleteUser(codigo);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Vendedor eliminado exitosamente");
                limpiarFormulario();
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Error al eliminar el vendedor");
            }
        }
    }

    private void cargarVendedoresCSV() {
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
                        "Formato CSV inválido. Encabezado debe contener: codigo,nombre,genero,contrasena");
                    return;
                }
                
                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();
                    if (line.isEmpty()) continue;
                    
                    String[] parts = line.split(",");
                    if (parts.length >= 4) {
                        String codigo = parts[0].trim();
                        String nombre = parts[1].trim();
                        String genero = parts[2].trim().toUpperCase();
                        String contrasena = parts[3].trim();
                        
                        // Validaciones
                        if (!codigo.matches("VE-\\d+")) {
                            System.err.println("Línea " + lineNumber + ": Formato código inválido - " + codigo);
                            errorCount++;
                            continue;
                        }
                        
                        if (!genero.equals("M") && !genero.equals("F")) {
                            System.err.println("Línea " + lineNumber + ": Género inválido - " + genero);
                            errorCount++;
                            continue;
                        }
                        
                        // Verificar si ya existe
                        User existente = controller.findUserByCode(codigo);
                        if (existente != null) {
                            System.err.println("Línea " + lineNumber + ": Código duplicado - " + codigo);
                            errorCount++;
                            continue;
                        }
                        
                        // Crear vendedor
                        User nuevoVendedor = new User(codigo, nombre, contrasena, "VENDEDOR", genero, "");
                        boolean ok = controller.addUser(nuevoVendedor);
                        
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
                    "• Vendedores creados: " + successCount + "\n" +
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

    private void cargarDatosVendedorSeleccionado(int row) {
        User vendedor = allVendedores[row];
        tfCodigo.setText(vendedor.getCodigo());
        tfNombre.setText(vendedor.getNombre());
        tfPassword.setText(vendedor.getPassword());
        
        // Establecer género si está disponible
        if (vendedor.getGenero() != null && !vendedor.getGenero().isEmpty()) {
            cbGenero.setSelectedItem(vendedor.getGenero());
        }
    }

    private void aplicarFiltro(String searchText) {
        if (allVendedores == null) return;
        
        if (searchText.isEmpty()) {
            model.setVendedores(allVendedores);
            return;
        }
        
        List<User> filtrados = new ArrayList<>();
        for (User vendedor : allVendedores) {
            if (vendedor.getCodigo().toLowerCase().contains(searchText) ||
                vendedor.getNombre().toLowerCase().contains(searchText) ||
                (vendedor.getGenero() != null && vendedor.getGenero().toLowerCase().contains(searchText))) {
                filtrados.add(vendedor);
            }
        }
        model.setVendedores(filtrados.toArray(new User[0]));
    }

    private void limpiarFormulario() {
        tfCodigo.setText("");
        tfNombre.setText("");
        tfPassword.setText("");
        cbGenero.setSelectedIndex(0);
    }

    public void refreshTable(){
        User[] allUsers = controller.listUsers();
        List<User> vendedores = new ArrayList<>();
        for (User u : allUsers) {
            if ("VENDEDOR".equals(u.getRol())) {
                vendedores.add(u);
            }
        }
        allVendedores = vendedores.toArray(new User[0]);
        aplicarFiltro(tfSearch.getText().trim().toLowerCase());
    }

    class VendedoresTableModel extends AbstractTableModel {
        private String[] cols = {"Código", "Nombre", "Género", "Ventas Confirmadas"};
        private User[] vendedores = new User[0];

        public void setVendedores(User[] v) { 
            vendedores = v == null ? new User[0] : v; 
            fireTableDataChanged(); 
        }
        
        public int getRowCount(){ return vendedores.length; }
        public int getColumnCount(){ return cols.length; }
        public String getColumnName(int i){ return cols[i]; }
        
        public Object getValueAt(int r, int c){
            User v = vendedores[r];
            switch(c){ 
                case 0: return v.getCodigo(); 
                case 1: return v.getNombre(); 
                case 2: return v.getGenero() != null ? v.getGenero() : "M"; 
                case 3: return calcularVentasConfirmadas(v.getCodigo());
                default: return ""; 
            }
        }
        
        private int calcularVentasConfirmadas(String codigoVendedor) {
            // Contar pedidos confirmados por este vendedor
            int ventas = 0;
            try {
                // Usar reflexión para acceder al DataStore y contar pedidos
                var dataStore = controller.getDataStore();
                var pedidosConfirmados = dataStore.getPedidosConfirmados();
                
                for (var pedido : pedidosConfirmados) {
                    if (pedido != null && codigoVendedor.equals(pedido.getCodigoVendedor())) {
                        ventas++;
                    }
                }
            } catch (Exception e) {
                // En caso de error, retornar 0
                System.err.println("Error calculando ventas: " + e.getMessage());
            }
            return ventas;
        }
    }
}
