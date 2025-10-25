package com.proyecto2.utiles;

import com.proyecto2.modelo.Product;
import com.proyecto2.modelo.User;
import java.io.*;
import java.util.ArrayList;

public class CSVUtil {

    // === MÉTODOS PARA PRODUCTOS ===

    // Export productos a CSV
    public static void exportProducts(Product[] products, String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("codigo,nombre,categoria,precio,stock,atributo_especifico");
            bw.newLine();
            for (Product p : products) {
                String line = escape(p.getCodigo()) + "," + escape(p.getNombre()) + "," + escape(p.getCategoria())
                        + "," + p.getPrecio() + "," + p.getStock() + "," + escape(p.getAtributoEspecifico());
                bw.write(line); bw.newLine();
            }
        }
    }

    // Import productos desde CSV
    public static Product[] importProducts(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) return new Product[0];
        java.util.List<Product> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine();
            String line;
            int lineNumber = 1;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",", -1);
                if (parts.length >= 6) {
                    try {
                        String cod = unescape(parts[0]);
                        String nom = unescape(parts[1]);
                        String cat = unescape(parts[2]);
                        double precio = Double.parseDouble(parts[3]);
                        int stock = Integer.parseInt(parts[4]);
                        String atributo = unescape(parts[5]);
                        
                        // Validaciones adicionales
                        if (cod.isEmpty() || nom.isEmpty() || cat.isEmpty()) {
                            System.err.println("Línea " + lineNumber + ": Campos obligatorios vacíos");
                            continue;
                        }
                        
                        if (precio <= 0) {
                            System.err.println("Línea " + lineNumber + ": Precio debe ser mayor a 0");
                            continue;
                        }
                        
                        if (stock < 0) {
                            System.err.println("Línea " + lineNumber + ": Stock no puede ser negativo");
                            continue;
                        }
                        
                        // Validar atributo según categoría
                        String error = validarAtributoProducto(cat, atributo);
                        if (error != null) {
                            System.err.println("Línea " + lineNumber + ": " + error);
                            continue;
                        }
                        
                        list.add(new Product(cod, nom, cat, precio, stock, atributo));
                    } catch(Exception ex){
                        System.err.println("Línea " + lineNumber + ": Error en formato numérico");
                    }
                } else if (parts.length >= 5) {
                    // Para compatibilidad con archivos antiguos
                    try {
                        String cod = unescape(parts[0]);
                        String nom = unescape(parts[1]);
                        String cat = unescape(parts[2]);
                        double precio = Double.parseDouble(parts[3]);
                        int stock = Integer.parseInt(parts[4]);
                        list.add(new Product(cod, nom, cat, precio, stock, ""));
                    } catch(Exception ex){
                        System.err.println("Línea " + lineNumber + ": Error en formato numérico");
                    }
                } else {
                    System.err.println("Línea " + lineNumber + ": Formato inválido");
                }
            }
        }
        return list.toArray(new Product[0]);
    }

    // === MÉTODOS PARA VENDEDORES ===

    // Export vendedores a CSV
    public static void exportVendedores(User[] vendedores, String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("codigo,nombre,genero,contrasena");
            bw.newLine();
            for (User v : vendedores) {
                if ("VENDEDOR".equals(v.getRol())) {
                    String line = escape(v.getCodigo()) + "," + escape(v.getNombre()) + "," + 
                                 escape(v.getGenero()) + "," + escape(v.getPassword());
                    bw.write(line); bw.newLine();
                }
            }
        }
    }

    // Import vendedores desde CSV
    public static User[] importVendedores(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) return new User[0];
        java.util.List<User> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine();
            String line;
            int lineNumber = 1;
            
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",", -1);
                if (parts.length >= 4) {
                    String codigo = unescape(parts[0]);
                    String nombre = unescape(parts[1]);
                    String genero = unescape(parts[2]);
                    String contrasena = unescape(parts[3]);
                    
                    // Validaciones
                    if (codigo.isEmpty() || nombre.isEmpty() || contrasena.isEmpty()) {
                        System.err.println("Línea " + lineNumber + ": Campos obligatorios vacíos");
                        continue;
                    }
                    
                    if (!codigo.matches("VE-\\d+")) {
                        System.err.println("Línea " + lineNumber + ": Formato código inválido. Debe ser VE-XXX");
                        continue;
                    }
                    
                    if (!genero.equals("M") && !genero.equals("F")) {
                        System.err.println("Línea " + lineNumber + ": Género inválido. Use M o F");
                        continue;
                    }
                    
                    list.add(new User(codigo, nombre, contrasena, "VENDEDOR", genero, ""));
                } else {
                    System.err.println("Línea " + lineNumber + ": Formato inválido, se esperaban 4 columnas");
                }
            }
        }
        return list.toArray(new User[0]);
    }

    // === VALIDACIONES ESPECÍFICAS ===

    // Validar atributo de producto según categoría
    public static String validarAtributoProducto(String categoria, String atributo) {
        if (atributo == null || atributo.trim().isEmpty()) {
            return "Atributo no puede estar vacío";
        }
        
        categoria = categoria.toUpperCase();
        switch(categoria) {
            case "TECNOLOGIA":
                try {
                    int meses = Integer.parseInt(atributo.trim());
                    if (meses <= 0) return "Meses de garantía deben ser mayores a 0";
                } catch (NumberFormatException e) {
                    return "Para tecnología, atributo debe ser número de meses de garantía";
                }
                break;
                
            case "ALIMENTO":
                if (!atributo.matches("\\d{2}/\\d{2}/\\d{4}")) {
                    return "Para alimento, atributo debe ser fecha (formato: dd/mm/aaaa)";
                }
                break;
                
            case "GENERAL":
                if (atributo.trim().length() < 2) {
                    return "Para categoría general, atributo debe describir el material";
                }
                break;
                
            default:
                return "Categoría no reconocida. Use: TECNOLOGIA, ALIMENTO o GENERAL";
        }
        
        return null; // Válido
    }

    // === MÉTODOS AUXILIARES ===

    private static String escape(String s){
        if (s == null) return "";
        // Escapar comas envolviendo en comillas
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }
    
    private static String unescape(String s){
        if (s == null) return "";
        s = s.trim();
        // Remover comillas exteriores si existen
        if (s.startsWith("\"") && s.endsWith("\"")) {
            s = s.substring(1, s.length() - 1);
            s = s.replace("\"\"", "\"");
        }
        return s;
    }
}