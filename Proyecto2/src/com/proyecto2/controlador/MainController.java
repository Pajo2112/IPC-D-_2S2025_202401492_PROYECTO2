package com.proyecto2.controlador;

import com.proyecto2.modelo.*;
import com.proyecto2.utiles.Bitacora;
import com.proyecto2.utiles.CSVUtil;
import com.proyecto2.utiles.PDFGenerator;

public class MainController {
    private DataStore store;
    private Bitacora bitacora;

    public MainController(DataStore store, Bitacora bitacora) {
        this.store = store;
        this.bitacora = bitacora;
    }

    // Autenticación
    public User authenticate(String codigo, String password){
        User u = store.findUserByCode(codigo);
        if (u != null && u.getPassword().equals(password)) {
            bitacora.append("LOGIN_OK", codigo);
            return u;
        } else {
            bitacora.append("LOGIN_FAIL", codigo);
            return null;
        }
    }

    // USUARIOS
    public boolean addUser(User u){
        boolean ok = store.addUser(u);
        if (ok) {
            store.saveToFile("datastore.ser");
            bitacora.append("ADD_USER", u.getCodigo());
        }
        return ok;
    }

    public User[] listUsers(){ return store.listUsers(); }

    public boolean deleteUser(String codigo){
        boolean ok = store.deleteUser(codigo);
        if (ok) {
            store.saveToFile("datastore.ser");
            bitacora.append("DEL_USER", codigo);
        }
        return ok;
    }

    // PRODUCTOS
    public boolean addProduct(Product p){
        boolean ok = store.addProduct(p);
        if (ok){
            store.saveToFile("datastore.ser");
            bitacora.append("ADD_PRODUCT", p.getCodigo());
        }
        return ok;
    }

    public boolean updateProduct(String codigo, String nombre, String categoria, String atributo, int stock){
        boolean ok = store.updateProduct(codigo,nombre,categoria,atributo,stock);
        if (ok){
            store.saveToFile("datastore.ser");
            bitacora.append("UPDATE_PRODUCT", codigo);
        }
        return ok;
    }

    public boolean deleteProduct(String codigo){
        boolean ok = store.deleteProduct(codigo);
        if (ok){
            store.saveToFile("datastore.ser");
            bitacora.append("DEL_PRODUCT", codigo);
        }
        return ok;
    }

    public Product[] listProducts(){ return store.listProducts(); }

    // CSV Import/Export (productos)
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
            // agregar sin duplicados
            for (Product p : imported) {
                if (store.findProductByCode(p.getCodigo()) == null) store.addProduct(p);
            }
            store.saveToFile("datastore.ser");
            bitacora.append("IMPORT_CSV", filename);
            return true;
        } catch(Exception e){ e.printStackTrace(); return false; }
    }

    // PDF report
    public boolean generateProductsPDF(String filename){
        try {
            PDFGenerator.createProductReport(store.listProducts(), filename);
            bitacora.append("PDF_PRODUCTS", filename);
            return true;
        } catch(Exception e){ e.printStackTrace(); return false; }
    }

    // Persistence helpers
    public void saveAll(){ store.saveToFile("datastore.ser"); }
    public static DataStore loadStore(){ return DataStore.loadFromFile("datastore.ser"); }
}
