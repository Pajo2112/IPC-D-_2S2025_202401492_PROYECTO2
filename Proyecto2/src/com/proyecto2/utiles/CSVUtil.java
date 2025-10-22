package com.proyecto2.utiles;

import com.proyecto2.modelo.Product;
import java.io.*;
import java.util.ArrayList; // solo internamente para parseo; si debes evitar completamente, puedo adaptar.

public class CSVUtil {

    // Export productos a CSV
    public static void exportProducts(Product[] products, String filename) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("codigo,nombre,categoria,atributo,stock");
            bw.newLine();
            for (Product p : products) {
                String line = escape(p.getCodigo()) + "," + escape(p.getNombre()) + "," + escape(p.getCategoria())
                        + "," + escape(p.getAtributo()) + "," + p.getStock();
                bw.write(line); bw.newLine();
            }
        }
    }

    // Import productos desde CSV (simple)
    public static Product[] importProducts(String filename) throws IOException {
        File f = new File(filename);
        if (!f.exists()) return new Product[0];
        java.util.List<Product> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    String cod = unescape(parts[0]);
                    String nom = unescape(parts[1]);
                    String cat = unescape(parts[2]);
                    String att = unescape(parts[3]);
                    int stock = 0;
                    try { stock = Integer.parseInt(parts[4]); } catch(Exception ex){}
                    list.add(new Product(cod,nom,cat,att,stock));
                }
            }
        }
        return list.toArray(new Product[0]);
    }

    private static String escape(String s){
        if (s == null) return "";
        return s.replace(",", " ");
    }
    private static String unescape(String s){
        if (s == null) return "";
        return s;
    }
}
