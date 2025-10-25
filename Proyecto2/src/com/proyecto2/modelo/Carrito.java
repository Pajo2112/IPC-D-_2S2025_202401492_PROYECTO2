package com.proyecto2.modelo;

import java.io.Serializable;

public class Carrito implements Serializable {
    private static final long serialVersionUID = 1L;
    private Product[] productos;
    private int[] cantidades;
    private int itemCount;
    private static final int MAX_ITEMS = 50;

    public Carrito() {
        productos = new Product[MAX_ITEMS];
        cantidades = new int[MAX_ITEMS];
        itemCount = 0;
    }

    public boolean agregarProducto(Product producto, int cantidad) {
        if (itemCount >= MAX_ITEMS || producto == null) return false;
        
        // Verificar si ya existe el producto
        for (int i = 0; i < itemCount; i++) {
            if (productos[i] != null && productos[i].getCodigo().equals(producto.getCodigo())) {
                cantidades[i] += cantidad;
                return true;
            }
        }
        
        // Si no existe, agregar nuevo
        productos[itemCount] = producto;
        cantidades[itemCount] = cantidad;
        itemCount++;
        return true;
    }

    public boolean eliminarProducto(String codigoProducto) {
        for (int i = 0; i < itemCount; i++) {
            if (productos[i] != null && productos[i].getCodigo().equals(codigoProducto)) {
                // Mover elementos hacia atrás
                for (int j = i; j < itemCount - 1; j++) {
                    productos[j] = productos[j + 1];
                    cantidades[j] = cantidades[j + 1];
                }
                productos[itemCount - 1] = null;
                cantidades[itemCount - 1] = 0;
                itemCount--;
                return true;
            }
        }
        return false;
    }

    public boolean actualizarCantidad(String codigoProducto, int nuevaCantidad) {
        for (int i = 0; i < itemCount; i++) {
            if (productos[i] != null && productos[i].getCodigo().equals(codigoProducto)) {
                cantidades[i] = nuevaCantidad;
                return true;
            }
        }
        return false;
    }

    public double calcularTotal() {
        double total = 0;
        for (int i = 0; i < itemCount; i++) {
            if (productos[i] != null) {
                total += productos[i].getPrecio() * cantidades[i];
            }
        }
        return total;
    }

    public void limpiar() {
        for (int i = 0; i < itemCount; i++) {
            productos[i] = null;
            cantidades[i] = 0;
        }
        itemCount = 0;
    }

    // Getters - CORREGIDOS para evitar null
    public Product[] getProductos() { 
        Product[] validos = new Product[itemCount];
        for (int i = 0; i < itemCount; i++) {
            validos[i] = productos[i];
        }
        return validos;
    }
    
    public int[] getCantidades() { 
        int[] validos = new int[itemCount];
        for (int i = 0; i < itemCount; i++) {
            validos[i] = cantidades[i];
        }
        return validos;
    }
    
    public int getItemCount() { return itemCount; }
}
