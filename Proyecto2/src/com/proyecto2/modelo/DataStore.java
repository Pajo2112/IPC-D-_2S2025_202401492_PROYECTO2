package com.proyecto2.modelo;

import java.io.*;
import java.time.LocalDateTime;

public class DataStore implements Serializable {
    private static final long serialVersionUID = 1L;

    private User[] users;
    private Product[] products;
    private Pedido[] pedidos;
    private int userCount;
    private int productCount;
    private int pedidoCount;

    public static final int MAX_USERS = 500;
    public static final int MAX_PRODUCTS = 2000;
    public static final int MAX_PEDIDOS = 1000;

    public DataStore() {
        users = new User[MAX_USERS];
        products = new Product[MAX_PRODUCTS];
        pedidos = new Pedido[MAX_PEDIDOS]; // ✅ INICIALIZAR el array
        userCount = 0;
        productCount = 0;
        pedidoCount = 0;
        ensureDefaultAdmin();
    }

    private void ensureDefaultAdmin(){
        if (findUserByCode("admin") == null) {
            addUser(new User("admin","Administrador","IPC1D","ADMIN"));
        }
    }

    // Users
    public boolean addUser(User u){
        if (userCount >= MAX_USERS) return false;
        if (findUserByCode(u.getCodigo()) != null) return false;
        users[userCount++] = u;
        return true;
    }

    public User findUserByCode(String code){
        for (int i=0;i<userCount;i++){
            if (users[i].getCodigo().equalsIgnoreCase(code)) return users[i];
        }
        return null;
    }

    public boolean deleteUser(String code){
        for (int i=0;i<userCount;i++){
            if (users[i].getCodigo().equalsIgnoreCase(code)){
                for (int j=i;j<userCount-1;j++) users[j]=users[j+1];
                users[--userCount]=null;
                return true;
            }
        }
        return false;
    }

    public User[] listUsers(){
        User[] out = new User[userCount];
        for (int i=0;i<userCount;i++) out[i]=users[i];
        return out;
    }

    // Products
    public boolean addProduct(Product p){
        if (productCount >= MAX_PRODUCTS) return false;
        if (findProductByCode(p.getCodigo()) != null) return false;
        products[productCount++] = p;
        return true;
    }

    public Product findProductByCode(String code){
        for (int i=0;i<productCount;i++){
            if (products[i].getCodigo().equalsIgnoreCase(code)) return products[i];
        }
        return null;
    }

    public boolean updateProduct(String code, String nombre, String categoria, double precio, int stock){
        Product p = findProductByCode(code);
        if (p == null) return false;
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setPrecio(precio);
        p.setStock(stock);
        return true;
    }

    public boolean updateProduct(String code, String nombre, String categoria, double precio, int stock, String atributoEspecifico){
        Product p = findProductByCode(code);
        if (p == null) return false;
        p.setNombre(nombre);
        p.setCategoria(categoria);
        p.setPrecio(precio);
        p.setStock(stock);
        p.setAtributoEspecifico(atributoEspecifico);
        return true;
    }

    public boolean deleteProduct(String code){
        for (int i=0;i<productCount;i++){
            if (products[i].getCodigo().equalsIgnoreCase(code)){
                for (int j=i;j<productCount-1;j++) products[j]=products[j+1];
                products[--productCount]=null;
                return true;
            }
        }
        return false;
    }

    public Product[] listProducts(){
        Product[] out = new Product[productCount];
        for (int i=0;i<productCount;i++) out[i]=products[i];
        return out;
    }

    // ✅ NUEVO: Métodos para Pedidos - CORREGIDOS
    public boolean agregarPedido(Pedido pedido) {
        // ✅ VERIFICAR que pedidos no sea null
        if (pedidos == null) {
            pedidos = new Pedido[MAX_PEDIDOS];
            pedidoCount = 0;
        }
        
        if (pedidoCount >= MAX_PEDIDOS) return false;
        pedidos[pedidoCount++] = pedido;
        return true;
    }

    public Pedido[] getPedidosPendientes() {
        // ✅ VERIFICAR que pedidos no sea null
        if (pedidos == null) {
            pedidos = new Pedido[MAX_PEDIDOS];
            return new Pedido[0];
        }
        
        int count = 0;
        for (int i = 0; i < pedidoCount; i++) {
            if (pedidos[i] != null && !pedidos[i].isConfirmado()) count++;
        }
        
        Pedido[] pendientes = new Pedido[count];
        int index = 0;
        for (int i = 0; i < pedidoCount; i++) {
            if (pedidos[i] != null && !pedidos[i].isConfirmado()) {
                pendientes[index++] = pedidos[i];
            }
        }
        return pendientes;
    }

    public Pedido findPedidoByCodigo(String codigo) {
        if (pedidos == null) return null;
        
        for (int i = 0; i < pedidoCount; i++) {
            if (pedidos[i] != null && pedidos[i].getCodigo().equalsIgnoreCase(codigo)) {
                return pedidos[i];
            }
        }
        return null;
    }

    public boolean confirmarPedido(String codigoPedido, String codigoVendedor) {
        Pedido pedido = findPedidoByCodigo(codigoPedido);
        if (pedido == null || pedido.isConfirmado()) return false;
        
        // Verificar stock antes de confirmar
        for (int i = 0; i < pedido.getProductCount(); i++) {
            Product p = pedido.getProductos()[i];
            int cantidad = pedido.getCantidades()[i];
            if (p == null || p.getStock() < cantidad) return false;
        }
        
        // Actualizar stock y confirmar
        for (int i = 0; i < pedido.getProductCount(); i++) {
            Product p = pedido.getProductos()[i];
            int cantidad = pedido.getCantidades()[i];
            if (p != null) {
                p.vender(cantidad);
            }
        }
        
        pedido.setConfirmado(true);
        pedido.setCodigoVendedor(codigoVendedor);
        
        // Actualizar contador de ventas del vendedor
        User vendedor = findUserByCode(codigoVendedor);
        if (vendedor != null && "VENDEDOR".equals(vendedor.getRol())) {
            // Aquí podrías agregar lógica para contar ventas del vendedor
        }
        
        return true;
    }

    public Pedido[] getPedidosConfirmados() {
        // ✅ VERIFICAR que pedidos no sea null
        if (pedidos == null) {
            pedidos = new Pedido[MAX_PEDIDOS];
            return new Pedido[0];
        }
        
        int count = 0;
        for (int i = 0; i < pedidoCount; i++) {
            if (pedidos[i] != null && pedidos[i].isConfirmado()) count++;
        }
        
        Pedido[] confirmados = new Pedido[count];
        int index = 0;
        for (int i = 0; i < pedidoCount; i++) {
            if (pedidos[i] != null && pedidos[i].isConfirmado()) {
                confirmados[index++] = pedidos[i];
            }
        }
        return confirmados;
    }

    // ✅ NUEVO: Métodos para reportes
    public Product[] getProductosMasVendidos() {
        Product[] copia = listProducts();
        // Ordenar productos por cantidad vendida (descendente)
        for (int i = 0; i < productCount - 1; i++) {
            for (int j = i + 1; j < productCount; j++) {
                if (copia[i].getCantidadVendida() < copia[j].getCantidadVendida()) {
                    Product temp = copia[i];
                    copia[i] = copia[j];
                    copia[j] = temp;
                }
            }
        }
        return copia;
    }

    public Product[] getProductosMenosVendidos() {
        Product[] copia = listProducts();
        // Ordenar productos por cantidad vendida (ascendente)
        for (int i = 0; i < productCount - 1; i++) {
            for (int j = i + 1; j < productCount; j++) {
                if (copia[i].getCantidadVendida() > copia[j].getCantidadVendida()) {
                    Product temp = copia[i];
                    copia[i] = copia[j];
                    copia[j] = temp;
                }
            }
        }
        return copia;
    }

    // Serialización
    public boolean saveToFile(String filename){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            // ✅ Asegurar que pedidos esté inicializado antes de guardar
            if (pedidos == null) {
                pedidos = new Pedido[MAX_PEDIDOS];
            }
            oos.writeObject(this);
            return true;
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static DataStore loadFromFile(String filename){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            Object obj = ois.readObject();
            if (obj instanceof DataStore) {
                DataStore loaded = (DataStore) obj;
                // ✅ Asegurar que pedidos esté inicializado después de cargar
                if (loaded.pedidos == null) {
                    loaded.pedidos = new Pedido[MAX_PEDIDOS];
                }
                loaded.ensureDefaultAdmin();
                return loaded;
            } else {
                return new DataStore();
            }
        } catch(Exception e){
            return new DataStore();
        }
    }
}
