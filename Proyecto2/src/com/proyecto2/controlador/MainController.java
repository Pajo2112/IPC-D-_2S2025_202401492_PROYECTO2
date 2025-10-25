package com.proyecto2.controlador;

import com.proyecto2.modelo.*;
import com.proyecto2.utiles.Bitacora;
import com.proyecto2.utiles.CSVUtil;
import com.proyecto2.utiles.PDFGenerator;

public class MainController {
    private DataStore store;
    private Bitacora bitacora;
    private Carrito carrito;
    private User usuarioActual;

    public MainController(DataStore store, Bitacora bitacora) {
        this.store = store;
        this.bitacora = bitacora;
        this.carrito = new Carrito();
    }

    // Autenticación
    public User authenticate(String codigo, String password){
        User u = store.findUserByCode(codigo);
        if (u != null && u.getPassword().equals(password)) {
            bitacora.append("LOGIN_OK", codigo);
            this.usuarioActual = u;
            return u;
        } else {
            bitacora.append("LOGIN_FAIL", codigo);
            return null;
        }
    }

    public void setUsuarioActual(User usuario) {
        this.usuarioActual = usuario;
    }

    // USUARIOS - CORREGIDO: Guardar inmediatamente después de modificar
    public boolean addUser(User u){
        boolean ok = store.addUser(u);
        if (ok) {
            store.saveToFile("datastore.ser"); // ✅ GUARDAR INMEDIATAMENTE
            bitacora.append("ADD_USER", u.getCodigo());
        }
        return ok;
    }

    public User[] listUsers(){ return store.listUsers(); }

    public boolean deleteUser(String codigo){
        boolean ok = store.deleteUser(codigo);
        if (ok) {
            store.saveToFile("datastore.ser"); // ✅ GUARDAR INMEDIATAMENTE
            bitacora.append("DEL_USER", codigo);
        }
        return ok;
    }

    public User findUserByCode(String codigo) {
        return store.findUserByCode(codigo);
    }

    // PRODUCTOS - CORREGIDO: Guardar inmediatamente después de modificar
    public boolean addProduct(Product p){
        boolean ok = store.addProduct(p);
        if (ok){
            store.saveToFile("datastore.ser"); // ✅ GUARDAR INMEDIATAMENTE
            bitacora.append("ADD_PRODUCT", p.getCodigo());
        }
        return ok;
    }

    public boolean updateProduct(String codigo, String nombre, String categoria, double precio, int stock){
        boolean ok = store.updateProduct(codigo, nombre, categoria, precio, stock);
        if (ok){
            store.saveToFile("datastore.ser"); // ✅ GUARDAR INMEDIATAMENTE
            bitacora.append("UPDATE_PRODUCT", codigo);
        }
        return ok;
    }

    // Nuevo método para actualizar producto con atributo específico
    public boolean updateProduct(String codigo, String nombre, String categoria, double precio, int stock, String atributoEspecifico) {
        Product p = store.findProductByCode(codigo);
        if (p == null) return false;
        
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setAtributoEspecifico(atributoEspecifico);
        
        store.saveToFile("datastore.ser"); // ✅ GUARDAR INMEDIATAMENTE
        bitacora.append("UPDATE_PRODUCT", codigo);
        return true;
    }

    public boolean deleteProduct(String codigo){
        boolean ok = store.deleteProduct(codigo);
        if (ok){
            store.saveToFile("datastore.ser"); // ✅ GUARDAR INMEDIATAMENTE
            bitacora.append("DEL_PRODUCT", codigo);
        }
        return ok;
    }

    public Product[] listProducts(){ return store.listProducts(); }

    public Product findProductByCode(String codigo) {
        return store.findProductByCode(codigo);
    }

    public String getDetalleProducto(String codigo) {
        Product p = store.findProductByCode(codigo);
        return p != null ? p.getDetalleCategoria() : "Producto no encontrado";
    }

    // MÉTODOS PARA EL CARRITO
    public void agregarAlCarrito(Product producto, int cantidad) {
        carrito.agregarProducto(producto, cantidad);
    }

    public void eliminarDelCarrito(String codigoProducto) {
        carrito.eliminarProducto(codigoProducto);
    }

    public void actualizarCantidadCarrito(String codigoProducto, int nuevaCantidad) {
        carrito.actualizarCantidad(codigoProducto, nuevaCantidad);
    }

    public boolean realizarPedido() {
        if (usuarioActual == null || carrito.getItemCount() == 0) return false;
        
        String codigoPedido = "PE-" + System.currentTimeMillis();
        Pedido pedido = new Pedido(codigoPedido, usuarioActual.getCodigo(), usuarioActual.getNombre());
        
        // ✅ VERIFICAR que los productos del carrito no sean null
        for (int i = 0; i < carrito.getItemCount(); i++) {
            Product p = carrito.getProductos()[i];
            int cantidad = carrito.getCantidades()[i];
            if (p != null) {
                pedido.agregarProducto(p, cantidad);
            }
        }
        
        // ✅ VERIFICAR que el pedido tenga productos
        if (pedido.getProductCount() == 0) return false;
        
        boolean ok = store.agregarPedido(pedido);
        if (ok) {
            carrito.limpiar();
            store.saveToFile("datastore.ser"); // ✅ GUARDAR INMEDIATAMENTE
            bitacora.append("REALIZAR_PEDIDO", usuarioActual.getCodigo() + " - " + codigoPedido);
        }
        return ok;
    }

    public Carrito getCarrito() {
        return carrito;
    }

    public int getCarritoItemCount() {
        return carrito.getItemCount();
    }

    public double getTotalCarrito() {
        return carrito.calcularTotal();
    }

    // MÉTODOS PARA PEDIDOS (VENDEDOR)
    public Pedido[] getPedidosPendientes() {
        return store.getPedidosPendientes();
    }

    public boolean confirmarPedido(String codigoPedido) {
        if (usuarioActual == null || !"VENDEDOR".equals(usuarioActual.getRol())) return false;
        
        boolean ok = store.confirmarPedido(codigoPedido, usuarioActual.getCodigo());
        if (ok) {
            store.saveToFile("datastore.ser"); // ✅ GUARDAR INMEDIATAMENTE
            bitacora.append("CONFIRMAR_PEDIDO", usuarioActual.getCodigo() + " - " + codigoPedido);
        }
        return ok;
    }

    public Pedido[] getPedidosConfirmados() {
        return store.getPedidosConfirmados();
    }

    // MÉTODOS PARA REPORTES
    public Product[] getProductosMasVendidos() {
        return store.getProductosMasVendidos();
    }

    public Product[] getProductosMenosVendidos() {
        return store.getProductosMenosVendidos();
    }

    // MÉTODOS PARA GENERAR REPORTES PDF
    public boolean generateProductosMasVendidosPDF(String filename) {
        try {
            Product[] productos = store.getProductosMasVendidos();
            PDFGenerator.createProductosMasVendidosReport(productos, filename);
            bitacora.append("PDF_MAS_VENDIDOS", filename);
            return true;
        } catch(Exception e){ 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean generateProductosMenosVendidosPDF(String filename) {
        try {
            Product[] productos = store.getProductosMenosVendidos();
            PDFGenerator.createProductosMenosVendidosReport(productos, filename);
            bitacora.append("PDF_MENOS_VENDIDOS", filename);
            return true;
        } catch(Exception e){ 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean generateInventarioPDF(String filename) {
        try {
            Product[] productos = store.listProducts();
            PDFGenerator.createInventarioReport(productos, filename);
            bitacora.append("PDF_INVENTARIO", filename);
            return true;
        } catch(Exception e){ 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean generateVentasPorVendedorPDF(String filename) {
        try {
            User[] usuarios = store.listUsers();
            Pedido[] pedidos = store.getPedidosConfirmados();
            PDFGenerator.createVentasPorVendedorReport(usuarios, pedidos, filename);
            bitacora.append("PDF_VENTAS_VENDEDOR", filename);
            return true;
        } catch(Exception e){ 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean generateClientesActivosPDF(String filename) {
        try {
            User[] usuarios = store.listUsers();
            Pedido[] pedidos = store.getPedidosConfirmados();
            PDFGenerator.createClientesActivosReport(usuarios, pedidos, filename);
            bitacora.append("PDF_CLIENTES_ACTIVOS", filename);
            return true;
        } catch(Exception e){ 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean generateReporteFinancieroPDF(String filename) {
        try {
            Product[] productos = store.listProducts();
            Pedido[] pedidos = store.getPedidosConfirmados();
            PDFGenerator.createReporteFinancieroReport(productos, pedidos, filename);
            bitacora.append("PDF_FINANCIERO", filename);
            return true;
        } catch(Exception e){ 
            e.printStackTrace(); 
            return false; 
        }
    }

    public boolean generateProductosPorCaducarPDF(String filename) {
        try {
            Product[] productos = store.listProducts();
            PDFGenerator.createProductosPorCaducarReport(productos, filename);
            bitacora.append("PDF_POR_CADUCAR", filename);
            return true;
        } catch(Exception e){ 
            e.printStackTrace(); 
            return false; 
        }
    }

    // CSV Import/Export
    public boolean exportProductsToCSV(String filename){
        try {
            CSVUtil.exportProducts(store.listProducts(), filename);
            bitacora.append("EXPORT_CSV", filename);
            return true;
        } catch(Exception e){ return false; }
    }

    public boolean importProductsFromCSV(String filename){
        try {
            Product[] imported = CSVUtil.importProducts(filename);
            for (Product p : imported) {
                if (store.findProductByCode(p.getCodigo()) == null) store.addProduct(p);
            }
            store.saveToFile("datastore.ser"); // ✅ GUARDAR después de importar
            bitacora.append("IMPORT_CSV", filename);
            return true;
        } catch(Exception e){ e.printStackTrace(); return false; }
    }

    // PDF report básico
    public boolean generateProductsPDF(String filename){
        try {
            PDFGenerator.createProductReport(store.listProducts(), filename);
            bitacora.append("PDF_PRODUCTS", filename);
            return true;
        } catch(Exception e){ e.printStackTrace(); return false; }
    }

    // Persistence helpers
    public void saveAll(){ 
        store.saveToFile("datastore.ser"); // ✅ Método para guardar manualmente
    }
    
    public static DataStore loadStore(){ return DataStore.loadFromFile("datastore.ser"); }
    
    // ✅ NUEVO: Método para obtener el DataStore (útil para debugging)
    public DataStore getDataStore() {
        return store;
    }
}